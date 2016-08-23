package com.wangdaye.mysplash.collection.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui.dialog.UpdateCollectionDialog;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;
import com.wangdaye.mysplash.collection.presenter.activity.ToolbarImplementor;
import com.wangdaye.mysplash.collection.view.widget.CollectionPhotosView;
import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.data.data.User;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.i.view.ToolbarView;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackLayout;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

/**
 * Collection activity.
 * */

public class CollectionActivity extends MysplashActivity
        implements ToolbarView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, SwipeBackLayout.OnSwipeListener,
        UpdateCollectionDialog.OnCollectionChangedListener {
    // model.
    private Collection collection;
    public static final String DELETE_COLLECTION = "delete_collection";

    // view.
    private AppBarLayout appBar;
    private RelativeLayout creatorBar;
    private CircleImageView avatarImage;
    private CollectionPhotosView photosView;
    
    // presenter.
    private ToolbarPresenter toolbarPresenter;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initModel();
            initView();
            initPresenter();
            animShowView(photosView, 400);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        photosView.cancelRequest();
    }

    @Override
    protected void setTheme() {
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Collection);
        } else {
            setTheme(R.style.MysplashTheme_dark_Collection);
        }
    }

    @Override
    public void onBackPressed() {
        Mysplash.getInstance().setCollection(collection);
        Intent result = new Intent();
        result.putExtra(DELETE_COLLECTION, false);
        setResult(RESULT_OK, result);
        super.onBackPressed();
    }

    private void finishActivity(boolean delete) {
        Intent result = new Intent();
        result.putExtra(DELETE_COLLECTION, delete);
        setResult(RESULT_OK, result);
        finish();
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor(this);
    }

    /** <br> view. */

    @SuppressLint("SetTextI18n")
    private void initView() {
        SwipeBackLayout swipeBackLayout = (SwipeBackLayout) findViewById(R.id.activity_collection_swipeBackLayout);
        swipeBackLayout.setOnSwipeListener(this);

        StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_collection_statusBar);
        if (ThemeUtils.getInstance(this).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        this.appBar = (AppBarLayout) findViewById(R.id.activity_collection_appBar);

        TextView title = (TextView) findViewById(R.id.activity_collection_title);
        title.setText(collection.title);

        TextView description = (TextView) findViewById(R.id.activity_collection_description);
        if (TextUtils.isEmpty(collection.description)) {
            description.setVisibility(View.GONE);
        } else {
            TypefaceUtils.setTypeface(this, description);
            description.setText(collection.description);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_collection_toolbar);
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
            if (Mysplash.getInstance().isMyOwnCollection()) {
                toolbar.inflateMenu(R.menu.activity_collection_light);
                toolbar.setOnMenuItemClickListener(this);
            }
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
            if (Mysplash.getInstance().isMyOwnCollection()) {
                toolbar.inflateMenu(R.menu.activity_collection_dark);
                toolbar.setOnMenuItemClickListener(this);
            }
        }
        toolbar.setNavigationOnClickListener(this);

        this.creatorBar = (RelativeLayout) findViewById(R.id.activity_collection_creatorBar);

        this.avatarImage = (CircleImageView) findViewById(R.id.activity_collection_avatar);
        avatarImage.setOnClickListener(this);
        Glide.with(this)
                .load(collection.user.profile_image.large)
                .priority(Priority.HIGH)
                .override(128, 128)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(avatarImage);

        TextView subtitle = (TextView) findViewById(R.id.activity_collection_subtitle);
        TypefaceUtils.setTypeface(this, subtitle);
        subtitle.setText("By " + collection.user.name);

        this.photosView = (CollectionPhotosView) findViewById(R.id.activity_collection_photosView);
        photosView.setActivity(this);
        photosView.initRefresh();
    }

    /** <br> model. */

    private void initModel() {
        this.collection = Mysplash.getInstance().getCollection();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.touchNavigatorIcon();
                break;

            case R.id.activity_collection_avatar:
                toolbarPresenter.touchToolbar();
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        toolbarPresenter.touchMenuItem(item.getItemId());
        return false;
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        if (dir == SwipeBackLayout.UP_DIR) {
            return photosView.canSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight() + creatorBar.getMeasuredHeight();
        } else {
            return photosView.canSwipeBack(dir)
                    && appBar.getY() >= 0;
        }
    }

    @Override
    public void onSwipeFinish() {
        Mysplash.getInstance().setCollection(collection);
        finishActivity(false);
    }

    // on collection changed listener.

    @Override
    public void onEditCollection(Collection c) {
        collection = c;
        TextView title = (TextView) findViewById(R.id.activity_collection_title);
        title.setText(collection.title);

        TextView description = (TextView) findViewById(R.id.activity_collection_description);
        if (TextUtils.isEmpty(collection.description)) {
            description.setVisibility(View.GONE);
        } else {
            TypefaceUtils.setTypeface(this, description);
            description.setText(collection.description);
        }
    }

    @Override
    public void onDeleteCollection(Collection c) {
        Mysplash.getInstance().setCollection(c);
        finishActivity(true);
    }

    // view.

    // toolbar view.

    @Override
    public void touchNavigatorIcon() {
        Mysplash.getInstance().setCollection(collection);
        finishActivity(false);
    }

    @Override
    public void touchToolbar() {
        User u = User.buildUser(collection);
        Mysplash.getInstance().setUser(u);

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
            case R.id.action_edit:
                UpdateCollectionDialog dialog = new UpdateCollectionDialog();
                dialog.setCollection(collection);
                dialog.setOnCollectionChangedListener(this);
                dialog.show(getFragmentManager(), null);
                break;
        }
    }
}
