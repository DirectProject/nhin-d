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

package org.nhindirect.policy.tools.policybuild;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Main application entry point
 * @author Greg Meyer
 * @since 1.0
 */
///CLOVER:OFF
public class SimpleTextPolicyBuilder extends JFrame
{

	private static final long serialVersionUID = -5676575004993173301L;

	protected JMenuBar menuBar;
	protected EditorPanel editPanel;
	protected JMenuItem openFile;
	protected JMenuItem quit;
	protected JMenuItem save;
	protected JMenuItem saveAs;
	
	public static void main(String[] _args)
	{     		
		SimpleTextPolicyBuilder hi = new SimpleTextPolicyBuilder();
		hi.setVisible(true);
		
	}
	
	public SimpleTextPolicyBuilder()
	{	
		super("DirectProject SimpleText Policy Builder");
		setDefaultLookAndFeelDecorated(true);
		setSize(700, 700);
		
		Point pt = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		
		this.setLocation(pt.x - (350), pt.y - (350));			
		
	    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    initUI();	
	    
	    addActions();
	}
	
	private void initUI()
	{
		// build the menu bar
		menuBar = new JMenuBar();
		
		// build the file menu
		JMenu fileMenu = new JMenu("File");
		quit = new JMenuItem("Quit");
		save = new JMenuItem("Save");
		saveAs = new JMenuItem("SaveAs");	
		openFile = new JMenuItem("Open");
		fileMenu.add(openFile);
		fileMenu.addSeparator();
		fileMenu.add(save);
		fileMenu.add(saveAs);
		//fileMenu.add(quit);
		
		menuBar.add(fileMenu);
		
		
		this.setJMenuBar(menuBar);
		
		getContentPane().setLayout(new BorderLayout());
		
		editPanel = new EditorPanel();
		
		getContentPane().add(editPanel);
	}
	
	private void addActions()
	{
		openFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				openPolicyFile();
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				savePolicyFile();
			}
		});
		
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				savePolicyFileAs();
			}
		});
	}
	
	private void openPolicyFile()
	{
	 	final JFileChooser fc = new JFileChooser(); 
        fc.setDragEnabled(false); 
        
 		int result = fc.showOpenDialog(this); 
		 
 		// if we selected file, load the file 
 		if(result == JFileChooser.APPROVE_OPTION) 
 		{ 
 			editPanel.loadFromFile(fc.getSelectedFile());
 			
 		} 
	}
	
	private void savePolicyFile()
	{
		editPanel.savePolicyFile();
	}
	
	private void savePolicyFileAs()
	{
		editPanel.savePolicyFileAs();
	}
}
///CLOVER:ON
