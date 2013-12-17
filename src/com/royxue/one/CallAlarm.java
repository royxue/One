package com.royxue.one;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CallAlarm extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "Ìø×ªÔÚ´Ë", Toast.LENGTH_SHORT).show();
		intent.setClass(context, AlarmDo.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
		
	}

}
