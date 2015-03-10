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
public class Member implements Parcelable {
	private String memberName;
	private String memberId;
	private String memberGroupId;
	private String memberGroupName;
	private String memberImage;

	public Member() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Member(String memberName, String memberId, String memberGroupId,
			String memberGroupName, String memberImage) {
		super();
		this.memberName = memberName;
		this.memberId = memberId;
		this.memberGroupId = memberGroupId;
		this.memberGroupName = memberGroupName;
		this.memberImage = memberImage;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberGroupId() {
		return memberGroupId;
	}

	public void setMemberGroupId(String memberGroupId) {
		this.memberGroupId = memberGroupId;
	}

	public String getMemberGroupName() {
		return memberGroupName;
	}

	public void setMemberGroupName(String memberGroupName) {
		this.memberGroupName = memberGroupName;
	}

	public String getMemberImage() {
		return memberImage;
	}

	public void setMemberImage(String memberImage) {
		this.memberImage = memberImage;
	}

	protected Member(Parcel in) {
		memberName = in.readString();
		memberId = in.readString();
		memberGroupId = in.readString();
		memberGroupName = in.readString();
		memberImage = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(memberName);
		dest.writeString(memberId);
		dest.writeString(memberGroupId);
		dest.writeString(memberGroupName);
		dest.writeString(memberImage);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Member> CREATOR = new Parcelable.Creator<Member>() {
		@Override
		public Member createFromParcel(Parcel in) {
			return new Member(in);
		}

		@Override
		public Member[] newArray(int size) {
			return new Member[size];
		}
	};
}
