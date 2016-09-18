package com.argus.caller.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.argus.caller.R;
import com.argus.caller.bean.CallLogBean;

public class Fragment1 extends LazyFragment {

	public static final String TITLE = "title";
	private String mTitle = "Defaut Value";

	// private TextView mTextView;
	// private MyInnerScrollView mInnerScrollView;
	//
	// private ScrollView mParentScrollView;

	// ��־λ����־�Ѿ���ʼ����ɡ�
	private boolean isPrepared;

	private Context mCtx;

	private ListView listView;
	private int mPosX, mPosY;

	private String path;
	
	private Button record;
	//Wx add
	private int IsRecord;
	
	private int CallId;
	private String Time;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mTitle = getArguments().getString(TITLE);
		}
		mCtx = this.getActivity().getBaseContext();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment1, container, false);

		// mTextView = (TextView) view.findViewById(R.id.id_info);
		// mTextView.setText(mTitle);
		//record = (Button)view.findViewById(R.id.button_record);
		listView = (ListView) view.findViewById(android.R.id.list);



		init();

		isPrepared = true;
		lazyLoad();

		return view;

	}
	
	public static TabFragment newInstance(String title) {
		TabFragment tabFragment = new TabFragment();
		Bundle bundle = new Bundle();
		bundle.putString(TITLE, title);
		tabFragment.setArguments(bundle);
		return tabFragment;
	}

	// public void setParentScrollView(ScrollView scrollView) {
	// this.mParentScrollView = scrollView;
	// }

	private AsyncQueryHandler asyncQuery;

	// ��ʼ�����ݿ��ѯ����
	private void init() {
		Uri uri = android.provider.CallLog.Calls.CONTENT_URI;// ��ϵ��Uri
		// ��ѯ���ֶ�
		String[] projection = { CallLog.Calls.DATE, // ����
				CallLog.Calls.NUMBER, // ����
				CallLog.Calls.TYPE, // ����
				CallLog.Calls.CACHED_NAME, // ����
				CallLog.Calls._ID, // id

		};
		asyncQuery = new MyAsyncQueryHandler(getActivity().getContentResolver());

		asyncQuery.startQuery(0, null, uri, projection, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);
	}

	private class MyAsyncQueryHandler extends AsyncQueryHandler {
		private List<CallLogBean> callLogs;

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				callLogs = new ArrayList<CallLogBean>();
				SimpleDateFormat sfd = new SimpleDateFormat("MM-dd hh:mm");
				Date date;
				cursor.moveToFirst(); // �α��ƶ�����һ��
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					date = new Date(cursor.getLong(cursor
							.getColumnIndex(CallLog.Calls.DATE)));
					String number = cursor.getString(cursor
							.getColumnIndex(CallLog.Calls.NUMBER));
					int type = cursor.getInt(cursor
							.getColumnIndex(CallLog.Calls.TYPE));
					String cachedName = cursor.getString(cursor
							.getColumnIndex(CallLog.Calls.CACHED_NAME));// �����������绰���룬������Ĵ���
					int id = cursor.getInt(cursor
							.getColumnIndex(CallLog.Calls._ID));
					
					CallLogBean callLogBean = new CallLogBean();
					callLogBean.setId(id);
					callLogBean.setNumber(number);
					callLogBean.setName(cachedName);
					if (null == cachedName || "".equals(cachedName)) {
						callLogBean.setName(number);
					}
					callLogBean.setType(type);
					callLogBean.setDate(sfd.format(date));

//					DatabaseHelper database = new DatabaseHelper(mCtx);// ��δ���ŵ�Activity���в���this
//					SQLiteDatabase db = null;
//					db = database.getReadableDatabase();
//					ContentValues values = new ContentValues();
//					values.put("UserPhone",number);
//					values.put("IsRecord",IsRecord);
//					values.put("CallId",id);
//					values.put("Time", "1");
//					db.insert("calllog", null, values);
					
					
					callLogs.add(callLogBean);
				}
				if (callLogs.size() > 0) {
					setAdapter(callLogs);

				}
			}
			super.onQueryComplete(token, cookie, cursor);
		}

		private CallLogAdapter adapter;
		private String name = null;
		private String number = null;

		private void setAdapter(List<CallLogBean> callLogs) {
			adapter = new CallLogAdapter(mCtx, callLogs);
			listView.setAdapter(adapter);
			// listView.setItemsCanFocus(false);
			// setListViewHeightBasedOnChildren(listView);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					name = adapter.getName(arg2);
					number = adapter.getNumber(arg2);
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // �ؼ�֮��
					intent.putExtra("name", name);
					intent.putExtra("number", number);
					intent.setClass(mCtx, PersonalActivity.class);
					mCtx.startActivity(intent);
				}
			});
		}
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		// ��ȡListView��Ӧ��Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			// listAdapter.getCount()�������������Ŀ
			View listItem = listAdapter.getView(i, null, listView);
			// ��������View �Ŀ��
			listItem.measure(0, 0);
			// ͳ������������ܸ߶�
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()��ȡ�����ָ���ռ�õĸ߶�
		// params.height���õ�����ListView������ʾ��Ҫ�ĸ߶�
		listView.setLayoutParams(params);
	}

	@Override
	protected void lazyLoad() {
		if (!isPrepared || !isVisible) {
			return;
		}
		// �����ؼ�������
	}

	@Override
	protected void ReLoad() {
		// TODO Auto-generated method stub
		init();
	}

}
