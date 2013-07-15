/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Vincent Lewis     vincent.lewis@gsihealth.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhind.james.mailet;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.nhind.mail.service.DocumentRepository;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.tx.TxUtil;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.gateway.smtp.NotificationProducer;
import org.nhindirect.gateway.smtp.NotificationSettings;
import org.nhindirect.gateway.smtp.ReliableDispatchedNotificationProducer;
import org.nhindirect.gateway.smtp.dsn.DSNCreator;
import org.nhindirect.gateway.smtp.dsn.provider.FailedDeliveryDSNCreatorProvider;
import org.nhindirect.gateway.smtp.james.mailet.AbstractNotificationAwareMailet;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.notifications.NotificationMessage;
import org.nhindirect.xd.routing.RoutingResolver;
import org.nhindirect.xd.routing.impl.RoutingResolverImpl;
import org.nhindirect.xd.transform.MimeXdsTransformer;
import org.nhindirect.xd.transform.impl.DefaultMimeXdsTransformer;

import com.google.inject.Provider;

/**
 * An Apache James Mailet that converts clinical messages into IHE
 * Cross-Enterprise Document Reliability (XDR) messages and transmits them to an
 * XDR Document Recipient via IHE XDS.b Provide and Register transaction
 * (ITI-41).
 *
 */
public class DirectXdMailet extends AbstractNotificationAwareMailet
{
	protected static final String RELIABLE_DELIVERY_OPTION = MDNStandard.DispositionOption_TimelyAndReliable + "=optional,true";

    private String endpointUrl;
    private String configServiceUrl;

    private MimeXdsTransformer mimeXDSTransformer;
    private DocumentRepository documentRepository;
    private RoutingResolver resolver;
    protected NotificationProducer notificationProducer;
    private static final Log LOGGER = LogFactory.getFactory().getInstance(DirectXdMailet.class);

    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.mailet.base.GenericMailet#service(org.apache.mailet.Mail)
     */
    @Override
    public void service(Mail mail) throws MessagingException
    {
        LOGGER.info("Servicing DirectXdMailet");

        if (StringUtils.isBlank(endpointUrl))
        {
            LOGGER.error("DirectXdMailet endpoint URL cannot be empty or null.");
            throw new MessagingException("DirectXdMailet endpoint URL cannot be empty or null.");
        }

        boolean successfulTransaction = false;
		final MimeMessage msg = mail.getMessage();
		final boolean isReliableAndTimely = TxUtil.isReliableAndTimelyRequested(msg);
		
		final NHINDAddressCollection initialRecipients = getMailRecipients(mail);
		final NHINDAddressCollection xdRecipients = new NHINDAddressCollection();					
		
		final NHINDAddress sender = getMailSender(mail);
		Tx txToTrack = null;
		
        // Get recipients and create a collection of Strings
        final List<String> recipAddresses = new ArrayList<String>();

        for (NHINDAddress addr : initialRecipients)
        {
            recipAddresses.add(addr.getAddress());
        }

        // Service XD* addresses
       if (getResolver().hasXdEndpoints(recipAddresses))
        {
            LOGGER.info("Recipients include XD endpoints");
            
            try
            {
                // Extract XD* addresses
                //List<Address> xdAddresses = new ArrayList<Address>();
                for (String s : getResolver().getXdEndpoints(recipAddresses))
                {
                    //xdAddresses.add((new MailAddress(s)).toInternetAddress());
                    xdRecipients.add(new NHINDAddress(s));
            		
                }
                txToTrack = this.getTxToTrack(msg, sender, xdRecipients);
                
                // Replace recipients with only XD* addresses
                //msg.setRecipients(RecipientType.TO, xdAddresses.toArray(new Address[0]));
                msg.setRecipients(RecipientType.TO, xdRecipients.toArray(new Address[0]));

                // Transform MimeMessage into ProvideAndRegisterDocumentSetRequestType object
                ProvideAndRegisterDocumentSetRequestType request = getMimeXDSTransformer().transform(msg);

                for (String directTo : recipAddresses)
                {
                    String response = getDocumentRepository().forwardRequest(endpointUrl, request, directTo, sender.toString());

                    if (!isSuccessful(response))
                    {
                        LOGGER.error("DirectXdMailet failed to deliver XD message.");
                        LOGGER.error(response);
                        
                    }
                    else
                    {
                    	successfulTransaction = true;
	                    if (isReliableAndTimely && txToTrack != null && txToTrack.getMsgType() == TxMessageType.IMF)
	                    {
	
	                    	// send MDN dispatch for messages the recipients that were successful
	        				final Collection<NotificationMessage> notifications = 
	        						notificationProducer.produce(new Message(msg), xdRecipients.toInternetAddressCollection());
	        				if (notifications != null && notifications.size() > 0)
	        				{
	        					LOGGER.debug("Sending MDN \"dispathed\" messages");
	        					// create a message for each notification and put it on James "stack"
	        					for (NotificationMessage message : notifications)
	        					{
	        						try
	        						{
	        							message.setHeader(MDNStandard.Headers.DispositionNotificationOptions, RELIABLE_DELIVERY_OPTION);
	        							message.saveChanges();
	        							getMailetContext().sendMail(message);
	        						}
	        						catch (Throwable t)
	        						{
	        							// don't kill the process if this fails
	        							LOGGER.error("Error sending MDN dispatched message.", t);
	        						}
	        					}
	        				}
	                    }
                    }
                }
            }
            catch (Throwable e)
            {
                LOGGER.error("DirectXdMailet delivery failure", e);
            }
        }

        // Service SMTP addresses (fall through)
        // this basically sets the message back to it's original state with SMTP addresses only
        if (getResolver().hasSmtpEndpoints(recipAddresses))
        {
            LOGGER.info("Recipients include SMTP endpoints");
            
            mail.setRecipients(getSmtpRecips(recipAddresses));
        }
        else
        {
            LOGGER.info("Recipients do not include SMTP endpoints");
            
            // No SMTP addresses, ghost it
            mail.setState(Mail.GHOST);
        }
        
        if (!successfulTransaction )
        {
        	if (txToTrack != null && txToTrack.getMsgType() == TxMessageType.IMF)
        	{
	        	// for good measure, send DSN messages back to the original sender on failure
				// create a DSN message
				this.sendDSN(txToTrack, xdRecipients, false);
        	}
        }
    }

