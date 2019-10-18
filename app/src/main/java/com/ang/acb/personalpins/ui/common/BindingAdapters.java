package com.ang.acb.personalpins.ui.common;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.utils.GlideApp;

/**
 * Binding adapters are responsible for making the appropriate framework calls to set values.
 *
 * See: https://developer.android.com/topic/libraries/data-binding/binding-adapters
 * See: https://github.com/googlesamples/android-sunflower
 */
public class BindingAdapters {

    @BindingAdapter("toggleVisibility")
    public static void toggleVisibility(View view, Boolean isVisible) {
        if (isVisible) view.setVisibility(View.VISIBLE);
        else view.setVisibility(View.GONE);
    }

    @BindingAdapter({"imageUri"})
    // See: https://stackoverflow.com/questions/32332003/glide-load-local-image-by-uri
    public static void bindImage(ImageView imageView, String imageUri) {
        GlideApp.with(imageView.getContext())
                .load(R.color.colorAccent)
                .placeholder(R.color.colorAccent)
                .into(imageView);
    }
}
