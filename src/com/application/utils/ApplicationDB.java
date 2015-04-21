package com.application.utils;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.chat.ttogs.BuildConfig;

public class ApplicationDB extends ContentProvider{
	
	public static final String AUTHORITY = "com.application.utils.ApplicationDB";
	
	private static final UriMatcher sUriMatcher;
	private static final String TAG = ApplicationDB.class.getSimpleName();
	
	private static final int NOTIFICATION = 1;
	private static final int CHAT 		  = 2;
	private static final int GROUPS		  = 3;
	private static final int CITY         = 4;
	private static final int MEMBER       = 5;
	private static final int GROUPS_D     = 6;
	
	private static HashMap<String, String> notificationProjectionMap;
	private static HashMap<String, String> chatProjectionMap;
	private static HashMap<String, String> groupProjectionMap;
	private static HashMap<String, String> cityProjectionMap;
	private static HashMap<String, String> memberProjectionMap;
	private static HashMap<String, String> groupDistinctProjectionMap;
	
	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DBConstant.DB_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			//allcontacts
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("CREATE TABLE ");
			strBuilder.append(DBConstant.TABLE_NOTIFICATIONS);
			strBuilder.append('(');
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_ID +" TEXT UNIQUE," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_PAGE +" TEXT ," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_TO +" TEXT ," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_POST_ID +" TEXT ," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_WHAT +" TEXT ," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER +" TEXT ," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER_PATH +" TEXT ," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_READ_STATUS +" NUMBER" );
			strBuilder.append(')');
			db.execSQL(strBuilder.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilder.toString());
			}
			
			StringBuilder strBuilderChat = new StringBuilder();
			strBuilderChat.append("CREATE TABLE ");
			strBuilderChat.append(DBConstant.TABLE_CHAT);
			strBuilderChat.append('(');
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," );
			/*INTEGER(20) PRIMARY KEY NOT NULL DEFAULT (STRFTIME('%s',CURRENT_TIMESTAMP)),*/
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_USER_ID +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_GROUP_ID +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_CITY_ID +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_USER_ID_MYSQL +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_USER_JABBER_ID +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_GROUP_ID_MYSQL +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_MESSAGE +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_MESSAGE_ID +" TEXT UNIQUE," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_PATH +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_FILE_LINK +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_TYPE +" NUMBER DEFAULT 0," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_ISSENT +" NUMBER DEFAULT 0," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_USER_SENT_MESSAGE +" NUMBER DEFAULT 0," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_ISDELIEVERED +" NUMBER DEFAULT 0," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_ISREAD +" NUMBER DEFAULT 0," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_TIMESTAMP +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_MESSAGE_TIME +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_MESSAGE_DATE +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_TAGGED +" TEXT ," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_ISLEFT +" NUMBER DEFAULT 0," );
			strBuilderChat.append(DBConstant.Chat_Columns.COLUMN_ISNOTIFIED +" NUMBER DEFAULT 0" );
			strBuilderChat.append(')');
			db.execSQL(strBuilderChat.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilderChat.toString());
			}
			
			
			StringBuilder strBuilderGroup = new StringBuilder();
			strBuilderGroup.append("CREATE TABLE ");
			strBuilderGroup.append(DBConstant.TABLE_GROUPS);
			strBuilderGroup.append('(');
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," );
			/*INTEGER(20) PRIMARY KEY NOT NULL DEFAULT (STRFTIME('%s',CURRENT_TIMESTAMP)),*/
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_ID +" TEXT UNIQUE," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID +" TEXT UNIQUE," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl +" TEXT ," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_IS_JOIN +" TEXT ," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_NAME +" TEXT ," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE_LOCAL +" TEXT ," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_IS_UNREAD +" NUMBER DEFAULT 0," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_UNREAD_COUNTER +" TEXT ," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_CITY_ID +" TEXT ," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE +" NUMBER DEFAULT 1," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_IS_READ +" NUMBER DEFAULT 0," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_IS_ADMIN +" NUMBER DEFAULT 0," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_CREATED +" TEXT ," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_MEMBERS +" TEXT ," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_LAST_PING +" TEXT ," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE +" TEXT ," );
			strBuilderGroup.append(DBConstant.Group_Columns.COLUMN_GROUP_LAST_MSG +" TEXT " );
			strBuilderGroup.append(')');
			db.execSQL(strBuilderGroup.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilderGroup.toString());
			}
			
			StringBuilder strBuilderGroupDistinct = new StringBuilder();
			strBuilderGroupDistinct.append("CREATE TABLE ");
			strBuilderGroupDistinct.append(DBConstant.TABLE_GROUP_D);
			strBuilderGroupDistinct.append('(');
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," );
			/*INTEGER(20) PRIMARY KEY NOT NULL DEFAULT (STRFTIME('%s',CURRENT_TIMESTAMP)),*/
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_ID +" TEXT," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_JABBER_ID +" TEXT," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_ID_MYSQl +" TEXT UNIQUE," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_JOIN +" TEXT ," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_NAME +" TEXT UNIQUE," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IMAGE_LOCAL +" TEXT ," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_UNREAD +" NUMBER DEFAULT 0," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_UNREAD_COUNTER +" TEXT ," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_CITY_ID +" TEXT ," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_ACTIVE +" NUMBER DEFAULT 1," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_READ +" NUMBER DEFAULT 0," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_ADMIN +" NUMBER DEFAULT 0," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_CREATED +" TEXT ," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_MEMBERS +" TEXT ," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_LAST_PING +" TEXT ," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IMAGE +" TEXT ," );
			strBuilderGroupDistinct.append(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_LAST_MSG +" TEXT " );
			strBuilderGroupDistinct.append(')');
			db.execSQL(strBuilderGroupDistinct.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilderGroupDistinct.toString());
			}
			
			StringBuilder strBuilderCity = new StringBuilder();
			strBuilderCity.append("CREATE TABLE ");
			strBuilderCity.append(DBConstant.TABLE_CITY);
			strBuilderCity.append('(');
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," );
			/*INTEGER(20) PRIMARY KEY NOT NULL DEFAULT (STRFTIME('%s',CURRENT_TIMESTAMP)),*/
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_ID +" TEXT UNIQUE," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_GROUP_ID +" TEXT UNIQUE," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_GROUP_JABBER_ID +" TEXT UNIQUE," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_IS_JOIN +" TEXT ," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_NAME +" TEXT ," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_IS_UNREAD +" NUMBER DEFAULT 0," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_UNREAD_COUNTER +" TEXT ," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_IS_READ +" NUMBER DEFAULT 0," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_IS_ADMIN +" NUMBER DEFAULT 0," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_CREATED +" TEXT ," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_LAST_PING +" TEXT ," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_IMAGE +" TEXT ," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_IS_ACTIVE +" NUMBER DEFAULT 1," );
			strBuilderCity.append(DBConstant.City_Columns.COLUMN_CITY_LAST_MSG +" TEXT " );
			strBuilderCity.append(')');
			db.execSQL(strBuilderCity.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilderCity.toString());
			}
			
			
			StringBuilder strBuilderMember = new StringBuilder();
			strBuilderMember.append("CREATE TABLE ");
			strBuilderMember.append(DBConstant.TABLE_MEMBER);
			strBuilderMember.append('(');
			strBuilderMember.append(DBConstant.Member_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," );
			/*INTEGER(20) PRIMARY KEY NOT NULL DEFAULT (STRFTIME('%s',CURRENT_TIMESTAMP)),*/
			strBuilderMember.append(DBConstant.Member_Columns.COLUMN_MEMBER_ID +" TEXT," );
			strBuilderMember.append(DBConstant.Member_Columns.COLUMN_MEMBER_GROUP_ID +" TEXT ," );
			strBuilderMember.append(DBConstant.Member_Columns.COLUMN_MEMBER_IMAGE +" TEXT ," );
			strBuilderMember.append(DBConstant.Member_Columns.COLUMN_MEMBER_NAME +" TEXT ," );
			strBuilderMember.append(DBConstant.Member_Columns.COLUMN_MEMBER_IS_ACTIVE +" TEXT " );
			strBuilderMember.append(')');
			db.execSQL(strBuilderMember.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilderMember.toString());
			}
		}

		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_NOTIFICATIONS);
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_CHAT);
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_GROUPS);
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_CITY);
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_MEMBER);
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_GROUP_D);
			
			onCreate(db);
		}
	}

	/* VERSION      DATABASE_VERSION      MODIFIED            BY
	 * ----------------------------------------------------------------
	 * V 0.0.1             1              18/02/15        VIKALP PATEL
	 * V 0.0.2             2              13/03/15        VIKALP PATEL
	 * -----------------------------------------------------------------
	 */
	private static final int DATABASE_VERSION = 2;
		
	OpenHelper openHelper;


	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case NOTIFICATION:
			count = db.delete(DBConstant.TABLE_NOTIFICATIONS, where, whereArgs);
			break;
		case CHAT:
			count = db.delete(DBConstant.TABLE_CHAT, where, whereArgs);
			break;
		case GROUPS:
			count = db.delete(DBConstant.TABLE_GROUPS, where, whereArgs);
			break;
		case CITY:
			count = db.delete(DBConstant.TABLE_CITY, where, whereArgs);
			break;
		case MEMBER:
			count = db.delete(DBConstant.TABLE_MEMBER, where, whereArgs);
			break;
		case GROUPS_D:
			count = db.delete(DBConstant.TABLE_GROUP_D, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}


	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (sUriMatcher.match(uri)) {
		case NOTIFICATION:
			return DBConstant.Notification_Columns.CONTENT_TYPE;
		case CHAT:
			return DBConstant.Chat_Columns.CONTENT_TYPE;
		case GROUPS:
			return DBConstant.Group_Columns.CONTENT_TYPE;
		case CITY:
			return DBConstant.City_Columns.CONTENT_TYPE;
		case MEMBER:
			return DBConstant.Member_Columns.CONTENT_TYPE;
		case GROUPS_D:
			return DBConstant.GroupDistinct_Columns.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}


	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// TODO Auto-generated method stub
		if (sUriMatcher.match(uri) != NOTIFICATION && sUriMatcher.match(uri) != CHAT
				&& sUriMatcher.match(uri) != GROUPS
				&& sUriMatcher.match(uri) != CITY
				&& sUriMatcher.match(uri) != MEMBER
				&& sUriMatcher.match(uri)!= GROUPS_D) 
		{ 
			throw new IllegalArgumentException("Unknown URI " + uri); 
		}
		
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} 
		else {
			values = new ContentValues();
		}
		
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long rowId = 0;
		
		switch (sUriMatcher.match(uri)) 
		{
			case NOTIFICATION:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_NOTIFICATIONS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.Notification_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;
			case CHAT:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_CHAT, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.Chat_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;
			case GROUPS:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_GROUPS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.Group_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;
			case CITY:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_CITY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.City_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;
			case MEMBER:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_MEMBER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.Member_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;
			case GROUPS_D:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_GROUP_D, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.GroupDistinct_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;
				
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
				
		}
		throw new SQLException("Failed to insert row into " + uri);
	}


	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		openHelper 		= new OpenHelper(getContext());
		return true;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
		case NOTIFICATION:
			qb.setTables(DBConstant.TABLE_NOTIFICATIONS);
			qb.setProjectionMap(notificationProjectionMap);
			break;
		case CHAT:
			qb.setTables(DBConstant.TABLE_CHAT);
			qb.setProjectionMap(chatProjectionMap);
			break;
		case GROUPS:
			qb.setTables(DBConstant.TABLE_GROUPS);
			qb.setProjectionMap(groupProjectionMap);
			break;
		case CITY:
			qb.setTables(DBConstant.TABLE_CITY);
			qb.setProjectionMap(cityProjectionMap);
			break;
		case MEMBER:
			qb.setTables(DBConstant.TABLE_MEMBER);
			qb.setProjectionMap(memberProjectionMap);
			break;
		case GROUPS_D:
			qb.setTables(DBConstant.TABLE_GROUP_D);
			qb.setProjectionMap(groupDistinctProjectionMap);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
