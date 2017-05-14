
import javax.swing.*;

import java.awt.*;

import java.awt.event.*;

public class ServerGUI extends JFrame implements ActionListener, WindowListener {

	private static final long serialVersionUID = 1L;
	private JButton statusButton = new JButton("Start");
	private JTextArea usersOutput = new JTextArea(80, 50);
	private JTextArea logsOutput = new JTextArea(80, 50);
	private JTextField portTextField;
	private Server server = null;

	ServerGUI(int port) {
		super("Chat Server");

		JPanel north = new JPanel();
		JPanel center = new JPanel(new GridLayout(2, 1));

		north.add(new JLabel("Numar Port: "));
		portTextField = new JTextField("  " + port);
		north.add(portTextField);
		statusButton.addActionListener(this);
		north.add(statusButton);

		usersOutput.setEditable(false);
		appendRoom("Chat room.\n");
		center.add(new JScrollPane(usersOutput));

		logsOutput.setEditable(false);
		appendEvent("Loguri.\n");
		center.add(new JScrollPane(logsOutput));

		add(north, BorderLayout.NORTH);
		add(center);
		addWindowListener(this);
		setSize(600, 600);
		setVisible(true);
	}

	void appendRoom(String str) {
		usersOutput.append(str);
		usersOutput.setCaretPosition(usersOutput.getText().length() - 10);
	}

	void appendEvent(String str) {
		logsOutput.append(str);
		logsOutput.setCaretPosition(usersOutput.getText().length() - 10);
	}

	public void actionPerformed(ActionEvent e) {

		if (server != null) {

			server.stop();
			server = null;
			portTextField.setEditable(true);
			statusButton.setText("Start");

			return;
		}

		int port;

		try {
			port = Integer.parseInt(portTextField.getText().trim());
		}

		catch (Exception er) {
			appendEvent("Port invalid");

			return;
		}

		server = new Server(port, this);
		new ServerRunning().start();
		statusButton.setText("Stop");
		portTextField.setEditable(false);
	}

	public void windowClosing(WindowEvent e) {

		if (server != null) {

			try {
				server.stop();
			}

			catch (Exception eClose) {

			}

			server = null;
		}

		dispose();
		System.exit(0);
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	class ServerRunning extends Thread {

		public void run() {

			server.start();
			statusButton.setText("Start");
			portTextField.setEditable(true);
			appendEvent("A picat serverul\n");
			server = null;
		}
	}

	public static void main(String[] arg) {
		new ServerGUI(4200);
	}
}
