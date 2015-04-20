package com.example.androidsns;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.video.VideoQuality;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NewFrag1Activity extends Fragment implements Session.Callback,SurfaceHolder.Callback {

	private SurfaceView mSurfaceView;
	private Session mSession;
	private final static String TAG = "frag new 1";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.new_frag1, container,false);
		
		mSurfaceView = (SurfaceView) v.findViewById(R.id.new_preview);
		
		 	mSession=SessionBuilder.getInstance()
				.setCallback(this)
				.setSurfaceView(mSurfaceView)
				.setContext(getActivity())
				.setAudioEncoder(SessionBuilder.AUDIO_NONE)
				.setAudioQuality(new AudioQuality(16000,32000))
				.setVideoEncoder(SessionBuilder.VIDEO_H264)
				.setVideoQuality(new VideoQuality(320,240,20,500000))
				.build();
		//mSession.con
		//Button sbtn=(Button)v.findViewById(R.id.newbtn);
		//sbtn.setOnClickListener(new ClickHandler());
		mSurfaceView.getHolder().addCallback(this);
		
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mSession.isStreaming()) {
			//mButton1.setText(R.string.stop);
		} else {
			//mButton1.setText(R.string.start);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mSession.release();
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	//	mSession.configure();
		//mSession.switchCamera();
		//mSession.configure();
	//	mSession.startPreview();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mSession.stop();
	}

	@Override
	public void onBitrareUpdate(long bitrate) {
		// TODO Auto-generated method stub
		Log.d(TAG,"Bitrate: "+bitrate);
	}

	@Override
	public void onSessionError(int reason, int streamType, Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPreviewStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSessionConfigured() {
		// TODO Auto-generated method stub
		Log.d(TAG,"Preview configured.");
		mSession.start();
	}

	@Override
	public void onSessionStarted() {
		// TODO Auto-generated method stub
		Log.d(TAG,"Session started.");
	}

	@Override
	public void onSessionStopped() {
		// TODO Auto-generated method stub
		Log.d(TAG,"Session stopped.");
	}
	class ClickHandler implements OnClickListener
	{
		public void onClick(View v)
		{
			//if(v.getId()==R.id.newbtn)
			{
			//	mSession.configure();
			//	mSession.switchCamera();
			//	mSession.startPreview();
			}
		}
		
	}
}
