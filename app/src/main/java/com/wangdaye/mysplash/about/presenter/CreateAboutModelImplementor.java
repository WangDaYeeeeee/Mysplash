package com.wangdaye.mysplash.about.presenter;

import android.content.Context;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.AboutModel;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash.about.model.AppAboutObject;
import com.wangdaye.mysplash.about.model.CategoryAboutObject;
import com.wangdaye.mysplash.about.model.HeaderAboutObject;
import com.wangdaye.mysplash.about.model.LibraryObject;
import com.wangdaye.mysplash.about.model.TranslatorObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Create about model implementor.
 * */

public class CreateAboutModelImplementor {

    public static List<AboutModel> createModelList(Context c) {
        List<AboutModel> modelList = new ArrayList<>(17);
        boolean light = ThemeUtils.getInstance(c).isLightTheme();

        // header.
        modelList.add(new HeaderAboutObject());

        // about app.
        modelList.add(new CategoryAboutObject(c.getString(R.string.about_app)));
        modelList.add(new AppAboutObject(
                1,
                light ? R.drawable.ic_book_light : R.drawable.ic_book_dark,
                c.getString(R.string.introduce)));
        modelList.add(new AppAboutObject(
                2,
                light ? R.drawable.ic_github_light : R.drawable.ic_github_dark,
                c.getString(R.string.gitHub)));
        modelList.add(new AppAboutObject(
                3,
                light ? R.drawable.ic_email_light : R.drawable.ic_email_dark,
                c.getString(R.string.email)));
        modelList.add(new AppAboutObject(
                4,
                light ? R.drawable.ic_android_studio_light : R.drawable.ic_android_studio_dark,
                c.getString(R.string.source_code)));

        // translator.
        modelList.add(new CategoryAboutObject(c.getString(R.string.translators)));
        modelList.add(new TranslatorObject(
                "https://lh3.googleusercontent.com/-zf-IZfbNHg4/AAAAAAAAAAI/AAAAAAAANfM/-0-pEtFp5a8/s60-p-rw-no/photo.jpg",
                "Federico Cappelletti",
                R.drawable.flag_it,
                "fedec96@gmail.com"));

        // library.
        modelList.add(new CategoryAboutObject(c.getString(R.string.libraries)));
        modelList.add(new LibraryObject(
                c.getString(R.string.retrofit),
                c.getString(R.string.about_retrofit),
                "https://github.com/square/retrofit"));
        modelList.add(new LibraryObject(
                c.getString(R.string.glide),
                c.getString(R.string.about_glide),
                "https://github.com/bumptech/glide"));
        modelList.add(new LibraryObject(
                c.getString(R.string.circular_progress_view),
                c.getString(R.string.about_circular_progress_view),
                "https://github.com/rahatarmanahmed/CircularProgressView"));
        modelList.add(new LibraryObject(
                c.getString(R.string.circle_image_view),
                c.getString(R.string.about_circle_image_view),
                "https://github.com/hdodenhof/CircleImageView"));
        modelList.add(new LibraryObject(
                c.getString(R.string.tagLayout),
                c.getString(R.string.about_tagLayout),
                "https://github.com/hongyangAndroid/FlowLayout"));
        modelList.add(new LibraryObject(
                c.getString(R.string.photo_view),
                c.getString(R.string.about_photo_view),
                "https://github.com/bm-x/PhotoView"));
        modelList.add(new LibraryObject(
                c.getString(R.string.page_indicator),
                c.getString(R.string.about_page_indicator),
                "https://github.com/DavidPacioianu/InkPageIndicator"));
        modelList.add(new LibraryObject(
                c.getString(R.string.greendao_db),
                c.getString(R.string.about_greendao_db),
                "https://github.com/greenrobot/greenDAO"));

        return modelList;
    }
}
