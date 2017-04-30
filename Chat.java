import java.io.*;

@SuppressWarnings("serial")
public class Chat implements Serializable {

	static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
	private int tip;
	private String mesaj;

	Chat(int tip, String mesaj) {
		this.tip = tip;
		this.mesaj = mesaj;
	}

	int getTip() {
		return tip;
	}

	String getMesaj() {
		return mesaj;
	}
}
