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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;


/**
 * The TrustBundlePublisher is the UI class for Trust Bundle Publisher. This class is the entry point for the Trust Bundle Publisher tool
 * Where user can provide details of anchor, Meta data files, trust bundle name and can create the trust bundle.
 * This screen also has an option to traverse to the other two screen where user can create signed bundles and view bundles.
 * @author Satyajeet
 *
 */
public class TrustBundlePublisher implements ActionListener {
    
	
	final static boolean RIGHT_TO_LEFT = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JFrame controlFrame;

	public static void main(String[] args) {
		controlFrame = UnSignedTrustBundle.createAndShowGUI();
	
    }

	/*
	 * Control The Flow of the Screen for Different Type of Trust Bundle and Viewing the same
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
				
		if(e.getActionCommand().trim().equalsIgnoreCase("Create Unsigned Trust Bundle"))
		{
			
			controlFrame.dispose();
			controlFrame = UnSignedTrustBundle.createAndShowGUI();
			
		}
		else if (e.getActionCommand().trim().equalsIgnoreCase("Create Signed Trust Bundle"))
		{
			
			controlFrame.dispose();
			controlFrame = SignedTrustBundle.createAndShowGUI();
			
		}
		else if (e.getActionCommand().trim().equalsIgnoreCase("View Trust Bundle"))
		{
			
			controlFrame.dispose();
			controlFrame = ViewTrustBundle.createAndShowGUI();
			
		}
	}
	
}//end of class
