package com.wangdaye.mysplash._common.ui.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.api.PhotoApi;
import com.wangdaye.mysplash._common.ui.toast.MaterialToast;
import com.wangdaye.mysplash._common.utils.ValueUtils;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

import java.util.List;

/**
 * Settings fragment.
 * */

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener, MaterialToast.OnActionClickListener {

    /** <br> life cycle. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.perference);
        initView();
    }

    /** <br> view. */

    private void initView() {
        initBasicPart();
    }

    private void initBasicPart() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // default order.
        ListPreference defaultOrder = (ListPreference) findPreference(getString(R.string.key_default_photo_order));
        String orderValue = sharedPreferences.getString(
                getString(R.string.key_default_photo_order),
                PhotoApi.ORDER_BY_LATEST);
        String orderName = ValueUtils.getOrderName(getActivity(), orderValue);
        defaultOrder.setSummary("Now : " + orderName);
        defaultOrder.setOnPreferenceChangeListener(this);

        // collection type.
        ListPreference collectionType = (ListPreference) findPreference(getString(R.string.key_default_collection_type));
        String typeValue = sharedPreferences.getString(
                getString(R.string.key_default_collection_type),
                "featured");
        String valueName = ValueUtils.getCollectionName(getActivity(), typeValue);
        collectionType.setSummary("Now : " + valueName);
        collectionType.setOnPreferenceChangeListener(this);

        // download scale.
        ListPreference downloadScale = (ListPreference) findPreference(getString(R.string.key_download_scale));
        String scaleValue = sharedPreferences.getString(
                getString(R.string.key_download_scale),
                "compact");
        String scaleName = ValueUtils.getScaleName(getActivity(), scaleValue);
        downloadScale.setSummary("Now : " + scaleName);
        downloadScale.setOnPreferenceChangeListener(this);

        // language.
        ListPreference language = (ListPreference) findPreference(getString(R.string.key_language));
        String languageValue = sharedPreferences.getString(
                getString(R.string.key_language),
                "follow_system");
        String languageName = ValueUtils.getLanguageName(getActivity(), languageValue);
        language.setSummary("Now : " + languageName);
        language.setOnPreferenceChangeListener(this);
    }

    /** <br> interface. */

    // on preference changed listener.

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getKey().equals(getString(R.string.key_default_photo_order))) {
            // default order.
            String order = ValueUtils.getOrderName(getActivity(), (String) o);
            preference.setSummary("Now : " + order);
            MaterialToast.makeText(
                    getActivity(),
                    getString(R.string.feedback_notify_restart),
                    getString(R.string.restart),
                    MaterialToast.LENGTH_SHORT)
                    .setOnActionClickListener(this)
                    .show();
        } else if (preference.getKey().equals(getString(R.string.key_default_collection_type))) {
            // collection type.
            String type = ValueUtils.getCollectionName(getActivity(), (String) o);
            preference.setSummary("Now : " + type);
            MaterialToast.makeText(
                    getActivity(),
                    getString(R.string.feedback_notify_restart),
                    getString(R.string.restart),
                    MaterialToast.LENGTH_SHORT)
                    .setOnActionClickListener(this)
                    .show();
        } else if (preference.getKey().equals(getString(R.string.key_download_scale))) {
            // download scale.
            String scale = ValueUtils.getScaleName(getActivity(), (String) o);
            preference.setSummary("Now : " + scale);
        } else if (preference.getKey().equals(getString(R.string.key_language))) {
            // language.
            String language = ValueUtils.getLanguageName(getActivity(), (String) o);
            preference.setSummary("Now : " + language);
            MaterialToast.makeText(
                    getActivity(),
                    getString(R.string.feedback_notify_restart),
                    null,
                    MaterialToast.LENGTH_SHORT)
                    .show();
        }
        return true;
    }

    // on action click listener.

    @Override
    public void onActionClick() {
        List<Activity> list = Mysplash.getInstance().getActivityList();
        MainActivity a = (MainActivity) list.get(0);
        a.reboot();
    }
}
