package br.ufpe.cin.jarlesproject;


import java.io.InputStream;

import android.bluetooth.BluetoothSocket;

public class ThreadReceive extends Thread {
	private InputStream inStream = null;
	private BluetoothSocket mBtSocket  = null;
	private String mStrRec = "";
	private String aux = "";
	byte[] strRotaBuffer = new byte[1000];
	BufferMessage mBuffer = new BufferMessage();

	public ThreadReceive(BluetoothSocket mBtSocket, BufferMessage mBuffer){
		this.mBtSocket = mBtSocket;
		this.mBuffer = mBuffer;

		if(mBtSocket != null){
			try {
				inStream = mBtSocket.getInputStream();
			} catch (Exception e) {
				System.out.println( "Exception(Receive): " + e);
			}
		}
		else{
			System.out.println("Bluetooth nao conetado!");
		}
	}

	@Override
	public void run() {
		while(true){
			try {
				char b = ((char) inStream.read());
				System.out.println("Char: " + b);
				aux = aux + b;				
				
				//inStream.read(strRotaBuffer);
				
				//mStrRec.concat(strRotaBuffer.toString());
				mBuffer.insertMess(aux);
				
				if(b == '$')
					break;
				
				//System.out.println("AQUI " + (char) inStream.read());
			} catch (Exception e) {
				//System.out.println("Exception Thread: " + e);
			}
		}
	}
}