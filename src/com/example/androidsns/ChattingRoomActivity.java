package com.example.androidsns;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.navdrawer.SimpleSideDrawer;

public class ChattingRoomActivity extends ActionBarActivity {

	private ArrayList<listinfo> clist;
	private ArrayList<String> chalist;
	private listinfo lf;
	//private CustomAdaopter adapter;
	private ArrayAdapter adapter;
	SimpleSideDrawer mSlidingMenu;
	String information;
	//Button
	Button side,sendbtn,callbtn;
	EditText txtfield;
	
	//network
	ServerSocket ssoc;
	Socket soc;
	PrintWriter cpw;
	BufferedReader cbr;
	SocketHandler sh;
	listListenHandler llh;
	//String[] token;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chattingroom);
		clist=new ArrayList<listinfo>();
		chalist=new ArrayList<String>();
		//lf=new listinfo(R.drawable.ic_launcher,"asdasdasdasdas");
	//	clist.add(lf);
	//	lf=new listinfo(R.drawable.ic_launcher,"asdasdasdasds");
	//	clist.add(lf);
		Intent i=getIntent();
		information=i.getStringExtra("clientinformation");
		//token=information.split(":");
		Log.i("MyTag",("myinformation"+information));
		//adapter=new CustomAdaopter(this,android.R.layout.simple_expandable_list_item_1,clist);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,chalist);
		ListView custom_list=(ListView)findViewById(R.id.chatlist);
		custom_list.setAdapter(adapter);
		mSlidingMenu=new SimpleSideDrawer(this);
		mSlidingMenu.setBehindContentView(R.layout.sidemenu);
		callbtn=(Button)mSlidingMenu.findViewById(R.id.callbtn);
		txtfield=(EditText)findViewById(R.id.sendfield);
		side=(Button)findViewById(R.id.sidemnu);
		sendbtn=(Button)findViewById(R.id.sndbtn);
		callbtn.setOnClickListener(new clickHandler());
		side.setOnClickListener(new clickHandler());
		sendbtn.setOnClickListener(new clickHandler());
		sh=new SocketHandler();
		llh=new listListenHandler();
		try{
		sh.start();
		//llh.start();
	//	sh.join();
	//	llh.join();
		}catch(Exception e){}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chatting_room, menu);
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
	
	class clickHandler implements OnClickListener
	{
		public void onClick(View v)
		{
			if(v.getId()==R.id.sndbtn)
			{
				String snd=txtfield.getText().toString();
				//lf=new listinfo(R.drawable.ic_launcher,snd);
				chalist.add(snd);
				txtfield.setText("");
				cpw.println(ServiceHandler.myid+" : "+snd);
				adapter.notifyDataSetChanged();
			}
			else if(v.getId()==R.id.sidemnu)
			{
				mSlidingMenu.toggleDrawer();
			}
			else if(v.getId()==R.id.callbtn)
			{
				Intent i=new Intent(getApplicationContext(),FrameActivity.class);
				startActivity(i);
			}
			
		}
	}
	class SocketHandler extends Thread
	{
		@Override
		public void run()
		{
			//token=information.split(":");
			try{
			if(ServiceHandler.chatsoc==null)
			{
				String[] token=information.split(":");
				soc=new Socket(token[1], Integer.parseInt(token[2]));
				if(soc.isConnected()){
					Log.i("asdasda","!!!!");
				}else{
					Log.i("asdasd","113???");
				}
				
			}
			else
			{
				//token=information.split(":");
				Log.i("MtTag",ServiceHandler.chatsoc.getInetAddress().toString()+" "+ServiceHandler.chatsoc.getPort());
				String[] a=ServiceHandler.chatsoc.getInetAddress().toString().split("/");
				soc=new Socket(a[1],9190);// 접속을 요청 받았을 떄 연결 
				if(soc.isConnected()){
					Log.i("asdasda123","!!!!");
				}else{
					Log.i("asdasd1234","113???");
				}
			}
			cpw=new PrintWriter(soc.getOutputStream(),true);
			//cbr=new BufferedReader(new InputStreamReader(ServiceHandler.chatsoc.getInputStream()));
			
			String msg;
			while(true)
			{
				//String temp=token[0]+" "+msg;
				if(ServiceHandler.csbr!=null){
			//	String[] token=information.split(":");
				msg=ServiceHandler.csbr.readLine();
				//lf=new listinfo(R.drawable.ic_launcher,token[0]+" : "+msg);
				chalist.add(msg);
				runOnUiThread(new Runnable(){
					public void run()
					{
						adapter.notifyDataSetChanged();
					}
				});
				}
			}
			}catch(Exception e){
				Log.i("Mytag",e.toString());
			}
			
		}
	}
	class listListenHandler extends Thread
	{
		@Override
		public void run()
		{
			try{
				String msg;
				Log.i("Mytag","my Receivce");
			while((msg=cbr.readLine())!=null)
			{
				Log.i("Mytag",msg);
				lf=new listinfo(R.drawable.ic_launcher,msg);
				clist.add(lf);
				runOnUiThread(new Runnable(){
					public void run()
					{
						adapter.notifyDataSetChanged();
					}
				});
				
			}
			}catch(Exception e){}
		}
	}
}
