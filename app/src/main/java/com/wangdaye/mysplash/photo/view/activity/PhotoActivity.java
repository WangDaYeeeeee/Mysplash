package com.wangdaye.mysplash.photo.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.PhotoDetails;
import com.wangdaye.mysplash._common.data.tools.DownloadManager;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.model.PhotoInfoModel;
import com.wangdaye.mysplash._common.i.model.ScrollModel;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash._common.i.presenter.PhotoInfoPresenter;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash._common.i.view.DownloadView;
import com.wangdaye.mysplash._common.i.view.PhotoInfoView;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.i.view.ScrollView;
import com.wangdaye.mysplash._common.ui.dialog.StatsDialog;
import com.wangdaye.mysplash._common.ui.popup.PhotoMenuPopupWindow;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.LanguageUtils;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.ui.widget.FreedomImageView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackLayout;
import com.wangdaye.mysplash._common.ui.dialog.DownloadDialog;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;
import com.wangdaye.mysplash.photo.model.activity.DownloadObject;
import com.wangdaye.mysplash.photo.model.activity.PhotoInfoObject;
import com.wangdaye.mysplash.photo.presenter.activity.DownloadImplementor;
import com.wangdaye.mysplash.photo.presenter.activity.PhotoActivityPopupManageImplementor;
import com.wangdaye.mysplash.photo.presenter.activity.PhotoInfoImplementor;
import com.wangdaye.mysplash.photo.presenter.activity.ScrollImplementor;
import com.wangdaye.mysplash.photo.view.widget.PhotoDetailsView;
import com.wangdaye.mysplash.user.model.widget.ScrollObject;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

/**
 * Photo activity.
 * */

