package br.ufpe.cin.jarlesproject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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

	//Atributos de view
	private GridView mGridView;
	private GridAdapter mAdapter;
	private int mStartIndex;
	private int mDarkColor;
	private int finalIndex;
	private int atualIndex = -1;
	private int ultimoObstaculo;
	private float w = 0, h = 0;
	private List<Integer> mRoute = new ArrayList<Integer>();
	private List<Integer> obstaculos = new ArrayList<Integer>();
	private OnTouchListener mGridTouch;
	public Button button;

	//Atributos da conexao bluetooth
	private final int REQUEST_ENABLE_BT = 1;
	private final String StrBluetooth = "linvor";
	private static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	Button btSend, btRec, btConect, btDesconect;
	private String mStrRec;
	private String mStrSendç;
	private BluetoothAdapter mBluetoothAdapter;
	private Set<BluetoothDevice> mPairedDevices; 
	private BluetoothSocket mBtSocket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;

	//Variaveis de thread
	private BufferMessage mBuffer = new BufferMessage();
	ThreadReceive T1 = null;
	
	
	
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
		findViewById(R.id.conect).setOnClickListener(this);
		findViewById(R.id.desconect).setOnClickListener(this);

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
									if(atualIndex != -1){
										View selectedView = mGridView.getChildAt(atualIndex);
										if(selectedView != null){
											TextView txtView = ((TextView) selectedView.findViewById(R.id.index));
											txtView.setTextColor(mDarkColor);
											selectedView.setBackgroundResource(R.drawable.background_position);
										}
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
		System.out.println(menssagem);

		if(proto_inicial.compareTo(menssagem) != 0){
			for(char c : menssagem.toCharArray()){
				if(c == 'F'){
					char prox = menssagem.charAt(menssagem.indexOf(c) + 1);
					count = count + Character.getNumericValue(prox);
				}
			}
			if((menssagem.charAt(menssagem.length() - 2) == 'E') || (menssagem.charAt(menssagem.length() - 2) == 'D')){
				System.out.println("entrou no if ");
				count = count + 1;
			}
			
			int indexObstaculo = mRoute.get(count);
			System.out.println("indice em " + count);
			System.out.println("Obstaculo em " + indexObstaculo);
			obstaculos.add(indexObstaculo);
			ultimoObstaculo = indexObstaculo;
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
	public void onPause() {
		super.onPause();  // Always call the superclass method first
		
		if(T1 != null){
			T1.destroy();
			T1 = null;
			desconect();
		}
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

	public void executeRoute() throws InterruptedException{
		
		if(mBtSocket != null){
			try {
				outStream = mBtSocket.getOutputStream();
			} catch (Exception e) {
				System.out.println("Exception(Send): " + e);
			}

			String protocolo = "";
			if(mRoute.get(0) == atualIndex){
				if(atualIndex - ultimoObstaculo == 10){
					if(mRoute.get(0) - mRoute.get(1) == -1){
						protocolo = "D" + converteRota_proto(mRoute);
					}
					if(mRoute.get(0) - mRoute.get(1) == 1){
						protocolo = "E" + converteRota_proto(mRoute);
					}
				}	
				if(atualIndex - ultimoObstaculo == -1){
					if(mRoute.get(0) - mRoute.get(1) == -10){
						protocolo = "D" + converteRota_proto(mRoute);
					}
					if(mRoute.get(0) - mRoute.get(1) == 10){
						protocolo = "E" + converteRota_proto(mRoute);
					}
				}
				if(atualIndex - ultimoObstaculo == -10){
					if(mRoute.get(0) - mRoute.get(1) == 1){
						protocolo = "D" + converteRota_proto(mRoute);
					}
					if(mRoute.get(0) - mRoute.get(1) == -1){
						protocolo = "E" + converteRota_proto(mRoute);
					}
				}	
				if(atualIndex - ultimoObstaculo == 1){
					if(mRoute.get(0) - mRoute.get(1) == 10){
						protocolo = "D" + converteRota_proto(mRoute);
					}
					if(mRoute.get(0) - mRoute.get(1) == -10){
						protocolo = "E" + converteRota_proto(mRoute);
					}
				}			
			}
			else{
				protocolo = converteRota_proto(mRoute);
			}

			byte[] strRotaBuffer = protocolo.getBytes();
			
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
		
		T1 = new ThreadReceive(mBtSocket,mBuffer);
		T1.start();				
		T1.join();
				
		if(mBuffer.getLenght() > 0){
			System.out.println(mBuffer.removeMess());
			obstaculo(mBuffer.removeMess());
			mBuffer.clear();
		}
		
	}

	public void conect(){
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		//Conecta-se com dispositivo já pareado			
		//Checa se o bluetooth está disponivel
		//mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null){
			Toast.makeText(context, "Bluetooth não está disponivel!", Toast.LENGTH_SHORT).show();
		}
			
		//Checa se o bluetooth está habilitado
		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(context, "Ligando bluetooth", Toast.LENGTH_SHORT).show();
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		mPairedDevices = mBluetoothAdapter.getBondedDevices();
		BluetoothDevice mDeviceBluetooth = null;

		if(mPairedDevices.size() > 0){
			for(BluetoothDevice device : mPairedDevices){
				if(device.getName().equals(StrBluetooth)){
					mDeviceBluetooth = device;
					break;
				}
			}
		}

		if(mDeviceBluetooth != null){
			try{
				//Socket de conexao
				mBtSocket = mDeviceBluetooth.createRfcommSocketToServiceRecord(mUUID);
				//Inicia conexão
				mBtSocket.connect();
				Toast.makeText(context, "Bluetooth conectado com sucesso!", Toast.LENGTH_SHORT).show();
			}catch(IOException e){
				System.out.println("Exception gerada: " + e);
			}
		}else{
			Toast.makeText(context, "Dispositivo não está disponivel", Toast.LENGTH_SHORT).show();
		}
	}

	public void desconect(){
		if(T1 != null){
			T1.destroy();
			T1 = null;			
		}
		
		if(mBtSocket != null){
			try{
				mBtSocket.close();
				mBtSocket = null;

				//informa ao usuario que a rota foi salva com sucesso e desenha a nova rota
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setTitle("JARLes");
				alertDialogBuilder.setMessage("Deseja desligar o bluetooth?");
				alertDialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						//desenha a rota carregada
						mBluetoothAdapter.disable();
						Toast.makeText(context, "Conexão encerrada e Bluetooth desligado!", Toast.LENGTH_SHORT).show();
					}
				});
				alertDialogBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						Toast.makeText(context, "Conexão encerrada!", Toast.LENGTH_SHORT).show();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();

			}catch(IOException e){
				System.out.println("Exception gerada: " + e);
			}
		}
		else{
			Toast.makeText(context, "Dispositivo não conectado!", Toast.LENGTH_SHORT).show();
		}
	}

	public void clear(){

		mRoute.clear();
		mGridView.setAdapter(mAdapter);

		//informa ao usuario que um obstaculo foi encontrado
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("JARLes");
		alertDialogBuilder.setMessage("Deseja apagar todos os obstaculos e a posição atual do Rôbo?");
		alertDialogBuilder.setNegativeButton("Sim", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				obstaculos.clear();
				atualIndex = -1;
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
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.execute:
			try {
				executeRoute();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case R.id.save:
			sendMessage(converteRota_save (mRoute));
			break;

		case R.id.conect: 
			conect();
			break;

		case R.id.desconect: 
			desconect();
			break;

		case R.id.clear:
			clear();
			break;
		}

	}

	//Método que retorna do startActivityForResult com os códigos de resultado
	//Usado para habilitar o bluetooth
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == REQUEST_ENABLE_BT){
			if(resultCode == Activity.RESULT_OK){
				Toast.makeText(context, "Bluetooth Ativado!", Toast.LENGTH_SHORT).show();
			}
			else{
				finish();
			}
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
