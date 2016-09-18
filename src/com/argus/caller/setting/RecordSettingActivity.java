package com.argus.caller.setting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.argus.caller.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RecordSettingActivity extends Activity{
	private Spinner spinner1;
	private RadioGroup group;
	private TextView tv;
	private ImageButton RecordingPath;
	private TextView filePath;
	private ImageButton back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_recordsetting);
		spinner1=(Spinner) findViewById(R.id.spinner1);
		group=(RadioGroup) findViewById(R.id.radioGroup1);
		back=(ImageButton) findViewById(R.id.back);
		tv=(TextView) findViewById(R.id.tpfs);
		RecordingPath=(ImageButton) findViewById(R.id.RecordingPath);
		//Intent intent = getIntent();
		//String valueText = intent.getStringExtra("text");
		filePath=(TextView) findViewById(R.id.filepath);
		//filePath.setText(valueText);
		
		
		
		ArrayAdapter<String>adapter=new ArrayAdapter<String>(RecordSettingActivity.this,android.R.layout.browser_link_context_header,getData());
		spinner1.setAdapter(adapter);
		
		/*spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(RecordSettingActivity.this, parent.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});*/
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int radioButtonId=group.getCheckedRadioButtonId();
				RadioButton rb=(RadioButton) RecordSettingActivity.this.findViewById(radioButtonId);
				tv.setText("you choose : "+rb.getText());
			}
		});
		RecordingPath.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(RecordSettingActivity.this, FileBrowerActivity.class);
				startActivity(intent);
				RecordSettingActivity.this.finish();
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RecordSettingActivity.this.finish();
			}
		});
	}
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences.Editor spfs=getPreferences(0).edit();
		spfs.putString("valueText",filePath.getText().toString() );
		spfs.commit();
	}
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preference = getSharedPreferences("text1", Context.MODE_PRIVATE);   
        String valueText = preference.getString("text1", "");//如果取不到值就取值后面的参数     
        
		SharedPreferences spfs1=getPreferences(0);
		Intent intent = getIntent();
		if(valueText!=null){
			filePath.setText(valueText);
		}
	}
	 
	private List<String> getData() {
		List<String> dataList=new ArrayList<String>();
		dataList.add("Mp3");
		dataList.add("Wav");
		dataList.add("Wma");
		return dataList;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
			RecordSettingActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
}
