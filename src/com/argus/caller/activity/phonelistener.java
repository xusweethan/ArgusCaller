package com.argus.caller.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

public class phonelistener {

	private static phonelistener instance;
	private Context context;

	public phonelistener() {
		// TODO Auto-generated constructor stub
	}

	public static phonelistener getInstance() {
		if (instance == null) {
			instance = new phonelistener();
		}
		return instance;
	}

	public void queryContacts(Context context) {
		DatabaseHelper database = new DatabaseHelper(context);// 这段代码放到Activity类中才用this
		SQLiteDatabase db = null;
		db = database.getReadableDatabase();
		// 获取用来操作数据的类的对象，对联系人的基本操作都是使用这个对象
		ContentResolver cr = context.getContentResolver();
		// 查询contacts表的所有记录
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		// 如果记录不为空
		if (cursor.getCount() > 0) {
			// 游标初始指向查询结果的第一条记录的上方，执行moveToNext函数会判断
			// 下一条记录是否存在，如果存在，指向下一条记录。否则，返回false。
			while (cursor.moveToNext()) {
				String rawContactId = "";
				int VersionD = 0, VersionC = 0;
				// 从Contacts表当中取得ContactId
				String id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				// 获取RawContacts表的游标
				Cursor rawContactCur = cr.query(RawContacts.CONTENT_URI, null,
						RawContacts.CONTACT_ID + "=?", new String[] { id },
						null);
				// 该查询结果一般只返回一条记录，所以我们直接让游标指向第一条记录
				if (rawContactCur.moveToFirst()) {
					// 读取第一条记录的RawContacts._ID列的值
					rawContactId = rawContactCur.getString(rawContactCur
							.getColumnIndex(RawContacts._ID));
					VersionD = rawContactCur.getInt(rawContactCur
							.getColumnIndex(RawContacts.VERSION));
				}
				// 关闭游标
				rawContactCur.close();
				Cursor contactG = db.rawQuery(
						"select * from contacts where RawContactId = ?",
						new String[] { rawContactId });
				if (contactG.moveToFirst()) {
					VersionC = contactG.getInt(contactG
							.getColumnIndex("Version"));
				} else {
					VersionC = -1;
				}
				contactG.close();
				// 读取号码
				if (VersionD != VersionC) {
					if (Integer
							.parseInt(cursor.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) { // 根据查询RAW_CONTACT_ID查询该联系人的号码
						Cursor phoneCur = cr
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
												+ "=?",
										new String[] { rawContactId }, null);
						// 上面的ContactsContract.CommonDataKinds.Phone.CONTENT_URI
						// 可以用下面的phoneUri代替
						// Uri
						// phoneUri=Uri.parse("content://com.android.contacts/data/phones");
						// 一个联系人可能有多个号码，需要遍历
						while (phoneCur.moveToNext()) {
							// 获取号码
							String k = phoneCur
									.getString(phoneCur
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							String number = k.trim();
							number = number.replaceAll("\\s*", "");
							number = number.replaceAll("-", "");
							// number=phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							// 获取号码类型
							String type = phoneCur
									.getString(phoneCur
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
							ContentValues values2 = new ContentValues();
							values2.put("RawContactId", rawContactId);
							values2.put("UserPhone", number);
							Cursor contactD = db.rawQuery(
									"select * from contactsdetail", null);
							if (contactD.moveToFirst()) {
								Cursor contactE = db
										.rawQuery(
												"select * from contactsdetail where UserPhone=?",
												new String[] { number });
								if (contactE.moveToFirst()) {
									db.update("contactsdetail", values2,
											"UserPhone=?",
											new String[] { number });
								} else {
									db.insert("contactsdetail", null, values2);
								}
								Log.v("TestContact", "contactID:" + id
										+ " rawContactID:" + rawContactId
										+ " number:" + number + " type:" + type);
								contactE.close();
							} else {
								db.insert("contactsdetail", null, values2);
							}
							contactD.close();
						}
						phoneCur.close();
						ContentValues values = new ContentValues();
						values.put("ContactId", id);
						values.put("RawContactId", rawContactId);
						values.put("Version", VersionD);
						Cursor contactD = db.rawQuery("select * from contacts",
								null);
						if (contactD.moveToFirst()) {
							Cursor contactE = db
									.rawQuery(
											"select * from contacts where RawContactId=?",
											new String[] { rawContactId });
							if (contactE.moveToFirst()) {
								db.update("contacts", values, "RawContactId=?",
										new String[] { rawContactId });
							} else {
								db.insert("contacts", null, values);
							}
							contactE.close();
						} else {
							db.insert("contacts", null, values);
						}
						contactD.close();
					}
				}
			}
		}
		cursor.close();
		Cursor contactDe = db.rawQuery("select * from contacts", null);
		if (contactDe.moveToFirst()) {
			do {
				String RId = contactDe.getString(contactDe
						.getColumnIndex("RawContactId"));
				Cursor deleteContact = cr.query(RawContacts.CONTENT_URI, null,
						RawContacts._ID + "=?", new String[] { RId }, null);
				if (deleteContact.moveToFirst()) {
					String De = deleteContact.getString(deleteContact
							.getColumnIndex(RawContacts.DELETED));
					if (De.equals("1")) {
						db.delete("contacts", "RawContactId=?",
								new String[] { RId });
						db.delete("contactsdetail", "RawContactId=?",
								new String[] { RId });
						Log.v("delete","delete"+RId);
					}
				}deleteContact.close();
			} while (contactDe.moveToNext());
		}contactDe.close();
		db.close();
	}
}
