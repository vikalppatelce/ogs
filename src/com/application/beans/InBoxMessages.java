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
public class InBoxMessages implements Parcelable {
	private String mMessage;
	private String mMessageGroupId;

	public InBoxMessages(String mMessage, String mMessageGroupId) {
		super();
		this.mMessage = mMessage;
		this.mMessageGroupId = mMessageGroupId;
	}

	public InBoxMessages() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getmMessage() {
		return mMessage;
	}

	public void setmMessage(String mMessage) {
		this.mMessage = mMessage;
	}

	public String getmMessageGroupId() {
		return mMessageGroupId;
	}

	public void setmMessageGroupId(String mMessageGroupId) {
		this.mMessageGroupId = mMessageGroupId;
	}

	protected InBoxMessages(Parcel in) {
		mMessage = in.readString();
		mMessageGroupId = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mMessage);
		dest.writeString(mMessageGroupId);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<InBoxMessages> CREATOR = new Parcelable.Creator<InBoxMessages>() {
		@Override
		public InBoxMessages createFromParcel(Parcel in) {
			return new InBoxMessages(in);
		}

		@Override
		public InBoxMessages[] newArray(int size) {
			return new InBoxMessages[size];
		}
	};
}
