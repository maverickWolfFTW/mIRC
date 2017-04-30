
import javax.swing.*;

import java.awt.*;

import java.awt.event.*;

public class ServerGUI extends JFrame implements ActionListener, WindowListener {

	private static final long serialVersionUID = 1L;
	private JButton stopStart;
	private JTextArea chat, event;
	private JTextField nrPort;
	private Server server;

	ServerGUI(int port) {

		super("Chat Server");
		server = null;
		JPanel north = new JPanel();
		north.add(new JLabel("Numar Port: "));
		nrPort = new JTextField("  " + port);
		north.add(nrPort);
		stopStart = new JButton("Start");
		stopStart.addActionListener(this);
		north.add(stopStart);
		add(north, BorderLayout.NORTH);
		JPanel center = new JPanel(new GridLayout(2,1));
		chat = new JTextArea(80,50);
		chat.setEditable(false);
		appendRoom("Chat room.\n");
		center.add(new JScrollPane(chat));
		event = new JTextArea(80,50);
		event.setEditable(false);
				
		appendEvent("Loguri.\n");
		center.add(new JScrollPane(event));
		add(center);
		addWindowListener(this);
		setSize(600, 600);
		setVisible(true);
	}      

	void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 10);
	}

	void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(chat.getText().length() - 10);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if(server != null) {

			server.stop();
			server = null;
			nrPort.setEditable(true);
			stopStart.setText("Start");

			return;
		}

		int port;

		try {
			port = Integer.parseInt(nrPort.getText().trim());
		}

		catch(Exception er) {
			appendEvent("Port invalid");

			return;
		}

		server = new Server(port, this);
		new ServerRunning().start();
		stopStart.setText("Stop");
		nrPort.setEditable(false);
	}

	public static void main(String[] arg) {
		new ServerGUI(1500);
	}

	public void windowClosing(WindowEvent e) {	
		
		
		if(server != null) {
			
			try {
				server.stop();
			}

			catch(Exception eClose) {

			}

			server = null;
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

	class ServerRunning extends Thread {

		public void run() {

			server.start();
			stopStart.setText("Start");
			nrPort.setEditable(true);
			appendEvent("A picat serverul\n");
			server = null;
		}
	}
}
