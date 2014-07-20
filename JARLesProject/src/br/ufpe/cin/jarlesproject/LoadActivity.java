package br.ufpe.cin.jarlesproject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class LoadActivity extends ListActivity {

	public static ArrayAdapter<String> adapter;
	private SharedPreferences conteudoSalvo;
	
	//pode ser que tenha que mudar
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Recupera o intent que iniciou a atividade e a mensagem.
	    conteudoSalvo = acessaSharedPreferences();

		String[] keys;
		keys = new String[] {};
		keys = conteudoSalvo.getAll().keySet().toArray(keys);
	    
		for(String nome : keys){
			System.out.println(nome);
		}
	    
	    String[] lista = new String[] { "data1","data2" };
	    adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, keys);
	    setListAdapter(adapter);
	    
	    /*
	    // Cria uma view para exibir as informações salvas.
	    TextView textView = new TextView(this);
	    textView.setTextSize(30);
	    textView.setText(conteudoSalvo);
 
	    // Define que o conteúdo exibido pela tela é o campo que
	    // irá exibir as informações.
	    setContentView(textView);
	    */
	}
	
	private void sendMessage(String message) {
		Intent intent = new Intent(this, GridActivity.class);
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
	
	@Override 
	protected void onListItemClick (ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String key = this.getListAdapter().getItem(position).toString();
		Toast.makeText(this, "Você escolheu a rota " + key, Toast.LENGTH_SHORT).show();
		sendMessage((String) conteudoSalvo.getAll().get(key));
	}
	
	private SharedPreferences acessaSharedPreferences() {
		// Acesso às Shared Preferences usando o nome definido.
		SharedPreferences prefs = getSharedPreferences("save_1", Context.MODE_PRIVATE);
		return prefs;
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
