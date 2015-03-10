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
public class City implements Parcelable {
	private String cityId;
	private String cityName;
	private String cityUnreadCount;
	private String cityGroupId;
	private int cityIsActive;
	private String cityGroupJabberId;

	public City(String cityId, String cityName, String cityUnreadCount,
			String cityGroupId, int cityIsActive, String cityGroupJabberId) {
		super();
		this.cityId = cityId;
		this.cityName = cityName;
		this.cityUnreadCount = cityUnreadCount;
		this.cityGroupId = cityGroupId;
		this.cityIsActive = cityIsActive;
		this.cityGroupJabberId = cityGroupJabberId;
	}

	public City() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityUnreadCount() {
		return cityUnreadCount;
	}

	public void setCityUnreadCount(String cityUnreadCount) {
		this.cityUnreadCount = cityUnreadCount;
	}

	public String getCityGroupId() {
		return cityGroupId;
	}

	public void setCityGroupId(String cityGroupId) {
		this.cityGroupId = cityGroupId;
	}

	public int getCityIsActive() {
		return cityIsActive;
	}

	public void setCityIsActive(int cityIsActive) {
		this.cityIsActive = cityIsActive;
	}

	public String getCityGroupJabberId() {
		return cityGroupJabberId;
	}

	public void setCityGroupJabberId(String cityGroupJabberId) {
		this.cityGroupJabberId = cityGroupJabberId;
	}

	protected City(Parcel in) {
		cityId = in.readString();
		cityName = in.readString();
		cityUnreadCount = in.readString();
		cityGroupId = in.readString();
		cityIsActive = in.readInt();
		cityGroupJabberId = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(cityId);
		dest.writeString(cityName);
		dest.writeString(cityUnreadCount);
		dest.writeString(cityGroupId);
		dest.writeInt(cityIsActive);
		dest.writeString(cityGroupJabberId);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
		@Override
		public City createFromParcel(Parcel in) {
			return new City(in);
		}

		@Override
		public City[] newArray(int size) {
			return new City[size];
		}
	};
}
