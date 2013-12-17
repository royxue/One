package com.royxue.one;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

//list BT devices
//send back device MAC address
public class BtDeviceList extends Activity {
	
	final String Carname = "ATK-HC05";
	public static int rssi[]={0,0};
	
	//return intent extra
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	
	//member fields
	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mPairedDeviceArrayAdapter;
	private ArrayAdapter<String> mNewDeviceArrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//setup windows
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.bt_device_list);
		
		//cancel result when back out
		setResult(Activity.RESULT_CANCELED);
		
		//initialize button scan
		Button scan =(Button) findViewById(R.id.scanit);
		scan.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				doDiscovery();
				v.setVisibility(View.GONE);
			}
		});
		//initial 2 arrayadpter
		//one paired one new
		mPairedDeviceArrayAdapter = new ArrayAdapter<String>(this,R.layout.bt_device_name);
		mNewDeviceArrayAdapter = new ArrayAdapter<String>(this,R.layout.bt_device_name);
		
		//setup paired
		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(mPairedDeviceArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);
		
		//setup new
		ListView newListView = (ListView) findViewById(R.id.new_devices);
		newListView.setAdapter(mNewDeviceArrayAdapter);
		newListView.setOnItemClickListener(mDeviceClickListener);
		
		//register broadcasts device found
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver,filter);
		
		//register when discovery finish
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver,filter);
		
		//get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		
		//get set of currently paired device
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		
		//add paired devices
		if(pairedDevices.size()>0)
		{
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for(BluetoothDevice device:pairedDevices)
			{
				mPairedDeviceArrayAdapter.add(device.getName()+"\n"+device.getAddress());				
			}
		}
		else 
		{
			String noDevices = getResources().getText(R.string.none_paired).toString();
			mPairedDeviceArrayAdapter.add(noDevices);
		}	
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//not doing discovery
		if(mBtAdapter!=null)
		{
			mBtAdapter.cancelDiscovery();
		}
		
		//unregister broadcast listeners
		this.unregisterReceiver(mReceiver);
	}
	
	//start dicover with BtAdapter
	private void doDiscovery()
	{
		//indicate scanning in the title
		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scanning);
		
		//Turn on sub-title 4 new devices
		findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
		
		//if now is dicoverying stop
		if(mBtAdapter.isDiscovering())
		{
			mBtAdapter.cancelDiscovery();
		}
	}
		
		//on-click listener 4 devices in the ListViews
		//private OnItemClickListener
		private OnItemClickListener mDeviceClickListener = new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> av,View v,int arg2,long arg3)
			{
				//cancel discovery 
				mBtAdapter.cancelDiscovery();
				
				//MAC address last 17 chars in the View
				String info = ((TextView)v).getText().toString();
				String address = info.substring(info.length()-17);
				
				//create result intent and include Mac address
				Intent intent = new Intent();
				intent.putExtra(EXTRA_DEVICE_ADDRESS,address);
				
				//set result & finish Activity
				setResult(Activity.RESULT_OK,intent);
				finish();
			}
		};
		
		//记得要看啊啊啊啊啊
		//BroadReceiver that listens 4 dicovered and change title change when discovery is finish
	    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();

	            // When discovery finds a device
	            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	                // Get the BluetoothDevice object from the Intent
	                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	                // If it's already paired, skip it, because it's been listed already
	                //if (device.getName().equals(Carname)){      //device.getBondState()!= BluetoothDevice.BOND_BONDED) {
	                	if (device.getBondState()!= BluetoothDevice.BOND_BONDED){
	                    mNewDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	                	//mBtAdapter.cancelDiscovery();
	                	//rssi[1]= intent.getExtras().getShort(device.EXTRA_RSSI);
	                }
	            // When discovery is finished, change the Activity title
	            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	                setProgressBarIndeterminateVisibility(false);
	                setTitle(R.string.select_device);
	                if (mNewDeviceArrayAdapter.getCount() == 0) {
	                    String noDevices = getResources().getText(R.string.none_found).toString();
	                    mNewDeviceArrayAdapter.add(noDevices);
	                }
	            }
	        }
	    };
	}


