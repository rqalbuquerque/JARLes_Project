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
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


//Primeira atividade a ser chamada quando o app é iniciado
public class MainActivity extends ActionBarActivity {

	
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	//Método de criação da atividade, 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//bundle -> usado para passar valores entre atividades
		super.onCreate(savedInstanceState);
		//Seta o layout da primeira tela
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	    
	//Ao clicar em "new grid" este método é chamado
	public void draw_new_grid(View view){
		Intent i = new Intent(this, GridActivity.class);
		startActivity(i);
	}
	
	//Ao clicar em "new grid" este método é chamado
	public void load_grid(View view){
		Intent i = new Intent(this, LoadActivity.class);
		startActivity(i);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
