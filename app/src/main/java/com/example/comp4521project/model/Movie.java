package com.example.comp4521project.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String id = "";
    private String description = "";
    private String year = "";
    private String name = "";
    Float price = 0F;
    private Integer popularity = 0;
    private String trailerURL = "";

    protected Movie(Parcel parcel) {
        id = parcel.readString();
        description = parcel.readString();
        year = parcel.readString();
        name = parcel.readString();

        if (parcel.readByte() != 0) popularity = parcel.readInt();
        else popularity = null;

        price = parcel.readFloat();
        trailerURL = parcel.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() { return 0; }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(year);
        parcel.writeString(description);
        if (popularity == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(popularity);
        }
        parcel.writeString(trailerURL);
        parcel.writeFloat(price);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }

}
