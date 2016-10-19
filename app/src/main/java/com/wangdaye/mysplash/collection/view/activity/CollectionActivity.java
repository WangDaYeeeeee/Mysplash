package com.wangdaye.mysplash.collection.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash._common.i.model.BrowsableModel;
import com.wangdaye.mysplash._common.i.model.EditResultModel;
import com.wangdaye.mysplash._common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash._common.i.presenter.EditResultPresenter;
import com.wangdaye.mysplash._common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash._common.i.view.BrowsableView;
import com.wangdaye.mysplash._common.i.view.EditResultView;
import com.wangdaye.mysplash._common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash._common.ui.dialog.RequestBrowsableDataDialog;
import com.wangdaye.mysplash._common.ui.dialog.UpdateCollectionDialog;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;
import com.wangdaye.mysplash.collection.model.activity.BorwsableObject;
import com.wangdaye.mysplash.collection.model.activity.EditResultObject;
import com.wangdaye.mysplash.collection.presenter.activity.BrowsableImplementor;
import com.wangdaye.mysplash.collection.presenter.activity.EditResultImplementor;
import com.wangdaye.mysplash.collection.presenter.activity.SwipeBackManageImplementor;
import com.wangdaye.mysplash.collection.presenter.activity.ToolbarImplementor;
import com.wangdaye.mysplash.collection.view.widget.CollectionPhotosView;
import com.wangdaye.mysplash._common.data.entity.Collection;
import com.wangdaye.mysplash._common.data.entity.User;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackLayout;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

/**
 * Collection activity.
 * */

