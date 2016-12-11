package com.orgware.cloudnarisample.dataitem;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nandagopal on 12/8/16.
 */
public class ImageItem implements Parcelable {

  private String publicId;
  private String createdAt;
  private String secureUrl;
  private String url =
      "http://res.cloudinary.com/demo/image/fetch/w_250,h_250,c_fill,r_max,f_auto/";

  private String detailUrl;

  public ImageItem(Parcel in) {
    publicId = in.readString();
    createdAt = in.readString();
    secureUrl = in.readString();
    detailUrl = in.readString();
    url = in.readString();
  }

  public ImageItem() {
  }

  public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
    @Override public ImageItem createFromParcel(Parcel in) {
      return new ImageItem(in);
    }

    @Override public ImageItem[] newArray(int size) {
      return new ImageItem[size];
    }
  };

  public String getDetailUrl() {
    return detailUrl;
  }

  public void setDetailUrl(String detailUrl) {
    this.detailUrl = detailUrl;
  }

  public String getPublicId() {
    return publicId;
  }

  public void setPublicId(String publicId) {
    this.publicId = publicId;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getSecureUrl() {
    return url + secureUrl;
  }

  public void setSecureUrl(String secureUrl) {
    this.secureUrl = secureUrl;
    setDetailUrl(secureUrl);
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(publicId);
    parcel.writeString(createdAt);
    parcel.writeString(secureUrl);
    parcel.writeString(detailUrl);
    parcel.writeString(url);
  }
}
