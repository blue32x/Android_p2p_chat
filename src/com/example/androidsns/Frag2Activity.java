package com.example.androidsns;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.app.Fragment;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class Frag2Activity extends Fragment implements SurfaceHolder.Callback{

	private SurfaceView sv;
	private SurfaceHolder sh;
	private Camera mcamera;
	
	
	//decoder
	DecoderHandler dhdl=null;
	//FileInputStream fis;
	BufferedInputStream bis;
	private MediaExtractor extractor;
	private MediaCodec decoder;
	private Surface surface;
	private BufferedInputStream is;
	private FileOutputStream fos;
	private FileInputStream fis;
	//DecodeClass Decode=null;
	public static byte[] SPS = null;
    public static byte[] PPS = null;
    byte[] mydata=new byte[10000];
	private PlayerThread mPlayer = null;
	public static final String ENCODING = "h264";
	MediaPlayer mMediaPlayer;
	RingBuffer a=null;
	BlockingQueue<Frame> queue = new ArrayBlockingQueue<Frame>(100);
	//String path=Environment.getExternalStorageDirectory().getAbsolutePath();
	private static final String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/"+"girls.mp4";
	private static final String MOVIE_URL = "http://www.archive.org/download/Unexpect2001/Unexpect2001_512kb.mp4";
	
	private static class Frame
    {
        public int id;
        public byte[] frameData;

        public Frame(int id)
        {
            this.id = id;
        }
    }
	
		@Override

		 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			View v=inflater.inflate(R.layout.frag2, container,false);
			sv=(SurfaceView)v.findViewById(R.id.remotepreview);	
			sh=sv.getHolder();	
			sh.addCallback(this);
			sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		  return v;

		 }

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stu
			if (mPlayer == null) {
			///	mPlayer = new PlayerThread(holder.getSurface());
			//	mPlayer.start();
			}
		//	if(dhdl==null)
		//	{
		//		dhdl=new DecoderHandler(holder.getSurface());
		//		dhdl.start();
		//	}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
		//	if (mPlayer != null) {
		//		mPlayer.interrupt();
		//	}
			if(dhdl!=null){
			//	dhdl.interrupt();

			}
		}
		//media player
		class Mplayer extends Thread
		{
			public void run()
			{
				
			}
		}
		//only to do decoding..
		private class DecoderHandler extends Thread
		{
			Surface surface;
			DecoderHandler(Surface surface)
			{
				this.surface=surface;
			}
			public void run()
			{
				init();
			}
			
			void init()
			{
				try{
				//FileInputStream in = new FileInputStream(dir);
					bis=new BufferedInputStream(ServiceHandler.mediasoc.getInputStream(),5000);
				String mimeType = "video/avc";
				MediaCodec decoder = MediaCodec.createDecoderByType(mimeType);
				MediaFormat format = MediaFormat.createVideoFormat(mimeType, 640, 480);

				byte[] csd_info = {0,0,0,1,103,66,-128,22,-23,1,64,123,32  };
				byte[] header_pps = { 0, 0, 0, 1,104,-50,6,-30 };
				format.setByteBuffer("csd-0", ByteBuffer.wrap(csd_info));
				format.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
				format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 640 * 480);
				format.setInteger("durationUs", 63446722);

				decoder.configure(format, surface, null, 0);
				decoder.start();

				ByteBuffer[] inputBuffers = decoder.getInputBuffers();
				ByteBuffer[] outputBuffers = decoder.getOutputBuffers();
				BufferInfo info = new BufferInfo();
				boolean isEOS = false;
				long startMs = System.currentTimeMillis();

				while (!Thread.interrupted()) {
				    if (!isEOS) {
				        int inIndex = decoder.dequeueInputBuffer(10000000);
				        if (inIndex >= 0) {
				            byte buffer2[] = new byte[18800 * 8 * 8 * 8];
				            ByteBuffer buffer = inputBuffers[inIndex];
				            int sampleSize;
				            sampleSize = bis.read(buffer2);

				            buffer.clear();
				            Log.i("Mytag","samplesize------------------> "+sampleSize);
				            buffer.put(buffer2, 0, sampleSize);
				            buffer.clear();

				            if (sampleSize < 0) {
				                decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
				                isEOS = true;
				            } else {
				                decoder.queueInputBuffer(inIndex, 0, sampleSize, 0, 0);
				            }
				        }
				    }

				    int outIndex = decoder.dequeueOutputBuffer(info, 10000);
				    switch (outIndex) {
				    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
				        Log.d("DecodeActivity", "INFO_OUTPUT_BUFFERS_CHANGED");
				        outputBuffers = decoder.getOutputBuffers();
				        break;
				    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
				        Log.d("DecodeActivity", "New format " + decoder.getOutputFormat());
				        break;
				    case MediaCodec.INFO_TRY_AGAIN_LATER:
				        Log.d("DecodeActivity", "dequeueOutputBuffer timed out! " + info);
				        break;
				    default:
				        ByteBuffer buffer = outputBuffers[outIndex];
				        Log.v("DecodeActivity", "We can't use this buffer but render it due to the API limit, " + buffer);

				        while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
				            try {
				                sleep(10);
				            } catch (InterruptedException e) {
				                e.printStackTrace();
				                break;
				            }
				        }
				        decoder.releaseOutputBuffer(outIndex, true);
				        break;
				    }

				    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
				        Log.d("DecodeActivity", "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
				        break;
				    }
				}

				decoder.stop();
				decoder.release();
				}catch(Exception e)
				{
					Log.i("Mytag","-------------->exception e "+e.toString());
					
				}
			}
		}		
		private class PlayerThread1 extends Thread 
	    {
	        //private MediaExtractor extractor;
	        private MediaCodec decoder;
	        private Surface surface;

	        public PlayerThread1(Surface surface) 
	        {
	            this.surface = surface;
	        }

	        @Override
	        public void run() 
	        {
	            while(SPS == null || PPS == null || SPS.length == 0 || PPS.length == 0)
	            {
	                try 
	                {
	                    Log.d("EncodeDecode", "DECODER_THREAD:: sps,pps not ready yet");
	                    sleep(1000);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();

	                }
	            }

	            Log.d("EncodeDecode", "DECODER_THREAD:: sps,pps READY");

	            if(ENCODING.equalsIgnoreCase("h264"))
	            {
	                decoder = MediaCodec.createDecoderByType("video/avc");
	                MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", 320, 240);
	                mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(SPS));
	                mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(PPS));
	                decoder.configure(mediaFormat, surface /* surface */, null /* crypto */, 0 /* flags */);
	            }
	            else if(ENCODING.equalsIgnoreCase("h263"))
	            { 
	                decoder = MediaCodec.createDecoderByType("video/3gpp");
	                MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/3gpp", 352, 288);
	                decoder.configure(mediaFormat, surface /* surface */, null /* crypto */, 0 /* flags */);
	            }

	            if (decoder == null) 
	            {
	                Log.e("DecodeActivity", "DECODER_THREAD:: Can't find video info!");
	                return;
	            }

	            decoder.start();
	            Log.d("EncodeDecode", "DECODER_THREAD:: decoder.start() called");

	            ByteBuffer[] inputBuffers = decoder.getInputBuffers();
	            ByteBuffer[] outputBuffers = decoder.getOutputBuffers();


	            int i = 0;
	            while(!Thread.interrupted())
	            {
	                Frame currentFrame = null;
	                try 
	                {
	                    Log.d("EncodeDecode", "DECODER_THREAD:: calling queue.take(), if there is no frame in the queue it will wait");
	                    currentFrame = queue.take();
	                } 
	                catch (InterruptedException e) 
	                {
	                    Log.e("EncodeDecode","DECODER_THREAD:: interrupted while PlayerThread was waiting for the next frame");
	                    e.printStackTrace();
	                }

	                if(currentFrame == null)
	                    Log.e("EncodeDecode","DECODER_THREAD:: null frame dequeued");
	                else
	                    Log.d("EncodeDecode","DECODER_THREAD:: " + currentFrame.id + " no frame dequeued");

	                if(currentFrame != null && currentFrame.frameData != null && currentFrame.frameData.length != 0)
	                {
	                    Log.d("EncodeDecode", "DECODER_THREAD:: decoding frame no: " + i + " , dataLength = " + currentFrame.frameData.length);

	                    int inIndex = 0; 
	                    while ((inIndex = decoder.dequeueInputBuffer(1)) < 0)
	                        ;

	                    if (inIndex >= 0) 
	                    {
	                        Log.d("EncodeDecode", "DECODER_THREAD:: sample size: " + currentFrame.frameData.length);

	                        ByteBuffer buffer = inputBuffers[inIndex];
	                        buffer.clear();
	                        buffer.put(currentFrame.frameData);
	                        decoder.queueInputBuffer(inIndex, 0, currentFrame.frameData.length, 0, 0);

	                        BufferInfo info = new BufferInfo();
	                        int outIndex = decoder.dequeueOutputBuffer(info, 100000);

	                        switch (outIndex) 
	                        {
	                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
	                            Log.e("EncodeDecode", "DECODER_THREAD:: INFO_OUTPUT_BUFFERS_CHANGED");
	                            outputBuffers = decoder.getOutputBuffers();
	                            break;
	                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
	                            Log.e("EncodeDecode", "DECODER_THREAD:: New format " + decoder.getOutputFormat());

	                            break;
	                        case MediaCodec.INFO_TRY_AGAIN_LATER:
	                            Log.e("EncodeDecode", "DECODER_THREAD:: dequeueOutputBuffer timed out!");
	                            break;
	                        default:
	                            Log.d("EncodeDecode", "DECODER_THREAD:: decoded SUCCESSFULLY!!!");
	                            ByteBuffer outbuffer = outputBuffers[outIndex];
	                            decoder.releaseOutputBuffer(outIndex, true);
	                            break;
	                        }
	                        i++;
	                    }
	                }
	            }

	            decoder.stop();
	            decoder.release();

	        }
	    }
		
		class RingBuffer extends Thread
		{
			byte[] data;
			FileOutputStream fos;
			RingBuffer(byte[] a,FileOutputStream b)
			{
				this.data=a;
				this.fos=b;
				try{
				//is=ServiceHandler.mediasoc.getInputStream();
					is=new BufferedInputStream(ServiceHandler.mediasoc.getInputStream());
				}catch(Exception e)
				{}
			}
			@Override
			public void run()
			{
				try{
				//is=ServiceHandler.mediasoc.getInputStream();
				byte[] data=new byte[5000];
				while(is.read(data)>0)
				{
				fos.write(data);
				Log.i("rcv","data length()------------> "+data.length);
				}
				}catch(Exception e)
				{}
			}
		}
		
		
		private class PlayerThread extends Thread {
			private MediaExtractor extractor;
			private MediaCodec decoder;
			private Surface surface;

			public PlayerThread(Surface surface) {
				this.surface = surface;
			}

			@Override
			public void run() {
			//	try{
				//	fos=new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/"+"test1.mp4");
			//	is=new BufferedInputStream(ServiceHandler.mediasoc.getInputStream());
			//	byte[] data=new byte[5000];
			//	int i;
			//	while((i=is.read())>0)
			//	{
			//	fos.write(i);
				
			//	Log.i("rcv","data length()------------> "+data.length);
				//fos.flush();
			//	}
			//	fos.flush();
			//	fos.close();
			//	}catch(Exception e)
			//	{
				//}
				
				
				
				try{

				//ParcelFileDescriptor pfd=ParcelFileDescriptor.fromSocket(ServiceHandler.mediasoc);
				//FileDescriptor fd=pfd.getFileDescriptor();
				extractor = new MediaExtractor();
				extractor.setDataSource("http://192.168.0.5:1935/vod/mp4:girls.mp4/manifest.f4m");
				}catch(Exception e){
					Log.i("MYtag","playexception ------------------>"+e.toString());
				}
				for (int i = 0; i < extractor.getTrackCount(); i++) {
					MediaFormat format = extractor.getTrackFormat(i);
					String mime = format.getString(MediaFormat.KEY_MIME);
					if (mime.startsWith("video/avc")) {
						extractor.selectTrack(i);
						decoder = MediaCodec.createDecoderByType(mime);
						decoder.configure(format, surface, null, 0);
						break;
					}
				}

				if (decoder == null) {
					Log.e("DecodeActivity", "Can't find video info!");
					return;
				}

				decoder.start();

				ByteBuffer[] inputBuffers = decoder.getInputBuffers();
				ByteBuffer[] outputBuffers = decoder.getOutputBuffers();
				BufferInfo info = new BufferInfo();
				boolean isEOS = false;
				long startMs = System.currentTimeMillis();

				while (!Thread.interrupted()) {
					if (!isEOS) {
						int inIndex = decoder.dequeueInputBuffer(10000);
						if (inIndex >= 0) {
							ByteBuffer buffer = inputBuffers[inIndex];
							int sampleSize = extractor.readSampleData(buffer, 0);
							if (sampleSize < 0) {
								// We shouldn't stop the playback at this point, just pass the EOS
								// flag to decoder, we will get it again from the
								// dequeueOutputBuffer
								Log.d("DecodeActivity", "InputBuffer BUFFER_FLAG_END_OF_STREAM");
								decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
								isEOS = true;
							} else {
								decoder.queueInputBuffer(inIndex, 0, sampleSize, extractor.getSampleTime(), 0);
								extractor.advance();
							}
						}
					}

					int outIndex = decoder.dequeueOutputBuffer(info, 10000);
					switch (outIndex) {
					case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
						Log.d("DecodeActivity", "INFO_OUTPUT_BUFFERS_CHANGED");
						outputBuffers = decoder.getOutputBuffers();
						break;
					case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
						Log.d("DecodeActivity", "New format " + decoder.getOutputFormat());
						break;
					case MediaCodec.INFO_TRY_AGAIN_LATER:
						Log.d("DecodeActivity", "dequeueOutputBuffer timed out!");
						break;
					default:
						ByteBuffer buffer = outputBuffers[outIndex];
						Log.v("DecodeActivity", "We can't use this buffer but render it due to the API limit, " + buffer);

						// We use a very simple clock to keep the video FPS, or the video
						// playback will be too fast
						while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
							try {
								sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
								break;
							}
						}
						decoder.releaseOutputBuffer(outIndex, true);
						break;
					}

					// All decoded frames have been rendered, we can stop playing now
					if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
						Log.d("DecodeActivity", "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
						break;
					}
				}

				decoder.stop();
				decoder.release();
				extractor.release();
				
			}
		}
		
}
