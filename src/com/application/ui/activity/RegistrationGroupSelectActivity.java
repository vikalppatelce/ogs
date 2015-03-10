/*
 * This is the source code of Telegram for Android v. 1.3.2.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013.
 */

package com.application.ui.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitattva.ttogs.R;
import com.flurry.android.FlurryAgent;
import com.application.ui.view.CountryAdapter;
import com.application.ui.view.CountryAdapter.Country;
import com.application.ui.view.CountrySearchAdapter;
import com.application.ui.view.Crouton;
import com.application.ui.view.SectionsListView;
import com.application.ui.view.Style;
import com.application.utils.AndroidUtilities;
import com.application.utils.AppConstants;
import com.application.utils.ApplicationLoader;
import com.application.utils.BuildVars;
import com.application.utils.LocaleController;
import com.application.utils.RequestBuilder;
import com.application.utils.RestClient;
import com.application.utils.Utilities;

@SuppressLint("NewApi")
public class RegistrationGroupSelectActivity extends ActionBarActivity {

	public static interface CountrySelectActivityDelegate {
		public abstract void didSelectCountry(String name);
	}

	private SectionsListView listView;
	private TextView emptyTextView;
	private CountryAdapter listViewAdapter;
	private CountrySearchAdapter searchListViewAdapter;

	private boolean searchWas;
	private boolean searching;

	private FrameLayout mFrameLayout;

	private TextView mActionBarTitleTv;
	private ImageView mActionBarSearchIconIv;
	private ImageView mActionBarSearchCloseIv;
	private EditText mActionBarSearchBoxEdt;

	private RelativeLayout mActionBarTitleLayout;
	private RelativeLayout mActionBarSearchLayout;

	private CountrySelectActivityDelegate delegate;

	private Intent mIntent;
	private Button mRetryBtn;

