package com.wangdaye.mysplash.main.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
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
import com.wangdaye.mysplash.common.data.model.LikePhotoResult;
import com.wangdaye.mysplash.common.data.model.Photo;
import com.wangdaye.mysplash.common.data.model.SimplifiedPhoto;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.widget.FreedomImageView;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.ColorUtils;
import com.wangdaye.mysplash.common.utils.ObservableColorMatrix;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Photos adapter. (Recycler view)
 * */

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder>
        implements PhotoService.OnSetLikeListener {
    // widget
    private Context a;
    private List<Photo> itemList;
    private PhotoService service;

    /** <br> life cycle. */

    public PhotosAdapter(Context a, List<Photo> list) {
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Glide.with(a)
                    .load(itemList.get(position).urls.regular)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.image);
        } else {
            Glide.with(a)
                    .load(itemList.get(position).urls.regular)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
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
                            String titleTxt = "by " + itemList.get(position).user.name + ", "
                                    + itemList.get(position).created_at.split("T")[0];
                            holder.title.setText(titleTxt);
                            return false;
                        }

                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable>
                                target, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.image);
        }

        holder.likeButton.setImageResource(
                itemList.get(position).liked_by_user ?
                        R.drawable.ic_item_heart_red : R.drawable.ic_item_heart_outline);
        holder.card.setBackgroundColor(ColorUtils.calcCardBackgroundColor(itemList.get(position).color));
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

    public List<Photo> getItemList() {
        return itemList;
    }

    private void setLikeForAPhoto(int position) {
        if (service == null) {
            service = PhotoService.getService()
                    .buildClient();
        } else {
            service.cancel();
        }
        service.setLikeForAPhoto(
                itemList.get(position).id,
                !itemList.get(position).liked_by_user,
                position,
                this);
    }

    public void cancelService() {
        if (service != null) {
            service.cancel();
        }
    }

    /** <br> interface. */

    // on set like listener.

    @Override
    public void onSetLikeSuccess(Call<LikePhotoResult> call, Response<LikePhotoResult> response, int position) {
        if (response.body() != null && itemList.get(position).id.equals(response.body().photo.id)) {
            itemList.get(position).liked_by_user = response.body().photo.liked_by_user;
            itemList.get(position).likes = response.body().photo.likes;
        }
    }

    @Override
    public void onSetLikeFailed(Call<LikePhotoResult> call, Throwable t, int position) {
        // do nothing.
    }

    /** <br> inner class. */

    // view holder.

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // widget
        public CardView card;
        public FreedomImageView image;
        public TextView title;
        public ImageButton likeButton;

        public ViewHolder(View itemView, int position) {
            super(itemView);

            this.card = (CardView) itemView.findViewById(R.id.item_photo_card);
            card.setOnClickListener(this);

            this.image = (FreedomImageView) itemView.findViewById(R.id.item_photo_image);
            image.setSize(
                    itemList.get(position).width,
                    itemList.get(position).height);

            this.title = (TextView) itemView.findViewById(R.id.item_photo_title);
            this.likeButton = (ImageButton) itemView.findViewById(R.id.item_photo_likeButton);
            likeButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_photo_card:
                    if (a instanceof Activity) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(
                                a.getString(R.string.intent_key_photo),
                                new SimplifiedPhoto(itemList.get(getAdapterPosition())));

                        Intent intent = new Intent(a, PhotoActivity.class);
                        intent.putExtras(bundle);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptionsCompat options = ActivityOptionsCompat
                                    .makeScaleUpAnimation(
                                            view,
                                            (int) view.getX(), (int) view.getY(),
                                            view.getMeasuredWidth(), view.getMeasuredHeight());
                            ActivityCompat.startActivity((Activity) a, intent, options.toBundle());
                        } else {
                            View imageView = ((RelativeLayout) ((CardView) view).getChildAt(0)).getChildAt(0);
                            ActivityOptionsCompat options = ActivityOptionsCompat
                                    .makeSceneTransitionAnimation(
                                            (Activity) a,
                                            Pair.create(imageView, a.getString(R.string.transition_photo_image)));
                            ActivityCompat.startActivity((Activity) a, intent, options.toBundle());
                        }
                    }
                    break;

                case R.id.item_photo_likeButton:
                    setLikeForAPhoto(getAdapterPosition());
                    if (itemList.get(getAdapterPosition()).liked_by_user) {
                        itemList.get(getAdapterPosition()).liked_by_user = false;
                        ((ImageButton) view).setImageResource(R.drawable.ic_item_heart_broken);
                    } else {
                        itemList.get(getAdapterPosition()).liked_by_user = true;
                        ((ImageButton) view).setImageResource(R.drawable.ic_item_heart_red);
                    }
                    break;
            }
        }
    }
}
