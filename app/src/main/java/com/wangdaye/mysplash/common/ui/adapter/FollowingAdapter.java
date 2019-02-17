package com.wangdaye.mysplash.common.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.wangdaye.mysplash.common.basic.FooterAdapter;
import com.wangdaye.mysplash.common.basic.activity.LoadableActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.LikePhotoResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.data.service.downloader.DownloaderService;
import com.wangdaye.mysplash.common.data.service.network.PhotoService;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Following adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Photo}.
 *
 * */

public class FollowingAdapter extends FooterAdapter<RecyclerView.ViewHolder>
        implements PhotoHolder.OnClickPhotoItemListener,
        SelectCollectionDialog.OnCollectionsChangedListener {

    private Context a;
    private RecyclerView recyclerView;

    private List<Photo> photoList; // this list is used to save the feed data.
    private List<ViewType> typeList; // this list is used to save the display information of view holder.

    private PhotoService photoService;

    private int columnCount;

    /**
     * This class is used to save the view holder's information.
     * */
    private class ViewType {
        // data
        int photoPosition;
        int adapterPosition;

        @ViewTypeRule
        int type;

        ViewType(int photoPosition, int adapterPosition, int type) {
            this.photoPosition = photoPosition;
            this.adapterPosition = adapterPosition;
            this.type = type;
        }
    }

    @IntDef({
            TitleHolder.VIEW_TYPE_TITLE,
            PhotoHolder.VIEW_TYPE_PHOTO})
    @interface ViewTypeRule {}

    public FollowingAdapter(Context a, List<Photo> list) {
        this(a, list, DisplayUtils.getGirdColumnCount(a));
    }

    private FollowingAdapter(Context a, List<Photo> list, int columnCount) {
        this.a = a;
        this.photoList = list;
        this.typeList = new ArrayList<>();
        this.columnCount = columnCount;
        addPhotos(list);
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
                case TitleHolder.VIEW_TYPE_TITLE: {
                    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_following_title, parent, false);
                    return new TitleHolder(v, columnCount);
                }
                case PhotoHolder.VIEW_TYPE_PHOTO:
                default: {
                    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_following_photo, parent, false);
                    return new PhotoHolder(v, this);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // adapter adapterPosition (type adapterPosition).
        if (!isFooter(position)) {
            if (holder instanceof TitleHolder) {
                ((TitleHolder) holder).onBindView(getUser(position));
            } else if (holder instanceof PhotoHolder) {
                ((PhotoHolder) holder).onBindView(a,  getPhoto(position), position, columnCount);
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof TitleHolder) {
            ((TitleHolder) holder).onRecycled();
        } else if (holder instanceof PhotoHolder) {
            ((PhotoHolder) holder).onRecycled();
        }
    }

    @Override
    public int getItemCount() {
        return typeList.size() + (hasFooter() ? 1 : 0);
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
    protected boolean hasFooter() {
        return !DisplayUtils.isLandscape(a)
                && DisplayUtils.getNavigationBarHeight(a.getResources()) != 0;
    }

    // control.

    public void setActivity(MysplashActivity a) {
        this.a = a;
    }

    public void setRecyclerView(RecyclerView v) {
        this.recyclerView = v;
    }

    public void insertItems(List<Photo> list) {
        int typeNum = typeList.size();
        addPhotos(list);
        notifyItemRangeInserted(typeNum, typeList.size() - typeNum - 1);
    }

    public void clearItem() {
        photoList.clear();
        typeList.clear();
        notifyDataSetChanged();
    }

    void setLikeForAPhoto(boolean like, int adapterPosition) {
        if (photoService == null) {
            photoService = PhotoService.getService();
        }
        Photo photo = getPhoto(adapterPosition);
        if (photo != null) {
            photo.settingLike = true;
            photoList.set(typeList.get(adapterPosition).photoPosition, photo);
            photoService.setLikeForAPhoto(
                    photo.id,
                    like,
                    new OnSetLikeListener(photo.id, adapterPosition));
        }
    }

    public boolean isFooterView(int adapterPosition) {
        return typeList.size() <= adapterPosition + 1
                || typeList.get(adapterPosition + 1).type == TitleHolder.VIEW_TYPE_TITLE;
    }

    public void setTitleAvatarVisibility(RecyclerView recyclerView, int lastPosition, int newPosition) {
        RecyclerView.ViewHolder lastHolder = recyclerView.findViewHolderForAdapterPosition(lastPosition);
        if (lastHolder instanceof TitleHolder) {
            ((TitleHolder) lastHolder).setAvatarVisibility(true);
        }

        RecyclerView.ViewHolder newHolder = recyclerView.findViewHolderForAdapterPosition(newPosition);
        if (newHolder instanceof TitleHolder) {
            ((TitleHolder) newHolder).setAvatarVisibility(false);
        }
    }

    // type list.

    private void addPhotos(List<Photo> list) {
        int photoListFromIndex = photoList.size();
        photoList.addAll(list);

        for (int i = photoListFromIndex; i < photoList.size(); i ++) {
            if (typeList.size() == 0) {
                typeList.add(new ViewType(i, typeList.size(), TitleHolder.VIEW_TYPE_TITLE));
                typeList.add(new ViewType(i, typeList.size(), PhotoHolder.VIEW_TYPE_PHOTO));
            } else {
                int lastTypeIndex = typeList.size() - 1;
                Photo photo = getPhoto(lastTypeIndex);
                if (photo == null || !photo.user.username.equals(photoList.get(i).user.username)) {
                    typeList.add(new ViewType(i, typeList.size(), TitleHolder.VIEW_TYPE_TITLE));
                    typeList.add(new ViewType(i, typeList.size(), PhotoHolder.VIEW_TYPE_PHOTO));
                } else {
                    typeList.add(new ViewType(i, typeList.size(), PhotoHolder.VIEW_TYPE_PHOTO));
                }
            }
        }
    }

    // user.

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

    // photo.

    @Nullable
    Photo getPhoto(int adapterPosition) {
        if (adapterPosition >= typeList.size()) {
            return null;
        }
        int photoPosition = typeList.get(adapterPosition).photoPosition;
        if (photoPosition >= photoList.size()) {
            return null;
        }
        return photoList.get(photoPosition);
    }

    public void updatePhoto(RecyclerView recyclerView, Photo p, boolean refreshView, boolean probablyRepeat) {
        for (int i = 0; i < typeList.size(); i ++) {
            if (typeList.get(i).type == PhotoHolder.VIEW_TYPE_PHOTO) {
                Photo photo = getPhoto(i);
                if (photo != null && photo.id.equals(p.id)) {
                    photoList.set(typeList.get(i).photoPosition, p);
                    if (refreshView) {
                        updatePhotoItemView(recyclerView, p, i);
                    }
                    if (!probablyRepeat) {
                        return;
                    }
                }
            }
        }
    }

    private void updatePhotoItemView(@Nullable RecyclerView recyclerView, Photo photo, int adapterPosition) {
        if (recyclerView == null) {
            notifyItemChanged(adapterPosition);
        } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition();
            int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();
            if (firstVisiblePosition <= adapterPosition && adapterPosition <= lastVisiblePosition) {
                // is a visible item.
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(adapterPosition);
                if (holder instanceof PhotoHolder) {
                    ((PhotoHolder) holder).update(photo);
                }
            } else {
                notifyItemChanged(adapterPosition);
            }
        }
    }

    void updatePhoto(Photo photo, int adapterPosition) {
        photoList.set(typeList.get(adapterPosition).photoPosition, photo);
    }

    // feeds.

    public List<Photo> getPhotoList() {
        return new ArrayList<>(photoList);
    }

    public void setPhotoList(List<Photo> list) {
        photoList = new ArrayList<>();
        typeList = new ArrayList<>();
        addPhotos(list);
        notifyDataSetChanged();
    }

    /** <br> interface. */

    // on set like listener.

    private class OnSetLikeListener implements PhotoService.OnSetLikeListener {

        private String id;
        private int adapterPosition;

        OnSetLikeListener(String id, int adapterPosition) {
            this.id = id;
            this.adapterPosition = adapterPosition;
        }

        @Override
        public void onSetLikeSuccess(Call<LikePhotoResult> call, Response<LikePhotoResult> response) {
            if (Mysplash.getInstance() != null && Mysplash.getInstance().getTopActivity() != null) {
                if (typeList.size() < adapterPosition) {
                    return;
                }
                Photo photo = getPhoto(adapterPosition);
                if (photo != null && photo.id.equals(id)) {
                    photo.settingLike = false;

                    if (response.isSuccessful() && response.body() != null) {
                        photo.liked_by_user = response.body().photo.liked_by_user;
                        photo.likes = response.body().photo.likes;
                    } else {
                        NotificationHelper.showSnackbar(
                                photo.liked_by_user ?
                                        a.getString(R.string.feedback_unlike_failed) : a.getString(R.string.feedback_like_failed));
                    }

                    photoList.set(typeList.get(adapterPosition).photoPosition, photo);

                    updateView(photo.liked_by_user);
                }
            }
        }

        @Override
        public void onSetLikeFailed(Call<LikePhotoResult> call, Throwable t) {
            if (Mysplash.getInstance() != null && Mysplash.getInstance().getTopActivity() != null) {
                if (typeList.size() < adapterPosition) {
                    return;
                }
                Photo photo = getPhoto(adapterPosition);
                if (photo != null && photo.id.equals(id)) {
                    photo.settingLike = false;

                    NotificationHelper.showSnackbar(
                            photo.liked_by_user ?
                                    a.getString(R.string.feedback_unlike_failed) : a.getString(R.string.feedback_like_failed));

                    photoList.set(typeList.get(adapterPosition).photoPosition, photo);
                    updateView(photo.liked_by_user);
                }
            }
        }

        private void updateView(boolean to) {
            if (recyclerView != null) {
                StaggeredGridLayoutManager layoutManager
                        = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {int[] firstPositions = layoutManager.findFirstVisibleItemPositions(null);
                    int[] lastPositions = layoutManager.findLastVisibleItemPositions(null);
                    if (firstPositions[0] <= adapterPosition
                            && adapterPosition <= lastPositions[lastPositions.length - 1]) {
                        PhotoHolder holder = (PhotoHolder) recyclerView.findViewHolderForAdapterPosition(adapterPosition);
                        if (holder != null) {
                            holder.likeButton.setResultState(
                                    to ? R.drawable.ic_item_heart_red : R.drawable.ic_item_heart_outline);
                        }
                    }
                }
            }
        }
    }

    // on click photo item listener.

    @Override
    public void onClick(View image, View background, int adapterPosition) {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        if (a != null) {
            ViewType viewType = typeList.get(adapterPosition);
            ArrayList<Photo> list = new ArrayList<>();
            int headIndex = viewType.photoPosition - 2;
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

            Bundle bundle = new Bundle();
            if (a instanceof LoadableActivity) {
                bundle = ((LoadableActivity) a).getBundleOfList();
            }

            IntentHelper.startPhotoActivity(
                    a, image, background,
                    list, viewType.photoPosition, headIndex,
                    bundle);
        }
    }

    // on collections changed listener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        updatePhoto(recyclerView, p, true, true);
    }
}

