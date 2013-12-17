package com.royxue.one;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaPlayer;
import android.os.Bundle;

public class AlarmDo extends Activity {
	
	MediaPlayer alarmMusic;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		alarmMusic = MediaPlayer.create(this, R.raw.alarm);
		alarmMusic.setLooping(true);
		alarmMusic.start();
		
		new AlertDialog.Builder(AlarmDo.this).setTitle("ƒ÷÷”").setMessage("ƒ÷÷” ±º‰µΩ!").setPositiveButton("Õ£÷πƒ÷÷”",new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				alarmMusic.stop();
				AlarmDo.this.finish();
				
				}	
			}
		).show();
	}
}
