package com.tom.rxanroiddome;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "MainActivity";
    private static final int FROM =1 ;
    private static final int JUST =2;
    private static final int REPEAT =3 ;
    private static final int DEFER =4;
    private static final int RANGE =5 ;
    private static final int INTERVAL =6 ;
    private static final int TIMER =7 ;
    private static final int FILTER =8 ;
    private static final int TAKE =9 ;
    private static final int DISTINCT =10 ;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    /*  @Bind(R.id.fab)
      FloatingActionButton fab;*/
    @Bind(R.id.nav_view)
    NavigationView navView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.siwpeRefresh)
    SwipeRefreshLayout siwpeRefresh;

    AppInfoAdapter mAdapter;
    List<AppInfo> mDatas;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navView.setNavigationItemSelectedListener(this);

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        siwpeRefresh.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_green_light)
        );

        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AppInfoAdapter(MainActivity.this);
        recyclerview.setAdapter(mAdapter);
        siwpeRefresh.setOnRefreshListener(this);
        onRefresh();
    }

    private void refreshTheList() {
        getApps().toSortedList().subscribe(new Observer<List<AppInfo>>() {
            @Override
            public void onCompleted() {
                completed();
            }

            @Override
            public void onError(Throwable e) {
                error();
            }

            @Override
            public void onNext(List<AppInfo> appInfos) {
                Log.d(TAG, "onNext");
                mAdapter.addApplications(appInfos);
                siwpeRefresh.setRefreshing(false);
            }
        });
    }

    private Observable<AppInfo> getApps() {
        return Observable.create(new Observable.OnSubscribe<AppInfo>() {
            @Override
            public void call(Subscriber<? super AppInfo> subscriber) {
                mDatas = Service.getAppInfos(getActivity());
                for (int i = 0; i < mDatas.size(); i++) {
                    subscriber.onNext(mDatas.get(i));
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, FROM, 0, "from");
        menu.add(0, JUST, 0, "just");
        menu.add(0, REPEAT, 0, "repeat");
        menu.add(0, DEFER, 0, "defer");
        menu.add(0, RANGE, 0, "range");
        menu.add(0, INTERVAL, 0, "interval");
        menu.add(0, TIMER, 0, "timer");
        menu.add(0,FILTER,0,"filter");
        menu.add(0,TAKE,0,"take");
        menu.add(0,DISTINCT,0,"distinct");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case FROM:
                loadList(Service.getAppInfos(getActivity()));
                break;
            case 2:
                List<AppInfo> apps = Service.getAppInfos(getActivity());
                loadApps(apps.get(2), apps.get(4), apps.get(10));
                break;
            case 3:
                List<AppInfo> appss = Service.getAppInfos(getActivity());
                loadAppsRepeat(appss.get(2), appss.get(4), appss.get(10));
                break;
            case 4:
//                Observable<Integer> deferred = Observable.defer();
//                deferred.subscribe(new Observer<Integer>() {
//                    @Override
//                    public void onCompleted() {
//                        completed();
//                    }
//                    @Override
//                    public void onError(Throwable e) {
//                        error();
//                    }
//                    @Override
//                    public void onNext(Integer integer) {
//                        Log.d(TAG,integer+"");
//                    }
//                });
                break;
            case 5:
//                第一个是起始点，第二个是我们想发射数字的个数。
                Observable.range(10, 3).subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        completed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        error();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Toast.makeText(getActivity(), integer + "", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, integer + "");
                    }
                });
                break;
            case 6:
//                函数的两个参数：一个指定两次发射的时间间隔，另一个是用到的时间单位。
                Observable.interval(3, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        completed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        error();
                    }

                    @Override
                    public void onNext(Long aLong) {
//                        Toast.makeText(getActivity(), aLong + "", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, aLong + "");
                    }
                });
                break;
            case 7:
//           用这个代码，你可以创建一个以初始值来延迟（上一个例子是3秒）
//           执行的interval()版本，然后每隔N秒就发射一个新的数字（前面的例子是3秒）。
                Observable.timer(3, 3, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        completed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        error();
                    }

                    @Override
                    public void onNext(Long aLong) {
//                        Toast.makeText(getActivity(), aLong + "", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, aLong + "");
                    }
                });
                break;
