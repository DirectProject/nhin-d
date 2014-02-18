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
package org.nhindirect.trustbundle.ui;

import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;


import org.nhindirect.trustbundle.utils.ComponentCreation;

/**
 * The SignedTrustBundle is the UI class for Trust Bundle Publisher. This class is the entry point for the Trust Bundle Publisher tool
 * Where user can provide details of anchor, Meta data files, trust bundle name and can create the trust bundle.
 * This screen also has an option to traverse to the other two screen where user can create signed bundles and view bundles.
 * @author Amulya Kumar Mishra
 *
 */
public class SignedTrustBundle extends JFrame {
	
    /**
	 * Generated Serial Number
	 */
	private static final long serialVersionUID = -7193107570054075169L;
    final static boolean RIGHT_TO_LEFT = false;       
    /**
	 * Set the orientation of the components inside the pane.
	 * @param pane Accept the container pane and set its orientation.
	 * @return void 
	 */  
    public static void addComponentsToPane(Container pane) {
        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }

       
	pane.setLayout(new GridBagLayout());
	ComponentCreation creation  = new ComponentCreation();
	/**
	 * Below block create the screen by using the ComponentCreation class and its method to create components.
	 * The componentCreation class has various methods exposed which will take the pane and type of component with its position.
	 * Radio, label, File, Directory etc are different type of pane can be created using the class. 
	 */   
	GridBagConstraints c = new GridBagConstraints();
	c.anchor = (c.gridx == 0) ? GridBagConstraints.EAST : GridBagConstraints.WEST;
	c.weighty  = 1.5;
	c.weightx = 0.5;
	ButtonGroup myButtonGroup = new ButtonGroup();
	creation.createComponent("RADIO", "  Create Unsigned Trust Bundle", false, c, pane,myButtonGroup, 0, 0);
	creation.createComponent("RADIO", "  Create Signed Trust Bundle", true, c, pane,myButtonGroup, 1, 0);
	creation.createComponent("RADIO", "  View Trust Bundle", false, c, pane,myButtonGroup, 2, 0);
	
	creation.createComponent("LABEL", "  Trust Anchor Directory",false,c, pane,null, 0, 1);
	creation.createBrowseFile("DIRECTORY", "Select Trust Anchor Directory",c, pane, 1, 1);
	creation.createComponent("LABEL", "  Optional Meta Data File",false,c, pane,null, 0, 2);
	creation.createBrowseFile("FILE", "Select Meta Data File",c, pane, 1, 2);
	creation.createComponent("LABEL", "  Signing Certificate",false,c, pane,null, 0, 3);
	creation.createBrowseFile("FILE", "Select Certificate File (.p12)",c, pane, 1, 3);
	creation.createComponent("LABEL", "  Signing Certificate Password Key",false,c, pane,null, 0, 4);
	c.fill = GridBagConstraints.HORIZONTAL;
	creation.createComponent("PASSWORD", "",false,c, pane,null, 1, 4);	
	creation.createComponent("LABEL", "  Trust Bundle Destination Directory",false,c, pane,null, 0, 5);
	creation.createBrowseFile("DIRECTORY", "Select Trust Bundle Destination Directory",c, pane, 1, 5);
	creation.createComponent("LABEL", "  Trust Bundle Name",false,c, pane,null, 0, 6);
	c.fill = GridBagConstraints.HORIZONTAL;
	creation.createComponent("TEXT", "",false,c, pane,null, 1, 6);
	c.fill = GridBagConstraints.NONE;
	creation.createComponent("BUTTON", "Create Signed Bundle",false,c, pane,null, 1, 7);
	c.fill = GridBagConstraints.NONE;
	creation.createComponent("LABEL", " 	Feedback:",false,c, pane,null, 0, 8); 
	creation.createComponent("TEXTPANE", "",false,c, pane,null, 1, 8); 

    }    
         
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static JFrame createAndShowGUI() {
        //Create and set up the window.
    	JFrame frame= new JFrame("Trust Bundle Publisher");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    	frame.setSize(900,500);
    	frame.setName("signed");
       
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());

        frame.setVisible(true);
        return frame;

    }

  }