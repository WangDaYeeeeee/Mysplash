package com.wangdaye.mysplash._common.ui.adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;

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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(position);
    }

    /** <br> data. */

    @Override
    public int getItemCount() {
        return AuthManager.getInstance()
                .getCollectionsManager()
                .getCollectionList()
                .size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
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

        void onBindView(int position) {
            if (position == 0) {
                image.setImageResource(R.color.colorTextSubtitle_light);
                title.setText(c.getString(R.string.feedback_create_collection).toUpperCase());
                subtitle.setVisibility(View.GONE);
                lockIcon.setVisibility(View.GONE);
                stateIcon.forceSetResultState(android.R.color.transparent);
                return;
            }

            final Collection collection = AuthManager.getInstance()
                    .getCollectionsManager()
                    .getCollectionList()
                    .get(position - 1);

            subtitle.setVisibility(View.VISIBLE);
            lockIcon.setVisibility(View.VISIBLE);

            title.setText(collection.title.toUpperCase());
            int photoNum = collection.total_photos;
            subtitle.setText(photoNum + " " + c.getResources().getStringArray(R.array.user_tabs)[0]);

            reloadCoverImage(collection);

            if (collection.privateX) {
                lockIcon.setAlpha(1f);
            } else {
                lockIcon.setAlpha(0f);
            }

            if (collection.insertingPhoto) {
                stateIcon.forceSetProgressState();
            } else {
                for (int i = 0; i < photo.current_user_collections.size(); i ++) {
                    if (collection.id == photo.current_user_collections.get(i).id) {
                        stateIcon.forceSetResultState(R.drawable.ic_item_state_succeed);
                        return;
                    }
                }
                stateIcon.forceSetResultState(android.R.color.transparent);
            }
        }

        public void setProgressState() {
            stateIcon.setProgressState();
        }

        public void setResultState(@DrawableRes int imageId) {
            stateIcon.setResultState(imageId);
        }

        public void reloadCoverImage(final Collection collection) {
            if (collection.cover_photo != null) {
                ImageHelper.loadCollectionCover(c, image, collection, new ImageHelper.OnLoadImageListener() {
                    @Override
                    public void onLoadSucceed() {
                        if (!collection.cover_photo.hasFadedIn) {
                            collection.cover_photo.hasFadedIn = true;
                            AuthManager.getInstance()
                                    .getCollectionsManager()
                                    .updateCollection(collection);
                            ImageHelper.startSaturationAnimation(c, image);
                        }
                    }

                    @Override
                    public void onLoadFailed() {
                        image.setImageResource(R.color.colorTextContent_light);
                    }
                });
            } else {
                image.setImageResource(R.color.colorTextContent_light);
            }
        }

        void onRecycled() {
            ImageHelper.releaseImageView(image);
        }

        // interface.

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_collection_mini_card:
                    if (getAdapterPosition() == 0 && listener != null) {
                        listener.onCreateCollection();
                    } else if (stateIcon.isUsable() && listener != null) {
                        Collection collection = AuthManager.getInstance()
                                .getCollectionsManager()
                                .getCollectionList()
                                .get(getAdapterPosition() - 1);
                        collection.insertingPhoto = true;
                        AuthManager.getInstance()
                                .getCollectionsManager()
                                .updateCollection(collection);
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