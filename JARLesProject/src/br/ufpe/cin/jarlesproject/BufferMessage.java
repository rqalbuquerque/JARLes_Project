package br.ufpe.cin.jarlesproject;

public class BufferMessage {
	private String mess = "";

	public BufferMessage(){
		mess = "";
	}

	public synchronized void clear(){
		mess = "";
	}
	
	public synchronized int getLenght(){
		return mess.length();
	}

	public synchronized void insertMess(String mess){
		this.mess = mess;
	}

	public synchronized String removeMess(){
		return mess;
	}
}