public class PhotoActivity extends AppCompatActivity
        implements PhotoInfoView, DownloadView, ScrollView, PopupManageView,
        View.OnClickListener, DownloadDialog.OnDismissListener, SwipeBackLayout.OnSwipeListener {
    // model.
    private PhotoInfoModel photoInfoModel;
    private DownloadModel downloadModel;
    private ScrollModel scrollModel;

    // view.
    private DownloadDialog dialog;
    private NestedScrollView scrollView;
    private RelativeLayout titleBar;
    private ImageButton menuBtn;
    private CircleImageView avatarImage;
    private LinearLayout buttonBar;
    private PhotoDetailsView detailsView;

    // presenter.
    private PhotoInfoPresenter photoInfoPresenter;
    private DownloadPresenter downloadPresenter;
    private ScrollPresenter scrollPresenter;
    private PopupManagePresenter popupManagePresenter;

    // data
    private boolean started = false;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        setTheme();
        Mysplash.getInstance().addActivity(this);
        setContentView(R.layout.activity_photo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!started) {
            started = true;
            initModel();
            initView();
            initPresenter();
            AnimUtils.animInitShow(titleBar, 200);
            AnimUtils.animInitShow(buttonBar, 300);
            AnimUtils.animInitShow(detailsView, 400);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().removeActivity();
        DownloadManager.getInstance().removeDownloadListener((DownloadImplementor) downloadPresenter);
        detailsView.cancelRequest();
    }

    private void setTheme() {
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Photo);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Photo);
        }
    }

    private void loadLanguage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString(
                getString(R.string.key_language),
                getResources().getStringArray(R.array.language_values)[0]);
        LanguageUtils.setLanguage(this, language);
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.photoInfoPresenter = new PhotoInfoImplementor(photoInfoModel, this);
        this.downloadPresenter = new DownloadImplementor(downloadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
        this.popupManagePresenter = new PhotoActivityPopupManageImplementor(this);
    }

    /** <br> view. */

    // init.

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    private void initView() {
        SwipeBackLayout swipeBackLayout = (SwipeBackLayout) findViewById(R.id.activity_photo_swipeBackLayout);
        swipeBackLayout.setOnSwipeListener(this);

        FreedomImageView photoImage = (FreedomImageView) findViewById(R.id.activity_photo_image);
        if (Mysplash.getInstance().getDrawable() != null) {
            photoImage.setImageDrawable(Mysplash.getInstance().getDrawable());
        } else {
            Glide.with(this)
                    .load(photoInfoModel.getPhoto().urls.regular)
                    .priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(photoImage);
        }

        this.scrollView = (NestedScrollView) findViewById(R.id.activity_photo_scrollView);

        this.titleBar = (RelativeLayout) findViewById(R.id.activity_photo_titleBar);

        this.menuBtn = (ImageButton) findViewById(R.id.activity_photo_menuBtn);

        this.avatarImage = (CircleImageView) findViewById(R.id.activity_photo_avatar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatarImage.setTransitionName(photoInfoModel.getPhoto().user.username);
        }
        avatarImage.setOnClickListener(this);
        Glide.with(this)
                .load(photoInfoModel.getPhoto().user.profile_image.large)
                .priority(Priority.NORMAL)
                .crossFade(300)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(128, 128)
                .into(avatarImage);

        ImageButton menuButton = (ImageButton) findViewById(R.id.activity_photo_menuBtn);
        menuButton.setOnClickListener(this);

        TextView title = (TextView) findViewById(R.id.activity_photo_title);
        title.setText("By " + photoInfoModel.getPhoto().user.name);

        TextView subtitle = (TextView) findViewById(R.id.activity_photo_subtitle);
        subtitle.setText("On " + photoInfoModel.getPhoto().created_at.split("T")[0]);
        TypefaceUtils.setTypeface(this, subtitle);

        this.buttonBar = (LinearLayout) findViewById(R.id.activity_photo_btnBar);

        ImageButton[] optionButtons = new ImageButton[] {
                (ImageButton) findViewById(R.id.activity_photo_downloadBtn),
                (ImageButton) findViewById(R.id.activity_photo_shareBtn),
                (ImageButton) findViewById(R.id.activity_photo_wallBtn)};
        for (ImageButton optionButton : optionButtons) {
            optionButton.setOnClickListener(this);
        }

        this.detailsView = (PhotoDetailsView) findViewById(R.id.activity_photo_detailsView);
        detailsView.requestPhotoDetails();

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_photo_toolbar);
        toolbar.setTitle(title.getText().toString());
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);

        if (ThemeUtils.getInstance(this).isLightTheme()) {
            ((ImageButton) findViewById(R.id.activity_photo_downloadBtn)).setImageResource(R.drawable.ic_download_light);
            ((ImageButton) findViewById(R.id.activity_photo_shareBtn)).setImageResource(R.drawable.ic_send_light);
            ((ImageButton) findViewById(R.id.activity_photo_wallBtn)).setImageResource(R.drawable.ic_mountain_light);
            ((ImageButton) findViewById(R.id.activity_photo_menuBtn)).setImageResource(R.drawable.ic_menu_light);
        } else {
            ((ImageButton) findViewById(R.id.activity_photo_downloadBtn)).setImageResource(R.drawable.ic_download_dark);
            ((ImageButton) findViewById(R.id.activity_photo_shareBtn)).setImageResource(R.drawable.ic_send_dark);
            ((ImageButton) findViewById(R.id.activity_photo_wallBtn)).setImageResource(R.drawable.ic_mountain_dark);
            ((ImageButton) findViewById(R.id.activity_photo_menuBtn)).setImageResource(R.drawable.ic_menu_dark);
        }
    }

    /** <br> model. */

    // init.

    private void initModel() {
        this.photoInfoModel = new PhotoInfoObject();
        this.downloadModel = new DownloadObject(photoInfoModel.getPhoto());
        this.scrollModel = new ScrollObject();
    }

    /** <br> permission. */

    private void requestPermission(int permissionCode, int type) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        switch (permissionCode) {
            case Mysplash.WRITE_EXTERNAL_STORAGE:
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(
                            new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            type);
                } else {
                    downloadByType(type);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        for (int i = 0; i < permission.length; i ++) {
            switch (permission[i]) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    if (grantResult[i] == PackageManager.PERMISSION_GRANTED) {
                        downloadByType(requestCode);
                    } else {
                        Toast.makeText(
                                this,
                                getString(R.string.feedback_need_permission),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    public void downloadByType(int type) {
        switch (type) {
            case DownloadObject.DOWNLOAD_TYPE:
                downloadPresenter.download();
                break;

            case DownloadObject.SHARE_TYPE:
                downloadPresenter.share();
                break;

            case DownloadObject.WALLPAPER_TYPE:
                downloadPresenter.setWallpaper();
                break;
        }
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                break;

            case R.id.activity_photo_toolbar:
                scrollPresenter.scrollToTop();
                break;

            case R.id.activity_photo_avatar:
                photoInfoPresenter.touchAuthorAvatar();
                break;

            case R.id.activity_photo_menuBtn:
                popupManagePresenter.showPopup(this, menuBtn, null, 0);
                break;

            case R.id.activity_photo_downloadBtn:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    downloadPresenter.download();
                } else {
                    requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadObject.DOWNLOAD_TYPE);
                }
                break;

            case R.id.activity_photo_shareBtn:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    downloadPresenter.download();
                } else {
                    requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadObject.SHARE_TYPE);
                }
                break;

            case R.id.activity_photo_wallBtn:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    downloadPresenter.download();
                } else {
                    requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadObject.WALLPAPER_TYPE);
                }
                break;
        }
    }

    // on dismiss listener.

    @Override
    public void onBackstage() {
        downloadPresenter.setDialogShowing(false);
    }

    @Override
    public void onCancel() {
        downloadPresenter.setDialogShowing(false);
        downloadPresenter.cancelDownloading();
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackLayout.canSwipeBack(scrollView, dir);
    }

    @Override
    public void onSwipeFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    // view.

    // photo info view.

    @Override
    public void touchAuthorAvatar() {
        Intent intent = new Intent(this, UserActivity.class);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent);
        } else {
            View v = avatarImage;
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            this,
                            Pair.create(v, getString(R.string.transition_user_avatar)));
            ActivityCompat.startActivity(this, intent, options.toBundle());
        }
    }

    @Override
    public void touchMenuItem(int itemId) {
        switch (itemId) {
            case PhotoMenuPopupWindow.ITEM_STATS:
                StatsDialog dialog = new StatsDialog();
                dialog.setPhoto(photoInfoPresenter.getPhoto());
                dialog.show(getFragmentManager(), null);
                break;

            case PhotoMenuPopupWindow.ITEM_BROWSER:
                Intent p = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(photoInfoPresenter.getPhoto().links.html));
                startActivity(p);
                break;

            case PhotoMenuPopupWindow.ITEM_DOWNLOAD_PAGE:
                Intent d = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(photoInfoPresenter.getPhoto().links.download));
                startActivity(d);
                break;
        }
    }

    @Override
    public void drawPhotoDetails(PhotoDetails details) {
        // do nothing.
    }

    // download view.

    @Override
    public void showDownloadDialog() {
        this.dialog = new DownloadDialog();
        dialog.setOnDismissListener(this);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), null);
    }

    @Override
    public void dismissDownloadDialog() {
        dialog.dismiss();
    }

    @Override
    public void onDownloadProcess(int progress) {
        dialog.setDownloadProgress(progress);
    }

    // scroll view.

    @Override
    public void scrollToTop() {
        ((NestedScrollView) findViewById(R.id.activity_photo_scrollView)).smoothScrollTo(0, 0);
    }

    @Override
    public void autoLoad(int dy) {
        // do nothing.
    }

    @Override
    public boolean needBackToTop() {
        return false;
    }

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        photoInfoPresenter.touchMenuItem(position);
    }
}