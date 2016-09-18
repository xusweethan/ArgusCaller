package com.argus.caller.recordervice;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.argus.caller.R;
import com.argus.caller.activity.DatabaseHelper;

public class phoneService extends Service {
	private String path;
	public boolean n = false;
	private boolean mdh = false;// �жϺ����Ƿ񱻼�¼��
	private static AnimationDrawable animationDrawable;
	private Context ctx;
	int IsRecord;
	private String contactId = null;

	private boolean Super = false;
	private String CallId, UserPhone, mNumber;
	private int CallType;
	private String luyin = "��ʼ����¼��";

	// ����IBiner����service
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ctx = this.getBaseContext();
		Log.v("IsSuper", "IsSuper" + ctx);
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);

	}

	private void getContactPeople(String incomingNumber) {
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = null;
		// cursor��Ҫ�ŵ��ֶ�����
		String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
		// ������绰����ȥ�Ҹ���ϵ��
		cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
				ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[] { incomingNumber }, "");
		if (cursor.getCount() == 0) {
			Constant.numberToCall = incomingNumber;
		} else if (cursor.getCount() > 0) {
			cursor.moveToFirst();// �α�
			System.out.println(cursor.getString(1));
			Constant.numberToCall = cursor.getString(1);
			contactId = cursor.getString(3);
		}
	}

	private final class PhoneListener extends PhoneStateListener {
		private MediaRecorder mediaRecorder;
		private File file;
		WindowManager wm1;// ϵͳ���񣬽��û���ָ��͵�����window
		Button bb1;
		private Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);// �񶯷���

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			DatabaseHelper database = new DatabaseHelper(getBaseContext());// ��δ���ŵ�Activity���в���this
			SQLiteDatabase db = null;
			db = database.getReadableDatabase();
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// ����
				getContactPeople(incomingNumber);
				String k = incomingNumber;
				String number = k.trim();
				number = number.replaceAll("\\s*", "");
				number = number.replaceAll("-", "");
				mNumber = number;

				Cursor contactD = db.rawQuery("select * from contactsdetail where UserPhone = ?",
						new String[] { number });
				if (contactD.moveToFirst()) {
					String RawContactId = contactD.getString(contactD.getColumnIndex("RawContactId"));
					Cursor contactd = db.rawQuery("select * from contacts where RawContactId = ?",
							new String[] { RawContactId });
					if (contactd.moveToFirst()) {
						String IsRecord = contactd.getString(contactd.getColumnIndex("IsSuper"));
						if (IsRecord.equals("1")) {
							Super = true;
							luyin = "����¼��";
						} else {
							Super = false;
							luyin = "���粻¼";
						}
					}
					contactd.close();
				}
				contactD.close();
				/*************************/
				// Cursor contactD = db.rawQuery(
				// "select * from contactsdetail where UserPhone = ?",
				// new String[] { number });
				// if (contactD.moveToFirst()) {
				// String IsRecord = contactD.getString(contactD
				// .getColumnIndex("IsRecord"));
				// if (IsRecord.equals("1")) {
				// mNumber =
				// contactD.getString(contactD.getColumnIndex("UserPhone"));
				// Super = true;
				// luyin = "¼��";
				// } else {
				// Super = false;
				// luyin = "��¼";
				// }
				//
				// contactD.close();
				// break;
				// } else {
				// Cursor contactOffHook = db.rawQuery(
				// "select * from contactsdetail", null);
				// if (contactOffHook.getCount() > 0) {
				// while (contactOffHook.moveToNext()) {
				// UserPhone = contactOffHook.getString(contactOffHook
				// .getColumnIndex("UserPhone"));
				// IsRecord = contactOffHook.getInt(contactOffHook
				// .getColumnIndex("IsRecord"));
				// int length = UserPhone.length() - number.length();
				// if (((number.startsWith("+") && length < 6) || (number
				// .startsWith("00") && length < 7))
				// && number.contains(UserPhone)
				// && IsRecord == 1) {
				// mNumber = UserPhone;
				// Super = true;
				// luyin = "¼��";
				// break;
				// }
				// }
				// }
				// contactOffHook.close();
				// }

				Toast.makeText(getApplicationContext(), luyin, Toast.LENGTH_SHORT).show();

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// ��ͨ�绰
				if (!mdh) {
					getContactPeople(Constant.numberToCall);
					Toast.makeText(getApplicationContext(), "���Ǻ���", Toast.LENGTH_SHORT).show();

					k = Constant.numberToCall;
					number = k.trim();
					number = number.replaceAll("\\s*", "");
					number = number.replaceAll("-", "");
					mNumber = number;
					Cursor contactE = db.rawQuery("select * from contactsdetail where UserPhone = ?",
							new String[] { number });
					if (contactE.moveToFirst()) {
						String RawContactId = contactE.getString(contactE.getColumnIndex("RawContactId"));
						Cursor contactd = db.rawQuery("select * from contacts where RawContactId = ?",
								new String[] { RawContactId });
						if (contactd.moveToFirst()) {
							String IsRecord = contactd.getString(contactd.getColumnIndex("IsSuper"));
							if (IsRecord.equals("1")) {
								Super = true;
								luyin = "��ͨ¼��";
							} else {
								Super = false;
								luyin = "��ͨ��¼";
							}
						}
						contactd.close();
					}
					contactE.close();
					/******************************************/
					// Cursor contactC = db
					// .rawQuery(
					// "select * from contactsdetail where UserPhone = ?",
					// new String[] { number });
					// if (contactC.moveToFirst()) {
					// String IsRecord = contactC.getString(contactC
					// .getColumnIndex("IsRecord"));
					// Log.v("IsSuper", "IsSuper" + IsRecord);
					// if (IsRecord.equals("1")) {
					// mNumber =
					// contactC.getString(contactC.getColumnIndex("UserPhone"));
					// Super = true;
					// luyin = "¼��";
					// } else {
					// Super = false;
					// luyin = "��¼";
					// }
					// } else {
					// Cursor contactOffHook = db.rawQuery(
					// "select * from contactsdetail", null);
					// if (contactOffHook.getCount() > 0) {
					// while (contactOffHook.moveToNext()) {
					// UserPhone = contactOffHook
					// .getString(contactOffHook
					// .getColumnIndex("UserPhone"));
					// IsRecord = contactOffHook.getInt(contactOffHook
					// .getColumnIndex("IsRecord"));
					// int length = UserPhone.length()
					// - number.length();
					// if (((number.startsWith("+") && length < 6) || (number
					// .startsWith("00") && length < 7))
					// && number.contains(UserPhone)
					// && IsRecord == 1) {
					// mNumber = UserPhone;
					// Super = true;
					// luyin = "¼��";
					// break;
					// }
					// }
					// }
					// contactOffHook.close();
					// }

					Toast.makeText(getApplicationContext(), luyin, Toast.LENGTH_SHORT).show();
				}

				if (Super) {
					dqtime();
					SharedPreferences preference = getSharedPreferences("text2", Context.MODE_PRIVATE);
					String valueText = preference.getString("text2", null);
					path = valueText;
					File f = new File(path);

					if (!f.exists()) {
						f.mkdirs();
					}
					String path1 = path + "/" + mNumber;
					File f1 = new File(path1);
					if (!f1.exists()) {
						f1.mkdirs();
					}
					file = new File(path1, Constant.str + ".3gp");
					ly();
				}

