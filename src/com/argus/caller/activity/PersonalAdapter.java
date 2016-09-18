package com.argus.caller.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.argus.caller.R;
import com.argus.caller.bean.CallLogBean;

//个人中心界面listview的适配器
public class PersonalAdapter extends BaseAdapter {

	private Context ctx;
	private List<CallLogBean> calllogs;
	private List<Map<String, Object>> listitems;
	private LayoutInflater inflater;
	SparseBooleanArray selected;
	boolean isSingle = true;
	int old = -1;
	private View CustomView;
	HashMap<Integer, View> map = new HashMap<Integer, View>();
	PersonalActivity context;
	private int s,CallType;
	private String stime;
	boolean isVisibity = false;//是否已经出现
	private int CallId,IsLock,TAG;
	private String recordfile,file;
	private boolean Lock=false;

/*	public PersonalAdapter(PersonalActivity context, List<CallLogBean> calllogs, List<Map<String, Object>> listitems) {
		this.context = context;
		this.ctx = context;
		this.calllogs = calllogs;
		this.inflater = LayoutInflater.from(context);
		selected = new SparseBooleanArray();
		this.listitems = listitems;
	}*/
	
	public PersonalAdapter(PersonalActivity context, List<Map<String, Object>> listitems) {
		this.context = context;
		this.ctx = context;
		// 创建视图容器并设置上下文
		inflater = LayoutInflater.from(context);
		selected = new SparseBooleanArray();
		this.listitems = listitems;
		
	}

	@Override
	public int getCount() {
		//return calllogs.size();
		return listitems.size();
	}

	@Override
	public Object getItem(int position) {
		//return calllogs.get(position);
		return position;
	}

