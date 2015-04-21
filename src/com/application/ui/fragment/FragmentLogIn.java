package com.application.ui.fragment;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import com.application.service.NotificationsService;
import com.application.ui.activity.GroupChatActivity;
import com.application.ui.activity.DepreceatedRegistrationActivity;
import com.application.ui.activity.GroupSelectActivity;
import com.application.ui.view.Crouton;
import com.application.ui.view.Style;
import com.application.utils.AppConstants;
import com.application.utils.ApplicationLoader;
import com.application.utils.BuildVars;
import com.application.utils.DBConstant;
import com.application.utils.RequestBuilder;
import com.application.utils.RestClient;
import com.application.utils.Utilities;

public class FragmentLogIn extends Fragment {

	private EditText mEditUserName;
	private EditText mEditPassword;

	private Button mBtnLogIn;
	private Button mBtnRegister;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_login, container, false);
		initUi(view);
		return view;
	}

	private void initUi(View view) {
		mEditPassword = (EditText) view.findViewById(R.id.passWord);
		mEditUserName = (EditText) view.findViewById(R.id.userName);

		mBtnLogIn = (Button) view.findViewById(R.id.logInBtn);

		mBtnRegister = (Button) view.findViewById(R.id.registerBtn);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setUiListener();
	}

	private void setUiListener() {
		mBtnLogIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Crouton.cancelAllCroutons();
				if (validateFields()) {
					if (Utilities.isInternetConnected()) {
						new AysncTaskLogin(buildJSONRequestForLogin(),getActivity())
								.execute();
					} else {
						Crouton.makeText(getActivity(), getResources()
								.getString(R.string.no_internet_connection),
								Style.ALERT);
					}
				}
			}
		});

		mBtnRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				((DepreceatedRegistrationActivity) getActivity()).slideToNextTab(1);
			}
		});
	}

	private boolean validateFields() {

		if (TextUtils.isEmpty(mEditUserName.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter Username",
					Style.ALERT).show();
			return false;
		}

		if (TextUtils.isEmpty(mEditPassword.getText().toString())) {
			Crouton.makeText(getActivity(), "Please Enter Password",
					Style.ALERT).show();
			return false;
		}

		if (!TextUtils.isEmpty(mEditUserName.getText().toString())) {
			Pattern p = Pattern.compile("[a-zA-Z0-9_]+");
			Matcher matcher = p.matcher(mEditUserName.getText().toString());
			if (!matcher.matches()) {
				Crouton.makeText(getActivity(), "Please Enter Valid Username",
						Style.ALERT).show();
				return false;
			}
		}

		return true;
	}

	private JSONObject buildJSONRequestForLogin() {
		return RequestBuilder.getPostSignInData(mEditUserName.getText()
				.toString(), mEditPassword.getText().toString(), "", Utilities
				.getDeviceId(), Utilities.getDeviceName(), Utilities
				.getAndroidOSVersion(), Utilities.getApplicationVersion());
	}

	private void onSuccessfulLogin() {
		ApplicationLoader.getPreferences().setJabberId(
				mEditUserName.getText().toString());
		ApplicationLoader.getPreferences().setUserPassword(
				mEditPassword.getText().toString());
		getActivity().startService(
				new Intent(getActivity(), NotificationsService.class));
		getActivity().startActivity(
				new Intent(getActivity(), GroupSelectActivity.class));
	}

	public class AysncTaskLogin extends AsyncTask<Void, Void, Void> {
		private ProgressDialog mProgress;
		private boolean isLoggedIn = false;
		private String mError;
		private JSONObject mJSONObjectToSend;
		private String mResponseFromApi;
		private Context mContext;

		public AysncTaskLogin(JSONObject mJSONObjectToSend, Context mContext) {
			super();
			// TODO Auto-generated constructor stub
			this.mJSONObjectToSend = mJSONObjectToSend;
			this.mContext = mContext;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgress = new ProgressDialog(getActivity());
			mProgress.setMessage("Logging in");
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
					mResponseFromApi = Utilities.readFile("apiSignIn.json");
				}
				try {
					JSONObject mJSONObject = new JSONObject(mResponseFromApi);
					boolean mSuccess = mJSONObject.getBoolean("success");
					if (mSuccess) {
						isLoggedIn = true;
						ApplicationLoader.getPreferences().setJabberId(
								mJSONObject.getString("userjabberid"));
						ApplicationLoader.getPreferences().setEndTime(
								mJSONObject.getString("endtime"));
						ApplicationLoader.getPreferences().setName(
								mJSONObject.getString("usernickname"));
					} else {
						mError = mJSONObject.getString("error");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					isLoggedIn = false;
					e.printStackTrace();
					mError = "Unknown Error!";
				}
			}

			if (mProgress != null) {
				mProgress.dismiss();
			}
			
			if(isLoggedIn){
				onSuccessfulLogin();
			}else{
				Crouton.makeText((DepreceatedRegistrationActivity)mContext, mError, Style.ALERT).show();
			}
		}
	}

	private void createDB() {
		ContentValues values = new ContentValues();
		values.put(DBConstant.Chat_Columns.COLUMN_USER_ID, "1");
		getActivity().getContentResolver().insert(
				DBConstant.Chat_Columns.CONTENT_URI, values);
		if (BuildVars.DEBUG_VERSION) {
			Utilities.sendDBInMail(getActivity());
		}
	}

	public class AsyncConnectXMPP extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				SASLAuthentication.supportSASLMechanism("PLAIN", 0);
				ConnectionConfiguration mConnectionConfig = new ConnectionConfiguration(
						AppConstants.XMPP.HOST, 5222, AppConstants.XMPP.TALE);
				XMPPConnection mXMPPConnection = new XMPPTCPConnection(
						mConnectionConfig);
				mConnectionConfig.setDebuggerEnabled(true);
				mConnectionConfig.setSecurityMode(SecurityMode.disabled);
				mXMPPConnection.connect();
				mXMPPConnection.login("o_user1@" + AppConstants.XMPP.TALE,
						"abc");

				/*
				 * Instant Messenger
				 */
				/*
				 * Chat chat = ChatManager.getInstanceFor(mXMPPConnection)
				 * .createChat("admin@"+TALE, new MessageListener() { public
				 * void processMessage(Chat chat, Message message) { // Print
				 * out any messages we get back to // standard out. //
				 * System.out.println(<font //
				 * color="green">"Received message: "</font> + // message); }
				 * }); chat.sendMessage("Howdy!");
				 */

				/*
				 * Group Chat
				 */
				MultiUserChat mMultiUserChat = new MultiUserChat(
						mXMPPConnection, "room1@conference."
								+ AppConstants.XMPP.TALE);
				// mMultiUserChar.createOrJoin("MyRoom2");
				mMultiUserChat.join("MyRoom2");
				/*
				 * Collection<Affiliate> mMemberList =
				 * mMultiUserChat.getMembers(); for (Affiliate mObj :
				 * mMemberList) { System.out.println(mObj.getJid());
				 * System.out.println(mObj.getNick());
				 * System.out.println(mObj.getRole());
				 * System.out.println(mObj.getAffiliation()); }
				 * Collection<Occupant> mParticipants = mMultiUserChat
				 * .getParticipants(); for (Occupant mObj : mParticipants) {
				 * System.out.println(mObj.getJid()); }
				 * 
				 * List<String> mStrOccupants = mMultiUserChat.getOccupants();
				 * for (String mObj : mStrOccupants) { System.out.println(mObj);
				 * }
				 * 
				 * RoomInfo info = MultiUserChat.getRoomInfo(mXMPPConnection,
				 * "room1@conference."+TALE);
				 * System.out.println("Number of occupants:" +
				 * info.getOccupantsCount()); System.out.println("Room Subject:"
				 * + info.getSubject());
				 */
				// mMultiUserChat.sendConfigurationForm(new
				// Form(Form.TYPE_SUBMIT));

				Message msg = new Message("room1@conference."
						+ AppConstants.XMPP.TALE, Message.Type.groupchat);
				msg.setBody(mEditUserName.getText().toString());
				mMultiUserChat.sendMessage(msg);

				PacketFilter filter = new MessageTypeFilter(
						Message.Type.groupchat);
				mXMPPConnection.addPacketListener(new PacketListener() {
					@Override
					public void processPacket(Packet packet) {
						Message message = (Message) packet;
						if (message.getBody() != null) {
							String from = message.getFrom();
							String Body = message.getBody();
							// Add incoming message to the list view or similar
							Crouton.makeText(getActivity(),
									from + " : " + Body, Style.INFO).show();
						}
					}
				}, filter);

				Thread.sleep(1000000);
				/*
				 * File Transfer
				 */

				/*
				 * FileTransferManager manager = new FileTransferManager(
				 * mXMPPConnection); OutgoingFileTransfer transfer = manager
				 * .createOutgoingFileTransfer
				 * ("room1@conference."+TALE+"/Smack"); File file = new
				 * File(Environment.getExternalStorageDirectory() +
				 * File.separator + "Mobcast.db_Dev.db");
				 * transfer.sendFile(file, "test_file"); while
				 * (!transfer.isDone()) { if (transfer .getStatus()
				 * .equals(org.jivesoftware
				 * .smackx.filetransfer.FileTransfer.Status.error)) {
				 * System.out.println("ERROR!!! " + transfer.getError()); } else
				 * if (transfer .getStatus()
				 * .equals(org.jivesoftware.smackx.filetransfer
				 * .FileTransfer.Status.cancelled) || transfer .getStatus()
				 * .equals
				 * (org.jivesoftware.smackx.filetransfer.FileTransfer.Status
				 * .refused)) { System.out.println("Cancelled!!! " +
				 * transfer.getError()); } try { Thread.sleep(1000L); } catch
				 * (InterruptedException e) { e.printStackTrace(); } } if
				 * (transfer .getStatus()
				 * .equals(org.jivesoftware.smackx.filetransfer
				 * .FileTransfer.Status.refused) || transfer .getStatus()
				 * .equals
				 * (org.jivesoftware.smackx.filetransfer.FileTransfer.Status
				 * .error) || transfer .getStatus()
				 * .equals(org.jivesoftware.smackx
				 * .filetransfer.FileTransfer.Status.cancelled)) {
				 * System.out.println("refused cancelled error " +
				 * transfer.getError()); } else { System.out.println("Success");
				 * }
				 */

			} catch (SmackException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Crouton.makeText(getActivity(), "Send Successful!", Style.CONFIRM)
					.show();
		}
	}
}
