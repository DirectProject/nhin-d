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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

///CLOVER:OFF
/**
 * Visual selector field for a file.
 * @author Greg Meyer
 * @since 1.0
 */
public class FileField extends JPanel
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
 		 
 		// if we selected file, load the file 
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
///CLOVER:ON