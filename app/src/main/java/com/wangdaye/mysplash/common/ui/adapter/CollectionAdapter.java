package com.wangdaye.mysplash.common.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.user.ui.UserActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Collection adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Collection}.
 *
 * */

public class CollectionAdapter extends FooterAdapter<RecyclerView.ViewHolder> {

    private List<Collection> itemList;
    private int columnCount;

    private static final int PAYLOAD_RESET = 1;
    private static final int PAYLOAD_UPDATE = 2;

    public CollectionAdapter(Context context, List<Collection> list, int columnCount) {
        super(context);
        this.itemList = list;
        this.columnCount = columnCount;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int position) {
        if (isFooter(position)) {
            // footer.
            return FooterHolder.buildInstance(parent);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_collection, parent, false);
            return new CollectionHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CollectionHolder && position < itemList.size()) {
            ((CollectionHolder) holder).onBindView(itemList.get(position), columnCount, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.isEmpty() || payloads.indexOf(PAYLOAD_UPDATE) == -1) {
            onBindViewHolder(holder, position);
        } else {
            ((CollectionHolder) holder).onBindView(itemList.get(position), columnCount, true);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof CollectionHolder) {
            ((CollectionHolder) holder).onRecycled();
        }
    }

    @Override
    public int getRealItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    protected boolean hasFooter(Context context) {
        return !DisplayUtils.isLandscape(context)
                && DisplayUtils.getNavigationBarHeight(context.getResources()) != 0;
    }

    public void insertItem(Collection c, int position) {
        if (position <= itemList.size()) {
            itemList.add(position, c);
            notifyItemInserted(position);
        }
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

    public void updateItem(Collection c, boolean refreshView, boolean probablyRepeat) {
        for (int i = 0; i < getRealItemCount(); i ++) {
            if (itemList.get(i).id == c.id) {
                if (c.cover_photo != null && itemList.get(i).cover_photo != null) {
                    c.cover_photo.loadPhotoSuccess = itemList.get(i).cover_photo.loadPhotoSuccess;
                    c.cover_photo.hasFadedIn = itemList.get(i).cover_photo.hasFadedIn;
                }
                if ((itemList.get(i).cover_photo == null && c.cover_photo != null)
                        || (itemList.get(i).cover_photo != null && c.cover_photo == null)
                        || (itemList.get(i).cover_photo != null && c.cover_photo != null
                        && !itemList.get(i).cover_photo.id.equals(c.cover_photo.id))) {
                    itemList.set(i, c);
                    if (refreshView) {
                        notifyItemChanged(i, PAYLOAD_RESET);
                    }
                } else {
                    itemList.set(i, c);
                    if (refreshView) {
                        notifyItemChanged(i, PAYLOAD_UPDATE);
                    }
                }
                if (!probablyRepeat) {
                    return;
                }
            }
        }
    }

    public List<Collection> getItemList() {
        return itemList;
    }
}

class CollectionHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_collection) CardView card;
    @BindView(R.id.item_collection_cover) FreedomImageView image;

    @BindView(R.id.item_collection_title) TextView title;
    @BindView(R.id.item_collection_subtitle) TextView subtitle;
    @BindView(R.id.item_collection_avatar) CircleImageView avatar;
    @BindView(R.id.item_collection_name) TextView name;

    CollectionHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    void onBindView(Collection collection, int columnCount, boolean update) {
        Context context = itemView.getContext();

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
        if (columnCount > 1) {
            int margin = context.getResources().getDimensionPixelSize(R.dimen.normal_margin);
            params.setMargins(0, 0, margin, margin);
            card.setLayoutParams(params);
            card.setRadius(context.getResources().getDimensionPixelSize(R.dimen.material_card_radius));
        } else {
            params.setMargins(0, 0, 0, 0);
            card.setLayoutParams(params);
            card.setRadius(0);
        }
        card.setOnClickListener(v -> {
            MysplashActivity activity = Mysplash.getInstance().getTopActivity();
            if (activity != null) {
                IntentHelper.startCollectionActivity(activity, avatar, card, collection);
            }
        });

        if (collection.cover_photo != null
                && collection.cover_photo.width != 0
                && collection.cover_photo.height != 0) {
            image.setSize(
                    collection.cover_photo.width,
                    collection.cover_photo.height);
        }

        if (update) {
            title.setText(collection.title.toUpperCase());
            subtitle.setText(collection.total_photos
                    + " " + context.getResources().getStringArray(R.array.user_tabs)[0]);
        } else {
            title.setText("");
            subtitle.setText("");
            name.setText("");
            image.setShowShadow(false);

            if (collection.cover_photo != null) {
                ImageHelper.loadCollectionCover(image.getContext(), image, collection, () -> {
                    collection.cover_photo.loadPhotoSuccess = true;
                    if (!collection.cover_photo.hasFadedIn) {
                        collection.cover_photo.hasFadedIn = true;
                        ImageHelper.startSaturationAnimation(context, image);
                    }
                    title.setText(collection.title.toUpperCase());
                    subtitle.setText(collection.total_photos
                            + " " + context.getResources().getStringArray(R.array.user_tabs)[0]);
                    name.setText(collection.user.name);
                    image.setShowShadow(true);
                });
                card.setCardBackgroundColor(
                        ImageHelper.computeCardBackgroundColor(
                                image.getContext(),
                                collection.cover_photo.color));
            } else {
                ImageHelper.loadResourceImage(image.getContext(), image, R.drawable.default_collection_cover);
            }

            ImageHelper.loadAvatar(avatar.getContext(), avatar, collection.user, null);
            avatar.setOnClickListener(v -> {
                MysplashActivity activity = Mysplash.getInstance().getTopActivity();
                if (activity != null) {
                    IntentHelper.startUserActivity(
                            activity, avatar, card, collection.user, UserActivity.PAGE_PHOTO);
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            card.setTransitionName(collection.id + "-background");
            avatar.setTransitionName(collection.user.username + "-avatar");
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
        ImageHelper.releaseImageView(avatar);
    }
}