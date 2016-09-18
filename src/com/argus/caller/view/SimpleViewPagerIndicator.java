package com.argus.caller.view;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleViewPagerIndicator extends LinearLayout {

	private static final int COLOR_TEXT_NORMAL = 0xFFFFFFFF;
	private static final int COLOR_INDICATOR_COLOR = Color.YELLOW;
	CharSequence k = "0";
	private String[] mTitles;
	private int mTabCount;
	private int mIndicatorColor = COLOR_INDICATOR_COLOR;
	private float mTranslationX;
	private Paint mPaint = new Paint();
	private int mTabWidth;
	
	
	public SimpleViewPagerIndicator(Context context) {
		this(context, null);
	}

	public SimpleViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint.setColor(mIndicatorColor);
		mPaint.setStrokeWidth(9.0F);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mTabWidth = w / mTabCount;
	}

	public void setTitles(String[] titles) {
		mTitles = titles;
		mTabCount = titles.length;
		generateTitleView();

	}

	public void setIndicatorColor(int indicatorColor) {
		this.mIndicatorColor = indicatorColor;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		canvas.save();
		canvas.translate(mTranslationX, getHeight() - 2);
		canvas.drawLine(0, 0, mTabWidth, 0, mPaint);
		canvas.restore();
	}

	public void scroll(int position, float offset) {
		/**
		 * <pre>
		 *  0-1:position=0 ;1-0:postion=0;
		 * </pre>
		 */
		mTranslationX = getWidth() / mTabCount * (position + offset);
		invalidate();
	}

//	private ScrollView mParentScrollView;
//
//	public void setParentScrollView(ScrollView scrollView) {
//		this.mParentScrollView = scrollView;
//	}
//
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		if (mParentScrollView.getScrollY() + mParentScrollView.getHeight() == mParentScrollView
//				.getChildAt(0).getHeight()) {
//			mParentScrollView.requestDisallowInterceptTouchEvent(true);
//		}
//		return super.dispatchTouchEvent(ev);
//	}

	private void generateTitleView() {
		if (getChildCount() > 0)
			this.removeAllViews();
		int count = mTitles.length;

		setWeightSum(count);
		final TextView[] tv = new TextView[count];
		for (int i = 0; i < count; i++) {
			tv[i] = new TextView(getContext());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
					LayoutParams.MATCH_PARENT);
			lp.weight = 1;
			tv[i].setGravity(Gravity.CENTER);
			tv[i].setTextColor(COLOR_TEXT_NORMAL);
			tv[i].setText(mTitles[i]);
			tv[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			tv[i].setLayoutParams(lp);
			tv[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = -1;
					for(int i = 0; i < tv.length; i++){
						if(v == tv[i]){
							position = i;
							break;
						}
					}
					//Toast.makeText(getContext(), tv[position].getText(),Toast.LENGTH_SHORT).show();
					mListener.onSelected(position);
				}
			});
			addView(tv[i]);
		}
	}

	public interface OnIndicatorSelectListener{
		public void onSelected(int position);
	}
	
	private OnIndicatorSelectListener mListener;
	public void setOnIndicatorSelectListener(OnIndicatorSelectListener listener){
		mListener = listener;
	}
}
