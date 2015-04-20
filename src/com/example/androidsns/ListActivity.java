package com.example.androidsns;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ListActivity extends ActionBarActivity implements OnItemClickListener {

	ArrayList<String> list;
	ArrayAdapter<String> adapter;
	listHandler lh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list);
		list=new ArrayList<String>();
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
		ListView listview1=(ListView)findViewById(R.id.userlist);
		listview1.setAdapter(adapter);
		listview1.setOnItemClickListener(this);
		//updateList();
		//Button
		//list.add("asdasdsdasdasdassadasd");
		Button rfsh=(Button)findViewById(R.id.Refresh);
		rfsh.setOnClickListener(new clickHandler());
		//adapter.notifyDataSetChanged();
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list, menu);
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		String clientinfo=(String)parent.getItemAtPosition(position);
		String[] token=clientinfo.split(":");
		Log.i("token : ",token[0]+token[1]+token[2]);
		Intent i=new Intent(getApplicationContext(),ChattingRoomActivity.class);
		i.putExtra("clientinformation", clientinfo);
		startActivity(i);
		
	}
	class clickHandler implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId()==R.id.Refresh){
				//list.add("asdasdasdasd");
				list.clear();
				
				try{
				lh=new listHandler();
				lh.start();
				
				lh.join(1000);
				adapter.notifyDataSetChanged();
				}catch(Exception e){}
				
			}
		}
	}
	class listHandler extends Thread
	{
		void updateList()
		{
			try{
			ServiceHandler.spw.println("_refresh");
			String tmp;
			ServiceHandler.sbr.readLine();
			while((tmp=ServiceHandler.sbr.readLine())!=null){
				//tmp=ServiceHandler.sbr.readLine();
				//String tmp=ServiceHandler.sbr.readLine();
				Log.i("Mylist",tmp);
				list.add(tmp);
				//listview1.
				//adapter.notifyDataSetChanged();
			}
			}catch(Exception e){
				Log.e("myERR","ERROR LIST UPDATE");
			}
			//adapter.notifyDataSetChanged();
				
		}
		public void run()
		{
			updateList();
			runOnUiThread(new Runnable(){
				public void run()
				{
					try{
					while(true){
					adapter.notifyDataSetChanged();
					sleep(1000);
					}
					}catch(Exception e){}
				}
			});
		}
	}


}
