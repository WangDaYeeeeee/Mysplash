package com.wangdaye.mysplash.photo.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.model.SimplifiedPhoto;
import com.wangdaye.mysplash.common.view.activity.MysplashActivity;
import com.wangdaye.mysplash.photo.model.DownloadObject;
import com.wangdaye.mysplash.photo.model.PhotoObject;
import com.wangdaye.mysplash.photo.model.i.DownloadModel;
import com.wangdaye.mysplash.photo.model.i.PhotoModel;
import com.wangdaye.mysplash.photo.presenter.DownloadImp;
import com.wangdaye.mysplash.photo.presenter.PhotoInfoImp;
import com.wangdaye.mysplash.photo.presenter.i.DownloadPresenter;
import com.wangdaye.mysplash.photo.presenter.i.PhotoInfoPresenter;
import com.wangdaye.mysplash.photo.view.activity.i.DownloadView;
import com.wangdaye.mysplash.photo.view.activity.i.PhotoInfoView;
import com.wangdaye.mysplash.photo.view.dialog.DownloadDialog;
import com.wangdaye.mysplash.common.widget.CircleImageView;
import com.wangdaye.mysplash.common.widget.FreedomImageView;
import com.wangdaye.mysplash.photo.view.dialog.StatsDialog;

/**
 * Photo activity.
 * */

public class PhotoActivity extends MysplashActivity
        implements PhotoInfoView, DownloadView,
        View.OnClickListener, DownloadDialog.OnDismissListener {
    // model.
    private PhotoModel photoModel;
    private DownloadModel downloadModel;

    // view.
    private DownloadDialog dialog;

    // presenter.
    private DownloadPresenter downloadPresenter;
    private PhotoInfoPresenter photoInfoPresenter;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initModel();
            initView();
            initPresenter();
        }
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.downloadPresenter = new DownloadImp(photoModel, downloadModel, this);
        this.photoInfoPresenter = new PhotoInfoImp(photoModel, this);
    }

    /** <br> view. */

    @SuppressLint("SetTextI18n")
    private void initView() {
        RelativeLayout background = (RelativeLayout) findViewById(R.id.activity_photo_background);
        background.setBackground(new ColorDrawable(Color.argb((int) (255 * 0.6), 0, 0, 0)));
        background.setOnClickListener(this);

        FreedomImageView photoImage = (FreedomImageView) findViewById(R.id.activity_photo_image);
        photoImage.setOnClickListener(this);
        photoImage.setSize(photoModel.getWidth(), photoModel.getHeight());
        Glide.with(this)
                .load(photoModel.getRegularUrl())
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(photoImage);

        CircleImageView avatarImage = (CircleImageView) findViewById(R.id.activity_photo_avatar);
        avatarImage.setOnClickListener(this);
        Glide.with(this)
                .load(photoModel.getAvatarUrl())
                .priority(Priority.NORMAL)
                .crossFade(300)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(avatarImage);

        ImageButton menuButton = (ImageButton) findViewById(R.id.activity_photo_menuBtn);
        menuButton.setOnClickListener(this);

        TextView title = (TextView) findViewById(R.id.activity_photo_title);
        title.setText(photoModel.getUserName());

        TextView subtitle = (TextView) findViewById(R.id.activity_photo_subtitle);
        subtitle.setText("taken on " + photoModel.getCreateTime());

        ImageButton[] optionButtons = new ImageButton[] {
                (ImageButton) findViewById(R.id.activity_photo_downloadBtn),
                (ImageButton) findViewById(R.id.activity_photo_shareBtn),
                (ImageButton) findViewById(R.id.activity_photo_wallBtn)};
        for (ImageButton optionButton : optionButtons) {
            optionButton.setOnClickListener(this);
        }
    }

    /** <br> model. */

    // init.

    private void initModel() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SimplifiedPhoto photo = getIntent().getParcelableExtra(getString(R.string.intent_key_photo));
        String scale = sharedPreferences.getString(
                getString(R.string.key_download_scale),
                "compact");

        this.photoModel = new PhotoObject(photo, scale);
        this.downloadModel = new DownloadObject();
    }

    // interface.

    public int getDownloadId() {
        return downloadPresenter.getDownloadId();
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
            case DownloadObject.SIMPLE_DOWNLOAD_TYPE:
                downloadPresenter.download(this);
                break;

            case DownloadObject.SHARE_DOWNLOAD_TYPE:
                downloadPresenter.share(this);
                break;

            case DownloadObject.WALL_DOWNLOAD_TYPE:
                downloadPresenter.setWallpaper(this);
                break;
        }
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_photo_background:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                break;

            case R.id.activity_photo_image:
                photoInfoPresenter.showWeb(this);
                break;

            case R.id.activity_photo_avatar:
                photoInfoPresenter.showAuthorInfo(this);
                break;

            case R.id.activity_photo_menuBtn:
                photoInfoPresenter.showMenu(this, findViewById(R.id.activity_photo_menuBtn));
                break;

            case R.id.activity_photo_downloadBtn:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    downloadPresenter.download(this);
                } else {
                    requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadObject.SIMPLE_DOWNLOAD_TYPE);
                }
                break;

            case R.id.activity_photo_shareBtn:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    downloadPresenter.download(this);
                } else {
                    requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadObject.SHARE_DOWNLOAD_TYPE);
                }
                break;

            case R.id.activity_photo_wallBtn:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    downloadPresenter.download(this);
                } else {
                    requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadObject.WALL_DOWNLOAD_TYPE);
                }
                break;
        }
    }

    // on dismiss listener.

    @Override
    public void onDismiss() {
        downloadPresenter.dismissDialog();
    }

    @Override
    public void onCancel() {
        downloadPresenter.cancelDownload(this);
    }

    // view.

    // photo information view.

    @Override
    public void showStatsDialog(StatsDialog dialog) {
        dialog.show(getFragmentManager(), null);
    }

    // download view.

    @Override
    public void showDialog() {
        this.dialog = new DownloadDialog();
        dialog.setOnDismissListener(this);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), null);
    }

    @Override
    public void dismissDialog() {
        dialog.dismiss();
    }

    @Override
    public void setDialogProgress(int progress) {
        dialog.setDownloadProgress(progress);
    }
}