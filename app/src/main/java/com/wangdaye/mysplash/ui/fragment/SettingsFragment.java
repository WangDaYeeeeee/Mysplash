package com.wangdaye.mysplash.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.data.unslpash.api.PhotoApi;
import com.wangdaye.mysplash.utils.ValueUtils;

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
        initBasicPart();
    }

    /** <br> UI. */

    private void initBasicPart() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // default order.
        ListPreference defaultOrder = (ListPreference) findPreference(getString(R.string.key_default_order));
        String orderKey = sharedPreferences.getString(
                getString(R.string.key_default_order),
                PhotoApi.ORDER_BY_LATEST);
        String orderName = ValueUtils.getOrderName(getActivity(), orderKey);
        defaultOrder.setSummary("Now : " + orderName);
        defaultOrder.setOnPreferenceChangeListener(this);

        // download scale.
        ListPreference downloadScale = (ListPreference) findPreference(getString(R.string.key_download_scale));
        String scaleKey = sharedPreferences.getString(
                getString(R.string.key_download_scale),
                "compact");
        String scaleName = ValueUtils.getScaleName(getActivity(), scaleKey);
        downloadScale.setSummary("Now : " + scaleName);
        downloadScale.setOnPreferenceChangeListener(this);

        // language.
        ListPreference language = (ListPreference) findPreference(getString(R.string.key_language));
        String languageKey = sharedPreferences.getString(
                getString(R.string.key_language),
                "follow_system");
        String languageName = ValueUtils.getLanguageName(getActivity(), languageKey);
        language.setSummary("Now : " + languageName);
        language.setOnPreferenceChangeListener(this);
    }

    /** <br> interface. */

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getKey().equals(getString(R.string.key_default_order))) {
            // default order
            ListPreference defaultOrder = (ListPreference) findPreference(getString(R.string.key_default_order));
            String orderName = ValueUtils.getOrderName(getActivity(), (String) o);
            defaultOrder.setSummary("Now : " + orderName);
            Toast.makeText(
                    getActivity(),
                    getString(R.string.feedback_notify_restart),
                    Toast.LENGTH_SHORT).show();
        } else if (preference.getKey().equals(getString(R.string.key_download_scale))) {
            // download scale.
            ListPreference downloadScale = (ListPreference) findPreference(getString(R.string.key_download_scale));
            String scaleName = ValueUtils.getScaleName(getActivity(), (String) o);
            downloadScale.setSummary("Now : " + scaleName);
        } else if (preference.getKey().equals(getString(R.string.key_language))) {
            // language.
            ListPreference language = (ListPreference) findPreference(getString(R.string.key_language));
            String languageName = ValueUtils.getLanguageName(getActivity(), (String) o);
            language.setSummary("Now : " + languageName);
            Toast.makeText(
                    getActivity(),
                    getString(R.string.feedback_notify_restart),
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
