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
	//��ʱ����ͬ����ϵ�ˣ�������ʱ�ڼ䣬ͨ����¼���ݿ�Ϊ�ı䣬���ж�Ϊ��ϵ�˱��ı���
	private final static int ELAPSE_TIME=10000;
	private Context ctx;
	private Handler mHandler;
	
	public ContentObserver mObserver=new ContentObserver(mHandler) {
		@Override
		public void onChange(boolean selfChange) {
			//��ϵͳ��ϵ�˷����ı�ʱ�����˲���
			Log.i(TAG, "Catact");
			//ȥ������Ļ����ظ���ͬ��
			mHandler.removeMessages(0);
			
			DbTask dbTask = new DbTask(ctx);  
			dbTask.execute();
			
			//��ʱELAPSE_TIME(10��)����ͬ���źš�0��
			mHandler.sendEmptyMessageDelayed(0, ELAPSE_TIME);
		}
	};
	//��ͨ����¼���ݿⷢ���ı�ʱ�����˲���
	private ContentObserver  mCallLogObserver=new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			//��ͨ����¼���ݿⷢ������ʱ�����˲���
			Log.i(TAG, "callLog");
			//�����ʱ�ڼ䷢��ͨ����¼���ݿ�Ҳ���ı��ˣ����ж�Ϊ��ϵ��δ�ı䣬��ȡ��ǰ���ͬ��
			mHandler.removeMessages(0);
		};
	};
	//�ڴ˴�����ϵ�˱��޸ĺ��ִ�еĲ���
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
		//ע�����ͨ����¼���ݿ�
		getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,true, mCallLogObserver);
		//ע�������ϵ�����ݿ�
		getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI,true, mObserver);
		
		//Ϊ�˱���ͬ����ϵ��ʱ�������̣߳��˴���ȡһ�����̵߳�Handler
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
