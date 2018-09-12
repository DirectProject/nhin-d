package org.nhindirect.common.rest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.http.annotation.GuardedBy;
import org.apache.http.client.HttpClient;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.signature.PlainTextMessageSigner;

public class OAuthManager
{
    protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    @GuardedBy("readWriteLock")
    protected final OAuthProvider provider;
    @GuardedBy("readWriteLock")
    protected final OAuthConsumer consumer;
    
    public OAuthManager()
    {
    	provider = null;
    	consumer = null;
    }
    
    /**
     * Constructor.
     * 
     * @param consumerKey
     *            the consumer key to manager.
     * @param consumerSecret
     *            the corresponding consumer secret.
     * @param accessTokenUrl
     *            the URL of the OAuth service's access token endpoint.
     * @param httpClient
     *            the {@link HttpClient} to use to communicate with the OAuth service.
     * @throws OAuthException
     *             if an error occurs with one of the OAuth components during initialization.
     */
    public OAuthManager(String consumerKey, String consumerSecret, String accessTokenUrl,
            HttpClient httpClient) throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException 
    {
        
        this(new CommonsHttpOAuthProvider(null, accessTokenUrl, null, httpClient), initConsumer(
                consumerKey, consumerSecret));
    }

    public static OAuthConsumer initConsumer(String consumerKey, String consumerSecret) 
    {
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        consumer.setMessageSigner(new PlainTextMessageSigner());
        consumer.setTokenWithSecret("", "");
        return consumer;
    }

    /**
     * Unit testing constructor
     * 
     * @throws OAuthException
     *             if an error occurs with one of the OAuth components during initialization.
     */
    public OAuthManager(OAuthProvider provider, OAuthConsumer consumer)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException 
    {
        this.provider = provider;
        this.consumer = consumer;
        updateToken();
    }

    /**
     * Updates the stored token if the last time it was updated was before the passed time. The time
     * passed should be the earliest known time at which the token was valid (within a given
     * thread).
     * 
     * @throws OAuthException
     *             if the token cannot be updated for some reason.
     * 
     */
    public void updateToken() throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException         
    {
        try 
        {
            Lock writeLock = readWriteLock.writeLock();
            writeLock.lockInterruptibly();
            try 
            {
                // writer is allowed to acquire a read lock
                OAuthConsumer tempConsumer = initConsumer(consumer.getConsumerKey(), consumer.getConsumerSecret());
                provider.retrieveAccessToken(tempConsumer, "");
                consumer.setTokenWithSecret(tempConsumer.getToken(), tempConsumer.getTokenSecret());
            } 
            finally 
            {
                writeLock.unlock();
            }
        } 
        ///CLOVER:OFF
        catch (InterruptedException e) 
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                    "Thread was interrupted before the OAuth token could be updated.", e);
        }
        ///CLOVER:ON
    }

    /**
     * @return a new instance of {@link OAuthConsumer} with the appropriate token values. The
     *         returned instance should only be used by one thread at a time.
     */
    public OAuthConsumer getOAuthConsumer() 
    {
        try 
        {
            Lock readLock = readWriteLock.readLock();
            readLock.lockInterruptibly();
            try 
            {
                OAuthConsumer returnedConsumer = new CommonsHttpOAuthConsumer(
                        consumer.getConsumerKey(), consumer.getConsumerSecret());
                returnedConsumer.setMessageSigner(new PlainTextMessageSigner());
                returnedConsumer.setTokenWithSecret(consumer.getToken(), consumer.getTokenSecret());
                return returnedConsumer;
            } 
            finally 
            {
                readLock.unlock();
            }
        } 
        ///CLOVER:OFF
        catch (InterruptedException e) {
        	
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                    "Thread was interrupted before an OAuth token could be acquired.", e);
        }
      ///CLOVER:ON
    }
}
