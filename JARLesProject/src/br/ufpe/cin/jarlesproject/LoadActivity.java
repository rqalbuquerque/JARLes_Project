package br.ufpe.cin.jarlesproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LoadActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		// Recupera o intent que iniciou a atividade e a mensagem.
	    String conteudoSalvo;
	    conteudoSalvo = acessaSharedPreferences();
 
	    // Cria uma view para exibir as informações salvas.
	    TextView textView = new TextView(this);
	    textView.setTextSize(30);
	    textView.setText(conteudoSalvo);
 
	    // Define que o conteúdo exibido pela tela é o campo que
	    // irá exibir as informações.
	    setContentView(textView);
	}
	
	private String acessaSharedPreferences() {
		// Acesso às Shared Preferences usando o nome definido.
		SharedPreferences prefs = getSharedPreferences("save_1", Context.MODE_PRIVATE);
 
		// Acesso às informações de acordo com o tipo.
		System.out.println(prefs.getAll());
		//String texto = prefs.getString("ROTA1", "não encontrado");
		String texto = "";
		// Formata um string com todo o conteúdo separado por linha.
		return (texto);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.load, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_load, container,
					false);
			return rootView;
		}
	}

}
