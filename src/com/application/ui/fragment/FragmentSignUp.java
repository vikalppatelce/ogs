package com.application.ui.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.chat.ttogs.R;
import com.application.ui.activity.DepreceatedRegistrationActivity;
import com.application.ui.view.Crouton;
import com.application.ui.view.Style;
import com.application.utils.AppConstants;
import com.application.utils.RequestBuilder;
import com.application.utils.RestClient;
import com.application.utils.Utilities;

public class FragmentSignUp extends Fragment {

	private EditText mEditFirstName;
	private EditText mEditLastName;
	private EditText mEditUserName;
	private EditText mEditPassword;
	private EditText mEditConfirmPassword;
	private EditText mEditEmailAddress;
	private EditText mEditMobileNumber;
	private EditText mEditGender;
	private EditText mEditCountry;
	private EditText mEditCity;
	private EditText mEditState;

	private Button mBtnRegister;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater
				.inflate(R.layout.fragment_signup, container, false);
		initUi(view);
		return view;
	}

	private void initUi(View view) {
		mEditFirstName = (EditText) view.findViewById(R.id.firstName);
		mEditLastName = (EditText) view.findViewById(R.id.lastName);
		mEditEmailAddress = (EditText) view.findViewById(R.id.emailAddress);
		mEditConfirmPassword = (EditText) view
				.findViewById(R.id.confirmPassword);
		mEditPassword = (EditText) view.findViewById(R.id.password);
		mEditUserName = (EditText) view.findViewById(R.id.userName);

		mEditMobileNumber = (EditText) view.findViewById(R.id.mobileNumber);
		mEditGender = (EditText) view.findViewById(R.id.gender);
		mEditCountry = (EditText) view.findViewById(R.id.country);
		mEditState = (EditText) view.findViewById(R.id.state);
		mEditCity = (EditText) view.findViewById(R.id.city);

		mBtnRegister = (Button) view.findViewById(R.id.registerBtn);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setUiListener();
	}

	private void setUiListener() {
		mBtnRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Crouton.cancelAllCroutons();
				if (validateFields()) {
					if (Utilities.isInternetConnected()) {
						new AysncTaskRegistration(
								buildJSONRequestForRegistration()).execute();
					} else {
						Crouton.makeText(
								getActivity(),
								getActivity().getResources().getString(
										R.string.no_internet_connection),
								Style.ALERT).show();
					}
				}
			}
		});
	}

	private boolean validateFields() {
		if (TextUtils.isEmpty(mEditFirstName.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter First Name",
					Style.ALERT).show();
			return false;
		}

		if (TextUtils.isEmpty(mEditLastName.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter Last Name",
					Style.ALERT).show();
			return false;
		}

		if (TextUtils.isEmpty(mEditEmailAddress.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter Email Address",
					Style.ALERT).show();
			return false;
		}

		if (TextUtils.isEmpty(mEditPassword.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter Password",
					Style.ALERT).show();
			return false;
		}

		if (TextUtils.isEmpty(mEditConfirmPassword.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter Confirm Password",
					Style.ALERT).show();
			return false;
		}

		if (TextUtils.isEmpty(mEditUserName.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter Username",
					Style.ALERT).show();
			return false;
		}

		if (TextUtils.isEmpty(mEditCountry.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter Country", Style.ALERT)
					.show();
			return false;
		}

		if (TextUtils.isEmpty(mEditState.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter State", Style.ALERT)
					.show();
			return false;
		}

		if (TextUtils.isEmpty(mEditCity.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter City", Style.ALERT)
					.show();
			return false;
		}

		if (TextUtils.isEmpty(mEditMobileNumber.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter Mobile Number",
					Style.ALERT).show();
			return false;
		}

		if (TextUtils.isEmpty(mEditGender.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter Gender", Style.ALERT)
					.show();
			return false;
		}

		if (!TextUtils.isEmpty(mEditFirstName.getText().toString())) {
			Pattern p = Pattern.compile("[a-zA-Z]+");
			Matcher matcher = p.matcher(mEditFirstName.getText().toString());
			if (!matcher.matches()) {
				Crouton.makeText(getActivity(),
						"Please Enter Valid First Name", Style.ALERT).show();
				return false;
			}
		}

		if (!TextUtils.isEmpty(mEditLastName.getText().toString())) {
			Pattern p = Pattern.compile("[a-zA-Z]+");
			Matcher matcher = p.matcher(mEditLastName.getText().toString());
			if (!matcher.matches()) {
				Crouton.makeText(getActivity(), "Please Enter Valid Last Name",
						Style.ALERT).show();
				return false;
			}
		}

		if (!TextUtils.isEmpty(mEditEmailAddress.getText().toString())) {
			if (!android.util.Patterns.EMAIL_ADDRESS.matcher(
					mEditEmailAddress.getText().toString()).matches()) {
				Crouton.makeText(getActivity(),
						"Please Enter Valid Email Address", Style.ALERT).show();
				return false;
			}

		}

		if (!TextUtils.isEmpty(mEditPassword.getText().toString())
				&& !TextUtils
						.isEmpty(mEditConfirmPassword.getText().toString())) {
			if (mEditPassword.getText().toString()
					.compareTo(mEditConfirmPassword.getText().toString()) != 0) {
				Crouton.makeText(
						getActivity(),
						"Please Make Sure Password and Confirm Password are same",
						Style.ALERT).show();
				return false;
			}
		}

		if (!TextUtils.isEmpty(mEditUserName.getText().toString())) {
			Pattern p = Pattern.compile("[a-zA-Z0-9]+");
			Matcher matcher = p.matcher(mEditUserName.getText().toString());
			if (!matcher.matches()) {
				Crouton.makeText(getActivity(), "Please Enter Valid Username",
						Style.ALERT).show();
				return false;
			}
		}

		if (!TextUtils.isEmpty(mEditGender.getText().toString())) {
			if (mEditGender.getText().toString().compareToIgnoreCase("male") == 0
					|| mEditGender.getText().toString()
							.compareToIgnoreCase("female") == 0) {
				Crouton.makeText(getActivity(),
						"Please Enter Either Male or Female", Style.ALERT)
						.show();
				return false;
			}
		}
		if (!TextUtils.isEmpty(mEditMobileNumber.getText().toString())) {
			Pattern p = Pattern.compile("[0-9]+");
			Matcher matcher = p.matcher(mEditMobileNumber.getText().toString());
			if (!matcher.matches()) {
				Crouton.makeText(getActivity(),
						"Please Enter Valid Mobile Number", Style.ALERT).show();
				return false;
			}
		}

		return true;
	}

	private JSONObject buildJSONRequestForRegistration() {
		return RequestBuilder.getPostSignUpData(mEditUserName.getText()
				.toString(), mEditPassword.getText().toString(), mEditFirstName
				.getText().toString(), mEditLastName.getText().toString(),
				mEditConfirmPassword.getText().toString(), mEditEmailAddress
						.getText().toString(), Utilities.getDeviceId(),
				Utilities.getDeviceName(), Utilities.getSDKVersion(), Utilities
						.getApplicationVersion(), mEditGender.getText()
						.toString(), mEditCountry.getText().toString(),
				mEditState.getText().toString(),
				mEditCity.getText().toString(), mEditMobileNumber.getText()
						.toString());
	}

	private void onSuccessfulRegistration() {
		((DepreceatedRegistrationActivity) getActivity()).slideToNextTab(0);
		Crouton.makeText(getActivity(),
				"Registered Successfull!\nPlease Login with Your Credentails!",
				Style.CONFIRM).show();
	}

	public class AysncTaskRegistration extends AsyncTask<Void, Void, Void> {
		private ProgressDialog mProgress;
		private boolean isSuccessfulRegistration = false;
		private JSONObject mJSONObjectToSend;

		public AysncTaskRegistration(JSONObject mJSONObjectToSend) {
			super();
			// TODO Auto-generated constructor stub
			this.mJSONObjectToSend = mJSONObjectToSend;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgress = new ProgressDialog(getActivity());
			mProgress.setMessage("Registration");
			mProgress.show();
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
				String responseFromApi = RestClient.postJSON(
						AppConstants.API.URL,
						mJSONObjectToSend);
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
			if (mProgress != null) {
				mProgress.dismiss();
				onSuccessfulRegistration();
			}
		}
	}
}
