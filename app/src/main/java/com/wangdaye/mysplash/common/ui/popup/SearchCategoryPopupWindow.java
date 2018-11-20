package com.wangdaye.mysplash.common.ui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.MysplashPopupWindow;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import butterknife.ButterKnife;

/**
 * Search category popup window.
 *
 * This popup window is used to select category.
 *
 * */

public class SearchCategoryPopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {

    private OnSearchCategoryChangedListener listener;

    private int valueNow;
    private boolean showAll = true;

    public SearchCategoryPopupWindow(Context c, View anchor, int valueNow, boolean showAll) {
        super(c);
        this.initialize(c, anchor, valueNow, showAll);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor, int valueNow, boolean showAll) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_search_category, null);
        setContentView(v);

        initData(valueNow, showAll);
        initWidget();
        show(anchor, 0, 0);
    }

    private void initData(int valueNow, boolean showAll) {
        this.valueNow = valueNow;
        this.showAll = showAll;
    }

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_search_category_all).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_buildings).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_food_drink).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_nature).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_object).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_people).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_technology).setOnClickListener(this);

        TextView allTxt = ButterKnife.findById(v, R.id.popup_search_category_allTxt);
        allTxt.setText(v.getContext().getText(R.string.all));
        if (valueNow == 0) {
            allTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        TextView buildingsTxt = ButterKnife.findById(v, R.id.popup_search_category_buildingsTxt);
        if (valueNow == Mysplash.CATEGORY_BUILDINGS_ID) {
            buildingsTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        TextView foodDrinkTxt = ButterKnife.findById(v, R.id.popup_search_category_food_drinkTxt);
        if (valueNow == Mysplash.CATEGORY_FOOD_DRINK_ID) {
            foodDrinkTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        TextView naturedTxt = ButterKnife.findById(v, R.id.popup_search_category_natureTxt);
        if (valueNow == Mysplash.CATEGORY_NATURE_ID) {
            naturedTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        TextView objectTxt = ButterKnife.findById(v, R.id.popup_search_category_objectTxt);
        if (valueNow == Mysplash.CATEGORY_OBJECTS_ID) {
            objectTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        TextView peopleTxt = ButterKnife.findById(v, R.id.popup_search_category_peopleTxt);
        if (valueNow == Mysplash.CATEGORY_PEOPLE_ID) {
            peopleTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        TextView technologyTxt = ButterKnife.findById(v, R.id.popup_search_category_technologyTxt);
        if (valueNow == Mysplash.CATEGORY_TECHNOLOGY_ID) {
            technologyTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        if (ThemeManager.getInstance(v.getContext()).isLightTheme()) {
            ((ImageView) v.findViewById(R.id.popup_search_category_allIcon))
                    .setImageResource(R.drawable.ic_infinity_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_buildingsIcon))
                    .setImageResource(R.drawable.ic_building_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_food_drinkIcon))
                    .setImageResource(R.drawable.ic_drink_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_natureIcon))
                    .setImageResource(R.drawable.ic_flower_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_objectIcon))
                    .setImageResource(R.drawable.ic_cube_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_peopleIcon))
                    .setImageResource(R.drawable.ic_face_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_technologyIcon))
                    .setImageResource(R.drawable.ic_technology_light);
        } else {
            ((ImageView) v.findViewById(R.id.popup_search_category_allIcon))
                    .setImageResource(R.drawable.ic_infinity_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_buildingsIcon))
                    .setImageResource(R.drawable.ic_building_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_food_drinkIcon))
                    .setImageResource(R.drawable.ic_drink_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_natureIcon))
                    .setImageResource(R.drawable.ic_flower_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_objectIcon))
                    .setImageResource(R.drawable.ic_cube_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_peopleIcon))
                    .setImageResource(R.drawable.ic_face_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_technologyIcon))
                    .setImageResource(R.drawable.ic_technology_dark);
        }

        if (showAll) {
            v.findViewById(R.id.popup_search_category_all).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.popup_search_category_all).setVisibility(View.GONE);
        }
    }

    // interface.

    // on search category changed listener.

    public interface OnSearchCategoryChangedListener {
        void onSearchCategoryChanged(int categoryId);
    }

    public void setOnSearchCategoryChangedListener(OnSearchCategoryChangedListener l) {
        listener = l;
    }

    // on click listener.

    @Override
    public void onClick(View view) {
        int newValue = valueNow;
        switch (view.getId()) {
            case R.id.popup_search_category_all:
                newValue = 0;
                break;

            case R.id.popup_search_category_buildings:
                newValue = Mysplash.CATEGORY_BUILDINGS_ID;
                break;

            case R.id.popup_search_category_food_drink:
                newValue = Mysplash.CATEGORY_FOOD_DRINK_ID;
                break;

            case R.id.popup_search_category_nature:
                newValue = Mysplash.CATEGORY_NATURE_ID;
                break;

            case R.id.popup_search_category_object:
                newValue = Mysplash.CATEGORY_OBJECTS_ID;
                break;

            case R.id.popup_search_category_people:
                newValue = Mysplash.CATEGORY_PEOPLE_ID;
                break;

            case R.id.popup_search_category_technology:
                newValue = Mysplash.CATEGORY_TECHNOLOGY_ID;
                break;
        }

        if (newValue != valueNow && listener != null) {
            listener.onSearchCategoryChanged(newValue);
            dismiss();
        }
    }
}
