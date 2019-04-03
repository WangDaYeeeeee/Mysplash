package com.wangdaye.mysplash.common.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.network.api.PhotoApi;
import com.wangdaye.mysplash.common.ui.activity.SettingsActivity;
import com.wangdaye.mysplash.common.ui.dialog.TimePickerDialog;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.download.NotificationHelper;
import com.wangdaye.mysplash.common.utils.ValueUtils;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.MuzeiOptionManager;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

/**
 * Settings fragment.
 *
 * This fragment is used to show setting options.
 *
 * */

public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener, TimePickerDialog.OnTimeChangedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.perference);
        if (getActivity() != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            initBasicPart(sharedPreferences);
            initFilterPart(sharedPreferences);
            initDownloadPart(sharedPreferences);
            initDisplayPart(sharedPreferences);
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // do nothing.
    }

    private void initBasicPart(SharedPreferences sharedPreferences) {
        // back to top.
        ListPreference backToTop = findPreference(getString(R.string.key_back_to_top));
        String backToTopValue = sharedPreferences.getString(getString(R.string.key_back_to_top), "all");
        String backToTopName = ValueUtils.getBackToTopName(getActivity(), backToTopValue);
        backToTop.setSummary(getString(R.string.now) + " : " + backToTopName);
        backToTop.setOnPreferenceChangeListener(this);

        // auto night mode.
        ListPreference autoNightMode = findPreference(getString(R.string.key_auto_night_mode));
        String autoNightModeValue = sharedPreferences.getString(getString(R.string.key_auto_night_mode), "follow_system");
        String autoNightModeName = ValueUtils.getAutoNightModeName(getActivity(), autoNightModeValue);
        autoNightMode.setSummary(getString(R.string.now) + " : " + autoNightModeName);
        autoNightMode.setOnPreferenceChangeListener(this);

        Preference nightStartTime = findPreference(getString(R.string.key_night_start_time));
        nightStartTime.setSummary(getString(R.string.now) + " : " + ThemeManager.getInstance(getActivity()).getNightStartTime());
        if (autoNightModeValue.equals("auto")) {
            nightStartTime.setEnabled(true);
        } else {
            nightStartTime.setEnabled(false);
        }

        Preference nightEndTime = findPreference(getString(R.string.key_night_end_time));
        nightEndTime.setSummary(getString(R.string.now) + " : " + ThemeManager.getInstance(getActivity()).getNightEndTime());
        if (autoNightModeValue.equals("auto")) {
            nightEndTime.setEnabled(true);
        } else {
            nightEndTime.setEnabled(false);
        }

        // language.
        ListPreference language = findPreference(getString(R.string.key_language));
        String languageValue = sharedPreferences.getString(getString(R.string.key_language), "follow_system");
        String languageName = ValueUtils.getLanguageName(getActivity(), languageValue);
        language.setSummary(getString(R.string.now) + " : " + languageName);
        language.setOnPreferenceChangeListener(this);

        // Muzei.
        Preference muzei = findPreference(getString(R.string.key_live_wallpaper_settings));
        if (!MuzeiOptionManager.isInstalledMuzei(getActivity())) {
            PreferenceCategory display = (PreferenceCategory) findPreference("basic");
            display.removePreference(muzei);
        }
    }

    private void initFilterPart(SharedPreferences sharedPreferences) {
        // default order.
        ListPreference defaultOrder = findPreference(getString(R.string.key_default_photo_order));
        String orderValue = sharedPreferences.getString(getString(R.string.key_default_photo_order), PhotoApi.ORDER_BY_LATEST);
        String orderName = ValueUtils.getOrderName(getActivity(), orderValue);
        defaultOrder.setSummary(getString(R.string.now) + " : " + orderName);
        defaultOrder.setOnPreferenceChangeListener(this);
    }

    private void initDownloadPart(SharedPreferences sharedPreferences) {
        // downloader.
        ListPreference downloader = findPreference(getString(R.string.key_downloader));
        String downloaderValue = sharedPreferences.getString(getString(R.string.key_downloader), "mysplash");
        String downloaderName = ValueUtils.getDownloaderName(getActivity(), downloaderValue);
        downloader.setSummary(getString(R.string.now) + " : " + downloaderName);
        downloader.setOnPreferenceChangeListener(this);

        // download scale.
        ListPreference downloadScale = findPreference(getString(R.string.key_download_scale));
        String scaleValue = sharedPreferences.getString(getString(R.string.key_download_scale), "compact");
        String scaleName = ValueUtils.getScaleName(getActivity(), scaleValue);
        downloadScale.setSummary(getString(R.string.now) + " : " + scaleName);
        downloadScale.setOnPreferenceChangeListener(this);
    }

    private void initDisplayPart(SharedPreferences sharedPreferences) {
        // saturation animation duration.
        ListPreference duration = findPreference(getString(R.string.key_saturation_animation_duration));
        String durationValue = sharedPreferences.getString(getString(R.string.key_saturation_animation_duration), "2000");
        String durationName = ValueUtils.getSaturationAnimationDurationName(getActivity(), durationValue);
        duration.setSummary(getString(R.string.now) + " : " + durationName);
        duration.setOnPreferenceChangeListener(this);

        // grid list in port.
        SwitchPreference gridPort = findPreference(getString(R.string.key_grid_list_in_port));
        gridPort.setOnPreferenceChangeListener(this);
        if (!DisplayUtils.isTabletDevice(getActivity())) {
            PreferenceCategory display = findPreference("display");
            display.removePreference(gridPort);
        }

        // grid list in land.
        SwitchPreference gridLand = findPreference(getString(R.string.key_grid_list_in_land));
        gridLand.setOnPreferenceChangeListener(this);
    }

    private void showRebootSnackbar() {
        NotificationHelper.showActionSnackbar(
                getString(R.string.feedback_notify_restart),
                getString(R.string.restart),
                rebootListener);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.key_night_start_time))) {
            if (getFragmentManager() != null) {
                TimePickerDialog dialog = new TimePickerDialog();
                dialog.setModel(true);
                dialog.setOnTimeChangedListener(this);
                dialog.show(getFragmentManager(), null);
            }
        } else if (preference.getKey().equals(getString(R.string.key_night_end_time))) {
            if (getFragmentManager() != null) {
                TimePickerDialog dialog = new TimePickerDialog();
                dialog.setModel(false);
                dialog.setOnTimeChangedListener(this);
                dialog.show(getFragmentManager(), null);
            }
        } else if (preference.getKey().equals(getString(R.string.key_live_wallpaper_settings))) {
            IntentHelper.startMuzeiSettingsActivity((MysplashActivity) getActivity());
        } else if (preference.getKey().equals(getString(R.string.key_custom_api_key))) {
            IntentHelper.startCustomApiActivity((SettingsActivity) getActivity());
        }
        return true;
    }

    // interface.

    // on preference changed listener.

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getKey().equals(getString(R.string.key_back_to_top))) {
            // back to top.
            SettingsOptionManager.getInstance(getActivity()).setBackToTopType((String) o);
            String backType = ValueUtils.getBackToTopName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + backType);
        } else if (preference.getKey().equals(getString(R.string.key_auto_night_mode))) {
            // auto night mode.
            SettingsOptionManager.getInstance(getActivity()).setAutoNightMode((String) o);
            String autoNightMode = ValueUtils.getAutoNightModeName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + autoNightMode);
            if (((String) o).equals("auto")) {
                findPreference(getString(R.string.key_night_start_time)).setEnabled(true);
                findPreference(getString(R.string.key_night_end_time)).setEnabled(true);
            } else {
                findPreference(getString(R.string.key_night_start_time)).setEnabled(false);
                findPreference(getString(R.string.key_night_end_time)).setEnabled(false);
            }
        } else if (preference.getKey().equals(getString(R.string.key_language))) {
            // language.
            SettingsOptionManager.getInstance(getActivity()).setLanguage((String) o);
            String language = ValueUtils.getLanguageName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + language);
            showRebootSnackbar();
        } else if (preference.getKey().equals(getString(R.string.key_default_photo_order))) {
            // default order.
            SettingsOptionManager.getInstance(getActivity()).setDefaultPhotoOrder((String) o);
            String order = ValueUtils.getOrderName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + order);
            showRebootSnackbar();
        } else if (preference.getKey().equals(getString(R.string.key_downloader))) {
            // downloader.
            if (DownloadHelper.getInstance(getActivity()).switchDownloader(getActivity(), (String) o)) {
                SettingsOptionManager.getInstance(getActivity()).setDownloader((String) o);
                String downloader = ValueUtils.getDownloaderName(getActivity(), (String) o);
                preference.setSummary(getString(R.string.now) + " : " + downloader);
            } else {
                NotificationHelper.showSnackbar(getString(R.string.feedback_task_in_process));
                return false;
            }
        } else if (preference.getKey().equals(getString(R.string.key_download_scale))) {
            // download scale.
            SettingsOptionManager.getInstance(getActivity()).setDownloadScale((String) o);
            String scale = ValueUtils.getScaleName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + scale);
        } else if (preference.getKey().equals(getString(R.string.key_saturation_animation_duration))) {
            // saturation animation duration.
            SettingsOptionManager.getInstance(getActivity()).setSaturationAnimationDuration((String) o);
            String duration = ValueUtils.getSaturationAnimationDurationName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + duration);
        } else if (preference.getKey().equals(getString(R.string.key_grid_list_in_port))
                || preference.getKey().equals(getString(R.string.key_grid_list_in_land))) {
            // grid.
            showRebootSnackbar();
        }
        return true;
    }

    // on action click listener.

    private View.OnClickListener rebootListener = v -> {
        if (getActivity() != null) {
            Mysplash.getInstance().dispatchRecreate();
        }
    };

    // on time changed listener.

    @Override
    public void timeChanged() {
        Preference nightStartTime = findPreference(getString(R.string.key_night_start_time));
        nightStartTime.setSummary(
                getString(R.string.now) + " : " + ThemeManager.getInstance(getActivity()).getNightStartTime());

        Preference nightEndTime = findPreference(getString(R.string.key_night_end_time));
        nightEndTime.setSummary(
                getString(R.string.now) + " : " + ThemeManager.getInstance(getActivity()).getNightEndTime());
    }
}
