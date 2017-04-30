import java.net.*;

import java.io.*;

import java.util.*;


public class Client  {

    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;
    private ClientGUI clientGui;
    private String server, username;
    private int port;

    Client(String server, int port, String username) {
        this(server, port, username, null);
    }

    Client(String server, int port, String username, ClientGUI clientGui) {

        this.server = server;
        this.port = port;
        this.username = username;
        this.clientGui = clientGui;
    }

    public boolean start() {

        try {
            socket = new Socket(server, port);
        }

        catch(Exception ec) {
            display("Eroare la conexiunea cu serverul: " + ec);
            
            return false;
        }

        String mesaj = "Conexiune acceptata " + socket.getInetAddress() + ":" + socket.getPort();
        display(mesaj);

        try {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }

        catch (IOException eIO) {
            display("Exceptie la creare: " + eIO);

            return false;
        }


        new ListenFromServer().start();

        try	{
            sOutput.writeObject(username);
        }

        catch (IOException eIO) {
            display("Exceptie la login : " + eIO);
            disconnect();

            return false;
        }

        return true;
    }


    private void display(String mesaj) {
    	
        if(clientGui == null)
            System.out.println(mesaj);

        else
            clientGui.append(mesaj + "\n");
    }

    void sendMessage(Chat mesaj) {
    	
        try {

            sOutput.writeObject(mesaj);
        }

        catch(IOException e) {

            display("Exceptie trimitere mesaj catre server: " + e);
        }
    }

    private void disconnect() {

        try {
        	
            if(sInput != null) 
            	sInput.close();
        }

        catch(Exception e) {}

        try {

            if(sOutput != null) sOutput.close();
        }

        catch(Exception e) {}

        try{
        	
            if(socket != null) 
            	socket.close();
        }

        catch(Exception e) {}

        if(clientGui != null)
            clientGui.connectionFailed();
    }

    public static void main(String[] args) {

        int portNumber = 1500;
        String serverAddress = "localhost";
        String userName = "Anonim";

        switch(args.length) {

            case 3:
                serverAddress = args[2];
                
            case 2:

                try {
                    portNumber = Integer.parseInt(args[1]);
                }

                catch(Exception e) {

                    System.out.println("Port invalid.");

                    return;
                }

            case 1:
                userName = args[0];
                
            case 0:
                break;
                
            default:

            return;
        }

        Client client = new Client(serverAddress, portNumber, userName);

        if(!client.start())
            return;

        @SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);

        while(true) {

            System.out.print(">>> ");
            String mesaj = scan.nextLine();
            
            if(mesaj.equalsIgnoreCase("LOGOUT")) {
            	
                client.sendMessage(new Chat(Chat.LOGOUT, ""));

                break;
            }

            else if(mesaj.equalsIgnoreCase("WHOISIN")) {

                client.sendMessage(new Chat(Chat.WHOISIN, ""));              
            }

            else { 

                client.sendMessage(new Chat(Chat.MESSAGE, mesaj));
            }
        }

        client.disconnect();   
    }

    class ListenFromServer extends Thread {

        public void run() {

            while(true) {

                try {
                    String mesaj = (String) sInput.readObject();

                    if(clientGui == null) {

                        System.out.println(mesaj);
                        System.out.print(">>> ");
                    }

                    else {

                        clientGui.append(mesaj);
                    }
                }

                catch(IOException e) {

                    display("Serverul a inchis conexiunea: " + e);

                    if(clientGui != null)
                        clientGui.connectionFailed();

                    break;
                }

                catch(ClassNotFoundException e2) {

                }
                
            }
       }
    }
}
