package com.wangdaye.mysplash.about;

import android.content.Context;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.about.model.AboutModel;
import com.wangdaye.mysplash.about.model.AppObject;
import com.wangdaye.mysplash.about.model.CategoryObject;
import com.wangdaye.mysplash.about.model.HeaderObject;
import com.wangdaye.mysplash.about.model.LibraryObject;
import com.wangdaye.mysplash.about.model.TranslatorObject;
import com.wangdaye.mysplash.about.ui.AboutAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Create about model presenter.
 *
 * This implementor is used to build a list by {@link AboutModel}. This list will provide data for
 * {@link AboutAdapter}.
 *
 * */

public class CreateAboutModelPresenter {

    public static List<AboutModel> createModelList(Context c) {
        List<AboutModel> modelList = new ArrayList<>();

        // header.
        modelList.add(new HeaderObject());

        // about app.
        modelList.add(new CategoryObject(c.getString(R.string.about_app)));
        modelList.add(new AppObject(
                1,
                R.drawable.ic_book,
                c.getString(R.string.introduce)));
        modelList.add(new AppObject(
                2,
                R.drawable.ic_github,
                c.getString(R.string.gitHub)));
        modelList.add(new AppObject(
                3,
                R.drawable.ic_email,
                c.getString(R.string.email)));
        modelList.add(new AppObject(
                4,
                R.drawable.ic_android_studio,
                c.getString(R.string.source_code)));
        modelList.add(new AppObject(
                5,
                R.drawable.ic_gift,
                c.getString(R.string.donate)));

        // translator.
        modelList.add(new CategoryObject(c.getString(R.string.translators)));
        modelList.add(new TranslatorObject(
                "https://lh3.googleusercontent.com/-zf-IZfbNHg4/AAAAAAAAAAI/AAAAAAAANfM/-0-pEtFp5a8/s60-p-rw-no/photo.jpg",
                "Federico Cappelletti",
                R.drawable.flag_it,
                "fedec96@gmail.com"));
        modelList.add(new TranslatorObject(
                "https://lh3.googleusercontent.com/3fnKvLj0v3uhsxzSDBwUBzN-ppW9LbaAi7opuGqav7QEIDd5Kl0Fm5GYTPX6oQ-wSb_9FFo7PD2WPg=w1920-h1080-rw-no",
                "Mehmet Saygin Yilmaz",
                R.drawable.flag_tr,
                "memcos@gmail.com"));
        modelList.add(new TranslatorObject(
                "https://avatars2.githubusercontent.com/u/22666602?v=3&s=460",
                "OfficialMITX",
                R.drawable.flag_de,
                "https://github.com/OffifialMITX"));
        modelList.add(new TranslatorObject(
                "https://avatars0.githubusercontent.com/u/3891063?v=3&s=400",
                "Alex",
                R.drawable.flag_ru,
                "https://github.com/Ulop"));
        modelList.add(new TranslatorObject(
                "https://ssl.gstatic.com/bt/C3341AA7A1A076756462EE2E5CD71C11/avatars/avatar_tile_s_80.png",
                "Sergio Otón",
                R.drawable.flag_es,
                "oton.translator@gmail.com"));
        modelList.add(new TranslatorObject(
                "https://avatars2.githubusercontent.com/u/8462938?v=3&s=460",
                "naofum",
                R.drawable.flag_ja,
                "https://github.com/naofum"));
        modelList.add(new TranslatorObject(
                "https://avatars2.githubusercontent.com/u/14093922?v=3&s=460",
                "Valentin Dumont",
                R.drawable.flag_fr,
                "https://github.com/valentind44"));
        modelList.add(new TranslatorObject(
                "https://avatars1.githubusercontent.com/u/22525368?v=3&s=400",
                "mueller-ma",
                R.drawable.flag_de,
                "https://github.com/mueller-ma"));
        modelList.add(new TranslatorObject(
                "https://lh3.googleusercontent.com/-G62rc78uq2Q/AAAAAAAAAAI/AAAAAAAAB14/IDC70nBA63U/s128-p-k-rw-no/photo.jpg",
                "Saksham Barsaiyan",
                R.drawable.flag_hi,
                "https://plus.google.com/+SakshamBarsaiyan"));

        // library.
        modelList.add(new CategoryObject(c.getString(R.string.libraries)));
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
        modelList.add(new LibraryObject(
                c.getString(R.string.butter_knife),
                c.getString(R.string.about_butter_knife),
                "https://github.com/JakeWharton/butterknife"));
        modelList.add(new LibraryObject(
                c.getString(R.string.number_anim_text_view),
                c.getString(R.string.about_number_anim_text_view),
                "https://github.com/Bakumon/NumberAnimTextView"));
        modelList.add(new LibraryObject(
                c.getString(R.string.file_downloader),
                c.getString(R.string.about_file_downloader),
                "https://github.com/lingochamp/FileDownloader"));
        modelList.add(new LibraryObject(
                c.getString(R.string.dagger),
                c.getString(R.string.about_dagger),
                "https://github.com/google/dagger"));
        modelList.add(new LibraryObject(
                c.getString(R.string.rx_java),
                c.getString(R.string.about_rx_java),
                "https://github.com/ReactiveX/RxJava"));
        modelList.add(new LibraryObject(
                c.getString(R.string.rx_android),
                c.getString(R.string.about_rx_android),
                "https://github.com/ReactiveX/RxAndroid"));
        modelList.add(new LibraryObject(
                c.getString(R.string.rx_lifecycle),
                c.getString(R.string.about_rx_lifecycle),
                "https://github.com/zhihu/RxLifecycle"));

        return modelList;
    }
}
