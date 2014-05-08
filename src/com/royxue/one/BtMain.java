package com.royxue.one;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BtMain extends Activity {

	// message type
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// key name
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static final int GET_SETTIME = 3;

	// Layout
	private TextView mTitle;
	private ListView mConversationView;
	private EditText mOutEditText;
	private Button mSendButton;

	// define sth
	private String mConnectedDeviceName = null;
	private ArrayAdapter<String> mConversationArrayAdapter;
	// buffer
	private StringBuffer mOutStringBuffer;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BtService mChatService = null;
	
	SimpleDateFormat sdf= new SimpleDateFormat("hh:mm");
	
	String Bufff ="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bt_main);
		mTitle = (TextView) findViewById(R.id.device_state);
		// get adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth Not Available", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// first check BT state
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		// BT is on
		else {
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (mChatService != null) {
			if (mChatService.getState() == BtService.STATE_NONE) {
				mChatService.start();
			}
		}
	}

	private void setupChat() {
		mConversationArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.bt_message);
		mConversationView = (ListView) findViewById(R.id.in);
		mConversationView.setAdapter(mConversationArrayAdapter);

		// initialize compose field with a listener for return key
		mOutEditText = (EditText) findViewById(R.id.edit_text_out);
		mOutEditText.setOnEditorActionListener(mWriteListener);

		// initialize button
		mSendButton = (Button) findViewById(R.id.send);
		mSendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView view = (TextView) findViewById(R.id.edit_text_out);
				String message = view.getText().toString();
				sendMessage(message);
			}
		});
		// initialize BtService
		mChatService = new BtService(this, mHandler);
		mOutStringBuffer = new StringBuffer("");
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mChatService != null) {
			mChatService.stop();
		}
	}

	public void ensureDiscoverable() {
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	// send message

	private void sendMessage(String message) {
		if (mChatService.getState() != BtService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_LONG)
					.show();
			return;
		}

		if (message.length() > 0) {
			byte[] send = message.getBytes();
			mChatService.write(send);

			// reset buffer and clean edittext
			mOutStringBuffer.setLength(0);
			mOutEditText.setText(mOutStringBuffer);
		}
	}

	// Edittext action listener , listen for return key
	private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() 
	{
		public boolean onEditorAction(TextView view,int actionId,KeyEvent event) {
			// if action is press on return key send message
			if (actionId == EditorInfo.IME_NULL	&& event.getAction() == KeyEvent.ACTION_UP)
				;
			{
				String message = view.getText().toString();
				sendMessage(message);
			}
			return true;
		}
	};
	
   
	// Handler get back information from BTCS
	private final Handler mHandler = new Handler() {
		//public String readBuffer = null;
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BtService.STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					mConversationArrayAdapter.clear();
					break;
				case BtService.STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case BtService.STATE_LISTEN:
					mTitle.setText(R.string.title_listening);
				case BtService.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// consruct string from buffer
				String writeMessage = new String(writeBuf);
				mConversationArrayAdapter.add("Me:" + writeMessage);//+ "@"+sdf.format(new Date()));
				break;
			case MESSAGE_READ:
				//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				byte[] readBuf = (byte[]) msg.obj;
				String readMessage =new String(readBuf,0,msg.arg1);
				//String readBuffer = null;
				mConversationArrayAdapter.add(mConnectedDeviceName + ":" + readMessage);				
				// + "@"+sdf.format(new Date()));
				break;
			case MESSAGE_DEVICE_NAME:
				// save device name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to" + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// DEVICElist return a device
			if (resultCode == Activity.RESULT_OK) {
				// get Mac address
				String address = data.getExtras().getString(BtDeviceList.EXTRA_DEVICE_ADDRESS);
				//get BT OBJECT
				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
				mChatService.connect(device);
				
			} 
			break;
		case REQUEST_ENABLE_BT:
			if(resultCode == Activity.RESULT_OK)
				{
				setupChat();
				}else {
				// BT is off
				Toast.makeText(this, R.string.bt_off, Toast.LENGTH_SHORT).show();
				finish();
			}
		case GET_SETTIME:
			Bundle setTime = data.getExtras();
			String stime = setTime.getString("time");
			sendMessage(stime);
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.bt_main, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.scan:
			//launch device list
			Intent serverIntent = new Intent(this,BtDeviceList.class);
			startActivityForResult(serverIntent,REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.discoverable:
			ensureDiscoverable();
			return true;
		}
		return false;
	}
}

