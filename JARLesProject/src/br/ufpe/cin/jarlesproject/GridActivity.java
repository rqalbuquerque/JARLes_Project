package br.ufpe.cin.jarlesproject;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

public class GridActivity extends ActionBarActivity implements OnClickListener {
	
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	final Context context = this;
	
	private GridView mGridView;
	private GridAdapter mAdapter;
	private int mStartIndex;
	private int mDarkColor;
	private float w = 0, h = 0;
	private List<Integer> mRoute = new ArrayList<Integer>();
	private OnTouchListener mGridTouch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String message = "";
		
		try {
			Intent intent = getIntent();
			if(intent != null){
				message = intent.getStringExtra(GridActivity.EXTRA_MESSAGE);
				System.out.println(message);
			
				mRoute = recuperaRota(message);
				System.out.println(mRoute);
			}
		} catch (Exception e) {
			System.out.println("Usuario acessou new grid!");
		}
		

		setContentView(R.layout.activity_grid);
		mGridView = (GridView) findViewById(R.id.grid);
		mAdapter = new GridAdapter(this);
		mGridView.setAdapter(mAdapter);
		
		System.out.println(mGridView.getChildCount());
		System.out.println("chegou no onStart()");
		
		if(!mRoute.isEmpty()){
			for(int index : mRoute){
				View selectedView = mGridView.getChildAt(index);
				
				if(selectedView != null){
					TextView txtView = ((TextView) selectedView.findViewById(R.id.index));
					txtView.setTextColor(mDarkColor);
				}
				
				if(mRoute.indexOf(index) == 0){
					if (selectedView != null)
						selectedView.setBackgroundResource(R.drawable.background_start);
				} 
				else if(mRoute.indexOf(index) == (mRoute.size() - 1)){
					if (selectedView != null)
						selectedView.setBackgroundResource(R.drawable.background_finish);
				} 
				else{
					if (selectedView != null)
						selectedView.setBackgroundResource(R.drawable.background_selected);
				}
			}
		}

		findViewById(R.id.execute).setOnClickListener(this);
		findViewById(R.id.save).setOnClickListener(this);
		findViewById(R.id.clear).setOnClickListener(this);
		
		mDarkColor = getResources().getColor(android.R.color.background_dark);

		mGridTouch = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				float x = event.getX();
				float y = event.getY();

				if (event.getAction() == event.ACTION_DOWN) {
					View vi = mGridView.getChildAt(0);
					w = vi.getMeasuredWidth();
					h = vi.getMeasuredHeight();
				}

				int column = (int) x / (int) w;
				int row = (int) y / (int) h;

				int index = (row * mGridView.getNumColumns()) + column;

				//System.out.println(mGridView.getChildCount());
				View selectedView = mGridView.getChildAt(index);

