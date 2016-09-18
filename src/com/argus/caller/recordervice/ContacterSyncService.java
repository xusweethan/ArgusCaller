package com.argus.caller.recordervice;

import com.argus.caller.activity.DbTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

public class ContacterSyncService extends Service{
	private final static String TAG ="Any.ONE.ContacterSyncService";
	//延时处理同步联系人，若在延时期间，通话记录数据库为改变，即判断为联系人被改变了
	private final static int ELAPSE_TIME=10000;
	private Context ctx;
	private Handler mHandler;
	
	public ContentObserver mObserver=new ContentObserver(mHandler) {
		@Override
		public void onChange(boolean selfChange) {
			//当系统联系人发生改变时触发此操作
			Log.i(TAG, "Catact");
			//去掉多余的或者重复的同步
			mHandler.removeMessages(0);
			
			DbTask dbTask = new DbTask(ctx);  
			dbTask.execute();
			
			//延时ELAPSE_TIME(10秒)发送同步信号“0”
			mHandler.sendEmptyMessageDelayed(0, ELAPSE_TIME);
		}
	};
	//当通话记录数据库发生改变时触发此操作
	private ContentObserver  mCallLogObserver=new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			//当通话记录数据库发生更改时触发此操作
			Log.i(TAG, "callLog");
			//如果延时期间发现通话记录数据库也被改变了，即判断为联系人未改变，则取消前面的同步
			mHandler.removeMessages(0);
		};
	};
	//在此处理联系人被修改后该执行的操作
	public void updataContact(){
//		DbTask dbTask = new DbTask(this);  
//		dbTask.execute();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG,"OnCreate");
		ctx = this.getBaseContext();
		//注册监听通话记录数据库
		getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,true, mCallLogObserver);
		//注册监听联系人数据库
		getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI,true, mObserver);
		
		//为了避免同步联系人时阻塞主线程，此处获取一个子线程的Handler
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();
				mHandler=new Handler(){
					public void handlerMessage(Message msg) {
						switch(msg.what){
						case 0:
							updataContact();
							break;
						}
					}
				};
				Looper.loop();
				
			}
		}).start();
	}

}
