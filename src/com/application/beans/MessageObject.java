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
public class MessageObject implements Parcelable {
	private String id;
	private String messageId;
	private String userId;
	private String groupId;
	private String cityId;
	private String groupIdMySQL;
	private String messageUserIdMySQL;
	private String messageUserJabberId;
	private String messageText;
	private String filePath;
	private String messageFileLink;
	private String messageTimeStamp;
	private String messageTime;
	private String messageDate;
	private String tagged;
	private int messageType;
	private boolean isDelieverd;
	private boolean isRead;
	private boolean isThisUserSentRight;
	private boolean isNotified;
	private boolean isSent;

	public MessageObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MessageObject(String id, String messageId, String userId,
			String groupId, String cityId, String groupIdMySQL,
			String messageUserIdMySQL, String messageUserJabberId,
			String messageText, String filePath, String messageFileLink,
			String messageTimeStamp, String messageTime, String messageDate,
			String tagged, int messageType, boolean isDelieverd,
			boolean isRead, boolean isThisUserSentRight, boolean isNotified,
			boolean isSent) {
		super();
		this.id = id;
		this.messageId = messageId;
		this.userId = userId;
		this.groupId = groupId;
		this.cityId = cityId;
		this.groupIdMySQL = groupIdMySQL;
		this.messageUserIdMySQL = messageUserIdMySQL;
		this.messageUserJabberId = messageUserJabberId;
		this.messageText = messageText;
		this.filePath = filePath;
		this.messageFileLink = messageFileLink;
		this.messageTimeStamp = messageTimeStamp;
		this.messageTime = messageTime;
		this.messageDate = messageDate;
		this.tagged = tagged;
		this.messageType = messageType;
		this.isDelieverd = isDelieverd;
		this.isRead = isRead;
		this.isThisUserSentRight = isThisUserSentRight;
		this.isNotified = isNotified;
		this.isSent = isSent;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getGroupIdMySQL() {
		return groupIdMySQL;
	}

	public void setGroupIdMySQL(String groupIdMySQL) {
		this.groupIdMySQL = groupIdMySQL;
	}

	public String getMessageUserIdMySQL() {
		return messageUserIdMySQL;
	}

	public void setMessageUserIdMySQL(String messageUserIdMySQL) {
		this.messageUserIdMySQL = messageUserIdMySQL;
	}

	public String getMessageUserJabberId() {
		return messageUserJabberId;
	}

	public void setMessageUserJabberId(String messageUserJabberId) {
		this.messageUserJabberId = messageUserJabberId;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getMessageFileLink() {
		return messageFileLink;
	}

	public void setMessageFileLink(String messageFileLink) {
		this.messageFileLink = messageFileLink;
	}

	public String getMessageTimeStamp() {
		return messageTimeStamp;
	}

	public void setMessageTimeStamp(String messageTimeStamp) {
		this.messageTimeStamp = messageTimeStamp;
	}

	public String getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}

	public String getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
	}

	public String getTagged() {
		return tagged;
	}

	public void setTagged(String tagged) {
		this.tagged = tagged;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public boolean isDelieverd() {
		return isDelieverd;
	}

	public void setDelieverd(boolean isDelieverd) {
		this.isDelieverd = isDelieverd;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public boolean isThisUserSentRight() {
		return isThisUserSentRight;
	}

	public void setThisUserSentRight(boolean isThisUserSentRight) {
		this.isThisUserSentRight = isThisUserSentRight;
	}

	public boolean isNotified() {
		return isNotified;
	}

	public void setNotified(boolean isNotified) {
		this.isNotified = isNotified;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	protected MessageObject(Parcel in) {
		id = in.readString();
		messageId = in.readString();
		userId = in.readString();
		groupId = in.readString();
		cityId = in.readString();
		groupIdMySQL = in.readString();
		messageUserIdMySQL = in.readString();
		messageUserJabberId = in.readString();
		messageText = in.readString();
		filePath = in.readString();
		messageFileLink = in.readString();
		messageTimeStamp = in.readString();
		messageTime = in.readString();
		messageDate = in.readString();
		tagged = in.readString();
		messageType = in.readInt();
		isDelieverd = in.readByte() != 0x00;
		isRead = in.readByte() != 0x00;
		isThisUserSentRight = in.readByte() != 0x00;
		isNotified = in.readByte() != 0x00;
		isSent = in.readByte() != 0x00;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(messageId);
		dest.writeString(userId);
		dest.writeString(groupId);
		dest.writeString(cityId);
		dest.writeString(groupIdMySQL);
		dest.writeString(messageUserIdMySQL);
		dest.writeString(messageUserJabberId);
		dest.writeString(messageText);
		dest.writeString(filePath);
		dest.writeString(messageFileLink);
		dest.writeString(messageTimeStamp);
		dest.writeString(messageTime);
		dest.writeString(messageDate);
		dest.writeString(tagged);
		dest.writeInt(messageType);
		dest.writeByte((byte) (isDelieverd ? 0x01 : 0x00));
		dest.writeByte((byte) (isRead ? 0x01 : 0x00));
		dest.writeByte((byte) (isThisUserSentRight ? 0x01 : 0x00));
		dest.writeByte((byte) (isNotified ? 0x01 : 0x00));
		dest.writeByte((byte) (isSent ? 0x01 : 0x00));
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<MessageObject> CREATOR = new Parcelable.Creator<MessageObject>() {
		@Override
		public MessageObject createFromParcel(Parcel in) {
			return new MessageObject(in);
		}

		@Override
		public MessageObject[] newArray(int size) {
			return new MessageObject[size];
		}
	};
}