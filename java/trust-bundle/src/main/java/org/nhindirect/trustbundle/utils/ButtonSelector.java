package org.nhindirect.trustbundle.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.nhindirect.trustbundle.core.CreateSignedPKCS7;
import org.nhindirect.trustbundle.core.CreateUnSignedPKCS7;
import org.nhindirect.trustbundle.core.ViewTrustBundlePKCS7;
import org.nhindirect.trustbundle.ui.PreviewTrustBundle;

public class ButtonSelector implements ActionListener{
	Container pane;
	String error="";
	public ButtonSelector(Container pane) {
		this.pane = pane;
	}

	public void actionPerformed(ActionEvent event) {
		
		//System.out.println("Event"+event.getActionCommand());
		if(event.getActionCommand().equalsIgnoreCase("Create Bundle")){	
			Component[] data =  pane.getComponents();
			JTextField anchorDir = (JTextField) data[4];
			JTextField metaDataFile = (JTextField) data[7];
			JTextField destDir = (JTextField) data[10];
			JTextField bundleName = (JTextField) data[13];
			
			CreateUnSignedPKCS7 unCert = new CreateUnSignedPKCS7();		
			error = unCert.getParameters(anchorDir.getText(), metaDataFile.getText(), destDir.getText(), bundleName.getText());
			
			JTextPane feedback = (JTextPane) data[16];
			feedback.setText(error);
			
		}else if(event.getActionCommand().equalsIgnoreCase("Create Signed Bundle")){
			Component[] data =  pane.getComponents();
			JTextField anchorDir = (JTextField) data[4];
			JTextField metaDataFile = (JTextField) data[7];
			JTextField certificateDir = (JTextField) data[10];
			JPasswordField passkey = (JPasswordField) data[13];
			//String passVal=new String();
			//System.out.println("The password:"+String.copyValueOf(passkey.getPassword()));
			JTextField destDir = (JTextField) data[15];
			JTextField bundleName = (JTextField) data[18];
			
			CreateSignedPKCS7 unCert = new CreateSignedPKCS7();	
			
			error = unCert.getParameters(anchorDir.getText(), metaDataFile.getText(),certificateDir.getText(),String.copyValueOf(passkey.getPassword()), destDir.getText(), bundleName.getText());
			
			
			//System.out.println("The destination file path is:"+error);
			
			JTextPane feedback = (JTextPane) data[21];
			feedback.setText(error);
		}else{
			Component[] data =  pane.getComponents();
			JTextField trustBundle = (JTextField) data[4];
			
			ViewTrustBundlePKCS7 unCert = new ViewTrustBundlePKCS7();	
			
			error = unCert.getParameters(trustBundle.getText());
			
			new PreviewTrustBundle(error);
			PreviewTrustBundle.createAndShowGUI();
			//JTextPane feedback = (JTextPane) data[8];
			//feedback.setText(error);
		}
		
	}
	
}
