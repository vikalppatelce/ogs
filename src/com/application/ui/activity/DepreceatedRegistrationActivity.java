package com.application.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitattva.ttogs.R;
import com.application.ui.view.PagerSlidingTabStrip;
import com.application.ui.view.ViewPagerAdapterText;

public class DepreceatedRegistrationActivity extends ActionBarActivity {

	private ViewPager mPager;
	private PagerSlidingTabStrip mPagerSlidingTabStrp;
	private ViewPager.SimpleOnPageChangeListener ViewPagerListener;
	private ActionBar mActionBar;

	private TextView mActionBarTitleTv;
	private ImageView mActionBarDrawerIv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initUi();
		setCustomActionBar();
		setPagerSlidingTabStrip();
	}

	private void initUi() {
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerSlidingTabStrp = (PagerSlidingTabStrip) findViewById(R.id.pager_sliding_tab_strip);
	}

	private void setCustomActionBar() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ab_solid_insurance));
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.action_bar_mother, null);
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBarTitleTv = (TextView) mCustomView
				.findViewById(R.id.actionBarTitle);
		mActionBarDrawerIv = (ImageView) mCustomView
				.findViewById(R.id.drawer_indicator);

		mActionBarTitleTv.setText("Group Chat");
		mActionBarDrawerIv.setVisibility(View.GONE);
	}

	private void setActionBar() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ab_solid_insurance));
		mActionBar.setTitle(Html.fromHtml("<font color='#ffffff'>" + "Login"
				+ "</font>"));
	}

	private void setPagerSlidingTabStrip() {
		ViewPagerAdapterText viewpageradapter = new ViewPagerAdapterText(
				getSupportFragmentManager());
		mPager.setOffscreenPageLimit(3);
		mPager.setAdapter(viewpageradapter);
		mPagerSlidingTabStrp.setShouldExpand(true);
		mPagerSlidingTabStrp.setViewPager(mPager);
		setViewPagerListener();
		// mPagerSlidingTabStrp.setOnPageChangeListener(ViewPagerListener);
	}

	private void setViewPagerListener() {
		ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				// Find the ViewPager Position
				switch (position) {
				case 0:
					mActionBar.setTitle(Html.fromHtml("<font color='#ffffff'>"
							+ "Login" + "</font>"));
					break;
				case 1:
					mActionBar.setTitle(Html.fromHtml("<font color='#ffffff'>"
							+ "Registration" + "</font>"));
					break;
				}
			}
		};
	}

	public void slideToNextTab(int mTab) {
		mPager.setCurrentItem(mTab);
	}
}
