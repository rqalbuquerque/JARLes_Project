package br.ufpe.cin.jarlesproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {

	Context mContext;
	
	public GridAdapter(Context ctx){
		mContext = ctx;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 100;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup viewGroup) {

		View v = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
		
		TextView index = (TextView) v.findViewById(R.id.index);
		index.setText(String.valueOf(position));
		
		return v;
	}

}
