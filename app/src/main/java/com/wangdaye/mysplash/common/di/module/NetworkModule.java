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

@Module
public class NetworkModule {

    @Provides
    public AuthorizeService getAuthorizeService() {
        return new AuthorizeService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory());
    }

    @Provides
    public CollectionService getCollectionService() {
        return new CollectionService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory());
    }

    @Provides
    public FeedService getFeedService() {
        return new FeedService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory());
    }

    @Provides
    public GetStreamService getGetStreamService() {
        return new GetStreamService(Mysplash.getInstance().getHttpClient());
    }

    @Provides
    public NotificationService getNotificationService() {
        return new NotificationService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory());
    }

    @Provides
    public PhotoService getPhotoService() {
        return new PhotoService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory());
    }

    @Provides
    public SearchService getSearchService() {
        return new SearchService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory());
    }

    @Provides
    public StatusService getStatusService() {
        return new StatusService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory());
    }

    @Provides
    public UserService getUserService() {
        return new UserService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory());
    }

    @Provides
    public FollowService getFollowService() {
        return new FollowService(
                Mysplash.getInstance().getHttpClient(),
                Mysplash.getInstance().getGsonConverterFactory());
    }
}
