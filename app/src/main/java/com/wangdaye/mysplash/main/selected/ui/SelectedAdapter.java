package com.wangdaye.mysplash.main.selected.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.ui.widget.CoverImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Selected adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Collection}.
 *
 * */

public class SelectedAdapter extends FooterAdapter<RecyclerView.ViewHolder> {

    private List<Collection> itemList;
    private int columnCount;

    public SelectedAdapter(Context context, List<Collection> list, int columnCount) {
        super(context);
        this.itemList = list;
        this.columnCount = columnCount;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        if (isFooter(position)) {
            // footer.
            return FooterHolder.buildInstance(parent);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_selected, parent, false);
            return new SelectedHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SelectedHolder && position < itemList.size()) {
            ((SelectedHolder) holder).onBindView(itemList.get(position), columnCount);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof SelectedHolder) {
            ((SelectedHolder) holder).onRecycled();
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

    public List<Collection> getItemList() {
        return itemList;
    }

    public void updateCollection(Collection c, boolean refreshView, boolean probablyRepeat) {
        for (int i = 0; i < getRealItemCount(); i ++) {
            if (itemList.get(i).id == c.id) {
                c.editing = itemList.get(i).editing;
                if (c.cover_photo != null && itemList.get(i).cover_photo != null) {
                    c.cover_photo.loadPhotoSuccess = itemList.get(i).cover_photo.loadPhotoSuccess;
                    c.cover_photo.hasFadedIn = itemList.get(i).cover_photo.hasFadedIn;
                    c.cover_photo.settingLike = itemList.get(i).cover_photo.settingLike;
                }
                itemList.set(i, c);
                if (refreshView) {
                    notifyItemChanged(i);
                }
                if (!probablyRepeat) {
                    return;
                }
            }
        }
    }
}

class SelectedHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_selected) CardView card;
    @BindView(R.id.item_selected_cover) CoverImageView image;
    @BindView(R.id.item_selected_title) TextView title;
    @BindView(R.id.item_selected_content) TextView content;

    SelectedHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    void onBindView(Collection collection, int columnCount) {
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
                IntentHelper.startCollectionActivity(activity, collection);
            }
        });

        if (collection.cover_photo != null
                && collection.cover_photo.width != 0
                && collection.cover_photo.height != 0) {
            image.setSize(
                    collection.cover_photo.width,
                    collection.cover_photo.height);
        }

        title.setText(collection.title);
        if (!TextUtils.isEmpty(collection.description)) {
            content.setVisibility(View.VISIBLE);
            content.setText(collection.description);
        } else {
            content.setVisibility(View.GONE);
        }

        if (collection.cover_photo != null) {
            ImageHelper.loadCollectionCover(image.getContext(), image, collection, () -> {
                collection.cover_photo.loadPhotoSuccess = true;
                if (!collection.cover_photo.hasFadedIn) {
                    collection.cover_photo.hasFadedIn = true;
                    ImageHelper.startSaturationAnimation(context, image);
                }
            });
        } else {
            ImageHelper.loadResourceImage(image.getContext(), image, R.drawable.default_collection_cover);
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
    }
}