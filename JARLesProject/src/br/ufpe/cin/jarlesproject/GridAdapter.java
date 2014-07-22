package br.ufpe.cin.jarlesproject;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {

	private Context mContext;
	private List<Integer> mRoute;
	
	public GridAdapter(Context ctx) {
		mContext = ctx;
	}

	@Override
	public int getCount() {
		return 90;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
		TextView index = (TextView) v.findViewById(R.id.index);
		index.setText(String.valueOf(position));
		return v;
	}

}
