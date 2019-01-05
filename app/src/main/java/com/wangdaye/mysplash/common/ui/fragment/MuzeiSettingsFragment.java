package com.wangdaye.mysplash.common.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.utils.ValueUtils;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.MuzeiOptionManager;

public class MuzeiSettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.perference_muzei);
        if (getActivity() != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            ListPreference source = (ListPreference) findPreference(getString(R.string.key_muzei_source));
            String sourceValue = sharedPreferences.getString(getString(R.string.key_muzei_source), "collection");
            String sourceName = ValueUtils.getMuzeiSourceName(getActivity(), sourceValue);
            source.setSummary(getString(R.string.now) + " : " + sourceName);
            source.setOnPreferenceChangeListener(this);

            Preference collectionSource = findPreference(getString(R.string.key_muzei_collection_source));
            collectionSource.setEnabled(MuzeiOptionManager.getInstance(getActivity()).getSource().equals("collection"));
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // do nothing.
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.key_muzei_collection_source))) {
            IntentHelper.startMuzeiCollectionSourceConfigActivity((MysplashActivity) getActivity());
        }
        return true;
    }

    // interface.

    // on preference changed listener.

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getKey().equals(getString(R.string.key_muzei_source))) {
            // muzei source.
            MuzeiOptionManager.getInstance(getActivity()).setSource((String) o);
            String sourceName = ValueUtils.getMuzeiSourceName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + sourceName);

            Preference collectionSource = findPreference(getString(R.string.key_muzei_collection_source));
            collectionSource.setEnabled(MuzeiOptionManager.getInstance(getActivity()).getSource().equals("collection"));
        }
        return true;
    }
}
