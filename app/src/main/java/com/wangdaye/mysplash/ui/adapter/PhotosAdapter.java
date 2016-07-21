package com.wangdaye.mysplash.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.data.unslpash.model.Photo;
import com.wangdaye.mysplash.ui.widget.FreedomImageView;
import com.wangdaye.mysplash.utils.AnimUtils;
import com.wangdaye.mysplash.utils.MathUtils;
import com.wangdaye.mysplash.utils.ObservableColorMatrix;

import java.util.List;

/**
 * Photos adapter. (Recycler view)
 * */

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {
    // widget
    private Context context;
    private List<Photo> itemList;
    private OnItemClickListener listener;

    /** <br> life cycle. */

    public PhotosAdapter(Context context, List<Photo> list) {
        this.context = context;
        this.itemList = list;
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(v, viewType, listener);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(context)
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
                            saturation.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(context));
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
                        return false;
                    }

                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable>
                            target, boolean isFirstResource) {
                        return false;
                    }
                })
                .placeholder(getBackgroundColor(MathUtils.getRandomInt(3)))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .fitCenter()
                .into(holder.image);
    }

    private int getBackgroundColor(int key) {
        switch (key) {
            case 0:
                return R.color.colorPhotoBackground_1;

            case 1:
                return R.color.colorPhotoBackground_2;

            case 2:
                return R.color.colorPhotoBackground_3;

            default:
                return R.color.colorRoot;
        }
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

    /** <br> interface. */

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    /** <br> inner class. */

    // view holder.

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // widget
        public FreedomImageView image;
        private OnItemClickListener listener;

        public ViewHolder(View itemView, int position, OnItemClickListener l) {
            super(itemView);
            this.listener = l;

            this.image = (FreedomImageView) itemView.findViewById(R.id.item_photo_image);
            image.setSize(
                    itemList.get(position).width,
                    itemList.get(position).height);

            CardView card = (CardView) itemView.findViewById(R.id.item_photo_card);
            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}