	private static final String TAG = RegistrationGroupSelectActivity.class
			.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_city);
		initUi();
		hideActionBar();
		setCustomActionBar();
		getIntentFromActivity();
	}

	private void initUi() {
		mFrameLayout = (FrameLayout) findViewById(R.id.cityframeLayout);
	}

	private void getIntentFromActivity() {
		mIntent = getIntent();
	}

	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	private void hideActionBar() {
		try {
			if (Build.VERSION.SDK_INT < 11) {
				getSupportActionBar().hide();
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	private void getDataFromApi() {
		if (Utilities.isInternetConnected()) {
			new AsyncTaskCity(RegistrationGroupSelectActivity.this,
					buildJSONRequestForCity()).execute();
		} else {
			Crouton.makeText(
					RegistrationGroupSelectActivity.this,
					ApplicationLoader.getApplication().getApplicationContext()
							.getResources()
							.getString(R.string.no_internet_connection),
					Style.ALERT).show();
		}
	}

	private void loadUiWithList(String mResponseFromApi) {
		if (mFrameLayout != null) {
			searching = false;
			searchWas = false;

			listViewAdapter = new CountryAdapter(
					RegistrationGroupSelectActivity.this, mResponseFromApi,false);
			searchListViewAdapter = new CountrySearchAdapter(
					RegistrationGroupSelectActivity.this,
					listViewAdapter.getCountries());

			LinearLayout emptyTextLayout = new LinearLayout(
					RegistrationGroupSelectActivity.this);
			emptyTextLayout.setVisibility(View.INVISIBLE);
			emptyTextLayout.setOrientation(LinearLayout.VERTICAL);
			((FrameLayout) mFrameLayout).addView(emptyTextLayout);
			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) emptyTextLayout
					.getLayoutParams();
			layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
			layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
			layoutParams.gravity = Gravity.TOP;
			emptyTextLayout.setLayoutParams(layoutParams);
			emptyTextLayout.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});

			emptyTextView = new TextView(RegistrationGroupSelectActivity.this);
			emptyTextView.setTextColor(0xff808080);
			emptyTextView.setTextSize(20);
			emptyTextView.setGravity(Gravity.CENTER);
			emptyTextView.setText(LocaleController.getString("NoResult",
					R.string.NoResult));
			emptyTextLayout.addView(emptyTextView);
			LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) emptyTextView
					.getLayoutParams();
			layoutParams1.width = LinearLayout.LayoutParams.MATCH_PARENT;
			layoutParams1.height = LinearLayout.LayoutParams.MATCH_PARENT;
			layoutParams1.weight = 0.5f;
			emptyTextView.setLayoutParams(layoutParams1);

			FrameLayout frameLayout = new FrameLayout(
					RegistrationGroupSelectActivity.this);
			emptyTextLayout.addView(frameLayout);
			layoutParams1 = (LinearLayout.LayoutParams) frameLayout
					.getLayoutParams();
			layoutParams1.width = LinearLayout.LayoutParams.MATCH_PARENT;
			layoutParams1.height = LinearLayout.LayoutParams.MATCH_PARENT;
			layoutParams1.weight = 0.5f;
			frameLayout.setLayoutParams(layoutParams1);

			listView = new SectionsListView(
					RegistrationGroupSelectActivity.this);
			listView.setEmptyView(emptyTextLayout);
			listView.setVerticalScrollBarEnabled(false);
			listView.setDivider(null);
			listView.setDividerHeight(0);
			listView.setFastScrollEnabled(true);
			listView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
			listView.setAdapter(listViewAdapter);
			if (Build.VERSION.SDK_INT >= 11) {
				listView.setFastScrollAlwaysVisible(true);
				listView.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT
						: ListView.SCROLLBAR_POSITION_RIGHT);
			}
			((FrameLayout) mFrameLayout).addView(listView);
			layoutParams = (FrameLayout.LayoutParams) listView
					.getLayoutParams();
			layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
			layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
			listView.setLayoutParams(layoutParams);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view,
						int i, long l) {
					Country country = null;
					if (searching && searchWas) {
						country = searchListViewAdapter.getItem(i);
					} else {
						int section = listViewAdapter.getSectionForPosition(i);
						int row = listViewAdapter
								.getPositionInSectionForPosition(i);
						if (row < 0 || section < 0) {
							return;
						}
						country = listViewAdapter.getItem(section, row);
					}
					if (i < 0) {
						return;
					}
					// if (country != null && delegate != null) {
					// delegate.didSelectCountry(country.name);
					if (country != null) {
						Intent mIntent = new Intent();
						mIntent.putExtra("group_id", country.code);
						mIntent.putExtra("group_name", country.name);
						setResult(RESULT_OK, mIntent);
						finish();
					}
				}
			});

			listView.setOnScrollListener(new AbsListView.OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView absListView, int i) {
					if (i == SCROLL_STATE_TOUCH_SCROLL && searching
							&& searchWas) {
						AndroidUtilities
								.hideKeyboard(RegistrationGroupSelectActivity.this
										.getCurrentFocus());
					}
				}

				@Override
				public void onScroll(AbsListView absListView,
						int firstVisibleItem, int visibleItemCount,
						int totalItemCount) {
					if (absListView.isFastScrollEnabled()) {
						AndroidUtilities.clearDrawableAnimation(absListView);
					}
				}
			});
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(Utilities.isInternetConnected()){
			getDataFromApi();	
		}else{
			mRetryBtn = new Button(RegistrationGroupSelectActivity.this);
			mRetryBtn.setText("Retry");
			FrameLayout.LayoutParams mParams = new FrameLayout.LayoutParams(
					100, 100);
			mParams.gravity = Gravity.CENTER;
			mFrameLayout.addView(mRetryBtn, mParams);
			setRetryListener(mRetryBtn);
		}
		if (listViewAdapter != null) {
			listViewAdapter.notifyDataSetChanged();
		}
	}

	public void setCountrySelectActivityDelegate(
			CountrySelectActivityDelegate delegate) {
		this.delegate = delegate;
	}

	private void setCustomActionBar() {
		mActionBarTitleTv = (TextView) findViewById(R.id.actionBarTitle);
		mActionBarSearchCloseIv = (ImageView) findViewById(R.id.actionBarSearchBoxClose);
		mActionBarSearchIconIv = (ImageView) findViewById(R.id.actionBarSearchIcon);
		mActionBarSearchBoxEdt = (EditText) findViewById(R.id.actionBarSearchBox);
		mActionBarTitleLayout = (RelativeLayout) findViewById(R.id.actionBarTitleLayout);
		mActionBarSearchLayout = (RelativeLayout) findViewById(R.id.actionBarSearchLayout);

		mActionBarTitleLayout.setVisibility(View.VISIBLE);
		mActionBarTitleTv.setText("Choose Group");

		setCustomActionBarListener();
	}

	private void setCustomActionBarListener() {
		mActionBarSearchCloseIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActionBarSearchLayout.setVisibility(View.GONE);
				mActionBarTitleLayout.setVisibility(View.VISIBLE);
			}
		});

		mActionBarSearchIconIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActionBarSearchLayout.setVisibility(View.VISIBLE);
				mActionBarTitleLayout.setVisibility(View.GONE);
			}
		});

		mActionBarSearchBoxEdt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable mEditText) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(mEditText.toString().toLowerCase())) {
					searchListViewAdapter.search(mEditText.toString());
					searchWas = true;
					listView.setAdapter(searchListViewAdapter);

					if (android.os.Build.VERSION.SDK_INT >= 11) {
						listView.setFastScrollAlwaysVisible(false);
					}
					listView.setFastScrollEnabled(false);
					listView.setVerticalScrollBarEnabled(true);
				} else {
					searchListViewAdapter.search(null);
					searching = false;
					searchWas = false;
					listView.setAdapter(listViewAdapter);
					if (android.os.Build.VERSION.SDK_INT >= 11) {
						listView.setFastScrollAlwaysVisible(true);
					}
					listView.setFastScrollEnabled(true);
					listView.setVerticalScrollBarEnabled(false);
				}
			}
		});
	}

	public class AsyncTaskCity extends AsyncTask<Void, Void, Void> {
		private Context mContext;
		private ProgressBar mProgressBar;
		private Button mRetryBtn;
		private String mResponseFromApi;
		private boolean mIsCityData = false;
		private boolean mIsSuccess = false;
		private JSONObject mJSONObjectToSend;

		public AsyncTaskCity(Context mContext, JSONObject mJSONObjectToSend) {
			super();
			this.mJSONObjectToSend = mJSONObjectToSend;
			this.mContext = mContext;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressBar = new ProgressBar(mContext);
			mProgressBar.setIndeterminate(true);
			FrameLayout.LayoutParams mParams = new FrameLayout.LayoutParams(
					100, 100);
			mParams.gravity = Gravity.CENTER;
			mFrameLayout.addView(mProgressBar, mParams);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				mResponseFromApi = RestClient.postJSON(
						AppConstants.API.URL, mJSONObjectToSend);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (!TextUtils.isEmpty(mResponseFromApi) || BuildVars.DEBUG_VERSION) {
				if (BuildVars.DEBUG_VERSION) {
					mResponseFromApi = Utilities.readFile("apiCity.json");
				}
				try {
					JSONObject mJSONObject = new JSONObject(mResponseFromApi);
					mIsSuccess = mJSONObject.getBoolean("success");
					if(mIsSuccess){
						JSONObject mJSONObjectInner = mJSONObject.getJSONObject("data");
						JSONArray mJSONArray = mJSONObjectInner.getJSONArray("cities");
						if (mJSONArray.length() > 0) {
							mIsCityData = true;
						}	
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mIsCityData = false;
				}
			}

			if (mProgressBar != null) {
				mFrameLayout.removeView(mProgressBar);
			}

			if (mIsCityData) {
				loadUiWithList(mResponseFromApi);
			} else {
				mRetryBtn = new Button(mContext);
				mRetryBtn.setText("Retry");
				FrameLayout.LayoutParams mParams = new FrameLayout.LayoutParams(
						100, 100);
				mParams.gravity = Gravity.CENTER;
				mFrameLayout.addView(mRetryBtn, mParams);
				setRetryListener(mRetryBtn);
			}
		}
	}

	private JSONObject buildJSONRequestForCity() {
		return RequestBuilder.getPostCity();
	}

	private void setRetryListener(Button mButton) {
		mFrameLayout.removeView(mRetryBtn);
		mButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getDataFromApi();
			}
		});

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
