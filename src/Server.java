import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

//Server
public class Server
{
	private ServerGUI gui;
	private int port;
	private static int connectionID;
	private boolean running;
	private SimpleDateFormat dateFormat;
	private ArrayList<ClientThread> clientArray;
	
	public Server(int port, ServerGUI gui)
	{
		this.gui = gui;
		this.port = port;
		dateFormat = new SimpleDateFormat();
		clientArray = new ArrayList<ClientThread>();
	}
	
	public void start()
	{
		running = true;
		try 
		{
			ServerSocket serverSocket = new ServerSocket(port);

			while(running) 
			{
				display("Waiting for connection on port " + port + ".");
				Socket socket = serverSocket.accept();  	
				if(!running)
				{
					break;
				}
				ClientThread t = new ClientThread(socket);
				clientArray.add(t);									
				t.start();
			}
			try 
			{
				serverSocket.close();
				for(int i = 0; i < clientArray.size(); ++i) 
				{
					ClientThread c = clientArray.get(i);
					try {
					c.streamIn.close();
					c.streamOut.close();
					c.socket.close();
					}
					catch(IOException e) {}
				}
			}
			catch(Exception e)
			{
			}
		}
		catch (IOException e) 
		{
		} 
	}
	
	protected void stop()
	{
		running = false;
		display("Server stopped");
		try
		{
			new Socket("localhost", port);
		}
		catch(Exception e){}
	}
	
	private void display(String msg)
	{
		String message = "[" + dateFormat.format(new Date()) + "]: " + msg;
		if(gui != null)
		{
			gui.addEvent(message + "\n");
		}
		
	}
	
	private synchronized void broadcast(String msg)
	{
		String time = "[" + dateFormat.format(new Date());
		String message = time + "]: " + msg + "\n";
		if(gui != null)
		{
			gui.addEvent(message);
		}
		for(int i = clientArray.size(); --i >= 0;)
		{
			ClientThread clientThread = clientArray.get(i);
			if(!clientThread.writeMessage(message))
			{
				clientArray.remove(i);
				display(clientThread.username + " removed from list of users");
			}
		}
	}
	
	synchronized void remove(int id)
	{
		for(int i = 0; i < clientArray.size(); i++)
		{
			ClientThread t = clientArray.get(i);
			if(t.clientID == id)
			{
				clientArray.remove(i);
				return;
			}
		}
	}
	
	public static void main(String[] args)
	{
		int port = 1234;
		Server server = new Server(port, null);
		server.start();
	}

	
	class ClientThread extends Thread 
	{
		Socket socket;
		ObjectInputStream streamIn;
		ObjectOutputStream streamOut;
		int clientID;
		String username;
		ChatMessage chatMessage;
		String date;

		ClientThread(Socket socket) 
		{
			clientID = ++connectionID;
			this.socket = socket;
			try
			{
				streamOut = new ObjectOutputStream(socket.getOutputStream());
				streamIn  = new ObjectInputStream(socket.getInputStream());
				username = (String) streamIn.readObject();
				display(username + " has connected to the chatroom");
			}
			catch (IOException e) 
			{
				return;
			}
			catch (ClassNotFoundException e) 
			{
			}
            date = new Date().toString() + "\n";
		}

		public void run() 
		{
			boolean running = true;
			while(running) 
			{
				try 
				{
					chatMessage = (ChatMessage) streamIn.readObject();
				}
				catch (IOException e) 
				{
					break;				
				}
				catch(ClassNotFoundException e) 
				{
					break;
				}
				String message = chatMessage.getMessage();

				switch(chatMessage.getType()) 
				{

				case ChatMessage.MESSAGE:
					broadcast(username + ": " + message);
					break;
				case ChatMessage.LOGOUT:
					display(username + " has disconnected");
					running = false;
					break;
				case ChatMessage.WHO:
					writeMessage("List of the users currently connected " + "\n");
					for(int i = 0; i < clientArray.size(); ++i) 
					{
						ClientThread clientThread = clientArray.get(i);
						writeMessage((i+1) + ") " + clientThread.username + "\n");
					}
					break;
				}
			}
			remove(clientID);
			close();
		}
		
		private void close() 
		{
			try 
			{
				if(streamOut != null) streamOut.close();
			}
			catch(Exception e) 
			{
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
			}
			try 
			{
				if(socket != null)
				{
					socket.close();
				}
			}
			catch (Exception e) {}
		}

		private boolean writeMessage(String msg) {
			if(!socket.isConnected()) 
			{
				close();
				return false;
			}
			try 
			{
				streamOut.writeObject(msg);
			}
			catch(IOException e) 
			{
				display("Error sending message");
			}
			return true;
		}
	}
}