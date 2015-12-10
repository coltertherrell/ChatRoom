import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

//ClientGUI
public class ClientGUI extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JTextField textField, addressField, portField;
	private JButton login, logout, who;
	private JTextArea chatroom;
	private boolean connection;
	private Client client;
	private int portNumber;
	private String hostAddress;
	
	ClientGUI(String host, int port)
	{
		super("Chatroom");
		portNumber = port;
		hostAddress = host;
		
		JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
		JPanel serverPort = new JPanel(new GridLayout(1, 5, 1, 3));
		addressField = new JTextField(host);
		portField = new JTextField("" + port);
		portField.setHorizontalAlignment(SwingConstants.LEFT);
		
		serverPort.add(new JLabel("Address: "));
		serverPort.add(addressField);
		serverPort.add(new JLabel("Port: "));
		serverPort.add(portField);
		serverPort.add(new JLabel(""));
		bottomPanel.add(serverPort);
		
		textField = new JTextField("Username");
		bottomPanel.add(textField);
		add(bottomPanel, BorderLayout.SOUTH);
		
		chatroom = new JTextArea("Chatroom" + "\n", 120, 120);
		JPanel middlePanel = new JPanel(new GridLayout(1,1));
		middlePanel.add(new JScrollPane(chatroom));
		chatroom.setEditable(false);
		add(middlePanel, BorderLayout.CENTER);
		
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);
		who = new JButton("Who");
		who.addActionListener(this);
		who.setEnabled(false); 
		
		JPanel topPanel = new JPanel();
		topPanel.add(login);
		topPanel.add(logout);
		topPanel.add(who);
		add(topPanel, BorderLayout.NORTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
		setVisible(true);
	}
	void addToTextArea(String s)
	{
		chatroom.append(s);
		chatroom.setCaretPosition(chatroom.getText().length() - 1);
	}
	void fail()
	{
		login.setEnabled(true);
		logout.setEnabled(true);
		who.setEnabled(true);
		textField.setText("Username");
		portField.setText("" + portNumber);
		addressField.setText(hostAddress);
		textField.removeActionListener(this);
		connection = false;
	}
	public void actionPerformed(ActionEvent event)
	{
		Object object = event.getSource();
		if(object == logout)
		{
			client.send(new ChatMessage(ChatMessage.LOGOUT, ""));
			return;
		}
		if(object == who)
		{
			client.send(new ChatMessage(ChatMessage.WHO, ""));
			return;
		}
		if(connection)
		{
			client.send(new ChatMessage(ChatMessage.MESSAGE, textField.getText()));
			textField.setText("");
			return;
		}
		if(object == login)
		{
			String username = textField.getText().trim();
			String server = addressField.getText().trim();
			String portNum = portField.getText().trim();
			int port = 0;
			try
			{
				port = Integer.parseInt(portNum);
			}
			catch(Exception e)
			{
				return;
			}
			
			client = new Client(server, port, username, this);
			if(!client.start())
			{
				return;
			}
			textField.setText("");
			connection = true;
			login.setEnabled(false);
			logout.setEnabled(true);
			who.setEnabled(true);
			addressField.setEditable(false);
			portField.setEditable(false);
			textField.addActionListener(this);
		}
	}
	public static void main(String[] args)
	{
		new ClientGUI("localhost", 1234);
	}
}
