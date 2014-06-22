package br.ufpe.cin.jarlesproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {

	private Context mContext;
	
	public GridAdapter(Context ctx) {
		mContext = ctx;
	}

	@Override
	public int getCount() {
		return 100;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
		TextView index = (TextView) v.findViewById(R.id.index);
		index.setText(String.valueOf(position));
		return v;
	}

}