	@Override
	public long getItemId(int position) {
		//return position;
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		DatabaseHelper database = new DatabaseHelper(ctx);// 这段代码放到Activity类中才用this
		SQLiteDatabase db = null;
		db = database.getReadableDatabase();
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_personal_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.call_type = (ImageView) convertView.findViewById(R.id.call_type2);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.tv_time2);
			viewHolder.suo = (ImageView) convertView
					.findViewById(R.id.image_suo);
			viewHolder.biaoQian = (ImageView) convertView// 选择标签
					.findViewById(R.id.tv_biaoQian);
			viewHolder.length = (TextView)convertView.findViewById(R.id.tv_length2);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.biaoQian.setImageResource(R.drawable.nomal);
			viewHolder.suo.setVisibility(View.INVISIBLE);
		}
		Log.d("selected position == " + position,
				"hashCode == " + convertView.hashCode());
		if (selected.get(position)) {
			convertView.setBackgroundResource(R.color.blue1);
		} else {
			convertView.setBackgroundResource(R.color.white);
		}
		//CallLogBean callLog = calllogs.get(position);
		file = context.getPos();
		Lock = context.returnLock();
		Cursor d = db.rawQuery("select * from calllog", null);
		for (int ii = 0; ii < d.getCount(); ii++) {
			if (d.moveToFirst()) {
				d.moveToPosition(ii);
				IsLock = d.getInt(d.getColumnIndex("IsLock"));
				recordfile = d.getString(d.getColumnIndex("recordfile"));
				CallType = d.getInt(d.getColumnIndex("CallType"));
				if(context.returnFile(position).equals(recordfile) && IsLock == 1){
					isVisibity= true;	
					viewHolder.suo.setVisibility(View.VISIBLE);
					d.close();
					break;
				}else if(context.returnFile(position).equals(recordfile) && IsLock == 0){
					isVisibity= false;
					viewHolder.suo.setVisibility(View.INVISIBLE);
					d.close();
					break;
				}
			}else{
				viewHolder.suo.setVisibility(View.INVISIBLE);
			}
		}
		d.close();
		if (file.equals(context.returnFile(position))&&Lock) {
			Cursor e = db.rawQuery("select * from calllog", null);
			for (int ii = 0; ii < e.getCount(); ii++) {
				if (e.moveToFirst()) {
					e.moveToPosition(ii);
					IsLock = e.getInt(e.getColumnIndex("IsLock"));
					recordfile = e.getString(e.getColumnIndex("recordfile"));
					if(context.returnFile(position).equals(recordfile) && IsLock == 1){
						ContentValues values = new ContentValues();
						// 修改条件
						String whereClause = "recordfile=?";
						// 修改添加参数
						String[] whereArgs = { recordfile };
					    values.put("IsLock", 0);
						// 修改
						db.update("calllog", values, whereClause, whereArgs);
						isVisibity= false;
						viewHolder.suo.setVisibility(View.INVISIBLE);
						e.close();
						break;
					}else if(context.returnFile(position).equals(recordfile) && IsLock == 0){
						ContentValues values = new ContentValues();
						// 修改条件
						String whereClause = "recordfile=?";
						// 修改添加参数
						String[] whereArgs = { recordfile };
					    values.put("IsLock", 1);
						// 修改
						db.update("calllog", values, whereClause, whereArgs);
						isVisibity= true;
						viewHolder.suo.setVisibility(View.VISIBLE);
						e.close();
						break;
					}
				}	
			}
			e.close();
		}
		
		// 电话型号
		switch (CallType) {
		case 1:
			viewHolder.call_type.setImageResource(R.drawable.recall);
			break;
		case 2:
			viewHolder.call_type.setImageResource(R.drawable.call);
			break;
		case 3:
			viewHolder.call_type.setImageResource(R.drawable.arrow);
			break;
		}

		Cursor f = db.rawQuery("select * from calllog", null);
		for (int ii = 0; ii < f.getCount(); ii++) {
			if (f.moveToFirst()) {
				f.moveToPosition(ii);
				TAG = f.getInt(f.getColumnIndex("TAG"));
				recordfile = f.getString(f.getColumnIndex("recordfile"));
				if(context.returnFile(position).equals(recordfile) && TAG == 1){
					viewHolder.biaoQian.setImageResource(R.drawable.nomal);
					break;
				}else if(context.returnFile(position).equals(recordfile) && TAG == 2){
					viewHolder.biaoQian.setImageResource(R.drawable.inprotant);
					break;
				}else if(context.returnFile(position).equals(recordfile) && TAG == 3){
					viewHolder.biaoQian.setImageResource(R.drawable.urgent);
					break;
			}
			}
		}
		f.close();
		
		viewHolder.biaoQian.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Builder builder = myBuilder(ctx);
				final AlertDialog dialog = builder.show();
				
				dialog.setCanceledOnTouchOutside(true);// 点击屏幕外侧，dialog消失
				// 第一个颜色标签
				RelativeLayout relative1 = (RelativeLayout) CustomView
						.findViewById(R.id.dial_relative1);
				relative1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						DatabaseHelper database = new DatabaseHelper(ctx);// 这段代码放到Activity类中才用this
						SQLiteDatabase db = null;
						db = database.getReadableDatabase();
						Toast.makeText(ctx, "该录音为普通录音", Toast.LENGTH_SHORT)
								.show();
						ContentValues values1 = new ContentValues();
						// 修改条件
						String whereClause = "recordfile=?";
						// 修改添加参数
						String[] whereArgs = { (String) listitems.get(position).get("text") };
					    values1.put("TAG", 1);
						// 修改
						db.update("calllog", values1, whereClause, whereArgs);
						db.close();
						viewHolder.biaoQian.setImageResource(R.drawable.nomal);
						dialog.dismiss();
					}
				});
				// 第2个颜色标签
				RelativeLayout relative2 = (RelativeLayout) CustomView
						.findViewById(R.id.dial_relative2);
				relative2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						DatabaseHelper database = new DatabaseHelper(ctx);// 这段代码放到Activity类中才用this
						SQLiteDatabase db = null;
						db = database.getReadableDatabase();
						ContentValues values2 = new ContentValues();
						// 修改条件
						String whereClause = "recordfile=?";
						// 修改添加参数
						String[] whereArgs = { (String) listitems.get(position).get("text") };
					    values2.put("TAG", 2);
						// 修改
						db.update("calllog", values2, whereClause, whereArgs);
						db.close();
						Toast.makeText(ctx, "该录音为重要录音", Toast.LENGTH_SHORT)
								.show();
						viewHolder.biaoQian.setImageResource(R.drawable.inprotant);
						dialog.dismiss();
					}
				});
				// 第3个颜色标签
				RelativeLayout relative3 = (RelativeLayout) CustomView
						.findViewById(R.id.dial_relative3);
				relative3.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						DatabaseHelper database = new DatabaseHelper(ctx);// 这段代码放到Activity类中才用this
						SQLiteDatabase db = null;
						db = database.getReadableDatabase();
						ContentValues values = new ContentValues();
						// 修改条件
						String whereClause = "recordfile=?";
						// 修改添加参数
						String[] whereArgs = { (String) listitems.get(position).get("text") };
					    values.put("TAG", 3);
						// 修改
						db.update("calllog", values, whereClause, whereArgs);
						db.close();
						Toast.makeText(ctx, "该录音为紧急录音", Toast.LENGTH_SHORT)
								.show();
						viewHolder.biaoQian.setImageResource(R.drawable.urgent);
						dialog.dismiss();
					}
				});
				// 回退
				ImageButton back = (ImageButton) CustomView
						.findViewById(R.id.dial_Cancel);
				back.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		});
		
		
		//viewHolder.time.setText(callLog.getDate());
		
		viewHolder.time.setText((CharSequence) listitems.get(position).get("text"));
		try {
			stime = context.returnTime(position);
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewHolder.length.setText(stime);
		db.close();
		return convertView;
	}

	public class ViewHolder {
		ImageView call_type;
		TextView time;
		ImageView biaoQian;
		TextView length;
		ImageView suo;
	}

	// 高亮方法
	public void setSelectedItem(int selected) {
		if (isSingle = true && old != -1) {
			this.selected.put(old, false);
		}
		this.selected.put(selected, true);
		old = selected;
	}

	// dialog方法
	protected Builder myBuilder(Context context2) {
		LayoutInflater inflater1 = LayoutInflater.from(context2);
		AlertDialog.Builder builder = new AlertDialog.Builder(context2);
		CustomView = inflater1.inflate(R.layout.dialogview, null);
		return builder.setView(CustomView);
	}
}
