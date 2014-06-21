package br.ufpe.cin.jarlesproject;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.GridView;
import android.widget.TextView;

public class GridActivity extends ActionBarActivity {

	GridView mGridView;
	GridAdapter mAdapter;
	int startIndex;
	int mDarkColor;
	float w = 0, h = 0;
	List<Integer> mRoute;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_grid);
		mGridView = (GridView) findViewById(R.id.grid);
		mAdapter = new GridAdapter(this);
		mRoute = new ArrayList<Integer>();
		mGridView.setAdapter(mAdapter);
		
		mDarkColor = getResources().getColor(android.R.color.background_dark);
		
		mGridView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				float x = event.getX();
				float y = event.getY();
				
				if(event.getAction() == event.ACTION_DOWN){
					View vi = mGridView.getChildAt(0);
					w = vi.getMeasuredWidth();
					h = vi.getMeasuredHeight();
				}
				
				int column = (int)x / (int)w;
				int row = (int)y / (int)h;
				
				int index = (row * mGridView.getNumColumns()) + column;
								
				View selectedView = mGridView.getChildAt(index);
				
				if(selectedView != null){
					TextView txtView = ((TextView)selectedView.findViewById(R.id.index));
					txtView.setTextColor(mDarkColor);
					
					if(event.getAction() == event.ACTION_DOWN){
						startIndex = index;
						selectedView.setBackgroundResource(R.drawable.background_start);
					}
					else if(event.getAction() == event.ACTION_UP){
						selectedView.setBackgroundResource(R.drawable.background_finish);
					}
					else{
						if(index != startIndex)
							selectedView.setBackgroundResource(R.drawable.background_selected);
					}
					
					if(!mRoute.contains(index)){
						mRoute.add(index);
					}
				}
				
				return false;
			}
		});
		
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

}
