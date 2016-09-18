package com.argus.caller.recordervice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class broadcastReceiver extends BroadcastReceiver {// BroadcastReceiver

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			System.out.println(Intent.ACTION_NEW_OUTGOING_CALL);
			Constant.numberToCall = intent
					.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		}
		Intent service = new Intent(context,phoneService.class);
		context.startService(service);

	}

}
