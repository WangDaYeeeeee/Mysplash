package com.wangdaye.mysplash.common.ui.adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
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
import com.wangdaye.mysplash.common.utils.DisplayUtils;
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

    public class ViewHolder extends RecyclerView.ViewHolder {

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

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            DisplayUtils.setTypeface(itemView.getContext(), subtitle);
        }

        void onBindView(int position) {
            if (position == 0) {
                ImageHelper.loadResourceImage(c, image, R.drawable.default_collection_creator);
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

        public void setSubtitle(Collection collection) {
            subtitle.setText(
                    collection.total_photos
                            + " " + c.getResources().getStringArray(R.array.user_tabs)[0]);
        }

        public void setResultState(@DrawableRes int imageId) {
            stateIcon.setResultState(imageId);
        }

        public void reloadCoverImage(final Collection collection) {
            if (collection.cover_photo != null) {
                ImageHelper.loadCollectionCover(
                        c, image, collection,
                        new ImageHelper.OnLoadImageListener() {
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
                        ImageHelper.loadResourceImage(c, image, R.drawable.default_collection_cover);
                    }
                });
            } else {
                ImageHelper.loadResourceImage(c, image, R.drawable.default_collection_cover);
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
    }

    public CollectionMiniAdapter(Context c, Photo p) {
        this.c = c;
        updatePhoto(p);
    }

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

    @Override
    public void onViewRecycled(ViewHolder holder) {
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