package com.wangdaye.mysplash._common.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import com.wangdaye.mysplash._common.ui.widget.LikeImageButton;
import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.ColorUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;

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

    // data
    private List<FollowingResult> resultList;
    private List<ViewType> typeList;
    private PhotoService photoService;

    private static final int VIEW_TYPE_TITLE = 0;
    private static final int VIEW_TYPE_CONTENT_1 = 1;
    private static final int VIEW_TYPE_CONTENT_2 = 2;
    private static final int VIEW_TYPE_CONTENT_3 = 3;
    private static final int VIEW_TYPE_MORE = 4;

    private static final String VERB_RELEASED = "released";
    private static final String VERB_LIKED = "liked";
    private static final String VERB_COLLECTED = "collected";

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
            case VIEW_TYPE_CONTENT_1:
            case VIEW_TYPE_CONTENT_2:
            case VIEW_TYPE_CONTENT_3: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_following_content, parent, false);
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
                holder.actor.setText(getUser(position).first_name + " " + getUser(position).last_name);
                if (getUser(position).profile_image != null) {
                    Glide.with(a)
                            .load(getUser(position).profile_image.large)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model,
                                                               Target<GlideDrawable> target,
                                                               boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }
                            })
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
                    case VERB_RELEASED:
                        holder.verb.setVisibility(View.VISIBLE);
                        holder.verb.setText(
                                a.getString(R.string.released)
                                        + " " + resultList.get(typeList.get(position).resultPosition).objects.size()
                                        + " " + a.getString(R.string.photos));
                        break;

                    case VERB_LIKED:
                        holder.verb.setVisibility(View.VISIBLE);
                        holder.verb.setText(
                                a.getString(R.string.liked)
                                        + " " + resultList.get(typeList.get(position).resultPosition).objects.size()
                                        + " " + a.getString(R.string.photos));
                        break;

                    case VERB_COLLECTED:
                        holder.verb.setVisibility(View.VISIBLE);
                        holder.verb.setText(
                                a.getString(R.string.added)
                                        + " " + resultList.get(typeList.get(position).resultPosition).objects.size()
                                        + " " + a.getString(R.string.photos)
                                        + " to " + resultList.get(typeList.get(position).resultPosition).targets.get(0).title);
                        break;

                    default:
                        holder.verb.setVisibility(View.GONE);
                        break;
                }
                break;
            }
            case VIEW_TYPE_CONTENT_1:
            case VIEW_TYPE_CONTENT_2:
            case VIEW_TYPE_CONTENT_3: {
                final Photo photo = getPhoto(position);
                holder.title.setText("");
                holder.image.setShowShadow(false);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(a)
                            .load(photo.urls.regular)
                            .override(photo.getRegularWidth(), photo.getRegularHeight())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model,
                                                               Target<GlideDrawable> target,
                                                               boolean isFromMemoryCache, boolean isFirstResource) {
                                    photo.loadPhotoSuccess = true;
                                    holder.title.setText(photo.user.name);
                                    holder.image.setShowShadow(true);
                                    return false;
                                }

                                @Override
                                public boolean onException(Exception e, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(holder.image);
                } else {
                    Glide.with(a)
                            .load(photo.urls.regular)
                            .override(photo.getRegularWidth(), photo.getRegularHeight())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model,
                                                               Target<GlideDrawable> target,
                                                               boolean isFromMemoryCache, boolean isFirstResource) {
                                    photo.loadPhotoSuccess = true;
                                    if (!photo.hasFadedIn) {
                                        holder.image.setHasTransientState(true);
                                        final AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
                                        final ObjectAnimator saturation = ObjectAnimator.ofFloat(
                                                matrix, AnimUtils.ObservableColorMatrix.SATURATION, 0f, 1f);
                                        saturation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener
                                                () {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                // just animating the color matrix does not invalidate the
                                                // drawable so need this update listener.  Also have to create a
                                                // new CMCF as the matrix is immutable :(
                                                holder.image.setColorFilter(new ColorMatrixColorFilter(matrix));
                                            }
                                        });
                                        saturation.setDuration(2000L);
                                        saturation.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(a));
                                        saturation.addListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                holder.image.clearColorFilter();
                                                holder.image.setHasTransientState(false);
                                            }
                                        });
                                        saturation.start();
                                        photo.hasFadedIn = true;
                                    }
                                    holder.title.setText(photo.user.name);
                                    holder.image.setShowShadow(true);
                                    return false;
                                }

                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(holder.image);

                    holder.image.setTransitionName(photo.id + "-image");
                    holder.background.setTransitionName(photo.id + "-background");
                }

                if (photo.current_user_collections.size() != 0) {
                    holder.collectionButton.setImageResource(R.drawable.ic_item_added);
                } else {
                    holder.collectionButton.setImageResource(R.drawable.ic_item_plus);
                }

                holder.likeButton.initLikeState(photo.liked_by_user);

                holder.background.setBackgroundColor(ColorUtils.calcCardBackgroundColor(photo.color));
                break;
            }
            case VIEW_TYPE_MORE: {
                final Photo photo = getPhoto(position);
                holder.more.setText(
                        (resultList.get(typeList.get(position).resultPosition).objects.size() - 3)
                                + " " + a.getString(R.string.more));
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(a)
                            .load(photo.urls.regular)
                            .override(photo.getRegularWidth(), photo.getRegularHeight())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model,
                                                               Target<GlideDrawable> target,
                                                               boolean isFromMemoryCache, boolean isFirstResource) {
                                    photo.loadPhotoSuccess = true;
                                    holder.title.setText(photo.user.name);
                                    holder.image.setShowShadow(true);
                                    return false;
                                }

                                @Override
                                public boolean onException(Exception e, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(holder.image);
                } else {
                    Glide.with(a)
                            .load(photo.urls.regular)
                            .override(photo.getRegularWidth(), photo.getRegularHeight())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model,
                                                               Target<GlideDrawable> target,
                                                               boolean isFromMemoryCache, boolean isFirstResource) {
                                    photo.loadPhotoSuccess = true;
                                    if (!photo.hasFadedIn) {
                                        holder.image.setHasTransientState(true);
                                        final AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
                                        final ObjectAnimator saturation = ObjectAnimator.ofFloat(
                                                matrix, AnimUtils.ObservableColorMatrix.SATURATION, 0f, 1f);
                                        saturation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener
                                                () {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                // just animating the color matrix does not invalidate the
                                                // drawable so need this update listener.  Also have to create a
                                                // new CMCF as the matrix is immutable :(
                                                holder.image.setColorFilter(new ColorMatrixColorFilter(matrix));
                                            }
                                        });
                                        saturation.setDuration(2000L);
                                        saturation.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(a));
                                        saturation.addListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                holder.image.clearColorFilter();
                                                holder.image.setHasTransientState(false);
                                            }
                                        });
                                        saturation.start();
                                        photo.hasFadedIn = true;
                                    }
                                    return false;
                                }

                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(holder.image);
                }
                break;
            }
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Glide.clear(holder.image);
    }

    public void setActivity(MysplashActivity a) {
        this.a = a;
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
        photoService.setLikeForAPhoto(
                getPhoto(position).id,
                like,
                new OnSetLikeListener(getPhoto(position).id, like, position));
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
        int position = 0;
        for (int i = 0; i < resultList.size(); i ++) {
            position ++;
            int j;
            for (j = 0; j < resultList.get(i).objects.size(); j ++) {
                if (j < 3) {
                    position ++;
                }
                if (resultList.get(i).objects.get(j).id.equals(p.id)) {
                    resultList.get(i).objects.set(j, p);
                    if (j < 3) {
                        notifyItemChanged(position);
                    }
                    if (!probablyRepeat) {
                        break;
                    }
                }
            }
            if (j > 3) {
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
        if (result.objects.size() > 3) {
            typeList.add(new ViewType(i, -1, VIEW_TYPE_TITLE));
            typeList.add(new ViewType(i, 0, VIEW_TYPE_CONTENT_1));
            typeList.add(new ViewType(i, 1, VIEW_TYPE_CONTENT_2));
            typeList.add(new ViewType(i, 2, VIEW_TYPE_CONTENT_3));
            typeList.add(new ViewType(i, 3, VIEW_TYPE_MORE));
        } else if (result.objects.size() == 3) {
            typeList.add(new ViewType(i, -1, VIEW_TYPE_TITLE));
            typeList.add(new ViewType(i, 0, VIEW_TYPE_CONTENT_1));
            typeList.add(new ViewType(i, 1, VIEW_TYPE_CONTENT_2));
            typeList.add(new ViewType(i, 2, VIEW_TYPE_CONTENT_3));
        } else if (result.objects.size() == 2) {
            typeList.add(new ViewType(i, -1, VIEW_TYPE_TITLE));
            typeList.add(new ViewType(i, 0, VIEW_TYPE_CONTENT_1));
            typeList.add(new ViewType(i, 1, VIEW_TYPE_CONTENT_2));
        } else if (result.objects.size() == 1) {
            typeList.add(new ViewType(i, -1, VIEW_TYPE_TITLE));
            typeList.add(new ViewType(i, 0, VIEW_TYPE_CONTENT_1));
        } else if (result.objects.size() == 0) {
            typeList.add(new ViewType(i, -1, VIEW_TYPE_TITLE));
        }
    }

    private User getUser(int position) {
        return resultList.get(typeList.get(position).resultPosition)
                .actors.get(0);
    }

    private Photo getPhoto(int position) {
        return resultList.get(typeList.get(position).resultPosition)
                .objects.get(typeList.get(position).objectPosition);

    }

    private Collection getCollection(int position) {
        return resultList.get(typeList.get(position).resultPosition)
                .targets.get(0);
    }

    /** <br> interface. */

    // on set like listener.

    private class OnSetLikeListener implements PhotoService.OnSetLikeListener {
        // data
        private String id;
        private boolean like;
        private int position;

        OnSetLikeListener(String id, boolean like, int position) {
            this.id = id;
            this.like = like;
            this.position = position;
        }

        @Override
        public void onSetLikeSuccess(Call<LikePhotoResult> call, Response<LikePhotoResult> response) {
            if (response.isSuccessful() && response.body() != null) {
                if (typeList.size() >= position
                        && getPhoto(position).id.equals(response.body().photo.id)) {
                    Photo p = getPhoto(position);

                    p.liked_by_user = response.body().photo.liked_by_user;
                    p.likes = response.body().photo.likes;

                    resultList.get(typeList.get(position).resultPosition)
                            .objects.set(typeList.get(position).objectPosition, p);
                }
            } else {
                photoService.setLikeForAPhoto(
                        id,
                        like,
                        this);
            }
        }

        @Override
        public void onSetLikeFailed(Call<LikePhotoResult> call, Throwable t) {
            photoService.setLikeForAPhoto(
                    id,
                    like,
                    this);
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
            implements View.OnClickListener, LikeImageButton.OnLikeListener {
        // widget
        public RelativeLayout background;

        CircleImageView avatar;
        TextView actor;
        TextView verb;

        public FreedomImageView image;
        TextView title;
        ImageButton collectionButton;
        LikeImageButton likeButton;

        TextView more;

        // data
        private int position;

        ViewHolder(View itemView, int position) {
            super(itemView);
            this.position = position;
            switch (typeList.get(position).type) {
                case VIEW_TYPE_TITLE:
                    this.background = (RelativeLayout) itemView.findViewById(R.id.item_following_title_background);

                    this.avatar = (CircleImageView) itemView.findViewById(R.id.item_following_title_avatar);
                    avatar.setOnClickListener(this);

                    this.actor = (TextView) itemView.findViewById(R.id.item_following_title_actor);
                    actor.setOnClickListener(this);

                    this.verb = (TextView) itemView.findViewById(R.id.item_following_title_verb);
                    verb.setOnClickListener(this);
                    break;

                case VIEW_TYPE_CONTENT_1:
                case VIEW_TYPE_CONTENT_2:
                case VIEW_TYPE_CONTENT_3:
                    this.background = (RelativeLayout) itemView.findViewById(R.id.item_following_content_background);
                    background.setOnClickListener(this);

                    this.image = (FreedomImageView) itemView.findViewById(R.id.item_following_content_image);
                    image.setSize(getPhoto(position).width, getPhoto(position).height);

                    this.title = (TextView) itemView.findViewById(R.id.item_following_content_title);
                    DisplayUtils.setTypeface(itemView.getContext(), title);

                    this.collectionButton = (ImageButton) itemView.findViewById(R.id.item_following_content_collectionButton);
                    collectionButton.setOnClickListener(this);

                    this.likeButton = (LikeImageButton) itemView.findViewById(R.id.item_following_content_likeButton);
                    likeButton.setOnLikeListener(this);

                    itemView.findViewById(R.id.item_following_content_downloadButton).setOnClickListener(this);
                    break;

                case VIEW_TYPE_MORE:
                    this.background = (RelativeLayout) itemView.findViewById(R.id.item_following_more_background);
                    background.setOnClickListener(this);

                    this.image = (FreedomImageView) itemView.findViewById(R.id.item_following_more_image);
                    image.setSize(getPhoto(position).width, getPhoto(position).height);

                    this.more = (TextView) itemView.findViewById(R.id.item_following_more_title);
                    break;
            }
        }

        @Override
        public void onClick(View view) {
            switch (typeList.get(position).type) {
                case VIEW_TYPE_TITLE:
                    switch (view.getId()) {
                        case R.id.item_following_title_avatar:
                        case R.id.item_following_title_actor: {
                            IntentHelper.startUserActivity(
                                    (MysplashActivity) a,
                                    avatar,
                                    getUser(position));
                            break;
                        }
                        case R.id.item_following_title_verb: {
                            if (resultList.get(typeList.get(position).resultPosition).verb.equals(VERB_COLLECTED)) {
                                IntentHelper.startCollectionActivity(
                                        (MysplashActivity) a,
                                        avatar,
                                        background,
                                        getCollection(position));
                            } else if (resultList.get(typeList.get(position).resultPosition).verb.equals(VERB_RELEASED)
                                    || resultList.get(typeList.get(position).resultPosition).verb.equals(VERB_LIKED)) {
                                IntentHelper.startUserActivity(
                                        (MysplashActivity) a,
                                        avatar,
                                        getUser(position));
                            }
                            break;
                        }
                    }
                    break;

                case VIEW_TYPE_CONTENT_1:
                case VIEW_TYPE_CONTENT_2:
                case VIEW_TYPE_CONTENT_3:
                    switch (view.getId()) {
                        case R.id.item_following_content_background:
                            IntentHelper.startPhotoActivity(
                                    (MysplashActivity) a,
                                    image,
                                    background,
                                    getPhoto(position));
                            break;

                        case R.id.item_following_content_collectionButton:
                            if (!AuthManager.getInstance().isAuthorized()) {
                                IntentHelper.startLoginActivity((MysplashActivity) a);
                            } else {
                                SelectCollectionDialog dialog = new SelectCollectionDialog();
                                dialog.setPhotoAndListener(getPhoto(position), FollowingAdapter.this);
                                dialog.show(((Activity) a).getFragmentManager(), null);
                            }
                            break;

                        case R.id.item_following_content_downloadButton:
                            Photo p = getPhoto(position);
                            if (DatabaseHelper.getInstance(a).readDownloadEntityCount(p.id) == 0) {
                                DownloadHelper.getInstance(a).addMission(a, p, DownloadHelper.DOWNLOAD_TYPE);
                            } else {
                                NotificationUtils.showSnackbar(
                                        a.getString(R.string.feedback_download_repeat),
                                        Snackbar.LENGTH_SHORT);
                            }
                            break;
                    }
                    break;

                case VIEW_TYPE_MORE:
                    switch (view.getId()) {
                        case R.id.item_following_more_background:
                            if (resultList.get(typeList.get(position).resultPosition).verb.equals(VERB_COLLECTED)) {
                                IntentHelper.startCollectionActivity(
                                        (MysplashActivity) a,
                                        background,
                                        getCollection(position));
                            } else if (resultList.get(typeList.get(position).resultPosition).verb.equals(VERB_RELEASED)
                                    || resultList.get(typeList.get(position).resultPosition).verb.equals(VERB_LIKED)) {
                                IntentHelper.startUserActivity(
                                        (MysplashActivity) a,
                                        getUser(position));
                            }
                            break;
                    }
                    break;
            }
        }

        @Override
        public void onClickLikeButton(boolean newLikeState) {
            setLikeForAPhoto(newLikeState, getAdapterPosition());
        }
    }
}
