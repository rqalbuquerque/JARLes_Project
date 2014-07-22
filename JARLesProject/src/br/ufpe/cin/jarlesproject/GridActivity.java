package br.ufpe.cin.jarlesproject;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class GridActivity extends ActionBarActivity implements OnClickListener {
	
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	final Context context = this;
	
	private GridView mGridView;
	private GridAdapter mAdapter;
	private int mStartIndex;
	private int mDarkColor;
	private int finalIndex;
	private int atualIndex;
	private float w = 0, h = 0;
	private List<Integer> mRoute = new ArrayList<Integer>();
	private List<Integer> obstaculos = new ArrayList<Integer>();
	private OnTouchListener mGridTouch;
	public Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String message = "";

		//inicializa a gridView
		setContentView(R.layout.activity_grid);
		mGridView = (GridView) findViewById(R.id.grid);
		mAdapter = new GridAdapter(this);
		mGridView.setAdapter(mAdapter);
		
		//tenta receber o intent da atividade load
		try {
			Intent intent = getIntent();
			if(intent != null){
				//menssagem que contem a rota carregada
				message = intent.getStringExtra(GridActivity.EXTRA_MESSAGE);
				System.out.println(message);
				mRoute = recuperaRota_save(message);
				System.out.println(mRoute);

				//informa ao usuario que a rota foi salva com sucesso e desenha a nova rota
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setTitle("JARLes");
				alertDialogBuilder.setMessage("Rota carregada com sucesso!");
				alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
							//desenha a rota carregada
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
					});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		} catch (Exception e) {
			System.out.println("Usuario acessou new grid!");
		}
		
		//seta o click listener para os botões
		findViewById(R.id.execute).setOnClickListener(this);
		findViewById(R.id.save).setOnClickListener(this);
		findViewById(R.id.clear).setOnClickListener(this);
		
		mDarkColor = getResources().getColor(android.R.color.background_dark);
		
		//define o touch listener para desenhar uma nova rota
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

						if(!testaRota(mRoute)){
							mRoute.clear();
							mGridView.setAdapter(mAdapter);

							//informa ao usuario que a rota esta errada
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
							alertDialogBuilder.setTitle("JARLes");
							alertDialogBuilder.setMessage("Rota invalida!");
							alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									//marca todos os obstaculos
									for(Integer index : obstaculos){
										View selectedView = mGridView.getChildAt(index);
										if(selectedView != null){
											TextView txtView = ((TextView) selectedView.findViewById(R.id.index));
											txtView.setTextColor(mDarkColor);
											selectedView.setBackgroundResource(R.drawable.background_obstacle);
										}
									}
									//marca a posicao atual do robo
									View selectedView = mGridView.getChildAt(atualIndex);
									if(selectedView != null){
										TextView txtView = ((TextView) selectedView.findViewById(R.id.index));
										txtView.setTextColor(mDarkColor);
										selectedView.setBackgroundResource(R.drawable.background_position);
									}
								}
							});
							AlertDialog alertDialog = alertDialogBuilder.create();
							alertDialog.show();
							
							mGridView.setOnTouchListener(mGridTouch);
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
		
		//reinicializa a gridview e seta o touch listener
		mGridView.setOnTouchListener(mGridTouch);
		mGridView = (GridView) findViewById(R.id.grid);
		mGridView.setAdapter(new GridAdapter(this));
	}
	
	public void obstaculo(String menssagem){
		String proto_inicial = converteRota_proto(mRoute);
		List<Integer> novaRota = new ArrayList<Integer>();
		int count = 0;
		
		if(proto_inicial.compareTo(menssagem) != 0){
			for(char c : menssagem.toCharArray()){
				if(c == 'F'){
					char prox = menssagem.charAt(menssagem.indexOf(c) + 1);
					count = count + Character.getNumericValue(prox);
				}
			}
			int indexObstaculo = mRoute.get(count);
			System.out.println("Obstaculo em " + indexObstaculo);
			obstaculos.add(indexObstaculo);
			finalIndex = mRoute.get(mRoute.size()-1);
			
			for(int x: mRoute){
				if(mRoute.indexOf(x) <= count){
					novaRota.add(x);
				}
			}
			
			mAdapter = new GridAdapter(this);
			mGridView.setAdapter(mAdapter);
			mRoute = novaRota;
			
			atualIndex = mRoute.get(mRoute.size()-2);
			
			//informa ao usuario que um obstaculo foi encontrado
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setTitle("JARLes");
			alertDialogBuilder.setMessage("Obstaculo encontrado!");
			alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					
						//desenha a rota com o obstaculo
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
								else if(mRoute.indexOf(index) == (mRoute.size() - 2)){
									if (selectedView != null)
										selectedView.setBackgroundResource(R.drawable.background_position);
								} 
								else{
									if (selectedView != null)
										selectedView.setBackgroundResource(R.drawable.background_selected);
								}
							}
						}
						
						//marca todos os obstaculos
						for(Integer index : obstaculos){
							View selectedView = mGridView.getChildAt(index);
							if(selectedView != null){
								TextView txtView = ((TextView) selectedView.findViewById(R.id.index));
								txtView.setTextColor(mDarkColor);
								selectedView.setBackgroundResource(R.drawable.background_obstacle);
							}
						}

						//marca o ultimo ponto final
						View selectedView = mGridView.getChildAt(finalIndex);
						if(selectedView != null){
							TextView txtView = ((TextView) selectedView.findViewById(R.id.index));
							txtView.setTextColor(mDarkColor);
							selectedView.setBackgroundResource(R.drawable.background_finish);
						}

						//solicita e espera usuario digitar nova rota
						Toast.makeText(context, "Digite uma nova rota", Toast.LENGTH_SHORT).show();
						mRoute.clear();
						mGridView.setOnTouchListener(mGridTouch);
					}
				});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
		else{
			//informa ao usuario que a rota foi concluida com sucesso
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setTitle("JARLes");
			alertDialogBuilder.setMessage("Rota concluida com sucesso!");
			alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					}
				});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
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
			obstaculo("F4$");
			break;
		case R.id.save:
			sendMessage(converteRota_save (mRoute));
			break;
		case R.id.clear:
			mRoute.clear();
			mGridView.setAdapter(mAdapter);

			//informa ao usuario que um obstaculo foi encontrado
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setTitle("JARLes");
			alertDialogBuilder.setMessage("Deseja apagar todos os obstaculos e a posição atual do Rôbo?");
			alertDialogBuilder.setNegativeButton("Sim", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					obstaculos.clear();
				}
			});
			alertDialogBuilder.setPositiveButton("Nao", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					//marca todos os obstaculos
					for(Integer index : obstaculos){
						View selectedView = mGridView.getChildAt(index);
						if(selectedView != null){
							TextView txtView = ((TextView) selectedView.findViewById(R.id.index));
							txtView.setTextColor(mDarkColor);
							selectedView.setBackgroundResource(R.drawable.background_obstacle);
						}
					}
					//marca a posicao atual do robo
					View selectedView = mGridView.getChildAt(atualIndex);
					if(selectedView != null){
						TextView txtView = ((TextView) selectedView.findViewById(R.id.index));
						txtView.setTextColor(mDarkColor);
						selectedView.setBackgroundResource(R.drawable.background_position);
					}
				}
			});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			
			mGridView.setOnTouchListener(mGridTouch);
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
	
	//envia rota para a atividade de salvar
	private void sendMessage(String message) {
		Intent intent = new Intent(this, SaveRouteActivity.class);
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	//recupera a rota da menssagem para o vetor
	private List<Integer> recuperaRota_save(String message){
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
	
	//recupera a rota da menssagem para o vetor
	private List<Integer> recuperaRota_proto(String message){
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

	//converte rota para a string de salvar
	private String converteRota_save (List<Integer> mRoute){
		String rota = "";
		
		for(Integer x: mRoute){
			rota = rota + "#" + x;
		}
		rota = rota + "$";
		return rota;
	}
	
	//converte a rota para enviar
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
