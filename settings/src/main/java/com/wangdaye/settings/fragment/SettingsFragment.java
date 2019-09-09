package com.wangdaye.settings.fragment;

import android.os.Bundle;

import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import android.view.View;

import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.base.fragment.MysplashSettingsFragment;
import com.wangdaye.component.service.SettingsService;
import com.wangdaye.settings.SettingsServiceIMP;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.settings.R;
import com.wangdaye.settings.activity.SettingsActivity;
import com.wangdaye.settings.base.RoutingHelper;
import com.wangdaye.settings.dialog.TimePickerDialog;

/**
 * Settings fragment.
 *
 * This fragment is used to show setting options.
 *
 * */

public class SettingsFragment extends MysplashSettingsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.perference);
        initBasicPart();
        initFilterPart();
        initDownloadPart();
        initDisplayPart();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // do nothing.
    }

    private void initBasicPart() {
        // back to top.
        ListPreference backToTop = findPreference(getString(R.string.key_back_to_top));
        backToTop.setSummary(
                getNameByValue(
                        ComponentFactory.getSettingsService().getBackToTopType(),
                        R.array.back_to_top_types,
                        R.array.back_to_top_type_values
                )
        );
        backToTop.setOnPreferenceChangeListener((preference, newValue) -> {
            SettingsServiceIMP.getInstance(requireActivity()).setBackToTopType((String) newValue);
            preference.setSummary(
                    getNameByValue(
                            (String) newValue,
                            R.array.back_to_top_types,
                            R.array.back_to_top_type_values
                    )
            );
            return true;
        });

        // auto night mode.
        ListPreference autoNightMode = findPreference(getString(R.string.key_auto_night_mode));
        autoNightMode.setSummary(
                getNameByValue(
                        ComponentFactory.getSettingsService().getAutoNightMode(),
                        R.array.auto_night_mode_types,
                        R.array.auto_night_mode_type_values
                )
        );
        autoNightMode.setOnPreferenceChangeListener((preference, newValue) -> {
            SettingsServiceIMP.getInstance(getActivity()).setAutoNightMode((String) newValue);
            preference.setSummary(
                    getNameByValue(
                            (String) newValue,
                            R.array.auto_night_mode_types,
                            R.array.auto_night_mode_type_values
                    )
            );
            if (SettingsService.DAY_NIGHT_MODE_AUTO.equals(newValue)) {
                findPreference(getString(R.string.key_night_start_time)).setEnabled(true);
                findPreference(getString(R.string.key_night_end_time)).setEnabled(true);
            } else {
                findPreference(getString(R.string.key_night_start_time)).setEnabled(false);
                findPreference(getString(R.string.key_night_end_time)).setEnabled(false);
            }
            return true;
        });

        // night start time.
        Preference nightStartTime = findPreference(getString(R.string.key_night_start_time));
        nightStartTime.setSummary(ThemeManager.getInstance(requireActivity()).getNightStartTime());
        nightStartTime.setEnabled(
                ComponentFactory.getSettingsService()
                        .getAutoNightMode()
                        .equals(SettingsService.DAY_NIGHT_MODE_AUTO)
        );
        nightStartTime.setOnPreferenceClickListener(preference -> {
            TimePickerDialog dialog = new TimePickerDialog();
            dialog.setModel(true);
            dialog.setOnTimeChangedListener(() ->
                    nightStartTime.setSummary(
                            ThemeManager.getInstance(requireActivity()).getNightStartTime()
                    )
            );
            dialog.show(requireFragmentManager(), null);
            return true;
        });

        // night end time.
        Preference nightEndTime = findPreference(getString(R.string.key_night_end_time));
        nightEndTime.setSummary(ThemeManager.getInstance(requireActivity()).getNightEndTime());
        nightEndTime.setEnabled(
                ComponentFactory.getSettingsService()
                        .getAutoNightMode()
                        .equals(SettingsService.DAY_NIGHT_MODE_AUTO)
        );
        nightEndTime.setOnPreferenceClickListener(preference -> {
            TimePickerDialog dialog = new TimePickerDialog();
            dialog.setModel(false);
            dialog.setOnTimeChangedListener(() ->
                    nightEndTime.setSummary(
                            ThemeManager.getInstance(requireActivity()).getNightEndTime()
                    )
            );
            dialog.show(requireFragmentManager(), null);
            return true;
        });

        // custom api key.
        findPreference(getString(R.string.key_custom_api_key)).setOnPreferenceClickListener(p -> {
            RoutingHelper.startCustomApiActivity(
                    requireActivity(),
                    SettingsActivity.ACTIVITY_REQUEST_CODE_CUSTOM_API
            );
            return true;
        });

        // language.
        ListPreference language = findPreference(getString(R.string.key_language));
        language.setSummary(
                getNameByValue(
                        ComponentFactory.getSettingsService().getLanguage(),
                        R.array.languages,
                        R.array.language_values
                )
        );
        language.setOnPreferenceChangeListener((preference, newValue) -> {
            SettingsServiceIMP.getInstance(requireActivity()).setLanguage((String) newValue);
            preference.setSummary(
                    getNameByValue(
                            (String) newValue,
                            R.array.languages,
                            R.array.language_values
                    )
            );
            showRebootSnackbar();
            return true;
        });

        // Muzei.
        Preference muzei = findPreference(getString(R.string.key_live_wallpaper_settings));
        muzei.setVisible(ComponentFactory.getMuzeiService().isMuzeiInstalled(getActivity()));
        muzei.setOnPreferenceClickListener(preference -> {
            ComponentFactory.getMuzeiService().startMuzeiSettingsActivity(requireActivity());
            return true;
        });
    }

    private void initFilterPart() {
        // default order.
        ListPreference defaultOrder = findPreference(getString(R.string.key_default_photo_order));
        defaultOrder.setSummary(
                getNameByValue(
                        ComponentFactory.getSettingsService().getDefaultPhotoOrder(),
                        R.array.photo_orders,
                        R.array.photo_order_values
                )
        );
        defaultOrder.setOnPreferenceChangeListener((preference, newValue) -> {
            SettingsServiceIMP.getInstance(requireActivity()).setDefaultPhotoOrder((String) newValue);
            preference.setSummary(
                    getNameByValue(
                            (String) newValue,
                            R.array.photo_orders,
                            R.array.photo_order_values
                    )
            );
            showRebootSnackbar();
            return true;
        });
    }

    private void initDownloadPart() {
        // downloader.
        ListPreference downloader = findPreference(getString(R.string.key_downloader));
        downloader.setSummary(
                getNameByValue(
                        ComponentFactory.getSettingsService().getDownloader(),
                        R.array.downloader_types,
                        R.array.downloader_type_values
                )
        );
        downloader.setOnPreferenceChangeListener((preference, newValue) -> {
            if (ComponentFactory.getDownloaderService().switchDownloader(getActivity(), (String) newValue)) {
                SettingsServiceIMP.getInstance(getActivity()).setDownloader((String) newValue);
                preference.setSummary(
                        getNameByValue(
                                (String) newValue,
                                R.array.downloader_types,
                                R.array.downloader_type_values
                        )
                );
                return true;
            } else {
                NotificationHelper.showSnackbar(
                        (MysplashActivity) requireActivity(),
                        getString(R.string.feedback_task_in_process)
                );
                return false;
            }
        });

        // download scale.
        ListPreference downloadScale = findPreference(getString(R.string.key_download_scale));
        downloadScale.setSummary(
                getNameByValue(
                        ComponentFactory.getSettingsService().getDownloadScale(),
                        R.array.download_types,
                        R.array.download_type_values
                )
        );
        downloadScale.setOnPreferenceChangeListener((preference, newValue) -> {
            SettingsServiceIMP.getInstance(requireActivity()).setDownloadScale((String) newValue);
            preference.setSummary(
                    getNameByValue(
                            (String) newValue,
                            R.array.download_types,
                            R.array.download_type_values
                    )
            );
            return true;
        });
    }

    private void initDisplayPart() {
        // saturation animation duration.
        ListPreference duration = findPreference(getString(R.string.key_saturation_animation_duration));
        duration.setSummary(
                getNameByValue(
                        ComponentFactory.getSettingsService().getSaturationAnimationDuration(),
                        R.array.saturation_animation_durations,
                        R.array.saturation_animation_duration_values
                )
        );
        duration.setOnPreferenceChangeListener((preference, newValue) -> {
            SettingsServiceIMP.getInstance(requireActivity())
                    .setSaturationAnimationDuration((String) newValue);
            preference.setSummary(
                    getNameByValue(
                            (String) newValue,
                            R.array.saturation_animation_durations,
                            R.array.saturation_animation_duration_values
                    )
            );
            return true;
        });

        // grid list in port.
        SwitchPreference gridPort = findPreference(getString(R.string.key_grid_list_in_port));
        gridPort.setVisible(DisplayUtils.isTabletDevice(requireActivity()));
        gridPort.setOnPreferenceChangeListener((preference, newValue) -> {
            showRebootSnackbar();
            return true;
        });

        // grid list in land.
        findPreference(getString(R.string.key_grid_list_in_land)).setOnPreferenceChangeListener((p, v) -> {
            showRebootSnackbar();
            return true;
        });
    }

    private void showRebootSnackbar() {
        NotificationHelper.showActionSnackbar(
                getString(R.string.feedback_notify_restart),
                getString(R.string.restart),
                rebootListener);
    }

    private View.OnClickListener rebootListener = v -> {
        if (getActivity() != null) {
            MysplashApplication.getInstance().dispatchRecreate();
        }
    };
}
