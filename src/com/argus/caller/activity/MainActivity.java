package com.argus.caller.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.argus.caller.R;
import com.argus.caller.recordervice.Constant;
import com.argus.caller.recordervice.ContacterSyncService;
import com.argus.caller.recordervice.phoneService;
import com.argus.caller.search.ContactsActivity;
import com.argus.caller.setting.MessageSettingActivity;
import com.argus.caller.setting.RecordSettingActivity;
import com.argus.caller.view.MyDrawerLayout;
import com.argus.caller.view.SimpleViewPagerIndicator;
import com.argus.caller.view.SimpleViewPagerIndicator.OnIndicatorSelectListener;
import com.argus.caller.view.ViewPagerScroller;
import com.nineoldandroids.view.ViewHelper;

public class MainActivity extends FragmentActivity {

	private String[] mTitles = new String[] { "Recents", "Voice box", "Filter" };
	private SimpleViewPagerIndicator mIndicator;
	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private ScrollView mParentScrollView;
	private RelativeLayout mtoptitles;
	private TabFragment[] mFragments = new TabFragment[mTitles.length];
	private MyDrawerLayout mDrawerLayout;
	Button search_button, menu_button = null;
	private LazyFragment mfragment1;
	private LazyFragment mfragment2;
	private LazyFragment mfragment3;
	private List<Fragment> fragmentList;
	private String path;
	private File file;
	private static Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
//		phonelistener.getInstance().queryContacts(
//				getBaseContext());
		SharedPreferences DefaultPath = getSharedPreferences("text1",
				Context.MODE_PRIVATE);
		String DefaultPathString = DefaultPath.getString("text1", null);
		if (DefaultPathString == null) {
			// String text = "/storage/emulated/0/jjjj";
			String text = Environment.getExternalStorageDirectory()
					.getAbsoluteFile() + "/Argus";
			File file = new File(text);
			if (!file.exists()) {
				file.mkdirs();
			}
			Editor edit = DefaultPath.edit();
			edit.putString("text1", text);
			edit.commit();

			String text2 = text + "/normal/";
			File normalfile = new File(text2);
			if (!normalfile.exists()) {
				normalfile.mkdirs();
			}
			SharedPreferences perferences2 = getSharedPreferences("text2",
					Context.MODE_PRIVATE);
			Editor edit2 = perferences2.edit();
			edit2.putString("text2", text2);
			edit2.commit();
		}

		// 设置文件存储路径
		SharedPreferences preference = getSharedPreferences("text2",
				Context.MODE_PRIVATE);
		String valueText = preference.getString("text2", null);
		path = valueText;
		if (path != null) {
			File normalfile = new File(path);
			if (!normalfile.exists()) {
				normalfile.mkdirs();
			}
			String path1 = path + Constant.numberToCall;
			File f1 = new File(path1);
			if (!f1.exists()) {
				f1.mkdirs();
			}
			file = new File(path1, Constant.str + ".3gp");
		}

		// 启动服务

		Intent it = new Intent(MainActivity.this, phoneService.class);
		startService(it);
		Intent it2 = new Intent(MainActivity.this, ContacterSyncService.class);
		startService(it2);

		mfragment1 = new Fragment1();
		mfragment2 = new fragment2();
		mfragment3 = new fragment3();

		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(mfragment1);
		fragmentList.add(mfragment2);
		fragmentList.add(mfragment3);

		initLeftMenuView();
		initLeftMenuEvents();
		initListsViews();
		initListDatas();
		initListEvents();

		RelativeLayout Record_Setting = (RelativeLayout) findViewById(R.id.Record_Setting);
		Record_Setting.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MessageSettingActivity.class);
				startActivity(intent);
			}
		});

