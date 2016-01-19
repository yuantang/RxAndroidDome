package com.tom.rxanroiddome;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";
    @Bind(R.id.siwpeRefresh)
    SwipeRefreshLayout siwpeRefresh;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    AppInfoAdapter mAdapter;

    List<AppInfo> mDatas;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initVew();
        siwpeRefresh.setOnRefreshListener(this);
        onRefresh();
    }
    private void initVew() {
        setSupportActionBar(toolbar);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        siwpeRefresh.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_green_light)
        );
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter=new  AppInfoAdapter(MainActivity.this);
        recyclerview.setAdapter(mAdapter);
//      siwpeRefresh.setProgressBackgroundColorSchemeResource(R.color.colorAccent);
    }

    private List<AppInfo> getAppInfos() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infos = getActivity().getPackageManager().queryIntentActivities(mainIntent, 0);
        PackageManager pm = getActivity().getPackageManager();
        mDatas = new ArrayList<AppInfo>();
        for (ResolveInfo info : infos) {
            Log.e(TAG, infos.size() + "");
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

    private void refreshTheList() {
        getApps().toSortedList().subscribe(new Observer<List<AppInfo>>() {
            @Override
            public void onCompleted() {
                Toast.makeText(getActivity(), "onCompleted", Toast.LENGTH_SHORT).show();
                siwpeRefresh.setRefreshing(false);
            }
            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), "onError", Toast.LENGTH_SHORT).show();
                siwpeRefresh.setRefreshing(false);
            }

            @Override
            public void onNext(List<AppInfo> appInfos) {
                Log.println(7, "appInfos", appInfos.size() + "");
                mAdapter.addApplications(appInfos);
                siwpeRefresh.setRefreshing(false);
            }
        });
    }

    private Observable<AppInfo> getApps() {
        return Observable.create(new Observable.OnSubscribe<AppInfo>() {
            @Override
            public void call(Subscriber<? super AppInfo> subscriber) {
                mDatas = getAppInfos();
                for (int i = 0; i < mDatas.size(); i++) {
                    subscriber.onNext(mDatas.get(i));
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }

    public Context getActivity() {
        return MainActivity.this;
    }

    @Override
    public void onRefresh() {
        refreshTheList();
    }
}
