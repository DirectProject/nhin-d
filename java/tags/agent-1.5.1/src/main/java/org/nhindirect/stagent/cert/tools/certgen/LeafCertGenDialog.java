/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
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

package org.nhindirect.stagent.cert.tools.certgen;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.nhindirect.stagent.cert.tools.CreatePKCS12;

/**
 * UI component for creating leaf certificates.
 * @author Greg Meyer
 *
 */
///CLOVER:OFF
class LeafCertGenDialog extends JDialog 
{ 
	static final long serialVersionUID = -4500679031509430866L;	
	
	private LeafGenPanel genPanel;
	
	public LeafCertGenDialog(Frame parent, CertCreateFields signer)
	{
		super(parent, "Certificate Creation", true);
		setResizable(false);
		setSize(700, 250);
		setResizable(false);
		
		Point pt = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		
		this.setLocation(pt.x - (150), pt.y - (120));	
		
		
		initUI(signer);
	}
	
	
	private void initUI(CertCreateFields signer)
	{	
		getContentPane().setLayout(new BorderLayout());
		
		genPanel = new LeafGenPanel(signer);
		

		getContentPane().add(genPanel);
	}
	
	private static class LeafGenPanel extends CAPanel
	{
		static final long serialVersionUID = -3829240137598058532L;		
		
		private CertCreateFields signer;
		private X509Certificate signerCert;
		private PrivateKey signerKey;
		
		public LeafGenPanel(CertCreateFields signer)
		{
			super();
			
			this.signer = signer;
			signerCert = signer.getSignerCert();
			signerKey = (PrivateKey)signer.getSignerKey();						
			
			allowedToSign.setVisible(true);
			
			prePopulateFields();
		}
		
		@Override
		protected void initUI()
		{
			super.initUI();			
			createCA.setVisible(false);
			loadCA.setVisible(false);
			certFileField.setVisible(false);
			keyFileField.setVisible(false);
		}
		
		private void prePopulateFields()
		{
			// get the fields from the cert and pre populate the new cert			
			
			if (signer.getAttributes().containsKey("C"))
				this.countryField.setText(signer.getAttributes().get("C").toString());
			else
				this.countryField.setText("");
			
			if (signer.getAttributes().containsKey("ST"))
				this.stateField.setText(signer.getAttributes().get("ST").toString());
			else
				this.stateField.setText("");
			
			if (signer.getAttributes().containsKey("L"))
				this.locField.setText(signer.getAttributes().get("L").toString());
			else
				this.locField.setText("");
			
			if (signer.getAttributes().containsKey("O"))
				this.orgField.setText(signer.getAttributes().get("O").toString());
			else
				this.orgField.setText("");	
			
			this.expField.setValue(signer.getExpDays());			
			
		}
		
		protected void createCACert()
		{
			if (passField.getPassword().length == 0)
			{
				int selection = JOptionPane.showConfirmDialog(this, "The password field is empty.  Do you wish to create a private key file without a password?",
						"Empty Password", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				
				if (selection == JOptionPane.NO_OPTION)
					return;
			}
			
			// CN needs to be filled out
			if (cnField.getText().isEmpty())
			{
				JOptionPane.showMessageDialog(this,"Common Name (CN) must have a value.", 
			 		    "Invalid Cert File", JOptionPane.ERROR_MESSAGE );
				return;
			}
			
			// must also have an email address
			if (emailField.getText().isEmpty())
			{
				JOptionPane.showMessageDialog(this,"An email address is required.", 
			 		    "Invalid Email Address", JOptionPane.ERROR_MESSAGE );
				return;
			}
			
			// make sure the email is a valid email address
			try
			{
				new InternetAddress(emailField.getText());
			}
			catch (AddressException e)
			{
				JOptionPane.showMessageDialog(this,"The email address is an invalid address.", 
			 		    "Invalid Email Address", JOptionPane.ERROR_MESSAGE );
				return;
			}
			
			// create the new files
			certFileField.setFile(createNewFileName(false));						
			keyFileField.setFile(createNewFileName(true));	
			
			// check if the files already exist
			if (certFileField.getFile().exists() || keyFileField.getFile().exists())
			{
				int selection = JOptionPane.showConfirmDialog(this, "The certificate or key file already exists for this email address.\r\n" +
						"This operation will overwrite the file.  Continue?",
						"Certificate Confilct", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				
				if (selection == JOptionPane.NO_OPTION)
					return;			
			}
			
			
			
			// get the fields
			Map<String, Object> attributes = new HashMap<String, Object>(); 
			attributes.put("CN", cnField.getText());
			if (!countryField.getText().isEmpty())
				attributes.put("C", countryField.getText());
			if (!stateField.getText().isEmpty())
				attributes.put("ST", stateField.getText());		
			if (!locField.getText().isEmpty())
				attributes.put("L", locField.getText());				
			if (!orgField.getText().isEmpty())
				attributes.put("O", orgField.getText());	
			if (!emailField.getText().isEmpty())
				attributes.put("EMAILADDRESS", emailField.getText());	
			
			attributes.put("ALLOWTOSIGN", Boolean.toString(allowedToSign.isSelected()));	
			
			int exp = Integer.parseInt(expField.getValue().toString());
			int keyStre =  Integer.parseInt(keyStr.getValue().toString());
			
			CertCreateFields createFields = new CertCreateFields(attributes, certFileField.getFile(), keyFileField.getFile(),
					passField.getPassword(), exp, 
					keyStre, signerCert, signerKey);
			
			// create the cert
			CertCreateFields retCert;
			try
			{
				retCert = CertGenerator.createCertificate(createFields, addToAltSubjects.isSelected());
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this,"An error occured creating the certificate: " + e.getMessage(), 
			 		    "Certificate Creation Error", JOptionPane.ERROR_MESSAGE);
			 
			    return;
			}
			
			if (retCert == null)
			{
				JOptionPane.showMessageDialog(this,"An error occured creating the certificate: unknown error", 
			 		    "Certificate Creation Error", JOptionPane.ERROR_MESSAGE);
			 
			    return;
			}
			
			// SUCCESS... create the pkcs12 file
			File pcks12File = CreatePKCS12.create(retCert.getNewCertFile(), retCert.getNewKeyFile(), new String(passField.getPassword()), null);
			
			if (pcks12File == null)
			{
				JOptionPane.showMessageDialog(this,"An error occured creating the pkcs12 file: unknown error", 
			 		    "Certificate Creation Error", JOptionPane.ERROR_MESSAGE);
			 
			    return;
			}
			
			JOptionPane.showMessageDialog(this,"User/org certificate and private key created successfully:\r\n" +
					retCert.getNewCertFile().getName() + "\r\n" +
					retCert.getNewKeyFile().getName() + "\r\n" +
					pcks12File.getName(), 
		 		    "SUCCESS", JOptionPane.PLAIN_MESSAGE);
			
			// clear the fields
			cnField.setText("");
			emailField.setText("");
			passField.clear();
		}		
	}
}
///CLOVER:ON