package com.wangdaye.common.base.fragment;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.wangdaye.common.utils.ValueUtils;

import java.util.Objects;

public abstract class MysplashSettingsFragment extends PreferenceFragmentCompat {

    @NonNull
    @Override
    public <T extends Preference> T findPreference(@NonNull CharSequence key) {
        return Objects.requireNonNull(super.findPreference(key));
    }

    protected String getNameByValue(String value,
                                    @ArrayRes int nameArrayId, @ArrayRes int valueArrayId) {
        return ValueUtils.getNameByValue(requireActivity(), value, nameArrayId, valueArrayId);
    }
}