//		RelativeLayout Message_Setting = (RelativeLayout) findViewById(R.id.Message_Setting);
//		Message_Setting.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(MainActivity.this, MessageSettingActivity.class);
//				startActivity(intent);
//			}
//		});

		/*
		 * final Button record = (Button)findViewById(R.id.record_button); if
		 * (!Constant.b) { record.setText("已关闭"); }else { record.setText("已开启");
		 * } record.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { if (Constant.b) { Constant.b
		 * = false; record.setText("已关闭"); } else { Constant.b = true;
		 * record.setText("已开启"); }
		 * 
		 * } });
		 */
		ImageButton search = (ImageButton) findViewById(R.id.search_button);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ContactsActivity.class);
				startActivity(intent);
			}
		});

	}

	private void initLeftMenuView() {
		mDrawerLayout = (MyDrawerLayout) findViewById(R.id.id_drawerLayout);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
				Gravity.LEFT);
	}

	private void initLeftMenuEvents() {
		mDrawerLayout.setDrawerListener(new DrawerListener() {
			@Override
			public void onDrawerStateChanged(int newState) {
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				View mContent = mDrawerLayout.getChildAt(0);
				View mMenu = drawerView;

				/*
				 * float scale = 1 - slideOffset; float rightScale = 0.8f +
				 * scale * 0.2f; float leftScale = 1 - 0.3f * scale;
				 * 
				 * ViewHelper.setScaleX(mMenu, leftScale);
				 * ViewHelper.setScaleY(mMenu, leftScale);
				 * ViewHelper.setAlpha(mMenu, 0.1f + 0.9f * slideOffset);
				 */

				ViewHelper.setTranslationX(mContent, mMenu.getMeasuredWidth()
						* slideOffset);

				/*
				 * ViewHelper.setPivotX(mContent, 0);
				 * ViewHelper.setPivotY(mContent, mContent.getMeasuredHeight() /
				 * 2); mContent.invalidate(); ViewHelper.setScaleX(mContent,
				 * rightScale); ViewHelper.setScaleY(mContent, rightScale);
				 */

			}

			@Override
			public void onDrawerOpened(View drawerView) {
			}

			@Override
			public void onDrawerClosed(View drawerView) {
			}

		});
	}

	public void OpenLeftMenu(View view) {
		mDrawerLayout.openDrawer(Gravity.LEFT);
	}

	private void initListsViews() {
		mIndicator = (SimpleViewPagerIndicator) findViewById(R.id.id_indicator);
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
		// mParentScrollView = (ScrollView) findViewById(R.id.id_sv_wrapper);
		mtoptitles = (RelativeLayout) findViewById(R.id.id_top);
		// mIndicator.setParentScrollView(mParentScrollView);
	}

	private void initListDatas() {
		mIndicator.setTitles(mTitles);

		for (int i = 0; i < mTitles.length; i++) {
			mFragments[i] = (TabFragment) TabFragment.newInstance(mTitles[i]);
			// mFragments[i].setParentScrollView(mParentScrollView);
		}

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return fragmentList.size();
				// return mTitles.length;
			}

			@Override
			public Fragment getItem(int arg0) {
				return fragmentList.get(arg0);
				// return mFragments[position];
			}

		};

		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(0);

	}

	private void initListEvents() {
		// ViewTreeObserver vto = mViewPager.getViewTreeObserver();
		// vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		//
		// public void onGlobalLayout() {
		// mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(
		// this);
		// LayoutParams params = mViewPager.getLayoutParams();
		// params.height = getWindow().getDecorView()
		// .findViewById(android.R.id.content).getHeight()
		// - mIndicator.getHeight() - mtoptitles.getHeight();
		// mViewPager.setLayoutParams(params);
		// }
		// });

		final ViewPagerScroller scroller = new ViewPagerScroller(
				getBaseContext());

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// Log.d("TEST", "onPageSelected--position:" + position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				scroller.setScrollDuration(500);
				mIndicator.scroll(position, positionOffset);
				// Log.d("TEST", "onPageScrolled--position:" + position
				// + "   positionOffset:" + positionOffset);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// Log.d("TEST", "onPageScrollStateChanged--state:" + state);

			}
		});

		mIndicator
				.setOnIndicatorSelectListener(new OnIndicatorSelectListener() {
					@Override
					public void onSelected(int position) {
						Log.d("TEST",
								"onSelected:" + mViewPager.getCurrentItem());

						if ((mViewPager.getCurrentItem() - position) == 1
								|| (mViewPager.getCurrentItem() - position) == -1) {
							scroller.setScrollDuration(600);

						} else {
							scroller.setScrollDuration(900);

						}
						mViewPager.setCurrentItem(position);
					}

				});
		scroller.initViewPagerScroll(mViewPager);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			} else {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			}
			return true; // 最后，一定要做完以后返回 true，或者在弹出菜单后返回true，其他键返回super，让其他键默认
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		/*mfragment1.ReLoad();
		mfragment2.ReLoad();
		mfragment3.ReLoad();*/
	}

}
