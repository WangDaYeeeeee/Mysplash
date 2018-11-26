package com.wangdaye.mysplash.common.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Collection mini adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Collection} in mini style.
 *
 * */

public class CollectionMiniAdapter extends RecyclerView.Adapter<CollectionMiniAdapter.ViewHolder> {

    private Context c;
    private OnCollectionResponseListener listener;

    private Photo photo;

    public class ViewHolder extends RecyclerView.ViewHolder
            implements ImageHelper.OnLoadImageListener<Photo> {

        @BindView(R.id.item_collection_mini_image)
        ImageView image;

        @BindView(R.id.item_collection_mini_title)
        TextView title;

        @BindView(R.id.item_collection_mini_subtitle)
        TextView subtitle;

        @BindView(R.id.item_collection_mini_lockIcon)
        ImageView lockIcon;

        @BindView(R.id.item_collection_icon)
        CircularProgressIcon stateIcon;

        private Collection collection;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(int position) {
            if (position == 0) {
                ImageHelper.loadResourceImage(image.getContext(), image, R.drawable.default_collection_creator);
                title.setText(c.getString(R.string.feedback_create_collection).toUpperCase());
                subtitle.setVisibility(View.GONE);
                lockIcon.setVisibility(View.GONE);
                stateIcon.forceSetResultState(android.R.color.transparent);
                return;
            }

            this.collection = AuthManager.getInstance()
                    .getCollectionsManager()
                    .getCollectionList()
                    .get(position - 1);

            subtitle.setVisibility(View.VISIBLE);
            lockIcon.setVisibility(View.VISIBLE);

            title.setText(collection.title.toUpperCase());
            setSubtitle(collection);

            reloadCoverImage(collection);

            if (collection.privateX) {
                lockIcon.setAlpha(1f);
            } else {
                lockIcon.setAlpha(0f);
            }

            if (collection.editing) {
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

        void onRecycled() {
            ImageHelper.releaseImageView(image);
        }

        public void setProgressState() {
            stateIcon.setProgressState();
        }

        @SuppressLint("SetTextI18n")
        public void setSubtitle(Collection collection) {
            subtitle.setText(
                    collection.total_photos
                            + " " + c.getResources().getStringArray(R.array.user_tabs)[0]);
        }

        public void setResultState(@DrawableRes int imageId) {
            stateIcon.setResultState(imageId);
        }

        public void reloadCoverImage(Collection collection) {
            if (collection.cover_photo != null) {
                ImageHelper.loadCollectionCover(image.getContext(), image, collection, getAdapterPosition() - 1, this);
            } else {
                ImageHelper.loadResourceImage(image.getContext(), image, R.drawable.default_collection_cover);
            }
        }

        @OnClick(R.id.item_collection_mini_card) void clickItem() {
            if (getAdapterPosition() == 0 && listener != null) {
                listener.onCreateCollection();
            } else if (stateIcon.isUsable() && listener != null) {
                Collection collection = AuthManager.getInstance()
                        .getCollectionsManager()
                        .getCollectionList()
                        .get(getAdapterPosition() - 1);
                collection.editing = true;
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
        }

        // on load image listener.

        @Override
        public void onLoadImageSucceed(Photo newT, int index) {
            if (collection.cover_photo != null
                    && collection.cover_photo.updateLoadInformation(newT)) {
                Collection c = AuthManager.getInstance()
                        .getCollectionsManager()
                        .getCollectionList()
                        .get(index);
                if (c.cover_photo != null) {
                    c.cover_photo.updateLoadInformation(newT);
                }
                AuthManager.getInstance()
                        .getCollectionsManager()
                        .updateCollection(c);
            }
        }

        @Override
        public void onLoadImageFailed(Photo originalT, int index) {
            ImageHelper.loadResourceImage(image.getContext(), image, R.drawable.default_collection_cover);
        }
    }

    public CollectionMiniAdapter(Context c, Photo p) {
        this.c = c;
        updatePhoto(p);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection_mini, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindView(position);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

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

    public void updatePhoto(Photo p) {
        this.photo = p;
    }

    // interface.

    public interface OnCollectionResponseListener {
        void onCreateCollection();
        void onClickCollectionItem(int collectionId, int adapterPosition);
    }

    public void setOnCollectionResponseListener(OnCollectionResponseListener l) {
        this.listener = l;
    }
}