    private Collection<MailAddress> getSmtpRecips(Collection<String> recips) throws AddressException
    {
        List<MailAddress> addrs = new ArrayList<MailAddress>();

        for (String s : getResolver().getSmtpEndpoints(recips))
            addrs.add(new MailAddress(s));

        return addrs;
    }

    private boolean isSuccessful(String response)
    {
        if (StringUtils.contains(response, "Failure"))
            return false;

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.mailet.base.GenericMailet#init()
     */
    @Override
    public void init() throws MessagingException
    {

    	LOGGER.info("Initializing DirectXdMailet");
        super.init();
        
        // Get the endpoint URL
        endpointUrl = getInitParameter("EndpointURL");

        if (StringUtils.isBlank(endpointUrl))
        {
            LOGGER.error("DirectXdMailet endpoint URL cannot be empty or null.");
            throw new MessagingException("DirectXdMailet endpoint URL cannot be empty or null.");
        }

        // Get the config-service URL
        try
        {
            configServiceUrl = getInitParameter("ConfigURL");
        }
        catch (Exception e)
        {
            // eat it
        }
        
        notificationProducer = new ReliableDispatchedNotificationProducer(new NotificationSettings(true, "Direct XD Delivery Agent", "Your message was successfully dispatched."));
    }

    /**
     * Return the value of endpointUrl.
     * 
     * @return the value of endpointUrl.
     */
    protected String getEndpointUrl()
    {
        return this.endpointUrl;
    }

    /**
     * Set the value of endpointUrl.
     * 
     * @param endpointUrl
     *            The value of endpointUrl.
     */
    protected void setEndpointUrl(String endpointUrl)
    {
        this.endpointUrl = endpointUrl;
    }

    /**
     * Return the value of configServiceUrl.
     * 
     * @return the value of configServiceUrl.
     */
    protected String getConfigServiceUrl()
    {
        return this.configServiceUrl;
    }

    /**
     * Set the value of configServiceUrl.
     * 
     * @param configServiceUrl
     *            The value of configServiceUrl.
     */
    protected void setConfigServiceUrl(String configServiceUrl)
    {
        this.configServiceUrl = configServiceUrl;
    }

    /**
     * Set the value of resolver.
     * 
     * @param resolver
     *            The value of resolver.
     */
    protected void setResolver(RoutingResolver resolver)
    {
        this.resolver = resolver;
    }

    /**
     * Get the value of resolver.
     * 
     * @return the value of resolver.
     */
    protected RoutingResolver getResolver()
    {
        if (this.resolver == null)
        {
            this.resolver = new RoutingResolverImpl(configServiceUrl);
        }

        return resolver;
    }

    /**
     * Set the value of mimeXDSTransformer.
     * 
     * @param mimeXDSTransformer
     *            The value of mimeXDSTransformer.
     */
    protected void setMimeXDSTransformer(MimeXdsTransformer mimeXDSTransformer)
    {
        this.mimeXDSTransformer = mimeXDSTransformer;
    }

    /**
     * Get the value of mimeXDSTransfomer.
     * 
     * @return the value of mimeXDSTransformer, or a new object if null.
     */
    protected MimeXdsTransformer getMimeXDSTransformer()
    {
        if (this.mimeXDSTransformer == null)
        {
            this.mimeXDSTransformer = new DefaultMimeXdsTransformer();
        }

        return this.mimeXDSTransformer;
    }

    /**
     * Set the value of documentRepository.
     * 
     * @param documentRepository
     *            The value of documentRepository.
     */
    public void setDocumentRepository(DocumentRepository documentRepository)
    {
        this.documentRepository = documentRepository;
    }

    /**
     * Get the value of documentRepository.
     * 
     * @return the value of documentRepository, or a new object if null.
     */
    public DocumentRepository getDocumentRepository()
    {
        if (this.documentRepository == null)
        {
            this.documentRepository = new DocumentRepository();
        }

        return documentRepository;
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Provider<DSNCreator> getDSNProvider() 
	{
		return new FailedDeliveryDSNCreatorProvider(this);
	}
}
