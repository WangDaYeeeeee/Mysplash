package com.wangdaye.common.presenter;

import androidx.annotation.NonNull;

import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.FollowEvent;
import com.wangdaye.common.network.ComponentCollection;
import com.wangdaye.common.network.observer.NoBodyObserver;
import com.wangdaye.common.network.service.FollowService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.reactivex.disposables.CompositeDisposable;

public class FollowUserPresenter {

    private static class Inner{
        private static FollowUserPresenter instance = new FollowUserPresenter();
    }

    public static FollowUserPresenter getInstance() {
        return Inner.instance;
    }

    private final List<User> userList;
    private final FollowService followService;
    private final ReadWriteLock lock;

    private FollowUserPresenter() {
        userList = new ArrayList<>();
        followService = new FollowService(
                ComponentCollection.getInstance().getHttpClient(),
                ComponentCollection.getInstance().getGsonConverterFactory(),
                ComponentCollection.getInstance().getRxJava2CallAdapterFactory(),
                new CompositeDisposable()
        );
        lock = new ReentrantReadWriteLock();
    }

    private int indexUser(User user) {
        for (int i = 0; i < userList.size(); i ++) {
            if (userList.get(i).username.equals(user.username)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isInProgress(User user) {
        lock.readLock().lock();
        boolean result = indexUser(user) >= 0;
        lock.readLock().unlock();
        return result;
    }

    public void follow(User user) {
        lock.writeLock().lock();
        if (indexUser(user) < 0) {
            userList.add(user);
            followService.followUser(
                    user.username,
                    new NoBodyObserver(succeed -> handleResult(user, true, succeed))
            );
        }
        lock.writeLock().unlock();

        MessageBus.getInstance().post(new FollowEvent(user));
    }

    public void unfollow(User user) {
        lock.writeLock().lock();
        if (indexUser(user) < 0) {
            userList.add(user);
            followService.cancelFollowUser(
                    user.username,
                    new NoBodyObserver(succeed -> handleResult(user, false, succeed))
            );
        }
        lock.writeLock().unlock();

        MessageBus.getInstance().post(new FollowEvent(user));
    }

    private void handleResult(@NonNull User user, boolean follow, boolean succeed) {
        lock.writeLock().lock();
        int index = indexUser(user);
        if (index >= 0) {
            userList.remove(index);
        }
        lock.writeLock().unlock();

        MessageBus.getInstance().post(new FollowEvent(user));

        if (succeed) {
            user.followed_by_user = follow;
            user.followers_count += follow ? 1 : -1;
        }
        MessageBus.getInstance().post(user);
    }
}
