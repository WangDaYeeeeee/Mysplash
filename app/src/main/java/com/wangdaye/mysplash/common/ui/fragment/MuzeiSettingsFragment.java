package com.wangdaye.mysplash.common.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.muzei.MuzeiUpdateHelper;
import com.wangdaye.mysplash.common.ui.dialog.MuzeiQueryDialog;
import com.wangdaye.mysplash.common.utils.ValueUtils;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.muzei.MuzeiOptionManager;

public class MuzeiSettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.perference_muzei);
        if (getActivity() != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            MuzeiOptionManager manager = MuzeiOptionManager.getInstance(getActivity());

            ListPreference source = findPreference(getString(R.string.key_muzei_source));
            String sourceValue = sharedPreferences.getString(getString(R.string.key_muzei_source), "collection");
            String sourceName = ValueUtils.getMuzeiSourceName(getActivity(), sourceValue);
            source.setSummary(getString(R.string.now) + " : " + sourceName);
            source.setOnPreferenceChangeListener(this);

            SwitchPreference screenSizeImage = findPreference(getString(R.string.key_muzei_screen_size_image));
            int[] size = MuzeiUpdateHelper.getScreenSize(getActivity());
            screenSizeImage.setSummaryOn(size[1] + "×" + size[0]);
            screenSizeImage.setSummaryOff(R.string.muzei_settings_title_screen_size_image_summary_off);
            screenSizeImage.setOnPreferenceChangeListener(this);

            ListPreference cacheMode = findPreference(getString(R.string.key_muzei_cache_mode));
            String cacheModeValue = sharedPreferences.getString(getString(R.string.key_muzei_cache_mode), "keep");
            String cacheModeName = ValueUtils.getMuzeiCacheModeName(getActivity(), cacheModeValue);
            cacheMode.setSummary(getString(R.string.now) + " : " + cacheModeName);
            cacheMode.setOnPreferenceChangeListener(this);

            Preference collectionSource = findPreference(getString(R.string.key_muzei_collection_source));
            collectionSource.setEnabled(manager.getSource().equals("collection"));

            Preference query = findPreference(getString(R.string.key_muzei_query));
            query.setSummary(manager.getQuery());
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
        } else if (preference.getKey().equals(getString(R.string.key_muzei_query))) {
            MuzeiQueryDialog dialog = new MuzeiQueryDialog();
            dialog.setOnQueryChangedListener(query -> {
                MuzeiOptionManager.updateQuery(getActivity(), query);
                Preference q = findPreference(getString(R.string.key_muzei_query));
                q.setSummary(MuzeiOptionManager.getInstance(getActivity()).getQuery());
            });
            dialog.show(getFragmentManager(), null);
        }
        return true;
    }

    // interface.

    // on preference changed listener.

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        MuzeiOptionManager manager = MuzeiOptionManager.getInstance(getActivity());

        if (preference.getKey().equals(getString(R.string.key_muzei_source))) {
            // muzei source.
            manager.setSource((String) o);
            String sourceName = ValueUtils.getMuzeiSourceName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + sourceName);

            Preference collectionSource = findPreference(getString(R.string.key_muzei_collection_source));
            collectionSource.setEnabled(manager.getSource().equals("collection"));
        } else if (preference.getKey().equals(getString(R.string.key_muzei_screen_size_image))) {
            // screen size image.
            manager.setScreenSizeImage((Boolean) o);
        } else if (preference.getKey().equals(getString(R.string.key_muzei_cache_mode))) {
            // cache mode.
            manager.setCacheMode((String) o);
            String cacheModeName = ValueUtils.getMuzeiCacheModeName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + cacheModeName);
        }
        return true;
    }
}
