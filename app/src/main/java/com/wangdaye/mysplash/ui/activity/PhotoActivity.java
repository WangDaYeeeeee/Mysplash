package com.wangdaye.mysplash.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.data.constant.Mysplash;
import com.wangdaye.mysplash.data.constant.Permission;
import com.wangdaye.mysplash.data.unslpash.model.SimplifiedPhoto;
import com.wangdaye.mysplash.ui.dialog.DownloadDialog;
import com.wangdaye.mysplash.ui.widget.CircleImageView;
import com.wangdaye.mysplash.ui.widget.FreedomImageView;
import com.wangdaye.mysplash.utils.DisplayUtils;

import java.io.File;

/**
 * Photo activity.
 * */

public class PhotoActivity extends AppCompatActivity
        implements View.OnClickListener, DownloadDialog.OnCancelListener {
    // widget
    private DownloadDialog dialog;

    // data
    private SimplifiedPhoto photo;
    private String downlaodScale;

    private boolean started = false;
    private boolean downloading = false;

    private int downloadType;
    private final int SIMPLE_DOWNLOAD_TYPE = 1;
    private final int SHARE_DOWNLOAD_TYPE = 2;
    private final int WALL_DOWNLOAD_TYPE = 3;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.setStatusBarTransparent(this);
        DisplayUtils.setStatusBarTextDark(this);
        DisplayUtils.setWindowTop(this,
                getString(R.string.app_name),
                ContextCompat.getColor(this, R.color.colorPrimary));
        setContentView(R.layout.activity_photo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!started) {
            started = true;
            initData();
            initWidget();
        }
    }

    /** <br> UI. */

    @SuppressLint("SetTextI18n")
    private void initWidget() {
        RelativeLayout background = (RelativeLayout) findViewById(R.id.activity_photo_background);
        background.setBackground(new ColorDrawable(Color.argb((int) (255 * 0.6), 0, 0, 0)));
        background.setOnClickListener(this);

        FreedomImageView photoImage = (FreedomImageView) findViewById(R.id.activity_photo_image);
        photoImage.setSize(photo.width, photo.height);
        Glide.with(this)
                .load(photo.url_regular)
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(photoImage);

        CircleImageView avatarImage = (CircleImageView) findViewById(R.id.activity_photo_avatar);
        avatarImage.setOnClickListener(this);
        Glide.with(this)
                .load(photo.url_user_avatar)
                .priority(Priority.NORMAL)
                .crossFade(300)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(avatarImage);

        ImageButton menuButton = (ImageButton) findViewById(R.id.activity_photo_menuBtn);
        menuButton.setOnClickListener(this);

        TextView title = (TextView) findViewById(R.id.activity_photo_title);
        title.setText(photo.user_name);

        TextView subtitle = (TextView) findViewById(R.id.activity_photo_subtitle);
        subtitle.setText("taken on " + photo.created_at.split("T")[0]);

        ImageButton[] optionButtons = new ImageButton[] {
                (ImageButton) findViewById(R.id.activity_photo_downloadBtn),
                (ImageButton) findViewById(R.id.activity_photo_shareBtn),
                (ImageButton) findViewById(R.id.activity_photo_wallBtn)};
        for (ImageButton optionButton : optionButtons) {
            optionButton.setOnClickListener(this);
        }
    }

    /** <br> data. */

    private void initData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.photo = getIntent().getParcelableExtra(getString(R.string.intent_key_photo));
        this.downlaodScale = sharedPreferences.getString(
                getString(R.string.key_download_scale),
                "compact");
    }

    private void download() {
        this.dialog = new DownloadDialog();
        dialog.setOnDismissListener(this);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), null);

        DownloadRequest request = new DownloadRequest.Builder()
                .setTitle(photo.id + Mysplash.DOWNLOAD_FORMAT)
                .setUri(getDownloadUrl())
                .setFolder(new File(Mysplash.DOWNLOAD_PATH))
                .build();

        DownloadManager.getInstance()
                .download(request, photo.id, new DownloadCallback(photo.id));
    }

    private String getDownloadUrl() {
        switch (downlaodScale) {
            case "compact":
                return photo.url_full;

            case "raw":
                return photo.url_raw;

            case "standard":
                return photo.url_download;

            default:
                return photo.url_full;
        }
    }

    /** <br> permission. */

    private void requestPermission(int permissionCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        switch (permissionCode) {
            case Permission.WRITE_EXTERNAL_STORAGE:
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(
                            new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            permissionCode);
                } else {
                    download();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        switch (requestCode) {
            case Permission.WRITE_EXTERNAL_STORAGE:
                if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    download();
                } else {
                    Toast.makeText(
                            this,
                            getString(R.string.feedback_need_permission),
                            Toast.LENGTH_SHORT).show();
                }
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
                Uri photoUri = Uri.parse(photo.url_html);
                startActivity(new Intent(Intent.ACTION_VIEW, photoUri));
                break;

            case R.id.activity_photo_avatar:
                // do nothing.
                break;

            case R.id.activity_photo_downloadBtn:
                if (!downloading) {
                    downloadType = SIMPLE_DOWNLOAD_TYPE;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        download();
                    } else {
                        requestPermission(Permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
                break;

            case R.id.activity_photo_shareBtn:
                if (!downloading) {
                    downloadType = SHARE_DOWNLOAD_TYPE;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        download();
                    } else {
                        requestPermission(Permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
                break;

            case R.id.activity_photo_wallBtn:
                if (!downloading) {
                    downloadType = WALL_DOWNLOAD_TYPE;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        download();
                    } else {
                        requestPermission(Permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
                break;
        }
    }

    // on dismiss listener.

    @Override
    public void onCancel() {
        if (downloading) {
            downloading = false;
            DownloadManager.getInstance().cancel(photo.id);
        }
    }

    private class DownloadCallback implements CallBack {
        // data
        private String id;

        public DownloadCallback(String id) {
            this.id = id;
        }

        @Override
        public void onStarted() {
            downloading = true;
        }

        @Override
        public void onConnecting() {
            dialog.setDownloadProgress(-1);
        }

        @Override
        public void onConnected(long l, boolean b) {
            dialog.setDownloadProgress(0);
        }

        @Override
        public void onProgress(long l, long l1, int i) {
            dialog.setDownloadProgress(i);
        }

        @Override
        public void onCompleted() {
            downloading = false;
            dialog.dismiss();
            Toast.makeText(
                    PhotoActivity.this,
                    getString(R.string.feedback_download_success) + "\n" + "ID = " + id,
                    Toast.LENGTH_SHORT).show();

            Uri file = Uri.fromFile(new File(Mysplash.DOWNLOAD_PATH + photo.id + Mysplash.DOWNLOAD_FORMAT));
            Intent broadcast = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, file);
            sendBroadcast(broadcast);
            switch (downloadType) {
                case SIMPLE_DOWNLOAD_TYPE:
                    break;

                case SHARE_DOWNLOAD_TYPE: {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, file);
                    intent.setType("image/*");
                    startActivity(Intent.createChooser(intent, getString(R.string.feedback_choose_share_app)));
                    break;
                }

                case WALL_DOWNLOAD_TYPE: {
                    Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                    intent.setDataAndType(file, "image/jpg");
                    intent.putExtra("mimeType", "image/jpg");
                    startActivity(Intent.createChooser(intent, getString(R.string.feedback_choose_wallpaper_app)));
                    break;
                }
            }
        }

        @Override
        public void onDownloadPaused() {
            dialog.dismiss();
            Toast.makeText(
                    PhotoActivity.this,
                    getString(R.string.feedback_download_failed) + "\n" + "ID = " + id,
                    Toast.LENGTH_SHORT).show();
            downloading = false;
        }

        @Override
        public void onDownloadCanceled() {
            Toast.makeText(
                    PhotoActivity.this,
                    getString(R.string.feedback_download_cancel) + "\n" + "ID = " + id,
                    Toast.LENGTH_SHORT).show();
            downloading = false;
        }

        @Override
        public void onFailed(DownloadException e) {
            dialog.dismiss();
            Toast.makeText(
                    PhotoActivity.this,
                    getString(R.string.feedback_download_failed) + "\n" + "ID = " + id,
                    Toast.LENGTH_SHORT).show();
            downloading = false;
        }
    }
}