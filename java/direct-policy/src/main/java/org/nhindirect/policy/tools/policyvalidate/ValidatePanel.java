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

package org.nhindirect.policy.tools.policyvalidate;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.text.Document;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nhindirect.policy.PolicyFilterFactory;
import org.nhindirect.policy.PolicyGrammarException;
import org.nhindirect.policy.PolicyLexicon;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyFilter;
import org.nhindirect.policy.PolicyLexiconParser;
import org.nhindirect.policy.PolicyLexiconParserFactory;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.impl.machine.StackMachineCompiler;

///CLOVER:OFF
/**
 * Main application panel that performs certificate validation.
 * @author Greg Meyer	
 * @since 1.0
 */
public class ValidatePanel extends JPanel 
{
	static final long serialVersionUID = -1058079566562354445L;
	
	protected FileField policyFileField;
	protected FileField certFileField;
	protected JTextArea reportText;
	
	protected JButton cmdValidate;
	
	protected boolean feedMode = false;
	
	protected PolicyLexicon feedLexicon;
	
	protected Document feed;
	
	public ValidatePanel()
	{
		super();
		
		initUI();
		
		addActions();
	}
	
	protected void initUI()
	{
		setLayout(new BorderLayout());
		setBorder(new CompoundBorder( 
                new SoftBevelBorder(BevelBorder.LOWERED), new EmptyBorder(5,5,5,5)) ); 
		
		// File Load Fields
		policyFileField = new FileField("Policy Definition File:", "");
		certFileField = new FileField("Certificate File: ", "");
		
		
		final JPanel filePanel = new JPanel(new GridLayout(1, 2));
		filePanel.add(certFileField);
		filePanel.add(policyFileField);

		this.add(filePanel, BorderLayout.NORTH);
		
		// Report panel
		final JLabel reportHeaderLabel = new JLabel("Validation Report");
		reportText = new JTextArea();
		reportText.setLineWrap(true);
		reportText.setWrapStyleWord(true);
		reportText.setEditable(false);
		
		
		final JScrollPane scrollPane = new JScrollPane(reportText); 
		scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		final JPanel reportPanel = new JPanel(new BorderLayout());
		reportPanel.add(reportHeaderLabel, BorderLayout.NORTH);
		reportPanel.add(scrollPane, BorderLayout.CENTER);
		
		this.add(reportPanel, BorderLayout.CENTER);
		
		// Button Panel
		cmdValidate = new JButton("Validate");
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(cmdValidate);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
	}
	
	private void addActions()
	{
		cmdValidate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				validateCert();
			}
		});
	}
	
	public void setFeedMode(PolicyLexicon lexicon, Document feed)
	{
		this.feedLexicon = lexicon;
		this.feedMode = true;
		this.feed = feed;
		this.policyFileField.setVisible(false);
	}
	
	private void validateCert()
	{
		reportText.setText("");
		
		final File certFile = certFileField.getFile();
		final File policyFile = policyFileField.getFile();
		
		if (!certFile.exists())
		{
			JOptionPane.showMessageDialog(this,"Certificate file does not exist or cannot be found.", 
		 		    "Invalid Cert File", JOptionPane.ERROR_MESSAGE );
			return;
		}
		
		
		InputStream policyInput = null;
		if (!feedMode)
		{
			if (!policyFile.exists())
			{
				JOptionPane.showMessageDialog(this,"Policy file does not exist or cannot be found.", 
			 		    "Invalid Policy File", JOptionPane.ERROR_MESSAGE );
				
				return;
			}
			
			try
			{
				// load the policy as an input stream
				policyInput = FileUtils.openInputStream(policyFile);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this,"Could not load policy from file: " + e.getMessage(), 
			 		    "Invalid Policy File", JOptionPane.ERROR_MESSAGE );
				
				return;
			}
		}
		else
		{
			try
			{
				final int length = feed.getLength();
				policyInput = IOUtils.toInputStream(feed.getText(0, length));
			}			
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this,"Could not load policy: " + e.getMessage(), 
			 		    "Invalid Policy", JOptionPane.ERROR_MESSAGE );
				
				return;
			}
		}
			
		// load the certificate
		X509Certificate cert = null;

		try
		{
			cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(FileUtils.openInputStream(certFile));    
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this,"Could not load certificate from file: " + e.getMessage(), 
		 		    "Invalid Cert File", JOptionPane.ERROR_MESSAGE );
			
			return;
		}
		
		final DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d yyyy HH:mm:ss", Locale.getDefault());
		final StringBuilder reportTextBuilder = new StringBuilder("Validation run at " + dateFormat.format(Calendar.getInstance(Locale.getDefault()).getTime())
				+ "\r\n\r\n");
		
		try
		{
			final PolicyLexiconParser parser = (feedMode) ? PolicyLexiconParserFactory.getInstance(feedLexicon): 
				PolicyLexiconParserFactory.getInstance(PolicyLexicon.XML);
			
			final PolicyExpression policyExpression = parser.parse(policyInput);
		
			
			final org.nhindirect.policy.Compiler compiler = new StackMachineCompiler();
			compiler.setReportModeEnabled(true);
			final PolicyFilter filter = PolicyFilterFactory.getInstance(compiler);
			
			if (filter.isCompliant(cert, policyExpression) && compiler.getCompilationReport().isEmpty())			
				reportTextBuilder.append("Certificate is compliant with the provided policy.");
			else
			{
				reportTextBuilder.append("Certificate is NOT compliant with the provided policy.\r\n\r\n");
				
				final Collection<String> report = compiler.getCompilationReport();
				if (!report.isEmpty())
				{
					for (String reportEntry : report)
						reportTextBuilder.append(reportEntry + "\r\n");
				}
			}
		}
		catch (PolicyRequiredException e)
		{
			reportTextBuilder.append("Validation Successful\r\nCertificate is missing a required field\r\n\t" + e.getMessage());
		}
		catch (PolicyGrammarException e)
		{
			reportTextBuilder.append("Validation Failed\r\nError compiling policy\r\n\t" + e.getMessage());
		}		
		catch (Exception e)
		{
			final ByteArrayOutputStream str = new ByteArrayOutputStream();
			final PrintStream printStr = new PrintStream(str);
			
			e.printStackTrace();
			
			e.printStackTrace(printStr);
			
			final String stackTrace = new String(str.toByteArray());
			
			reportTextBuilder.append("Validation Failed\r\nError compiling or proccessing policy\r\n\t" + e.getMessage() + "\r\n" + stackTrace);
		}
		finally
		{
			reportText.setText(reportTextBuilder.toString());
			IOUtils.closeQuietly(policyInput);
		}
	}
}
///CLOVER:ON
