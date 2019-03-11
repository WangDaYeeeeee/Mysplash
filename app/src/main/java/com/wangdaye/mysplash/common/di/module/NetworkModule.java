package com.wangdaye.mysplash.common.di.module;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.service.AuthorizeService;
import com.wangdaye.mysplash.common.network.service.CollectionService;
import com.wangdaye.mysplash.common.network.service.FeedService;
import com.wangdaye.mysplash.common.network.service.FollowService;
import com.wangdaye.mysplash.common.network.service.GetStreamService;
import com.wangdaye.mysplash.common.network.service.NotificationService;
import com.wangdaye.mysplash.common.network.service.PhotoService;
import com.wangdaye.mysplash.common.network.service.SearchService;
import com.wangdaye.mysplash.common.network.service.StatusService;
import com.wangdaye.mysplash.common.network.service.UserService;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module(includes = ApplicationModule.class)
public class NetworkModule {

    @Provides
    public AuthorizeService getAuthorizeService(CompositeDisposable disposable) {
        return new AuthorizeService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory(),
                Mysplash.getInstance().getRxJava2CallAdapterFactory(),
                disposable);
    }

    @Provides
    public CollectionService getCollectionService(CompositeDisposable disposable) {
        return new CollectionService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory(),
                Mysplash.getInstance().getRxJava2CallAdapterFactory(),
                disposable);
    }

    @Provides
    public FeedService getFeedService(CompositeDisposable disposable) {
        return new FeedService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory(),
                Mysplash.getInstance().getRxJava2CallAdapterFactory(),
                disposable);
    }

    @Provides
    public GetStreamService getGetStreamService(CompositeDisposable disposable) {
        return new GetStreamService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getRxJava2CallAdapterFactory(),
                disposable);
    }

    @Provides
    public NotificationService getNotificationService(CompositeDisposable disposable) {
        return new NotificationService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory(),
                Mysplash.getInstance().getRxJava2CallAdapterFactory(),
                disposable);
    }

    @Provides
    public PhotoService getPhotoService(CompositeDisposable disposable) {
        return new PhotoService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory(),
                Mysplash.getInstance().getRxJava2CallAdapterFactory(),
                disposable);
    }

    @Provides
    public SearchService getSearchService(CompositeDisposable disposable) {
        return new SearchService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory(),
                Mysplash.getInstance().getRxJava2CallAdapterFactory(),
                disposable);
    }

    @Provides
    public StatusService getStatusService(CompositeDisposable disposable) {
        return new StatusService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory(),
                Mysplash.getInstance().getRxJava2CallAdapterFactory(),
                disposable);
    }

    @Provides
    public UserService getUserService(CompositeDisposable disposable) {
        return new UserService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory(),
                Mysplash.getInstance().getRxJava2CallAdapterFactory(),
                disposable);
    }

    @Provides
    public FollowService getFollowService(CompositeDisposable disposable) {
        return new FollowService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory(),
                Mysplash.getInstance().getRxJava2CallAdapterFactory(),
                disposable);
    }
}
