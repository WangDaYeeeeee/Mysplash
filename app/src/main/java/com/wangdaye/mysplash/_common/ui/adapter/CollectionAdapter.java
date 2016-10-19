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
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash._common.utils.ColorUtils;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;
import com.wangdaye.mysplash.collection.view.activity.CollectionActivity;
import com.wangdaye.mysplash._common.data.entity.Collection;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.ObservableColorMatrix;
import com.wangdaye.mysplash._common.ui.widget.FreedomImageView;
import com.wangdaye.mysplash.me.view.activity.MeActivity;

import java.util.List;

/**
 * Collection adapter. (Recycler view)
 * */

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {
    // widget
    private Context a;
    private List<Collection> itemList;

    /** <br> life cycle. */

    public CollectionAdapter(Context a, List<Collection> list) {
        this.a = a;
        this.itemList = list;
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent, false);
        return new ViewHolder(v, viewType);
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.title.setText("");
        holder.subtitle.setText("");
        holder.image.setShowShadow(false);
        if (itemList.get(position).cover_photo != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Glide.with(a)
                        .load(itemList.get(position).cover_photo.urls.regular)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {
                                holder.title.setText(itemList.get(position).title.toUpperCase());
                                int photoNum = itemList.get(position).total_photos;
                                holder.subtitle.setText(photoNum + " " + a.getResources().getStringArray(R.array.user_tabs)[0]);
                                holder.image.setShowShadow(true);
                                return false;
                            }

                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.image);
            } else {
                Glide.with(a)
                        .load(itemList.get(position).cover_photo.urls.regular)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {
                                if (!itemList.get(position).cover_photo.hasFadeIn) {
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
                                    itemList.get(position).cover_photo.hasFadeIn = true;
                                }

                                holder.title.setText(itemList.get(position).title.toUpperCase());
                                int photoNum = itemList.get(position).total_photos;
                                holder.subtitle.setText(photoNum + " " + a.getResources().getStringArray(R.array.user_tabs)[0]);
                                holder.image.setShowShadow(true);
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
            holder.background.setBackgroundColor(
                    ColorUtils.calcCardBackgroundColor(
                            a,
                            itemList.get(position).cover_photo.color));
        } else {
            holder.image.setImageResource(R.color.colorTextContent_light);
            holder.title.setText(itemList.get(position).title.toUpperCase());
            int photoNum = itemList.get(position).total_photos;
            holder.subtitle.setText(photoNum + (photoNum > 1 ? " photos" : " photo"));
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

    public void insertItem(Collection c, int position) {
        itemList.add(position, c);
        notifyItemInserted(position);
    }

    public void removeItem(Collection c) {
        for (int i = 0; i < itemList.size(); i ++) {
            if (itemList.get(i).id == c.id) {
                itemList.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void changeItem(Collection c) {
        for (int i = 0; i < itemList.size(); i ++) {
            if (itemList.get(i).id == c.id) {
                itemList.remove(i);
                itemList.add(i, c);
                notifyItemChanged(i);
                return;
            }
        }
        insertItem(c, 0);
    }

    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

    public int getRealItemCount() {
        return itemList.size();
    }

    /** <br> inner class. */

    // view holder.

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        public RelativeLayout background;
        public FreedomImageView image;
        public TextView title;
        public TextView subtitle;

        ViewHolder(View itemView, int position) {
            super(itemView);

            this.background = (RelativeLayout) itemView.findViewById(R.id.item_collection_background);
            background.setOnClickListener(this);

            this.image = (FreedomImageView) itemView.findViewById(R.id.item_collection_cover);
            if (itemList.get(position).cover_photo != null) {
                image.setSize(itemList.get(position).cover_photo.width, itemList.get(position).cover_photo.height);
            }

            this.title = (TextView) itemView.findViewById(R.id.item_collection_title);

            this.subtitle = (TextView) itemView.findViewById(R.id.item_collection_subtitle);
            TypefaceUtils.setTypeface(itemView.getContext(), subtitle);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_collection_background:
                    if (a instanceof Activity) {
                        Collection c = itemList.get(getAdapterPosition());
                        Mysplash.getInstance().setCollection(c);

                        Intent intent = new Intent(a, CollectionActivity.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat
                                .makeScaleUpAnimation(
                                        view,
                                        (int) view.getX(), (int) view.getY(),
                                        view.getMeasuredWidth(), view.getMeasuredHeight());
                        if (AuthManager.getInstance().getUsername() != null
                                &&
                                AuthManager.getInstance()
                                        .getUsername()
                                        .equals(itemList.get(getAdapterPosition()).user.username)) {
                            ActivityCompat.startActivityForResult(
                                    (Activity) a,
                                    intent,
                                    MeActivity.COLLECTION_ACTIVITY,
                                    options.toBundle());
                        } else {
                            ActivityCompat.startActivity((Activity) a, intent, options.toBundle());
                        }
                    }
                    break;
            }
        }
    }
}