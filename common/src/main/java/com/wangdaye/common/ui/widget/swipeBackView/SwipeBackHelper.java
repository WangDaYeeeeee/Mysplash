package com.wangdaye.common.ui.widget.swipeBackView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;

import com.wangdaye.common.R;
import com.wangdaye.common.utils.AnimUtils;

import java.io.ByteArrayOutputStream;

/**
 * {@link SwipeBackActivity}.
 * */
public class SwipeBackHelper {

    private ViewGroup currentContentView;
    private View backgroundView;
    private boolean prepared;

    SwipeBackHelper() {
        currentContentView = null;
        backgroundView = null;
        prepared = false;
    }

    void prepareViews(SwipeBackActivity currentActivity) {
        if (prepared) {
            return;
        }
        prepared = true;

        currentContentView = currentActivity.findViewById(Window.ID_ANDROID_CONTENT);
        backgroundView = LayoutInflater.from(currentActivity).inflate(
                R.layout.container_swipe_back_background, currentContentView, false);
        currentContentView.addView(backgroundView, 0);

        AnimUtils.alphaInitShow(backgroundView, 400);
    }

    void clearViews() {
        if (!prepared) {
            return;
        }
        prepared = false;

        if (currentContentView != null && backgroundView != null) {
            currentContentView.removeView(backgroundView);
        }

        currentContentView = null;
        backgroundView = null;
    }

    @Nullable
    public static Bitmap getViewSnapshot(View view) {
        try {
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] bytes = stream.toByteArray();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            return blur(
                    view.getContext(),
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options),
                    25f / 8f
            );
        } catch (Exception e) {
            return null;
        }
    }

    private static Bitmap blur(Context context, Bitmap source, float radius){
        RenderScript renderScript =  RenderScript.create(context);

        final Allocation input = Allocation.createFromBitmap(renderScript, source);
        final Allocation output = Allocation.createTyped(renderScript,input.getType());

        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(
                renderScript,
                Element.U8_4(renderScript)
        );
        scriptIntrinsicBlur.setInput(input);
        scriptIntrinsicBlur.setRadius(radius);
        scriptIntrinsicBlur.forEach(output);
        output.copyTo(source);
        renderScript.destroy();

        return source;
    }
}
