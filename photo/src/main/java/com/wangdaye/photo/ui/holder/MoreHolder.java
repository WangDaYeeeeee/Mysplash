package com.wangdaye.photo.ui.holder;

import android.annotation.SuppressLint;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.ui.adapter.PagerAdapter;
import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.adapter.PhotoInfoAdapter3;
import com.wangdaye.common.ui.widget.SwipeSwitchLayout;
import com.wangdaye.common.image.ImageHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * More holder.
 *
 * This view holder is used to show related photos and collections information.
 *
 * */

public class MoreHolder extends PhotoInfoAdapter3.ViewHolder
        implements ViewPager.OnPageChangeListener {

    @BindView(R2.id.item_photo_3_more_viewPager) SwipeSwitchLayout.ViewPager2 viewPager;
    @BindView(R2.id.item_photo_3_more_indicator) InkPageIndicator indicator;

    private ImageView[] covers;
    private TextView[] titles;

    private MoreHolderModel model;

    public static final int TYPE_MORE = 7;

    public MoreHolder(View itemView, Photo photo, @Nullable MoreHolderModel model) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        if (model == null) {
            model = new MoreHolderModel(photo);
        }
        this.model = model;
        this.covers = new ImageView[model.totalPage];
        this.titles = new TextView[model.totalPage];
    }

    public static class MoreHolderModel {

        int position;
        int totalPage;
        Boolean[] hasFadedIn;

        MoreHolderModel(Photo photo) {
            position = 0;
            totalPage = photo.related_collections.results.size();
            hasFadedIn = new Boolean[totalPage];
            for (int i = 0; i < hasFadedIn.length; i ++) {
                hasFadedIn[i] = false;
            }
        }
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onBindView(final PhotoActivity a, final Photo photo) {
        int size = photo.related_collections.results.size();
        List<View> viewList = new ArrayList<>(size);
        List<String> titleList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            View view = LayoutInflater.from(a).inflate(R.layout.item_photo_3_more_page_vertical, null);
            final int finalI = i;
            view.setOnClickListener(v ->
                    ComponentFactory.getCollectionModule().startCollectionActivity(
                            a,
                            photo.related_collections.results.get(finalI)
                    )
            );

            covers[i] = view.findViewById(R.id.item_photo_3_more_page_vertical_cover);
            ImageHelper.loadCollectionCover(
                    a,
                    covers[i],
                    photo.related_collections.results.get(i),
                    null
            );

            titles[i] = view.findViewById(R.id.item_photo_3_more_page_vertical_title);
            titles[i].setText(photo.related_collections.results.get(i).title.toUpperCase());

            viewList.add(view);
        }

        viewPager.setAdapter(new PagerAdapter(viewList, titleList));
        viewPager.setCurrentItem(model.position);
        viewPager.clearOnPageChangeListeners();
        viewPager.addOnPageChangeListener(this);

        indicator.setViewPager(viewPager);
    }

    @Override
    protected void onRecycled() {
        for (ImageView i : covers) {
            ImageHelper.releaseImageView(i);
        }
    }

    public MoreHolderModel saveModel() {
        return model;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    // interface.

    // on page change listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        model.position = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing.
    }
}
