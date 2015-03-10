/**
 * 
 */
package com.application.ui.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.beans.MessageObject;
import com.application.ui.activity.GroupChatActivity;
import com.application.ui.activity.MediaPlayerActivity;
import com.application.ui.activity.PhotoViewerActivity;
import com.application.utils.AppConstants;
import com.application.utils.ApplicationLoader;
import com.application.utils.BuildVars;
import com.application.utils.RequestBuilder;
import com.application.utils.RestClient;
import com.application.utils.Utilities;
import com.digitattva.ttogs.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class ChatAdapter extends BaseFragmentAdapter {

	private Context mContext;
	private ArrayList<MessageObject> mListMessageObject;
	private static final String TAG = ChatAdapter.class.getSimpleName();
	private ImageLoader mImageLoader;

	public ChatAdapter(Context mContext,
			ArrayList<MessageObject> mListMessageObject, ImageLoader mImageLoader) {
		this.mContext = mContext;
		this.mListMessageObject = mListMessageObject;
		this.mImageLoader = mImageLoader;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int i) {
		return true;
	}

	@Override
	public int getCount() {
		int count = mListMessageObject.size();
		return count;
	}

	public void addMessageObject(MessageObject messageObject) {
		this.mListMessageObject.add(messageObject);
		notifyDataSetChanged();
	}

	public void addMessageObjectList(
			ArrayList<MessageObject> mListMessageObjectCollections) {
		this.mListMessageObject.addAll(mListMessageObjectCollections);
		notifyDataSetChanged();
	}
	
	public void syncMessageObjectList(
			ArrayList<MessageObject> mListMessageObjectCollections) {
		this.mListMessageObject = mListMessageObjectCollections;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int i) {
		return null;
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(int i, View convertView, ViewGroup viewGroup) {
		int offset = 1;
		View v = null;
		ViewHolder holder = null;
		// final MessageObject messageObj =
		// mListMessageObject.get(mListMessageObject
		// .size() - i - offset);
		final MessageObject messageObj = mListMessageObject.get(i);
		MessageObject messageObjLast = null;
		int type = messageObj.getMessageType();
		if (type == 0) {
			// holderChatMessageCellOld(i,convertView,viewGroup,v,holder,messageObj);
			// chatMessageCellOld(v, viewGroup, messageObj);
			// holderChatMessageCellNew(i,convertView,viewGroup,holder,v,messageObj);
			// chatMessageCellNew(v,viewGroup,messageObj);

			RelativeLayout mSingleMessageContainerLeft;
			TextView mSingleMessageUserLeft;
			TextView mSingleMessageUserTimeLeft;
			TextView mSingleMessageTextLeft;

			RelativeLayout mSingleMessageContainerRight;
			TextView mSingleMessageUserRight;
			TextView mSingleMessageUserTimeRight;
			TextView mSingleMessageTextRight;
			
			LinearLayout mChatDateLayout;
			TextView mChatDateTv;

			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.item_chat_message_cell, viewGroup, false);
			mSingleMessageContainerLeft = (RelativeLayout) v
					.findViewById(R.id.singleMessageContainerLeft);
			mSingleMessageTextLeft = (TextView) v
					.findViewById(R.id.singleMessageTextLeft);
			mSingleMessageUserLeft = (TextView) v
					.findViewById(R.id.singleMessageUserLeft);
			mSingleMessageUserTimeLeft = (TextView) v
					.findViewById(R.id.singleMessageUserTimeLeft);

			mSingleMessageContainerRight = (RelativeLayout) v
					.findViewById(R.id.singleMessageContainerRight);
			mSingleMessageTextRight = (TextView) v
					.findViewById(R.id.singleMessageTextRight);
			mSingleMessageUserRight = (TextView) v
					.findViewById(R.id.singleMessageUserRight);
			mSingleMessageUserTimeRight = (TextView) v
					.findViewById(R.id.singleMessageUserTimeRight);
			
			mChatDateLayout = (LinearLayout)v.findViewById(R.id.chatDateDivider);
			mChatDateTv = (TextView)v.findViewById(R.id.chatDateDividerText);

			try{
				if(i!=0){
					messageObjLast = mListMessageObject.get(i-1);	
				}else{
					messageObjLast = new MessageObject();
					messageObjLast.setMessageDate("0000000");
				}
			}catch(Exception e){
				Log.i(TAG, e.toString());
			}

			setMessageObject(messageObj,messageObjLast, mSingleMessageContainerLeft,
					mSingleMessageContainerRight, mSingleMessageTextLeft,
					mSingleMessageTextRight, mSingleMessageUserLeft,
					mSingleMessageUserRight, mSingleMessageUserTimeLeft,
					mSingleMessageUserTimeRight, mChatDateLayout, mChatDateTv);
			setMessageUiListener(messageObj, mSingleMessageUserLeft,
					mSingleMessageUserRight);
		}
		
		if (type == 1) {
			RelativeLayout mSingleMessageContainerLeft;
			TextView mSingleMessageUserLeft;
			TextView mSingleMessageUserTimeLeft;
			ImageView mSingleMessageImageLeft;
			ProgressBar mSingleMessageImageProgressLeft;

			RelativeLayout mSingleMessageContainerRight;
			TextView mSingleMessageUserRight;
			TextView mSingleMessageUserTimeRight;
			ImageView mSingleMessageImageRight;
			ProgressBar mSingleMessageImageProgressRight;
			
			LinearLayout mChatDateLayout;
			TextView mChatDateTv;
			
			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.item_chat_image_cell, viewGroup, false);
			
			mSingleMessageContainerLeft = (RelativeLayout) v
					.findViewById(R.id.singleMessageContainerLeft);
			mSingleMessageImageLeft = (ImageView) v
					.findViewById(R.id.singleMessageImageLeft);
			mSingleMessageImageProgressLeft = (ProgressBar)v.findViewById(R.id.singleMessageImageProgressLeft);
			mSingleMessageUserLeft = (TextView) v
					.findViewById(R.id.singleMessageUserLeft);
			mSingleMessageUserTimeLeft = (TextView) v
					.findViewById(R.id.singleMessageUserTimeLeft);

			mSingleMessageContainerRight = (RelativeLayout) v
					.findViewById(R.id.singleMessageContainerRight);
			mSingleMessageImageRight = (ImageView) v
					.findViewById(R.id.singleMessageImageRight);
			mSingleMessageUserRight = (TextView) v
					.findViewById(R.id.singleMessageUserRight);
			mSingleMessageUserTimeRight = (TextView) v
					.findViewById(R.id.singleMessageUserTimeRight);
			mSingleMessageImageProgressRight = (ProgressBar)v.findViewById(R.id.singleMessageImageProgressRight);
			
			mChatDateLayout = (LinearLayout)v.findViewById(R.id.chatDateDivider);
			mChatDateTv = (TextView)v.findViewById(R.id.chatDateDividerText);

			try{
				if(i!=0){
					messageObjLast = mListMessageObject.get(i-1);	
				}else{
					messageObjLast = new MessageObject();
					messageObjLast.setMessageDate("0000000");
				}
			}catch(Exception e){
				Log.i(TAG, e.toString());
			}

			setMessageObjectImage(messageObj,messageObjLast, mSingleMessageContainerLeft,
					mSingleMessageContainerRight, mSingleMessageImageLeft,
					mSingleMessageImageRight,mSingleMessageImageProgressLeft, mSingleMessageImageProgressRight,mSingleMessageUserLeft,
					mSingleMessageUserRight, mSingleMessageUserTimeLeft,
					mSingleMessageUserTimeRight, mChatDateLayout, mChatDateTv);
			setMessageImageUiListener(messageObj, mSingleMessageUserLeft,
					mSingleMessageUserRight, mSingleMessageImageLeft, mSingleMessageImageRight);
		}
		
		if (type == 2) {
			RelativeLayout mSingleMessageContainerLeft;
			TextView mSingleMessageUserLeft;
			TextView mSingleMessageUserTimeLeft;
			ImageView mSingleMessageAudioLeft;
			ProgressBar mSingleMessageAudioProgressLeft;

			RelativeLayout mSingleMessageContainerRight;
			TextView mSingleMessageUserRight;
			TextView mSingleMessageUserTimeRight;
			ImageView mSingleMessageAudioRight;
			ProgressBar mSingleMessageAudioProgressRight;
			
			LinearLayout mChatDateLayout;
			TextView mChatDateTv;
			
			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.item_chat_audio_cell, viewGroup, false);
			
			mSingleMessageContainerLeft = (RelativeLayout) v
					.findViewById(R.id.singleMessageContainerLeft);
			mSingleMessageAudioLeft = (ImageView) v
					.findViewById(R.id.singleMessageAudioLeft);
			mSingleMessageAudioProgressLeft = (ProgressBar)v.findViewById(R.id.singleMessageAudioProgressLeft);
			mSingleMessageUserLeft = (TextView) v
					.findViewById(R.id.singleMessageUserLeft);
			mSingleMessageUserTimeLeft = (TextView) v
					.findViewById(R.id.singleMessageUserTimeLeft);

			mSingleMessageContainerRight = (RelativeLayout) v
					.findViewById(R.id.singleMessageContainerRight);
			mSingleMessageAudioRight = (ImageView) v
					.findViewById(R.id.singleMessageAudioRight);
			mSingleMessageUserRight = (TextView) v
					.findViewById(R.id.singleMessageUserRight);
			mSingleMessageUserTimeRight = (TextView) v
					.findViewById(R.id.singleMessageUserTimeRight);
			mSingleMessageAudioProgressRight = (ProgressBar)v.findViewById(R.id.singleMessageAudioProgressRight);
			
			mChatDateLayout = (LinearLayout)v.findViewById(R.id.chatDateDivider);
			mChatDateTv = (TextView)v.findViewById(R.id.chatDateDividerText);

			try{
				if(i!=0){
					messageObjLast = mListMessageObject.get(i-1);	
				}else{
					messageObjLast = new MessageObject();
					messageObjLast.setMessageDate("0000000");
				}
			}catch(Exception e){
				Log.i(TAG, e.toString());
			}

			setMessageObjectAudio(messageObj,messageObjLast, mSingleMessageContainerLeft,
					mSingleMessageContainerRight, mSingleMessageAudioLeft,
					mSingleMessageAudioRight,mSingleMessageAudioProgressLeft,
					mSingleMessageAudioProgressRight,mSingleMessageUserLeft,
					mSingleMessageUserRight, mSingleMessageUserTimeLeft,
					mSingleMessageUserTimeRight, mChatDateLayout, mChatDateTv);
			setMessageAudioUiListener(messageObj, mSingleMessageUserLeft,
					mSingleMessageUserRight, mSingleMessageAudioLeft,
					mSingleMessageAudioRight, mSingleMessageAudioProgressLeft,
					mSingleMessageAudioProgressRight);
		}
		
		/*
		 * if (type == 1) { } else if (type == 2) { } else if (type == 3) { }
		 * else if (type == 6) { } else if (type == 4) { }
		 */
		return v;
	}

	@Override
	public int getItemViewType(int i) {
		MessageObject message = mListMessageObject.get(i);
		return message.getMessageType();
	}

	@Override
	public int getViewTypeCount() {
		return 5;
	}

	@Override
	public boolean isEmpty() {
		int count = mListMessageObject.size();
		return count == 0;
	}

	static class ViewHolder {
		/*
		 * Old Message Cell Item Holder
		 */
		TextView mSingleMessageUserTv;
		TextView mSingleMessageTextTv;
		LinearLayout mSingleMessageContainerLL;

		/*
		 * New Message Cell Item Holder
		 */
		RelativeLayout mSingleMessageContainerLeft;
		TextView mSingleMessageUserLeft;
		TextView mSingleMessageUserTimeLeft;
		TextView mSingleMessageTextLeft;

		RelativeLayout mSingleMessageContainerRight;
		TextView mSingleMessageUserRight;
		TextView mSingleMessageUserTimeRight;
		TextView mSingleMessageTextRight;
		
		LinearLayout mChatDateLayout;
		TextView mChatDateTv;
	}

	public void holderChatMessageCellNew(int i, View convertView,
			ViewGroup viewGroup, ViewHolder holder, View v,
			MessageObject messageObj, MessageObject messageObjLast) {
		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.item_chat_message_cell, viewGroup, false);
			holder = new ViewHolder();
			holder.mSingleMessageContainerLeft = (RelativeLayout) v
					.findViewById(R.id.singleMessageContainerLeft);
			holder.mSingleMessageTextLeft = (TextView) v
					.findViewById(R.id.singleMessageTextLeft);
			holder.mSingleMessageUserLeft = (TextView) v
					.findViewById(R.id.singleMessageUserLeft);
			holder.mSingleMessageUserTimeLeft = (TextView) v
					.findViewById(R.id.singleMessageUserTimeLeft);

			holder.mSingleMessageContainerRight = (RelativeLayout) v
					.findViewById(R.id.singleMessageContainerRight);
			holder.mSingleMessageTextRight = (TextView) v
					.findViewById(R.id.singleMessageTextRight);
			holder.mSingleMessageUserRight = (TextView) v
					.findViewById(R.id.singleMessageUserRight);
			holder.mSingleMessageUserTimeRight = (TextView) v
					.findViewById(R.id.singleMessageUserTimeRight);
			
			holder.mChatDateLayout = (LinearLayout)v.findViewById(R.id.chatDateDivider);
			holder.mChatDateTv = (TextView)v.findViewById(R.id.chatDateDividerText);
		} else {
			v = convertView;
			holder = (ViewHolder) v.getTag();
		}
		setMessageObject(messageObj, messageObjLast,holder.mSingleMessageContainerLeft,
				holder.mSingleMessageContainerRight,
				holder.mSingleMessageTextLeft, holder.mSingleMessageTextRight,
				holder.mSingleMessageUserLeft, holder.mSingleMessageUserRight,
				holder.mSingleMessageUserTimeLeft,
				holder.mSingleMessageUserTimeRight, holder.mChatDateLayout, holder.mChatDateTv);
	}

	public void chatMessageCellNew(View v, ViewGroup viewGroup,
			MessageObject messageObj, MessageObject messageObjLast) {
		RelativeLayout mSingleMessageContainerLeft;
		TextView mSingleMessageUserLeft;
		TextView mSingleMessageUserTimeLeft;
		TextView mSingleMessageTextLeft;

		RelativeLayout mSingleMessageContainerRight;
		TextView mSingleMessageUserRight;
		TextView mSingleMessageUserTimeRight;
		TextView mSingleMessageTextRight;
		
		LinearLayout mChatDateLayout;
		TextView mChatDateTv;

		LayoutInflater li = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = li.inflate(R.layout.item_chat_message_cell, viewGroup, false);
		mSingleMessageContainerLeft = (RelativeLayout) v
				.findViewById(R.id.singleMessageContainerLeft);
		mSingleMessageTextLeft = (TextView) v
				.findViewById(R.id.singleMessageTextLeft);
		mSingleMessageUserLeft = (TextView) v
				.findViewById(R.id.singleMessageUserLeft);
		mSingleMessageUserTimeLeft = (TextView) v
				.findViewById(R.id.singleMessageUserTimeLeft);

		mSingleMessageContainerRight = (RelativeLayout) v
				.findViewById(R.id.singleMessageContainerRight);
		mSingleMessageTextRight = (TextView) v
				.findViewById(R.id.singleMessageTextRight);
		mSingleMessageUserRight = (TextView) v
				.findViewById(R.id.singleMessageUserRight);
		mSingleMessageUserTimeRight = (TextView) v
				.findViewById(R.id.singleMessageUserTimeRight);
		
		mChatDateLayout = (LinearLayout)v.findViewById(R.id.chatDateDivider);
		mChatDateTv = (TextView)v.findViewById(R.id.chatDateDividerText);
		
		setMessageObject(messageObj, messageObjLast,mSingleMessageContainerLeft,
				mSingleMessageContainerRight, mSingleMessageTextLeft,
				mSingleMessageTextRight, mSingleMessageUserLeft,
				mSingleMessageUserRight, mSingleMessageUserTimeLeft,
				mSingleMessageUserTimeRight, mChatDateLayout, mChatDateTv);
	}

	public void setMessageObject(MessageObject messageObj, MessageObject messageObjLast,
			RelativeLayout mSingleMessageContainerLeft,
			RelativeLayout mSingleMessageContainerRight,
			TextView mSingleMessageTextLeft, TextView mSingleMessageTextRight,
			TextView mSingleMessageUserLeft, TextView mSingleMessageUserRight,
			TextView mSingleMessageUserTimeLeft,
			TextView mSingleMessageUserTimeRight, LinearLayout mChatDateLayout, TextView mChatDateTv) {

		if (messageObj.isThisUserSentRight()) {
			mSingleMessageContainerRight.setVisibility(View.VISIBLE);
			mSingleMessageContainerLeft.setVisibility(View.GONE);
			mSingleMessageTextRight.setText(messageObj.getMessageText());
			mSingleMessageUserRight.setText(messageObj.getUserId());
			mSingleMessageUserTimeRight.setText(messageObj.getMessageTime());
		} else {
			mSingleMessageContainerRight.setVisibility(View.GONE);
			mSingleMessageContainerLeft.setVisibility(View.VISIBLE);
			mSingleMessageTextLeft.setText(messageObj.getMessageText());
			mSingleMessageUserLeft.setText(messageObj.getUserId());
			mSingleMessageUserTimeLeft.setText(messageObj.getMessageTime());
		}
		
		if(messageObj.getMessageDate().compareToIgnoreCase(messageObjLast.getMessageDate()) == 0){
			mChatDateLayout.setVisibility(View.GONE);
		}else{
			mChatDateLayout.setVisibility(View.VISIBLE);
			mChatDateTv.setText(messageObj.getMessageDate());
		}
	}
	
	public void setMessageObjectImage(final MessageObject messageObj,
			MessageObject messageObjLast,
			RelativeLayout mSingleMessageContainerLeft,
			RelativeLayout mSingleMessageContainerRight,
			final ImageView mSingleMessageImageLeft,
			final ImageView mSingleMessageImageRight,
			final ProgressBar mSingleMessageImageProgressLeft,
			final ProgressBar mSingleMessageImageProgressRight,
			TextView mSingleMessageUserLeft, TextView mSingleMessageUserRight,
			TextView mSingleMessageUserTimeLeft,
			TextView mSingleMessageUserTimeRight, LinearLayout mChatDateLayout,
			TextView mChatDateTv) {

		if (messageObj.isThisUserSentRight()) {
			mSingleMessageContainerRight.setVisibility(View.VISIBLE);
			mSingleMessageContainerLeft.setVisibility(View.GONE);
			mSingleMessageUserRight.setText(messageObj.getUserId());
			mSingleMessageUserTimeRight.setText(messageObj.getMessageTime());
			if(new File(messageObj.getFilePath()).exists()){
				mSingleMessageImageRight.setImageBitmap(BitmapFactory.decodeFile(messageObj.getFilePath()));
			}else{
				mImageLoader.displayImage(messageObj.getMessageFileLink(), mSingleMessageImageRight, new ImageLoadingListener() {
					
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						mSingleMessageImageRight.setVisibility(View.GONE);
						mSingleMessageImageProgressRight.setVisibility(View.VISIBLE);
					}
					
					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						// TODO Auto-generated method stub
						mSingleMessageImageProgressRight.setVisibility(View.GONE);
					}
					
					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap mBitmap) {
						// TODO Auto-generated method stub
						mSingleMessageImageRight.setVisibility(View.VISIBLE);
						mSingleMessageImageProgressRight.setVisibility(View.GONE);
						Utilities.writeBitmapToSDCard(mBitmap, Utilities.getJabberGroupIdWithoutTale(messageObj.getGroupId()), messageObj.getMessageTimeStamp());
					}
					
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
						mSingleMessageImageProgressRight.setVisibility(View.GONE);
					}
				});	
			}
		} else {
			mSingleMessageContainerRight.setVisibility(View.GONE);
			mSingleMessageContainerLeft.setVisibility(View.VISIBLE);
			mSingleMessageUserLeft.setText(messageObj.getUserId());
			mSingleMessageUserTimeLeft.setText(messageObj.getMessageTime());
			Log.i(TAG, messageObj.getFilePath());
			if(new File(messageObj.getFilePath()).exists()){
				mSingleMessageImageLeft.setImageBitmap(BitmapFactory.decodeFile(messageObj.getFilePath()));
			}else{
				mImageLoader.displayImage(messageObj.getMessageFileLink(), mSingleMessageImageLeft, new ImageLoadingListener() {
					
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						mSingleMessageImageProgressLeft.setVisibility(View.VISIBLE);
						mSingleMessageImageLeft.setVisibility(View.GONE);
					}
					
					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason string) {
						// TODO Auto-generated method stub
						mSingleMessageImageProgressLeft.setVisibility(View.GONE);
					}
					
					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap mBitmap) {
						// TODO Auto-generated method stub
						mSingleMessageImageProgressLeft.setVisibility(View.GONE);
						mSingleMessageImageLeft.setVisibility(View.VISIBLE);
						Utilities.writeBitmapToSDCard(mBitmap, Utilities.getJabberGroupIdWithoutTale(messageObj.getGroupId()), messageObj.getMessageTimeStamp());
					}
					
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
						mSingleMessageImageProgressLeft.setVisibility(View.GONE);
					}
				});	
			}
		}
		
		Log.i(TAG, messageObj.getMessageText());
		
		if(messageObj.getMessageDate().compareToIgnoreCase(messageObjLast.getMessageDate()) == 0){
			mChatDateLayout.setVisibility(View.GONE);
		}else{
			mChatDateLayout.setVisibility(View.VISIBLE);
			mChatDateTv.setText(messageObj.getMessageDate());
		}
	}
	
	
	public void setMessageObjectAudio(final MessageObject messageObj,
			MessageObject messageObjLast,
			RelativeLayout mSingleMessageContainerLeft,
			RelativeLayout mSingleMessageContainerRight,
			final ImageView mSingleMessageAudioLeft,
			final ImageView mSingleMessageAudioRight,
			final ProgressBar mSingleMessageAudioProgressLeft,
			final ProgressBar mSingleMessageAudioProgressRight,
			TextView mSingleMessageUserLeft, TextView mSingleMessageUserRight,
			TextView mSingleMessageUserTimeLeft,
			TextView mSingleMessageUserTimeRight, LinearLayout mChatDateLayout,
			TextView mChatDateTv) {

		if (messageObj.isThisUserSentRight()) {
			mSingleMessageContainerRight.setVisibility(View.VISIBLE);
			mSingleMessageContainerLeft.setVisibility(View.GONE);
			mSingleMessageUserRight.setText(messageObj.getUserId());
			mSingleMessageUserTimeRight.setText(messageObj.getMessageTime());
			if(new File(messageObj.getFilePath()).exists()){
				mSingleMessageAudioRight.setImageResource(R.drawable.ic_play);
				mSingleMessageAudioRight.setTag(R.id.TAG_IS_PLAY, true);
			}else{
				mSingleMessageAudioRight.setImageResource(R.drawable.ic_download);
				mSingleMessageAudioRight.setTag(R.id.TAG_IS_PLAY, false);
			}
		} else {
			mSingleMessageContainerRight.setVisibility(View.GONE);
			mSingleMessageContainerLeft.setVisibility(View.VISIBLE);
			mSingleMessageUserLeft.setText(messageObj.getUserId());
			mSingleMessageUserTimeLeft.setText(messageObj.getMessageTime());
			Log.i(TAG, messageObj.getFilePath());
			if(new File(messageObj.getFilePath()).exists()){
				mSingleMessageAudioLeft.setImageResource(R.drawable.ic_play);
				mSingleMessageAudioLeft.setTag(R.id.TAG_IS_PLAY, true);
			}else{
				mSingleMessageAudioLeft.setImageResource(R.drawable.ic_download);
				mSingleMessageAudioLeft.setTag(R.id.TAG_IS_PLAY, false);
			}
		}
		
		Log.i(TAG, messageObj.getMessageText());
		
		if(messageObj.getMessageDate().compareToIgnoreCase(messageObjLast.getMessageDate()) == 0){
			mChatDateLayout.setVisibility(View.GONE);
		}else{
			mChatDateLayout.setVisibility(View.VISIBLE);
			mChatDateTv.setText(messageObj.getMessageDate());
		}
	}

	public void setMessageUiListener(final MessageObject messageObj,
			TextView mSingleMessageUserLeft, TextView mSingleMessageUserRight) {
		mSingleMessageUserLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Utilities.isInternetConnected()) {
					showUserInfo(messageObj);
				} else {
					Crouton.makeText(
							(GroupChatActivity) mContext,
							mContext.getResources().getString(
									R.string.no_internet_connection),
							Style.ALERT).show();
				}

			}
		});

		mSingleMessageUserRight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Utilities.isInternetConnected()) {
					showUserInfo(messageObj);
				} else {
					Crouton.makeText(
							(GroupChatActivity) mContext,
							mContext.getResources().getString(
									R.string.no_internet_connection),
							Style.ALERT).show();
				}
			}
		});
	}
	
	public void setMessageImageUiListener(final MessageObject messageObj,
			TextView mSingleMessageUserLeft, TextView mSingleMessageUserRight,
			ImageView mSingleMessageImageLeft,
			ImageView mSingleMessageImageRight) {
		mSingleMessageUserLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Utilities.isInternetConnected()) {
					showUserInfo(messageObj);
				} else {
					Crouton.makeText(
							(GroupChatActivity) mContext,
							mContext.getResources().getString(
									R.string.no_internet_connection),
							Style.ALERT).show();
				}

			}
		});

		mSingleMessageUserRight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Utilities.isInternetConnected()) {
					showUserInfo(messageObj);
				} else {
					Crouton.makeText(
							(GroupChatActivity) mContext,
							mContext.getResources().getString(
									R.string.no_internet_connection),
							Style.ALERT).show();
				}
			}
		});
		
		mSingleMessageImageLeft.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(ApplicationLoader.getPreferences().isDefaultGallery()){
					Intent intent = new Intent();  
					intent.setAction(android.content.Intent.ACTION_VIEW);  
					File file = new File(messageObj.getFilePath());  
					intent.setDataAndType(Uri.fromFile(file), "image/*");  
					mContext.startActivity(intent);
				}else{
					Intent mIntent = new Intent(mContext,PhotoViewerActivity.class);
					mIntent.putExtra("messageObject", messageObj);
					mContext.startActivity(mIntent);	
				}
			}
		});
		
		mSingleMessageImageRight.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(ApplicationLoader.getPreferences().isDefaultGallery()){
					Intent intent = new Intent();  
					intent.setAction(android.content.Intent.ACTION_VIEW);  
					File file = new File(messageObj.getFilePath());  
					intent.setDataAndType(Uri.fromFile(file), "image/*");  
					mContext.startActivity(intent);
				}else{
					Intent mIntent = new Intent(mContext,PhotoViewerActivity.class);
					mIntent.putExtra("messageObject", messageObj);
					mContext.startActivity(mIntent);	
				}
			}
		});
	}
	
	public void setMessageAudioUiListener(final MessageObject messageObj,
			TextView mSingleMessageUserLeft, TextView mSingleMessageUserRight,
			final ImageView mSingleMessageAudioLeft,
			final ImageView mSingleMessageAudioRight,
			final ProgressBar mSingleMessageAudioProgressLeft,
			final ProgressBar mSingleMessageAudioProgressRight) {
		mSingleMessageUserLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Utilities.isInternetConnected()) {
					showUserInfo(messageObj);
				} else {
					Crouton.makeText(
							(GroupChatActivity) mContext,
							mContext.getResources().getString(
									R.string.no_internet_connection),
							Style.ALERT).show();
				}
			}
		});

		mSingleMessageUserRight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Utilities.isInternetConnected()) {
					showUserInfo(messageObj);
				} else {
					Crouton.makeText(
							(GroupChatActivity) mContext,
							mContext.getResources().getString(
									R.string.no_internet_connection),
							Style.ALERT).show();
				}
			}
		});
		
		mSingleMessageAudioLeft.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mSingleMessageAudioLeft.getTag(R.id.TAG_IS_PLAY).toString().equalsIgnoreCase("true")) {
					if(ApplicationLoader.getPreferences().isDefaultMusicPlayer()){
						Intent intent = new Intent();  
						intent.setAction(android.content.Intent.ACTION_VIEW);  
						File file = new File(messageObj.getFilePath());  
						intent.setDataAndType(Uri.fromFile(file), "audio/*");  
						mContext.startActivity(intent);
					}else{
						Intent mIntent = new Intent(mContext, MediaPlayerActivity.class);
						mIntent.putExtra("messageObject", messageObj);
						mContext.startActivity(mIntent);	
					}
				} else {
					mSingleMessageAudioProgressLeft.setVisibility(View.VISIBLE);
					mSingleMessageAudioLeft.setVisibility(View.GONE);
					Utilities.globalQueue.postRunnable(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							downloadAudioFile(messageObj.getFilePath(),
									messageObj.getMessageFileLink(),
									mSingleMessageAudioProgressLeft, mSingleMessageAudioProgressRight,
									mSingleMessageAudioLeft, mSingleMessageAudioRight,true);
						}
					});
				}		
			}
		});
		
		mSingleMessageAudioRight.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mSingleMessageAudioRight.getTag(R.id.TAG_IS_PLAY).toString().equalsIgnoreCase("true")) {
					if(ApplicationLoader.getPreferences().isDefaultMusicPlayer()){
						Intent intent = new Intent();  
						intent.setAction(android.content.Intent.ACTION_VIEW);  
						File file = new File(messageObj.getFilePath());  
						intent.setDataAndType(Uri.fromFile(file), "audio/*");  
						mContext.startActivity(intent);	
					}else{
						Intent mIntent = new Intent(mContext, MediaPlayerActivity.class);
						mIntent.putExtra("messageObject", messageObj);
						mContext.startActivity(mIntent);	
					}
				} else {
					mSingleMessageAudioProgressRight.setVisibility(View.VISIBLE);
					mSingleMessageAudioRight.setVisibility(View.GONE);
					Utilities.globalQueue.postRunnable(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							downloadAudioFile(messageObj.getFilePath(),
									messageObj.getMessageFileLink(),
									mSingleMessageAudioProgressLeft, mSingleMessageAudioProgressRight,
									mSingleMessageAudioLeft, mSingleMessageAudioRight,false);
						}
					});
				}		
			}
		});
	}

	public void showUserInfo(MessageObject messageObject) {
		Dialog mDialog = new Dialog(mContext);
		mDialog.setCancelable(true);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setTitle(messageObject.getUserId());
		mDialog.setContentView(R.layout.dialog_user_info);
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_VERTICAL);
		dialogWindow.setAttributes(lp);

		CircleImageView mDialogImage = (CircleImageView)mDialog.findViewById(R.id.dialogUserInfoPicure);
		TextView mDialogNameTv = (TextView) mDialog
				.findViewById(R.id.dialogUserInfoName);
		TextView mDialogCityTv = (TextView) mDialog
				.findViewById(R.id.dialogUserInfoCity);
		final TextView mDialogMobileTv = (TextView) mDialog
				.findViewById(R.id.dialogUserInfoMobile);
		ProgressBar mDialogProgress = (ProgressBar) mDialog
				.findViewById(R.id.userInfoContainerProgress);
		LinearLayout mDialogUserInfoLayout = (LinearLayout)mDialog.findViewById(R.id.userInfoContainerLayout);
		
		mDialogMobileTv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub : Dial User Number
				if(!TextUtils.isEmpty(mDialogMobileTv.getText().toString())){
					Uri number = Uri.parse("tel:"+mDialogMobileTv.getText().toString());
			        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
			        mContext.startActivity(callIntent);
				}
			}
		});
		
		new AsyncTaskShowUserInfo(mDialogImage, mDialogNameTv, mDialogCityTv,
				mDialogMobileTv, mDialogProgress,mDialogUserInfoLayout, messageObject.getMessageUserIdMySQL()).execute();

		mDialog.show();
	}

	public class AsyncTaskShowUserInfo extends AsyncTask<Void, Void, Void> {

		ProgressBar mDialogProgress;
		LinearLayout mDialogUserLayout;
		TextView mDialogMobileTv;
		TextView mDialogCityTv;
		TextView mDialogNameTv;
		CircleImageView mDialogImage;
		String mResponseFromApi;
		String userJabberId;
		boolean isResonseFromApi = false;
		boolean isSuccess = false;

		public AsyncTaskShowUserInfo(CircleImageView mDialogImage,TextView mDialogNameTv,
				TextView mDialogCityTv, TextView mDialogMobileTv,
				ProgressBar mDialogProgress,LinearLayout mDialogUserLayout, String userJabberId) {
			this.mDialogCityTv = mDialogCityTv;
			this.mDialogMobileTv = mDialogMobileTv;
			this.mDialogNameTv = mDialogNameTv;
			this.mDialogProgress = mDialogProgress;
			this.mDialogImage = mDialogImage;
			this.userJabberId = userJabberId;
			this.mDialogUserLayout = mDialogUserLayout;
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
				mResponseFromApi = RestClient.postJSON(AppConstants.API.URL, RequestBuilder.getViewUserProfile(userJabberId));
				isResonseFromApi = true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isResonseFromApi = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(isResonseFromApi){
				mDialogProgress.setVisibility(View.GONE);
				if(BuildVars.DEBUG_VERSION){
					mResponseFromApi = Utilities.readFile("apiUserProfile.json");
				}
				try {
					JSONObject mJSONObject = new JSONObject(mResponseFromApi);
					isSuccess = mJSONObject.getBoolean("success");
					if(isSuccess){
						JSONObject mJSONObjectData = mJSONObject.getJSONObject("data");
						JSONObject mJSONObjectDataInfo = mJSONObjectData.getJSONObject("view_user_info");
						mDialogProgress.setVisibility(View.GONE);
						mDialogUserLayout.setVisibility(View.VISIBLE);
						mDialogMobileTv.setText(mJSONObjectDataInfo.getString("mobile_no"));
						mDialogNameTv.setText(mJSONObjectDataInfo.getString("display_name"));
						mDialogCityTv.setText(mJSONObjectDataInfo.getString("email"));	
						mImageLoader.displayImage(mJSONObjectDataInfo.getString("user_image_path"), mDialogImage);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mDialogProgress.setVisibility(View.VISIBLE);
			mDialogUserLayout.setVisibility(View.GONE);
		}
	}
	
	public void downloadAudioFile(String filePath, String fileLink,
			final ProgressBar mSingleMessageAudioProgressLeft,
			final ProgressBar mSingleMessageAudioProgressRight,
			final ImageView mSingleMessageAudioLeft,
			final ImageView mSingleMessageAudioRight, final boolean isLeft) {
		InputStream mInput = null;
		OutputStream mOutput = null;
		File mFile = null;
		HttpURLConnection mConnection = null;
		try {
			URL mURL = new URL(fileLink);
			mConnection = (HttpURLConnection) mURL.openConnection();
			mConnection.connect();
			mInput = mConnection.getInputStream();

			File mFileDirectory = new File(AppConstants.AUDIO_DIRECTORY_PATH);
			mFileDirectory.mkdirs();
			String mFileName = Utilities.getFileNameFromPath(filePath);
			mFile = new File(mFileDirectory, mFileName);
			if (mFile.exists()) {
				mFile.delete();
			}

			mOutput = new FileOutputStream(mFile);
			byte data[] = new byte[4096];
			int count;
			while ((count = mInput.read(data)) != -1) {
				mOutput.write(data, 0, count);
			}
			((Activity) mContext).runOnUiThread(new Runnable() {
				public void run() {
					if(isLeft){
						mSingleMessageAudioProgressLeft.setVisibility(View.GONE);
						mSingleMessageAudioLeft
								.setImageResource(R.drawable.ic_play);
						mSingleMessageAudioLeft.setTag(R.id.TAG_IS_PLAY, true);	
					}else{
						mSingleMessageAudioProgressRight.setVisibility(View.GONE);
						mSingleMessageAudioRight
								.setImageResource(R.drawable.ic_play);
						mSingleMessageAudioRight.setTag(R.id.TAG_IS_PLAY, true);
					}
					
					notifyDataSetChanged();
				}
			});
		} catch (Exception e) {
			try {
				mFile.delete();
			} catch (Exception ex) {

			}
			Log.i(TAG, e.toString());

		} finally {
			try {
				if (mOutput != null)
					mOutput.close();
				if (mInput != null)
					mInput.close();
			} catch (IOException ignored) {
			}

			if (mConnection != null)
				mConnection.disconnect();
		}
	}
	
}
