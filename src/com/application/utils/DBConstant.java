package com.application.utils;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class DBConstant {

	public static final String DB_NAME                          = "ApplicationDB";
	public static final String TABLE_CHAT 			    		= "chat";
	public static final String TABLE_NOTIFICATIONS 			    = "notification";
	public static final String TABLE_GROUPS		 			    = "groups";
	public static final String TABLE_CITY		 			    = "city";
	public static final String TABLE_MEMBER		 			    = "member";
	
//	public static final Uri DISTINCT_CONTENT_URI = Uri.parse("content://"+ ZnameDB.AUTHORITY + "/contacts");
	
	public static class Chat_Columns implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://"+ ApplicationDB.AUTHORITY + "/chat");
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/chat";
		
		public static final String COLUMN_ID 							= "_id";
		public static final String COLUMN_USER_ID 					    = "_user_id";
		public static final String COLUMN_GROUP_ID						= "_group_id";
		public static final String COLUMN_GROUP_ID_MYSQL				= "_group_id_mysql";
		public static final String COLUMN_CITY_ID						= "_city_id";
		public static final String COLUMN_USER_JABBER_ID			    = "_user_jabber_id";
		public static final String COLUMN_USER_ID_MYSQL					= "_user_id_mysql";
		public static final String COLUMN_USER_SENT_MESSAGE			    = "_is_user_sent";
		public static final String COLUMN_MESSAGE						= "_message";
		public static final String COLUMN_MESSAGE_ID					= "_message_id";
		public static final String COLUMN_PATH                 			= "_path";
		public static final String COLUMN_FILE_LINK            			= "_file_link";
		public static final String COLUMN_TYPE      					= "_type";
		public static final String COLUMN_ISSENT 				        = "_is_sent";
		public static final String COLUMN_ISREAD 				    	= "_is_read";
		public static final String COLUMN_ISDELIEVERED 					= "_is_delieverd";
		public static final String COLUMN_ISNOTIFIED 					= "_is_notified";
		public static final String COLUMN_TIMESTAMP 					= "_timestamp";
		public static final String COLUMN_MESSAGE_TIME 					= "_message_time";
		public static final String COLUMN_MESSAGE_DATE 					= "_message_date";
		public static final String COLUMN_TAGGED	 					= "_tagged";
		public static final String COLUMN_ISLEFT	 					= "_is_left";
	}
	
	public static class Notification_Columns implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://"+ ApplicationDB.AUTHORITY + "/notification");
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/notification";
		
		public static final String COLUMN_ID 							= "_id";
		public static final String COLUMN_NOTIF_ID 					    = "_notif_id";
		public static final String COLUMN_NOTIF_FROM_USER				= "_user_id";
		public static final String COLUMN_NOTIF_FROM_USER_PATH			= "_user_path";
		public static final String COLUMN_NOTIF_POST_ID                 = "post_id";
		public static final String COLUMN_NOTIF_WHAT      				= "_notif_what";
		public static final String COLUMN_NOTIF_TO 				        = "_notif_to";
		public static final String COLUMN_NOTIF_PAGE 				    = "_notif_page";
		public static final String COLUMN_READ_STATUS 					= "_read_status";
	}
	
	
	public static class Group_Columns implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://"+ ApplicationDB.AUTHORITY + "/groups");
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/groups";
		
		public static final String COLUMN_ID 							= "_id";
		public static final String COLUMN_GROUP_ID 					    = "_group_id";
		public static final String COLUMN_GROUP_ID_MYSQl			    = "_group_id_mysql";
		public static final String COLUMN_GROUP_JABBER_ID			    = "_group_jabber_id";
		public static final String COLUMN_CITY_ID 					    = "_city_id";
		public static final String COLUMN_GROUP_NAME				    = "_name";
		public static final String COLUMN_GROUP_CREATED 				= "_created_at";
		public static final String COLUMN_GROUP_LAST_PING 				= "_last_ping_at";
		public static final String COLUMN_GROUP_LAST_MSG 				= "_last_message_at";
		public static final String COLUMN_GROUP_IMAGE 					= "_path";
		public static final String COLUMN_GROUP_IS_JOIN 				= "_is_join";
		public static final String COLUMN_GROUP_IS_UNREAD 				= "_is_unread";
		public static final String COLUMN_GROUP_IS_READ 				= "_is_read";
		public static final String COLUMN_GROUP_IS_ADMIN 				= "_is_admin";
		public static final String COLUMN_GROUP_UNREAD_COUNTER			= "unread_counter";
		public static final String COLUMN_GROUP_MEMBERS 				= "_members";
		public static final String COLUMN_GROUP_IS_ACTIVE				= "_is_active";
		public static final String COLUMN_GROUP_IMAGE_LOCAL				= "_path_local";
	}
	
	public static class City_Columns implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://"+ ApplicationDB.AUTHORITY + "/city");
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/city";
		
		public static final String COLUMN_ID 							= "_id";
		public static final String COLUMN_CITY_ID 					    = "_city_id";
		public static final String COLUMN_CITY_GROUP_ID 			    = "_group_id";
		public static final String COLUMN_CITY_GROUP_JABBER_ID		    = "_group_jabber_id";
		public static final String COLUMN_CITY_NAME				    	= "_name";
		public static final String COLUMN_CITY_CREATED 					= "_created_at";
		public static final String COLUMN_CITY_LAST_PING 				= "_last_ping_at";
		public static final String COLUMN_CITY_LAST_MSG 				= "_last_message_at";
		public static final String COLUMN_CITY_IMAGE 					= "_path";
		public static final String COLUMN_CITY_IS_JOIN 					= "_is_join";
		public static final String COLUMN_CITY_IS_UNREAD 				= "_is_unread";
		public static final String COLUMN_CITY_IS_READ 					= "_is_read";
		public static final String COLUMN_CITY_IS_ADMIN 				= "_is_admin";
		public static final String COLUMN_CITY_UNREAD_COUNTER			= "unread_counter";
		public static final String COLUMN_CITY_IS_ACTIVE				= "_is_active";
	}
	
	public static class Member_Columns implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://"+ ApplicationDB.AUTHORITY + "/member");
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/member";
		
		public static final String COLUMN_ID 							= "_id";
		public static final String COLUMN_MEMBER_ID 					= "_member_id";
		public static final String COLUMN_MEMBER_GROUP_ID 			    = "_group_id";
		public static final String COLUMN_MEMBER_NAME				    = "_member_name";
		public static final String COLUMN_MEMBER_IMAGE				    = "_member_image";
		public static final String COLUMN_MEMBER_IS_ACTIVE				= "_is_active";
	}
}