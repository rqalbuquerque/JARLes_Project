package br.ufpe.cin.jarlesproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

public class SaveRouteActivity extends ActionBarActivity {

	String message;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		message = intent.getStringExtra(GridActivity.EXTRA_MESSAGE);
		
		setContentView(R.layout.activity_save_route);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.save_route, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_save_route,
					container, false);
			return rootView;
		}
	}
	
	public void save(View view) {
		salvarSharedPref(message);
	 }
	
	private void salvarSharedPref(String rota) {
		EditText editText = (EditText) findViewById(R.id.nome);
		String nome = editText.getText().toString();
		System.out.println(nome);
		System.out.println(rota);
		
		// Cria ou abre.
		SharedPreferences prefs = getSharedPreferences("save_1", Context.MODE_PRIVATE);
		// Precisamos utilizar um editor para alterar Shared Preferences.
		Editor ed = prefs.edit();
		// salvando informações de acordo com o tipo
		ed.putString(nome, rota);
		// Grava efetivamente as alterações.
		ed.commit();
		
	}

}
