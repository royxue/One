package com.royxue.one;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Entrance extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entrance);
		Button bt = (Button) findViewById(R.id.bt);
		Button map = (Button) findViewById(R.id.map);
		Button sql = (Button) findViewById(R.id.sql);
		Button alarm = (Button) findViewById(R.id.alarm);
		
		bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Entrance.this,BtMain.class);
				startActivity(intent);
			}
		});
		map.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Entrance.this,Map.class);
				startActivity(intent);
			}
		});
		sql.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Entrance.this,SQL.class);
				startActivity(intent);
			}
		});
		alarm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Entrance.this,Alarm.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entrance, menu);
		return true;
	}

}
