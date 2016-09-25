package com.wangdaye.mysplash._common.ui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;

/**
 * Search category popup window.
 * */

public class SearchCategoryPopupWindow extends PopupWindow
        implements View.OnClickListener {
    // widget
    private OnSearchCategoryChangedListener listener;

    // data
    private int valueNow;

    /** <br> life cycle. */

    public SearchCategoryPopupWindow(Context c, View anchor, int valueNow) {
        super(c);
        this.initialize(c, anchor, valueNow);
        Mysplash.getInstance().setActivityInBackstage(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                Mysplash.getInstance().setActivityInBackstage(false);
            }
        });
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor, int valueNow) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_search_category, null);
        setContentView(v);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        initData(valueNow);
        initWidget();

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(10);
        }
        showAsDropDown(anchor, 0, 0, Gravity.CENTER);
    }

    /** <br> UI. */

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_search_category_all).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_buildings).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_food_drink).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_nature).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_object).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_people).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_technology).setOnClickListener(this);

        TextView allTxt = (TextView) v.findViewById(R.id.popup_search_category_allTxt);
        TypefaceUtils.setTypeface(v.getContext(), allTxt);
        allTxt.setText(v.getContext().getText(R.string.all));
        if (valueNow == 0) {
            if (ThemeUtils.getInstance(v.getContext()).isLightTheme()) {
                allTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                allTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView buildingsTxt = (TextView) v.findViewById(R.id.popup_search_category_buildingsTxt);
        TypefaceUtils.setTypeface(v.getContext(), buildingsTxt);
        buildingsTxt.setText(v.getContext().getText(R.string.action_category_buildings));
        if (valueNow == Mysplash.CATEGORY_BUILDINGS_ID) {
            if (ThemeUtils.getInstance(v.getContext()).isLightTheme()) {
                buildingsTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                buildingsTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView foodDrinkTxt = (TextView) v.findViewById(R.id.popup_search_category_food_drinkTxt);
        TypefaceUtils.setTypeface(v.getContext(), foodDrinkTxt);
        foodDrinkTxt.setText(v.getContext().getText(R.string.action_category_buildings));
        if (valueNow == Mysplash.CATEGORY_FOOD_DRINK_ID) {
            if (ThemeUtils.getInstance(v.getContext()).isLightTheme()) {
                foodDrinkTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                foodDrinkTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView naturedTxt = (TextView) v.findViewById(R.id.popup_search_category_natureTxt);
        TypefaceUtils.setTypeface(v.getContext(), naturedTxt);
        naturedTxt.setText(v.getContext().getText(R.string.action_category_buildings));
        if (valueNow == Mysplash.CATEGORY_NATURE_ID) {
            if (ThemeUtils.getInstance(v.getContext()).isLightTheme()) {
                naturedTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                naturedTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView objectTxt = (TextView) v.findViewById(R.id.popup_search_category_objectTxt);
        TypefaceUtils.setTypeface(v.getContext(), objectTxt);
        objectTxt.setText(v.getContext().getText(R.string.action_category_buildings));
        if (valueNow == Mysplash.CATEGORY_OBJECTS_ID) {
            if (ThemeUtils.getInstance(v.getContext()).isLightTheme()) {
                objectTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                objectTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView peopleTxt = (TextView) v.findViewById(R.id.popup_search_category_peopleTxt);
        TypefaceUtils.setTypeface(v.getContext(), peopleTxt);
        peopleTxt.setText(v.getContext().getText(R.string.action_category_buildings));
        if (valueNow == Mysplash.CATEGORY_PEOPLE_ID) {
            if (ThemeUtils.getInstance(v.getContext()).isLightTheme()) {
                peopleTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                peopleTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView technologyTxt = (TextView) v.findViewById(R.id.popup_search_category_technologyTxt);
        TypefaceUtils.setTypeface(v.getContext(), technologyTxt);
        technologyTxt.setText(v.getContext().getText(R.string.action_category_buildings));
        if (valueNow == Mysplash.CATEGORY_TECHNOLOGY_ID) {
            if (ThemeUtils.getInstance(v.getContext()).isLightTheme()) {
                technologyTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                technologyTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        if (ThemeUtils.getInstance(v.getContext()).isLightTheme()) {
            ((ImageView) v.findViewById(R.id.popup_search_orientation_allIcon))
                    .setImageResource(R.drawable.ic_infinity_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_buildingsIcon))
                    .setImageResource(R.drawable.ic_building_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_food_drinkIcon))
                    .setImageResource(R.drawable.ic_drink_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_natureIcon))
                    .setImageResource(R.drawable.ic_duck_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_objectIcon))
                    .setImageResource(R.drawable.ic_cube_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_peopleIcon))
                    .setImageResource(R.drawable.ic_face_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_technologyIcon))
                    .setImageResource(R.drawable.ic_technology_light);
        } else {
            ((ImageView) v.findViewById(R.id.popup_search_orientation_allIcon))
                    .setImageResource(R.drawable.ic_infinity_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_buildingsIcon))
                    .setImageResource(R.drawable.ic_building_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_food_drinkIcon))
                    .setImageResource(R.drawable.ic_drink_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_natureIcon))
                    .setImageResource(R.drawable.ic_duck_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_objectIcon))
                    .setImageResource(R.drawable.ic_cube_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_peopleIcon))
                    .setImageResource(R.drawable.ic_face_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_technologyIcon))
                    .setImageResource(R.drawable.ic_technology_dark);
        }
    }

    /** <br> data. */

    private void initData(int valueNow) {
        this.valueNow = valueNow;
    }

    /** <br> interface. */

    public interface OnSearchCategoryChangedListener {
        void onSearchCategoryChanged(int categoryId);
    }

    public void setOnSearchCategoryChangedListener(OnSearchCategoryChangedListener l) {
        listener = l;
    }

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
