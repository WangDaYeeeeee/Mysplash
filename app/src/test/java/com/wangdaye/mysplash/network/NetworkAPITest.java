package com.wangdaye.mysplash.network;

import com.wangdaye.mysplash.common.network.TLSCompactHelper;
import com.wangdaye.mysplash.common.network.service.PhotoService;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkAPITest {

    private static PhotoService photoService;

    @BeforeClass
    public static void init() {
        Scheduler immediate = new Scheduler() {
            @Override
            public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
                return super.scheduleDirect(run, 0, unit);
            }

            @Override
            public Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(Runnable::run, false);
            }
        };

        RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);

        OkHttpClient httpClient = TLSCompactHelper.getOKHttpClient();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
        RxJava2CallAdapterFactory rxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create();
        CompositeDisposable disposable = new CompositeDisposable();

        photoService = new PhotoService(
                httpClient,
                gsonConverterFactory,
                rxJava2CallAdapterFactory,
                disposable
        );
    }

    @Test
    public void downloadPhoto() {
        photoService.downloadPhoto("bG34b0wtRbw");
    }
}