public class CollectionActivity extends MysplashActivity
        implements SwipeBackManageView, EditResultView, BrowsableView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, SwipeBackLayout.OnSwipeListener,
        UpdateCollectionDialog.OnCollectionChangedListener {
    // model.
    private EditResultModel editResultModel;
    private BrowsableModel browsableModel;

    // view.
    private RequestBrowsableDataDialog requestDialog;

    private CoordinatorLayout container;
    private AppBarLayout appBar;
    private RelativeLayout creatorBar;
    private CircleImageView avatarImage;
    private CollectionPhotosView photosView;
    
    // presenter.
    private ToolbarPresenter toolbarPresenter;
    private SwipeBackManagePresenter swipeBackManagePresenter;
    private EditResultPresenter editResultPresenter;
    private BrowsablePresenter browsablePresenter;

    // data
    public static final String DELETE_COLLECTION = "delete_collection";

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        initModel();
        initPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initView(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        browsablePresenter.cancelRequest();
        if (photosView != null) {
            photosView.cancelRequest();
        }
    }

    @Override
    protected void setTheme() {
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Common);
        }
    }

    @Override
    public void onBackPressed() {
        if (Mysplash.getInstance().isActivityInBackstage()) {
            super.onBackPressed();
        } else if (photosView.needPagerBackToTop()
                && BackToTopUtils.getInstance(this).isSetBackToTop(false)) {
            photosView.pagerBackToTop();
        } else {
            Intent result = new Intent();
            result.putExtra(DELETE_COLLECTION, false);
            setResult(RESULT_OK, result);
            super.onBackPressed();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                overridePendingTransition(0, R.anim.activity_slide_out_bottom);
            }
        }
    }

    public void finishActivity(int dir, boolean delete) {
        Intent result = new Intent();
        result.putExtra(DELETE_COLLECTION, delete);
        setResult(RESULT_OK, result);
        finish();
        switch (dir) {
            case SwipeBackLayout.UP_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_top);
                break;

            case SwipeBackLayout.DOWN_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                break;
        }
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
        this.swipeBackManagePresenter = new SwipeBackManageImplementor(this);
        this.editResultPresenter = new EditResultImplementor(editResultModel, this);
        this.browsablePresenter = new BrowsableImplementor(browsableModel, this);
    }

    /** <br> view. */

    // init.

    @SuppressLint("SetTextI18n")
    private void initView(boolean init) {
        if (init && browsablePresenter.isBrowsable()) {
            browsablePresenter.requestBrowsableData();
        } else {
            Collection c = (Collection) editResultPresenter.getEditKey();

            SwipeBackLayout swipeBackLayout = (SwipeBackLayout) findViewById(R.id.activity_collection_swipeBackLayout);
            swipeBackLayout.setOnSwipeListener(this);

            StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_collection_statusBar);
            if (ThemeUtils.getInstance(this).isNeedSetStatusBarMask()) {
                statusBar.setBackgroundResource(R.color.colorPrimary_light);
                statusBar.setMask(true);
            }

            this.container = (CoordinatorLayout) findViewById(R.id.activity_collection_container);
            this.appBar = (AppBarLayout) findViewById(R.id.activity_collection_appBar);

            TextView title = (TextView) findViewById(R.id.activity_collection_title);
            title.setText(c.title);

            TextView description = (TextView) findViewById(R.id.activity_collection_description);
            if (TextUtils.isEmpty(c.description)) {
                description.setVisibility(View.GONE);
            } else {
                TypefaceUtils.setTypeface(this, description);
                description.setText(c.description);
            }

            Toolbar toolbar = (Toolbar) findViewById(R.id.activity_collection_toolbar);
            if (ThemeUtils.getInstance(this).isLightTheme()) {
                if (browsablePresenter.isBrowsable()) {
                    toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_light);
                } else {
                    toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
                }
                toolbar.inflateMenu(R.menu.activity_collection_toolbar_light);
                toolbar.setOnMenuItemClickListener(this);
            } else {
                if (browsablePresenter.isBrowsable()) {
                    toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_dark);
                } else {
                    toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
                }
                toolbar.inflateMenu(R.menu.activity_collection_toolbar_dark);
                toolbar.setOnMenuItemClickListener(this);
            }
            toolbar.setNavigationOnClickListener(this);
            if (AuthManager.getInstance().getUsername() != null
                    && AuthManager.getInstance().getUsername().equals(c.user.username)) {
                toolbar.getMenu().getItem(0).setVisible(true);
            } else {
                toolbar.getMenu().getItem(0).setVisible(false);
            }
            if (c.curated) {
                toolbar.getMenu().getItem(2).setVisible(true);
            } else {
                toolbar.getMenu().getItem(2).setVisible(false);
            }

            this.creatorBar = (RelativeLayout) findViewById(R.id.activity_collection_creatorBar);
            creatorBar.setOnClickListener(this);

            this.avatarImage = (CircleImageView) findViewById(R.id.activity_collection_avatar);
            avatarImage.setOnClickListener(this);
            Glide.with(this)
                    .load(c.user.profile_image.large)
                    .priority(Priority.HIGH)
                    .override(128, 128)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(avatarImage);

            TextView subtitle = (TextView) findViewById(R.id.activity_collection_subtitle);
            TypefaceUtils.setTypeface(this, subtitle);
            subtitle.setText(getString(R.string.by) + " " + c.user.name);

            this.photosView = (CollectionPhotosView) findViewById(R.id.activity_collection_photosView);
            photosView.initMP(this, (Collection) editResultPresenter.getEditKey());
            photosView.initRefresh();

            AnimUtils.animInitShow(photosView, 400);
        }
    }

    // interface.

    public CollectionPhotosView getPhotosView() {
        return photosView;
    }

    /** <br> model. */

    // init.

    private void initModel() {
        this.editResultModel = new EditResultObject();
        this.browsableModel = new BorwsableObject(getIntent());
    }

    // interface.

    public Collection getCollection() {
        return (Collection) editResultPresenter.getEditKey();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                if (browsablePresenter.isBrowsable()) {
                    browsablePresenter.visitParentView();
                }
                toolbarPresenter.touchNavigatorIcon(this);
                break;

            case R.id.activity_collection_creatorBar:
                toolbarPresenter.touchToolbar(this);
                break;

            case R.id.activity_collection_avatar:
                User u = User.buildUser((Collection) editResultPresenter.getEditKey());
                Mysplash.getInstance().setUser(u);

                Intent intent = new Intent(this, UserActivity.class);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_in, 0);
                } else {
                    View v = avatarImage;
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(
                                    this,
                                    Pair.create(v, getString(R.string.transition_user_avatar)));
                    ActivityCompat.startActivity(this, intent, options.toBundle());
                }
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem(this, item.getItemId());
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return swipeBackManagePresenter.checkCanSwipeBack(dir);
    }

    @Override
    public void onSwipeFinish(int dir) {
        swipeBackManagePresenter.swipeBackFinish(this, dir);
    }

    // on collection changed listener.

    @Override
    public void onEditCollection(Collection c) {
        editResultPresenter.updateSomething(c);
    }

    @Override
    public void onDeleteCollection(Collection c) {
        editResultPresenter.deleteSomething(c);
    }

    // snackbar container.

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    // view.

    // swipe back manage view.

    @Override
    public boolean checkCanSwipeBack(int dir) {
        if (dir == SwipeBackLayout.UP_DIR) {
            return photosView.canSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight() + creatorBar.getMeasuredHeight();
        } else {
            return photosView.canSwipeBack(dir)
                    && appBar.getY() >= 0;
        }
    }

    // edit result view.

    @Override
    public void drawCreateResult(Object newKey) {
        // do nothing.
    }

    @Override
    public void drawUpdateResult(Object newKey) {
        Collection c = (Collection) newKey;

        TextView title = (TextView) findViewById(R.id.activity_collection_title);
        title.setText(c.title);

        TextView description = (TextView) findViewById(R.id.activity_collection_description);
        if (TextUtils.isEmpty(c.description)) {
            description.setVisibility(View.GONE);
        } else {
            TypefaceUtils.setTypeface(this, description);
            description.setText(c.description);
        }
    }

    @Override
    public void drawDeleteResult(Object oldKey) {
        finishActivity(SwipeBackLayout.NULL_DIR, true);
    }

    // browsable view.

    @Override
    public void showRequestDialog() {
        requestDialog = new RequestBrowsableDataDialog();
        requestDialog.show(getFragmentManager(), null);
    }

    @Override
    public void dismissRequestDialog() {
        requestDialog.dismiss();
        requestDialog = null;
    }

    @Override
    public void drawBrowsableView() {
        initModel();
        initPresenter();
        initView(false);
    }

    @Override
    public void visitParentView() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
