package com.example.androidsns;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServiceHandler extends Service {

	
	//================================
	public static String myid;
	//=================================
	//Server와의 접속 위한.....
	public static ServerSocket listssoc;
	public static Socket listsoc;
	public static PrintWriter spw;
	public static BufferedReader sbr;
	DatagramSocket dsoc;
	String listHost="203.246.112.200";
	int PORT=52273;
	Thread thread1,thread2,thread3;
	//===================================
	
	//chatting 할 때
	public ServerSocket chatssoc;
	public static Socket chatsoc;
	public static  PrintWriter cspw;
	public static  BufferedReader csbr;
	
	//media call socket
	public ServerSocket mediassoc;
	public static Socket mediasoc;
	//
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent,int flags,int startId){
		Log.i("Mytag","onStartCommand accepted!!!!!!");
		thread1=new Thread(new Runnable(){
			@Override
			public void run(){
				Log.i("Mytag","rUN");
				try{
					listsoc=new Socket(listHost,PORT);
					spw=new PrintWriter(listsoc.getOutputStream(),true);
					sbr=new BufferedReader(new InputStreamReader(listsoc.getInputStream()));
					}catch(Exception e){
						
						
					}
				
			}
		});
		thread1.start();
		
		//
		//화상 통화
		
		//
		
		return super.onStartCommand(intent, flags, startId);
	}
	public void onCreate()
	{
		Log.i("Mytag","on create() before accepted!!");
		
		
		//listSoket 관리
		
		//server와의 접속
		//1:1 문자 채팅
		//chatting !!
		
				thread2=new Thread(new Runnable(){
					@Override
					public void run(){
						try{
							chatssoc=new ServerSocket(9190);
							chatsoc=chatssoc.accept();
							Log.i("Mytag","chat accepted");
							csbr=new BufferedReader(new InputStreamReader(chatsoc.getInputStream()));
						}catch(Exception e){}
							if(isActivityTop("com.example.androidsns.ChattingRoomActivity")==false){
								Intent i=new Intent(getApplicationContext(),ChattingRoomActivity.class);
								i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(i);}
							else
							{
								
							}
							
					}
					
				});
				
				thread2.start();
		//1:1 화상 통화
				thread3=new Thread(new Runnable(){
					@Override
					public void run(){
						try{
							//dsoc=new DatagramSocket(9191);
							mediassoc=new ServerSocket(9191);
							mediasoc=mediassoc.accept();
							Log.i("Mytag","media call  accepted");
						}catch(Exception e){}
							if(isActivityTop("com.example.androidsns.FrameActivity")==false){
								Intent i=new Intent(getApplicationContext(),FrameActivity.class);
								i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(i);}
							else
							{
								
							}
							
					}
					
				});
				
				//thread3.start();
	}
	@Override
	public void onDestroy(){
	}

	
	private boolean isActivityTop(String arg){

    	ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List<RunningTaskInfo> info;

        info = activityManager.getRunningTasks(1);

        if(info.get(0).topActivity.getClassName().equals(arg)) {

             return true;

        } else {

             return false;

        }

    }
}
