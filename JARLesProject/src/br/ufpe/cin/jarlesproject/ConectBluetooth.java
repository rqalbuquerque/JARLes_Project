package br.ufpe.cin.jarlesproject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
 
public class ConectBluetooth extends ActionBarActivity {
	 
    private static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
   
    Button btSend, btRec, btConect, btDesconect;
    private String mStrRec;
    private String mStrSendç;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mPairedDevices; 
    private BluetoothSocket mBtSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
   
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
}
   


   





//Envia dados    
public void sendData(String strRota){
    if(mBtSocket != null){
            try {
                    outStream = mBtSocket.getOutputStream();
            } catch (Exception e) {
                    System.out.println("Exception(Send): " + e);
            }
           
            byte[] strRotaBuffer = strRota.getBytes();
            try {
    				Toast.makeText(this.getBaseContext(), "Rota Enviada", Toast.LENGTH_SHORT).show();
                    outStream.write(strRotaBuffer);
            } catch (Exception e) {
                    System.out.println("Exception(Send): " + e);
            }
    }
    else{
		Toast.makeText(this.getBaseContext(), "Bluetooth nao conetado!", Toast.LENGTH_SHORT).show();
	}
}
 






@Override
public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
        return true;
    }
    return super.onOptionsItemSelected(item);
}
}