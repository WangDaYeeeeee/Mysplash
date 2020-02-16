package com.wangdaye.common.base.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.utils.ValueUtils;

import java.util.Objects;

public abstract class MysplashSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        RecyclerView rv = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        rv.setClipToPadding(false);
        rv.setFitsSystemWindows(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            rv.setOnApplyWindowInsetsListener((v, insets) -> {
                v.setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());
                return insets;
            });
        }
        return rv;
    }

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
