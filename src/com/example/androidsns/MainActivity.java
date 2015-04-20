package com.example.androidsns;

import net.majorkernelpanic.streaming.rtsp.RtspServer;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {

	EditText mEdit;
	Button btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		mEdit=(EditText)findViewById(R.id.texfield);
		btn=(Button)findViewById(R.id.startbtn);
		btn.setOnClickListener(new ClickHandler());
		this.startService(new Intent(this,RtspServer.class));
		Intent i = new Intent(this , ServiceHandler.class);
		//Log.i("mytag","asdasd");
		ComponentName b = startService(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	class ClickHandler implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(v.getId()==R.id.startbtn)
			{
				String arg=mEdit.getText().toString();
				ServiceHandler.myid=arg;
				String[] token=ServiceHandler.listsoc.getLocalAddress().toString().split("/");
				ServiceHandler.spw.println("_add="+arg+":"+token[1]+":"+"9190");
				Intent i=new Intent(getApplicationContext(),ListActivity.class);
				startActivity(i);
			}
		}
	}
}
