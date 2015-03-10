/**
 * 
 */
package com.application.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class LostMessagesInfo implements Parcelable {
	private String mRoomJabberId;
	private String mLastMessageId;

	public LostMessagesInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LostMessagesInfo(String mRoomJabberId, String mLastMessageId) {
		super();
		this.mRoomJabberId = mRoomJabberId;
		this.mLastMessageId = mLastMessageId;
	}

	public String getmRoomJabberId() {
		return mRoomJabberId;
	}

	public void setmRoomJabberId(String mRoomJabberId) {
		this.mRoomJabberId = mRoomJabberId;
	}

	public String getmLastMessageId() {
		return mLastMessageId;
	}

	public void setmLastMessageId(String mLastMessageId) {
		this.mLastMessageId = mLastMessageId;
	}

	protected LostMessagesInfo(Parcel in) {
		mRoomJabberId = in.readString();
		mLastMessageId = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mRoomJabberId);
		dest.writeString(mLastMessageId);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<LostMessagesInfo> CREATOR = new Parcelable.Creator<LostMessagesInfo>() {
		@Override
		public LostMessagesInfo createFromParcel(Parcel in) {
			return new LostMessagesInfo(in);
		}

		@Override
		public LostMessagesInfo[] newArray(int size) {
			return new LostMessagesInfo[size];
		}
	};
}
