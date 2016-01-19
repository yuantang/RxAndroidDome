package com.tom.rxanroiddome;

import android.graphics.Bitmap;

/**
 * Created by tom on 2016/1/10.
 */


public class AppInfo implements Comparable<Object> {
    String mPack;
    String mName;
    Bitmap mIcon;

    public String getmPack() {
        return mPack;
    }

    public void setmPack(String mPack) {
        this.mPack = mPack;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public Bitmap getmIcon() {
        return mIcon;
    }

    public void setmIcon(Bitmap mIcon) {
        this.mIcon = mIcon;
    }

    @Override
    public int compareTo(Object another) {
        AppInfo f = (AppInfo)another;
        return getmName().compareTo(f.getmName());
    }
}
