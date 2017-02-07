package com.wangdaye.mysplash._common.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash._common.utils.widget.ColorAnimRequestListener;

/**
 * Collection mini adapter. (Recycler view)
 * */

public class CollectionMiniAdapter extends RecyclerView.Adapter<CollectionMiniAdapter.ViewHolder> {
    // widget
    private Context c;
    private OnCollectionResponseListener listener;

    // data
    private Photo photo;

    /** <br> life cycle. */

    public CollectionMiniAdapter(Context c, Photo p) {
        this.c = c;
        updatePhoto(p);
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection_mini, parent, false);
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
            holder.stateIcon.forceSetResultState(android.R.color.transparent);
            return;
        } else if (position == getRealItemCount() + 1) {
            return;
        }

        final Collection collection = AuthManager.getInstance()
                .getCollectionsManager()
                .getCollectionList()
                .get(position - 1);

        holder.subtitle.setVisibility(View.VISIBLE);
        holder.lockIcon.setVisibility(View.VISIBLE);

        holder.title.setText(collection.title.toUpperCase());
        int photoNum = collection.total_photos;
        holder.subtitle.setText(photoNum + " " + c.getResources().getStringArray(R.array.user_tabs)[0]);

        holder.reloadCoverImage(collection);

        if (collection.privateX) {
            holder.lockIcon.setAlpha(1f);
        } else {
            holder.lockIcon.setAlpha(0f);
        }

        for (int i = 0; i < photo.current_user_collections.size(); i ++) {
            if (collection.id == photo.current_user_collections.get(i).id) {
                holder.stateIcon.forceSetResultState(R.drawable.ic_item_state_succeed);
                return;
            }
        }
        holder.stateIcon.forceSetResultState(android.R.color.transparent);
    }

    /** <br> data. */

    @Override
    public int getItemCount() {
        return getRealItemCount() + 1;
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
    }

    public int getRealItemCount() {
        return AuthManager.getInstance().getCollectionsManager().getCollectionList().size();
    }

    public void updatePhoto(Photo p) {
        this.photo = p;
    }

    /** <br> interface. */

    public interface OnCollectionResponseListener {
        void onCreateCollection();
        void onClickCollectionItem(int collectionId, int adapterPosition);
    }

    public void setOnCollectionResponseListener(OnCollectionResponseListener l) {
        this.listener = l;
    }

    /** <br> inner class. */

    // view holder.

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        public ImageView image;
        public TextView title;
        public TextView subtitle;
        ImageView lockIcon;
        CircularProgressIcon stateIcon;

        // life cycle.

        ViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.item_collection_mini_card).setOnClickListener(this);

            this.image = (ImageView) itemView.findViewById(R.id.item_collection_mini_image);
            this.title = (TextView) itemView.findViewById(R.id.item_collection_mini_title);

            this.subtitle = (TextView) itemView.findViewById(R.id.item_collection_mini_subtitle);
            DisplayUtils.setTypeface(itemView.getContext(), subtitle);

            this.lockIcon = (ImageView) itemView.findViewById(R.id.item_collection_mini_lockIcon);
            this.stateIcon = (CircularProgressIcon) itemView.findViewById(R.id.item_collection_icon);
        }

        // UI.

        public void setProgressState() {
            stateIcon.setProgressState();
        }

        public void setResultState(@DrawableRes int imageId) {
            stateIcon.setResultState(imageId);
        }

        public void reloadCoverImage(final Collection collection) {
            if (collection.cover_photo != null) {
                Glide.with(c)
                        .load(collection.cover_photo.urls.regular)
                        .listener(new ColorAnimRequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {
                                if (!collection.cover_photo.hasFadedIn) {
                                    collection.cover_photo.hasFadedIn = true;
                                    AuthManager.getInstance()
                                            .getCollectionsManager()
                                            .updateCollection(collection);
                                    startColorAnimation(c, image);
                                }
                                return false;
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(image);
            } else {
                image.setImageResource(R.color.colorTextContent_light);
            }
        }

        // interface.

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_collection_mini_card:
                    if (getAdapterPosition() == 0 && listener != null) {
                        listener.onCreateCollection();
                    } else if (stateIcon.isUsable() && listener != null) {
                        listener.onClickCollectionItem(
                                AuthManager.getInstance()
                                        .getCollectionsManager()
                                        .getCollectionList()
                                        .get(getAdapterPosition() - 1).id,
                                getAdapterPosition());
                    }
                    break;
            }
        }
    }
}