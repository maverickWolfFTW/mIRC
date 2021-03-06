import java.io.*;

import java.net.*;

import java.text.SimpleDateFormat;

import java.util.*;

public class Server {

	private static int idUtilizator;
	private ArrayList<ClientThread> listaClienti;
	private static ServerGUI serverGui;
	private SimpleDateFormat data;
	private int port;
	private boolean functioneaza;

	public Server(int port, ServerGUI serverGui) {
		Server.setServerGui(serverGui);
		this.port = port;
		data = new SimpleDateFormat("HH:mm:ss");
		listaClienti = new ArrayList<ClientThread>();
	}

	public void start() {

		functioneaza = true;

		try {
			ServerSocket serverSocket = new ServerSocket(port);

			while (functioneaza) {

				display("Se asteapta clienti e portul: " + port + ".");
				Socket socket = serverSocket.accept();

				if (!functioneaza)
					break;

				ClientThread thread = new ClientThread(socket);
				listaClienti.add(thread);
				thread.start();
			}

			try {

				serverSocket.close();

				for (int i = 0; i < listaClienti.size(); ++i) {

					ClientThread threadClient = listaClienti.get(i);

					try {
						threadClient.sInput.close();
						threadClient.sOutput.close();
						threadClient.socket.close();
					}

					catch (IOException ioE) {

					}
				}
			}

			catch (Exception e) {
				display("Clientul si Serverul nu au fost inchisi corect: " + e);
			}
		}

		catch (IOException e) {
			String mesaj = data.format(new Date()) + " Exceptie ServerSocket: " + e + "\n";
			display(mesaj);
		}
	}

	@SuppressWarnings("resource")
	protected void stop() {

		functioneaza = false;

		try {
			new Socket("localhost", port);
		}

		catch (Exception e) {

		}

	}

	private void display(String mesaj) {

		String timp = data.format(new Date()) + " " + mesaj;

		if (getServerGui() == null)
			System.out.println(timp);

		else
			getServerGui().appendEvent(timp + "\n");
	}

	synchronized void remove(int id) {

		for (int i = 0; i < listaClienti.size(); ++i) {
			ClientThread clientThread = listaClienti.get(i);

			if (clientThread.id == id) {
				listaClienti.remove(i);

				return;
			}
		}
	}

	private synchronized void broadcast(String room, String mesaj) {

		String timp = data.format(new Date());
		String mesajData = timp + " " + mesaj + "\n";

		System.out.println(listaClienti);

		if (getServerGui() == null)
			System.out.print(mesajData);
		else
			getServerGui().appendRoom(mesajData);

		for (int i = listaClienti.size(); --i >= 0;) {
			ClientThread clientThread = listaClienti.get(i);

			if (clientThread.room.equals(room))
				if (!clientThread.writeMsg(mesajData)) {
					listaClienti.remove(i);
					display("Client deconectat " + clientThread.username + " sters din lista.");
				}
		}
	}

	public static void main(String[] args) {

		int numarPort = 1500;

		switch (args.length) {
		case 1:
			try {
				numarPort = Integer.parseInt(args[0]);
			}

			catch (Exception e) {

				System.out.println("Port invalid.");

				return;
			}

		case 0:
			break;

		default:
			return;
		}

		Server server = new Server(numarPort, getServerGui());
		server.start();
	}

	public static ServerGUI getServerGui() {
		return serverGui;
	}

	public static void setServerGui(ServerGUI serverGui) {
		Server.serverGui = serverGui;
	}

	class ClientThread extends Thread {

		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id;
		String username;
		String room;
		String date;

		@Override
		public String toString() {
			return "ClientThread [socket=" + socket + ", id=" + id + ", username=" + username + ", room=" + room + "]";
		}

		ClientThread(Socket socket) {

			id = ++idUtilizator;
			this.socket = socket;

			try {
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput = new ObjectInputStream(socket.getInputStream());

				Chat chat = (Chat) sInput.readObject();
				System.out.println(chat);

				// String mesaj = chat.getMesaj();
				room = chat.getRoom();
				username = chat.getUser();
				// username = (String) sInput.readObject();

				display(username + " s-a conectat.");
			}

			catch (IOException e) {
				display("Exceptie la creare: " + e);

				return;
			}

			catch (ClassNotFoundException e) {

			}

			date = new Date().toString() + "\n";
		}

		public void run() {

			boolean functioneaza = true;

			Chat cm;
			while (functioneaza) {

				try {
					cm = (Chat) sInput.readObject();
					System.out.println(cm);
				}

				catch (IOException e) {
					display(username + " Exceptie la citire: " + e);

					break;
				}

				catch (ClassNotFoundException e2) {

					break;
				}

				switch (cm.getTip()) {

				case Chat.MESSAGE:

					broadcast(room, username + ": " + cm.getMesaj());

					// for (int i = 0; i < listaClienti.size(); ++i) {
					// ClientThread ct = listaClienti.get(i);
					// if (room.equals(ct.room))
					// writeMsg(username + ": " + mesaj);
					// }

					break;

				case Chat.CREATE:

					System.out.println("CERATE !!!!!");
					System.out.println(cm);

					break;

				case Chat.CHG_ROOM:

					room = cm.getRoom();
					username = cm.getUser();

					break;

				case Chat.LOGOUT:

					display(username + " deconectat prin LOGOUT.");
					functioneaza = false;

					break;

				case Chat.WHOISIN:

					writeMsg("Utilizatorul conectat la: " + data.format(new Date()) + "\n");

					int nr = 1;
					for (int i = 0; i < listaClienti.size(); ++i) {
						ClientThread ct = listaClienti.get(i);
						if (room.equals(ct.room))
							writeMsg(nr++ + ") " + ct.username + " de la " + ct.date);

					}

					break;

				}
			}

			remove(id);
			close();
		}

		private void close() {

			try {

				if (sOutput != null)
					sOutput.close();
			}

			catch (Exception e) {
			}

			try {
				if (sInput != null)
					sInput.close();
			}

			catch (Exception e) {
			}
			;

			try {
				if (socket != null)
					socket.close();
			}

			catch (Exception e) {
			}
		}

		private boolean writeMsg(String msg) {

			if (!socket.isConnected()) {

				close();

				return false;
			}

			try {

				sOutput.writeObject(new Chat(Chat.MESSAGE, room, username, msg));
			}

			catch (IOException e) {

				display("Eroare la trimitere mesaj catre: " + username);
				display(e.toString());
				e.printStackTrace();
			}

			return true;
		}
	}
}
