package com.qoovers.snapwhyb.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.qoovers.snapwhyb.R;

public class Device
{
    public static boolean isAtLeastMarshmallow() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static int convertDpToParamValue(Context context, int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int getHeightFromAspectRatio(Activity activity, Context context, double aspectRatio) {
        DisplayMetrics met = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(met);
        int t = context.getResources().getDimensionPixelSize(R.dimen.margin_3);
        int width = (met.widthPixels-2*t);
        int height = (int)(width * aspectRatio);

        return height;
    }

    public static int getGridHeight(Activity activity, Context context, double aspectRatio, int columns) {
        DisplayMetrics met = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(met);
        int t = context.getResources().getDimensionPixelSize(R.dimen.margin_3);
        int width = (met.widthPixels-2*t) / columns;
        int height = (int)(width * aspectRatio);

        return height;
    }

    public static int getWidth(Activity activity, Context context) {
        DisplayMetrics met = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(met);
        int t = context.getResources().getDimensionPixelSize(R.dimen.margin_3);
        int width = (met.widthPixels-2*t);

        return width;
    }

    public static int getHeight(Activity activity, Context context) {
        DisplayMetrics met = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(met);
        int t = context.getResources().getDimensionPixelSize(R.dimen.margin_3);
        int height = (met.heightPixels-2*t);

        return height;
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }

    public static int getToolBarHeight(Context context) {
        int[] attrs = new int[] {R.attr.actionBarSize};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();

        return toolBarHeight;
    }
}
