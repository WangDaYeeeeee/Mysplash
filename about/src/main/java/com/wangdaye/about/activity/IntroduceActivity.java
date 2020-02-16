package com.wangdaye.about.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.wangdaye.about.R;
import com.wangdaye.about.R2;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.ui.adapter.PagerAdapter;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.component.ComponentFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.RxLifecycle;
import io.reactivex.Emitter;
import io.reactivex.Observable;

/**
 * Introduce activity.
 *
 * This activity is used to show introduce to user.
 *
 * */

@Route(path = IntroduceActivity.INTRODUCE_ACTIVITY)
public class IntroduceActivity extends MysplashActivity
        implements ViewPager.OnPageChangeListener {

    @BindView(R2.id.activity_introduce_container) CoordinatorLayout container;
    @BindView(R2.id.activity_introduce_viewPager) ViewPager viewPager;
    @BindView(R2.id.activity_introduce_button) Button button;
    @OnClick(R2.id.activity_introduce_button) void clickBtn() {
        if (viewPager.getCurrentItem() == introduceModelList.size() - 1) {
            // last page --> finish activity.
            finishSelf(true);
        } else {
            // jump to next page.
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    private boolean backPressed = false; // mark the first click action.

    private List<IntroduceModel> introduceModelList;

    // if the version code > saved version, we need show this activity for user.
    private static final int VERSION_CODE = 1;
    private static final int FIRST_VERSION = 0;

    public static final String PREFERENCE_NAME = "mysplash_introduce";
    private static final String KEY_INTRODUCE_VERSION = "introduce_version";

    public static final String INTRODUCE_ACTIVITY = "/about/IntroduceActivity";

    /**
     * Model of introduce page.
     * */
    private class IntroduceModel {
        // data.
        public String title;
        int imageRes;
        String description;

        // life cycle.

        IntroduceModel(int titleRes, int imageRes, int descriptionRes) {
            this.title = getString(titleRes);
            this.imageRes = imageRes;
            this.description = getString(descriptionRes);
        }
    }

    public static void checkAndStartIntroduce(final Activity a) {
        SharedPreferences sharedPreferences = a.getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE);
/*
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_INTRODUCE_VERSION, FIRST_VERSION);
        editor.apply();
*/
        if (sharedPreferences.getInt(KEY_INTRODUCE_VERSION, FIRST_VERSION) < VERSION_CODE) {
            startIntroduceActivity(a);
        }
    }

    public static void watchAllIntroduce(Activity a) {
        SharedPreferences.Editor editor = a.getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        ).edit();
        editor.putInt(KEY_INTRODUCE_VERSION, FIRST_VERSION);
        editor.apply();

        startIntroduceActivity(a);
    }

    public static void startIntroduceActivity(Activity a) {
        ARouter.getInstance()
                .build(IntroduceActivity.INTRODUCE_ACTIVITY)
                // .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        ButterKnife.bind(this);
        initData();
        initWidget();
    }

    @Override
    public void handleBackPressed() {
        // double click to exit.
        if (backPressed) {
            finishSelf(true);
        } else {
            backPressed = true;
            NotificationHelper.showSnackbar(this, getString(R.string.feedback_click_again_to_exit));

            Observable.create(Emitter::onComplete)
                    .compose(RxLifecycle.bind(this).disposeObservableWhen(LifecycleEvent.DESTROY))
                    .delay(2, TimeUnit.SECONDS)
                    .doOnComplete(() -> backPressed = false)
                    .subscribe();
        }
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    public void finishSelf(boolean backPressed) {
        finish();
        if (backPressed) {
            // overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @Nullable
    @Override
    protected SwipeBackCoordinatorLayout provideSwipeBackView() {
        return null;
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        int versionCode = sharedPreferences.getInt(KEY_INTRODUCE_VERSION, FIRST_VERSION);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_INTRODUCE_VERSION, VERSION_CODE);
        editor.apply();

        introduceModelList = new ArrayList<>();
        switch (versionCode) {
            case FIRST_VERSION:
                introduceModelList.add(
                        new IntroduceModel(
                                R.string.introduce_title_back_top,
                                R.drawable.illustration_back_top,
                                R.string.introduce_description_back_top
                        )
                );
                break;
        }
    }

    private void initWidget() {
        AppCompatImageButton backBtn = findViewById(R.id.activity_introduce_backBtn);
        backBtn.setOnClickListener(v -> finishSelf(true));

        if (introduceModelList.size() <= 1) {
            findViewById(R.id.activity_introduce_buttonBar).setVisibility(View.GONE);
        }

        setBottomButtonStyle(0);

        initPage();

        InkPageIndicator indicator = findViewById(R.id.activity_introduce_indicator);
        if (introduceModelList.size() <= 1) {
            indicator.setAlpha(0f);
        } else {
            indicator.setViewPager(viewPager);
            indicator.setAlpha(1f);
        }
    }

    @SuppressLint("InflateParams")
    private void initPage() {
        List<View> pageList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        for (int i = 0; i < introduceModelList.size(); i ++) {
            View v = LayoutInflater.from(this).inflate(R.layout.container_introduce, null);

            TextView title = v.findViewById(R.id.container_introduce_title);
            title.setText(introduceModelList.get(i).title);

            AppCompatImageView image = v.findViewById(R.id.container_introduce_image);
            ImageHelper.loadImage(this, image, introduceModelList.get(i).imageRes);

            TextView description = v.findViewById(R.id.container_introduce_description);
            description.setText(introduceModelList.get(i).description);

            setPageButtonStyle(v, i);

            pageList.add(v);
            titleList.add(introduceModelList.get(i).title);
        }

        PagerAdapter adapter = new PagerAdapter(pageList, titleList);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    // control.

    private void setBottomButtonStyle(int page) {
        if (page == introduceModelList.size() - 1) {
            button.setText(getString(R.string.enter));
        } else {
            button.setText(getString(R.string.next));
        }
    }

    private void setPageButtonStyle(View page, int position) {
        Button b = page.findViewById(R.id.container_introduce_button);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (ThemeManager.getInstance(this).isLightTheme()) {
                b.setBackgroundResource(R.color.colorPrimary_dark);
            } else {
                b.setBackgroundResource(R.color.colorPrimary_light);
            }
        } else {
            b.setBackgroundResource(R.drawable.button_login);
        }

        if (introduceModelList.get(position).imageRes == R.drawable.illustration_back_top) {
            b.setText(getString(R.string.set));
            b.setOnClickListener(v -> {
                if (introduceModelList.get(viewPager.getCurrentItem()).imageRes
                        == R.drawable.illustration_back_top) {
                    ComponentFactory.getSettingsService().startSettingsActivity(this);
                }
            });
        } else {
            b.setVisibility(View.GONE);
        }
    }

    // interface.

    // on page changed listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        setBottomButtonStyle(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing.
    }
}
