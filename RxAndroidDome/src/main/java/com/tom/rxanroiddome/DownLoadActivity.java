package com.tom.rxanroiddome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tom.rxanroiddome.view.RateTextCircularProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class DownLoadActivity extends AppCompatActivity {
    public static final String TAG = "DownLoadActivity";

    @Bind(R.id.rate_progress_bar)
    RateTextCircularProgressBar rateProgressBar;
    @Bind(R.id.btn_download)
    Button btnDownload;

    String url = "http://archive.blender.org/fileadmin/movies/softboy.avi";
    final String destination = "sdcardsoftboy.avi";
    PublishSubject<Integer> mDownloadProgress = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        rateProgressBar.setProgress(0);
        rateProgressBar.setMax(100);
        rateProgressBar.getCircularProgressBar().setCircleWidth(30);
    }
    @OnClick({R.id.btn_download})
    protected void   OnClick(View view){
        if (view.getId()==R.id.btn_download){

            btnDownload.setText("正在下载...");
            btnDownload.setClickable(false);
            mDownloadProgress.distinct().observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {

                @Override
                public void onCompleted() {
                    Log.d(TAG,"onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG,"onError");
                }
                @Override
                public void onNext(Integer integer) {
                    Log.d(TAG,integer.intValue()+"");
                    rateProgressBar.setProgress(integer);
                }
            });

            obserbableDownload(url,destination).subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean sucess) {
                    resetDownloadButton();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(Environment.getExternalStorageDirectory()+destination);
                    intent.setDataAndType(Uri.fromFile(file), "video/avi");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable error) {
                    Log.d(TAG,error.getMessage());
                    Toast.makeText(DownLoadActivity.this, "Something went south", Toast.LENGTH_SHORT).show();
                    resetDownloadButton();
                }
            });
        }
    }
    private void resetDownloadButton() {
        btnDownload.setText("DownLoad File");
        btnDownload.setClickable(true);
        rateProgressBar.setProgress(0);
    }

    private Observable<Boolean> obserbableDownload(final String source, final String destination) {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean result = downloadFile(source, destination);
                    if (result) {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new Throwable("Download failed."));
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
    private boolean downloadFile(String source, String destination) {
        boolean result = false;
        InputStream input = null;
        OutputStream  output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(source);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
            int fileLength = connection.getContentLength();
            Log.e(TAG,"fileLength:"+fileLength);
            input = connection.getInputStream();
            output = new FileOutputStream(new File(Environment.getExternalStorageDirectory(),destination));
            byte data[] = new byte[1024*4];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                Log.e(TAG,count+"");
                total += count;
                Log.e(TAG,"total:"+total);
                if (count >0) {
                    int percentage = (int) (total * 100 / fileLength);
                    Log.e(TAG,percentage+"");
                    mDownloadProgress.onNext(percentage);
                }
                output.write(data, 0, count);
            }
            mDownloadProgress.onCompleted();
            result = true;
        } catch (Exception e) {
            mDownloadProgress.onError(e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                mDownloadProgress.onError(e);
            }
            if (connection != null) {
                connection.disconnect();
                mDownloadProgress.onCompleted();
            }
        }
        return result;
    }
}