/**
 * Title holder.
 *
 * ViewHolder class for {@link FollowingAdapter} to show the title part of following feed data.
 *
 * */
class TitleHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_following_title_background)
    RelativeLayout background;

    @BindView(R.id.item_following_title_avatar)
    CircleImageView avatar;

    @BindView(R.id.item_following_title_actor)
    TextView actor;

    @BindView(R.id.item_following_title_verb)
    TextView verb;

    @Nullable
    private User user;
    private boolean avatarVisibility;
    static final int VIEW_TYPE_TITLE = 0;

    TitleHolder(View itemView, int columnCount) {
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
            ImageHelper.loadAvatar(avatar.getContext(), avatar, user, getAdapterPosition(), null);
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

    // interface.

    @OnClick({
            R.id.item_following_title_avatar,
            R.id.item_following_title_actor}) void checkActor() {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        if (user != null && a != null) {
            IntentHelper.startUserActivity(
                    a,
                    avatar,
                    background,
                    user,
                    UserActivity.PAGE_PHOTO);
        }
    }

    @OnClick(R.id.item_following_title_verb) void clickVerb() {
        if (user != null && !TextUtils.isEmpty(user.location)) {
            MysplashActivity a = Mysplash.getInstance().getTopActivity();
            if (a != null) {
                IntentHelper.startSearchActivity(a, user.location);
            }
        }
    }
}

