package com.argus.caller.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.argus.caller.R;

public class PersonalActivity extends Activity implements OnClickListener {

	public static final int MESSAGE_UPDATE_PROGRESS = 998;// 消息：子线程请求更新UI
	private MediaPlayer player = new MediaPlayer();// 播放器
	private String path;// 录音路径
	private String playPath;// 播放路径
	private int currentMusicPosition;// 在list中的索引，当前是第x段录音
	private int pausePosition;// 记录暂停播放时，播放到的位置，即播放到?分?秒暂停的
	private int currentPosition;// 记录实时进度，该值在获取进度的线程中被获取和更新，并用于主线程更新进度条的进度
	private Handler handler;
	private Handler.Callback callback;
	private int playMode = 0;// 播放模式
	// 播放模式的值
	public static final int PLAY_MODE_ORDERED = 0;
	public static final int PLAY_MODE_RANDOM = 1;
	private boolean isPlayerStart = false;// 是否手动开启了播放
	private TextView tv_current, tv_duration;
	private ImageButton playerButton;
	private SeekBar progressBar;
	SparseBooleanArray selected;
	boolean isSingle = true;
	int old = -1;
	private ListView listView2;
	private PersonalAdapter prAdapter;
	private TextView tv_name;
	private TextView tv_number;
	private ImageView touxiang;
	private PopupWindow window;
	private int s = -1;;
	SharedPreferences sp;
	private List<Map<String, Object>> list;
	private String t[];
	private String number;
	private String name;
	private String k;
	private String k1;
	private String k2;
	private String stime;
	private int CallId,lastItem;
	private String Time,recordfile,file = "";
	private LinearLayout linear;
	private int IsLock;
    private boolean Lock;
    private String photoID = "";
	private long ContactId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_personal);

		initViews();// 获取控件

		Intent nowIntent = getIntent();
		Bundle bundle = nowIntent.getExtras();
		number = bundle.getString("number");
		name = bundle.getString("name");
		tv_name = (TextView) findViewById(R.id.tv_name2);
		tv_number = (TextView) findViewById(R.id.tv_number2);
		tv_name.setText(name);
		tv_number.setText(number);
		touxiang  = (ImageView) findViewById(R.id.touxiang);
		ContentResolver cr = getBaseContext().getContentResolver();
		Cursor phoneCur = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
				new String[] { number }, null);
		if (phoneCur.moveToFirst()) {
			// 读取第一条记录的RawContacts._ID列的值
			ContactId = phoneCur.getLong(phoneCur.getColumnIndex(RawContacts.CONTACT_ID));
		}
		Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,ContactId);
		InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(getBaseContext().getContentResolver(), uri);
		Bitmap contactPhoto = BitmapFactory.decodeStream(input);
		if (contactPhoto!= null){
			touxiang.setImageBitmap(contactPhoto);
		}else{
			touxiang.setImageResource(R.drawable.tx);
		}
		
		
		listView2 = (ListView) findViewById(R.id.listView2);
		//listView2.setSelectionFromTop(currentMusicPosition, 0);
		//setListViewPos(currentMusicPosition);
		prAdapter = new PersonalAdapter(this, getData());
		listView2.setAdapter(prAdapter);
		listView2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos,
					long arg3) {
				LinearLayout footer = (LinearLayout) findViewById(R.id.footer);
				footer.setVisibility(View.VISIBLE);
				prAdapter.setSelectedItem(pos);
				setPos(pos);
				file = "";
				prAdapter.notifyDataSetChanged();
				//Log.v("postion", "k" + k + "l" + k1 + "m" + k2);
				playPath = path + "/" + k2;
				//setListViewPos(currentMusicPosition);
				if (!player.isPlaying()) {// 点击时没有音乐在播放
					playerButton.setImageResource(R.drawable.pausea);
				}
				pausePosition = 0;
				currentMusicPosition = pos;
				play();
			}
		});
