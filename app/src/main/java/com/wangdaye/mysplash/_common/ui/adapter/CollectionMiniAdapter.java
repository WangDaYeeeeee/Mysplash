package com.wangdaye.mysplash._common.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.ObservableColorMatrix;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;

import java.util.List;

/**
 * Collection mini adapter. (Recycler view)
 * */

public class CollectionMiniAdapter extends RecyclerView.Adapter<CollectionMiniAdapter.ViewHolder> {
    // widget
    private Context c;
    private List<Collection> itemList;
    private OnCollectionResponseListener listener;

    /** <br> life cycle. */

    public CollectionMiniAdapter(Context c, List<Collection> list) {
        this.c = c;
        this.itemList = list;
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection_mini, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position == 0) {
            holder.image.setImageResource(R.color.colorTextSubtitle_light);
            holder.title.setText(c.getString(R.string.feedback_create_collection).toUpperCase());
            holder.subtitle.setVisibility(View.GONE);
            holder.lockIcon.setVisibility(View.GONE);
            return;
        }

        holder.title.setText(itemList.get(position - 1).title.toUpperCase());
        int photoNum = itemList.get(position - 1).total_photos;
        holder.subtitle.setText(photoNum + (photoNum > 1 ? " photos" : " photo"));

        if (itemList.get(position - 1).cover_photo != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Glide.with(c)
                        .load(itemList.get(position - 1).cover_photo.urls.regular)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.image);
            } else {
                Glide.with(c)
                        .load(itemList.get(position - 1).cover_photo.urls.regular)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {
                                if (!itemList.get(position - 1).cover_photo.hasFadeIn) {
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
                                    saturation.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(c));
                                    saturation.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            holder.image.clearColorFilter();
                                            holder.image.setHasTransientState(false);
                                        }
                                    });
                                    saturation.start();
                                    itemList.get(position - 1).cover_photo.hasFadeIn = true;
                                }
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
        } else {
            holder.image.setImageResource(R.color.colorTextContent_light);
        }
        if (itemList.get(position - 1).privateX) {
            holder.lockIcon.setVisibility(View.VISIBLE);
        } else {
            holder.lockIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Glide.clear(holder.image);
    }

    /** <br> data. */

    @Override
    public int getItemCount() {
        return itemList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void insertItem(Collection item, int position) {
        itemList.add(position, item);
        notifyItemInserted(position + 1);
    }

    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

    public int getRealItemCount() {
        return itemList.size();
    }

    /** <br> interface. */

    public interface OnCollectionResponseListener {
        void onCreateCollection();
        void onAddToCollection(int collection_id);
    }

    public void setOnClickCreateItemListener(OnCollectionResponseListener l) {
        this.listener = l;
    }

    /** <br> inner class. */

    // view holder.

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        public ImageView image;
        public TextView title;
        public TextView subtitle;
        ImageView lockIcon;

        ViewHolder(View itemView) {
            super(itemView);

            itemView.findViewById(R.id.item_collection_mini_card).setOnClickListener(this);

            this.image = (ImageView) itemView.findViewById(R.id.item_collection_mini_image);

            this.title = (TextView) itemView.findViewById(R.id.item_collection_mini_title);

            this.subtitle = (TextView) itemView.findViewById(R.id.item_collection_mini_subtitle);
            TypefaceUtils.setTypeface(itemView.getContext(), subtitle);

            this.lockIcon = (ImageView) itemView.findViewById(R.id.item_collection_privateIcon);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_collection_mini_card:
                    if (getAdapterPosition() == 0 && listener != null) {
                        listener.onCreateCollection();
                    } else if (listener != null) {
                        listener.onAddToCollection(itemList.get(getAdapterPosition() - 1).id);
                    }
                    break;
            }
        }
    }
}