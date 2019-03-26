package com.example.prashantgajera.tallyerpsystem;

import android.os.Parcel;
import android.os.Parcelable;

public class Productpojo implements Parcelable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String title,description,badge,price,image;

    public Productpojo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.badge);
        dest.writeString(this.price);
        dest.writeString(this.image);
    }

    protected Productpojo(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.badge = in.readString();
        this.price = in.readString();
        this.image = in.readString();
    }

    public static final Creator<Productpojo> CREATOR = new Creator<Productpojo>() {
        @Override
        public Productpojo createFromParcel(Parcel source) {
            return new Productpojo(source);
        }

        @Override
        public Productpojo[] newArray(int size) {
            return new Productpojo[size];
        }
    };
}
