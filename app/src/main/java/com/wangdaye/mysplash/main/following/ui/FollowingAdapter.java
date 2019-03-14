package com.wangdaye.mysplash.main.following.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.download.NotificationHelper;
import com.wangdaye.mysplash.common.db.DatabaseHelper;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.utils.bus.PhotoEvent;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.presenter.DispatchCollectionsChangedPresenter;
import com.wangdaye.mysplash.user.ui.UserActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Following adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Photo}.
 *
 * */

public class FollowingAdapter extends FooterAdapter<RecyclerView.ViewHolder>
        implements PhotoFeedHolder.ParentAdapter {

    private List<Photo> photoList; // this list is used to save the feed data.
    private List<ViewType> typeList; // this list is used to save the display information of view holder.
    private List<ViewType> photoViewTypeList;

    private boolean hasFooter;
    private int columnCount;

    @Nullable private ItemEventCallback callback;

    /**
     * This class is used to save the view holder's information.
     * */
    class ViewType {

        int photoPosition;
        int adapterPosition;

        @ViewTypeRule int type;

        ViewType(int photoPosition, int adapterPosition, int type) {
            this.photoPosition = photoPosition;
            this.adapterPosition = adapterPosition;
            this.type = type;
        }
    }

    @IntDef({
            TitleFeedHolder.VIEW_TYPE_TITLE,
            PhotoFeedHolder.VIEW_TYPE_PHOTO})
    @interface ViewTypeRule {}

    public FollowingAdapter(Context context, List<Photo> list, int columnCount) {
        super(context);
        this.photoList = list;
        this.typeList = new ArrayList<>();
        this.photoViewTypeList = new ArrayList<>();
        buildTypeList(0);

        this.hasFooter = hasFooter(context);
        this.columnCount = columnCount;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        // adapter adapterPosition (type adapterPosition).
        if (isFooter(position)) {
            // footer.
            return FooterHolder.buildInstance(parent);
        } else {
            ViewType viewType = typeList.get(position);
            switch (viewType.type) {
                case TitleFeedHolder.VIEW_TYPE_TITLE: {
                    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_following_title, parent, false);
                    return new TitleFeedHolder(v, columnCount);
                }
                case PhotoFeedHolder.VIEW_TYPE_PHOTO:
                default: {
                    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_following_photo, parent, false);
                    return new PhotoFeedHolder(v);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // adapter adapterPosition (type adapterPosition).
        if (!isFooter(position)) {
            if (holder instanceof TitleFeedHolder) {
                ((TitleFeedHolder) holder).onBindView(getUser(position));
            } else if (holder instanceof PhotoFeedHolder) {
                ((PhotoFeedHolder) holder).onBindView(
                        photoList.get(typeList.get(position).photoPosition),
                        columnCount,
                        false,
                        callback,
                        this);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        if (holder instanceof PhotoFeedHolder && position < getItemCount() && !payloads.isEmpty()) {
            ((PhotoFeedHolder) holder).onBindView(
                    photoList.get(typeList.get(position).photoPosition),
                    columnCount,
                    true,
                    callback,
                    this);
        } else {
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof TitleFeedHolder) {
            ((TitleFeedHolder) holder).onRecycled();
        } else if (holder instanceof PhotoFeedHolder) {
            ((PhotoFeedHolder) holder).onRecycled();
        }
    }

    @Override
    public int getItemCount() {
        return typeList.size() + (hasFooter ? 1 : 0);
    }

    @Override
    public int getRealItemCount() {
        return photoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    protected boolean hasFooter(Context context) {
        return !DisplayUtils.isLandscape(context)
                && DisplayUtils.getNavigationBarHeight(context.getResources()) != 0;
    }

    public int getTypeItemCount() {
        return typeList.size();
    }

    // control.

    public void buildTypeList(int photoListFromIndex) {
        for (int i = photoListFromIndex; i < photoList.size(); i ++) {
            if (typeList.size() == 0) {
                typeList.add(new ViewType(i, typeList.size(), TitleFeedHolder.VIEW_TYPE_TITLE));
                typeList.add(new ViewType(i, typeList.size(), PhotoFeedHolder.VIEW_TYPE_PHOTO));
            } else {
                int lastTypeIndex = typeList.size() - 1;
                Photo photo = getPhoto(lastTypeIndex);
                if (photo == null || !photo.user.username.equals(photoList.get(i).user.username)) {
                    typeList.add(new ViewType(i, typeList.size(), TitleFeedHolder.VIEW_TYPE_TITLE));
                    typeList.add(new ViewType(i, typeList.size(), PhotoFeedHolder.VIEW_TYPE_PHOTO));
                } else {
                    typeList.add(new ViewType(i, typeList.size(), PhotoFeedHolder.VIEW_TYPE_PHOTO));
                }
            }
            photoViewTypeList.add(typeList.get(typeList.size() - 1));
        }
    }

    @Nullable
    public User getUser(int adapterPosition) {
        if (adapterPosition >= typeList.size()) {
            return null;
        }
        int photoPosition = typeList.get(adapterPosition).photoPosition;
        if (photoPosition >= photoList.size()) {
            return null;
        }
        return photoList.get(photoPosition).user;
    }

    @Nullable
    private Photo getPhoto(int adapterPosition) {
        if (adapterPosition >= typeList.size()) {
            return null;
        }
        int photoPosition = typeList.get(adapterPosition).photoPosition;
        if (photoPosition >= photoList.size()) {
            return null;
        }
        return photoList.get(photoPosition);
    }

    public int getPhotoHolderAdapterPosition(int photoPosition) {
        return photoViewTypeList.get(photoPosition).adapterPosition;
    }

    public boolean isFooterView(int adapterPosition) {
        return typeList.size() <= adapterPosition + 1
                || typeList.get(adapterPosition + 1).type == TitleFeedHolder.VIEW_TYPE_TITLE;
    }

    public void setTitleAvatarVisibility(RecyclerView.ViewHolder lastHolder,
                                         RecyclerView.ViewHolder newHolder) {
        if (lastHolder instanceof TitleFeedHolder) {
            ((TitleFeedHolder) lastHolder).setAvatarVisibility(true);
        }

        if (newHolder instanceof TitleFeedHolder) {
            ((TitleFeedHolder) newHolder).setAvatarVisibility(false);
        }
    }

    // interface.

    public interface ItemEventCallback {
        void onLikeOrDislikePhoto(Photo photo, int adapterPosition, boolean setToLike);
        void onDownload(Photo photo);
    }

    public void setItemEventCallback(@Nullable ItemEventCallback c) {
        this.callback = c;
    }

    // parent adapter.

    @Override
    public void startPhotoActivity(View image, View background, int adapterPosition) {
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null) {
            ArrayList<Photo> list = new ArrayList<>();
            int headIndex = typeList.get(adapterPosition).photoPosition - 2;
            int size = 5;
            if (headIndex < 0) {
                headIndex = 0;
            }
            if (headIndex + size - 1 > photoList.size() - 1) {
                size = photoList.size() - headIndex;
            }
            for (int i = headIndex; i < headIndex + size; i ++) {
                list.add(photoList.get(i));
            }

            IntentHelper.startPhotoActivity(
                    activity,
                    image,
                    background,
                    list,
                    typeList.get(adapterPosition).photoPosition,
                    headIndex);
        }
    }
}

/**
 * Title holder.
 *
 * CollectionHolder class for {@link FollowingAdapter} to show the title part of following feed data.
 *
 * */
class TitleFeedHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_following_title_background) RelativeLayout background;

    @BindView(R.id.item_following_title_avatar) CircleImageView avatar;
    @BindView(R.id.item_following_title_actor) TextView actor;
    @OnClick({
            R.id.item_following_title_avatar,
            R.id.item_following_title_actor}) void checkActor() {
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (user != null && activity != null) {
            IntentHelper.startUserActivity(activity, avatar, background, user, UserActivity.PAGE_PHOTO);
        }
    }

    @BindView(R.id.item_following_title_verb) TextView verb;
    @OnClick(R.id.item_following_title_verb) void clickVerb() {
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (user != null && activity != null && !TextUtils.isEmpty(user.location)) {
            IntentHelper.startSearchActivity(activity, user.location);
        }
    }

    @Nullable private User user;
    private boolean avatarVisibility;
    static final int VIEW_TYPE_TITLE = 0;

    TitleFeedHolder(View itemView, int columnCount) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        avatarVisibility = false;
        setAvatarVisibility(true);

        if (columnCount > 1) {
            StaggeredGridLayoutManager.LayoutParams params
                    = (StaggeredGridLayoutManager.LayoutParams) background.getLayoutParams();
            params.setFullSpan(true);
            background.setLayoutParams(params);
        }
    }

    @SuppressLint("SetTextI18n")
    void onBindView(@Nullable User user) {
        this.user = user;

        if (user != null) {
            ImageHelper.loadAvatar(avatar.getContext(), avatar, user, null);
            actor.setText(user.name);

            if (!TextUtils.isEmpty(user.location)) {
                verb.setVisibility(View.VISIBLE);
                verb.setText(user.location);
            } else {
                verb.setVisibility(View.GONE);
            }
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(avatar);
    }

    void setAvatarVisibility(boolean visibility) {
        if (visibility != avatarVisibility) {
            avatarVisibility = visibility;

            float alpha = visibility ? 1F : 0F;
            avatar.setAlpha(alpha);
            avatar.setEnabled(visibility);
        }
    }
}

/**
 * Photo holder.
 *
 * CollectionHolder class for {@link FollowingAdapter} to show photo data.
 *
 * */
class PhotoFeedHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_following_photo_card) CardView card;
    @BindView(R.id.item_following_photo_image) FreedomImageView image;

    @BindView(R.id.item_following_photo_avatar) CircleImageView avatar;
    @BindView(R.id.item_following_photo_title) TextView title;

    @BindView(R.id.item_following_photo_collectionButton) AppCompatImageButton collectionButton;
    @BindView(R.id.item_following_photo_likeButton) CircularProgressIcon likeButton;

    private Photo photo;
    @Nullable private FollowingAdapter.ItemEventCallback callback;
    @Nullable private ParentAdapter parentAdapter;

    static final int VIEW_TYPE_PHOTO = 1;

    PhotoFeedHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void onBindView(Photo photo, int columnCount, boolean update,
                    @Nullable FollowingAdapter.ItemEventCallback callback, @NonNull ParentAdapter adapter) {
        Context context = itemView.getContext();

        this.photo = photo;
        this.callback = callback;
        this.parentAdapter = adapter;

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
        int margin = context.getResources().getDimensionPixelSize(R.dimen.normal_margin);
        if (columnCount > 1) {
            params.setMarginStart(0);
            params.setMarginEnd(margin);
            params.setMargins(0, 0, margin, margin);
            card.setLayoutParams(params);
        } else {
            params.setMargins(
                    context.getResources().getDimensionPixelSize(R.dimen.large_icon_size), 0, margin, margin);
            card.setLayoutParams(params);
        }

        image.setSize(photo.width, photo.height);

        if (!update) {
            ImageHelper.loadAvatar(avatar.getContext(), avatar, photo.user, null);

            title.setText("");
            image.setShowShadow(false);

            ImageHelper.loadRegularPhoto(image.getContext(), image, photo, () -> {
                photo.loadPhotoSuccess = true;
                if (!photo.hasFadedIn) {
                    photo.hasFadedIn = true;
                    ImageHelper.startSaturationAnimation(image.getContext(), image);
                }
                title.setText(photo.user.name);
                image.setShowShadow(true);
            });
        }

        if (photo.current_user_collections != null && photo.current_user_collections.size() != 0) {
            collectionButton.setImageResource(R.drawable.ic_item_collected);
        } else {
            collectionButton.setImageResource(R.drawable.ic_item_collect);
        }

        likeButton.setProgressColor(Color.WHITE);
        if (photo.settingLike) {
            likeButton.setProgressState();
        } else {
            likeButton.setResultState(
                    photo.liked_by_user ? R.drawable.ic_item_heart_red : R.drawable.ic_item_heart_outline);
        }

        card.setCardBackgroundColor(
                ImageHelper.computeCardBackgroundColor(card.getContext(), photo.color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.setTransitionName(photo.id + "-cover");
            card.setTransitionName(photo.id + "-background");
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
        likeButton.recycleImageView();
    }

    // interface.

    public interface ParentAdapter {
        void startPhotoActivity(View image, View background, int adapterPosition);
    }

    @OnClick(R.id.item_following_photo_card) void clickItem() {
        if (parentAdapter != null) {
            parentAdapter.startPhotoActivity(image, card, getAdapterPosition());
        }
    }

    @OnClick(R.id.item_following_photo_avatar) void checkAuthor() {
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null) {
            IntentHelper.startUserActivity(
                    activity, avatar, card, photo.user, UserActivity.PAGE_PHOTO);
        }
    }

    @OnClick(R.id.item_following_photo_likeButton) void likePhoto() {
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity == null) {
            return;
        }
        if (AuthManager.getInstance().isAuthorized()) {
            if (likeButton.isUsable()) {
                photo.settingLike = true;
                MessageBus.getInstance().post(new PhotoEvent(photo));
                if (callback != null) {
                    callback.onLikeOrDislikePhoto(photo, getAdapterPosition(), !photo.liked_by_user);
                }
            }
        } else {
            IntentHelper.startLoginActivity(activity);
        }
    }

    @OnClick(R.id.item_following_photo_collectionButton) void collectPhoto() {
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null) {
            if (!AuthManager.getInstance().isAuthorized()) {
                IntentHelper.startLoginActivity(activity);
            } else {
                SelectCollectionDialog dialog = new SelectCollectionDialog();
                dialog.setPhotoAndListener(photo, new DispatchCollectionsChangedPresenter());
                dialog.show(activity.getSupportFragmentManager(), null);
            }
        }
    }

    @OnClick(R.id.item_following_photo_downloadButton) void downloadPhoto() {
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null) {
            if (DatabaseHelper.getInstance(activity)
                    .readDownloadingEntityCount(photo.id) > 0) {
                NotificationHelper.showSnackbar(activity.getString(R.string.feedback_download_repeat));
            } else if (FileUtils.isPhotoExists(activity, photo.id)) {
                DownloadRepeatDialog dialog = new DownloadRepeatDialog();
                dialog.setDownloadKey(photo);
                dialog.setOnCheckOrDownloadListener(new DownloadRepeatDialog.OnCheckOrDownloadListener() {

                    private final Photo p = photo;
                    private final FollowingAdapter.ItemEventCallback c = callback;

                    @Override
                    public void onCheck(Object obj) {
                        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
                        if (activity != null) {
                            IntentHelper.startCheckPhotoActivity(activity, p.id);
                        }
                    }

                    @Override
                    public void onDownload(Object obj) {
                        if (c != null) {
                            c.onDownload(p);
                        }
                    }
                });
                dialog.show(activity.getSupportFragmentManager(), null);
            } else if (callback != null) {
                callback.onDownload(photo);
            }
        }
    }
}
