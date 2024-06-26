package org.fugerit.java.client.redis.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.fugerit.java.client.redis.ClientRedisArgs;
import org.fugerit.java.client.redis.ClientRedisHelper;
import org.fugerit.java.core.lang.helpers.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRedisGUI extends JFrame implements WindowListener, ActionListener {

	public static final String VERSION = "1.1.0";
	
	private static final Logger logger = LoggerFactory.getLogger(ClientRedisGUI.class);

	private static final String VALUE_FOR_KEY_LIT = "Value for key '";
	
	private static final String HAS_BEEN_SET_TO_LIT = "' has been set to '";
	
	private static final String ERROR_GETTING_VALUE_FOR_JEY_LIT = "Error getting value for key=";
	
	private Component setUI( Component c ) {
		return c;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 10544453453L;

	private JTextArea outputArea;
	private JTextArea urlArea;
	private JTextArea keyArea;
	private JTextArea valueArea;
	private JTextArea ttlArea;

	private JButton getButton;
	private JButton setButton;
	private JButton delButton;
	private JButton listKeysButton;
	private JButton listAllButton;

	private transient ClientRedisHelper helper = null;
	
	public ClientRedisGUI(Properties params) {
		super("REDIS Client "+VERSION);

		// crea layout
		BorderLayout mainLayout = new BorderLayout();
		this.setLayout(mainLayout);

		this.outputArea = (JTextArea) setUI(  new JTextArea("Result area...") );
		JScrollPane outputScroll = new JScrollPane(this.outputArea);

		JPanel controlPanel = (JPanel) setUI( new JPanel(new GridLayout( 4, 2, 2, 2 )) );
		this.urlArea = new JTextArea(params.getProperty(ClientRedisArgs.ARG_REDIS_URL, "... redis url ..."));
		this.keyArea = new JTextArea(params.getProperty(ClientRedisArgs.ARG_KEY, "") );
		this.valueArea = new JTextArea(params.getProperty(ClientRedisArgs.ARG_VALUE, "") );
		this.ttlArea = new JTextArea(params.getProperty(ClientRedisArgs.ARG_TTL, "") );
		controlPanel.add( setUI( new JLabel("URL:", SwingConstants.RIGHT ) ) );
		controlPanel.add( setUI( this.urlArea ) );
		controlPanel.add( setUI(  new JLabel("key:", SwingConstants.RIGHT ) ) );
		controlPanel.add( setUI( this.keyArea ) );
		controlPanel.add( setUI( new JLabel("value:", SwingConstants.RIGHT) ) );
		controlPanel.add( setUI( this.valueArea ) );
		controlPanel.add( setUI( new JLabel("ttl(s):", SwingConstants.RIGHT) ) );
		controlPanel.add( setUI( this.ttlArea ) );

		JPanel actionPanel = (JPanel) setUI( new JPanel(new GridLayout( 1, 5, 0, 0 )) );
		this.getButton = new JButton( "Get" );
		this.setButton = new JButton( "Set" );
		this.delButton = new JButton( "Del" );
		this.listKeysButton = new JButton( "List keys" );
		this.listAllButton = new JButton( "List values" );
		actionPanel.add( this.setUI( this.getButton ) );
		actionPanel.add( this.setUI( this.setButton ) );
		actionPanel.add( this.setUI( this.delButton ) );
		actionPanel.add( this.setUI( this.listKeysButton ) );
		actionPanel.add( this.setUI( this.listAllButton ) );

		this.add( this.setUI( actionPanel ), BorderLayout.SOUTH );
		this.add( controlPanel, BorderLayout.NORTH);
		this.add( outputScroll, BorderLayout.CENTER);

		this.getButton.addActionListener(this);
		this.setButton.addActionListener(this);
		this.delButton.addActionListener(this);
		this.listKeysButton.addActionListener(this);
		this.listAllButton.addActionListener(this);
		this.addWindowListener(this);

		this.setResizable(true);
		this.setSize(800, 600);
		this.setVisible(true);
	}

	private void outputLine(String line) {
		this.outputArea.append(line + "\n");
	}

	private void resetOutput(String line) {
		this.outputArea.setText("");
		this.outputLine(line);
	}

	private void handleError( String baseMessage, Exception e ) {
		String message = baseMessage+" : "+e;
		logger.error( message, e );
		this.outputLine( message );
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);
		this.outputLine(new String(baos.toByteArray()));
	}
	
	private ClientRedisHelper getHelper() {
		String redisUrl = this.urlArea.getText();
		if ( this.helper == null || !this.helper.getRedisUrl().equals( redisUrl ) ) {
			try {
				this.helper = ClientRedisHelper.newHelper(redisUrl);
			} catch (Exception e) {
				this.helper = null;
				this.handleError( "Error creating redis client" , e);
			}
		}
		return this.helper;
	}

	private void executeStart( JButton button ) {
		this.resetOutput("Execute "+button.getText()+" : " );
	}
	
	private void set() {
		this.executeStart( this.setButton );
		String key = this.keyArea.getText();
		String value = this.valueArea.getText();
		try {
			if ( StringUtils.isEmpty( key ) || StringUtils.isEmpty( value ) ) {
				this.outputLine("Required parameters : key, value");
			} else {
				ClientRedisHelper client = this.getHelper();
				if ( client != null ) {
					String ttl = this.ttlArea.getText();
					if ( StringUtils.isNotEmpty( ttl ) ) {
						long time = Long.parseLong( ttl );
						client.set(key, value, time);
						this.outputLine(VALUE_FOR_KEY_LIT+key+HAS_BEEN_SET_TO_LIT+value+"' and ttl="+time+"(s)");	
					} else {
						client.set(key, value);
						this.outputLine(VALUE_FOR_KEY_LIT+key+HAS_BEEN_SET_TO_LIT+value+"'");
					}
				}
			}
		} catch (Exception e) {
			this.handleError( ERROR_GETTING_VALUE_FOR_JEY_LIT+key , e);
		}
	}
	
	private void get() {
		this.executeStart( this.getButton );
		String key = this.keyArea.getText();
		try {
			if ( StringUtils.isEmpty( key ) ) {
				this.outputLine("Missing parameter : key");
			} else {
				ClientRedisHelper client = this.getHelper();
				if ( client != null ) {
					String value = client.get( key );
					if ( value == null ) {
						this.outputLine("Key '"+key+"' not found");	
					} else {
						String line = VALUE_FOR_KEY_LIT+key+"' is '"+value+"'";
						long ttl = client.getTTL(key);
						if ( ttl >= 0 ) {
							line+= ", ttl="+(ttl)+"(s)";
						} else if ( ttl == -1 ) {
							line+= ", with no expiration";
						}
						this.outputLine( line );
					}
					
				}
			}
		} catch (Exception e) {
			this.handleError( ERROR_GETTING_VALUE_FOR_JEY_LIT+key , e);
		}
	}
	
	private void del() {
		this.executeStart( this.delButton );
		String key = this.keyArea.getText();
		try {
			if ( StringUtils.isEmpty( key ) ) {
				this.outputLine("Missing parameter : key");
			} else {
				ClientRedisHelper client = this.getHelper();
				if ( client != null ) {
					long value = client.del( key );
					this.outputLine("Key '"+key+"' del result : "+value );	
				}
			}
		} catch (Exception e) {
			this.handleError( ERROR_GETTING_VALUE_FOR_JEY_LIT+key , e);
		}
	}

	private void listKeys() {
		this.executeStart( this.listKeysButton );
		try {
			ClientRedisHelper client = this.getHelper();
			if ( client != null ) {
				for ( String key : client.listKeys() ) {
					this.outputLine(key);
				}
			}
		} catch (Exception e) {
			this.handleError( "Error getting key list" , e);
		}
	}

	private void listAll() {
		this.executeStart( this.listKeysButton );
		try {
			ClientRedisHelper client = this.getHelper();
			if ( client != null ) {
				for ( Entry<String, String> entry : client.all() ) {
					this.outputLine( entry.getKey()+" : '"+entry.getValue()+"'" );
				}
			}
		} catch (Exception e) {
			this.handleError( "Error getting key list" , e);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.getButton) {
			this.get();
		} else if (e.getSource() == this.setButton) {
			this.set();
		} else if (e.getSource() == this.delButton) {
			this.del();
		} else if (e.getSource() == this.listKeysButton) {
			this.listKeys();
		} else if (e.getSource() == this.listAllButton) {
			this.listAll();			
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// this method is not needed in this implementation
	}

	@Override
	public void windowClosed(WindowEvent e) {
		if (e.getSource() == this) {
			this.setVisible(false);
			if ( this.helper != null ) {
				try {
					this.helper.close();
				} catch (Exception ex) {
					logger.warn( "Error closing redis client : "+ex, ex );
				}
			}
			System.exit(0);
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.windowClosed(e);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// this method is not needed in this implementation
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// this method is not needed in this implementation
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// this method is not needed in this implementation
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// this method is not needed in this implementation
	}

}