//		listView2.setOnScrollListener(new OnScrollListener() {
//			
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//			
//			}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
////				System.out.println("***firstParamInt:"+firstVisibleItem);
////				 System.out.println("***visibleItemCount:"+visibleItemCount);
////				 System.out.println("***totalItemCount:"+totalItemCount); 
////				listView2.getLastVisiblePosition();
////				System.out.println("****"+String.valueOf(listView2.getLastVisiblePosition()));  
////				 lastItem =listView2.getLastVisiblePosition(); 
//			}
//		});
		callback = new InnerHandlerCallback();
		handler = new Handler(callback);
		// 播放完成时间的监听
		player.setOnCompletionListener(new PlayerOnCompletionListener());
		new ProgressThread().start();
	}

	private List<Map<String, Object>> getData() {
		list = new ArrayList<Map<String, Object>>();
		SharedPreferences preference = getSharedPreferences("text2",
				Context.MODE_PRIVATE);
		String valueText = preference.getString("text2", null);
		path = valueText + number;
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		if (f.list() == null) {
			TextView note = (TextView) findViewById(R.id.text_note);
			note.setVisibility(View.VISIBLE);
		} else {
			t = f.list();
			if (t.length == 0) {
				TextView note = (TextView) findViewById(R.id.text_note);
				note.setVisibility(View.VISIBLE);
			} else {
				for (int i = 0; i < t.length; i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("text", t[i]);
					list.add(map);
				}
			}
		}
		return list;
	}

	

	private void initViews() {
		findViewById(R.id.imgbt_back).setOnClickListener(this);
		findViewById(R.id.imgbt_note).setOnClickListener(this);
		findViewById(R.id.imgbt_dial).setOnClickListener(this);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
		findViewById(R.id.button5).setOnClickListener(this);
		linear = (LinearLayout) findViewById(R.id.footer);
		playerButton = (ImageButton) findViewById(R.id.button2);
		tv_current = (TextView) findViewById(R.id.tv_current);
		tv_duration = (TextView) findViewById(R.id.tv_duration);
		progressBar = (SeekBar) findViewById(R.id.seekBar1);
		progressBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
	}

