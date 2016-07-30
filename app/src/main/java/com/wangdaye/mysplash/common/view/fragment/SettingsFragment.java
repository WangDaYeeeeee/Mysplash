package com.wangdaye.mysplash.common.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.api.PhotoApi;
import com.wangdaye.mysplash.common.utils.ValueUtils;

/**
 * Settings fragment.
 * */

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

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
        ListPreference defaultOrder = (ListPreference) findPreference(getString(R.string.key_default_order));
        String orderValue = sharedPreferences.getString(
                getString(R.string.key_default_order),
                PhotoApi.ORDER_BY_LATEST);
        String orderName = ValueUtils.getOrderName(getActivity(), orderValue);
        defaultOrder.setSummary("Now : " + orderName);
        defaultOrder.setOnPreferenceChangeListener(this);

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

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getKey().equals(getString(R.string.key_default_order))) {
            // default order
            String order = ValueUtils.getOrderName(getActivity(), (String) o);
            preference.setSummary("Now : " + order);
            Toast.makeText(
                    getActivity(),
                    getString(R.string.feedback_notify_restart),
                    Toast.LENGTH_SHORT).show();
        } else if (preference.getKey().equals(getString(R.string.key_download_scale))) {
            // download scale.
            String scale = ValueUtils.getScaleName(getActivity(), (String) o);
            preference.setSummary("Now : " + scale);
        } else if (preference.getKey().equals(getString(R.string.key_language))) {
            // language.
            String language = ValueUtils.getLanguageName(getActivity(), (String) o);
            preference.setSummary("Now : " + language);
            Toast.makeText(
                    getActivity(),
                    getString(R.string.feedback_notify_restart),
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
