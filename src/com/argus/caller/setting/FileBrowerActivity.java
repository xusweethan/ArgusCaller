package com.argus.caller.setting;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.argus.caller.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileBrowerActivity extends Activity implements OnClickListener,OnItemClickListener{
	
	private TextView mTextView;
	private ListView mListView;
	private ArrayList<File>fileList;
	private MyAdapter adapter;
	private File  currFile;
	private String TAG;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		this.setContentView(R.layout.activity_browser);
		this.mTextView=(TextView) findViewById(R.id.fileview);
		this.mListView=(ListView) findViewById(R.id.lv);
		this.findViewById(R.id.back).setOnClickListener(this);
		this.findViewById(R.id.newfile).setOnClickListener(this);
		this.findViewById(R.id.okbtn).setOnClickListener(this);
		
		
		this.mListView.setOnItemClickListener(this);
		this.fileList=new ArrayList<File>();
		this.adapter = new MyAdapter(this, fileList);
		this.mListView.setAdapter(adapter);
		this.getSDpath();
		this.refreshView();
		try {
			this.getFileSize(currFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getSDpath(){
		boolean sdCardExist=Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if(sdCardExist=true){
			currFile=Environment.getExternalStorageDirectory().getAbsoluteFile();
		}else{
			Toast.makeText(getApplicationContext(),"No SD card inserted!",Toast.LENGTH_LONG ).show();
			
		}
		return currFile.toString();
	}
	//获取文件夹大小
	private long getFileSize(File currFile)throws Exception{
		long size=0;
		File f1[]=currFile.listFiles();
		for(int i=0;i<f1.length;i++){
			if(f1[i].isDirectory()){
				size=size+getFileSize(f1[i]);
			}else{
				size=size+getFileSize(f1[i]);
			}
		}
		Log.i(TAG, "file is"+size);
		return size;
	}
	/**获取文件个数*/
	public long getlist(File currFile){//递归求取文件目录
		long size=0;
		File f1[]=currFile.listFiles();
		size=f1.length;
		for(int i=0;i<f1.length;i++){
			if(f1[i].isDirectory()){
				size=size+getlist(f1[i]);
				size--;
			}
		}
		return size;
		
	}

	private void refreshView() {
		this.fileList.clear();
		File[]f1=currFile.listFiles();
		if(f1!=null){
			for(File f:currFile.listFiles()){
				if(f.isDirectory())//是目录才加入，过滤掉不是目录的文件
					this.fileList.add(f);
			}
			this.adapter.notifyDataSetChanged();
			this.mTextView.setText(currFile.getPath());
		}
		
	}
	


	@Override
	public void onItemClick(AdapterView<?> pa, View v, int po, long id) {
		this.currFile = fileList.get(po);
		this.refreshView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			
			File parent=this.currFile.getParentFile();
			/*if(parent==null)
				return;*/
			if(!currFile.equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
				this.currFile=parent;
				this.refreshView();
			}else{
				setResult(RESULT_CANCELED);
				this.finish();
			}
			
			break;
		case R.id.newfile:
			AlertDialog.Builder dialog=new Builder(this);
			dialog.setMessage("New File");
			final EditText et=new EditText(this);
			dialog.setView(et);
			dialog.setPositiveButton("Cancle", null);
			dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					File f=new File(currFile,et.getText().toString());
					if(f.mkdir()){
						refreshView();
						return;
					}
					Toast.makeText(getApplicationContext(), "Folder creation failed!",Toast.LENGTH_SHORT).show();
				}
			});
			dialog.show();
			break;
		case R.id.okbtn:
			//Toast.makeText(this, "You choose"+currFile.getPath(),Toast.LENGTH_LONG).show();
			AlertDialog.Builder ok=new Builder(this);
			ok.setMessage("you choose : "+currFile.getPath());
			ok.setTitle("TIPS");
			ok.setPositiveButton("Cancle", null);
			ok.setNegativeButton("ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {			
					Toast.makeText(getApplicationContext(), "Save the file in the : "+currFile.getPath(),Toast.LENGTH_LONG).show();
					String text=currFile.getPath()+"/lock";
					File lockfile=new File(text);
					if (!lockfile.exists()) {
						lockfile.mkdirs();
					}
					SharedPreferences perferences = getSharedPreferences("text1", Context.MODE_PRIVATE);   
                    Editor edit = perferences.edit();   
                    edit.putString("text1", text);
                    edit.commit();   
                    
                    
                    
                    String text2=currFile.getPath()+"/normal/";
                    File normalfile=new File(text2);
                    if (!normalfile.exists()) {
                    	normalfile.mkdirs();
                    }
					SharedPreferences perferences2 = getSharedPreferences("text2", Context.MODE_PRIVATE);   
                    Editor edit2 = perferences2.edit();   
                    edit2.putString("text2", text2);
                    edit2.commit();
					
					
					
					String text1=currFile.getPath();
					Intent intent1=new Intent();
					intent1.putExtra("text1", text1);
					intent1.setClass(FileBrowerActivity.this, MessageSettingActivity.class);
					SharedPreferences perferences1 = getSharedPreferences("text1", Context.MODE_PRIVATE);   
                    Editor edit1 = perferences1.edit();   
                    edit1.putString("text1", text1);
                    edit1.commit();   
					startActivity(intent1);
				FileBrowerActivity.this.finish();
				}
			});
			ok.show();
			break;
		}
	}
	static class MyAdapter extends BaseAdapter{
		private ArrayList<File>fileList;
		private LayoutInflater inflater;
		
		public MyAdapter(Context context,ArrayList<File>fileList){
			this.fileList=fileList;
			this.inflater=LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.fileList.size();
		}

		@Override
		public Object getItem(int po) {
			// TODO Auto-generated method stub
			return this.fileList.get(po);
		}

		@Override
		public long getItemId(int po) {
			// TODO Auto-generated method stub
			return po;
		}

		@Override
		public View getView(int po, View v, ViewGroup pa) {
			TextView tv=null;
			if(v==null){
				v=this.inflater.inflate(R.layout.item, pa,false);
				tv=(TextView) v.findViewById(R.id.textView1);
				v.setTag(tv);
			}else{
				tv=(TextView) v.getTag();
			}
			tv.setText(fileList.get(po).getName());
			return v;
		}
		
	}
	

}
