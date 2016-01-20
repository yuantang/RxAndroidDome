package com.tom.rxanroiddome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;

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
    private static final int MAP =11 ;
    private static final int SCAN = 12;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Config.isDebug){
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
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
        menu.add(0,MAP,0,"map");
        menu.add(0,SCAN,0,"scan");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case FROM:
                loadList(Service.getAppInfos(getActivity()));
                break;
            case JUST:
                List<AppInfo> apps = Service.getAppInfos(getActivity());
                loadApps(apps.get(2), apps.get(4), apps.get(10));
                break;
            case REPEAT:
                List<AppInfo> appss = Service.getAppInfos(getActivity());
                loadAppsRepeat(appss.get(2), appss.get(4), appss.get(10));
                break;
            case DEFER:
                defer();
                break;
            case RANGE:
//                第一个是起始点，第二个是我们想发射数字的个数。
                range();
                break;
            case INTERVAL:
//                函数的两个参数：一个指定两次发射的时间间隔，另一个是用到的时间单位。
                interval();
                break;
            case TIMER:
//           用这个代码，你可以创建一个以初始值来延迟（上一个例子是3秒）
//           执行的interval()版本，然后每隔N秒就发射一个新的数字（前面的例子是3秒）。
                timer();
                break;
            case FILTER:
                //          RxJava让我们使用filter()方法来过滤我们观测序列中不想要的值，在上一章中，我们在几个例子
//           中使用了已安装的应用列表，但是我们只想展示以字母C开头的已安装的应用该怎么办呢？在这个新的例子中，
//           我们将使用同样的列表，但是我们会过滤它，通过把合适的谓词传给filter()函数来得到我们想要的值。
                loadListFilter(Service.getAppInfos(getActivity()));
                break;
            case TAKE:
                loadListTake(Service.getAppInfos(getActivity()));
                break;
            case DISTINCT://有且仅有一次(去重复)
                loadListDistinct(Service.getAppInfos(getActivity()));
                break;
            case MAP:// map(),flatMap(),concatMap(),flatMapIterable()以及switchMap().
                // 所有这些函数都作用于一个可观测序列，然后变换它发射的值，最后用一种新的形式返回它们.
                loadListMap(Service.getAppInfos(getActivity()));
                break;
            case SCAN:
//                scan()函数可以看做是一个累加器函数。
                scan();

                break;
        }
        return true;
    }

    private void scan() {
        Observable.just(1,2,3,4,5).scan(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer sum, Integer item) {
                return sum+item;
            }
        }).subscribe(new Subscriber<Integer>() {

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
                Log.d(TAG,"item---------->"+integer+"");
            }
        });
        mDatas.clear();
        Observable.from(Service.getAppInfos(getActivity())).scan(new Func2<AppInfo, AppInfo, AppInfo>() {
            @Override
            public AppInfo call(AppInfo appInfo, AppInfo appInfo2) {
                if (appInfo.getmName().length()>appInfo2.getmName().length()){
                    return  appInfo;
                }else {
                    return appInfo2;
                }
            }
        }).distinct().subscribe(new Observer<AppInfo>() {
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

    private void defer() {
   /*     Observable<Integer> deferred = Observable.defer();
        deferred.subscribe(new Observer<Integer>() {
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
                Log.d(TAG,integer+"");
            }
        });*/
    }

    private void range() {
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
    }

    private void interval() {
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
    }

    private void timer() {
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
    }

    private void loadListMap(List<AppInfo> appInfos) {
        mDatas.clear();
        Observable.from(appInfos).map(new Func1<AppInfo, AppInfo>() {
            @Override
            public AppInfo call(AppInfo appInfo) {
                String currentName= appInfo.getmName();
                String lowerCaseName=currentName.toLowerCase();
                appInfo.setmName(lowerCaseName);
                return appInfo;
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
                next(appInfo);
            }
        });
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
        int id = item.getItemId();
        switch (id){
            case R.id.item_merge:
//                在异步的世界经常会创建这样的场景，我们有多个来源但是只想有一个结果：多输入，单输出。RxJava的merge()方法将帮助你把两个甚至更多的Observables合并到他们发射的数据里。下图给出了把两个序列合并在一个最终发射的Observable。

                merge(Service.getAppInfos(getActivity()));

                break;
            case R.id.item_zip:
//                我们在处理多源时可能会带来这样一种场景：多从个Observables接收数据，处理它们，然后将它们合并成一个新的可观测序列来使用。RxJava有一个特殊的方法可以完成：zip()合并两个或者多个Observables发射出的数据项，根据指定的函数Func*变换它们，并发射一个新值。下图展示了zip()方法如何处理发射的“numbers”和“letters”然后将它们合并一个新的数据项：

                zip(Service.getAppInfos(getActivity()));
                break;
            case R.id.item_join:
//                前面两个方法，zip()和merge()方法作用在发射数据的范畴内，在决定如何操作值之前有些场景我们需要考虑时间的。RxJava的join()函数基于时间窗口将两个Observables发射的数据结合在一起。
                join(Service.getAppInfos(getActivity()));
                break;
            case R.id.item_combineLatest:
//                ombineLatest()函数有点像zip()函数的特殊形式。正如我们已经学习的，zip()作用于最近未打包的两个Observables。相反，combineLatest()作用于最近发射的数据项：如果Observable1发射了A并且Observable2发射了B和C，combineLatest()将会分组处理AB和AC
                combineLatest(Service.getAppInfos(getActivity()));
                break;
            case R.id.item_andTheWhen:
//                在将来还有一些zip()满足不了的场景。如复杂的架构，或者是仅仅为了个人爱好，你可以使用And/Then/When解决方案。它们在RxJava的joins包下，使用Pattern和Plan作为中介，将发射的数据集合并到一起。
                break;
            case R.id.item_down:
                startActivity(new Intent(MainActivity.this,DownLoadActivity.class));
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void combineLatest(final List<AppInfo> appInfos) {
        mDatas.clear();
        Observable<AppInfo> appsSequence = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, AppInfo>() {
                    @Override
                    public AppInfo call(Long aLong) {
                        return appInfos.get(aLong.intValue());
                    }
                });
        Observable<Long> tictoc = Observable.interval(1500, TimeUnit.MILLISECONDS);
        Observable.combineLatest(appsSequence, tictoc,new Func2<AppInfo, Long, AppInfo>() {
            @Override
            public AppInfo call(AppInfo appInfo, Long aLong) {
                appInfo.setmName(aLong + "" + appInfo.getmName());
                return appInfo;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<AppInfo>() {
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
    private void join(final List<AppInfo> appInfos) {
        mDatas.clear();
        Observable<Long> tictoc = Observable.interval(1000,TimeUnit.MILLISECONDS);
        Observable<AppInfo> appsSequence =
                Observable.interval(1000, TimeUnit.MILLISECONDS)
                        .map(new Func1<Long, AppInfo>() {
                            @Override
                            public AppInfo call(Long aLong) {
                                return appInfos.get(aLong.intValue());
                            }
                        });

        appsSequence.join(tictoc, new Func1<AppInfo, Observable<Long>>() {
            @Override
            public Observable<Long> call(AppInfo appInfo) {
                return Observable.timer(2,TimeUnit.HOURS);
            }
        }, new Func1<Long, Observable<Long>>() {
            @Override
            public Observable<Long> call(Long aLong) {
                return Observable.timer(0, TimeUnit.SECONDS);
            }
        }, new Func2<AppInfo, Long, AppInfo>() {
            @Override
            public AppInfo call(AppInfo appInfo, Long aLong) {
                appInfo.setmName(aLong + "" + appInfo.getmName());
                return appInfo;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .take(10)
                .subscribe(new Observer<AppInfo>() {
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

    private void zip(final List<AppInfo> appInfos) {
        mDatas.clear();
        Observable observableApp= Observable.from(appInfos);
        Observable tictoc=Observable.interval(1, TimeUnit.SECONDS);
        Observable.zip(observableApp, tictoc, new Func2<AppInfo, Long, AppInfo>() {
            @Override
            public AppInfo call(AppInfo appInfo, Long aLong) {
                appInfo.setmName(aLong + "" + appInfo.getmName());
                return appInfo;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppInfo>() {
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

    private void merge(List<AppInfo> apps) {
        mDatas.clear();
        Observable<AppInfo> observableApps =Observable.from(apps);

        Collections.reverse(apps);
        List reversedApps=new ArrayList();
        for (AppInfo app:apps){
            reversedApps.add(app);
        }
        Observable<AppInfo> observableReversedApps =Observable.from(reversedApps);

        Observable<AppInfo> mergedObserbable = Observable.merge(observableApps,observableReversedApps);

        mergedObserbable.subscribe(new Observer<AppInfo>(){
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
