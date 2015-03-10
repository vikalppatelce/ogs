package com.application.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {

	SharedPreferences sharedPreferences;
	Editor editor;

	public AppPreferences(Context context) {
		// TODO Auto-generated constructor stub
		sharedPreferences = context.getSharedPreferences("Cache",
				Context.MODE_PRIVATE);
	}

	/*
	 * PUSH SERVICE : XMPP
	 */
	public void setPushService(boolean value) {
		editor = sharedPreferences.edit();
		editor.putBoolean("isPushService", value);
		editor.commit();
	}

	public boolean isPushService() {
		return sharedPreferences.getBoolean("isPushService", true);
	}

	/*
	 * PREFERENCES : API DETAILS
	 */

	public void setApiKey(String str) {
		editor = sharedPreferences.edit();
		editor.putString("API_KEY", str);
		editor.commit();
	}

	public String getApiKey() {
		String flag = sharedPreferences.getString("API_KEY", null);
		return flag;
	}

	/*
	 * PUSH NOTIFICATION : GCM
	 */
	public void setRegisteredGCMToServer(boolean value) {
		editor = sharedPreferences.edit();
		editor.putBoolean("setRegisteredGCMToServer", value);
		editor.commit();
	}

	public boolean getRegisteredGCMToServer() {
		return sharedPreferences.getBoolean("setRegisteredGCMToServer", false);
	}
	
	public void setRegistrationId(String value) {
		editor = sharedPreferences.edit();
		editor.putString("registrationid", value);
		editor.commit();
	}

	public String getRegistrationId() {
		return sharedPreferences.getString("registrationid", null);
	}

	/*
	 * PREFERENCES : SETTINGS
	 */
	
	public void isDefaultMusicPlayer(boolean flag) {
		editor = sharedPreferences.edit();
		editor.putBoolean("isDefaultMusicPlayer", flag);
		editor.commit();
	}

	public boolean isDefaultMusicPlayer() {
		return sharedPreferences.getBoolean("isDefaultMusicPlayer", false);
	}
	
	public void isDefaultGallery(boolean flag) {
		editor = sharedPreferences.edit();
		editor.putBoolean("isDefaultGallery", flag);
		editor.commit();
	}

	public boolean isDefaultGallery() {
		return sharedPreferences.getBoolean("isDefaultGallery", false);
	}
	
	/*
	 * PREFERENCES : APPLICATION DETAILS
	 */

	public void setFirstTime(boolean flag) {
		editor = sharedPreferences.edit();
		editor.putBoolean("isFirstTime", flag);
		editor.commit();
	}

	public boolean getFirstTime() {
		return sharedPreferences.getBoolean("isFirstTime", false);
	}
	
	public void setPullCityFirstTime(boolean flag) {
		editor = sharedPreferences.edit();
		editor.putBoolean("isPullCityFirstTime", flag);
		editor.commit();
	}

	public boolean getPullCityFirstTime() {
		return sharedPreferences.getBoolean("isPullCityFirstTime", false);
	}
	
	public void setPullGroupFirstTime(boolean flag) {
		editor = sharedPreferences.edit();
		editor.putBoolean("isPullGroupFirstTime", flag);
		editor.commit();
	}

	public boolean getPullGroupFirstTime() {
		return sharedPreferences.getBoolean("isPullGroupFirstTime", false);
	}
	
	/*
	 * PREFERENCES : SYNC CITY & SYNC GROUP
	 */

	public void setUserCityLastId(String str) {
		editor = sharedPreferences.edit();
		editor.putString("_userCityLastId", str);
		editor.commit();
	}

	public String getUserCityLastId() {
		String flag = sharedPreferences.getString("_userCityLastId", null);
		return flag;
	}
	
	public void setUserGroupLastId(String str) {
		editor = sharedPreferences.edit();
		editor.putString("_userGroupLastId", str);
		editor.commit();
	}

	public String getUserGroupLastId() {
		String flag = sharedPreferences.getString("_userGroupLastId", null);
		return flag;
	}
	
	/*
	 * PREFERENCES : USER DETAILS
	 */

	public void setUserId(String str) {
		editor = sharedPreferences.edit();
		editor.putString("_userId", str);
		editor.commit();
	}

	public String getUserId() {
		String flag = sharedPreferences.getString("_userId", null);
		return flag;
	}
	
	public void setName(String str) {
		editor = sharedPreferences.edit();
		editor.putString("_name", str);
		editor.commit();
	}

	public String getName() {
		String flag = sharedPreferences.getString("_name", null);
		return flag;
	}
	
	public void setStartTime(String str) {
		editor = sharedPreferences.edit();
		editor.putString("_startTime", str);
		editor.commit();
	}

	public String getStartTime() {
		String flag = sharedPreferences.getString("_startTime", null);
		return flag;
	}

	public void setEndTime(String str) {
		editor = sharedPreferences.edit();
		editor.putString("_endTime", str);
		editor.commit();
	}

	public String getEndTime() {
		String flag = sharedPreferences.getString("_endTime", null);
		return flag;
	}
	
	public void setUserActiveStatus(String str) {
		editor = sharedPreferences.edit();
		editor.putString("_userActive", str);
		editor.commit();
	}

	public String getUserActiveStatus() {
		String flag = sharedPreferences.getString("_userActive", null);
		return flag;
	}
	
	public void setUserExpired(boolean str) {
		editor = sharedPreferences.edit();
		editor.putBoolean("_userExpired", false);
		editor.commit();
	}

	public boolean getUserExpired() {
		return sharedPreferences.getBoolean("_userExpired", false);
	}
	
	
	public void setUserAppUpdateAvailable(boolean str) {
		editor = sharedPreferences.edit();
		editor.putBoolean("_isAppUpdateAvailable", str);
		editor.commit();
	}

	public boolean getUserAppUpdateAvailable() {
		return sharedPreferences.getBoolean("_isAppUpdateAvailable", false);
	}
	
	public void setUserRegistrationTime(String str) {
		editor = sharedPreferences.edit();
		editor.putString("userUpTime", str);
		editor.commit();
	}

	public String getUserRegistrationTime() {
		String flag = sharedPreferences.getString("userUpTime", null);
		return flag;
	}
	
	public void setJabberId(String str) {
		editor = sharedPreferences.edit();
		editor.putString("jabberId", str);
		editor.commit();
	}

	public String getJabberId() {
		String flag = sharedPreferences.getString("jabberId", null);
		return flag;
	}

	public void setDisplayName(String str) {
		editor = sharedPreferences.edit();
		editor.putString("displayname", str);
		editor.commit();
	}

	public String getDisplayName() {
		String flag = sharedPreferences.getString("displayname", null);
		return flag;
	}

	public void setUserPassword(String str) {
		editor = sharedPreferences.edit();
		editor.putString("userPassword", str);
		editor.commit();
	}

	public String getUserPassword() {
		String flag = sharedPreferences.getString("userPassword", null);
		return flag;
	}

	public void setUserNumber(String str) {
		editor = sharedPreferences.edit();
		editor.putString("contactnumber", str);
		editor.commit();
	}

	public String getUserNumber() {
		String flag = sharedPreferences.getString("contactnumber", null);
		return flag;
	}

	public void setProfilePicPath(String str) {
		editor = sharedPreferences.edit();
		editor.putString("profile_pic", str);
		editor.commit();
	}

	public String getProfilePicPath() {
		String flag = sharedPreferences.getString("profile_pic", null);
		return flag;
	}

	public void setUserCity(String str) {
		editor = sharedPreferences.edit();
		editor.putString("user_city", str);
		editor.commit();
	}

	public String getUserCity() {
		String flag = sharedPreferences.getString("user_city", null);
		return flag;
	}

	public void setUserCityId(String str) {
		editor = sharedPreferences.edit();
		editor.putString("user_city_id", str);
		editor.commit();
	}

	public String getUserCityId() {
		String flag = sharedPreferences.getString("user_city_id", null);
		return flag;
	}
	
	public void setUserGroupId(String str) {
		editor = sharedPreferences.edit();
		editor.putString("user_group_id", str);
		editor.commit();
	}

	public String getUserGroupId() {
		String flag = sharedPreferences.getString("user_group_id", null);
		return flag;
	}
	
	public void setUserEmailId(String str) {
		editor = sharedPreferences.edit();
		editor.putString("user_email_id", str);
		editor.commit();
	}

	public String getUserEmailId() {
		String flag = sharedPreferences.getString("user_email_id", null);
		return flag;
	}
	
	/*
	 * PREFERENCES : ADS
	 */
	public void setAdsListBannerObject(String str) {
		editor = sharedPreferences.edit();
		editor.putString("adsListBannerObject", str);
		editor.commit();
	}

	public String getAdsListBannerObject() {
		String flag = sharedPreferences.getString("adsListBannerObject", null);
		return flag;
	}
	
	public void setAdsSponsorLogo(String str) {
		editor = sharedPreferences.edit();
		editor.putString("adsSponsorLogo", str);
		editor.commit();
	}

	public String getAdsSponsorLogo() {
		String flag = sharedPreferences.getString("adsSponsorLogo", null);
		return flag;
	}
	
	public void setAdsSponsorFull(String str) {
		editor = sharedPreferences.edit();
		editor.putString("adsSponsorFull", str);
		editor.commit();
	}

	public String getAdsSponsorFull() {
		String flag = sharedPreferences.getString("adsSponsorFull", null);
		return flag;
	}
	
	public void setAdsSponsorSmall(String str) {
		editor = sharedPreferences.edit();
		editor.putString("adsSponsorSmall", str);
		editor.commit();
	}

	public String getAdsSponsorSmall() {
		String flag = sharedPreferences.getString("adsSponsorSmall", null);
		return flag;
	}

	public void setAdsSponsorChat(String str) {
		editor = sharedPreferences.edit();
		editor.putString("adsSponsorChat", str);
		editor.commit();
	}

	public String getAdsSponsorChat() {
		String flag = sharedPreferences.getString("adsSponsorChat", null);
		return flag;
	}
	
	public void setAdsSponsorGroup(String str) {
		editor = sharedPreferences.edit();
		editor.putString("adsSponsorGroup", str);
		editor.commit();
	}

	public String getAdsSponsorGroup() {
		String flag = sharedPreferences.getString("adsSponsorGroup", null);
		return flag;
	}
	
	/*
	 * PREFERENCES : DEVICE DETAILS
	 */

	public void setScreenWidth(String str) {
		editor = sharedPreferences.edit();
		editor.putString("screenWidth", str);
		editor.commit();
	}

	public String getScreenWidth() {
		String flag = sharedPreferences.getString("screenWidth", null);
		return flag;
	}

	public void setDeviceId(String id) {
		editor = sharedPreferences.edit();
		editor.putString("deviceId", id);
		editor.commit();
	}

	public String getDeviceId() {
		String deviceId = sharedPreferences.getString("deviceId",
				"Device Id Not Found");
		return deviceId;
	}

	public void setDeviceSize(String id) {
		editor = sharedPreferences.edit();
		editor.putString("deviceId", id);
		editor.commit();
	}

	public String getDeviceSize() {
		String deviceId = sharedPreferences.getString("deviceSize",
				"Device Size Not Found");
		return deviceId;
	}


	public void setIMEINo(String id) {
		editor = sharedPreferences.edit();
		editor.putString("iMEINo", id);
		editor.commit();
	}

	public String getIMEINo() {
		String phoneId = sharedPreferences
				.getString("iMEINo", "IMEI Not Found");
		return phoneId;
	}
}
