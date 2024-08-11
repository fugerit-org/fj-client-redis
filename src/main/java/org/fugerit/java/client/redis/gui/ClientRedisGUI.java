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
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.fugerit.java.client.redis.ClientRedisArgs;
import org.fugerit.java.client.redis.ClientRedisFun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRedisGUI extends JFrame implements WindowListener, ActionListener {

	public static final String VERSION = "1.2.1";
	
	private static final Logger logger = LoggerFactory.getLogger(ClientRedisGUI.class);

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
	private JButton infoButton;

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

		JPanel actionPanel = (JPanel) setUI( new JPanel(new GridLayout( 1, 6, 0, 0 )) );
		this.getButton = new JButton( "Get" );
		this.setButton = new JButton( "Set" );
		this.delButton = new JButton( "Del" );
		this.listKeysButton = new JButton( "List keys" );
		this.listAllButton = new JButton( "List values" );
		this.infoButton = new JButton( "Server info" );
		actionPanel.add( this.setUI( this.getButton ) );
		actionPanel.add( this.setUI( this.setButton ) );
		actionPanel.add( this.setUI( this.delButton ) );
		actionPanel.add( this.setUI( this.listKeysButton ) );
		actionPanel.add( this.setUI( this.listAllButton ) );
		actionPanel.add( this.setUI( this.infoButton ) );

		this.add( this.setUI( actionPanel ), BorderLayout.SOUTH );
		this.add( controlPanel, BorderLayout.NORTH);
		this.add( outputScroll, BorderLayout.CENTER);

		this.getButton.addActionListener(this);
		this.setButton.addActionListener(this);
		this.delButton.addActionListener(this);
		this.listKeysButton.addActionListener(this);
		this.listAllButton.addActionListener(this);
		this.infoButton.addActionListener( this );
		this.addWindowListener(this);

		if ( params.containsKey( ClientRedisArgs.ARG_REDIS_URL ) ) {
			this.urlArea.setText( params.getProperty( ClientRedisArgs.ARG_REDIS_URL ) );
		}

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
	
	private ClientRedisFun getHelper() {
		ClientRedisGUI gui = this;
		return new ClientRedisFun( this.urlArea.getText() ) {
			@Override
			public String getKey() {
				return keyArea.getText();
			}

			@Override
			public String getValue() {
				return valueArea.getText();
			}

			@Override
			public String getTTL() {
				return ttlArea.getText();
			}

			@Override
			public void outputLine(String line) {
				gui.outputLine( line );
			}

			@Override
			public void error(String baseMessage, Exception e) {
				gui.handleError( baseMessage, e );
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() instanceof JButton ) {
			this.resetOutput( String.format( "Running command : %s", ((JButton)e.getSource()).getText() ) );
		}
		if (e.getSource() == this.getButton) {
			this.getHelper().get();
		} else if (e.getSource() == this.setButton) {
			this.getHelper().set();
		} else if (e.getSource() == this.delButton) {
			this.getHelper().del();
		} else if (e.getSource() == this.listKeysButton) {
			this.getHelper().listKeys();
		} else if (e.getSource() == this.listAllButton) {
			this.getHelper().listAll();
		} else if (e.getSource() == this.infoButton) {
			this.getHelper().info();
		} else {
			this.resetOutput( "No command selected" );
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
