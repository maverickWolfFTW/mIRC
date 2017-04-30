
import javax.swing.*;

import java.awt.*;

import java.awt.event.*;

public class ClientGUI extends JFrame implements ActionListener {


	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JTextField tf;
	private JTextField tfServer, tfPort;
	private JButton login, logout, whoIsIn;
	private JTextArea text;
	private boolean connected;
	private Client client;
	private int defaultPort;
	private String defaultHost;

	ClientGUI(String host, int port) {

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		
		
		
		JPanel northPanel = new JPanel(new GridLayout(3, 1));
		JPanel serverAndPort = new JPanel(new GridLayout(1, 5, 1, 3));
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		
		serverAndPort.add(new JLabel("Adresa Server:  "));
		serverAndPort.add(tfServer);
		
		serverAndPort.add(new JLabel("Portul:  "));
		serverAndPort.add(tfPort);
		
		serverAndPort.add(new JLabel(""));
		northPanel.add(serverAndPort);
		
		add(northPanel, BorderLayout.NORTH);
		
		text = new JTextArea("Bun venit in camera.\n", 80, 50);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(text));
		text.setEditable(false);
		
		add(centerPanel, BorderLayout.CENTER);
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);
		whoIsIn = new JButton("Who is in");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false); 

		
		JPanel southPanel = new JPanel();
		
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.LINE_AXIS));
		
		label = new JLabel("Username: ", SwingConstants.LEFT);
		southPanel.add(label);
		tf = new JTextField("Anonim");
		tf.setBackground(Color.WHITE);
		southPanel.add(tf);
		
		southPanel.add(login);
		southPanel.add(logout);
		
		add(southPanel, BorderLayout.SOUTH);
		
		JPanel eastPanel = new JPanel();
		
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.LINE_AXIS));
		
		
		label = new JLabel("Comenzi: ", SwingConstants.LEFT);


		eastPanel.add(whoIsIn);
		
		add(eastPanel, BorderLayout.EAST);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tf.requestFocus();
	}

	void append(String str) {

		text.append(str);
		text.setCaretPosition(text.getText().length() - 1);
	}

	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		label.setText("Username");
		tf.setText("Anonim");
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		tf.removeActionListener(this);
		connected = false;
	}

	public void actionPerformed(ActionEvent e) {

		Object o = e.getSource();

		if(o == logout) {
			client.sendMessage(new Chat(Chat.LOGOUT, ""));
			
			return;
		}

		if(o == whoIsIn) {
			client.sendMessage(new Chat(Chat.WHOISIN, ""));              

			return;
		}

		if(connected) {
			client.sendMessage(new Chat(Chat.MESSAGE, tf.getText()));            
			tf.setText("");
			
			return;
		}

		if(o == login) {
			String username = tf.getText().trim();
			
			
			if(username.length() == 0)
				return;

			String server = tfServer.getText().trim();
			
			if(server.length() == 0)
				return;

			String portNumber = tfPort.getText().trim();
			
			if(portNumber.length() == 0)
				return;

			int port = 0;

			try {
				port = Integer.parseInt(portNumber);
			}

			catch(Exception en) {
				return;  
			}

			client = new Client(server, port, username, this);

			if(!client.start())
				return;

			tf.setText("");

			label.setText("Scrie mesaj: ");
			label.setFont(new Font("Arial", 2, 15));

			connected = true;
			login.setEnabled(false);
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			tf.addActionListener(this);
		}
	}

	public static void main(String[] args) {
		
		new ClientGUI("localhost", 1500);
	}
}
