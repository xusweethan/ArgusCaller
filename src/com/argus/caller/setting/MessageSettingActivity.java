package com.argus.caller.setting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.argus.caller.R;









import com.argus.caller.activity.DatabaseHelper;
import com.argus.caller.activity.PersonalActivity;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MessageSettingActivity extends Activity {
	private ImageButton back,clean;
	private Spinner spinner1, spinner2, spinner3;
	private String UserPhone,RecordFile,number;
	private TextView MB, SIZE;
	private String mb;
	private long size;
	///////////////////////////
	private RadioGroup group;
	private TextView tv;
	private ImageButton RecordingPath;
	private TextView filePath;
	private int IsLock;
	private ScrollView scrollView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_messagesetting);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		back = (ImageButton) findViewById(R.id.back);
		MB = (TextView) findViewById(R.id.MB);
		RecordingPath=(ImageButton) findViewById(R.id.RecordingPath);
		filePath=(TextView) findViewById(R.id.filepath);
		tv=(TextView) findViewById(R.id.tpfs);
		SIZE = (TextView) findViewById(R.id.size);

		clean=(ImageButton) findViewById(R.id.run1);
		
		
		SharedPreferences preference = getSharedPreferences("text1",
				Context.MODE_PRIVATE);
		String valueText = preference.getString("text1", null);// 如果取不到值就取值后面的参数
		if (!(valueText == null)) {
			File file = new File(valueText);
			try {
				try {
					mb = GetFileSizeUtil.getInstance().FormetFileSize(
							GetFileSizeUtil.getInstance().getFileSize(file));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MB.setText(mb);
			size = GetFileSizeUtil.getInstance().getlist(file);
			SIZE.setText(Long.toString(size));
		}

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MessageSettingActivity.this.finish();
			}
		});
	RecordingPath.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(MessageSettingActivity.this, FileBrowerActivity.class);
				startActivity(intent);
				MessageSettingActivity.this.finish();
			}
		});
	clean.setOnClickListener(new OnClickListener() {
		private String pa;
		private String pb;
		private String spStr[];
		SharedPreferences preference = getSharedPreferences(
				"text2", Context.MODE_PRIVATE);
		String valueText2 = preference.getString("text2", null);
		File fl=new File(valueText2);//normal
		@Override
		public void onClick(View v) {
			//Toast.makeText(MessageSettingActivity.this, "What do you want", Toast.LENGTH_LONG).show();
			
			String path1=fl+"/";
			Log.v("name",path1+"/");
			
			getAllFiles(fl);
		}
		  private void getAllFiles(File fl){  
		        File files[] = fl.listFiles();  
		        if(files != null){  
		            for (File f : files){  
		                if(f.isDirectory()){  
		                    getAllFiles(f);  
		                }else{  
		                	
		                   // System.out.println(f);  
		                 pa=f.getPath();
		                String spStr[] = pa.split("/");
		                int k =  spStr.length;
		                spStr[k-2].replaceAll(".3gp", "");
		                String number = spStr[k-2];
		                String file = spStr[k-1];
		                System.out.println(spStr[k-2]);
		                DatabaseHelper database = new DatabaseHelper(getBaseContext());// 这段代码放到Activity类中才用this
		        		SQLiteDatabase db = null;
		        		db = database.getReadableDatabase();
		        		Cursor d = db.rawQuery("select * from calllog where recordfile = ? and UserPhone = ?", new String[]{file,number});
		        		if (d.moveToFirst()) {
							IsLock = d.getInt(d.getColumnIndex("IsLock"));
							if(IsLock==1){
								Log.v("deleteall","IsLock=1"+file+"+"+number);
							}else{
								Log.v("deleteall","IsLock=0"+file+"+"+number);
								f.delete();//删除文件
								db.delete("calllog", "recordfile=? and UserPhone = ?", new String[] {file,number});
							}
						}else{
							Log.v("deleteall","null"+file+"+"+number);
							f.delete();//删除文件
						}d.close();
						db.close();
		                }  
		            }  
		        }
		    } 
	});
		/*
		 * spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
		 * 
		 * @Override public void onItemSelected(AdapterView<?> arg0, View arg1,
		 * int arg2, long arg3) { // TODO Auto-generated method stub
		 * Toast.makeText(MessageSettingActivity.this,
		 * arg0.getItemAtPosition(arg2).toString(), Toast.LENGTH_LONG).show();
		 * 
		 * }
		 * 
		 * @Override public void onNothingSelected(AdapterView<?> arg0) { //
		 * TODO Auto-generated method stub
		 * 
		 * } });
		 */
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
				MessageSettingActivity.this,
				android.R.layout.browser_link_context_header, getData2());
		spinner2.setAdapter(adapter1);
	}


	private List<String> getData2() {
		List<String> dataList2 = new ArrayList<String>();
		dataList2.add("100MB");
		dataList2.add("150MB");
		dataList2.add("200MB");
		return dataList2;
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
			MessageSettingActivity.this.finish();
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
