package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.BuildConfig;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.NullResponseBody;
import com.wangdaye.mysplash.common.network.api.GetStreamApi;
import com.wangdaye.mysplash.common.network.observer.NoBodyObserver;
import com.wangdaye.mysplash.common.network.observer.ObserverContainer;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Get stream service.
 * */

public class GetStreamService {

    private GetStreamApi api;
    private CompositeDisposable compositeDisposable;

    public GetStreamService(OkHttpClient client,
                            RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                            CompositeDisposable disposable) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.STREAM_API_BASE_URL)
                .client(client.newBuilder().build())
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((GetStreamApi.class));
        compositeDisposable = disposable;
    }

    public void requestFirstPageStream(NoBodyObserver<ResponseBody> observer) {
        if (AuthManager.getInstance().isAuthorized()
                && AuthManager.getInstance().getUser() != null
                && AuthManager.getInstance().getUser().numeric_id >= 0) {
            int numericId = AuthManager.getInstance().getUser().numeric_id;
            api.optionFirstPageStream(
                    numericId,
                    Mysplash.DEFAULT_PER_PAGE,
                    BuildConfig.GET_STREAM_KEY,
                    "unspecified"
            ).subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .onExceptionResumeNext(Observable.create(emitter -> emitter.onNext(new NullResponseBody())))
                    .flatMap((Function<ResponseBody, ObservableSource<ResponseBody>>) responseBody ->
                            api.getFirstPageStream(
                                    numericId,
                                    Mysplash.DEFAULT_PER_PAGE,
                                    BuildConfig.GET_STREAM_KEY,
                                    "unspecified"
                            )
                    ).observeOn(AndroidSchedulers.mainThread())
                    .onExceptionResumeNext(Observable.create(emitter -> emitter.onNext(new NullResponseBody())))
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        } else {
            observer.onFailed();
        }
    }

    public void requestNextPageStream(final String nextPage, NoBodyObserver<ResponseBody> observer) {
        api.optionNextPageStream(nextPage)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .onExceptionResumeNext(Observable.create(emitter -> emitter.onNext(new NullResponseBody())))
                .flatMap((Function<ResponseBody, ObservableSource<ResponseBody>>) responseBody ->
                        api.getNextPageStream(nextPage)
                ).observeOn(AndroidSchedulers.mainThread())
                .onExceptionResumeNext(Observable.create(emitter -> emitter.onNext(new NullResponseBody())))
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public String getStreamUsablePart(String stream) {
        String result = "\"results\": ";
        return "stream_feed="
                + stream.substring(
                        stream.indexOf(result) + result.length(),
                        stream.lastIndexOf("]") + 1
                );
    }

    public void cancel() {
        compositeDisposable.clear();
    }
}
