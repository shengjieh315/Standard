package com.shengj.common.utils.screen;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.shengj.common.App;


public class AutoLayoutConifg {

    private static AutoLayoutConifg sIntance = new AutoLayoutConifg();

    private int mScreenWidth;
    private int mScreenHeight;

    private AutoLayoutConifg() {

        int[] screenSize = getScreenSize();
        if(screenSize[0]<screenSize[1]){
            mScreenWidth = screenSize[0];
            mScreenHeight = screenSize[1];
        }else{
            mScreenWidth = screenSize[1];
            mScreenHeight = screenSize[0];
        }

    }
    public void reset(){
        int[] screenSize = getScreenSize();
        if(screenSize[0]<screenSize[1]){
            mScreenWidth = screenSize[0];
            mScreenHeight = screenSize[1];
        }else{
            mScreenWidth = screenSize[1];
            mScreenHeight = screenSize[0];
        }

    }

    public int getMax(){
        return Math.max(mScreenWidth,mScreenHeight);
    }


    public static AutoLayoutConifg getInstance() {
        return sIntance;
    }


    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }


    public int[] getScreenSize() {

        int[] size = new int[2];
        int widthPixels = 0;
        int heightPixels = 0;
        Display d = null;
        try {
            WindowManager w = (WindowManager) App.getInstance().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            d = w.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            d.getMetrics(metrics);

            widthPixels = metrics.widthPixels;
            heightPixels = metrics.heightPixels;
        } catch (Throwable e) {
            e.printStackTrace();
        }


        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Throwable ignored) {
            }
        }

        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Throwable ignored) {
            }
        }


        size[0] = widthPixels;
        size[1] = heightPixels;
        return size;
    }

}
