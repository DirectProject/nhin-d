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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

import javax.swing.JFrame;

/**
 * Simple Swing application for generating self signed certificates (CAs) and leaf certificates.  The certificates generated are 
 * streamlined to simple uses cases: it does not support the numerous options supported using tools such as openssl.  Certificates
 * SHA1WithRSAEncryption for digital signatures and the PBEWithMD5AndDES algorithm for private key encryption.
 * @author Greg Meyer
 *
 */
///CLOVER:OFF
public class DirectProjectCertGenerator extends JFrame
{
	static final long serialVersionUID = 7357116822589862967L;
	
	private CAPanel certAuth;

	public static void main(String[] _args)
	{     		
		DirectProjectCertGenerator hi = new DirectProjectCertGenerator();
		hi.setVisible(true);
		
	}
	
	public DirectProjectCertGenerator()
	{	
		super("The Direct Project Certificate Generator");
		setDefaultLookAndFeelDecorated(true);
		setSize(700, 310);
		setResizable(false);
		
		Point pt = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		
		this.setLocation(pt.x - (150), pt.y - (120));			
		
	    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    initUI();		
	}

	private void initUI()
	{
		getContentPane().setLayout(new BorderLayout());
		
		certAuth = new CAPanel();
		
		getContentPane().add(certAuth);
	}
}
///CLOVER:ON