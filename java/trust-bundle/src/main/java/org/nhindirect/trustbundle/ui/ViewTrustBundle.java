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
 * The ViewTrustBundle is the UI class for Trust Bundle Publisher. This class is used to view any trust bundle by browsing it
 * Where user can provide details of trust bundle location and can view the trust bundle.
 * This screen also has an option to traverse to the other two screen where user can create signed bundles and view bundles.
 * This class extends JFrame and implements ActionListener 
 * @author Amulya Kumar Mishra
 *
 */
public class ViewTrustBundle extends JFrame {
	
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
	creation.createComponent("RADIO", "  Create Signed Trust Bundle", false, c, pane,myButtonGroup, 1, 0);
	creation.createComponent("RADIO", "  View Trust Bundle", true, c, pane,myButtonGroup, 2, 0);
	
	creation.createComponent("LABEL", "  Trust Bundle",false,c, pane,null, 0, 1);
	creation.createBrowseFile("FILE", "Select Trust Bundle File",c, pane, 1, 1);
	
	creation.createComponent("BUTTON", "View Bundle",false,c, pane,null, 1, 5);
	c.fill = GridBagConstraints.NONE;
	creation.createComponent("LABEL", " 	Feedback:",false,c, pane,null, 0, 6); 
	creation.createComponent("TEXTPANE", "",false,c, pane,null, 1, 6); 

    }    
         
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static JFrame createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Trust Bundle Publisher");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    	frame.setSize(900,500);
       
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());

        frame.setVisible(true);
        return frame;
  
    }
   

}