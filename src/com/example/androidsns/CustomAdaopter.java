package com.example.androidsns;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdaopter extends  ArrayAdapter<listinfo> {
	
	private ArrayList<listinfo> m_List;



	public CustomAdaopter(Context context, int resource, ArrayList<listinfo> objects) {
		super(context, resource, objects);
		this.m_List=objects;
		// TODO Auto-generated constructor stub
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_List.size();
	}


	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int pos=position;
		final Context context=parent.getContext();

		listinfo lf=m_List.get(position);
		if(convertView==null)
		{
			LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView=inflater.inflate(R.layout.custom, parent, false);
			
			
			TextView text=(TextView) convertView.findViewById(R.id.chattext);
			ImageView iv=(ImageView)convertView.findViewById(R.id.chatimage);
			iv.setBackgroundResource(lf.getImage_ID());
			//iv.setVisibility(View.GONE);
			text.setText(lf.getString());
		}
		return convertView;
	}

}
