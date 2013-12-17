package com.royxue.one;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class Alarm extends Activity {
	
	AlarmManager aManager;
	Calendar currentTime = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm);
		Button set = (Button) findViewById(R.id.set_alarm);
		final TextView show = (TextView) findViewById(R.id.alarm_state);
		//aManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
		
		set.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View source) {
				Calendar currentTime = Calendar.getInstance();
				
				new TimePickerDialog(Alarm.this,0,new TimePickerDialog.OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker tp, int h, int m) {
						
						Intent intent = new Intent(Alarm.this,CallAlarm.class);
						
						PendingIntent pi = PendingIntent.getBroadcast(Alarm.this,0,intent,0);
						
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(System.currentTimeMillis());
						
						c.set(Calendar.HOUR_OF_DAY, h);
						c.set(Calendar.MINUTE,m);
						c.set(Calendar.SECOND,0);  
			            c.set(Calendar.MILLISECOND,0);
			            
			            AlarmManager aManager = (AlarmManager)getSystemService(ALARM_SERVICE);
						aManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pi);
						
						
						
						show.setText(String.valueOf(c.getTimeInMillis()));
						
					}
				},currentTime.get(Calendar.HOUR_OF_DAY),currentTime.get(Calendar.MINUTE),false).show();
				
			}
			
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm, menu);
		return true;
	}

}
