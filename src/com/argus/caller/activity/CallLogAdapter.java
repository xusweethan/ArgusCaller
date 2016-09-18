package com.argus.caller.activity;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.argus.caller.R;
import com.argus.caller.bean.CallLogBean;

//通话记录界面listview的适配器
public class CallLogAdapter extends BaseAdapter {

	private Context context;
	private List<CallLogBean> calllogs;
	private LayoutInflater inflater;
	private String Time;
	private boolean cloudb=false;

	public CallLogAdapter(Context context, List<CallLogBean> calllogs) {
		this.context = context;
		this.calllogs = calllogs;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return calllogs.size();
	}

	@Override
	public Object getItem(int position) {
		return calllogs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public String getName(int position) {
		CallLogBean callLog = calllogs.get(position);
		return callLog.getName();
	}

	public String getNumber(int position) {
		CallLogBean callLog = calllogs.get(position);
		return callLog.getNumber();
	}

	public String getDate(int position) {
		CallLogBean callLog = calllogs.get(position);
		return callLog.getDate();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_main_item, null);
			viewHolder = new ViewHolder();
			// viewHolder.quickContactBadge = (QuickContactBadge) convertView
			// .findViewById(R.id.contactBadge1);
			viewHolder.call_type = (ImageView) convertView
					.findViewById(R.id.call_type1);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.tv_name1);
			viewHolder.number = (TextView) convertView
					.findViewById(R.id.tv_number1);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.tv_time1);
			viewHolder.length = (TextView) convertView
					.findViewById(R.id.tv_length1);
			viewHolder.cloud = (ImageView) convertView
					.findViewById(R.id.image_cloud1);
			viewHolder.clock = (ImageView) convertView
					.findViewById(R.id.image_clock1);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.cloud.setVisibility(View.INVISIBLE);	
		}
		CallLogBean callLog = calllogs.get(position);
		// 电话型号
		switch (callLog.getType()) {
		case 1:
			viewHolder.call_type.setBackgroundResource(R.drawable.recall);
			break;
		case 2:
			viewHolder.call_type.setBackgroundResource(R.drawable.call);
			break;
		case 3:
			viewHolder.call_type.setBackgroundResource(R.drawable.arrow);
			break;
		}
		/*
		 * //头像 viewHolder.quickContactBadge.assignContactUri
		 * (Contacts.getLookupUri
		 * (callLog.getContactId(),callLog.getLookUpKey())); if
		 * (0==callLog.getPhotoId()) {
		 * viewHolder.quickContactBadge.setImageResource
		 * (R.drawable.ic_launcher); }else { Uri uri =
		 * ContentUris.withAppendedId
		 * (ContactsContract.Contacts.CONTENT_URI,callLog.getContactId());
		 * InputStream input = ContactsContract.Contacts.
		 * openContactPhotoInputStream(context.getContentResolver(),uri); Bitmap
		 * contactPhoto = BitmapFactory.decodeStream(input);
		 * viewHolder.quickContactBadge.setImageBitmap(contactPhoto); }
		 */
		viewHolder.name.setText(callLog.getName());
		viewHolder.number.setText(callLog.getNumber());
		viewHolder.time.setText(callLog.getDate());
		String id = String.valueOf(callLog.getId());
		DatabaseHelper database = new DatabaseHelper(context);// 这段代码放到Activity类中才用this
		SQLiteDatabase db = null;
		db = database.getReadableDatabase();
		Cursor d = db.rawQuery("select * from calllog where CallId = ?", new String[]{id});
			if (d.moveToFirst()) {
				Time = d.getString(d.getColumnIndex("Time"));	
				cloudb =true;
			}else{
				Time = "";	
				cloudb =false;
			}d.close();
		if(cloudb){
			viewHolder.cloud.setVisibility(View.VISIBLE);
			viewHolder.clock.setVisibility(View.VISIBLE);
			}else{
			viewHolder.cloud.setVisibility(View.INVISIBLE);	
			viewHolder.clock.setVisibility(View.INVISIBLE);
			}
		
		viewHolder.length.setText(Time);
		db.close();
		return convertView;
	}

	private static class ViewHolder {
		// QuickContactBadge quickContactBadge;
		ImageView call_type;
		TextView name;
		TextView number;
		TextView time;
		TextView length;
		ImageView cloud;
		ImageView clock;
	}
}
