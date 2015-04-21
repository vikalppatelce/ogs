/**
 * 
 */
package com.application.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.application.utils.BuildVars;
import com.chat.ttogs.R;
import com.flurry.android.FlurryAgent;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class TermsAndConditionActivity extends ActionBarActivity {

	private static final String TAG = TermsAndConditionActivity.class.getSimpleName();
	
	private TextView actionBarTitle;
	
	private TextView mTermsText;
	private WebView mWebView;
	private WebView mWebView2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_terms);
		hideActionBar();
		initUi();
		setUiListener();
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
	
	private void setUiListener(){
		
	}
	
	private void initUi(){
		actionBarTitle = (TextView)findViewById(R.id.actionBarTitle);
		mTermsText = (TextView)findViewById(R.id.termsText);
//		mWebView = (WebView)findViewById(R.id.webView);
//		mWebView.setBackgroundColor(0x00000000);
//		
//		mWebView2 = (WebView)findViewById(R.id.webView2);
//		mWebView2.setBackgroundColor(0x00000000);
		actionBarTitle.setText("Terms & Conditions");
		setTermsData();
//		setWebData();
	}
	
	private void setTermsData(){
		String mTermsString = "1. We cater only to Tour & Travel Operators.\n\n2. We only provide links between Tours & Travel operators.\n\n3. We are a mere venue / platform for our members to interact / negotiate for buying, selling and their business services by way of locating companies to trade with through the leads provided.\n\n"
				+ "4. We do not take part in the actual transaction that take place between the buyers and sellers and hence are not a party to any agreement / contract for sale negotiated between buyers and sellers. All transactions will be the responsibilities of the members only. This agreement shall not be deemed to create any partnership, joint venture, or other joint business relationship between Tours & Travels Operator Guide Service and other party.\n\n5. We are not liable / responsible for any disputes such as rates, misunderstanding, breakdown etc. etc. between the Clients / Parties and Tour & Travel Operators.\n\n6. We are not committed for any specific number of leads in a day.\n\n7. In case of any discrepancies during the journey needs to be sorted out by the operator. We hold no responsibilities or liabilities of either side\n\n8. The subscription is valid for a period of 365 days from the date of invoice.\n\n9. This subscription is eligible for ONLY ONE mobile number.\n";
		mTermsText.setText(mTermsString);
	}
	
	private void setWebData(){
//		String text= "1. We cater only to Tour & Travel Operators.</br></br>2. We only provide links between Tours & Travel operators.</br></br>3. We are a mere venue / platform for our members to interact / negotiate for buying, selling and their business services by way of locating companies to trade with through the leads provided.</br></br>4. We do not take part in the actual transaction that take place between the buyers and sellers and hence are not a party to any agreement / contract for sale negotiated between buyers and sellers. All transactions will be the responsibilities of the members only. This agreement shall not be deemed to create any partnership, joint venture, or other joint business relationship between Tours & Travels Operator Guide Service and other party.</br></br>5. We are not liable / responsible for any disputes such as rates, misunderstanding, breakdown etc. etc. between the Clients / Parties and Tour & Travel Operators.</br></br>6. We are not committed for any specific number of leads in a day.</br></br>7. In case of any discrepancies during the journey needs to be sorted out by the operator. We hold no responsibilities or liabilities of either side.</br></br>8. The subscription is valid for a period of 365 days from the date of invoice.</br></br>9. This subscription is eligible for ONLY ONE mobile number.</br></br>";
		String text = "1. We cater only to Tour & Travel Operators.<br/><br/>"
				+ "2. We only provide links between Tours & Travel operators.<br/><br/>3. We are a mere venue / platform for our members to interact / negotiate for buying, selling and their business services by way of locating companies to trade with through the leads provided.<br/><br/>"
				+ "4. We do not take part in the actual transaction that take place between the buyers and sellers and hence are not a party to any agreement / contract for sale negotiated between buyers and sellers. All transactions will be the responsibilities of the members only. This agreement shall not be deemed to create any partnership, joint venture, or other joint business relationship between Tours & Travels Operator Guide Service and other party.<br/><br/>"
				+ "5. We are not liable / responsible for any disputes such as rates, misunderstanding, breakdown etc. etc. between the Clients / Parties and Tour & Travel Operators.<br/><br/>"
				+ "We are not committed for any specific number of leads in a day.<br/><br/>";
//		String text = "abc";
		String htmlText = "<html><body style=\"text-align:justify\"><font size=\"4\" color=\"#FFFFFF\"> %s </font></body></Html>";
		mWebView.setWebViewClient(new CustomWebViewClient());
        mWebView.loadUrl("file:///android_asset/termsandcondition.html");
        mWebView2.setWebViewClient(new CustomWebViewClient());
        mWebView2.loadUrl("file:///android_asset/termsandcondition2.html");
//		mWebView.loadData(String.format(htmlText, text), "text/html", "utf-8");
	}

	private class CustomWebViewClient extends WebViewClient {
	    @Override
	    public void onPageFinished(WebView view, String url) {
	        super.onPageFinished(view, url);
	        view.loadUrl(url);
	    }
	}
	
	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	
	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
	    switch(keycode) {
	        case KeyEvent.KEYCODE_MENU:
	            return true;
	    }
	    return super.onKeyDown(keycode, e);
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
