package com.tom.rxanroiddome;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 2016/1/19.
 */
public class Service {

    public static List<AppInfo> getAppInfos(Context context) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        PackageManager pm = context.getPackageManager();
        List<AppInfo>  mDatas = new ArrayList<AppInfo>();
        for (ResolveInfo info : infos) {
            String appPack = info.activityInfo.packageName;
            String appName = (String) info.loadLabel(pm);
            BitmapDrawable icon = (BitmapDrawable) info.loadIcon(pm);
            AppInfo app = new AppInfo();
            app.setmName(appName);
            app.setmPack(appPack);
            app.setmIcon(icon.getBitmap());
            mDatas.add(app);
        }
        return mDatas;
    }
}
