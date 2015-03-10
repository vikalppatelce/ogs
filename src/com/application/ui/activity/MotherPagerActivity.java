package com.application.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.application.ui.view.DrawerArrowDrawable;
import com.application.ui.view.PagerSlidingTabStrip;
import com.application.ui.view.ViewPagerAdapter;
import com.application.utils.BuildVars;
import com.digitattva.ttogs.R;
import com.flurry.android.FlurryAgent;

public class MotherPagerActivity extends ActionBarActivity {

	public static final String TAG = MotherPagerActivity.class.getSimpleName();

	private ViewPager mPager;
	private PagerSlidingTabStrip mPagerSlidingTabStrp;
	private ViewPager.SimpleOnPageChangeListener ViewPagerListener;
	private ActionBar mActionBar;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerTitles;
	private String[] mDrawerDetailTitles;

	DrawerArrayAdapter mDrawerAdapter;

	private SpinnerAdapter mSpinnerAdapter;

	private DrawerArrowDrawable drawerArrowDrawable;
	private float offset;
	private boolean flipped;
	private Resources mResources;

	private ImageView mImageViewActionBar;
	private TextView mTextViewActionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mother_pager);
		initUi();
		setCustomActionBar();
		setPagerSlidingTabStrip();
		setActionBarStyle();
		setDrawerLayout();
	}

	private void initUi() {
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerSlidingTabStrp = (PagerSlidingTabStrip) findViewById(R.id.pager_sliding_tab_strip);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
	}

	private void setPagerSlidingTabStrip() {
		ViewPagerAdapter viewpageradapter = new ViewPagerAdapter(
				getSupportFragmentManager());
		mPager.setOffscreenPageLimit(3);
		mPager.setAdapter(viewpageradapter);
		mPagerSlidingTabStrp.setShouldExpand(true);
		mPagerSlidingTabStrp.setViewPager(mPager);
		setViewPagerListener();
		mPagerSlidingTabStrp.setOnPageChangeListener(ViewPagerListener);
		setActionBarTitle("Attendance", true);
	}

	private void setViewPagerListener() {
		ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				// Find the ViewPager Position
				switch (position) {
				case 0:
					setActionBarTitle("Attendance", true);
					break;
				case 1:
					setActionBarTitle("Premium Calculator", false);
					break;
				case 2:
					setActionBarTitle("Lead Management", false);
					break;
				}
			}
		};
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
		mTextViewActionBar = (TextView) mCustomView
				.findViewById(R.id.actionBarTitle);
		mImageViewActionBar = (ImageView) mCustomView
				.findViewById(R.id.drawer_indicator);
	}

	private void setActionBarTitle(String str, boolean isList) {
		// getSupportActionBar().setTitle(
		// Html.fromHtml("<font color='#ffffff'>" + str + "</font>"));
		mTextViewActionBar.setText(str);
	}

	private void setActionBarStyle() {
		try {
			// mActionBar = getSupportActionBar();
			// mActionBar.setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.ab_solid_insurance));

			// setActionBarTitle(getResources().getString(R.string.app_name),
			// false);

			TextView titleView = (TextView) findViewById(R.id.action_bar_title);
			if (titleView == null) {
				int titleId = Resources.getSystem().getIdentifier(
						"action_bar_title", "id", "android");
				titleView = (TextView) findViewById(titleId);
				titleView.setTextColor(Color.parseColor("#FFFFFF"));
			} else {
				titleView.setTextColor(Color.parseColor("#FFFFFF"));
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	private void setDrawerLayout() {
		mResources = getResources();
		drawerArrowDrawable = new DrawerArrowDrawable(mResources);
		drawerArrowDrawable.setStrokeColor(mResources
				.getColor(android.R.color.white));
		mImageViewActionBar.setImageDrawable(drawerArrowDrawable);

		mTitle = mDrawerTitle = getTitle();
		mDrawerTitles = getResources()
				.getStringArray(R.array.drawer_menu_array);
		mDrawerDetailTitles = getResources().getStringArray(
				R.array.drawer_menu_detail_array);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerAdapter = new DrawerArrayAdapter(this, mDrawerTitles,
				mDrawerDetailTitles);
		mDrawerList.setAdapter(mDrawerAdapter);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mImageViewActionBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDrawerLayout.isDrawerVisible(Gravity.START)) {
					mDrawerLayout.closeDrawer(Gravity.START);
				} else {
					mDrawerLayout.openDrawer(Gravity.START);
				}
			}
		});

		mDrawerLayout
				.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
					@Override
					public void onDrawerSlide(View drawerView, float slideOffset) {
						offset = slideOffset;

						// Sometimes slideOffset ends up so close to but not
						// quite 1 or 0.
						if (slideOffset >= .995) {
							flipped = true;
							drawerArrowDrawable.setFlip(flipped);
						} else if (slideOffset <= .005) {
							flipped = false;
							drawerArrowDrawable.setFlip(flipped);
						}

						drawerArrowDrawable.setParameter(offset);
					}
				});
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		Fragment fragment = null;
		Bundle args = new Bundle();
		Intent drawerIntent = null;
		switch (position) {
		default:
			break;
		}
		mDrawerLayout.closeDrawer(mDrawerList);
		if (drawerIntent != null)
			startActivity(drawerIntent);
	}

	public class DrawerArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;
		private final String[] values2;

		public DrawerArrayAdapter(Context context, String[] values,
				String[] values2) {
			super(context, R.layout.item_list_drawer, values);
			this.context = context;
			this.values = values;
			this.values2 = values2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.item_list_drawer, parent,
					false);

			FrameLayout mDrawerProfileLayout = (FrameLayout) rowView
					.findViewById(R.id.drawer_layout_profile);
			LinearLayout mDrawerMenuLayout = (LinearLayout) rowView
					.findViewById(R.id.drawer_layout_menu);

			final ImageView mDrawerProfileImageView = (ImageView) rowView
					.findViewById(R.id.drawer_profile_image);

			TextView mDrawerProfileFullNameView = (TextView) rowView
					.findViewById(R.id.drawer_profile_full_name);
			TextView mDrawerTitleView = (TextView) rowView
					.findViewById(R.id.drawer_layout_menu_title);
			TextView mDrawerTitleSubView = (TextView) rowView
					.findViewById(R.id.drawer_layout_menu_subtitle);
			ImageView mDrawerIconView = (ImageView) rowView
					.findViewById(R.id.drawer_layout_menu_icon);

			switch (position) {
			case 0:
				mDrawerMenuLayout.setVisibility(View.GONE);
				mDrawerProfileFullNameView.setText("");
				break;
			case 1:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			case 2:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			default:
				break;
			}
			return rowView;
		}
	}
	
	/*
	 * Flurry Analytics
	 */
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, BuildVars.FLURRY_ID);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}
