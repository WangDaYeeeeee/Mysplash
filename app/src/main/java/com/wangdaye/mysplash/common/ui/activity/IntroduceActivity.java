package com.wangdaye.mysplash.common.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.widget.SafeHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Introduce activity.
 *
 * This activity is used to show introduce to user.
 *
 * */

public class IntroduceActivity extends MysplashActivity
        implements View.OnClickListener, ViewPager.OnPageChangeListener,
        SafeHandler.HandlerContainer {

    @BindView(R.id.activity_introduce_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_introduce_viewPager)
    ViewPager viewPager;

    @BindView(R.id.activity_introduce_button)
    Button button;

    private SafeHandler<IntroduceActivity> handler;

    private boolean backPressed = false; // mark the first click action.

    private List<IntroduceModel> introduceModelList;

    // if the version code > saved version, we need show this activity for user.
    private static final int VERSION_CODE = 1;
    private static final int FIRST_VERSION = 0;

    public static final String PREFERENCE_NAME = "mysplash_introduce";
    private static final String KEY_INTRODUCE_VERSION = "introduce_version";

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

    public static void checkAndStartIntroduce(final MysplashActivity a) {
        SharedPreferences sharedPreferences = a.getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE);
/*
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_INTRODUCE_VERSION, FIRST_VERSION);
        editor.apply();
*/
        if (sharedPreferences.getInt(KEY_INTRODUCE_VERSION, FIRST_VERSION) < VERSION_CODE) {
            IntentHelper.startIntroduceActivity(a);
        }
    }

    public static void watchAllIntroduce(MysplashActivity a) {
        SharedPreferences.Editor editor = a.getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_INTRODUCE_VERSION, FIRST_VERSION);
        editor.apply();

        IntentHelper.startIntroduceActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            ButterKnife.bind(this);
            initData();
            initWidget();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void setTheme() {
        if (ThemeManager.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Common);
        }
    }

    @Override
    public void handleBackPressed() {
        // double click to exit.
        if (backPressed) {
            finishSelf(true);
        } else {
            backPressed = true;
            NotificationHelper.showSnackbar(getString(R.string.feedback_click_again_to_exit));

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.obtainMessage(1).sendToTarget();
                }
            }, 2000);
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
            overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE);
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
                                R.string.introduce_description_back_top));
                break;
        }
    }

    private void initWidget() {
        this.handler = new SafeHandler<>(this);

        ImageButton backBtn = ButterKnife.findById(this, R.id.activity_introduce_backBtn);
        ThemeManager.setImageResource(backBtn, R.drawable.ic_close_light, R.drawable.ic_close_dark);
        backBtn.setOnClickListener(this);

        button.setOnClickListener(this);
        setBottomButtonStyle(0);

        initPage();

        InkPageIndicator indicator = ButterKnife.findById(this, R.id.activity_introduce_indicator);
        indicator.setViewPager(viewPager);
        if (introduceModelList.size() <= 1) {
            indicator.setAlpha(0f);
        } else {
            indicator.setAlpha(1f);
        }
    }

    @SuppressLint("InflateParams")
    private void initPage() {
        List<View> pageList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        for (int i = 0; i < introduceModelList.size(); i ++) {
            View v = LayoutInflater.from(this).inflate(R.layout.container_introduce, null);

            TextView title = ButterKnife.findById(v, R.id.container_introduce_title);
            title.setText(introduceModelList.get(i).title);

            ImageView image = ButterKnife.findById(v, R.id.container_introduce_image);
            Glide.with(this)
                    .load(introduceModelList.get(i).imageRes)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(image);

            TextView description = ButterKnife.findById(v, R.id.container_introduce_description);
            description.setText(introduceModelList.get(i).description);

            setPageButtonStyle(v, i);

            pageList.add(v);
            titleList.add(introduceModelList.get(i).title);
        }

        MyPagerAdapter adapter = new MyPagerAdapter(pageList, titleList);

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
        Button b = ButterKnife.findById(page, R.id.container_introduce_button);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (ThemeManager.getInstance(this).isLightTheme()) {
                b.setBackgroundResource(R.color.colorPrimary_dark);
            } else {
                b.setBackgroundResource(R.color.colorPrimary_light);
            }
        } else {
            b.setBackgroundResource(R.drawable.button_login);
        }

        switch (introduceModelList.get(position).imageRes) {
            case R.drawable.illustration_back_top:
                b.setText(getString(R.string.set));
                b.setOnClickListener(this);
                break;

            default:
                b.setVisibility(View.GONE);
                break;
        }
    }

    // interface.

    // on click listener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_introduce_backBtn:
                // finish activity.
                finishSelf(true);
                break;

            case R.id.activity_introduce_button:
                if (viewPager.getCurrentItem() == introduceModelList.size() - 1) {
                    // last page --> finish activity.
                    finishSelf(true);
                } else {
                    // jump to next page.
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
                break;

            case R.id.container_introduce_button:
                switch (introduceModelList.get(viewPager.getCurrentItem()).imageRes) {
                    case R.drawable.illustration_back_top:
                        Intent s = new Intent(this, SettingsActivity.class);
                        startActivity(s);
                        overridePendingTransition(R.anim.activity_slide_in, 0);
                        break;
                }
                break;
        }
    }

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

    // handler.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                backPressed = false;
                break;
        }
    }
}
