
import javax.swing.*;

import java.awt.*;

import java.awt.event.*;

public class ClientGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JLabel actionAbout = new JLabel("Nume: ", SwingConstants.LEFT);
	private JTextField actionData = new JTextField("Anonim");
	private JButton sendMessage = new JButton("Send");
	private JTextField serverInput;
	private JTextField portInput;
	private JButton logout = new JButton("Logout");
	private JButton whoIsIn = new JButton("List Users");
	private JTextArea chatOutput;
	private Client client;
	private int defaultPort;
	private String defaultHost;
	String currentRoom = "LOBBY";

	ClientGUI(String host, int port) {

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;

		JPanel northPanel = new JPanel(new GridLayout(3, 1));
		JPanel connectionData = new JPanel(new GridLayout(3, 5, 1, 3));
		serverInput = new JTextField(host);
		portInput = new JTextField("" + port);
		logout.addActionListener(logoutListener);
		logout.setEnabled(false);
		whoIsIn.addActionListener(whoistListener);
		whoIsIn.setEnabled(false);

		connectionData.add(new JLabel("Adresa Server:  "));
		connectionData.add(serverInput);
		connectionData.add(new JLabel("Portul:  "));
		connectionData.add(portInput);
		connectionData.add(new JLabel(""));
		connectionData.add(logout);
		connectionData.add(whoIsIn);
		connectionData.add(new JLabel(""));
		JButton r1 = new JButton("Room 1");
		r1.addActionListener(roomListener);
		connectionData.add(r1);
		JButton r2 = new JButton("Room 2");
		r2.addActionListener(roomListener);
		connectionData.add(r2);
		JButton r3 = new JButton("Room 3");
		r3.addActionListener(roomListener);
		connectionData.add(r3);
		JButton r4 = new JButton("Room 42");
		r4.addActionListener(roomListener);
		connectionData.add(r4);
		northPanel.add(connectionData);

		chatOutput = new JTextArea("Bun venit.\nAlege un nume si alatura-te unei camere.\n", 80, 50);
		JPanel centerPanel = new JPanel(new GridLayout(1, 1));
		centerPanel.add(new JScrollPane(chatOutput));
		chatOutput.setEditable(false);

		sendMessage.addActionListener(sendListener);
		actionData.setBackground(Color.WHITE);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.LINE_AXIS));
		southPanel.add(actionAbout);
		southPanel.add(actionData);
		southPanel.add(sendMessage);

		sendMessage.setEnabled(false);

		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		actionData.requestFocus();
	}

	void append(String str) {

		chatOutput.append(str);
		chatOutput.setCaretPosition(chatOutput.getText().length() - 1);
	}

	void connectionFailed() {
		sendMessage.setEnabled(false);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		actionAbout.setText("Nume: ");
		actionData.setText("Anonim");
		portInput.setText("" + defaultPort);
		serverInput.setText(defaultHost);
		serverInput.setEditable(false);
		portInput.setEditable(false);
		// actionData.removeActionListener(this);
		// connected = false;
	}

	ActionListener sendListener = e -> {
		client.sendMessage(actionData.getText());
		actionData.setText("");
	};

	ActionListener roomListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			currentRoom = e.getActionCommand();

			if (client != null) {
				String oldRoom = client.getRoom();
				client.setRoom(currentRoom);
				client.sendType(Chat.CHG_ROOM);
				client.sendMessage("" + client.getUser() + " went from room " + oldRoom + " to " + client.getRoom());
				return;
			}

			String username = actionData.getText().trim();
			if (username.length() == 0)
				return;

			String server = serverInput.getText().trim();

			if (server.length() == 0)
				return;

			String portNumber = portInput.getText().trim();

			if (portNumber.length() == 0)
				return;

			int port = 0;

			try {
				port = Integer.parseInt(portNumber);
			} catch (Exception en) {
				return;
			}

			client = new Client(server, port, username, currentRoom, ClientGUI.this);

			if (!client.start())
				return;

			actionData.setText("");
			actionAbout.setText("Mesaj: ");
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);
			serverInput.setEditable(false);
			portInput.setEditable(false);
			sendMessage.setEnabled(true);
			//
		}
	};

	ActionListener whoistListener = e -> {
		client.sendType(Chat.WHOISIN);

	};

	ActionListener logoutListener = e -> {
		client.sendType(Chat.LOGOUT);

		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		actionData.setText("Anonim");
		actionAbout.setText("Nume: ");

	};

	public static void main(String[] args) {

		new ClientGUI("localhost", 4200);
	}
}
