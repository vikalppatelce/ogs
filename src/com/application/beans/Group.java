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
public class Group implements Parcelable {
	private String groupId;
	private String groupName;
	private String groupImagePath;
	private String groupJabberId;
	private String groupIdMySQL;
	private String groupCreatedAt;
	private String groupUnreadCount;
	private String groupIsActive;
	private String groupCityId;
	private String groupImageLocal;

	public Group() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Group(String groupId, String groupName, String groupImagePath,
			String groupJabberId, String groupIdMySQL, String groupCreatedAt,
			String groupUnreadCount, String groupIsActive, String groupCityId,
			String groupImageLocal) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.groupImagePath = groupImagePath;
		this.groupJabberId = groupJabberId;
		this.groupIdMySQL = groupIdMySQL;
		this.groupCreatedAt = groupCreatedAt;
		this.groupUnreadCount = groupUnreadCount;
		this.groupIsActive = groupIsActive;
		this.groupCityId = groupCityId;
		this.groupImageLocal = groupImageLocal;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupImagePath() {
		return groupImagePath;
	}

	public void setGroupImagePath(String groupImagePath) {
		this.groupImagePath = groupImagePath;
	}

	public String getGroupJabberId() {
		return groupJabberId;
	}

	public void setGroupJabberId(String groupJabberId) {
		this.groupJabberId = groupJabberId;
	}

	public String getGroupIdMySQL() {
		return groupIdMySQL;
	}

	public void setGroupIdMySQL(String groupIdMySQL) {
		this.groupIdMySQL = groupIdMySQL;
	}

	public String getGroupCreatedAt() {
		return groupCreatedAt;
	}

	public void setGroupCreatedAt(String groupCreatedAt) {
		this.groupCreatedAt = groupCreatedAt;
	}

	public String getGroupUnreadCount() {
		return groupUnreadCount;
	}

	public void setGroupUnreadCount(String groupUnreadCount) {
		this.groupUnreadCount = groupUnreadCount;
	}

	public String getGroupIsActive() {
		return groupIsActive;
	}

	public void setGroupIsActive(String groupIsActive) {
		this.groupIsActive = groupIsActive;
	}

	public String getGroupCityId() {
		return groupCityId;
	}

	public void setGroupCityId(String groupCityId) {
		this.groupCityId = groupCityId;
	}

	public String getGroupImageLocal() {
		return groupImageLocal;
	}

	public void setGroupImageLocal(String groupImageLocal) {
		this.groupImageLocal = groupImageLocal;
	}

	protected Group(Parcel in) {
		groupId = in.readString();
		groupName = in.readString();
		groupImagePath = in.readString();
		groupJabberId = in.readString();
		groupIdMySQL = in.readString();
		groupCreatedAt = in.readString();
		groupUnreadCount = in.readString();
		groupIsActive = in.readString();
		groupCityId = in.readString();
		groupImageLocal = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(groupId);
		dest.writeString(groupName);
		dest.writeString(groupImagePath);
		dest.writeString(groupJabberId);
		dest.writeString(groupIdMySQL);
		dest.writeString(groupCreatedAt);
		dest.writeString(groupUnreadCount);
		dest.writeString(groupIsActive);
		dest.writeString(groupCityId);
		dest.writeString(groupImageLocal);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
		@Override
		public Group createFromParcel(Parcel in) {
			return new Group(in);
		}

		@Override
		public Group[] newArray(int size) {
			return new Group[size];
		}
	};
}