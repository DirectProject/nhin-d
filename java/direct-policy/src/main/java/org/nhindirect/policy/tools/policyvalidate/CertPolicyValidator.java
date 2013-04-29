package org.nhindirect.policy.tools.policyvalidate;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

import javax.swing.JFrame;

///CLOVER:OFF
public class CertPolicyValidator extends JFrame
{
	static final long serialVersionUID = -5834001498578311969L;

	
	private ValidatePanel validate;
	
	public static void main(String[] _args)
	{     		
		CertPolicyValidator hi = new CertPolicyValidator();
		hi.setVisible(true);
		
	}
	
	public CertPolicyValidator()
	{	
		super("The Direct Project Certificate Policy Validator");
		setDefaultLookAndFeelDecorated(true);
		setSize(700, 300);
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
		
		validate = new ValidatePanel();
		
		getContentPane().add(validate);
	}
}
///CLOVER:ON
