package com.tom.rxdome.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

/**
 * Created by tom on 2016/1/25.
 */
@Data
public class Joker implements Parcelable {
    String  id;       //----数据库自增id，没有任何意义
    String  xhid;     //----笑话id，判断笑话新旧用的
    String  author;   //----笑话作者
    String  content;  //----笑话内容
    String  picUrl;   //----笑话的图片（如果有）
    String  status;   //----笑话状态（能返回的都是1）

    protected Joker(Parcel in) {
        id = in.readString();
        xhid = in.readString();
        author = in.readString();
        content = in.readString();
        picUrl = in.readString();
        status = in.readString();
    }

    public static final Creator<Joker> CREATOR = new Creator<Joker>() {
        @Override
        public Joker createFromParcel(Parcel in) {
            return new Joker(in);
        }

        @Override
        public Joker[] newArray(int size) {
            return new Joker[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(xhid);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(picUrl);
        dest.writeString(status);
    }
}
