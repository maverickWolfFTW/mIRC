import java.io.*;

@SuppressWarnings("serial")
public class Chat implements Serializable {

	static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2, CREATE = 3, CHG_ROOM = 4;
	private int tip;
	private String mesaj;
	private String room;
	private String user;

	Chat(int tip, String room, String user, String mesaj) {
		this.tip = tip;
		this.mesaj = mesaj;
		this.room = room;
		this.user = user;
	}

	int getTip() {
		return tip;
	}

	public String getUser() {
		return user;
	}

	String getMesaj() {
		return mesaj;
	}

	String getRoom() {
		return room;
	}

	@Override
	public String toString() {
		return "Chat [tip=" + tip + ", mesaj=" + mesaj + ", room=" + room + ", user=" + user + "]";
	}

}
