package com.argus.caller;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.argus.caller.activity.MainActivity;

public class DiaLogActivity extends Activity implements OnClickListener {
	
	private TextView declare;
	private CheckBox checkBox;
	private Button affirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dia_log);

		initViews();
		affirm.setEnabled(false);//不可点击状态
		checkBox.setChecked(false);//未选择状态
		checkBox.setText(Html.fromHtml("<u>" + "我已阅读此协议" + "</u>" ));//下划线
		checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkBox.isChecked()) {//checkbox处于选择状态
					affirm.setEnabled(true);//可点击
				}else {
					affirm.setEnabled(false);
				}
			}
		});
	}

	private void initViews() {
		declare = (TextView) findViewById(R.id.tv_declare); 
		checkBox = (CheckBox)findViewById(R.id.checkBox_declare);
		affirm = (Button)findViewById(R.id.bt_affirm);
		findViewById(R.id.bt_affirm).setOnClickListener(this);
		findViewById(R.id.bt_exit).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_affirm://确认
			SharedPreferences mySharedPreferences= getSharedPreferences("pic", Activity.MODE_PRIVATE); 
			SharedPreferences.Editor editor = mySharedPreferences.edit(); 
			editor.putString("pic", "1"); 
			editor.commit(); 
			
			Intent intent=new Intent();
			intent.setClass(DiaLogActivity.this,MainActivity.class);
			DiaLogActivity.this.startActivity(intent);
			finish();
			break;
		case R.id.bt_exit://退出
			finish();
			break;
		}
	}

}
