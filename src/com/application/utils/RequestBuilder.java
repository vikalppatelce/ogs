package com.application.utils;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.application.beans.LostMessagesInfo;

public class RequestBuilder {

	private static final String TAG = RequestBuilder.class.getSimpleName();
	
	public static JSONObject getPostCountryData(String mCountryId) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("country_id", mCountryId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostSignInData(String mUserName,
			String mPassWord, String noField, String mDeviceId,
			String mDeviceName, String mAndroidVersion, String mAppVersion) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("username", mUserName);
			stringBuffer.put("password", mPassWord);
		} catch (Exception e) {

		}
		return stringBuffer;
	}
	
	public static JSONObject getMessageObjectToJSON(String message,
			String messageId, String messageTime, int messageType,
			String messageFrom, String messageTo, String messageFileLink,
			String messageSticker, String messageCityId,String messageGroupIdMySQL, String messageUserId, String messageUserJabberId) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("message", message);
			stringBuffer.put("messageId", messageId);
			stringBuffer.put("messageTime", messageTime);
			stringBuffer.put("messageType", messageType);
			stringBuffer.put("messageFrom", messageFrom);
			stringBuffer.put("messageTo", messageTo);
			stringBuffer.put("messageFileLink", messageFileLink);
			stringBuffer.put("messageSticker", messageSticker);
			stringBuffer.put("messageCityId", messageCityId);
			stringBuffer.put("messageGroupIdMySQL", messageGroupIdMySQL);
			stringBuffer.put("messageUserIdMySQL", messageUserId);
			stringBuffer.put("messageUserJabberId", messageUserJabberId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONObject getPostStateData(String mStateId) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("state_id", mStateId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONObject getPostCity() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("imei", Utilities.getDeviceId());
			stringBuffer.put("action", "getgroups");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostAppUpdate() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("device_version", Utilities.getApplicationVersion());
			stringBuffer.put("action", "getlatestappversion");
			stringBuffer.put("user_id", ApplicationLoader.getPreferences().getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostAds() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("user_id", ApplicationLoader.getPreferences().getUserId());
			stringBuffer.put("action", "getbanners");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostFailMessage() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("success", false);
			stringBuffer.put("message", "Please Try Again!");
			stringBuffer.put("error", "Please Try Again!");
			stringBuffer.put("messageError", "Please Try Again!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostViewGroupDetails(String mGroupId) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("room_jid", mGroupId.substring(0, mGroupId.indexOf("@")));
			stringBuffer.put("user_id", ApplicationLoader.getPreferences().getUserId());
			stringBuffer.put("action", "viewroomdetails");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostCityByUser() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("user_id", ApplicationLoader.getPreferences().getUserId());
			stringBuffer.put("action", "getcitiesbyuser");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostGroupByUser() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("user_id", ApplicationLoader.getPreferences().getUserId());
			stringBuffer.put("action", "getgroupsbyuser");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONObject getPostSignUpData(String mUserName,
			String mPassword, String mFirstName, String mLastName,
			String mConfirmPassword, String mEmail, String mDeviceId,
			String mDeviceName, String mSdkVersion, String mAppVersion,
			String mGender, String mCountryId, String mStateId, String mCityId,
			String mMobileNo) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("username", mUserName);
			stringBuffer.put("password", mPassword);
			stringBuffer.put("first_name", mFirstName);
			stringBuffer.put("last_name", mLastName);
			stringBuffer.put("password_confirmation", mConfirmPassword);
			stringBuffer.put("email", mEmail);
			stringBuffer.put("device_id", mDeviceId);
			stringBuffer.put("device_name", mDeviceName);
			stringBuffer.put("sdk_version", mSdkVersion);
			stringBuffer.put("app_version", mAppVersion);
			stringBuffer.put("gender", mGender);
			stringBuffer.put("country_id", mCountryId);
			stringBuffer.put("state_id", mStateId);
			stringBuffer.put("city_id", mCityId);
			stringBuffer.put("mobile_no", mMobileNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONObject getPostRegistrationData(
			String mDisplayName, String mEmail,String mDeviceId, String mDeviceName,
			String mSdkVersion, String mAppVersion, String mCityId,
			String mGroupId, String mMobileNo) {
		JSONObject stringBuffer = new JSONObject();
		String city = "[\""+mCityId + "\"]";
		String group = "[\""+mGroupId + "\"]";
		try {
			stringBuffer.put("action", "register");
			stringBuffer.put("display_name", mDisplayName);
			stringBuffer.put("email", mEmail);
			if(BuildVars.DEBUG_IMEI){
				stringBuffer.put("device_id", "124123412423");			
			}else{
				stringBuffer.put("device_id", mDeviceId);				
			}
			stringBuffer.put("device_name", mDeviceName);
			stringBuffer.put("sdk_version", mSdkVersion);
			stringBuffer.put("app_version", mAppVersion);
			stringBuffer.put("city_id", new JSONArray(city));
			stringBuffer.put("group_id", new JSONArray(group));
			stringBuffer.put("mobile_no", mMobileNo);
			
			if(!TextUtils.isEmpty(ApplicationLoader.getPreferences().getRegistrationId())){
				stringBuffer.put("gcm_reg_id", ApplicationLoader.getPreferences().getRegistrationId());	
			}else{
				stringBuffer.put("gcm_reg_id", mMobileNo);
			}
			
			if(BuildVars.DEBUG_IMEI){
				stringBuffer.put("imei", "124123412423");	
			}else{
				stringBuffer.put("imei", Utilities.getDeviceId());				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostRegistrationSkipData() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("action", "checkimei");
			if(BuildVars.DEBUG_IMEI){
				stringBuffer.put("imei", "124123412423");	
			}else{
				stringBuffer.put("imei", Utilities.getDeviceId());				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	
	public static JSONObject getPostProfileUpdateData(String filePath, String fileExtension) {
		JSONObject stringBuffer = new JSONObject();
		try{
			stringBuffer.put("action","edituserprofile");
			stringBuffer.put("user_id", ApplicationLoader.getPreferences().getUserId());
			stringBuffer.put("file_ext", fileExtension);
			stringBuffer.put("user_profile_image", Utilities.getEncodedFileToByteArray(new File(filePath).getAbsolutePath()));
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
		return stringBuffer;
	}
	
	public static JSONObject getViewUserProfile(String mUserJabberId){
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("action", "viewuserprofile");
			stringBuffer.put("view_user_id", mUserJabberId);
			stringBuffer.put("user_id", ApplicationLoader.getPreferences().getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostUploadFile(int fileType, String filePath, String fileExtenstion){
		JSONObject stringBuffer = new JSONObject();
		try{
			stringBuffer.put("action","userfileupload");
			if(BuildVars.DEBUG_VERSION){
				stringBuffer.put("user_id", "2");
			}else{
				stringBuffer.put("user_id", ApplicationLoader.getPreferences().getUserId());
			}
			stringBuffer.put("file_ext", fileExtenstion);
			if(fileType == 1){
//				stringBuffer.put("file_bytes", Utilities.getEncodedImageToByteArray(BitmapFactory.decodeFile(new File(filePath).getAbsolutePath())));
				stringBuffer.put("file_bytes", Utilities.getEncodedFileToByteArray(new File(filePath).getAbsolutePath()));
			}
			if(fileType == 2){
				stringBuffer.put("file_bytes", Utilities.getEncodedFileToByteArray(new File(filePath).getAbsolutePath()));
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostOfflieLostMessages(JSONArray mJSONArray){
		JSONObject stringBuffer = new JSONObject();
		
		try{
			stringBuffer.put("action","getofflinemessages");
			if(BuildVars.DEBUG_VERSION){
				stringBuffer.put("user_id", "2");
			}else{
				stringBuffer.put("user_id", ApplicationLoader.getPreferences().getUserId());
			}
			stringBuffer.put("rooms", mJSONArray);
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
		return stringBuffer;
	}
	
	public static JSONArray getOfflineLostMessagesRoomAndLastMessagesIdArray(ArrayList<LostMessagesInfo> mListLostMessagesInfo) {
		JSONArray mJSONArray = new JSONArray();
		try {
			for (int i = 0; i < mListLostMessagesInfo.size(); i++) {
				JSONObject mJSONObject = new JSONObject();
				mJSONObject.put("room_jid", mListLostMessagesInfo.get(i).getmRoomJabberId().substring(0, mListLostMessagesInfo.get(i).getmRoomJabberId().indexOf("@")));
				mJSONObject.put("last_message_id", mListLostMessagesInfo.get(i).getmLastMessageId());
				mJSONArray.put(mJSONObject);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return mJSONArray;
	}

}
