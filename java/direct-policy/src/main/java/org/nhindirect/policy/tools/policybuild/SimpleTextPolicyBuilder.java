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
 * Main application
 * @author Greg Meyer
 *
 */
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
}
