package com.royxue.one;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class BtService {
	
	private static final String NAME = "BluetoothChat";
	//SDP create server socket
	//private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	//unique UUID
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	//Member fields
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;
	
	
	
	
	
	
	//current Connection State
	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN=1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;
    
	//prepare  new BtMain session
	//Handler send back to UI
	public BtService(Context context,Handler handler)
	{
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = handler;
	}
	
	//set chat connection current state
	private synchronized void setState(int state)
	{
		mState = state;
		//state to handler then to UI
		mHandler.obtainMessage(BtMain.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}
	
	//return to current state
	public synchronized int getState()
	{
		return mState;
	}
	
	//start chat service AcceptThread begin session in listenning mode.called by onresume()
	public synchronized void start()
	{
		//cancel other thread
		if(mConnectThread!=null)
		{
			mConnectThread.cancel();
			mConnectThread = null;
		}
		
		if(mConnectedThread!=null)
		{
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		
		//start thread listening to BTServerSocker
		if(mAcceptThread == null )
		{
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		}
		setState(STATE_LISTEN);
	}

	//start ConnecetThread to initiate a connection to remote device
	public synchronized void connect(BluetoothDevice device)
	{
		//cancel any thread attempting to make a connection
		if(mState == STATE_CONNECTING)
		{
			if(mConnectThread!=null)
			{
				mConnectThread.cancel();
				mConnectThread=null;
			}
		}
		
		//cancel any thread running a connection
		if(mConnectedThread!=null)
		{
			mConnectedThread.cancel();
			mConnectedThread=null;
		}
		
		//start thread to connect device
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}
	
	//start ConnectedThread to begin managing a Bluetooth connection
	public synchronized void connected(BluetoothSocket socket,BluetoothDevice device)
	{
		//cancel thread that completed the connection
		if(mConnectThread!= null)
		{
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		
		//cancel accept thread when only connecet one device
		if(mAcceptThread != null)
		{
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
		//start thread to manage connection
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();
		
		//send the device name back to UI
		Message msg = mHandler.obtainMessage(BtMain.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(BtMain.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);
		setState(STATE_CONNECTED);
	}
	
	//Stop all threads
	public synchronized void stop()
	{
		if(mConnectThread!=null)
		{
			mConnectThread.cancel();
			mConnectThread = null ;
		}
		if(mConnectedThread != null)
		{
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		if(mAcceptThread != null )
		{
			mAcceptThread.cancel();
			mAcceptThread = null ;
		}
		setState(STATE_NONE);
	}
	
	//write to connectedThread
	public void write(byte[] out)
	{
		//create tempory object
		ConnectedThread r;
		//synchronize a copy of ConnectedThread
		synchronized(this)
		{
			if(mState!=STATE_CONNECTED) 
				return;
			r = mConnectedThread;
		}
		//perform write unsynchronized
		r.write(out);
	}
	
	//indicate that the connection attempt failed and notify UI
	private void connectionFailed()
	{
		setState(STATE_LISTEN);
		
		//send a failure message back to Activity
		Message msg = mHandler.obtainMessage(BtMain.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(BtMain.TOAST, "Unable to connect device");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}
	
	//indicate connection lost & notify to UI
	private void connectionLost()
	{
		setState(STATE_LISTEN);
		//send a faliure back to Activity
		Message msg = mHandler.obtainMessage(BtMain.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(BtMain.TOAST, "Device Connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}
	
	
	//Acceptthread listening 4 incoming connection
		private class AcceptThread extends Thread
		{
			//local server socket
			private final BluetoothServerSocket mmServerSocket;
			
			public AcceptThread()
			{
				BluetoothServerSocket tmp = null;
				//create listenning socket
				try
				{
					tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
				}
				catch(IOException e)
				{}
				mmServerSocket = tmp;
			}
			
			@Override
			public void run()
			{
				setName("AcceptThread");
				BluetoothSocket socket = null;
				//if not connected
				while(mState != STATE_CONNECTED)
				{
					try{
						socket = mmServerSocket.accept();
					}
					catch(IOException e)
					{
						break;
					}
					
					//if connection accept
					if(socket!= null)
					{
						synchronized (BtService.this)
						{
							switch(mState)
							{
							case STATE_LISTEN:
							case STATE_CONNECTING:
								//start connected
								connected(socket,socket.getRemoteDevice());
								break;
							case STATE_NONE:
							case STATE_CONNECTED:
								//terminate new socket
								try
								{
									socket.close();
								}
								catch(IOException e)
								{}
								break;
							}
						}
					}
				}
			}
			
			public void cancel()
			{
				try 
				{
					mmServerSocket.close();
				}
				catch(IOException e)
				{
					
				}
			}
			
		}
		
		//thread attempt make outgo connection
		
		private class ConnectThread extends Thread
		{
			private final BluetoothSocket mmSocket;
			private final BluetoothDevice mmDevice;
			
			public ConnectThread(BluetoothDevice device)
			{
				mmDevice = device;
				BluetoothSocket tmp = null;
				
				//get a socket 4 connection with given BTD
				try
				{
					tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
				}
				catch(IOException e)
				{}
				mmSocket = tmp;			
			}
			
			@Override
			public void run()
			{
				setName("ConnectThread");
				//cancel discovery
				mAdapter.cancelDiscovery();
				
				//connect to BluetoothSocket
				try
				{
					//only return successful connection
					mmSocket.connect();
				}
				catch(IOException e)
				{
					connectionFailed();
					try
					{
						mmSocket.close();
					}
					catch(IOException e2)
					{}
					//start service over to restart listening mode
					BtService.this.start();
					return;
				}
				
				//reset the ConnectThread because we`re done
				synchronized (BtService.this)
				{
					mConnectThread = null ;
				}
				
				
				//start the connected thread
				connected(mmSocket,mmDevice);
			}
			
			public void cancel()
			{
				try
				{
					mmSocket.close();
				}
				catch(IOException e)
				{}
			}
		}
		
		
	//run when running with a connection a remote device
		private class ConnectedThread extends Thread
		{
			private final BluetoothSocket mmSocket;
			private final InputStream mmInStream;
			//private final InputStream mmInStream;
			private final OutputStream mmOutStream;
			
			public ConnectedThread (BluetoothSocket socket)
			{
				mmSocket= socket;
				InputStream tmpIn= null;
				OutputStream tmpOut = null;
				
				//get bluetoothSocket input and output streams
				try
				{
				tmpIn=socket.getInputStream();
				tmpOut=socket.getOutputStream();
				}
				catch(IOException e)
				{}
				mmInStream = tmpIn;
				mmOutStream = tmpOut;
			}
			
			@Override
			public void run()
			{
				byte[] buffer = new byte[1024];    
				int bytes;
				//keep listening to inputStream
				while(true)
				{
					try
					{
						//ConnectedThread.sleep(1000);
						//read from InputStream
						bytes = mmInStream.read(buffer);
						//send bytes to UI
						mHandler.obtainMessage(BtMain.MESSAGE_READ, bytes , -1,buffer).sendToTarget();
					}
					catch(IOException e)
					{
						connectionLost();
						break;
					}
				}
				try {  
                    ConnectedThread.sleep(1000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
			}
			
			//write to connected OutStream
			public void write(byte[] buffer){
			try
			{
				mmOutStream.write(buffer);
				//Share message to UI
				mHandler.obtainMessage(BtMain.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
			}
			catch(IOException e)
			{}
		}
			
			public void cancel()
			{
			try
			{
				mmSocket.close();
			}
			catch(IOException e)
			{}
			}
		}


}	



