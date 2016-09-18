package com.argus.caller.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "argusdata.db"; // 数据库名称
	private static final int version = 1; // 数据库版本

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String contacts = "CREATE TABLE contacts (_id INTEGER PRIMARY KEY AUTOINCREMENT,ContactId INTEGER,Version INTEGER,RawContactId INTEGER,IsSuper INTEGER DEFAULT 1)";
		db.execSQL(contacts);
		String contactsdetail = "CREATE TABLE contactsdetail (_id INTEGER PRIMARY KEY AUTOINCREMENT,RawContactId INTEGER,UserPhone INTEGER,IsRecord INTEGER DEFAULT 1)";
		db.execSQL(contactsdetail);
		String calllog = "CREATE TABLE calllog (_id INTEGER PRIMARY KEY AUTOINCREMENT,CallType INTEGER,CallId INTEGER,recordfile TEXT,UserPhone INTEGER,IsRecord INTEGER DEFAULT 1,IsLock INTEGER DEFAULT 0,TAG INTEGER DEFAULT 1,Time TEXT)";
		db.execSQL(calllog);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}