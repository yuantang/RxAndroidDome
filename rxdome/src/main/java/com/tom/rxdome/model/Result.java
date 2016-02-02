package com.tom.rxdome.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.Data;

/**
 * Created by tom on 2016/1/25.
 */

@Data
public class Result implements Parcelable{
    String status;
    String desc;
    List<Joker> detail;

    protected Result(Parcel in) {
        status = in.readString();
        desc = in.readString();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(desc);
    }
}
