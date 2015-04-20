package com.example.androidsns;

import com.example.androidsns.NewFrag1Activity.ClickHandler;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Fragment {
	private static final String MOVIE_URL = "http://www.archive.org/download/Unexpect2001/Unexpect2001_512kb.mp4";
	private static final String fl = "http://www.archive.org/download/Unexpect2001/Unexpect2001_512kb.mp4";
	private static final String fll="rtsp://192.168.0.5:1935/vod/girls.mp4";
	VideoView videoView;
	boolean flg=false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.video, container,false);
		videoView=(VideoView)v.findViewById(R.id.video); //fragment 에서 는 이렇게 호출
		//MediaController mediaController=new MediaController(this);
		//String ipaddr=ServiceHandler.mediasoc.getInetAddress().toString();
	//	String[] token=ipaddr.split("/");
		 //Uri video = Uri.parse("rtsp://192.168.0.10:8086/stream.sdp");
		// Uri video = Uri.parse(fll);
		//    videoView.setMediaController(new MediaController(getActivity()));
		  //  videoView.setVideoURI(video);
		 //   videoView.requestFocus();
		  //  videoView.start();
		Button sbtn=(Button)v.findViewById(R.id.playerbtn);
		sbtn.setOnClickListener(new ClickHandler());
		return v;
	}
	class ClickHandler implements OnClickListener
	{
		public void onClick(View v)
		{
			if(v.getId()==R.id.playerbtn)
			{
				if(flg==false){
				String[] token=ServiceHandler.chatsoc.getInetAddress().toString().split("/");
				 Uri video = Uri.parse("rtsp://"+token[1]+":8086/stream.sdp");
				//Uri video = Uri.parse("rtsp://203.246.113.124:8086/stream.sdp");
					// Uri video = Uri.parse(fll);
					//    videoView.setMediaController(new MediaController(getActivity()));
					    videoView.setVideoURI(video);
					    videoView.requestFocus();
					   // videoView.setRotation(90f);
					    videoView.start();
					    flg=true;
				}
			}
		}
		
	}
}
