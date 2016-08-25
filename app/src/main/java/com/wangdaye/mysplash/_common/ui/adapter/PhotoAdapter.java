package com.wangdaye.mysplash._common.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
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
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.data.data.LikePhotoResult;
import com.wangdaye.mysplash._common.data.data.Me;
import com.wangdaye.mysplash._common.data.data.Photo;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.data.tools.AuthManager;
import com.wangdaye.mysplash._common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash._common.ui.toast.MaterialToast;
import com.wangdaye.mysplash._common.ui.widget.FreedomImageView;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.ColorUtils;
import com.wangdaye.mysplash._common.utils.ObservableColorMatrix;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;
import com.wangdaye.mysplash._common.ui.activity.LoginActivity;
import com.wangdaye.mysplash.me.view.activity.MeActivity;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Photos adapter. (Recycler view)
 * */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>
        implements SelectCollectionDialog.OnCollectionsChangedListener {
    // widget
    private Context a;
    private List<Photo> itemList;
    private PhotoService service;

    // data
    private boolean own = false;

    /** <br> life cycle. */

    public PhotoAdapter(Context a, List<Photo> list) {
        this.a = a;
        this.itemList = list;
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(v, viewType);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.title.setText("");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Glide.with(a)
                    .load(itemList.get(position).urls.regular)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            itemList.get(position).loadPhotoSuccess = true;
                            String titleTxt = "By " + itemList.get(position).user.name + ", On "
                                    + itemList.get(position).created_at.split("T")[0];
                            holder.title.setText(titleTxt);
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
                    .load(itemList.get(position).urls.regular)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            itemList.get(position).loadPhotoSuccess = true;
                            if (!itemList.get(position).hasFadedIn) {
                                holder.image.setHasTransientState(true);
                                final ObservableColorMatrix matrix = new ObservableColorMatrix();
                                final ObjectAnimator saturation = ObjectAnimator.ofFloat(
                                        matrix, ObservableColorMatrix.SATURATION, 0f, 1f);
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
                                itemList.get(position).hasFadedIn = true;
                            }
                            String titleTxt = "By " + itemList.get(position).user.name + ", On "
                                    + itemList.get(position).created_at.split("T")[0];
                            holder.title.setText(titleTxt);
                            return false;
                        }

                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.image);
        }

        if (itemList.get(position).liked_by_user) {
            holder.likeButton.setImageResource(R.drawable.ic_item_heart_red);
        } else {
            holder.likeButton.setImageResource(R.drawable.ic_item_heart_outline);
        }

        holder.background.setBackgroundColor(
                ColorUtils.calcCardBackgroundColor(
                        a,
                        itemList.get(position).color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.image.setTransitionName(itemList.get(position).id);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Glide.clear(holder.image);
    }

    public void setActivity(Activity a) {
        this.a = a;
    }

    /** <br> data. */

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void insertItem(Photo item) {
        itemList.add(item);
        notifyItemInserted(itemList.size() - 1);
    }

    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

    private void setLikeForAPhoto(int position) {
        if (service == null) {
            service = PhotoService.getService();
        } else {
            service.cancel();
        }
        service.setLikeForAPhoto(
                itemList.get(position).id,
                !itemList.get(position).liked_by_user,
                new OnSetLikeListener(position));
    }

    public void cancelService() {
        if (service != null) {
            service.cancel();
        }
    }

    public int getRealItemCount() {
        return itemList.size();
    }

    public void setOwn(boolean own) {
        this.own = own;
    }

    /** <br> interface. */

    // on set like listener.

    private class OnSetLikeListener implements PhotoService.OnSetLikeListener {
        // data
        private int position;

        public OnSetLikeListener(int position) {
            this.position = position;
        }

        @Override
        public void onSetLikeSuccess(Call<LikePhotoResult> call, Response<LikePhotoResult> response) {
            if (response.body() != null
                    && itemList.size() >= position && itemList.get(position).id.equals(response.body().photo.id)) {
                itemList.get(position).liked_by_user = response.body().photo.liked_by_user;
                itemList.get(position).likes = response.body().photo.likes;
            }
        }

        @Override
        public void onSetLikeFailed(Call<LikePhotoResult> call, Throwable t) {
            // do nothing.
        }
    }

    // on collections changed listener.

    @Override
    public void onAddCollection(Collection c) {
        ((MeActivity) a).addCollection(c);
    }

    @Override
    public void onAddPhotoToCollection(Collection c, Me me) {
        ((MeActivity) a).changeCollection(c);
    }

    /** <br> inner class. */

    // view holder.

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        public RelativeLayout background;
        public FreedomImageView image;
        public TextView title;
        public ImageButton likeButton;

        public ViewHolder(View itemView, int position) {
            super(itemView);

            this.background = (RelativeLayout) itemView.findViewById(R.id.item_photo_background);
            background.setOnClickListener(this);

            this.image = (FreedomImageView) itemView.findViewById(R.id.item_photo_image);
            image.setSize(itemList.get(position).width, itemList.get(position).height);

            this.title = (TextView) itemView.findViewById(R.id.item_photo_title);
            TypefaceUtils.setTypeface(itemView.getContext(), title);

            this.likeButton = (ImageButton) itemView.findViewById(R.id.item_photo_likeButton);
            likeButton.setOnClickListener(this);

            itemView.findViewById(R.id.item_photo_collectionButton).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_photo_background:
                    if (a instanceof Activity) {
                        Photo p = itemList.get(getAdapterPosition());
                        Mysplash.getInstance().setPhoto(p);

                        if (itemList.get(getAdapterPosition()).loadPhotoSuccess) {
                            View imageView = ((RelativeLayout) view).getChildAt(0);
                            Drawable d = ((FreedomImageView) imageView).getDrawable();
                            Mysplash.getInstance().setDrawable(d);
                        } else {
                            Mysplash.getInstance().setDrawable(null);
                        }

                        Intent intent = new Intent(a, PhotoActivity.class);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptionsCompat options = ActivityOptionsCompat
                                    .makeScaleUpAnimation(
                                            view,
                                            (int) view.getX(), (int) view.getY(),
                                            view.getMeasuredWidth(), view.getMeasuredHeight());
                            ActivityCompat.startActivity((Activity) a, intent, options.toBundle());
                        } else {
                            View imageView = ((RelativeLayout) view).getChildAt(0);
                            ActivityOptionsCompat options = ActivityOptionsCompat
                                    .makeSceneTransitionAnimation(
                                            (Activity) a,
                                            Pair.create(imageView, a.getString(R.string.transition_photo_image)),
                                            Pair.create(imageView, a.getString(R.string.transition_photo_background)));
                            ActivityCompat.startActivity((Activity) a, intent, options.toBundle());
                        }
                    }
                    break;

                case R.id.item_photo_likeButton:
                    if (AuthManager.getInstance().isAuthorized()) {
                        setLikeForAPhoto(getAdapterPosition());
                        if (itemList.get(getAdapterPosition()).liked_by_user) {
                            itemList.get(getAdapterPosition()).liked_by_user = false;
                            ((ImageButton) view).setImageResource(R.drawable.ic_item_heart_broken);
                        } else {
                            itemList.get(getAdapterPosition()).liked_by_user = true;
                            ((ImageButton) view).setImageResource(R.drawable.ic_item_heart_red);
                        }
                    }
                    break;

                case R.id.item_photo_collectionButton:
                    if (a instanceof Activity) {
                        if (!AuthManager.getInstance().isAuthorized()) {
                            Intent i = new Intent(a, LoginActivity.class);
                            a.startActivity(i);
                        } else if (AuthManager.getInstance().getMe() != null) {
                            SelectCollectionDialog dialog = new SelectCollectionDialog();
                            dialog.setPhoto(itemList.get(getAdapterPosition()));
                            if (own) {
                                dialog.setOnCollectionsChangedListener(PhotoAdapter.this);
                            }
                            dialog.show(((Activity) a).getFragmentManager(), null);
                        } else {
                            MaterialToast.makeText(
                                    a,
                                    a.getString(R.string.feedback_loading_my_profile),
                                    null,
                                    MaterialToast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
}