//  RxJava让我们使用filter()方法来过滤我们观测序列中不想要的值，在上一章中，我们在几个例子
// 中使用了已安装的应用列表，但是我们只想展示以字母C开头的已安装的应用该怎么办呢？在这个新的例子中，
// 我们将使用同样的列表，但是我们会过滤它，通过把合适的谓词传给filter()函数来得到我们想要的值。
            case FILTER:
                loadListFilter(Service.getAppInfos(getActivity()));
                break;
            case TAKE:
                loadListTake(Service.getAppInfos(getActivity()));
                break;
            case DISTINCT://有且仅有一次(去重复)
                loadListDistinct(Service.getAppInfos(getActivity()));
                break;
        }
        return true;
    }
    private void loadListDistinct(List<AppInfo> apps) {
        mDatas.clear();
        Observable<AppInfo> fullOfDuplicates = Observable.from(apps).take(3).repeat(3);
        fullOfDuplicates.distinct().subscribe(new Observer<AppInfo>() {
            @Override
            public void onCompleted() {
                completed();
            }
            @Override
            public void onError(Throwable e) {
                error();
            }

            @Override
            public void onNext(AppInfo appInfo) {
                next(appInfo);
            }

        });
    }

    private void loadListFilter(List<AppInfo> apps) {
        mDatas.clear();
        Observable.from(apps).filter(new Func1<AppInfo, Boolean>() {
            @Override
            public Boolean call(AppInfo appInfo) {
                return appInfo.getmName().startsWith("C");
            }
        }).subscribe(new Observer<AppInfo>() {
            @Override
            public void onCompleted() {
                completed();
            }

            @Override
            public void onError(Throwable e) {
                error();
            }

            @Override
            public void onNext(AppInfo appInfo) {
                Log.d(TAG, "onNext");
                mDatas.add(appInfo);
                mAdapter.addApplications(mDatas);
            }
        });
    }

    private void loadListTake(List<AppInfo> apps) {
        mDatas.clear();
        //takeLast/takeFirst/take....
        Observable.from(apps).take(3).subscribe(new Observer<AppInfo>() {
            @Override
            public void onCompleted() {
                completed();
            }

            @Override
            public void onError(Throwable e) {
                error();
            }
            @Override
            public void onNext(AppInfo appInfo) {
                next(appInfo);
            }
        });
    }

    private void loadList(List<AppInfo> apps) {
        mDatas.clear();
        Observable.from(apps).subscribe(new Observer<AppInfo>() {
            @Override
            public void onCompleted() {
                completed();
            }
            @Override
            public void onError(Throwable e) {
                error();
            }
            @Override
            public void onNext(AppInfo appInfo) {
                Log.d(TAG, "onNext");
                mDatas.add(appInfo);
                mAdapter.addApplications(mDatas);
            }
        });
    }

    private void loadApps(AppInfo appOne, AppInfo appTwo, AppInfo appThree) {
        mDatas.clear();
        Observable.just(appOne, appTwo, appThree).subscribe(new Observer<AppInfo>() {
            @Override
            public void onCompleted() {
                completed();
            }

            @Override
            public void onError(Throwable e) {
                error();
            }

            @Override
            public void onNext(AppInfo appInfo) {
                Log.d(TAG, "onNext");
                mDatas.add(appInfo);
                mAdapter.addApplications(mDatas);
            }
        });

    }

    private void loadAppsRepeat(AppInfo appOne, AppInfo appTwo, AppInfo appThree) {
        mDatas.clear();
        Observable.just(appOne, appTwo, appThree).repeat(3).subscribe(new Observer<AppInfo>() {
            @Override
            public void onCompleted() {
                completed();
            }

            @Override
            public void onError(Throwable e) {
                error();
            }

            @Override
            public void onNext(AppInfo appInfo) {
                next(appInfo);
            }
        });

    }

    private Observable<Integer> getInt() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (subscriber.isUnsubscribed()){
                    return;
                }
                Log.d(TAG,"GETINT");
                subscriber.onNext(42);
                subscriber.onCompleted();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public Context getActivity() {
        return MainActivity.this;
    }

    @Override
    public void onRefresh() {
        refreshTheList();
    }

    private void error() {
        Log.d(TAG, "onError");
        Toast.makeText(getActivity(), "onError", Toast.LENGTH_SHORT).show();
        siwpeRefresh.setRefreshing(false);
    }
    private void completed() {
        Log.d(TAG, "completed");
        Toast.makeText(getActivity(), "onCompleted", Toast.LENGTH_SHORT).show();
        siwpeRefresh.setRefreshing(false);
    }
    private void next(AppInfo appInfo) {
        Log.d(TAG, "onNext");
        mDatas.add(appInfo);
        mAdapter.addApplications(mDatas);
    }

}
