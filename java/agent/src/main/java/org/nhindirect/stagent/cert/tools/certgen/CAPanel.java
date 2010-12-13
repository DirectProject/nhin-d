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
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; 
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;


/**
 * UI component for creating or loading self signed certificates.
 * @author Greg Meyer
 *
 */
class CAPanel extends JPanel 
{
	static final long serialVersionUID = -92734291206052662L;
	
	protected  static final int WF_CONTEXT_LOAD_CERTS = 0;
	protected  static final int WF_CONTEXT_CREATE_CERTS = 1;
	protected  static final int WF_CONTEXT_CLEAR = 2;
	protected  static final int WF_CONTEXT_CERT_LOADED = 3;
	protected  static final int WF_CONTEXT_CERT_CREATED = 4;
	
	protected JRadioButton createCA;
	protected JRadioButton loadCA;
	protected JTextField certFile;
	protected JTextField certPrivKeyFile;
	
	protected TextEntryField cnField;
	protected TextEntryField countryField;
	protected TextEntryField stateField;
	protected TextEntryField locField;
	protected TextEntryField orgField;
	protected TextEntryField emailField;
	
	protected SpinEntryField expField;
	protected DropDownEntry keyStr;
	protected PasswordField passField;
	
	protected FileField certFileField;
	protected FileField keyFileField;
	
	protected JButton loadCert;
	protected JButton createCert;
	protected JButton clear;
	protected JButton genCert;
	protected JCheckBox addToAltSubjects;
	protected JCheckBox allowedToSign;
	
	protected CertCreateFields currentCert;
	
	public CAPanel()
	{
		super();
		
		initUI();
		
		addActions();
		
		setWorkflowContext(WF_CONTEXT_CREATE_CERTS);
	}
	
