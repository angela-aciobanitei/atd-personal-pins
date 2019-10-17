package com.ang.acb.personalpins.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class UiUtils {

    // See: https://stackoverflow.com/questions/13600802/android-convert-dp-to-float
    public static float dipToPixels(Context context, float dipValue){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  dipValue, metrics);
    }
}
