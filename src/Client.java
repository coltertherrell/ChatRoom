/*
 * Author: Colter Therrell
 * 
 * Title: Client.java
 * 
 * Usage: Simple client with minimal GUI to be used in server/client model
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Client implements Runnable
{
	
	private static final int PORT = 1234;
	
	public static void main(String[] args) throws Exception 
	{
		JFrame clientMainFrame = new JFrame("Client");
		JPanel clientNorthPanel = new JPanel();
		JPanel clientSouthPanel = new JPanel();
		JPanel clientMiddlePanel = new JPanel();
		final JLabel clientStatus = new JLabel("Client not running");
		
		clientMainFrame.getContentPane().setLayout(new BorderLayout());
		
		clientMainFrame.setPreferredSize(new Dimension(500, 500));
		
		clientNorthPanel.setPreferredSize(new Dimension(150, 150));
		clientSouthPanel.setPreferredSize(new Dimension(150, 150));
		
		clientMainFrame.add(clientNorthPanel, BorderLayout.NORTH);
		clientMainFrame.add(clientSouthPanel, BorderLayout.SOUTH);
		clientMainFrame.add(clientMiddlePanel, BorderLayout.CENTER);
		
		JButton startButton = new JButton("Start Client");
		final JButton connectButton = new JButton("Connect to server");
		final JButton disconnectButton = new JButton("Disconnect");
		
		connectButton.setVisible(false);
		disconnectButton.setVisible(false);
		
		clientNorthPanel.add(startButton);
		clientSouthPanel.add(clientStatus);
		clientMiddlePanel.add(connectButton);
		clientMiddlePanel.add(disconnectButton);
		
		clientMainFrame.setVisible(true);
		clientMainFrame.pack();
		
		startButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{				
					System.out.println("Client running, press connect to server...");
					clientStatus.setText("Client running, press connect to server...");
					connectButton.setVisible(true);
					disconnectButton.setVisible(true);
					
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		
		connectButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					Socket clientSocket = new Socket(InetAddress.getLocalHost(), PORT);
					System.out.println("Client successfully connected!");
					clientStatus.setText("Client successfully connected!");
					if(clientSocket.isClosed()) //not working
					{
						System.out.println("Server has closed");
						clientStatus.setText("Server has closed");
					}
				} 
				catch (Exception ex) 
				{
					ex.printStackTrace();
				}
			}
		});
		
		disconnectButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					
				} 
				catch (Exception ex) 
				{
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public void run() {
		// TODO 
		
	}

}