				if (selectedView != null) {
					TextView txtView = ((TextView) selectedView.findViewById(R.id.index));
					txtView.setTextColor(mDarkColor);

					if (event.getAction() == event.ACTION_DOWN) {
						mStartIndex = index;
						selectedView.setBackgroundResource(R.drawable.background_start);
					} else if (event.getAction() == event.ACTION_UP) {
						selectedView.setBackgroundResource(R.drawable.background_finish);
						mGridView.setOnTouchListener(null);

						System.out.println("Antes do if");
						if(!testaRota(mRoute)){
							mRoute = new ArrayList<Integer>();
							System.out.println("Rota invalida");
							
							//alerta ao usuario que a rota é invalida
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
							alertDialogBuilder.setTitle("JARLes");
							alertDialogBuilder.setMessage("Rota inválida!");
							AlertDialog alertDialog = alertDialogBuilder.create();
							alertDialog.show();
		
							mGridView.setOnTouchListener(mGridTouch);
							mGridView.setAdapter(mAdapter);
						}
						
					} else {
						if (index != mStartIndex)
							selectedView.setBackgroundResource(R.drawable.background_selected);
					}

					if (!mRoute.contains(index)) {
						mRoute.add(index);
					}
				}
				
				
				return false;
			}
		};
		
		mGridView.setOnTouchListener(mGridTouch);

		// Obtem o gridView de activity_grid
		mGridView = (GridView) findViewById(R.id.grid);
		//
		mGridView.setAdapter(new GridAdapter(this));
	}
	
	@Override
	public void onStart(){
	    super.onStart();
	    
		System.out.println(mGridView.getChildCount());
		System.out.println("chegou no onStart()");
		
		if(!mRoute.isEmpty()){
			for(int index : mRoute){
				View selectedView = mGridView.getChildAt(index);
				
				if(selectedView != null){
					TextView txtView = ((TextView) selectedView.findViewById(R.id.index));
					txtView.setTextColor(mDarkColor);
				}
				
				if(mRoute.indexOf(index) == 0){
					if (selectedView != null)
						selectedView.setBackgroundResource(R.drawable.background_start);
				} 
				else if(mRoute.indexOf(index) == (mRoute.size() - 1)){
					if (selectedView != null)
						selectedView.setBackgroundResource(R.drawable.background_finish);
				} 
				else{
					if (selectedView != null)
						selectedView.setBackgroundResource(R.drawable.background_selected);
				}
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grid, menu);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.execute:
			break;
		case R.id.save:
			sendMessage(converteRota_save (mRoute));
			break;
		case R.id.clear:
			mGridView.setOnTouchListener(mGridTouch);
			mGridView.setAdapter(new GridAdapter(this));
			break;
		}

	}
	//retorna falso caso seja uma rota invalida
	public boolean testaRota(List<Integer> mRoute){
		boolean resposta = true;
		Integer anterior = -200;
		for (Integer x:mRoute){
			if(anterior - x == 11 || anterior - x == -11)
				resposta = false;
			if(anterior - x == 9 || anterior - x == -9)
				resposta = false;
			
			anterior = x;
		}
		return resposta;
	}
	
	private void sendMessage(String message) {
		Intent intent = new Intent(this, SaveRouteActivity.class);
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
	
	private List<Integer> recuperaRota(String message){
		List<Integer> rota = new ArrayList<Integer>();
		int count = 0;
		char messageArray[] = message.toCharArray();
		
		for(char c : messageArray){
			if(c == '#'){
				count++;
				String numString = "";
				Integer numero;
				
				while(messageArray[count] != '#' && messageArray[count] != '$'){
					numString = numString + messageArray[count];
					count++;
				}
				numero = Integer.parseInt(numString);
				
				rota.add(numero);
			}
		}
		return rota;
	}

	private String converteRota_save (List<Integer> mRoute){
		String rota = "";
		
		for(Integer x: mRoute){
			rota = rota + "#" + x;
		}
		rota = rota + "$";
		return rota;
	}
	
	private String converteRota_proto (List<Integer> mRoute){
		String rota = "";
		String direcao = "";
		int y = -1;
		int count = 0;
		
		for(Integer x: mRoute){
			if(mRoute.indexOf(x) == 0){
				rota = rota + "F";
				y = x;
			}
			else{
				//caso a rota seja na mesma direção
				if((x - y == -1) && (direcao.equals("esquerda") || direcao.equals(""))){
					direcao = "esquerda";
					count++;
					y = x;
				}
				else if(x - y == 1 && (direcao.equals("direita") || direcao.equals(""))){
					direcao = "direita";
					count++;
					y = x;
				}
				else if(x - y == -10 && (direcao.equals("cima") || direcao.equals(""))){
					direcao = "cima";
					count++;
					y = x;
				}
				else if(x - y == 10 && (direcao.equals("baixo") || direcao.equals(""))){
					direcao = "baixo";
					count++;
					y = x;
				}
				
				//caso a rota mude de direção
				//caso vire para a direita
				else if(x - y == -10 && direcao.equals("esquerda")){
					direcao = "cima";
					rota = rota + count + "D" + "F";
					count = 1;
					y = x;
				}
				else if(x - y == 10 && direcao.equals("direita")){
					direcao = "baixo";
					rota = rota + count + "D" + "F";
					count = 1;	
					y = x;				
				}
				else if(x - y == 1 && direcao.equals("cima")){
					direcao = "direita";
					rota = rota + count + "D" + "F";
					count = 1;		
					y = x;			
				}
				else if(x - y == -1 && direcao.equals("baixo")){
					direcao = "esquerda";
					rota = rota + count + "D" + "F";
					count = 1;		
					y = x;			
				}
				
				//caso vire para a esquerda
				else if(x - y == 10 && direcao.equals("esquerda")){
					direcao = "baixo";
					rota = rota + count + "E" + "F"; 
					count = 1;		
					y = x;			
				}
				else if(x - y == -10 && direcao.equals("direita")){
					direcao = "cima";
					rota = rota + count + "E" + "F";
					count = 1;		
					y = x;			
				}
				else if(x - y == -1 && direcao.equals("cima")){
					direcao = "esquerda";
					rota = rota + count + "E" + "F";
					count = 1;		
					y = x;			
				}
				else if(x - y == 1 && direcao.equals("baixo")){
					direcao = "direita";
					rota = rota + count + "E" + "F";
					count = 1;	
					y = x;				
				}
			}
		}
		if( count != 0)
			rota = rota + count + "$";
		//System.out.println(rota);
		return rota;
	}

}
