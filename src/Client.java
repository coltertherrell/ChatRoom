
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//Client
public class Client
{
	private static ClientGUI gui;
	private ObjectInputStream streamIn;
	private ObjectOutputStream streamOut;
	private Socket socket;
	
	private int port;
	private String server;
	private String username;
	
	Client(String server, int port, String username, ClientGUI gui)
	{
		this.server = server;
		this.username = username;
		this.port = port;
		Client.gui = gui;
	}
	
	public boolean start()
	{
		try
		{
			socket = new Socket(server, port);
		}
		catch(Exception e)
		{
			display("Error connecting");
			return false;
		}
		
		display("Connected to " + socket.getInetAddress() + ":" + socket.getPort());
		
		try
		{
			streamIn = new ObjectInputStream(socket.getInputStream());
			streamOut = new ObjectOutputStream(socket.getOutputStream());
		}
		catch(IOException ex)
		{
			display("Error creating IO streams: ");
			return false;
		}
		
		new ServerListener().start();
		
		try
		{
			streamOut.writeObject(username);
		}
		catch(IOException exc)
		{
			display("Login error");
			disconnect();
			return false;
		}
		return true;
	}
	private void display(String msg)
	{
		if(gui != null)
		{
			gui.addToTextArea(msg + "\n");
		}
	}
	
	void send(ChatMessage msg)
	{
		try
		{
			streamOut.writeObject(msg);
		}
		catch(IOException e)
		{
			display("Error sending message");
		}
	}
	
	void disconnect()
	{
		if(gui != null)
		{
			gui.fail();
		}
		try
		{
			if(streamIn != null)
			{
				streamIn.close();
			}
		}
		catch(Exception e)
		{
			display("Error closing input stream");
		}
		
		try
		{
			if(streamOut != null)
			{
				streamOut.close();
			}
		}
		catch(Exception e)
		{
			display("Error closing output stream");
		}
		try
		{
			if(socket != null)
			{
				socket.close();
			}
		}
		catch(Exception e)
		{
			display("Error closing socket");
		}
	}
	
	public static void main(String[] args) 
	{
		int port = 1234;
		String address = "localhost";
		String username = "Username";
		
		Client client = new Client(address, port, username, gui);
		
		client.disconnect();
	}
	
	class ServerListener extends Thread 
	{
		public void run()
		{
			while(true)
			{
				try
				{
					String message = (String) streamIn.readObject();
					if(gui != null)
					{
						gui.addToTextArea(message);
					}
				}
				catch(IOException e)
				{
					display("Connection closed");
					gui.fail();
					break;
				} 
				catch (ClassNotFoundException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
		
}
