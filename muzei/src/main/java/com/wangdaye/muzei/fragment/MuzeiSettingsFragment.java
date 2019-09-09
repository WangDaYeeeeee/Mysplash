package com.wangdaye.muzei.fragment;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.wangdaye.common.base.fragment.MysplashSettingsFragment;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.muzei.base.MuzeiOptionManager;
import com.wangdaye.muzei.base.MuzeiUpdateHelper;
import com.wangdaye.muzei.ui.MuzeiQueryDialog;
import com.wangdaye.muzei.R;

public class MuzeiSettingsFragment extends MysplashSettingsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.perference_muzei);

        MuzeiOptionManager manager = MuzeiOptionManager.getInstance(requireActivity());

        // muzei source.
        ListPreference source = findPreference(getString(R.string.key_muzei_source));
        source.setSummary(
                getNameByValue(
                        manager.getSource(),
                        R.array.muzei_sources,
                        R.array.muzei_source_values
                )
        );
        source.setOnPreferenceChangeListener((preference, newValue) -> {
            manager.setSource((String) newValue);
            preference.setSummary(
                    getNameByValue(
                            manager.getSource(),
                            R.array.muzei_sources,
                            R.array.muzei_source_values
                    )
            );

            findPreference(getString(R.string.key_muzei_collection_source)).setEnabled(
                    manager.getSource().equals(MuzeiOptionManager.SOURCE_COLLECTIONS)
            );
            return true;
        });

        // screen size.
        SwitchPreference screenSizeImage = findPreference(getString(R.string.key_muzei_screen_size_image));
        int[] size = MuzeiUpdateHelper.getScreenSize(requireActivity());
        screenSizeImage.setSummaryOn(size[1] + "×" + size[0]);
        screenSizeImage.setSummaryOff(R.string.muzei_settings_title_screen_size_image_summary_off);
        screenSizeImage.setOnPreferenceChangeListener((preference, newValue) -> {
            manager.setScreenSizeImage((Boolean) newValue);
            return true;
        });

        // cache mode.
        ListPreference cacheMode = findPreference(getString(R.string.key_muzei_cache_mode));
        cacheMode.setSummary(
                getNameByValue(
                        manager.getCacheMode(),
                        R.array.muzei_cache_modes,
                        R.array.muzei_cache_mode_values
                )
        );
        cacheMode.setOnPreferenceChangeListener((preference, newValue) -> {
            manager.setCacheMode((String) newValue);
            preference.setSummary(
                    getNameByValue(
                            manager.getCacheMode(),
                            R.array.muzei_cache_modes,
                            R.array.muzei_cache_mode_values
                    )
            );
            return true;
        });

        // collections.
        Preference collectionSource = findPreference(getString(R.string.key_muzei_collection_source));
        collectionSource.setEnabled(manager.getSource().equals(MuzeiOptionManager.SOURCE_COLLECTIONS));
        collectionSource.setOnPreferenceClickListener(preference -> {
            ComponentFactory.getMuzeiService()
                    .startMuzeiCollectionSourceConfigActivity(requireActivity());
            return true;
        });

        // query.
        Preference query = findPreference(getString(R.string.key_muzei_query));
        query.setSummary(manager.getQuery());
        query.setOnPreferenceClickListener(preference -> {
            MuzeiQueryDialog dialog = new MuzeiQueryDialog();
            dialog.setOnQueryChangedListener(query1 -> {
                MuzeiOptionManager.updateQuery(requireActivity(), query1);
                Preference q = findPreference(getString(R.string.key_muzei_query));
                q.setSummary(MuzeiOptionManager.getInstance(requireActivity()).getQuery());
            });
            dialog.show(requireFragmentManager(), null);
            return true;
        });
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // do nothing.
    }
}
