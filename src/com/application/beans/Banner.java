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
public class Banner implements Parcelable {
	private String bannerId;
	private String bannerName;
	private String bannerDescription;
	private String bannerImage;
	private String bannerStartDate;
	private String bannerEndDate;
	private String bannerCompanyName;
	private String bannerPosition;
	private boolean bannerIsAdsOn;

	public Banner() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Banner(String bannerId, String bannerName, String bannerDescription,
			String bannerImage, String bannerStartDate, String bannerEndDate,
			String bannerCompanyName, String bannerPosition,
			boolean bannerIsAdsOn) {
		super();
		this.bannerId = bannerId;
		this.bannerName = bannerName;
		this.bannerDescription = bannerDescription;
		this.bannerImage = bannerImage;
		this.bannerStartDate = bannerStartDate;
		this.bannerEndDate = bannerEndDate;
		this.bannerCompanyName = bannerCompanyName;
		this.bannerPosition = bannerPosition;
		this.bannerIsAdsOn = bannerIsAdsOn;
	}

	public String getBannerId() {
		return bannerId;
	}

	public void setBannerId(String bannerId) {
		this.bannerId = bannerId;
	}

	public String getBannerName() {
		return bannerName;
	}

	public void setBannerName(String bannerName) {
		this.bannerName = bannerName;
	}

	public String getBannerDescription() {
		return bannerDescription;
	}

	public void setBannerDescription(String bannerDescription) {
		this.bannerDescription = bannerDescription;
	}

	public String getBannerImage() {
		return bannerImage;
	}

	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}

	public String getBannerStartDate() {
		return bannerStartDate;
	}

	public void setBannerStartDate(String bannerStartDate) {
		this.bannerStartDate = bannerStartDate;
	}

	public String getBannerEndDate() {
		return bannerEndDate;
	}

	public void setBannerEndDate(String bannerEndDate) {
		this.bannerEndDate = bannerEndDate;
	}

	public String getBannerCompanyName() {
		return bannerCompanyName;
	}

	public void setBannerCompanyName(String bannerCompanyName) {
		this.bannerCompanyName = bannerCompanyName;
	}

	public String getBannerPosition() {
		return bannerPosition;
	}

	public void setBannerPosition(String bannerPosition) {
		this.bannerPosition = bannerPosition;
	}

	public boolean isBannerIsAdsOn() {
		return bannerIsAdsOn;
	}

	public void setBannerIsAdsOn(boolean bannerIsAdsOn) {
		this.bannerIsAdsOn = bannerIsAdsOn;
	}

	protected Banner(Parcel in) {
		bannerId = in.readString();
		bannerName = in.readString();
		bannerDescription = in.readString();
		bannerImage = in.readString();
		bannerStartDate = in.readString();
		bannerEndDate = in.readString();
		bannerCompanyName = in.readString();
		bannerPosition = in.readString();
		bannerIsAdsOn = in.readByte() != 0x00;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(bannerId);
		dest.writeString(bannerName);
		dest.writeString(bannerDescription);
		dest.writeString(bannerImage);
		dest.writeString(bannerStartDate);
		dest.writeString(bannerEndDate);
		dest.writeString(bannerCompanyName);
		dest.writeString(bannerPosition);
		dest.writeByte((byte) (bannerIsAdsOn ? 0x01 : 0x00));
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Banner> CREATOR = new Parcelable.Creator<Banner>() {
		@Override
		public Banner createFromParcel(Parcel in) {
			return new Banner(in);
		}

		@Override
		public Banner[] newArray(int size) {
			return new Banner[size];
		}
	};
}