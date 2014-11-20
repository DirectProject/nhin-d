package org.nhindirect.common.crypto.tools;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.nhindirect.common.crypto.MutableKeyStoreProtectionManager;
import org.nhindirect.common.crypto.exceptions.CryptoException;
import org.nhindirect.common.crypto.impl.DynamicPKCS11TokenKeyStoreProtectionManager;

///CLOVER:OFF
public class PKCS11SecretKeyManager extends JFrame
{
	
	private static final long serialVersionUID = 4851276510546674236L;
	
	protected static String pkcs11ProviderCfg = null;
	protected static MutableKeyStoreProtectionManager mgr = null; 
	
	protected JTable keyDataTable;
	protected JButton removeKeyButton;
	protected JButton addAESKeyButton;
	protected JButton addGenericKeyButton;
	protected DefaultTableModel keyDataModel;
	protected JButton quitButton;

	
	public static void main(String[] argv)
	{     
		// need to check if there is a configuration for the PKCS11
		// provider... if not, assume the JVM has already been configured for one
		if (argv.length > 0)
		{
			// Check parameters
	        for (int i = 0; i < argv.length; i++)
	        {
	            String arg = argv[i];
	
	            // Options
	            if (!arg.startsWith("-"))
	            {
	                System.err.println("Error: Unexpected argument [" + arg + "]\n");
	                printUsage();
	                System.exit(-1);
	            }
	            else if (arg.equalsIgnoreCase("-pkcscfg"))
	            {
	                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
	                {
	                    System.err.println("Error: Missing pkcs11 provider file name.");
	                    printUsage();
	                    System.exit(-1);
	                }
	                
	                pkcs11ProviderCfg = argv[++i];
	                
	            }
	            else if (arg.equals("-help"))
	            {
	                printUsage();
	                System.exit(-1);
	            }            
	            else
	            {
	                System.err.println("Error: Unknown argument " + arg + "\n");
	                printUsage();
	                System.exit(-1);
	            }
	        }		
		}
		
		if (pkcs11ProviderCfg != null)
		{
			// add the security provider
			final Provider p = new sun.security.pkcs11.SunPKCS11(pkcs11ProviderCfg);
			Security.addProvider(p);
		}
		
		// need to login
		try
		{
			mgr = tokenLogin();
		}
		catch (CryptoException e)
		{
			JOptionPane.showMessageDialog(null, "Failed to login to hardware token: " + e.getMessage(), "Token Login Failure", 
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		final PKCS11SecretKeyManager hi = new PKCS11SecretKeyManager();
		hi.setVisible(true);
	}
	
   /*
    * Print program usage.
    */
    private static void printUsage()
    {
        StringBuffer use = new StringBuffer();
        use.append("Usage:\n");
        use.append("java PKCS11SecretKeyManager (options)...\n\n");
        use.append("options:\n");
        use.append("-pkcscfg    PKCS11 Config File  Optional location for the PKCS11 provider configuration.  If this is not" +
        		" set, then it is assumed that the JVM has already been configured to support your PKCS11 token.\n");
        use.append("            Default: \"\"\n\n");

        System.err.println(use);        
    }
	
    public static MutableKeyStoreProtectionManager tokenLogin() throws CryptoException
    {

    	TokenLoginCallback login = new TokenLoginCallback();
    	
    	final DynamicPKCS11TokenKeyStoreProtectionManager loginMgr = new DynamicPKCS11TokenKeyStoreProtectionManager("", "", login);

    	
    	return loginMgr;
    }
    
	public PKCS11SecretKeyManager()
	{
		super("DirectProject PKCS11 Secret Key Manager");
		setDefaultLookAndFeelDecorated(true);
		setSize(700, 700);
		
		Point pt = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		
		this.setLocation(pt.x - (350), pt.y - (350));			
		
	    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    initUI();	
	    
	    addActions();
	    
	    updateKeyTableData();
	}

	private void initUI()
	{
		this.getContentPane().setLayout(new BorderLayout(5, 5));
		
		// Top Panel
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		addAESKeyButton = new JButton("Add AES Key");
		addAESKeyButton.setSize(new Dimension(30, 100));
		addGenericKeyButton = new JButton("Add Text Key"); 
		addGenericKeyButton.setSize(new Dimension(30, 100));
		removeKeyButton = new JButton("Remove Key(s)");
		removeKeyButton.setSize(new Dimension(30, 100));
		
		topPanel.add(addAESKeyButton);
		topPanel.add(addGenericKeyButton);
		topPanel.add(removeKeyButton);
		
		this.getContentPane().add(topPanel, BorderLayout.NORTH);
		
		
		// Middle and list panel
		JPanel midPanel = new JPanel();
		midPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
		midPanel.setLayout(new BorderLayout(5, 5));
		
		JLabel keyListLabel = new JLabel("Secret Keys:");
		
		Object[][] data = {};
		String[] columnNames = {"Key Alias", "Key Type", "Key Value"};
		
		keyDataModel = new DefaultTableModel(data, columnNames);
		keyDataTable = new JTable(keyDataModel);
		JScrollPane scrollPane = new JScrollPane(keyDataTable);
		keyDataTable.setFillsViewportHeight(true);
		
		midPanel.add(keyListLabel, BorderLayout.NORTH);
		midPanel.add(scrollPane, BorderLayout.CENTER);
		
		this.getContentPane().add(midPanel, BorderLayout.CENTER);
		
		// Bottom Panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		quitButton = new JButton("Quit");
		quitButton.setSize(new Dimension(30, 100));
		bottomPanel.add(quitButton);
		
		this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void addActions()
	{
		addAESKeyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				addAESKey();
			}
		});
		
		addGenericKeyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				addTextKey();
			}
		});
		
		removeKeyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				removeKeys();
			}
		});
		
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.exit(-1);
			}
		});
	}
	
	private void addAESKey()
	{
		final String input = JOptionPane.showInputDialog(this, "Key Alias Name:", "Generate New random AES Secret Key", JOptionPane.OK_CANCEL_OPTION);
		if (input != null && !input.trim().isEmpty())
		{
			// generate a new random secret key
			try
			{
				final KeyGenerator keyGen = KeyGenerator.getInstance("AES");
				final SecureRandom random = new SecureRandom(); // cryptograph. secure random 
				keyGen.init(random); 
				final SecretKey key = keyGen.generateKey();
				
				mgr.clearKey(input);
				mgr.setKey(input, key);
				
				updateKeyTableData();
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this, "Failed to add random new AES key: " + e.getMessage(), "Add Key Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void addTextKey()
	{
		
		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		final JPanel topPanel = new JPanel();
		final JLabel aliasLabel = new JLabel("Alias:");
		aliasLabel.setSize(60, 30);
		final JTextField aliasField = new JTextField(40);

		topPanel.add(aliasLabel);
		topPanel.add(aliasField);
		
		final JPanel bottomPanel = new JPanel();
		final JLabel keyLabel = new JLabel("Key:");
		keyLabel.setSize(60, 30);
		final JTextField keyField = new JTextField(40);

		bottomPanel.add(keyLabel);
		bottomPanel.add(keyField);
		
		
		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(bottomPanel, BorderLayout.SOUTH);
		
		final String[] options = new String[]{"OK", "Cancel"};
		int option = JOptionPane.showOptionDialog(null, panel, "Generate New Text Based Secret Key ",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
		
		if (option == JOptionPane.OK_OPTION)
		{
			final String alias = aliasField.getText();
			final String keyText = keyField.getText();
			
			
			if ((alias != null && !alias.trim().isEmpty()) &&
					keyText != null && !keyText.trim().isEmpty())
			{
				// generate a new random secret key
				try
				{	
					mgr.clearKey(alias);
					mgr.setKey(alias, new SecretKeySpec(keyText.getBytes(), ""));
					
					updateKeyTableData();
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(this, "Failed to add new text based secret key: " + e.getMessage(), "Add Key Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}	
	
	private void removeKeys()
	{
		if (keyDataTable.getSelectedRowCount() == 0)
		{
			JOptionPane.showMessageDialog(this, "No keys are selected.", "Remove Keys", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		else if (JOptionPane.showConfirmDialog(this, "Are you sure you want to removed the selected Keys?", "Remove Keys", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		{
			int[] rows = keyDataTable.getSelectedRows();
			for (int row : rows)
			{
				final String alias = (String)keyDataTable.getValueAt(row, 0);
				
				try
				{
					mgr.clearKey(alias);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(this, "Failed to remove key with alias " + alias + ":" + e.getMessage(), 
							"Remove Key Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			updateKeyTableData();
		}
		
		
	}
	
	private void updateKeyTableData()
	{
		try
		{
			for (int ctx = (keyDataModel.getRowCount() -1 ); ctx >=0; --ctx)
			{
				keyDataModel.removeRow(ctx);
				
			}
			// get all of the data from the token
			Map<String, Key> keys = mgr.getAllKeys();
			for (Entry<String, Key> entry : keys.entrySet())
			{
				String type = "";
				final Object value = entry.getValue();
				if (value instanceof SecretKey)
					type = "Secret Key: " + ((Key)value).getAlgorithm();
				else if (value instanceof PublicKey)
					type = "Public Key: " + ((Key)value).getAlgorithm();
				else if (value instanceof PrivateKey)
					type = "Private Key: " + ((Key)value).getAlgorithm();
				else
					type = value.getClass().toString();
				
				keyDataModel.addRow(new Object[] {entry.getKey(), type ,"***"});
				keyDataModel.fireTableDataChanged();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
///CLOVER:ON