package com.wangdaye.common.di.module;

import com.wangdaye.common.di.annotation.ApplicationInstance;
import com.wangdaye.common.network.service.AuthorizeService;
import com.wangdaye.common.network.service.CollectionService;
import com.wangdaye.common.network.service.FeedService;
import com.wangdaye.common.network.service.FollowService;
import com.wangdaye.common.network.service.GetStreamService;
import com.wangdaye.common.network.service.NotificationService;
import com.wangdaye.common.network.service.PhotoService;
import com.wangdaye.common.network.service.SearchService;
import com.wangdaye.common.network.service.StatusService;
import com.wangdaye.common.network.service.UserService;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = {NetworkModule.class, RxJavaModule.class})
public class NetworkServiceModule {

    @Provides
    public AuthorizeService getAuthorizeService(@ApplicationInstance OkHttpClient client,
                                                @ApplicationInstance GsonConverterFactory gsonConverterFactory,
                                                @ApplicationInstance RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                                                CompositeDisposable disposable) {
        return new AuthorizeService(client, gsonConverterFactory, rxJava2CallAdapterFactory, disposable);
    }

    @Provides
    public CollectionService getCollectionService(@ApplicationInstance OkHttpClient client,
                                                  @ApplicationInstance GsonConverterFactory gsonConverterFactory,
                                                  @ApplicationInstance RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                                                  CompositeDisposable disposable) {
        return new CollectionService(client, gsonConverterFactory, rxJava2CallAdapterFactory, disposable);
    }

    @Provides
    public FeedService getFeedService(@ApplicationInstance OkHttpClient client,
                                      @ApplicationInstance GsonConverterFactory gsonConverterFactory,
                                      @ApplicationInstance RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                                      CompositeDisposable disposable) {
        return new FeedService(client, gsonConverterFactory, rxJava2CallAdapterFactory, disposable);
    }

    @Provides
    public FollowService getFollowService(@ApplicationInstance OkHttpClient client,
                                          @ApplicationInstance GsonConverterFactory gsonConverterFactory,
                                          @ApplicationInstance RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                                          CompositeDisposable disposable) {
        return new FollowService(client, gsonConverterFactory, rxJava2CallAdapterFactory, disposable);
    }

    @Provides
    public GetStreamService getGetStreamService(@ApplicationInstance OkHttpClient client,
                                                @ApplicationInstance RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                                                CompositeDisposable disposable) {
        return new GetStreamService(client, rxJava2CallAdapterFactory, disposable);
    }

    @Provides
    public NotificationService getNotificationService(@ApplicationInstance OkHttpClient client,
                                                      @ApplicationInstance GsonConverterFactory gsonConverterFactory,
                                                      @ApplicationInstance RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                                                      CompositeDisposable disposable) {
        return new NotificationService(client, gsonConverterFactory, rxJava2CallAdapterFactory, disposable);
    }

    @Provides
    public PhotoService getPhotoService(@ApplicationInstance OkHttpClient client,
                                        @ApplicationInstance GsonConverterFactory gsonConverterFactory,
                                        @ApplicationInstance RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                                        CompositeDisposable disposable) {
        return new PhotoService(client, gsonConverterFactory, rxJava2CallAdapterFactory, disposable);
    }

    @Provides
    public SearchService getSearchService(@ApplicationInstance OkHttpClient client,
                                          @ApplicationInstance GsonConverterFactory gsonConverterFactory,
                                          @ApplicationInstance RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                                          CompositeDisposable disposable) {
        return new SearchService(client, gsonConverterFactory, rxJava2CallAdapterFactory, disposable);
    }

    @Provides
    public StatusService getStatusService(@ApplicationInstance OkHttpClient client,
                                          @ApplicationInstance GsonConverterFactory gsonConverterFactory,
                                          @ApplicationInstance RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                                          CompositeDisposable disposable) {
        return new StatusService(client, gsonConverterFactory, rxJava2CallAdapterFactory, disposable);
    }

    @Provides
    public UserService getUserService(@ApplicationInstance OkHttpClient client,
                                      @ApplicationInstance GsonConverterFactory gsonConverterFactory,
                                      @ApplicationInstance RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                                      CompositeDisposable disposable) {
        return new UserService(client, gsonConverterFactory, rxJava2CallAdapterFactory, disposable);
    }
}
