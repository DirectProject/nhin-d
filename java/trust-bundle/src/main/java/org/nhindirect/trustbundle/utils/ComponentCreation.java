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

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;


import org.nhindirect.trustbundle.ui.TrustBundlePublisher;


/**
 * ComponentCreation is a utility class used to create swing components like Browse, JButton, JLabel, JText etc.
 * This class extend the Jframe and use Swing API to create the components. This class contants various methods which take parameters and create component dynamically as per the input.
 * @author Amulya Kumar Mishra
 *
 */
public class ComponentCreation extends JFrame {
	/**
	 * Generated Serial Number
	 */
	private static final long serialVersionUID = -7193107570054075169L;

	/**
	 * Process the container pane to create different swing component as per the identifier given as parameter
	 * @param type This decide which component should be created
	 * @param labelData This is the value of a component/or can be used as title of the component
	 * @param flag This is used to make any check box or radio button selected
	 * @param c is a object of GridBagConstarins, and used to set any grid layout
	 * @param pane Container object to add component into this pane
	 * @param myButtonGroup used for radio or check boxes to group together
	 * @param gridx This is the x index integer value for the component inside the grid
	 * @param gridy This is the y index integer value for the component inside the grid
	 * @return void 
	 */  
	public void createComponent(String type,String lableData,boolean flag,GridBagConstraints c,Container pane,ButtonGroup myButtonGroup,int gridx, int gridy) {      

		if(type.equalsIgnoreCase("RADIO")){
			JRadioButton aButton = new JRadioButton(lableData,flag);
			c.gridx = gridx;
			c.gridy = gridy;
			pane.add(aButton, c);
			/*
			 * Button group to make the dynamic movement of screens
			 */
			TrustBundlePublisher ex = new TrustBundlePublisher();
			if(myButtonGroup != null){
				myButtonGroup.add(aButton);	
				if(lableData.equalsIgnoreCase("  Create Unsigned Trust Bundle")){
					//UnSignedTrustBundle ex = new UnSignedTrustBundle();
					aButton.addActionListener(ex);
				}else if(lableData.equalsIgnoreCase("  Create Signed Trust Bundle")){
					//SignedTrustBundle ex = new SignedTrustBundle();
					aButton.addActionListener(ex);
				}else{
					//ViewTrustBundle ex = new ViewTrustBundle();
					aButton.addActionListener(ex);
				}
					
			}
		}	
		else if(type.equalsIgnoreCase("LABEL")){
			JLabel label1 = new JLabel(lableData); 
			c.gridx = gridx;
			c.gridy = gridy;
			pane.add(label1, c);
		}	
		else if(type.equalsIgnoreCase("TEXT")){
			JTextField field = new JTextField();
			c.gridx = gridx;
			c.gridy = gridy;
			pane.add(field, c);
		}
		else if(type.equalsIgnoreCase("PASSWORD")){
			//JTextField field = new JTextField();
			JPasswordField passwordField = new JPasswordField(10);
			//passwordField.setActionCommand(OK);
			//passwordField.addActionListener(this);
			c.gridx = gridx;
			c.gridy = gridy;
			pane.add(passwordField, c);
		}	
		else if(type.equalsIgnoreCase("BUTTON")){
			 JButton button = new JButton(lableData);
			c.gridx = gridx;
			c.gridy = gridy;
			
			pane.add(button, c);
			ButtonSelector create = new ButtonSelector(pane);
			button.addActionListener(create);
		}	
		else if(type.equalsIgnoreCase("TEXTPANE")){
			JTextPane jTextPane = new JTextPane();
		 	jTextPane.setEditable(false); 	 
		 	jTextPane.setBackground(new Color(238,238,238));;
		 	//jTextPane.setSize(55, 10);
			c.gridx = gridx;
			c.gridy = gridy;
			pane.add(jTextPane, c);
		}	
		else if(type.equalsIgnoreCase("TEXTAREA")){
			JTextArea jTextPane = new JTextArea(lableData);
		 	jTextPane.setEditable(false); 	 
		 	jTextPane.setBackground(new Color(238,238,238));;
			c.gridx = gridx;
			c.gridy = gridy;
			pane.add(jTextPane, c);
		}	
	}
	/**
	 * Process the container pane to create file chooser or directory chooser swing component as per the identifier given as parameter
	 * @param type This decide which component should be created
	 * @param labelData This is the value of a component/or can be used as title of the component
	 * @param c is a object of GridBagConstarins, and used to set any grid layout
	 * @param pane Container object to add component into this pane
	 * @param gridx This is the x index integer value for the component inside the grid
	 * @param gridy This is the y index integer value for the component inside the grid
	 * @return void 
	 */  
	public void createBrowseFile(String type,String lableData,GridBagConstraints c,Container pane,int gridx, int gridy) {      
		JTextField filename = new JTextField();
		filename.setText(lableData);
		ComponentCreation ex = new ComponentCreation();
		JButton open = new JButton("Browse");			
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = gridx;
		c.gridy = gridy;
		pane.add(filename, c);
		if(type.equalsIgnoreCase("FILE")){		    
			open.addActionListener(ex.new OpenL(filename));		     
		}	
		if(type.equalsIgnoreCase("DIRECTORY")){			
			open.addActionListener(ex.new OpenD(filename));		    
		}	
		c.fill = GridBagConstraints.NONE;
		c.gridx = gridx+1;
		c.gridy = gridy; 
		pane.add(open, c);    
		filename.setEditable(false);
	}  
	/**
	 * OpenL is a utility sub class used to define the action of a file chooser. 
	 * This class extend the ActionListener and use Swing API to create the components and action listener for it. 
	 * @author Amulya Kumar Mishra
	 */
	public class OpenL implements ActionListener {
		JTextField filename = new JTextField();
		OpenL(JTextField filename){
			this.filename = filename;
		}
		/**
		 * Implemented method of ActionListener to Process the FileChooser action and display a file to choose.
		 * @param e ActionEvent listener to listen the browse button click event
		 * @return void 
		 */  
		public void actionPerformed(ActionEvent e) {
			JFileChooser c = new JFileChooser();	         
			int rVal = c.showOpenDialog(ComponentCreation.this);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				filename.setText(c.getSelectedFile().getAbsolutePath());

			}
			if (rVal == JFileChooser.CANCEL_OPTION) {
				filename.setText("You pressed cancel");

			}
		}
	} 
	/**
	 * OpenL is a utility sub class used to define the action of a Directory chooser. 
	 * This class extend the ActionListener and use Swing API to create the components and action listener for it. 
	 * @author Amulya Kumar Mishra
	 */

	public class OpenD implements ActionListener {
		JTextField filename = new JTextField();
		OpenD(JTextField filename){
			this.filename = filename;
		}
		/**
		 * Implemented method of ActionListener to Process the FileChooser action and display a directory to choose.
		 * @param e ActionEvent listener to listen the browse button click event
		 * @return void 
		 */  

		public void actionPerformed(ActionEvent e) {
			JFileChooser c = new JFileChooser();
			
			c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int rVal = c.showOpenDialog(ComponentCreation.this);
			if (rVal == JFileChooser.APPROVE_OPTION) {

				filename.setText(c.getSelectedFile().getAbsolutePath());


			}
			if (rVal == JFileChooser.CANCEL_OPTION) {
				filename.setText("You pressed cancel");

			}
		}
	}
	
}