package com.tom.rxdome;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;

import com.google.gson.Gson;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpParams;
import com.tom.rxdome.adapter.Adapter;
import com.tom.rxdome.model.Result;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener{
    public static final String TAG ="MainActivity" ;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.siwpeRefresh)
    SwipeRefreshLayout siwpeRefresh;
    Adapter mAdapter;

    private String url = "http://api.1-blog.com/biz/bizserver/xiaohua/list.do";
    private int size = 10;
    private int page = 0;
    boolean isLoadingMore=false;
    int lastVisibleItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }
    private void getData(String url, int size, int page, final String tag) {
        HttpParams params = new HttpParams();
        params.put("size", size);
        params.put("page", page);
        Observable<com.kymjs.rxvolley.rx.Result> observable = new RxVolley.Builder()
                .url(url)
                .shouldCache(true)
                .httpMethod(RxVolley.Method.GET) //default GET or POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
                .cacheTime(6) //default: get 5min, post 0min
                .params(params)
                .contentType(RxVolley.ContentType.JSON)
                .getResult();

        observable
                .filter(new Func1<com.kymjs.rxvolley.rx.Result, Boolean>() {
                    @Override
                    public Boolean call(com.kymjs.rxvolley.rx.Result result) {
                        return result.data != null;
                    }
                })
                .map(new Func1<com.kymjs.rxvolley.rx.Result, String>() {
                    @Override
                    public String call(com.kymjs.rxvolley.rx.Result result) {
                        return new String(result.data);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String result) {
                        Log.e("kymjs", "======网络请求" + result);
                        Gson gson = new Gson();
                        Result datas = gson.fromJson(result, Result.class);
                        if (datas.getStatus().equals("000000")) {
                            if ("more".equals(tag)){
                                mAdapter.addMore(datas.getDetail());
                            }else{
                                mAdapter.addData(datas.getDetail());
                            }
                            siwpeRefresh.setRefreshing(false);
                        }
                    }
                });
    }

    private void initView() {
        setSupportActionBar(toolbar);
        siwpeRefresh.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_green_light)
        );
        siwpeRefresh.setOnRefreshListener(this);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        siwpeRefresh.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        final LinearLayoutManager mLayoutManager=new LinearLayoutManager(this);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        getData(url, size, 0,"");
        mAdapter = new Adapter(this);
        recyclerview.setAdapter(mAdapter);
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == mAdapter.getItemCount()) {
//                    siwpeRefresh.setRefreshing(true);
                    page++;
                    Log.d(TAG,"page:"+page);
                    getData(url, size, page,"more");
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            }
        });
    }
    @Override
    public void onRefresh() {
        getData(url, size, 0,"");
    }
}

