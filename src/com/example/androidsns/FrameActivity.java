package com.example.androidsns;

import net.majorkernelpanic.streaming.rtsp.RtspServer;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class FrameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.frame);
		
		//FragmentManager fm=getFragmentManager();
		//NewFrag1Activity frg1=(NewFrag1Activity)fm.findFragmentById(R.id.preview_fragment);
		//is.startService(new Intent(this,RtspServer.class));
		//Frag1Activity frg1=(Frag1Activity)fm.findFragmentById(R.id.preview_fragment);
		//Frag2Activity frg2=(Frag2Activity)fm.findFragmentById(R.id.remote_fragment);
	//	VideoActivity frg2=(VideoActivity)fm.findFragmentById(R.id.remote_fragment);
		//this.startService(new Intent(this,RtspServer.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.frame, menu);
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
