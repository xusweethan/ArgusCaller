package com.argus.caller.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

public class MyInnerScrollView extends ScrollView {
	private ScrollView mParentScrollView;
	private int mLastY;

	private ListView listView;
	private ListAdapter mAdapter;
	private OnItemClickListener mOnItemClickListener;

	public void setParentScrollView(ScrollView scrollView) {
		this.mParentScrollView = scrollView;

	}

	public MyInnerScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//
//		int action = ev.getAction();
//		switch (action) {
//		case MotionEvent.ACTION_DOWN:
//			mLastY = (int) ev.getY();
//			if (mParentScrollView.getScrollY() + mParentScrollView.getHeight() == mParentScrollView
//					.getChildAt(0).getHeight()) {
//				mParentScrollView.requestDisallowInterceptTouchEvent(true);
//			}
//			break;
//		case MotionEvent.ACTION_MOVE:
//			int y = (int) ev.getY();
//			int dy = y - mLastY;
//
//			if (getScrollY() == 0 && dy > 0) {
//				mParentScrollView.requestDisallowInterceptTouchEvent(true);
//				return false;
//			}
//			mLastY = y;
//			break;
//		case MotionEvent.ACTION_UP:
//			mParentScrollView.requestDisallowInterceptTouchEvent(true);
//			break;
//		}
//		return super.onTouchEvent(ev);
//	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		
//		mParentScrollView.requestDisallowInterceptTouchEvent(false);
//		return super.onInterceptTouchEvent(ev);
//
//	}

	

}