//				else {
//					// ��������
//					bb1 = new Button(getApplicationContext());
//					/*
//					 * wm1 = (WindowManager) getApplicationContext()
//					 * .getSystemService("window"); WindowManager.LayoutParams
//					 * wmParams1 = new WindowManager.LayoutParams();
//					 * wmParams1.gravity = Gravity.CENTER | Gravity.LEFT;
//					 * wmParams1.type = 2002; // �����ǹؼ�����Ҳ��������2003
//					 * wmParams1.format = 1; wmParams1.flags = 40;
//					 * wmParams1.width = 80; wmParams1.height = 80;
//					 * wm1.addView(bb1, wmParams1); // ����View
//					 */ n = false;
//					bb1.setBackgroundResource(R.drawable.play);
//					final Handler bhandler = new Handler() {
//						public void handleMessage(Message msg) {
//							switch (msg.what) {
//							case 0:
//								if (animationDrawable != null) {
//									animationDrawable.stop();
//								}
//								bb1.setBackgroundResource(R.drawable.play);
//								break;
//							case 1:
//								if (bb1 != null) {
//									bb1.setBackgroundResource(R.anim.battry_process);
//									animationDrawable = (AnimationDrawable) bb1.getBackground();
//									animationDrawable.start();
//								}
//								break;
//
//							default:
//								break;
//							}
//							super.handleMessage(msg);
//						}
//					};
//					bb1.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							Message message = new Message();
//							if (n == false) {
//								n = true;
//								message.what = 1;
//								bhandler.sendMessage(message);
//								dqtime();
//								// bb1.setBackgroundResource(R.drawable.switch_on);
//								SharedPreferences preference = getSharedPreferences("text2", Context.MODE_PRIVATE);
//								String valueText = preference.getString("text2", null);
//								path = valueText;
//								File f = new File(path);
//
//								if (!f.exists()) {
//									f.mkdirs();
//								}
//								String path1 = path + Constant.numberToCall;
//								File f1 = new File(path1);
//								if (!f1.exists()) {
//									f1.mkdirs();
//								}
//
//								file = new File(path1, Constant.str + ".3gp");
//								ly();
//							} else {
//								n = false;
//								message.what = 0;
//								bhandler.sendMessage(message);
//								// bb1.setBackgroundResource(R.drawable.switch_off);
//								if (mediaRecorder != null) {
//									mediaRecorder.stop();
//									mediaRecorder.release();
//									mediaRecorder = null;
//									Toast.makeText(getApplicationContext(), R.string.nlyyin, Toast.LENGTH_SHORT).show();
//									// ��һ��
//									vibrator.vibrate(100);
//								}
//
//							}
//
//						}
//					});
//				}
				
				break;
			case TelephonyManager.CALL_STATE_IDLE: // �Ҷϵ绰
				if (!Constant.b) {
					// wm1.removeView(bb1);
				}
				Toast.makeText(getApplicationContext(), "�绰����", Toast.LENGTH_SHORT).show();
				if (mediaRecorder != null) {
					mediaRecorder.stop();
					mediaRecorder.release();
					mediaRecorder = null;
					Toast.makeText(getApplicationContext(), R.string.mdianhua, Toast.LENGTH_SHORT).show();

					// �����¼����ͨ����¼�������ݿ�
					final Cursor cursor = getBaseContext().getContentResolver().query(CallLog.Calls.CONTENT_URI,
							new String[] { CallLog.Calls._ID, CallLog.Calls.TYPE }, "1=1", null, "date desc");
					if (cursor.moveToFirst()) {
						CallId = cursor.getString(0);
						CallType = cursor.getInt(1);
					}

					MediaPlayer player = new MediaPlayer();// ������

					player.reset();
					try {
						player.setDataSource(file.toString());
						player.prepare();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int musicDuration = player.getDuration();
					player.release();
					player = null;
					String recordfile = Constant.str + ".3gp";
					Log.v("recordcallog", "CallId" + CallId + "file" + recordfile + "musicDuration"
							+ getTime(musicDuration) + "UserPhone" + mNumber);
					ContentValues values = new ContentValues();
					values.put("UserPhone", mNumber);
					values.put("IsRecord", 1);
					values.put("CallId", CallId);
					values.put("Time", getTime(musicDuration));
					values.put("CallType", CallType);
					values.put("recordfile", recordfile);
					db.insert("calllog", null, values);

					// ��һ��
					vibrator.vibrate(100);
				}

				break;
			}

		}

		public String getTime(int time) {
			return new SimpleDateFormat("mm:ss").format(new Date(time));
		}

		private void ly() {
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.setOutputFile(file.getAbsolutePath());
			try {
				mediaRecorder.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mediaRecorder.start();
			Toast.makeText(getApplicationContext(), R.string.luyin, Toast.LENGTH_SHORT).show();
			// ��һ��
			vibrator.vibrate(100);

		}

		// �˷�����ȡ¼����ʱ��
		private void dqtime() {
			Calendar c = Calendar.getInstance();
			int mYear = c.get(Calendar.YEAR); // ��ȡ��ǰ���
			int mMonth = c.get(Calendar.MONTH) + 1;// ��ȡ��ǰ�·�
			int mDay = c.get(Calendar.DAY_OF_MONTH);// ��ȡ��ǰ�·ݵ����ں���
			int mHour = c.get(Calendar.HOUR_OF_DAY);// ��ȡ��ǰ��Сʱ��
			int mMinute = c.get(Calendar.MINUTE);// ��ȡ��ǰ�ķ�����
			int miao = c.get(Calendar.SECOND);
			Constant.str = mMonth + getResources().getString(R.string.yue) + mDay
					+ getResources().getString(R.string.ri) + mHour + getResources().getString(R.string.shi) + mMinute
					+ getResources().getString(R.string.fen) + miao + getResources().getString(R.string.miao);

		}
	}

}
