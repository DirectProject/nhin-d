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
///CLOVER:ON