package com.argus.caller.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.argus.caller.R;
import com.argus.caller.dialog.HuzAlertDialog;


public class ContactListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<ContactBean> list;
	private HashMap<String, Integer> alphaIndexer; // 字母索引
	private String[] sections; // 存储每个章节
	private Context ctx; // 上下文
	private String IsSuper, IsSuperN,UserPhone = "0";
	private String mUserPhoneAll,rawContactIds = "";
	public ContactListAdapter(Context context, List<ContactBean> list,
			QuickAlphabeticBar alpha) {
		this.ctx = context;
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.alphaIndexer = new HashMap<String, Integer>();
		this.sections = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			// 得到字母
			String name = getAlpha(list.get(i).getSortKey());
			if (!alphaIndexer.containsKey(name)) {
				alphaIndexer.put(name, i);

			}
		}

		Set<String> sectionLetters = alphaIndexer.keySet();
		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
		Collections.sort(sectionList);
		sections = new String[sectionList.size()];
		sectionList.toArray(sections);

		alpha.setAlphaIndexer(alphaIndexer);

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void remove(int position) {
		list.remove(position);
	}

	public Builder createAlertDlgBuilder() {
		return new HuzAlertDialog.Builder(ctx);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		DatabaseHelper database = new DatabaseHelper(ctx);// 这段代码放到Activity类中才用this
		SQLiteDatabase db = null;
		db = database.getReadableDatabase();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.contact_list_item, null);
			holder = new ViewHolder();
			holder.touxiang = (ImageView) convertView.findViewById(R.id.qcb);
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.lock = (ImageButton) convertView.findViewById(R.id.imageButton1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
//			holder.lock.setChecked(false);
		}

		final ContactBean contact = list.get(position);
		String name = contact.getDesplayName();
		String number = contact.getPhoneNum();
		int contactId = contact.getContactId();
		final String contactIds = String.valueOf(contactId);
		//Log.v("Dialog1", "ContactId"+contactId+"number"+number);
		holder.name.setText(name);
		holder.number.setText(number);

//		convertView.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Builder bd = createAlertDlgBuilder();
//				DatabaseHelper database = new DatabaseHelper(ctx);// 这段代码放到Activity类中才用this
//				SQLiteDatabase db = null;
//				db = database.getReadableDatabase();
//				final Cursor contactD = db.rawQuery(
//						"select * from contacts where ContactId = ?",
//						new String[] { contactIds });
//				if (contactD.moveToFirst()) {
//					rawContactIds = contactD.getString(contactD.getColumnIndex("RawContactId"));
//				}
//				contactD.close();
//				final Cursor contactB = db.rawQuery(
//						"select * from contactsdetail where RawContactId = ?",
//						new String[] { rawContactIds });
//				mUserPhoneAll="";
//				if (contactB.moveToFirst()) {
//					do  {
//						UserPhone = contactB.getString(contactB.getColumnIndex("UserPhone"));
//						mUserPhoneAll = mUserPhoneAll+ UserPhone+"\n"; 
//					}while(contactB.moveToNext());
//				}
//				contactB.close();
//				Log.v("Dialog2", "ContactId"+contactIds+"RawContactId"+rawContactIds+"mUserPhoneAll"+mUserPhoneAll);
//				bd.setTitle("自定义对话框");
//				String svs[] = mUserPhoneAll.split("\n");
//				boolean[] cks = new boolean[svs.length];
//				cks[0] = true;
//				bd.setMultiChoiceItems(svs, cks, null);
//				bd.setNegativeButton("取消", null);
//				bd.setPositiveButton("是", null);
//				bd.show();
//			}
//		});
		final Cursor contactC = db.rawQuery(
				"select * from contacts where ContactId = ?",
				new String[] { contactIds });
		if (contactC.moveToFirst()) {
			IsSuper = contactC.getString(contactC.getColumnIndex("IsSuper"));
		}contactC.close();
		boolean Super = false;
		if (IsSuper.equals("1")) {
			Super = true;
		} else {
			Super = false;
		}
//		holder.lock.setChecked(Super);
//		holder.lock.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				DatabaseHelper database2 = new DatabaseHelper(ctx);// 这段代码放到Activity类中才用this
//				SQLiteDatabase db2 = null;
//				db2 = database2.getWritableDatabase();
//				ContentValues values = new ContentValues();
//				String whereClause = "ContactId=?";
//				String[] whereArgs = { contactIds };
//				if (IsSuper.equals("1")) {
//					// 在values中添加内容
//					values.put("IsSuper", 0);
//				} else {
//					values.put("IsSuper", 1);
//				}
//				db2.update("contacts", values, whereClause, whereArgs);
//				db2.close();
//			}
//		});
		if (0 == contact.getPhotoId()) {
			holder.touxiang.setImageResource(R.drawable.tx);
		} else {
			Uri uri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI,
					contact.getContactId());
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(ctx.getContentResolver(), uri);
			Bitmap contactPhoto = BitmapFactory.decodeStream(input);
			holder.touxiang.setImageBitmap(contactPhoto);
		}
		// 当前字母
		String currentStr = getAlpha(contact.getSortKey());
		// 前面的字母
		String previewStr = (position - 1) >= 0 ? getAlpha(list.get(
				position - 1).getSortKey()) : " ";

		if (!previewStr.equals(currentStr)) {
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentStr);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}

		db.close();
		return convertView;
	}

	private static class ViewHolder {
		ImageView touxiang;
		TextView alpha;
		TextView name;
		TextView number;
		ImageButton lock;
	}

	/**
	 * 获取首字母
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式匹配
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // 将小写字母转换为大写
		} else {
			return "#";
		}
	}
}
