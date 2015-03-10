/* HISTORY
 * CATEGORY 		:- ACTIVITY
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- ADD IPD ACTIVITY
 * DESCRIPTION 		:- SAVE IPD
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 1000B      VIKALP PATEL    07/02/2014        RELEASE         ADD VIDEO EXTENSION
 * 1000E      VIKALP PATEL    15/02/2014        RELEASE         ADDED PASS HASH IN JSON
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.application.utils;

import android.os.Environment;

public class AppConstants {

	public interface XMPP {

		// VIKALP OPENFIRE
//		public static final String HOST = "192.168.43.218";
//		public static final String TALE = "vikalppatel";

		// BHARGAV OPENFIRE

//		public static final String HOST = "192.168.0.106";
//		public static final String TALE = "localhost";

		// PRASAD OPENFIRE
//		public static final String HOST = "192.168.0.111"; 
//		public static final String TALE = "prasad";
		
		//LIVE OPENFIRE SERVER
		public static final String HOST = "66.7.194.179";
		public static final String TALE = "66.7.194.179";
		public static final String PASSWORD = "ttogs_pass";
		public static final String DEBUG_PASSWORD = "abc";
		public static final String DEBUG_USERNAME = "testuser2";
		 
	}

	public interface API {
		// API V1
		public static final String URL = "http://66.7.194.179/~dummyaccount/ttogs/services/service.php";
		public static final String TIME_SERVER = "time-a.nist.gov";
//		public static final String LOCAL = "http://192.168.0.107/ttogs/services/service.php";
	}
	public interface PUSH{
//		public static final String PROJECT_ID ="83009443246";
//		public static final String API_KEY ="AIzaSyBqYCtH2ut7FUmHI4nWITYj-IVEi58_m60";
	}

	public interface HEADERS {
		public static final String USER = "netdoersadmin";
		public static final String PASSWORD = "538f25fc32727";
	}

	public interface RESPONSES {
		public interface LoginResponse {
			public static String STATUS = "success";
			public static String VID = "user_id";
			public static String PASSHASH = "hash";
			public static String USERNAME = "email";
			public static String INFO = "info";
		}

		public interface ProjectsResponse {
			public static String PID = "pid";
			public static String PROJECT = "project";
			public static String COMPANY = "company";
		}

		public interface VendorProjectsResponse {
			public static String PID = "pid";
			public static String PROJECT = "project_name";
			public static String COMPANY = "client_name";
		}

		public interface PreviousImagesResponse {
			public static String PROJECT = "project";
			public static String WORK_TITLE = "work_title";
			public static String IMAGE = "image";
			public static String ADDRESS = "address";
			public static String CITY = "city";
			public static String STATE = "state";
			public static String SIZE = "size";
		}
	}

	public interface TAGS {
		public static final String GROUP_ID = "group_id";
		public static final String GROUP_NAME = "group_name";
		public static final String POST_ID = "post_id";
		public static final String FEED_POSITION = "feed_position";
		public static final String GROUP_IMAGE = "group_image";
		public static final String GROUP_SUBSCRIBE = "is_subscribe";
		public static final String GROUP_ADMIN = "is_admin";
		public static final String WHATSAPP_ID = "whatsapp_id";
	}

	public interface INTENT {
		public static final int FEED_COMMENT = 10001;
		public static final String COMMENT_DATA = "refresh_post";
	}

	public interface BROADCAST_ACTION {
		public static final String POST_SERVICE_MEDIA = "post_service_media";
	}

	public static final String NETWORK_NOT_AVAILABLE = "Network not available";
	public static final String IMAGE_DIRECTORY_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/TTOGS/Images/";
	
	public static final String LOG_DIRECTORY_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/TTOGS/Logs/";
	
	public static final String IMAGE_DIRECTORY_PATH_DATA = ApplicationLoader
			.getApplication().getApplicationContext().getFilesDir()
			.getAbsolutePath();
	
	public static final String AUDIO_DIRECTORY_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/TTOGS/Audio/";
	
	public static final String GROUP_IMAGE_DIRECTORY_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/TTOGS/Groups/";
	
	public static final String ADS_DIRECTORY_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/TTOGS/Ads/";
	
	public static final String PROFILE_DIRECTORY_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/TTOGS/Profile/";
	
	public static final String AUDIO_DIRECTORY_PATH_DATA = ApplicationLoader
			.getApplication().getApplicationContext().getFilesDir()
			.getAbsolutePath();
	
	
	public static final String EXTENSION = ".jpg";
	public static final String VIDEO_EXTENSION = ".mp4";
	public static final boolean DEBUG = false;
	public static final String WHATSAPP_DOMAIN = "@s.whatsapp.net";

	public static final String GREEN = "#006400";
	public static final String BLUE = "#01AFD2";
	
	public static final int NOTIFICATION_ID = 434;

	public interface GPS {
		public static final int SUCCESS_RESULT = 0;

		public static final int FAILURE_RESULT = 1;

		public static final String PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress";

		public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

		public static final String RESULT_DATA_KEY = PACKAGE_NAME
				+ ".RESULT_DATA_KEY";

		public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME
				+ ".LOCATION_DATA_EXTRA";

	}

}
