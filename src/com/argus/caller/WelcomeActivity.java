package com.argus.caller;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.argus.caller.activity.DbTask;
import com.argus.caller.activity.MainActivity;

public class WelcomeActivity extends Activity {

	
	private SharedPreferences preferences;
	private static Handler handler = new Handler();
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		preferences = getSharedPreferences("phone",Context.MODE_PRIVATE);
		DbTask dbTask = new DbTask(this);  
		dbTask.execute();
					
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {//第一次启动
				if (preferences.getBoolean("firststart",true)) {
					editor = preferences.edit();
					editor.putBoolean("firststart",false);
					editor.commit();
					 Intent intent=new Intent();
						intent.setClass(WelcomeActivity.this,YinDaoActivity.class);
						WelcomeActivity.this.startActivity(intent);
						WelcomeActivity.this.finish();
				}else {//第一次启动之后再进入时走这
					SharedPreferences sharedPreferences= getSharedPreferences("pic",Activity.MODE_PRIVATE); 
					String valueText =sharedPreferences.getString("pic", ""); 
			        if(valueText.equals("1")){
			        	Intent intent=new Intent();
						intent.setClass(WelcomeActivity.this,MainActivity.class);
						WelcomeActivity.this.startActivity(intent);
						WelcomeActivity.this.finish();
			        }else{
			        	Intent intent=new Intent();
						intent.setClass(WelcomeActivity.this,DiaLogActivity.class);
						WelcomeActivity.this.startActivity(intent);
						WelcomeActivity.this.finish();
			        }
					 
				}
			}
		},500);
	}

}
