/* 
Copyright (c) 2013, NHIN Direct Project
All rights reserved.

Authors:
   Amulya Misra        Drajer LLC/G3Soft
   Satyajeet Mahapatra Drajer LLC/G3Soft
 
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
