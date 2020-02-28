package com.wangdaye.common.ui.widget.swipeBackView;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wangdaye.common.R;
import com.wangdaye.common.base.application.MysplashApplication;

import java.util.concurrent.TimeUnit;

import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.RxLifecycle;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * {@link SwipeBackHelper}.
 * */
public abstract class SwipeBackActivity extends AppCompatActivity {

    private static class BitmapWrapper {

        @Nullable Bitmap bitmap;

        BitmapWrapper(@Nullable Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

    @Nullable
    protected abstract SwipeBackCoordinatorLayout provideSwipeBackView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setTag(
                R.id.tag_activity_create_flag, savedInstanceState == null);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SwipeBackCoordinatorLayout view = provideSwipeBackView();
        if (view == null) {
            return;
        }

        SwipeBackActivity secondFloorActivity = MysplashApplication.getInstance().getSecondFloorActivity();
        if (secondFloorActivity == null
                || getOrientation(secondFloorActivity) != getOrientation(this)) {
            view.prepareViews(this);
        } else {
            View decorView = getWindow().getDecorView();
            boolean createFlag = (Boolean) decorView.getTag(R.id.tag_activity_create_flag);
            decorView.setTag(R.id.tag_activity_create_flag, false);

            Observable<BitmapWrapper> snapshot = Observable.create((ObservableOnSubscribe<BitmapWrapper>) emitter -> {
                ViewGroup previousContentView = secondFloorActivity.findViewById(Window.ID_ANDROID_CONTENT);
                Bitmap bitmap = null;
                if (previousContentView != null) {
                    bitmap = SwipeBackHelper.getViewSnapshot(previousContentView.getChildAt(0));
                }
                emitter.onNext(new BitmapWrapper(bitmap));
            }).compose(RxLifecycle.bind(this).disposeObservableWhen(LifecycleEvent.STOP))
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            Observable<Long> timer = createFlag
                    ? Observable.timer(350, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    : Observable.just(System.currentTimeMillis());

            Observable.zip(snapshot, timer, (wrapper, aLong) -> wrapper)
                    .compose(RxLifecycle.bind(this).disposeObservableWhen(LifecycleEvent.STOP))
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(wrapper -> {
                        if (wrapper.bitmap != null) {
                            decorView.setBackground(new BitmapDrawable(getResources(), wrapper.bitmap));
                        } else {
                            view.prepareViews(this);
                        }
                    })
                    .subscribe();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            return;
        }
        SwipeBackCoordinatorLayout view = provideSwipeBackView();
        if (view != null) {
            view.clearViews();
        }
    }

    private static int getOrientation(SwipeBackActivity activity) {
        return activity.getResources().getConfiguration().orientation;
    }
}