//		SQLiteDatabase db = openHelper.getReadableDatabase();
		SQLiteDatabase db = openHelper.getWritableDatabase();
//		Cursor c;
//		if(sUriMatcher.match(uri) == GROUPS_D){
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);	
//		}else{
//			String distinctQuery = SQLiteQueryBuilder.buildQueryString(true, DBConstant.TABLE_GROUPS, new String[]{DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl}, null, null, null, null, null);
//			c = db.rawQuery(distinctQuery, null);
//		}
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count = -1;
		switch (sUriMatcher.match(uri)) {
		case NOTIFICATION:
			count = db.update(DBConstant.TABLE_NOTIFICATIONS, values, where, whereArgs);
			break;
		case CHAT:
			count = db.update(DBConstant.TABLE_CHAT, values, where, whereArgs);
			break;
		case GROUPS :
			count = db.update(DBConstant.TABLE_GROUPS, values, where, whereArgs);
			break;
		case CITY :
			count = db.update(DBConstant.TABLE_CITY, values, where, whereArgs);
			break;
		case MEMBER :
			count = db.update(DBConstant.TABLE_MEMBER, values, where, whereArgs);
			break;
		case GROUPS_D :
			count = db.update(DBConstant.TABLE_GROUP_D, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_NOTIFICATIONS, NOTIFICATION);
		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_CHAT, CHAT);
		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_GROUPS, GROUPS);
		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_CITY, CITY);
		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_MEMBER, MEMBER);
		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_GROUP_D, GROUPS_D);

		notificationProjectionMap = new HashMap<String, String>();
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_ID, DBConstant.Notification_Columns.COLUMN_ID);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_ID, DBConstant.Notification_Columns.COLUMN_NOTIF_ID);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_PAGE, DBConstant.Notification_Columns.COLUMN_NOTIF_PAGE);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_TO, DBConstant.Notification_Columns.COLUMN_NOTIF_TO);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_POST_ID, DBConstant.Notification_Columns.COLUMN_NOTIF_POST_ID);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_WHAT, DBConstant.Notification_Columns.COLUMN_NOTIF_WHAT);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER, DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER_PATH, DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER_PATH);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_READ_STATUS, DBConstant.Notification_Columns.COLUMN_READ_STATUS);
		
		chatProjectionMap = new HashMap<String, String>();
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_ID, DBConstant.Chat_Columns.COLUMN_ID);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_USER_ID, DBConstant.Chat_Columns.COLUMN_USER_ID);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_GROUP_ID, DBConstant.Chat_Columns.COLUMN_GROUP_ID);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_CITY_ID, DBConstant.Chat_Columns.COLUMN_CITY_ID);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_USER_ID_MYSQL, DBConstant.Chat_Columns.COLUMN_USER_ID_MYSQL);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_USER_JABBER_ID, DBConstant.Chat_Columns.COLUMN_USER_JABBER_ID);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_GROUP_ID_MYSQL, DBConstant.Chat_Columns.COLUMN_GROUP_ID_MYSQL);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_MESSAGE, DBConstant.Chat_Columns.COLUMN_MESSAGE);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_MESSAGE_ID, DBConstant.Chat_Columns.COLUMN_MESSAGE_ID);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_USER_SENT_MESSAGE, DBConstant.Chat_Columns.COLUMN_USER_SENT_MESSAGE);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_PATH, DBConstant.Chat_Columns.COLUMN_PATH);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_FILE_LINK, DBConstant.Chat_Columns.COLUMN_FILE_LINK);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_TYPE, DBConstant.Chat_Columns.COLUMN_TYPE);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_ISSENT, DBConstant.Chat_Columns.COLUMN_ISSENT);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_ISDELIEVERED, DBConstant.Chat_Columns.COLUMN_ISDELIEVERED);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_ISREAD, DBConstant.Chat_Columns.COLUMN_ISREAD);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_ISNOTIFIED, DBConstant.Chat_Columns.COLUMN_ISNOTIFIED);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_TIMESTAMP, DBConstant.Chat_Columns.COLUMN_TIMESTAMP);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_MESSAGE_TIME, DBConstant.Chat_Columns.COLUMN_MESSAGE_TIME);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_MESSAGE_DATE, DBConstant.Chat_Columns.COLUMN_MESSAGE_DATE);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_TAGGED, DBConstant.Chat_Columns.COLUMN_TAGGED);
		chatProjectionMap.put(DBConstant.Chat_Columns.COLUMN_ISLEFT, DBConstant.Chat_Columns.COLUMN_ISLEFT);
		
		groupProjectionMap = new HashMap<String, String>();
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_ID, DBConstant.Group_Columns.COLUMN_ID);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_ID, DBConstant.Group_Columns.COLUMN_GROUP_ID);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl, DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_CREATED, DBConstant.Group_Columns.COLUMN_GROUP_CREATED);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE, DBConstant.Group_Columns.COLUMN_GROUP_IMAGE);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE_LOCAL, DBConstant.Group_Columns.COLUMN_GROUP_IMAGE_LOCAL);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID, DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_CITY_ID, DBConstant.Group_Columns.COLUMN_CITY_ID);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE, DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_NAME, DBConstant.Group_Columns.COLUMN_GROUP_NAME);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_IS_ADMIN, DBConstant.Group_Columns.COLUMN_GROUP_IS_ADMIN);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_IS_JOIN, DBConstant.Group_Columns.COLUMN_GROUP_IS_JOIN);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_IS_READ, DBConstant.Group_Columns.COLUMN_GROUP_IS_READ);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_IS_UNREAD, DBConstant.Group_Columns.COLUMN_GROUP_IS_UNREAD);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_UNREAD_COUNTER, DBConstant.Group_Columns.COLUMN_GROUP_UNREAD_COUNTER);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_LAST_MSG, DBConstant.Group_Columns.COLUMN_GROUP_LAST_MSG);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_LAST_PING, DBConstant.Group_Columns.COLUMN_GROUP_LAST_PING);
		groupProjectionMap.put(DBConstant.Group_Columns.COLUMN_GROUP_MEMBERS, DBConstant.Group_Columns.COLUMN_GROUP_MEMBERS);
		
		cityProjectionMap = new HashMap<String, String>();
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_ID, DBConstant.City_Columns.COLUMN_ID);
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_CITY_ID, DBConstant.City_Columns.COLUMN_CITY_ID);
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_CITY_CREATED, DBConstant.City_Columns.COLUMN_CITY_CREATED);
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_CITY_IMAGE, DBConstant.City_Columns.COLUMN_CITY_IMAGE);
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_CITY_IS_ACTIVE, DBConstant.City_Columns.COLUMN_CITY_IS_ACTIVE);
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_CITY_IS_ADMIN, DBConstant.City_Columns.COLUMN_CITY_IS_ADMIN);
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_CITY_IS_JOIN, DBConstant.City_Columns.COLUMN_CITY_IS_JOIN);
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_CITY_IS_READ, DBConstant.City_Columns.COLUMN_CITY_IS_READ);
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_CITY_IS_UNREAD, DBConstant.City_Columns.COLUMN_CITY_IS_UNREAD);
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_CITY_LAST_MSG, DBConstant.City_Columns.COLUMN_CITY_LAST_MSG);
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_CITY_LAST_PING, DBConstant.City_Columns.COLUMN_CITY_LAST_PING);
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_CITY_NAME, DBConstant.City_Columns.COLUMN_CITY_NAME);
		cityProjectionMap.put(DBConstant.City_Columns.COLUMN_CITY_UNREAD_COUNTER, DBConstant.City_Columns.COLUMN_CITY_UNREAD_COUNTER);
		
		memberProjectionMap = new HashMap<String, String>();
		memberProjectionMap.put(DBConstant.Member_Columns.COLUMN_ID, DBConstant.Member_Columns.COLUMN_ID);
		memberProjectionMap.put(DBConstant.Member_Columns.COLUMN_MEMBER_ID, DBConstant.Member_Columns.COLUMN_MEMBER_ID);
		memberProjectionMap.put(DBConstant.Member_Columns.COLUMN_MEMBER_IS_ACTIVE, DBConstant.Member_Columns.COLUMN_MEMBER_IS_ACTIVE);
		memberProjectionMap.put(DBConstant.Member_Columns.COLUMN_MEMBER_IMAGE, DBConstant.Member_Columns.COLUMN_MEMBER_IMAGE);
		memberProjectionMap.put(DBConstant.Member_Columns.COLUMN_MEMBER_NAME, DBConstant.Member_Columns.COLUMN_MEMBER_NAME);
		
		groupDistinctProjectionMap = new HashMap<String, String>();
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_ID, DBConstant.GroupDistinct_Columns.COLUMN_ID);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_ID, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_ID);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_ID_MYSQl, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_ID_MYSQl);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_CREATED, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_CREATED);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IMAGE, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IMAGE);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IMAGE_LOCAL, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IMAGE_LOCAL);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_JABBER_ID, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_JABBER_ID);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_CITY_ID, DBConstant.GroupDistinct_Columns.COLUMN_CITY_ID);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_ACTIVE, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_ACTIVE);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_NAME, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_NAME);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_ADMIN, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_ADMIN);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_JOIN, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_JOIN);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_READ, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_READ);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_UNREAD, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_UNREAD);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_UNREAD_COUNTER, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_UNREAD_COUNTER);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_LAST_MSG, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_LAST_MSG);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_LAST_PING, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_LAST_PING);
		groupDistinctProjectionMap.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_MEMBERS, DBConstant.GroupDistinct_Columns.COLUMN_GROUP_MEMBERS);
		}
}
