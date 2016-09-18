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
		DatabaseHelper database = new DatabaseHelper(context);// ��δ���ŵ�Activity���в���this
		SQLiteDatabase db = null;
		db = database.getReadableDatabase();
		// ��ȡ�����������ݵ���Ķ��󣬶���ϵ�˵Ļ�����������ʹ���������
		ContentResolver cr = context.getContentResolver();
		// ��ѯcontacts������м�¼
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		// �����¼��Ϊ��
		if (cursor.getCount() > 0) {
			// �α��ʼָ���ѯ����ĵ�һ����¼���Ϸ���ִ��moveToNext�������ж�
			// ��һ����¼�Ƿ���ڣ�������ڣ�ָ����һ����¼�����򣬷���false��
			while (cursor.moveToNext()) {
				String rawContactId = "";
				int VersionD = 0, VersionC = 0;
				// ��Contacts����ȡ��ContactId
				String id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				// ��ȡRawContacts����α�
				Cursor rawContactCur = cr.query(RawContacts.CONTENT_URI, null,
						RawContacts.CONTACT_ID + "=?", new String[] { id },
						null);
				// �ò�ѯ���һ��ֻ����һ����¼����������ֱ�����α�ָ���һ����¼
				if (rawContactCur.moveToFirst()) {
					// ��ȡ��һ����¼��RawContacts._ID�е�ֵ
					rawContactId = rawContactCur.getString(rawContactCur
							.getColumnIndex(RawContacts._ID));
					VersionD = rawContactCur.getInt(rawContactCur
							.getColumnIndex(RawContacts.VERSION));
				}
				// �ر��α�
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
				// ��ȡ����
				if (VersionD != VersionC) {
					if (Integer
							.parseInt(cursor.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) { // ���ݲ�ѯRAW_CONTACT_ID��ѯ����ϵ�˵ĺ���
						Cursor phoneCur = cr
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
												+ "=?",
										new String[] { rawContactId }, null);
						// �����ContactsContract.CommonDataKinds.Phone.CONTENT_URI
						// �����������phoneUri����
						// Uri
						// phoneUri=Uri.parse("content://com.android.contacts/data/phones");
						// һ����ϵ�˿����ж�����룬��Ҫ����
						while (phoneCur.moveToNext()) {
							// ��ȡ����
							String k = phoneCur
									.getString(phoneCur
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							String number = k.trim();
							number = number.replaceAll("\\s*", "");
							number = number.replaceAll("-", "");
							// number=phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							// ��ȡ��������
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
