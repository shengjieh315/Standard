package com.shengj.common.utils.screen;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 * ================================================
 * 作    者：FANGYI <87649669@qq.com>
 * 版    本：1.0.0
 * 日    期：2018/5/28
 * 说    明：
 * ================================================
 */
public class ScreenAdaptUtil {
    private static final int STANDER_SCREEN_WIDTH_IN_DP = 375;

    private static float sNonCompatDensity;
    private static float sNonCompatScaleDensity;


    private static float oldDensity;
    private static float oldScaledDensity;
    private static int oldDensityDpi;


    private static float newDensity;
    private static float newScaledDensity;
    private static int newDensityDpi;


    public static float getsNonCompatDensity() {
        return sNonCompatDensity;
    }

    public static float getOldDensity() {
        return oldDensity;
    }

    public static int getOldDensityDpi() {
        return oldDensityDpi;
    }

    public static float getNewDensity() {
        return newDensity;
    }

    public static float getNewScaledDensity() {
        return newScaledDensity;
    }

    public static int getNewDensityDpi() {
        return newDensityDpi;
    }

    public static float getOldScaledDensity() {
        return oldScaledDensity;
    }

    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    public static void setCustomDensity(Activity activity, final Application application) {

        try{
            final DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();

            if (sNonCompatDensity == 0) {

                oldDensity = appDisplayMetrics.density;
                oldScaledDensity = appDisplayMetrics.scaledDensity;
                oldDensityDpi = appDisplayMetrics.densityDpi;

//                KLog.e("oldDensity"+appDisplayMetrics.density);
//                KLog.e("oldScaledDensity"+appDisplayMetrics.scaledDensity);
//                KLog.e("oldDensityDpi"+appDisplayMetrics.densityDpi);

                sNonCompatDensity = appDisplayMetrics.density;
                sNonCompatScaleDensity = appDisplayMetrics.scaledDensity;
                application.registerComponentCallbacks(new ComponentCallbacks() {
                    @Override
                    public void onConfigurationChanged(Configuration newConfig) {
                        if (newConfig != null && newConfig.fontScale > 0) {
                            sNonCompatScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                        }
                        AutoLayoutConifg.getInstance().reset();
                    }

                    @Override
                    public void onLowMemory() {

                    }
                });
            }


            int width = AutoLayoutConifg.getInstance().getScreenWidth();

            if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                width = activity.getResources().getDisplayMetrics().widthPixels;
            }

            final float targetDensity = (float) width / STANDER_SCREEN_WIDTH_IN_DP;
//            final float targetScaleDensity = targetDensity * (sNonCompatScaleDensity / sNonCompatDensity);
            final int targetDensityDpi = (int) (160 * targetDensity);

            appDisplayMetrics.density = targetDensity;
            appDisplayMetrics.scaledDensity = targetDensity;
            appDisplayMetrics.densityDpi = targetDensityDpi;
//            KLog.e("appDisplayMetrics.density"+appDisplayMetrics.density);
//            KLog.e("appDisplayMetrics.scaledDensity"+appDisplayMetrics.scaledDensity);
//            KLog.e("appDisplayMetrics.densityDpi"+appDisplayMetrics.densityDpi);

            final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();


            activityDisplayMetrics.density = targetDensity;
            activityDisplayMetrics.scaledDensity = targetDensity;
            activityDisplayMetrics.densityDpi = targetDensityDpi;



            if(newDensity==0){
                newDensity = targetDensity;
                newScaledDensity = targetDensity;
                newDensityDpi = targetDensityDpi;
            }
        }catch (Throwable e){
            e.printStackTrace();
        }


    }


    public static float getDensityRatio(){

        return ScreenAdaptUtil.getOldDensity() / ScreenAdaptUtil.getNewDensity();
    }

}