package com.argus.caller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class YinDaoActivity extends Activity implements OnClickListener,
		OnPageChangeListener {

	private ViewPager vp;
	private YinDaoAdapter vpAdapter;
	private List<View> views;
	private Button bt_coming;
	// 引用图片资源
	private static final int[] pics = { R.drawable.guide1, R.drawable.guide2 };
	// 底部小点
	private ImageView[] dots;
	// 记录当前选中位置
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_yin_dao);
		bt_coming = (Button) findViewById(R.id.bt_coming);
		views = new ArrayList<View>();
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		// 初始化引导图片
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setImageResource(pics[i]);
			views.add(iv);
		}
		vp = (ViewPager) findViewById(R.id.viewpager);
		// 初始化Adapter
		vpAdapter = new YinDaoAdapter(views);
		vp.setAdapter(vpAdapter);
		// 绑定回调
		vp.setOnPageChangeListener(this);
		// 初始化底部小点
		initDots();
		bt_coming.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(YinDaoActivity.this, DiaLogActivity.class);
				YinDaoActivity.this.startActivity(intent);
				finish();
			}
		});
	}

	private void initDots() {
		LinearLayout linear_vp = (LinearLayout) findViewById(R.id.linear_viewpager);
		dots = new ImageView[pics.length];
		// 循环取得底部小点
		for (int i = 0; i < pics.length; i++) {
			dots[i] = (ImageView) linear_vp.getChildAt(i);
			dots[i].setEnabled(true);// 设为灰色
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);// 设置位置tag,方便与当期那位置
		}
		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 选中为白色
	}

	// 设置当前引导页
	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {
			return;
		}
		vp.setCurrentItem(position);
	}

	// 设置当前引导页小点
	private void setCurDot(int position) {
		if (position < 0 || position > pics.length - 1
				|| currentIndex == position) {
			return;
		}
		dots[position].setEnabled(false);
		dots[currentIndex].setEnabled(true);
		currentIndex = position;
	}

	// 当滑动状态时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	// 当当前界面被滑动时调动
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点为选中状态
		setCurDot(arg0);
		if (arg0 == 1) {
			bt_coming.setVisibility(View.VISIBLE);
		} else {
			bt_coming.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
	}

}