/**
 * Photo holder.
 *
 * ViewHolder class for {@link FollowingAdapter} to show photo data.
 *
 * */
class PhotoHolder extends RecyclerView.ViewHolder
        implements ImageHelper.OnLoadImageListener<Photo>,
        DownloadRepeatDialog.OnCheckOrDownloadListener {

    @BindView(R.id.item_following_photo_card)
    CardView card;

    @BindView(R.id.item_following_photo_image)
    FreedomImageView image;

    @BindView(R.id.item_following_photo_avatar)
    CircleImageView avatar;

    @BindView(R.id.item_following_photo_title)
    TextView title;

    @BindView(R.id.item_following_photo_collectionButton)
    AppCompatImageButton collectionButton;

    @BindView(R.id.item_following_photo_likeButton)
    CircularProgressIcon likeButton;

    private FollowingAdapter adapter;

    @Nullable
    private Photo photo;
    private int position;
    static final int VIEW_TYPE_PHOTO = 1;

    private OnClickPhotoItemListener listener;

    PhotoHolder(View itemView, FollowingAdapter adapter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.adapter = adapter;
        this.listener = adapter;
    }

    void onBindView(Context a, @Nullable Photo photo, int position, int columnCount) {
        this.photo = photo;
        this.position = position;

        if (photo == null) {
            return;
        }

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
        int margin = a.getResources().getDimensionPixelSize(R.dimen.normal_margin);
        if (columnCount > 1) {
            params.setMarginStart(0);
            params.setMarginEnd(margin);
            params.setMargins(0, 0, margin, margin);
            card.setLayoutParams(params);
        } else {
            params.setMargins(
                    a.getResources().getDimensionPixelSize(R.dimen.large_icon_size), 0, margin, margin);
            card.setLayoutParams(params);
        }

        image.setSize(photo.width, photo.height);

        ImageHelper.loadAvatar(avatar.getContext(), avatar, photo.user, getAdapterPosition(), null);

        title.setText("");
        image.setShowShadow(false);

        ImageHelper.loadRegularPhoto(image.getContext(), image, photo, position, this);

        if (photo.current_user_collections.size() != 0) {
            collectionButton.setImageResource(R.drawable.ic_item_collected);
        } else {
            collectionButton.setImageResource(R.drawable.ic_item_collect);
        }

        likeButton.setProgressColor(Color.WHITE);
        if (photo.settingLike) {
            likeButton.forceSetProgressState();
        } else {
            likeButton.forceSetResultState(photo.liked_by_user ?
                    R.drawable.ic_item_heart_red : R.drawable.ic_item_heart_outline);
        }

        card.setCardBackgroundColor(
                ImageHelper.computeCardBackgroundColor(card.getContext(), photo.color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.setTransitionName(photo.id + "-" + position + "-cover");
            card.setTransitionName(photo.id + "-" + position + "-background");
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
    }

    void update(Photo photo) {
        this.photo = photo;

        if (photo.current_user_collections.size() != 0) {
            collectionButton.setImageResource(R.drawable.ic_item_collected);
        } else {
            collectionButton.setImageResource(R.drawable.ic_item_collect);
        }

        if (photo.settingLike) {
            likeButton.setProgressState();
        } else if (likeButton.getState() == CircularProgressIcon.STATE_PROGRESS) {
            likeButton.setResultState(photo.liked_by_user ?
                    R.drawable.ic_item_heart_red : R.drawable.ic_item_heart_outline);
        } else {
            likeButton.forceSetResultState(photo.liked_by_user ?
                    R.drawable.ic_item_heart_red : R.drawable.ic_item_heart_outline);
        }
    }

    // interface.

    interface OnClickPhotoItemListener {
        void onClick(View image, View background, int position);
    }

    @OnClick(R.id.item_following_photo_card) void clickItem() {
        if (listener != null) {
            listener.onClick(image, card, position);
        }
    }

    @OnClick(R.id.item_following_photo_avatar) void checkAuthor() {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        if (photo != null && a != null) {
            IntentHelper.startUserActivity(
                    a,
                    avatar,
                    card,
                    photo.user,
                    UserActivity.PAGE_PHOTO);
        }
    }

    @OnClick(R.id.item_following_photo_likeButton) void likePhoto() {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        if (a != null) {
            if (AuthManager.getInstance().isAuthorized()) {
                if (likeButton.isUsable() && photo != null) {
                    likeButton.setProgressState();
                    adapter.setLikeForAPhoto(!photo.liked_by_user, position);
                }
            } else {
                IntentHelper.startLoginActivity(a);
            }
        }
    }

    @OnClick(R.id.item_following_photo_collectionButton) void collectPhoto() {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        if (a != null) {
            if (!AuthManager.getInstance().isAuthorized()) {
                IntentHelper.startLoginActivity(a);
            } else {
                SelectCollectionDialog dialog = new SelectCollectionDialog();
                dialog.setPhotoAndListener(photo, adapter);
                dialog.show(a.getSupportFragmentManager(), null);
            }
        }
    }

    @OnClick(R.id.item_following_photo_downloadButton) void downloadPhoto() {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        if (photo != null && a != null) {
            if (DatabaseHelper.getInstance(a).readDownloadingEntityCount(photo.id) > 0) {
                NotificationHelper.showSnackbar(a.getString(R.string.feedback_download_repeat));
            } else if (FileUtils.isPhotoExists(a, photo.id)) {
                MysplashActivity activity = Mysplash.getInstance().getTopActivity();
                if (activity != null) {
                    DownloadRepeatDialog dialog = new DownloadRepeatDialog();
                    dialog.setDownloadKey(photo);
                    dialog.setOnCheckOrDownloadListener(this);
                    dialog.show(activity.getSupportFragmentManager(), null);
                }
            } else {
                DownloadHelper.getInstance(a).addMission(a, photo, DownloaderService.DOWNLOAD_TYPE);
            }
        }
    }

    // on load image listener.

    @Override
    public void onLoadImageSucceed(Photo newT, int index) {
        if (photo != null && photo.updateLoadInformation(newT)) {
            Photo p = adapter.getPhoto(index);
            if (p != null) {
                p.updateLoadInformation(newT);
                adapter.updatePhoto(p, index);
            }
        }
        title.setText(newT.user.name);
        image.setShowShadow(true);
    }

    @Override
    public void onLoadImageFailed(Photo originalT, int index) {
        // do nothing.
    }

    // on check or download listener. (download repeat dialog)

    @Override
    public void onCheck(Object obj) {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        if (a != null) {
            IntentHelper.startCheckPhotoActivity(a, ((Photo) obj).id);
        }
    }

    @Override
    public void onDownload(Object obj) {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        if (a != null) {
            DownloadHelper.getInstance(a).addMission(a, photo, DownloaderService.DOWNLOAD_TYPE);
        }
    }
}
