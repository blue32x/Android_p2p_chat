package com.example.androidsns;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.video.H264Stream;
import net.majorkernelpanic.streaming.video.VideoQuality;
import android.app.Fragment;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class Frag1Activity extends Fragment implements SurfaceHolder.Callback, Camera.PictureCallback, Session.Callback{
	 SurfaceView sv;
	 SurfaceHolder sh;
	 Camera mcamera;
	String TAG="FRAG1";
	//media
	MediaCodec codec;
	MediaFormat mediaformat;
	ByteBuffer[] inputBuffers;
	ByteBuffer[] outputBuffers;
	InputStream in;
	OutputStream out;
	FileOutputStream fos;
	EncoderClass ec;
	
	//network
	Socket soc;
	OutputStream sos=null;
	SocketHandler sochndl;
	FileInputStream fis;
	ParcelFileDescriptor pfd;
	//we don't use this socket fuck..
	
	//rtsp
	//H264Stream mPacketizer;
	Session msession;
	
	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		View v=inflater.inflate(R.layout.frag1, container,false);
		sv=(SurfaceView)v.findViewById(R.id.mypreview);	
		sh=sv.getHolder();	
		sh.addCallback(this);
		sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		Button sbtn=(Button)v.findViewById(R.id.sessionbtn);
		sbtn.setOnClickListener(new ClickHandler());
		ec=new EncoderClass();
		//sochndl=new SocketHandler();
		//ec.start();
		
		Session msession=SessionBuilder.getInstance().
				setSurfaceView((net.majorkernelpanic.streaming.gl.SurfaceView)v.findViewById(R.id.mypreview))
		.setPreviewOrientation(90).setContext(getActivity())
		.setAudioEncoder(SessionBuilder.AUDIO_NONE)
		.setVideoEncoder(SessionBuilder.VIDEO_H264)
		.setVideoQuality(new VideoQuality(320,240,20,500000))
		.build();
		//this.startService();
		//msession.configure();
		
		
	//	sochndl.start();
	//	try{
		//	ec.join();
	//		sochndl.join();
	//	}catch(Exception e){}
	  return v;

	 }
	@Override
	public void onDestroy() {
		super.onDestroy();
		msession.release();
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try{
	
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		//Camera.Parameters parameters = mcamera.getParameters();
		//parameters.setPreviewFormat(ImageFormat.NV21);
        //parameters.setPreviewSize(640, 480);
        //mcamera.setParameters(parameters);
		//mcamera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		msession.stop();
		msession.release();
	}
	class SocketHandler extends Thread
	{
		public void run()
		{
			initSoc();
			
		}
		void initSoc()
		{
			try{
				String[] token=ServiceHandler.chatsoc.getInetAddress().toString().split("/");
			//soc=new Socket(token[1],9191); //소켓 초기화
		//	soc=new Socket("192.168.0.5",9387);
			//pfd=ParcelFileDescriptor.fromSocket(soc);
			if(soc.isConnected())
			{
				Log.e("my","is connected");
			}
			sos=soc.getOutputStream();
			}catch(Exception e)
			{
				Log.e("mytag","my Exception ----------> "+e.toString());
				
			}
			
			
		}
	}
	class EncoderClass
	{
		//public void run()
	//	{
	//		init();
	//	}
		
		public EncoderClass()
		{
			try
	        {
				String path=Environment.getExternalStorageDirectory().getAbsolutePath();
				String dir=path+"/DCIM/";
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }

			codec=MediaCodec.createEncoderByType("video/avc");//h.263으로 인코딩
			mediaformat=MediaFormat.createVideoFormat("video/avc", 640, 480);
			mediaformat.setInteger(MediaFormat.KEY_BIT_RATE, 125000);
			mediaformat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
			mediaformat.setInteger(MediaFormat.KEY_COLOR_FORMAT, 
					MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
			mediaformat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
			mediaformat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 2000000);
			//mediaformat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 50000);
			codec.configure(mediaformat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
			codec.start();
			//inputBuffers = codec.getInputBuffers(); 
		//outputBuffers = codec.getOutputBuffers();
			
			//session 초기화
			//session=new Session();
			//session.setOrigin(null);
		//	session.setDestination(null);
		//	session.setTimeToLive(64);
		//	session.setCallback(null);
			
		}
		
		//mediacodec의 buffer를 채워 주는 용도
		private void inputbuffer(byte[] data)
		{
			inputBuffers=codec.getInputBuffers();
			outputBuffers=codec.getOutputBuffers();
			
			int inputBufferIndex=codec.dequeueInputBuffer(-1);
			Log.i("MyENCODE", "inputBufferIndex-->" + inputBufferIndex);
			if(inputBufferIndex >=0)
			{
				ByteBuffer inputBuffer=inputBuffers[inputBufferIndex];
				
				inputBuffer.clear();
				
	            inputBuffer.put(YV12toYUV420PackedSemiPlanar(data, 640, 480));
				codec.queueInputBuffer(inputBufferIndex, 0, data.length, 0, 0);
			}
			else
			{
				return ;
			}
			
		}
		private synchronized void encode(byte[] data)
		{
			inputBuffers=codec.getInputBuffers();
			outputBuffers=codec.getOutputBuffers();
			
			int inputBufferIndex=codec.dequeueInputBuffer(-1);
			Log.i("MyENCODE", "inputBufferIndex-->" + inputBufferIndex);
			if(inputBufferIndex >=0)
			{
				ByteBuffer inputBuffer=inputBuffers[inputBufferIndex];
				
				inputBuffer.clear();
				
	            inputBuffer.put(YV12toYUV420PackedSemiPlanar(data, 640, 480));
				codec.queueInputBuffer(inputBufferIndex, 0, data.length, 0, 0);
			}
			else
			{
				return ;
			}
			MediaCodec.BufferInfo bufferInfo=new MediaCodec.BufferInfo();
			int outputBufferIndex =codec.dequeueOutputBuffer(bufferInfo, 0);
			Log.i("MyEncode","outputBufferIndex--->"+outputBufferIndex);
			do
			{
				if(outputBufferIndex>=0)
				{
					ByteBuffer outBuffer=outputBuffers[outputBufferIndex];
					System.out.println("buffer info-->"+bufferInfo.offset+"--"+bufferInfo.size+"--"+bufferInfo.flags+"--"
							+bufferInfo.presentationTimeUs);
					byte[] outData=new byte[bufferInfo.size];
					outBuffer.get(outData); //bufferunderflow 발생 왜?? 소켓에서 받아가지 않아서
					
					try{
					//	sleep(1000);
						if(bufferInfo.offset!=0)
						{
							//file에 쓰는 부분
							//fos.write(outData, bufferInfo.offset, outData.length - bufferInfo.offset);
							sos.write(outData, bufferInfo.offset, outData.length - bufferInfo.offset);
							//Socket a=new Socket("127.0.0.1",9190);
						//	sleep(5000);

						}
						else
						{
							//fos.write(outData, 0, outData.length);
							sos.write(outData, 0, outData.length);
						//	sleep(5000);
						}
						//flush자리     
						//fos.flush();
						sos.flush();
						Log.i("Myencode","out data -- >"+outData.length);
						codec.releaseOutputBuffer(outputBufferIndex, false);
						outputBufferIndex=codec.dequeueOutputBuffer(bufferInfo,0);
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
	            {
	                outputBuffers = codec.getOutputBuffers();
	            }
	            else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
	            {
	                MediaFormat format = codec.getOutputFormat();
	            }

				
			}while(outputBufferIndex>=0);
			
			
		}
		public byte[] swapYV12toI420(byte[] yv12bytes, int width, int height) {
		    byte[] i420bytes = new byte[yv12bytes.length];
		    for (int i = 0; i < width*height; i++)
		        i420bytes[i] = yv12bytes[i];
		    for (int i = width*height; i < width*height + (width/2*height/2); i++)
		        i420bytes[i] = yv12bytes[i + (width/2*height/2)];
		    for (int i = width*height + (width/2*height/2); i < width*height + 2*(width/2*height/2); i++)
		        i420bytes[i] = yv12bytes[i - (width/2*height/2)];
		    return i420bytes;
		}
	
		public  byte[] YV12toYUV420PackedSemiPlanar(final byte[] input, final int width, final int height) {
		    /* 
		     * COLOR_TI_FormatYUV420PackedSemiPlanar is NV12
		     * We convert by putting the corresponding U and V bytes together (interleaved).
		     */
		    final int frameSize = width * height;
		    final int qFrameSize = frameSize/4;
		    byte[] output=new byte[input.length];

		    System.arraycopy(input, 0, output, 0, frameSize); // Y

		    for (int i = 0; i < qFrameSize; i++) {
		        output[frameSize + i*2] = input[frameSize + i + qFrameSize]; // Cb (U)
		        output[frameSize + i*2 + 1] = input[frameSize + i]; // Cr (V)
		    }
		    return output;
		}
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
		Log.d(TAG,"Preview started.");
	}

	@Override
	public void onSessionConfigured() {
		// TODO Auto-generated method stub
		
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
			if(v.getId()==R.id.sessionbtn)
			{
			//	msession.configure();
			//	msession.startPreview();
			}
		}
		
	}
}
