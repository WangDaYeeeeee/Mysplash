package com.wangdaye.mysplash.photo.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.photo.model.i.PhotoModel;
import com.wangdaye.mysplash.photo.presenter.i.PhotoInfoPresenter;
import com.wangdaye.mysplash.photo.view.activity.i.PhotoInfoView;
import com.wangdaye.mysplash.photo.view.dialog.StatsDialog;

/**
 * Photo information implementor.
 * */

public class PhotoInfoImp
        implements PhotoInfoPresenter,
        PopupMenu.OnMenuItemClickListener {
    // model.
    private PhotoModel photoModel;

    // view.
    private PhotoInfoView photoInfoView;

    /** <br> life cycle. */

    public PhotoInfoImp(PhotoModel photoModel, PhotoInfoView photoInfoView) {
        this.photoModel = photoModel;
        this.photoInfoView = photoInfoView;
    }

    /** <br> presenter. */

    @Override
    public void showWeb(Context c) {
        Uri photoUri = Uri.parse(photoModel.getHtmlUrl());
        c.startActivity(new Intent(Intent.ACTION_VIEW, photoUri));
    }

    @Override
    public void showAuthorInfo(Context c) {
        // do nothing.
    }

    @Override
    public void showMenu(Context c, View anchor) {
        PopupMenu menu = new PopupMenu(c, anchor);
        menu.inflate(R.menu.menu_activity_photo);
        menu.setOnMenuItemClickListener(this);
        menu.show();
    }

    /** <br> interface. */

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_stats:
                StatsDialog statsDialog = new StatsDialog();
                statsDialog.setPhoto(photoModel.getPhoto());
                photoInfoView.showStatsDialog(statsDialog);
                return true;
        }
        return false;
    }
}
