package com.wangdaye.mysplash.photo.view.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.data.entity.PhotoDetails;
import com.wangdaye.mysplash._common.i.model.LoadModel;
import com.wangdaye.mysplash._common.i.model.PhotoDetailsModel;
import com.wangdaye.mysplash._common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash._common.i.presenter.PhotoDetailsPresenter;
import com.wangdaye.mysplash._common.i.view.LoadView;
import com.wangdaye.mysplash._common.ui.adapter.TagAdapter;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash.photo.model.widget.LoadObject;
import com.wangdaye.mysplash.photo.model.widget.PhotoDetailsObject;
import com.wangdaye.mysplash.photo.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.photo.presenter.widget.PhotoDetailsImplementor;
import com.zhy.view.flowlayout.TagFlowLayout;

/**
 * Photo details view.
 * */

public class PhotoDetailsView extends FrameLayout
        implements com.wangdaye.mysplash._common.i.view.PhotoDetailsView, LoadView,
        View.OnClickListener {
    // model.
    private PhotoDetailsModel photoDetailsModel;
    private LoadModel loadModel;

    // view.
    private CircularProgressView progressView;

    private RelativeLayout detailsContainer;
    private TextView sizeText;
    private TextView colorText;
    private TextView locationText;
    private TextView modelText;
    private TextView exposureText;
    private TextView apertureText;
    private TextView focalText;
    private TextView isoText;

    private FrameLayout colorSample;

    private TagFlowLayout tagView;

    // presenter.
    private PhotoDetailsPresenter photoDetailsPresenter;
    private LoadPresenter loadPresenter;

    /** <br> life cycle. */

    public PhotoDetailsView(Context context) {
        super(context);
        this.initialize();
    }

    public PhotoDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public PhotoDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhotoDetailsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    @SuppressLint("InflateParams")
    private void initialize() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_details, null);
        addView(v);

        initView();
    }

    public void initMP(Photo p) {
        initModel(p);
        initPresenter();
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.photoDetailsPresenter = new PhotoDetailsImplementor(photoDetailsModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
    }

    /** <br> view. */

    private void initView() {
        this.progressView = (CircularProgressView) findViewById(R.id.container_photo_details_progressView);
        progressView.setVisibility(VISIBLE);

        this.detailsContainer = (RelativeLayout) findViewById(R.id.container_photo_details_detailsContainer);
        detailsContainer.setVisibility(GONE);

        if (Mysplash.getInstance().isLightTheme()) {
            ((ImageView) findViewById(R.id.container_photo_details_sizeIcon)).setImageResource(R.drawable.ic_size_light);
            ((ImageView) findViewById(R.id.container_photo_details_colorIcon)).setImageResource(R.drawable.ic_color_light);
            ((ImageView) findViewById(R.id.container_photo_details_locationIcon)).setImageResource(R.drawable.ic_location_light);
            ((ImageView) findViewById(R.id.container_photo_details_modelIcon)).setImageResource(R.drawable.ic_camera_light);
            ((ImageView) findViewById(R.id.container_photo_details_exposureIcon)).setImageResource(R.drawable.ic_exposure_light);
            ((ImageView) findViewById(R.id.container_photo_details_apertureIcon)).setImageResource(R.drawable.ic_aperture_light);
            ((ImageView) findViewById(R.id.container_photo_details_focalIcon)).setImageResource(R.drawable.ic_focal_light);
            ((ImageView) findViewById(R.id.container_photo_details_isoIcon)).setImageResource(R.drawable.ic_iso_light);
        } else {
            ((ImageView) findViewById(R.id.container_photo_details_sizeIcon)).setImageResource(R.drawable.ic_size_dark);
            ((ImageView) findViewById(R.id.container_photo_details_colorIcon)).setImageResource(R.drawable.ic_color_dark);
            ((ImageView) findViewById(R.id.container_photo_details_locationIcon)).setImageResource(R.drawable.ic_location_dark);
            ((ImageView) findViewById(R.id.container_photo_details_modelIcon)).setImageResource(R.drawable.ic_camera_dark);
            ((ImageView) findViewById(R.id.container_photo_details_exposureIcon)).setImageResource(R.drawable.ic_exposure_dark);
            ((ImageView) findViewById(R.id.container_photo_details_apertureIcon)).setImageResource(R.drawable.ic_aperture_dark);
            ((ImageView) findViewById(R.id.container_photo_details_focalIcon)).setImageResource(R.drawable.ic_focal_dark);
            ((ImageView) findViewById(R.id.container_photo_details_isoIcon)).setImageResource(R.drawable.ic_iso_dark);
        }

        this.sizeText = (TextView) findViewById(R.id.container_photo_details_sizeTxt);
        DisplayUtils.setTypeface(getContext(), sizeText);

        this.colorText = (TextView) findViewById(R.id.container_photo_details_colorTxt);
        DisplayUtils.setTypeface(getContext(), colorText);

        this.locationText = (TextView) findViewById(R.id.container_photo_details_locationTxt);
        DisplayUtils.setTypeface(getContext(), locationText);

        this.modelText = (TextView) findViewById(R.id.container_photo_details_modelTxt);
        DisplayUtils.setTypeface(getContext(), modelText);

        this.exposureText = (TextView) findViewById(R.id.container_photo_details_exposureTxt);
        DisplayUtils.setTypeface(getContext(), exposureText);

        this.apertureText = (TextView) findViewById(R.id.container_photo_details_apertureTxt);
        DisplayUtils.setTypeface(getContext(), apertureText);

        this.focalText = (TextView) findViewById(R.id.container_photo_details_focalTxt);
        DisplayUtils.setTypeface(getContext(), focalText);

        this.isoText = (TextView) findViewById(R.id.container_photo_details_isoTxt);
        DisplayUtils.setTypeface(getContext(), isoText);

        this.colorSample = (FrameLayout) findViewById(R.id.container_photo_details_colorSample);

        findViewById(R.id.container_photo_details_sizeContainer).setOnClickListener(this);
        findViewById(R.id.container_photo_details_colorContainer).setOnClickListener(this);
        findViewById(R.id.container_photo_details_locationContainer).setOnClickListener(this);
        findViewById(R.id.container_photo_details_modelContainer).setOnClickListener(this);
        findViewById(R.id.container_photo_details_exposureContainer).setOnClickListener(this);
        findViewById(R.id.container_photo_details_apertureContainer).setOnClickListener(this);
        findViewById(R.id.container_photo_details_focalContainer).setOnClickListener(this);
        findViewById(R.id.container_photo_details_isoContainer).setOnClickListener(this);

        this.tagView = (TagFlowLayout) findViewById(R.id.container_photo_details_tagView);
    }

    /** <br> model. */

    // init.

    private void initModel(Photo p) {
        this.photoDetailsModel = new PhotoDetailsObject(p);
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
    }

    // interface.

    public void requestPhotoDetails() {
        photoDetailsPresenter.requestPhotoDetails(getContext());
    }

    public void cancelRequest() {
        photoDetailsPresenter.cancelRequest();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_photo_details_sizeContainer:
                photoDetailsPresenter.showExifDescription(
                        getContext(),
                        getContext().getString(R.string.feedback_size),
                        sizeText.getText().toString());
                break;

            case R.id.container_photo_details_colorContainer:
                photoDetailsPresenter.showExifDescription(
                        getContext(),
                        getContext().getString(R.string.feedback_color),
                        colorText.getText().toString());
                break;

            case R.id.container_photo_details_locationContainer:
                photoDetailsPresenter.showExifDescription(
                        getContext(),
                        getContext().getString(R.string.feedback_location),
                        locationText.getText().toString());
                break;

            case R.id.container_photo_details_modelContainer:
                photoDetailsPresenter.showExifDescription(
                        getContext(),
                        getContext().getString(R.string.feedback_model),
                        modelText.getText().toString());
                break;

            case R.id.container_photo_details_exposureContainer:
                photoDetailsPresenter.showExifDescription(
                        getContext(),
                        getContext().getString(R.string.feedback_exposure),
                        exposureText.getText().toString());
                break;

            case R.id.container_photo_details_apertureContainer:
                photoDetailsPresenter.showExifDescription(
                        getContext(),
                        getContext().getString(R.string.feedback_aperture),
                        apertureText.getText().toString());
                break;

            case R.id.container_photo_details_focalContainer:
                photoDetailsPresenter.showExifDescription(
                        getContext(),
                        getContext().getString(R.string.feedback_focal),
                        focalText.getText().toString());
                break;

            case R.id.container_photo_details_isoContainer:
                photoDetailsPresenter.showExifDescription(
                        getContext(),
                        getContext().getString(R.string.feedback_iso),
                        isoText.getText().toString());
                break;

        }
    }

    // view.

    // photo details view.

    @Override
    public void drawExif(PhotoDetails details) {
        String text;

        text = details.width + " × " + details.height;
        sizeText.setText(text);

        text = details.color;
        colorText.setText(text);

        if (details.location == null
                || (details.location.city == null && details.location.country == null)) {
            text = "Unknown";
        } else {
            text = details.location.city == null ? "" : details.location.city + ", ";
            text = text + (details.location.country == null ? "" : details.location.country);
        }
        locationText.setText(text);

        modelText.setText(details.exif.model == null ? "Unknown" : details.exif.model);

        exposureText.setText(details.exif.exposure_time == null ? "Unknown" : details.exif.exposure_time);

        apertureText.setText(details.exif.aperture == null ? "Unknown" : details.exif.aperture);

        focalText.setText(details.exif.focal_length == null ? "Unknown" : details.exif.focal_length);

        isoText.setText(details.exif.iso == 0 ? "Unknown" : String.valueOf(details.exif.iso));

        colorSample.setBackground(new ColorDrawable(Color.parseColor(details.color)));

        tagView.setAdapter(new TagAdapter(details.categories));
    }

    @Override
    public void initRefreshStart() {
        loadPresenter.setLoadingState();
    }

    @Override
    public void requestDetailsSuccess() {
        loadPresenter.setNormalState();
    }

    // load view.

    @Override
    public void animShow(final View v) {
        AnimUtils.animShow(v);
    }

    @Override
    public void animHide(final View v) {
        AnimUtils.animHide(v);
    }

    @Override
    public void setLoadingState() {
        animShow(progressView);
        animHide(detailsContainer);
    }

    @Override
    public void setFailedState() {
        // do nothing.
    }

    @Override
    public void setNormalState() {
        animShow(detailsContainer);
        animHide(progressView);
    }

    @Override
    public void resetLoadingState() {
        // do nothing.
    }
}
