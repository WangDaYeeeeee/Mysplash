package com.wangdaye.mysplash._common.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.FollowingResult;
import com.wangdaye.mysplash._common.data.entity.unsplash.LikePhotoResult;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash._common.utils.widget.ColorAnimRequestListener;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Following adapter.
 * */

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder>
        implements SelectCollectionDialog.OnCollectionsChangedListener {
    // widget
    private Context a;
    private RecyclerView recyclerView;

    // data
    private List<FollowingResult> resultList;
    private List<ViewType> typeList;
    private PhotoService photoService;

    private static final int VIEW_TYPE_TITLE = 0;
    private static final int VIEW_TYPE_PHOTO = 1;
    private static final int VIEW_TYPE_USER = 2;
    private static final int VIEW_TYPE_MORE = 3;

    private static final int MAX_DISPLAY_PHOTO_COUNT = 7;

    /** <br> life cycle. */

    public FollowingAdapter(Context a, List<FollowingResult> list) {
        this.a = a;
        this.resultList = list;
        this.typeList = new ArrayList<>();
        buildTypeList(list);
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        ViewType viewType = typeList.get(position);
        switch (viewType.type) {
            case VIEW_TYPE_TITLE: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_following_title, parent, false);
                return new ViewHolder(v, position);
            }
            case VIEW_TYPE_PHOTO: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_following_photo, parent, false);
                return new ViewHolder(v, position);
            }
            case VIEW_TYPE_USER: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_following_user, parent, false);
                return new ViewHolder(v, position);
            }
            default: { // VIEW_TYPE_MORE.
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_following_more, parent, false);
                return new ViewHolder(v, position);
            }
        }
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        switch (typeList.get(position).type) {
            case VIEW_TYPE_TITLE: {
                holder.actor.setText(getUser(position).name);
                if (getUser(position).profile_image != null) {
                    Glide.with(a)
                            .load(getUser(position).profile_image.large)
                            .override(128, 128)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.avatar);
                } else {
                    Glide.with(a)
                            .load(R.drawable.default_avatar)
                            .override(128, 128)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.avatar);
                }
                switch (resultList.get(typeList.get(position).resultPosition).verb) {
                    case FollowingResult.VERB_LIKED:
                        holder.verb.setVisibility(View.VISIBLE);
                        holder.verb.setText(
                                a.getString(R.string.liked)
                                        + " " + resultList.get(typeList.get(position).resultPosition).objects.size()
                                        + " " + a.getString(R.string.photos)
                                        + ".");
                        break;

                    case FollowingResult.VERB_COLLECTED:
                        holder.verb.setVisibility(View.VISIBLE);
                        holder.verb.setText(
                                Html.fromHtml(
                                        a.getString(R.string.collected)
                                                + " " + resultList.get(typeList.get(position).resultPosition).objects.size()
                                                + " " + a.getString(R.string.photos) + " " + a.getString(R.string.to)
                                                + " <u>" + resultList.get(typeList.get(position).resultPosition).targets.get(0).title + "</u>"
                                                + "."));
                        break;

                    case FollowingResult.VERB_FOLLOWED:
                        holder.verb.setVisibility(View.VISIBLE);
                        holder.verb.setText(
                                a.getString(R.string.followed)
                                        + " " + resultList.get(typeList.get(position).resultPosition).objects.size()
                                        + " " + a.getString(R.string.users)
                                        + ".");
                        break;

                    case FollowingResult.VERB_RELEASE:
                        holder.verb.setVisibility(View.VISIBLE);
                        holder.verb.setText(
                                a.getString(R.string.released)
                                        + " " + resultList.get(typeList.get(position).resultPosition).objects.size()
                                        + " " + a.getString(R.string.photos)
                                        + ".");
                        break;

                    case FollowingResult.VERB_PUBLISHED:
                        holder.verb.setVisibility(View.VISIBLE);
                        holder.verb.setText(
                                a.getString(R.string.published)
                                        + " " + resultList.get(typeList.get(position).resultPosition).objects.size()
                                        + " " + a.getString(R.string.photos)
                                        + ".");
                        break;

                    case FollowingResult.VERB_CURATED:
                        holder.verb.setVisibility(View.VISIBLE);
                        holder.verb.setText(
                                Html.fromHtml(
                                        a.getString(R.string.curated)
                                                + " " + resultList.get(typeList.get(position).resultPosition).objects.size()
                                                + " " + a.getString(R.string.photos)
                                                + (resultList.get(typeList.get(position).resultPosition).targets.size() > 0 ?
                                                " " + a.getString(R.string.to)
                                                        + " <u>" + resultList.get(typeList.get(position).resultPosition).targets.get(0).title + "</u>"
                                                :
                                                "")
                                                + "."));
                        break;

                    default:
                        holder.verb.setVisibility(View.GONE);
                        break;
                }
                break;
            }
            case VIEW_TYPE_PHOTO: {
                final Photo photo = getPhoto(position);
                assert photo != null;
                holder.title.setText("");
                holder.image.setShowShadow(false);
                Glide.with(a)
                        .load(photo.urls.regular)
                        .override(photo.getRegularWidth(), photo.getRegularHeight())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .listener(new ColorAnimRequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {
                                photo.loadPhotoSuccess = true;
                                if (!photo.hasFadedIn) {
                                    photo.hasFadedIn = true;
                                    updatePhoto(photo, position);
                                    startColorAnimation(a, holder.image);
                                }
                                holder.title.setText(photo.user.name);
                                holder.image.setShowShadow(true);
                                return false;
                            }
                        })
                        .into(holder.image);

                if (photo.current_user_collections.size() != 0) {
                    holder.collectionButton.setImageResource(R.drawable.ic_item_added);
                } else {
                    holder.collectionButton.setImageResource(R.drawable.ic_item_plus);
                }

                if (photo.settingLike) {
                    holder.likeButton.forceSetProgressState();
                } else {
                    holder.likeButton.forceSetResultState(photo.liked_by_user ?
                            R.drawable.ic_item_heart_red : R.drawable.ic_item_heart_outline);
                }

                holder.background.setBackgroundColor(DisplayUtils.calcCardBackgroundColor(photo.color));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.image.setTransitionName(photo.id + "-" + position + "-image");
                    holder.background.setTransitionName(photo.id + "-" + position + "-background");
                }
                break;
            }
            case VIEW_TYPE_USER:
                User user = getUser(position);
                holder.actor.setText(user.name);
                if (TextUtils.isEmpty(user.bio)) {
                    holder.verb.setText(
                            user.total_photos + " " + a.getResources().getStringArray(R.array.user_tabs)[0] + ", "
                                    + user.total_collections + " " + a.getResources().getStringArray(R.array.user_tabs)[1] + ", "
                                    + user.total_likes + " " + a.getResources().getStringArray(R.array.user_tabs)[2]);
                } else {
                    holder.verb.setText(user.bio);
                }

                if (user.profile_image != null) {
                    Glide.with(a)
                            .load(user.profile_image.large)
                            .override(128, 128)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.avatar);
                } else {
                    Glide.with(a)
                            .load(R.drawable.default_avatar)
                            .override(128, 128)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.avatar);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.avatar.setTransitionName(user.username + "-" + position + "-avatar");
                    holder.background.setTransitionName(user.username + "-" + position + "-background");
                }
                break;
            case VIEW_TYPE_MORE: {
                final Photo photo = getPhoto(position);
                assert photo != null;
                holder.more.setText(
                        (resultList.get(typeList.get(position).resultPosition).objects.size() - MAX_DISPLAY_PHOTO_COUNT)
                                + " " + a.getString(R.string.more));
                Glide.with(a)
                        .load(photo.urls.regular)
                        .override(photo.getRegularWidth(), photo.getRegularHeight())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .listener(new ColorAnimRequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {
                                photo.loadPhotoSuccess = true;
                                if (!photo.hasFadedIn) {
                                    photo.hasFadedIn = true;
                                    updatePhoto(photo, position);
                                    startColorAnimation(a, holder.image);
                                }
                                return false;
                            }
                        })
                        .into(holder.image);
                if (getUser(position).profile_image != null) {
                    Glide.with(a)
                            .load(getUser(position).profile_image.large)
                            .override(128, 128)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.avatar);
                } else {
                    Glide.with(a)
                            .load(R.drawable.default_avatar)
                            .override(128, 128)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.avatar);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.avatar.setTransitionName(getUser(position).username + "-" + position + "-avatar");
                    holder.background.setTransitionName(getUser(position).username + "-" + position + "-background");
                }
                break;
            }
        }
    }

    public void setActivity(MysplashActivity a) {
        this.a = a;
    }

    public void setRecyclerView(RecyclerView v) {
        this.recyclerView = v;
    }

    /** <br> data. */

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.image != null) {
            Glide.clear(holder.image);
        }
        if (holder.avatar != null) {
            Glide.clear(holder.avatar);
        }
    }

    public void insertItem(FollowingResult item) {
        resultList.add(item);
        addType(item, resultList.size() - 1);
        notifyItemInserted(typeList.size() - 1);
    }

    public void clearItem() {
        resultList.clear();
        typeList.clear();
        notifyDataSetChanged();
    }

    private void setLikeForAPhoto(boolean like, int position) {
        if (photoService == null) {
            photoService = PhotoService.getService();
        }
        Photo photo = getPhoto(position);
        if (photo != null) {
            photo.settingLike = true;
            resultList.get(typeList.get(position).resultPosition)
                    .objects.set(typeList.get(position).objectPosition, new FollowingResult.Object(photo));
            photoService.setLikeForAPhoto(
                    photo.id,
                    like,
                    new OnSetLikeListener(photo.id, position));
        }
    }

    public void cancelService() {
        if (photoService != null) {
            photoService.cancel();
        }
    }

    public int getRealItemCount() {
        return typeList.size();
    }

    public void updatePhoto(Photo p, boolean probablyRepeat) {
        int position = -1;
        for (int i = 0; i < resultList.size(); i ++) {
            position ++;
            int j = 0;
            if (resultList.get(i).verb.equals(FollowingResult.VERB_FOLLOWED)) {
                position += resultList.get(i).objects.size();
            } else {
                for (j = 0; j < resultList.get(i).objects.size(); j ++) {
                    if (j < MAX_DISPLAY_PHOTO_COUNT) {
                        position ++;
                    }
                    if (resultList.get(i).objects.get(j).id.equals(p.id)) {
                        resultList.get(i).objects.set(j, new FollowingResult.Object(p));
                        if (j < MAX_DISPLAY_PHOTO_COUNT) {
                            notifyItemChanged(position);
                        }
                        if (!probablyRepeat) {
                            return;
                        }
                    }
                }
            }
            if (j > MAX_DISPLAY_PHOTO_COUNT) {
                position ++;
            }
        }
    }

    private void buildTypeList(List<FollowingResult> resultList) {
        for (int i = 0; i < resultList.size(); i ++) {
            addType(resultList.get(i), i);
        }
    }

    private void addType(FollowingResult result, int i) {
        if (result.verb.equals(FollowingResult.VERB_FOLLOWED)) {
            typeList.add(new ViewType(i, -1, VIEW_TYPE_TITLE));
            for (int j = 0; j < result.objects.size(); j ++) {
                typeList.add(new ViewType(i, j, VIEW_TYPE_USER));
            }
        } else {
            for (int j = 0; j < result.objects.size(); j ++) {
                if (result.objects.get(j).width == 0 || result.objects.get(j).height == 0) {
                    result.objects.remove(j);
                    j --;
                }
            }
            if (result.objects.size() > 0) {
                typeList.add(new ViewType(i, -1, VIEW_TYPE_TITLE));
                for (int j = 0; j < MAX_DISPLAY_PHOTO_COUNT && j < result.objects.size(); j ++) {
                    typeList.add(new ViewType(i, j, VIEW_TYPE_PHOTO));
                }
                if (result.objects.size() > MAX_DISPLAY_PHOTO_COUNT) {
                    typeList.add(new ViewType(i, MAX_DISPLAY_PHOTO_COUNT, VIEW_TYPE_MORE));
                }
            }
        }
    }

    private User getUser(int position) {
        ViewType viewType = typeList.get(position);
        switch (viewType.type) {
            case VIEW_TYPE_TITLE:
            case VIEW_TYPE_PHOTO:
            case VIEW_TYPE_MORE:
                return resultList.get(viewType.resultPosition)
                        .actors.get(0);

            default: // VIEW_TYPE_USER.
                return resultList.get(viewType.resultPosition)
                        .objects.get(viewType.objectPosition)
                        .castToUser();
        }
    }

    @Nullable
    private Photo getPhoto(int position) {
        ViewType viewType = typeList.get(position);
        switch (viewType.type) {
            case VIEW_TYPE_PHOTO:
            case VIEW_TYPE_MORE:
                return resultList.get(viewType.resultPosition)
                        .objects.get(viewType.objectPosition)
                        .castToPhoto();

            default: // VIEW_TYPE_USER, VIEW_TYPE_TITLE.
                return null;
        }
    }

    private void updatePhoto(Photo photo, int position) {
        FollowingResult result = resultList.get(typeList.get(position).resultPosition);
        result.objects.set(typeList.get(position).objectPosition, new FollowingResult.Object(photo));
        resultList.set(typeList.get(position).resultPosition, result);
    }

    private Collection getCollection(int position) {
        return resultList.get(typeList.get(position).resultPosition)
                .targets.get(0);
    }

    public boolean isHeaderView(int position) {
        return typeList.get(position).type == VIEW_TYPE_TITLE;
    }

    public boolean isFooterView(int position) {
        return typeList.size() <= position + 1
                || typeList.get(position + 1).type == VIEW_TYPE_TITLE;
    }

    public User getActor(int position) {
        return resultList.get(typeList.get(position).resultPosition).actors.get(0);
    }

    /** <br> interface. */

    // on set like listener.

    private class OnSetLikeListener implements PhotoService.OnSetLikeListener {
        // data
        private String id;
        private int position;

        // life cycle.

        OnSetLikeListener(String id, int position) {
            this.id = id;
            this.position = position;
        }

        // interface.

        @Override
        public void onSetLikeSuccess(Call<LikePhotoResult> call, Response<LikePhotoResult> response) {
            if (Mysplash.getInstance() != null && Mysplash.getInstance().getTopActivity() != null) {
                if (typeList.size() < position) {
                    return;
                }
                Photo photo = getPhoto(position);
                if (photo != null && photo.id.equals(id)) {
                    photo.settingLike = false;

                    if (response.isSuccessful() && response.body() != null) {
                        photo.liked_by_user = response.body().photo.liked_by_user;
                        photo.likes = response.body().photo.likes;
                    } else {
                        NotificationUtils.showSnackbar(
                                photo.liked_by_user ?
                                        a.getString(R.string.feedback_unlike_failed) : a.getString(R.string.feedback_like_failed),
                                Snackbar.LENGTH_SHORT);
                    }

                    resultList.get(typeList.get(position).resultPosition)
                            .objects.set(typeList.get(position).objectPosition, new FollowingResult.Object(photo));

                    updateView(photo.liked_by_user);
                }
            }
        }

        @Override
        public void onSetLikeFailed(Call<LikePhotoResult> call, Throwable t) {
            if (Mysplash.getInstance() != null && Mysplash.getInstance().getTopActivity() != null) {
                if (typeList.size() < position) {
                    return;
                }
                Photo photo = getPhoto(position);
                if (photo != null && photo.id.equals(id)) {
                    photo.settingLike = false;

                    NotificationUtils.showSnackbar(
                            photo.liked_by_user ?
                                    a.getString(R.string.feedback_unlike_failed) : a.getString(R.string.feedback_like_failed),
                            Snackbar.LENGTH_SHORT);

                    resultList.get(typeList.get(position).resultPosition)
                            .objects.set(typeList.get(position).objectPosition, new FollowingResult.Object(photo));
                    updateView(photo.liked_by_user);
                }
            }
        }

        // UI.

        private void updateView(boolean to) {
            if (recyclerView != null) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstPosition = layoutManager.findFirstVisibleItemPosition();
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                if (firstPosition <= position && position <= lastPosition) {
                    ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                    holder.likeButton.setResultState(
                            to ? R.drawable.ic_item_heart_red : R.drawable.ic_item_heart_outline);
                }
            }
        }
    }

    // on collections changed listener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        updatePhoto(p, true);
    }

    /** <br> inner class. */

    // view type.

    private class ViewType {
        // data
        int resultPosition;
        int objectPosition;
        int type;

        ViewType(int resultPosition, int objectPosition, int type) {
            this.resultPosition = resultPosition;
            this.objectPosition = objectPosition;
            this.type = type;
        }
    }

    // view holder.

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        public RelativeLayout background;

        CircleImageView avatar;
        TextView actor;
        TextView verb;

        public FreedomImageView image;
        TextView title;
        ImageButton collectionButton;
        CircularProgressIcon likeButton;

        TextView more;

        // data
        private int position;

        ViewHolder(View itemView, int position) {
            super(itemView);
            this.position = position;
            switch (typeList.get(position).type) {
                case VIEW_TYPE_TITLE: {
                    this.background = (RelativeLayout) itemView.findViewById(R.id.item_following_title_background);

                    itemView.findViewById(R.id.item_following_title_avatarContainer).setOnClickListener(this);

                    this.avatar = (CircleImageView) itemView.findViewById(R.id.item_following_title_avatar);

                    this.actor = (TextView) itemView.findViewById(R.id.item_following_title_actor);
                    actor.setOnClickListener(this);

                    this.verb = (TextView) itemView.findViewById(R.id.item_following_title_verb);
                    DisplayUtils.setTypeface(itemView.getContext(), verb);
                    verb.setOnClickListener(this);
                    break;
                }
                case VIEW_TYPE_PHOTO: {
                    Photo photo = getPhoto(position);
                    assert photo != null;

                    this.background = (RelativeLayout) itemView.findViewById(R.id.item_following_photo_background);
                    background.setOnClickListener(this);

                    this.image = (FreedomImageView) itemView.findViewById(R.id.item_following_photo_image);
                    image.setSize(photo.width, photo.height);

                    this.title = (TextView) itemView.findViewById(R.id.item_following_photo_title);
                    DisplayUtils.setTypeface(itemView.getContext(), title);

                    this.collectionButton = (ImageButton) itemView.findViewById(R.id.item_following_photo_collectionButton);
                    collectionButton.setOnClickListener(this);

                    this.likeButton = (CircularProgressIcon) itemView.findViewById(R.id.item_following_photo_likeButton);
                    likeButton.setOnClickListener(this);

                    itemView.findViewById(R.id.item_following_photo_downloadButton).setOnClickListener(this);
                    break;
                }
                case VIEW_TYPE_USER: {
                    this.background = (RelativeLayout) itemView.findViewById(R.id.item_following_user_background);
                    background.setOnClickListener(this);

                    this.avatar = (CircleImageView) itemView.findViewById(R.id.item_following_user_avatar);
                    this.actor = (TextView) itemView.findViewById(R.id.item_following_user_title);

                    this.verb = (TextView) itemView.findViewById(R.id.item_following_user_subtitle);
                    DisplayUtils.setTypeface(a, verb);
                    break;
                }
                case VIEW_TYPE_MORE: {
                    Photo photo = getPhoto(position);
                    assert photo != null;

                    this.background = (RelativeLayout) itemView.findViewById(R.id.item_following_more_background);
                    background.setOnClickListener(this);

                    this.image = (FreedomImageView) itemView.findViewById(R.id.item_following_more_image);
                    image.setSize(photo.width, photo.height);

                    this.more = (TextView) itemView.findViewById(R.id.item_following_more_title);
                    this.avatar = (CircleImageView) itemView.findViewById(R.id.item_following_more_avatar);
                    break;
                }
            }
        }

        @Override
        public void onClick(View view) {
            switch (typeList.get(position).type) {
                case VIEW_TYPE_TITLE:
                    switch (view.getId()) {
                        case R.id.item_following_title_avatarContainer:
                        case R.id.item_following_title_actor: {
                            IntentHelper.startUserActivity(
                                    (MysplashActivity) a,
                                    avatar,
                                    getUser(position),
                                    UserActivity.PAGE_PHOTO);
                            break;
                        }
                        case R.id.item_following_title_verb: {
                            if (resultList.get(typeList.get(position).resultPosition).verb.equals(FollowingResult.VERB_COLLECTED)
                                    || resultList.get(typeList.get(position).resultPosition).verb.equals(FollowingResult.VERB_CURATED)) {
                                IntentHelper.startCollectionActivity(
                                        (MysplashActivity) a,
                                        avatar,
                                        background,
                                        getCollection(position));
                            } else if (resultList.get(typeList.get(position).resultPosition).verb.equals(FollowingResult.VERB_RELEASE)
                                    || resultList.get(typeList.get(position).resultPosition).verb.equals(FollowingResult.VERB_PUBLISHED)) {
                                IntentHelper.startUserActivity(
                                        (MysplashActivity) a,
                                        avatar,
                                        getUser(position),
                                        UserActivity.PAGE_PHOTO);
                            } else if (resultList.get(typeList.get(position).resultPosition).verb.equals(FollowingResult.VERB_LIKED)) {
                                IntentHelper.startUserActivity(
                                        (MysplashActivity) a,
                                        avatar,
                                        getUser(position),
                                        UserActivity.PAGE_LIKE);
                            }
                            break;
                        }
                    }
                    break;

                case VIEW_TYPE_PHOTO:
                    switch (view.getId()) {
                        case R.id.item_following_photo_background:
                            IntentHelper.startPhotoActivity(
                                    (MysplashActivity) a,
                                    image,
                                    background,
                                    getPhoto(position));
                            break;

                        case R.id.item_following_photo_likeButton:
                            if (AuthManager.getInstance().isAuthorized()) {
                                Photo photo = getPhoto(position);
                                if (likeButton.isUsable() && photo != null) {
                                    likeButton.setProgressState();
                                    setLikeForAPhoto(!photo.liked_by_user, position);
                                }
                            } else {
                                IntentHelper.startLoginActivity((MysplashActivity) a);
                            }
                            break;

                        case R.id.item_following_photo_collectionButton:
                            if (!AuthManager.getInstance().isAuthorized()) {
                                IntentHelper.startLoginActivity((MysplashActivity) a);
                            } else {
                                SelectCollectionDialog dialog = new SelectCollectionDialog();
                                dialog.setPhotoAndListener(getPhoto(position), FollowingAdapter.this);
                                dialog.show(((Activity) a).getFragmentManager(), null);
                            }
                            break;

                        case R.id.item_following_photo_downloadButton:
                            Photo p = getPhoto(position);
                            assert p != null;
                            if (DatabaseHelper.getInstance(a).readDownloadingEntityCount(p.id) == 0) {
                                DownloadHelper.getInstance(a).addMission(a, p, DownloadHelper.DOWNLOAD_TYPE);
                            } else {
                                NotificationUtils.showSnackbar(
                                        a.getString(R.string.feedback_download_repeat),
                                        Snackbar.LENGTH_SHORT);
                            }
                            break;
                    }
                    break;

                case VIEW_TYPE_USER:
                    switch (view.getId()) {
                        case R.id.item_following_user_background:
                            IntentHelper.startUserActivity(
                                    (MysplashActivity) a,
                                    avatar,
                                    getUser(position),
                                    UserActivity.PAGE_PHOTO);
                            break;
                    }
                    break;

                case VIEW_TYPE_MORE:
                    switch (view.getId()) {
                        case R.id.item_following_more_background:
                            if (resultList.get(typeList.get(position).resultPosition).verb.equals(FollowingResult.VERB_COLLECTED)
                                    || resultList.get(typeList.get(position).resultPosition).verb.equals(FollowingResult.VERB_CURATED)) {
                                IntentHelper.startCollectionActivity(
                                        (MysplashActivity) a,
                                        avatar,
                                        background,
                                        getCollection(position));
                            } else if (resultList.get(typeList.get(position).resultPosition).verb.equals(FollowingResult.VERB_RELEASE)
                                    || resultList.get(typeList.get(position).resultPosition).verb.equals(FollowingResult.VERB_PUBLISHED)) {
                                IntentHelper.startUserActivity(
                                        (MysplashActivity) a,
                                        avatar,
                                        getUser(position),
                                        UserActivity.PAGE_PHOTO);
                            } else if (resultList.get(typeList.get(position).resultPosition).verb.equals(FollowingResult.VERB_LIKED)) {
                                IntentHelper.startUserActivity(
                                        (MysplashActivity) a,
                                        avatar,
                                        getUser(position),
                                        UserActivity.PAGE_LIKE);
                            }
                            break;
                    }
                    break;
            }
        }
    }
}
