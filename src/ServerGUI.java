import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ServerGUI extends JFrame implements ActionListener, WindowListener
{
	private static final long serialVersionUID = 1L;
	private JButton storp;
	private JTextArea chatroom, event;
	private JTextField portField;
	private Server server;
	
	ServerGUI(int port)
	{
		super("Chat Server");
		server = null;
		
		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel("Port: "));
		portField = new JTextField(" " + port);
		topPanel.add(portField);
		
		storp = new JButton("Start");
		storp.addActionListener(this);
		topPanel.add(storp);
		add(topPanel, BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(new GridLayout(2, 1));
		chatroom = new JTextArea(128, 128);
		chatroom.setEditable(false);
		addRoom("Chat room. \n");
		centerPanel.add(new JScrollPane(chatroom));
		event = new JTextArea(56, 56);
		event.setEditable(false);
		addEvent("Event log \n");
		centerPanel.add(new JScrollPane(event));
		add(centerPanel);
		
		addWindowListener(this);
		setSize(800, 600);
		setVisible(true);
	}
	void addRoom(String s)
	{
		chatroom.append(s);
		chatroom.setCaretPosition(chatroom.getText().length() - 1);
	}
	void addEvent(String s)
	{
		event.append(s);
		event.setCaretPosition(chatroom.getText().length() - 1);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(server != null)
		{
			server.stop();
			server = null;
			portField.setEditable(true);
			storp.setText("Start");
			return;
		}
		int port;
		try 
		{
			port = Integer.parseInt(portField.getText().trim());
		}
		catch(Exception ex)
		{
			addEvent("Invalid port ");
			return;
		}
		
		server = new Server(port, this);
		new ServerThread().start();
		storp.setText("Stop");
		portField.setEditable(false);	
	}
	public void windowClosing(WindowEvent e)
	{
		if(server != null)
		{
			try
			{
				server.stop();
			}
			catch(Exception ex)
			{
				server = null;
			}
		}
		dispose();
		System.exit(0);
	}
	
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	
	public static void main(String[] args)
	{
		new ServerGUI(1234);
	}
	
	class ServerThread extends Thread
	{
		public void run()
		{
			server.start();
		}
	}
}
