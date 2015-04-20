package com.example.androidsns;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

public class MediaActivity extends ActionBarActivity implements SurfaceHolder.Callback, Camera.PictureCallback{

	private SurfaceView sv;
	private SurfaceHolder sh;
	private Camera mcamera;
	
	//encoder
	MediaCodec codec;
	MediaFormat mediaFormat;
	ByteBuffer[] inputBuffers;
	ByteBuffer[] outputBuffers;
	InputStream in;
	OutputStream out;
	FileOutputStream fos;
	
	
	//remote
	//private SurfaceView remotesv;
	//private SurfaceHolder remotesh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media);
		//initCodec();
		sv=(SurfaceView)findViewById(R.id.preview);
		//remotesv=(SurfaceView)findViewById(R.id.remote);
		sh=sv.getHolder();
		//remotesh=remotesv.getHolder();
		//sv.getHolder().setFixedSize(720, 480);
	//	remotesv.getHolder().setFixedSize(720, 480);
		sh.addCallback(this);
		//remotesh.addCallback(this);
		sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		//remotesh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.media, menu);
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
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try{
			mcamera=Camera.open(1);
			mcamera.setPreviewDisplay(sh);
			mcamera.setDisplayOrientation(90);
			//재정의
			mcamera.setPreviewCallback(new PreviewCallback(){
				@Override
				public void onPreviewFrame(byte[] data, Camera camera){
					//encode(data);
				}
			});
			
			
			
			Parameters param=mcamera.getParameters();
			
			param.setRotation(90);
			
			
			
			param.setFlashMode(Parameters.FLASH_MODE_AUTO);
			param.setFocusMode(Parameters.FOCUS_MODE_AUTO);
			param.setColorEffect(Parameters.EFFECT_AQUA);
			mcamera.setParameters(param);
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Camera.Parameters parameters=mcamera.getParameters();
		//parameters.setPreviewSize(width/2, height/2);
		mcamera.setParameters(parameters);
		mcamera.startPreview();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mcamera.stopPreview();
		mcamera.release();
		mcamera=null;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		
	}
	
	class SocketHandler extends Thread
	{
		public void run()
		{
			initCodec();
		}
		void initCodec()
		{
			codec=MediaCodec.createEncoderByType("video/avc");
			mediaFormat=MediaFormat.createVideoFormat("video/avc", 320, 240);
			mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,125000);
			mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE,15);
			mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
			mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
			codec.configure(mediaFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
			codec.start();
			inputBuffers=codec.getInputBuffers();
			outputBuffers=codec.getOutputBuffers();
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
		        int wh4 = data.length/6; //wh4 = width*height/4
		        byte tmp;
		        for (int i=wh4*4; i<wh4*5; i++)
		         {
		            tmp = data[i];
		            data[i] = data[i+wh4];
		            data[i+wh4] = tmp;
		         }
				
				
				//inputBuffer.put(data);
		        //컬러 매치를 위해서
		        inputBuffer.put(data, 0, wh4*4);
	            inputBuffer.put(data, wh4*5, wh4);
	            inputBuffer.put(data, wh4*4, wh4);
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
					outBuffer.get(outData);
					try{
						if(bufferInfo.offset!=0)
						{
							//file에 쓰는 부분
							//fos.write(outData, bufferInfo.offset, outData.length
	                          //      - bufferInfo.offset);
							Socket a=new Socket("127.0.0.1",9190);
							

						}
						else
						{
							//fos.write(outData, 0, outData.length);
						}
						//flush자리     fos.flush();
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
	}
	/*
	void initCodec()
	{
		codec=MediaCodec.createEncoderByType("video/avc");
		mediaFormat=MediaFormat.createVideoFormat("video/avc", 320, 240);
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,125000);
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE,15);
		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
		mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
		codec.configure(mediaFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
		codec.start();
		inputBuffers=codec.getInputBuffers();
		outputBuffers=codec.getOutputBuffers();
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
	        int wh4 = data.length/6; //wh4 = width*height/4
	        byte tmp;
	        for (int i=wh4*4; i<wh4*5; i++)
	         {
	            tmp = data[i];
	            data[i] = data[i+wh4];
	            data[i+wh4] = tmp;
	         }
			
			
			//inputBuffer.put(data);
	        //컬러 매치를 위해서
	        inputBuffer.put(data, 0, wh4*4);
            inputBuffer.put(data, wh4*5, wh4);
            inputBuffer.put(data, wh4*4, wh4);
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
				outBuffer.get(outData);
				try{
					if(bufferInfo.offset!=0)
					{
						//file에 쓰는 부분
						//fos.write(outData, bufferInfo.offset, outData.length
                          //      - bufferInfo.offset);
						Socket a=new Socket("127.0.0.1",9190);
						

					}
					else
					{
						//fos.write(outData, 0, outData.length);
					}
					//flush자리     fos.flush();
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
	*/
	class onClickHandler implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	}
}