	protected void initUI()
	{
		setLayout(new BorderLayout());
		//setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		setBorder(new CompoundBorder( 
                new SoftBevelBorder(BevelBorder.LOWERED), new EmptyBorder(5,5,5,5)) ); 
		
		
		createCA = new JRadioButton("Create New CA");
		loadCA = new JRadioButton("Load CA");		
		ButtonGroup group = new ButtonGroup();
		group.add(createCA);
		group.add(loadCA);
		createCA.setSelected(true);
		
		JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
				
		radioPanel.add(createCA);
		radioPanel.add(loadCA);
		
		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(new GridLayout(3, 3, 10,  10));
			
		cnField = new TextEntryField("CN:");
		fieldsPanel.add(cnField);
		
		countryField= new TextEntryField("Country:");
		fieldsPanel.add(countryField);
		
		stateField= new TextEntryField("State:");
		fieldsPanel.add(stateField);
		
		locField= new TextEntryField("Location:");
		fieldsPanel.add(locField);
		
		orgField= new TextEntryField("Org:");
		fieldsPanel.add(orgField);		
		
		emailField= new TextEntryField("Email:");
		fieldsPanel.add(emailField);		
		
		expField = new SpinEntryField("Experiation (Days):", 365);
		fieldsPanel.add(expField);
		
		keyStr = new DropDownEntry("Key Strength:", new Object[] {1024, 2048, 4096});
		fieldsPanel.add(keyStr);
		
		passField = new PasswordField("Password:");
		fieldsPanel.add(passField);
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(radioPanel, BorderLayout.NORTH);
		topPanel.add(fieldsPanel, BorderLayout.CENTER);
		
		add(topPanel, BorderLayout.NORTH);
		
		new FlowLayout(FlowLayout.LEFT);		
		
		certFileField = new FileField("Certificate Authority File:", "");
		keyFileField = new FileField("Private Key File:", "");
		
		
		JPanel filePanel = new JPanel(new GridLayout(1, 2));
		filePanel.add(certFileField);
		filePanel.add(keyFileField);
		
		loadCert = new JButton("Load");
		loadCert.setVisible(false);
		createCert = new JButton("Create");
		clear = new JButton("Clear");
		clear.setVisible(false);
		clear = new JButton("Clear");
		genCert = new JButton("Create Leaf Cert");
		genCert.setVisible(false);
		
		addToAltSubjects = new JCheckBox("Add Email To Alt Subject Names");
		addToAltSubjects.setVisible(true);
		allowedToSign = new JCheckBox("Allowed To Sign Certificates");
		allowedToSign.setVisible(false);
		JPanel addAltPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		addAltPanel.add(addToAltSubjects);
		addAltPanel.add(allowedToSign);
		
		
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(addAltPanel);		
		buttonPanel.add(loadCert);
		buttonPanel.add(createCert);
		buttonPanel.add(clear);
		buttonPanel.add(genCert);
		
		JPanel combineAltAndButtonPanel = new JPanel(new BorderLayout());
		combineAltAndButtonPanel.add(addAltPanel, BorderLayout.WEST);
		combineAltAndButtonPanel.add(buttonPanel, BorderLayout.EAST);
		
		JPanel bottomPannel = new JPanel(new BorderLayout());
		bottomPannel.add(filePanel, BorderLayout.NORTH);
		bottomPannel.add(combineAltAndButtonPanel, BorderLayout.SOUTH);
		
		this.add(bottomPannel, BorderLayout.SOUTH);				
	}
	
	private void addActions()
	{
		loadCert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				loadCACert();
			}
		});
		
		createCert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				createCACert();
			}
		});		
		
		createCA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				setWorkflowContext(WF_CONTEXT_CREATE_CERTS);
			}
		});
		
		loadCA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				setWorkflowContext(WF_CONTEXT_LOAD_CERTS);
			}
		});
		
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				setWorkflowContext(WF_CONTEXT_CLEAR);
			}
		});

		genCert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				createLeaf();
			}
		});
		
	}
	
	
	private void setWorkflowContext(int workflowContext)
	{
		switch (workflowContext)
		{
			case WF_CONTEXT_CREATE_CERTS:
			case WF_CONTEXT_CLEAR:
			{
				// creating a new CA			
				cnField.setEnabled(true);
				countryField.setEnabled(true);
				stateField.setEnabled(true);
				locField.setEnabled(true);
				orgField.setEnabled(true);
				emailField.setEnabled(true);
				
				expField.setEnabled(true);
				keyStr.setEnabled(true);
				passField.setEnabled(true);			

				certFileField.setEnabled(true);
				keyFileField.setEnabled(true);

				createCA.setEnabled(true);
				loadCA.setEnabled(true);
				
				loadCert.setVisible(false);
				createCert.setVisible(true);				
				clear.setVisible(false);
				genCert.setVisible(false);
				addToAltSubjects.setVisible(true);
	
				if(workflowContext == WF_CONTEXT_CLEAR)
				{
					cnField.setText("");
					countryField.setText("");
					stateField.setText("");
					locField.setText("");
					orgField.setText("");
					emailField.setText("");
					
					expField.setValue(365);
					keyStr.setValue("1024");
					passField.clear();			
					certFileField.setFile(null);
					keyFileField.setFile(null);
					
					createCA.setSelected(true);
					
					loadCert.setVisible(false);
					createCert.setVisible(true);				
					clear.setVisible(false);
					genCert.setVisible(false);
					addToAltSubjects.setVisible(true);
					
					currentCert = null;
					
				}
				break;
			}
			case WF_CONTEXT_LOAD_CERTS:
			{
				cnField.setEnabled(false);
				countryField.setEnabled(false);
				stateField.setEnabled(false);
				locField.setEnabled(false);
				orgField.setEnabled(false);
				emailField.setEnabled(false);
				
				expField.setEnabled(false);
				keyStr.setEnabled(false);
				passField.setEnabled(true);	
				
				loadCert.setVisible(true);
				createCert.setVisible(false);
				clear.setVisible(false);
				genCert.setVisible(false);
				addToAltSubjects.setVisible(false);
				
				createCA.setEnabled(true);
				loadCA.setEnabled(true);
				
				certFileField.setEnabled(true);
				keyFileField.setEnabled(true);
				
				break;
			}
			case WF_CONTEXT_CERT_CREATED:
			case WF_CONTEXT_CERT_LOADED:				
			{
				cnField.setEnabled(false);
				countryField.setEnabled(false);
				stateField.setEnabled(false);
				locField.setEnabled(false);
				orgField.setEnabled(false);
				emailField.setEnabled(false);
				
				expField.setEnabled(false);
				keyStr.setEnabled(false);
				passField.setEnabled(false);	

				createCA.setEnabled(false);
				loadCA.setEnabled(false);
			
				loadCert.setVisible(false);
				createCert.setVisible(false);
				clear.setVisible(true);
				genCert.setVisible(true);
				
				addToAltSubjects.setVisible(false);
				
				break;
			}

		}
	}
		
	private void createLeaf()
	{
		// create leaf certificates in the sub dialog
		LeafCertGenDialog generator = new LeafCertGenDialog(null, currentCert);
		generator.setVisible(true);
	}
	
	protected File createNewFileName(boolean isKey)
	{
		String fileName;
		
		int index;
		String field = emailField.getText();
		if (!field.isEmpty())
		{
			index = field.indexOf("@");
			if (index > -1)
				fileName = field.substring(0, index);
			else
				fileName = field;
		}
		else
		{
			field = cnField.getText();
			index = field.indexOf("@");
			if (index > -1)
				fileName = field.substring(0, index);
			else
				fileName = field;			
		}
		
		if (isKey)
			fileName += "Key";
		
		fileName += ".der";
		
		return new File(fileName);
		
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
		
		// make sure there is at least a CN
		if (cnField.getText().isEmpty())
		{
			JOptionPane.showMessageDialog(this,"Common Name (CN) must have a value.", 
		 		    "Invalid Cert File", JOptionPane.ERROR_MESSAGE );
			return;
		}
		
		// see if the file attributes are set
		if (certFileField.getFile().getPath().isEmpty())
			certFileField.setFile(createNewFileName(false));			
		
		if (keyFileField.getFile().getPath().isEmpty())
			keyFileField.setFile(createNewFileName(true));	
		
		// check if the files already exist
		if (certFileField.getFile().exists() || keyFileField.getFile().exists())
		{
			int selection = JOptionPane.showConfirmDialog(this, "The certificate or key file already exists.  This operation will overwrite the file.  Continue?",
					"Empty Password", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
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
		
		int exp = Integer.parseInt(expField.getValue().toString());
		int keyStre =  Integer.parseInt(keyStr.getValue().toString());
		
		CertCreateFields createFields = new CertCreateFields(attributes, certFileField.getFile(), keyFileField.getFile(),
				passField.getPassword(), exp, 
				keyStre, null, null);
		
		// this enough info to create the files... lets do it
		CertCreateFields retCert;
		try
		{
			retCert = CertGenerator.createCertificate(createFields, this.addToAltSubjects.isSelected());
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this,"An error occured creating the certificate authority: " + e.getMessage(), 
		 		    "Certificate Creation Error", JOptionPane.ERROR_MESSAGE);
		 
		    return;
		}
		
		if (retCert == null)
		{
			JOptionPane.showMessageDialog(this,"An error occured creating the certificate the authority: unknown error", 
		 		    "Certificate Creation Error", JOptionPane.ERROR_MESSAGE);
		 
		    return;
		}
		
		JOptionPane.showMessageDialog(this,"CA certificate and private key created successfully.", 
	 		    "SUCCESS", JOptionPane.PLAIN_MESSAGE);
		
		currentCert = retCert;
		
		setWorkflowContext(WF_CONTEXT_CERT_CREATED);
		
	}
	
	private void loadCACert()
	{
		File certFile = certFileField.getFile();
		File keyFile = keyFileField.getFile();
		
		if (!certFile.exists())
		{
			JOptionPane.showMessageDialog(this,"Certificate file does not exist or cannot be found.", 
		 		    "Invalid Cert File", JOptionPane.ERROR_MESSAGE );
			return;
		}
		
		if (!keyFile.exists())
		{
			JOptionPane.showMessageDialog(this,"Private key file does not exist or cannot be found.", 
		 		    "Invalid Key File", JOptionPane.ERROR_MESSAGE );
			
			return;
		}
		
		// load the certs from the file system
		CertCreateFields retCert;
		try
		{
			retCert = CertLoader.loadCertificate(certFile, keyFile, this.passField.getPassword());
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this,"An error occured loading the certificate authority: " + e.getMessage(), 
		 		    "Certificate Load Error", JOptionPane.ERROR_MESSAGE);
		 
		    return;
		}
		
		if (retCert == null)
		{
			JOptionPane.showMessageDialog(this,"An error occured loading the certificate the authority: unknown error", 
		 		    "Certificate Creation Error", JOptionPane.ERROR_MESSAGE);
		 
		    return;
		}
 
		// make sure this cert has privs to act as a CA and sign other CERTs		
		if (retCert.getSignerCert().getBasicConstraints() < 0)
		{
			JOptionPane.showMessageDialog(this,"This certificate's policy does not allowed it to sign other certificates.", 
		 		    "Policy Validation Error", JOptionPane.ERROR_MESSAGE);
		 
		    return;
		}

		// get the attributes
		if (retCert.getAttributes().containsKey("EMAILADDRESS"))
			this.emailField.setText(retCert.getAttributes().get("EMAILADDRESS").toString());
		else
			this.emailField.setText("");
		
		if (retCert.getAttributes().containsKey("CN"))
			this.cnField.setText(retCert.getAttributes().get("CN").toString());
		else
			this.cnField.setText("");
		
		if (retCert.getAttributes().containsKey("C"))
			this.countryField.setText(retCert.getAttributes().get("C").toString());
		else
			this.countryField.setText("");
		
		if (retCert.getAttributes().containsKey("ST"))
			this.stateField.setText(retCert.getAttributes().get("ST").toString());
		else
			this.stateField.setText("");
		
		if (retCert.getAttributes().containsKey("L"))
			this.locField.setText(retCert.getAttributes().get("L").toString());
		else
			this.locField.setText("");
		
		if (retCert.getAttributes().containsKey("O"))
			this.orgField.setText(retCert.getAttributes().get("O").toString());
		else
			this.orgField.setText("");	
		
		this.expField.setValue(retCert.getExpDays());
		
		JOptionPane.showMessageDialog(this,"CA certificate and private key loaded successfully.", 
	 		    "SUCCESS", JOptionPane.PLAIN_MESSAGE);
		
		currentCert = retCert;
		
		setWorkflowContext(WF_CONTEXT_CERT_LOADED);
	}
	
	protected static class TextEntryField extends JPanel
	{
		static final long serialVersionUID = -7340775331901207365L;
		
		private JLabel label;
		private JTextField text;
		
		public TextEntryField(String labelText)
		{
			super();
			
			this.setLayout(new BorderLayout());
						
			
			label = new JLabel(labelText);
			label.setPreferredSize(new Dimension(50, label.getPreferredSize().getSize().height));
			
			text = new JTextField();
			text.setPreferredSize(new Dimension(150, label.getPreferredSize().getSize().height));			
			
			add(label, BorderLayout.NORTH);
			
			JPanel textPanel = new JPanel(new BorderLayout());
			textPanel.add(text, BorderLayout.NORTH);
			add(textPanel);
		}
		
		@Override
		public void setEnabled(boolean b)
		{
			super.setEnabled(b);
			text.setEnabled(b);
		}
		
		public String getText()
		{
			return text.getText().trim();
		}
		
		public void setText(String value)
		{
			text.setText(value);
		}
	}
	
	protected static class SpinEntryField extends JPanel
	{
		static final long serialVersionUID = 2260694248137330015L;

		
		private JLabel label;
		private JSpinner value;
		
		public SpinEntryField(String labelText, int intValue)
		{
			super();
			this.setLayout(new BorderLayout());
			
			label = new JLabel(labelText);
			label.setPreferredSize(new Dimension(50, label.getPreferredSize().getSize().height));
			
			value = new JSpinner(new SpinnerNumberModel(intValue,
                    -10, //min
                    100000, //max
                    1));

			
			add(label, BorderLayout.NORTH);
			
			JPanel spinnerPanel = new JPanel(new BorderLayout());
			spinnerPanel.add(value, BorderLayout.NORTH);
			add(spinnerPanel);
		}
		
		@Override
		public void setEnabled(boolean b)
		{
			super.setEnabled(b);
			value.setEnabled(b);
		}
		
		public Object getValue()
		{
			return value.getValue();
		}
		
		public void setValue(Object value)
		{
			this.value.setValue(value);
		}
	}	
	
	protected static class DropDownEntry extends JPanel
	{
		static final long serialVersionUID = 3279442634853500454L;
		
		private JLabel label;
		private JComboBox selections; 
		
		public DropDownEntry(String labelText, Object[] items)
		{
			super();
			this.setLayout(new BorderLayout());
			
			label = new JLabel(labelText);
			label.setPreferredSize(new Dimension(50, label.getPreferredSize().getSize().height));
			
			selections = new JComboBox(items);

			
			add(label, BorderLayout.NORTH);
			
			JPanel dropPanel = new JPanel(new BorderLayout());
			dropPanel.add(selections, BorderLayout.NORTH);
			
			add(dropPanel);
		}
		
		@Override
		public void setEnabled(boolean b)
		{
			super.setEnabled(b);
			selections.setEnabled(b);
		}		
		
		public Object getValue()
		{
			return selections.getSelectedItem();
		}
		
		public void setValue(Object value)
		{
			selections.setSelectedItem(value);
		}		
	}	
	
	protected static class PasswordField extends JPanel
	{
		static final long serialVersionUID = -7837326704224526655L;		
		
		private JLabel label;
		private JPasswordField pass;
		
		public PasswordField(String labelText)
		{
			super();
			this.setLayout(new BorderLayout());
			
			label = new JLabel(labelText);
			label.setPreferredSize(new Dimension(150, label.getPreferredSize().getSize().height));
			
			pass = new JPasswordField();
			pass.setPreferredSize(new Dimension(150, label.getPreferredSize().getSize().height));
			
			add(label, BorderLayout.NORTH);
			
			JPanel passPanel = new JPanel(new BorderLayout());
			passPanel.add(pass, BorderLayout.NORTH);			
			
			add(passPanel);
		}
		
		@Override
		public void setEnabled(boolean b)
		{
			super.setEnabled(b);
			pass.setEnabled(b);
		}	
		
		public char[] getPassword()
		{	
			if (pass.getPassword() == null || pass.getPassword().length == 0)
				return "".toCharArray();
			
			return pass.getPassword();
		}
		
		public void clear()
		{
			pass.setText("");
		}
	}	
	
	protected static class FileField extends JPanel
	{
		static final long serialVersionUID = 8783281209944790372L;
		
		private JLabel label;
		private JTextField text;
		private JButton search;
		
		public FileField(String labelText, String file)
		{
			super();
			
			this.setLayout(new BorderLayout());
						
			
			label = new JLabel(labelText);
			label.setPreferredSize(new Dimension(50, label.getPreferredSize().getSize().height));
			
			text = new JTextField();
			text.setPreferredSize(new Dimension(250, label.getPreferredSize().getSize().height));			
			
			add(label, BorderLayout.NORTH);
			
			JPanel filePanel = new JPanel(new BorderLayout());
			
			search = new JButton("...");
			search.setPreferredSize(new Dimension(30, text.getPreferredSize().getSize().height));
			
			
			JPanel fileWithSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			fileWithSearchPanel.add(text);
			fileWithSearchPanel.add(search);
			
			filePanel.add(fileWithSearchPanel, BorderLayout.NORTH);
			add(filePanel);
			
			search.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					selectFile();
				}
			});
		}
		
		private void selectFile()
		{
		 	JFileChooser fc = new JFileChooser(); 
             fc.setDragEnabled(false); 

	 	 
             // set the current directory to be the images directory 
             if (!text.getText().trim().isEmpty())
             {
            	 File startFile = new File(text.getText()); 
            	 if (startFile.exists())
            	 {
            		 fc.setCurrentDirectory(startFile);
            	 }
             }

	 	 			
	 		int result = fc.showOpenDialog(this); 
	 		 
	 		// if we selected an image, load the image 
	 		if(result == JFileChooser.APPROVE_OPTION) 
	 		{ 
	 			text.setText(fc.getSelectedFile().getPath()); 
	 		} 
		}
		
		public File getFile()
		{
			return new File(text.getText().trim());
		}
		
		public void setFile(File fl)
		{
			if (fl != null)
				text.setText(fl.getAbsolutePath());
			else
				text.setText("");
		}
		
		@Override
		public void setEnabled(boolean b)
		{
			super.setEnabled(b);
			text.setEnabled(b);
			search.setEnabled(b);
		}			
	}	
}
