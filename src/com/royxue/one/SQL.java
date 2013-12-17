package com.royxue.one;

import java.net.MalformedURLException;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.*;


public class SQL extends Activity {
	
	private MobileServiceClient mClient;
	private MobileServiceTable<SQLDemo>  mTable;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sql);
		
			try {
				mClient = new MobileServiceClient("https://sqltest.azure-mobile.net/","OsONApcYUZHaehZcFHNNMEZEQinLvC30",this);
				mTable = mClient.getTable(SQLDemo.class);	
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			final EditText nameIn = (EditText) findViewById(R.id.name);
			Button in = (Button)findViewById(R.id.in);
			final EditText idOut = (EditText)findViewById(R.id.id_out);
			Button out = (Button)findViewById(R.id.out);
			final TextView nameOut = (TextView) findViewById(R.id.name_out);
			final SQLDemo sql = new SQLDemo();
			final int num = 1;
			in.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						String text= new String(nameIn.getText().toString());
						sql.setName(text);
						//sql.setId(num);
						mTable.insert(sql, new TableOperationCallback<SQLDemo>() {

							public void onCompleted(SQLDemo entity, Exception exception, ServiceFilterResponse response) 
							{
								if(exception == null)
								{
									nameIn.setText("");
									Toast.makeText(SQL.this, "insert done" ,Toast.LENGTH_SHORT ).show();
									
								}
								else
								{
									Toast.makeText(SQL.this, "insert failed" ,Toast.LENGTH_SHORT ).show();
								}
							}
						});
						
						
						
					}
				});
			
			out.setOnClickListener(new OnClickListener()
			{
				@Override
		    	public void onClick(View view)
		    	{
		    		String id= new String(idOut.getText().toString());
		    		nameOut.setText(mTable.getTableName());
		    	}
			});
	}

	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sql, menu);
		return true;
	}

}
