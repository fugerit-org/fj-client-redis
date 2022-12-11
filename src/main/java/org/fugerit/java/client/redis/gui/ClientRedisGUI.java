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

import org.fugerit.java.client.redis.ClientRedisArgs;
import org.fugerit.java.core.lang.helpers.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class ClientRedisGUI extends JFrame implements WindowListener, ActionListener {

	private final static Logger logger = LoggerFactory.getLogger(ClientRedisGUI.class);

	private Component setUI( Component c ) {
		return c;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7032775848800955092L;

	private JTextArea outputArea;
	private JTextArea urlArea;
	private JTextArea dbArea;
	private JTextArea collectionArea;
	private JTextArea queryArea;

	private JButton queryButton;

	public ClientRedisGUI(Properties params) {
		super("MongoDB Client");

		// crea layout
		BorderLayout mainLayout = new BorderLayout();
		this.setLayout(mainLayout);

		this.outputArea = (JTextArea) setUI(  new JTextArea("Result area...") );
		JScrollPane outputScroll = new JScrollPane(this.outputArea);

		JPanel controlPanel = (JPanel) setUI( new JPanel(new GridLayout( 4, 2, 2, 2 )) );
		this.urlArea = new JTextArea(params.getProperty(ClientRedisArgs.ARG_REDIS_URL, "URL"));
		this.dbArea = new JTextArea(params.getProperty(ClientRedisArgs.ARG_REDIS_URL, "URL"));
		this.collectionArea = new JTextArea(params.getProperty(ClientRedisArgs.ARG_REDIS_URL, "URL"));
		this.queryArea = new JTextArea(params.getProperty(ClientRedisArgs.ARG_REDIS_URL, "URL"));
		controlPanel.add( setUI( new JLabel("URL:", JLabel.RIGHT ) ) );
		controlPanel.add( setUI( this.urlArea ) );
		controlPanel.add( setUI(  new JLabel("DB:", JLabel.RIGHT ) ) );
		controlPanel.add( setUI( this.dbArea ) );
		controlPanel.add( setUI( new JLabel("Collection:", JLabel.RIGHT) ) );
		controlPanel.add( setUI( this.collectionArea ) );
		controlPanel.add( setUI( new JLabel("Query:", JLabel.RIGHT) ) );
		controlPanel.add( setUI( this.queryArea ) );

		this.queryButton = new JButton("Esegui");

		this.add( this.setUI( this.queryButton ), BorderLayout.SOUTH );
		this.add(controlPanel, BorderLayout.NORTH);
		this.add( outputScroll, BorderLayout.CENTER);

		this.queryButton.addActionListener(this);
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

	private void eseguiQuery() {
		this.resetOutput("Execute query...");
		try {
			String mongourl = this.urlArea.getText();
			String dbName = this.dbArea.getText();
			String collectionName = this.collectionArea.getText();
			String query = this.queryArea.getText();
			if (StringUtils.isEmpty(mongourl) 
					|| StringUtils.isEmpty(dbName) 
					|| StringUtils.isEmpty(collectionName)
					|| StringUtils.isEmpty(query)) {
				this.outputLine("Some parameters missing...");
			} else {
				RedisClient redisClient = RedisClient.create( RedisURI.create(mongourl) ) ;
				StatefulRedisConnection<String, String> connection = redisClient.connect();
		            RedisCommands<String, String> commands = connection.sync();            
		            String value = commands.get( query );   
		            System.out.println("Read value : "+value);
		            connection.close();
		            redisClient.shutdown();
		            this.outputLine("Result : " + value);
			}

		} catch (Exception e) {
			logger.error("Errore : " + e, e);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			e.printStackTrace(ps);
			this.outputLine(new String(baos.toByteArray()));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.queryButton) {
			this.eseguiQuery();
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
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
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}