//	private void setListViewPos(int pos) {  
//	    if (android.os.Build.VERSION.SDK_INT >= 8) {  
//	        listView2.smoothScrollToPosition(pos);  
//	    } else {  
//	        listView2.setSelection(pos);  
//	    }  
//	} 
	
	// 当前录音播放完之后自动播放下一个
	private final class PlayerOnCompletionListener implements
			OnCompletionListener {
		@Override
		public void onCompletion(MediaPlayer mp) {
			if (isPlayerStart) {
				next();
				play();
				prAdapter.setSelectedItem(currentMusicPosition);
				listView2.smoothScrollToPosition(currentMusicPosition);
				//listView2.smoothScrollByOffset(currentMusicPosition);
				setPos(currentMusicPosition);
				file = "";
				prAdapter.notifyDataSetChanged();
			}
		}
	}

	// 上一首
		public void last() {
			if (currentMusicPosition == 0) {// 如果在播放第一个，那么前面就没有了
				Toast.makeText(PersonalActivity.this, "前面没有了。。。",
						Toast.LENGTH_SHORT).show();
			} else {
				if (!player.isPlaying()) {
					playerButton.setImageResource(R.drawable.pausea);
				}
				
				pausePosition = 0;
				// 如果当前正在播放的不是第1个，则索引自减
				currentMusicPosition--;
				listView2.smoothScrollToPosition(currentMusicPosition);
				//listView2.smoothScrollByOffset(currentMusicPosition);
				setPos(currentMusicPosition);
				file = "";
				playPath = path + "/" + k2;
			}
		}

		// 下一首
		public void next() {
			if (currentMusicPosition >= list.size() - 1) {// 如果是最后一首
				Toast.makeText(PersonalActivity.this, "下面没有额。。。",
						Toast.LENGTH_SHORT).show();
			} else {
				if (!player.isPlaying()) {
					playerButton.setImageResource(R.drawable.pausea);
				}
				pausePosition = 0;
				currentMusicPosition++;
				listView2.smoothScrollToPosition(currentMusicPosition);
				//listView2.smoothScrollByOffset(currentMusicPosition);
				setPos(currentMusicPosition);
				file = "";
				playPath = path + "/" + k2;
			}

		}
	
	// 进度条监听
	private final class SeekBarChangeListener implements
			OnSeekBarChangeListener {
		// 停止拖动
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// 进度条最大值
			int max = 100;
			// 拖动后的进度百分比（因为该进度条设置为max=100）
			int progress = seekBar.getProgress();
			// 录音的时长
			int duration = player.getDuration();
			// 应该播放的位置，即跳到?分?秒再播放
			int playPosition = duration * progress / max;
			// 播放器播放指定位置
			player.seekTo(playPosition);
		}

		// 数值改变
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

		}

		// 开始拖动
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}
	}

	private final class ProgressThread extends Thread {
		@SuppressLint("SimpleDateFormat")
		public void run() {
			while (true) {
				if (player.isPlaying()) {
					// 获取当前播放进度
					currentPosition = player.getCurrentPosition();
					// 发消息
					Message msg = handler.obtainMessage();
					msg.what = MESSAGE_UPDATE_PROGRESS;
					handler.sendMessage(msg);
				}
				// 线程休眠，过一段时间后再次获取进度
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private final class InnerHandlerCallback implements Handler.Callback {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_UPDATE_PROGRESS:
				// 录音的完整长度
				int musicDuration = player.getDuration();
				// 已有currentPosition表示当前位置 ,已有进度条最大值是100,则：进度条位置 = 当前播放位置 * 100
				// / 录音长度
				int progress = player.getCurrentPosition() * 100
						/ musicDuration;
				progressBar.setProgress(progress);
				// 更新TextView显示时间
				tv_current.setText(getTime(player.getCurrentPosition()));
				tv_duration.setText(getTime(musicDuration));
				break;
			}
			return true;
		}
	}

	public String getTime(int time) {
		return new SimpleDateFormat("mm:ss").format(new Date(time));
	}

	public String returnTime(int pos) throws Exception {
		String rtime = null;
		k = list.get(pos).toString();
		k1 = k.replaceAll("\\{text=", "");// 去除
		k2 = k1.replaceAll("\\}", "");
		DatabaseHelper database = new DatabaseHelper(getBaseContext());// 这段代码放到Activity类中才用this
		SQLiteDatabase db = null;
		db = database.getReadableDatabase();
		Cursor d = db.rawQuery("select * from calllog", null);
		
			if (d.moveToFirst()) {
				for (int ii = 0; ii < d.getCount(); ii++) {
				d.moveToPosition(ii);
				CallId = d.getInt(d.getColumnIndex("CallId"));
				Time = d.getString(d.getColumnIndex("Time"));
				recordfile = d.getString(d.getColumnIndex("recordfile"));
				if(recordfile.equals(k2)){
					rtime=Time;
					break;
				}else{
					rtime=("没存");
				}
			}
		}else{
			rtime=("没存");
		}
			d.close();
			db.close();
		return rtime;		
	}
	
	
	// 播放
	public void play() {
		try {
			// 重置播放器
			player.reset();
			// 设置要播放的录音路径
			player.setDataSource(playPath);
			// 缓冲
			player.prepare();
			// 指定播放位置
			player.seekTo(pausePosition);
			pausePosition = 0;
			// 播放
			// 已经开始播放
			player.start();
			isPlayerStart = true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbt_back:// 返回
			player.stop();
			PersonalActivity.this.finish();
			break;
		case R.id.imgbt_note:// 发短信
			Intent in1 = getIntent();
			Uri uri1 = Uri.parse("smsto:" + in1.getStringExtra("number"));
			Intent it = new Intent(Intent.ACTION_SENDTO, uri1);
			it.putExtra("sms_body", "");
			startActivity(it);
			break;
		/**
		 * 注意:打电话何发短信时，在获取手机号时前面的前缀是固定格式，不可更改。 发短信是 "smsto:" 打电话是 "tel:"
		 */
		case R.id.imgbt_dial:// 拨号通话
			Intent in = getIntent();
			Uri uri = Uri.parse("tel:" + in.getStringExtra("number"));
			Intent intent1 = new Intent(Intent.ACTION_CALL, uri);
			startActivity(intent1);
			break;
		case R.id.button1:// 上一个
			last();
			play();

			prAdapter.setSelectedItem(currentMusicPosition);
			prAdapter.notifyDataSetChanged();
			break;
		case R.id.button2:// 播放
			if (!player.isPlaying()) {
				play();
				playerButton.setImageResource(R.drawable.pausea);
			} else if (player.isPlaying()) {
				pausePosition = player.getCurrentPosition();
				player.pause();
				playerButton.setImageResource(R.drawable.playa);
			}
			break;
		case R.id.button3:// 下一个
			next();
			play();
			
			prAdapter.setSelectedItem(currentMusicPosition);
			prAdapter.notifyDataSetChanged();
			break;
		case R.id.button4:// 停止
			if (player.isPlaying() || !player.isPlaying()) {
				player.stop();
				progressBar.setProgress(0);
				tv_current.setText("00:00");
				pausePosition = 0;
				playerButton.setImageResource(R.drawable.playa);
			}
			break;
		case R.id.button5:// 设置
			showPopupWindow();
			break;

		}
	}

	private void showPopupWindow() {
		// 利用layoutInflater获得View
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.menu, null);
		// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
		window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		window.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		window.setBackgroundDrawable(dw);
		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.mypopwindow_anim_style);
		// 在底部显示
		//window.showAtLocation(PersonalActivity.this.findViewById(R.id.button5),Gravity.BOTTOM, 0, 70);
		int y = linear.getHeight();
		int yy = y*2;
		window.showAsDropDown(findViewById(R.id.mainLayout), 0, -yy);
		/** 为按钮添加点击事件 */
		ImageButton button6 = (ImageButton) view.findViewById(R.id.button6);
		button6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(PersonalActivity.this, "别点了，这个键没用。。。",
						Toast.LENGTH_SHORT).show();
			}
		});
		ImageButton button7 = (ImageButton) view.findViewById(R.id.button7);
		button7.setOnClickListener(new OnClickListener() {
			final int nn = currentMusicPosition;
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(PersonalActivity.this);
				builder.setMessage("您确定要删除吗？");
				builder.setTitle("提示");
				builder.setPositiveButton("删除",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								SharedPreferences spfs1 = getPreferences(0);
								file = spfs1.getString("k2", "");
								File ff = new File(path + "/" + file);
								DatabaseHelper database = new DatabaseHelper(
										getBaseContext());// 这段代码放到Activity类中才用this
								SQLiteDatabase db = null;
								db = database.getReadableDatabase();
								Cursor d = db
										.rawQuery(
												"select * from calllog where recordfile = ?",
												new String[] { file });
								if (d.moveToFirst()) {
									IsLock = d.getInt(d
											.getColumnIndex("IsLock"));
									if (IsLock == 1) {
										Toast.makeText(PersonalActivity.this,
												"已加锁不能删除", Toast.LENGTH_SHORT)
												.show();
										setLock(0);
									} else {
										ff.delete();// 删除文件
										list.remove(nn);// 删除listview显示的条目
									}
								} else {
									ff.delete();// 删除文件
									list.remove(nn);// 删除listview显示的条目
									Toast.makeText(PersonalActivity.this,
											"数据库不存在已经删除", Toast.LENGTH_SHORT)
											.show();
								}
								db.close();
								prAdapter.notifyDataSetChanged();
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			}
		});
		ImageButton button8 = (ImageButton) view.findViewById(R.id.button8);
		button8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences spfs1 = getPreferences(0);
				file = spfs1.getString("k2", "");
				setLock(1);
				prAdapter.notifyDataSetChanged();
			}
		});
//		// popWindow消失监听方法
//		window.setOnDismissListener(new OnDismissListener() {
//			@Override
//			public void onDismiss() {
//
//			}
//		});
	}
	
	public void setPos(int pos){
		k = list.get(pos).toString();
		k1 = k.replaceAll("\\{text=", "");
		k2 = k1.replaceAll("\\}", "");
		SharedPreferences.Editor spfs = getPreferences(0).edit();
		spfs.putString("k2", k2);
		spfs.commit();
	}
	
	public void setLock(int lock){
		if (lock == 1){
			Lock=true;
			}else{
			Lock=false;}
	}
	
	public boolean returnLock(){
		return Lock;
	}
	
	public String returnFile(int pos){
		k = list.get(pos).toString();
		k1 = k.replaceAll("\\{text=", "");// 去除
		k2 = k1.replaceAll("\\}", "");
		return k2;
	}

	public String getPos(){
		return file;
	}

	/*public void setPostion(int k) {
		s = k;
	}

	public int returnPostion() {
		return s;
	}*/

	// back键触发此方法，销毁此activity，停止音乐
	@Override
	protected void onDestroy() {
		super.onDestroy();
		player.stop();
	}
	
	
}
