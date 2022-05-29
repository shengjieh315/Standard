package com.shengj.common.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.shengj.common.App;
import com.shengj.common.utils.screen.AutoLayoutConifg;
import com.shengj.common.utils.screen.ScreenAdaptUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 与电话相关功能的工具类
 */
public class PhoneHelper {


    private static PhoneHelper util;

    synchronized public static PhoneHelper getInstance() {
        if (util == null) {
            util = new PhoneHelper();
        }
        return util;

    }

    private PhoneHelper() {
        super();

    }

    /**
     * 生产商家
     *
     * @return String
     */
    public String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获得固件版本
     *
     * @return String
     */
    public String getRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获得手机型号
     *
     * @return String
     */
    public String getModel() {
        return Build.MODEL;
    }


    /**
     * 获得手机型号
     *
     * @return String
     */
    public String getDisplay() {
        return Build.DISPLAY;
    }

    /**
     * 获得手机型号
     *
     * @return String
     */
    public String getID() {
        return Build.ID;
    }

    /**
     * 获得手机型号
     *
     * @return String
     */
    public String getProduct() {
        return Build.PRODUCT;
    }


    /**
     * 获得手机型号
     *
     * @return String
     */
    public String getDevice() {
        return Build.DEVICE;
    }

    /**
     * 获得手机品牌
     *
     * @return String
     */
    public String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机运营商
     */
    public String getSimOperatorName() {
        try {
            TelephonyManager tm = (TelephonyManager) App.getInstance().getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);

            return tm.getSimOperatorName();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获取SIM卡运营商
     *
     * @return 0-未知，1-移动，2-联通，3-电信
     */
    @SuppressLint("MissingPermission")
    public String getOperators() {
        String operator = "0";
        try {
            TelephonyManager tm = (TelephonyManager) App.getInstance().getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String IMSI = tm.getSubscriberId();
            if (IMSI == null || IMSI.equals("")) {
                return operator;
            }
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                operator = "1";//"中国移动";
            } else if (IMSI.startsWith("46001")) {
                operator = "2";//"中国联通";
            } else if (IMSI.startsWith("46003")) {
                operator = "3";//"中国电信";
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return operator;

    }

    /**
     * 得到本机手机号,未安装SIM卡或者SIM卡中未写入手机号，都会获取不到
     *
     * @return String
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getThisPhoneNumber() {
        try {
            TelephonyManager tm = (TelephonyManager) App.getInstance().getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getLine1Number();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return "";


    }


    /**
     * 获取唯一标识
     *
     * @return String
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getIME() {

        String imei = PreferenceUtil.getString("DEVICE_ID", "", App.getInstance().getApplicationContext());

        if (!TextUtils.isEmpty(imei)) {

            return imei;
        }

        try {

            if (PreferenceUtil.getBoolean("READ_PHONE_STATE", true, App.getInstance())) {
                TelephonyManager tm = (TelephonyManager) App.getInstance().getApplicationContext()
                        .getSystemService(Context.TELEPHONY_SERVICE);

                imei = tm.getDeviceId();
                if (!TextUtils.isEmpty(imei)) {
                    try {
                        if (Long.valueOf(imei) == 0) {
                            imei = "";
                        }
                    } catch (Throwable e) {

                    }
                }
            }
        } catch (Throwable e) {
            imei = "";
        }


        if (TextUtils.isEmpty(imei)) {

            try {
                imei = Settings.Secure.getString(App.getInstance().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            } catch (Throwable e) {
            }
        }
        //安卓10及以上，不再支持获取imei，但是各个app还是需要获取一个唯一的设备id，那怎么办？ 工信部牵头搞了个移动安全联盟，里面提供了个方案。主要是让各个手机厂商提供获取方法，他们出个统一sdk方便普通厂商调用。
        //工信部的sdk：http://www.msa-alliance.cn/col.jsp?id=120
        //下面还有两个开源的，也一样能获取各大厂商的设备id方案：
        //https://github.com/gzu-liyujiang/Android_CN_OAID
        //https://github.com/shuzilm-open-source/Get_Oaid_CNAdid
        //
        //实际上，大家都是调用各个厂商提供的接口
        try {
            if (TextUtils.isEmpty(imei)) {
                imei = OAIdManager.getOAId(App.getInstance());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            if (TextUtils.isEmpty(imei)) {
                imei = InfoUtils.getM_Id();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        PreferenceUtil.putString("DEVICE_ID", imei, App.getInstance().getApplicationContext());

        return imei;
    }


    /**
     * 是否是电话号码
     *
     * @param phonenumber String
     * @return boolean
     */
    public boolean isPhoneNumber(String phonenumber) {
//      Pattern pa = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
//      Matcher ma = pa.matcher(phonenumber);
//      return ma.matches();
        return !TextUtils.isEmpty(phonenumber);
    }

    /**
     * 打电话
     *
     * @param phone String
     */
    public void doPhone(String phone) {
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                + phone));
        App.getInstance().getApplicationContext().startActivity(phoneIntent);
    }

    /**
     * 发短信
     *
     * @param phone   String
     * @param content String
     */
    public void doSMS(String phone, String content) {
        Uri uri = null;
        if (!TextUtils.isEmpty(phone))
            uri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", content);
        App.getInstance().getApplicationContext().startActivity(intent);
    }

    /**
     * 得到屏幕信息 getScreenDisplayMetrics().heightPixels 屏幕高
     * getScreenDisplayMetrics().widthPixels 屏幕宽
     *
     * @return DisplayMetrics
     */
    public DisplayMetrics getScreenDisplayMetrics() {
        WindowManager manager = (WindowManager) App.getInstance().getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = manager.getDefaultDisplay();
        display.getMetrics(displayMetrics);

        return displayMetrics;

    }

    /**
     * 屏幕分辨率
     *
     * @return float
     */
    public float getDip() {

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                App.getInstance().getApplicationContext().getResources().getDisplayMetrics());
    }

    /**
     * 状态栏高度
     */
    private int statusBarHeight;

    /**
     * 状态栏高度
     *
     * @return int
     */
    public int getStatusBarHeight() {

        if (statusBarHeight > 0) {
            return statusBarHeight;
        }

        try {
            int resourceId = App.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId == 0) {
                try {
                    Class<?> c = Class.forName("com.android.internal.R$dimen");

                    Object obj = c.newInstance();

                    Field field = c.getField("status_bar_height");

                    resourceId = Integer.parseInt(field.get(obj).toString());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            int sbar = App.getInstance().getResources().getDimensionPixelSize(resourceId);

            float scalePercent = (ScreenAdaptUtil.getsNonCompatDensity() / App.getInstance().getApplicationContext().getResources().getDisplayMetrics().density);
            statusBarHeight = (int) (sbar * scalePercent);

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return statusBarHeight;
    }


    /**
     * 安装apk
     */
    public void install(File file) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(getFileUri(file),
                    "application/vnd.android.package-archive");
            App.getInstance().getApplicationContext().startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public Intent installIntent(File file) {
        Intent intent = new Intent();
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(getFileUri(file),
                    "application/vnd.android.package-archive");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return intent;
    }

    public Uri getFileUri(File file) {

        if (Build.VERSION.SDK_INT >= 24) {

            return FileProvider.getUriForFile(App.getInstance().getApplicationContext(), App.getInstance().getApplicationContext().getPackageName() + ".fileprovider", file);

        } else {
            return Uri.fromFile(file);
        }

    }


    public boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }


    public void gotoMarket(Context context) {

        try {
            String pkg = context.getPackageName();
            gotoMarket(context, pkg);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到应用市场
     *
     * @param context Context
     */
    public void gotoMarket(Context context, String packageName) {
        try {
            Uri uri = Uri.parse("market://details?id=" + packageName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Throwable e) {
            show("no_app_store");
        }
    }


    /**
     * 检测网络是否可用
     *
     * @return boolean
     */
    public boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) App.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo[] allNetworkInfo = connectivityManager.getAllNetworkInfo();
                if (allNetworkInfo != null) {
                    for (NetworkInfo networkInfo : allNetworkInfo) {
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean isActiveNetworkMobile() {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) App.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isActiveNetworkWifi() {

        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) App.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    public @interface Length {
    }

    /**
     * 将Toast放在屏幕
     */
    public void show(@NonNull String message, @Length int time, int gravity) {


        try {
            Toast toast = Toast.makeText(App.getInstance().getApplicationContext(), message, time);
            toast.setGravity(gravity, 0,
                    (AutoLayoutConifg.getInstance().getScreenHeight() / 5));
            toast.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }

    /**
     * 将Toast放在屏幕上方
     */
    public void show(@NonNull String message, @Length int time) {

        try {
            Toast toast = Toast.makeText(App.getInstance().getApplicationContext(), message, time);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }


    public void show(@NonNull String message) {

        show(message, Toast.LENGTH_SHORT);
    }

    public void show(@StringRes int rid) {
        try {
            show(App.getInstance().getApplicationContext().getString(rid));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用浏览器打开
     *
     * @param url String
     */
    public void openWeb(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getInstance().getApplicationContext().startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }

    /**
     * 是否有外存卡
     *
     * @return boolean
     */
    public boolean isExistExternalStore() {

        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 得到sd卡路径
     *
     * @return String
     */
    public String getExternalStorePath() {
        if (isExistExternalStore()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 得到网络类型，0是未知或未连上网络，1为WIFI，2为2g，3为3g，4为4g
     *
     * @return int
     */
    public int getNetType() {


        int type = 1;

        try {
            ConnectivityManager connectMgr = (ConnectivityManager) App.getInstance().getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info == null || !info.isConnected()) {
                return 0;
            }

            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    type = 1;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    type = getNetworkClass(info.getSubtype());
                    break;

                default:
                    type = 0;
                    break;
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }


        return type;
    }

    /**
     * 判断数据连接的类型
     *
     * @param networkType int
     * @return int
     */
    public int getNetworkClass(int networkType) {


        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:

                return 2;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return 3;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return 4;
            default:
                return 0;
        }
    }

    /**
     * 开始震动
     *
     * @param context Context
     * @param repeat  0重复 -1不重复
     * @param pattern long
     */
    @SuppressLint("NewApi")
    public synchronized void doVibrate(
            Context context, int repeat,
            long... pattern) {

        if (pattern == null) {
            pattern = new long[]{1000, 1000, 1000};
        }
        Vibrator mVibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator.hasVibrator()) {
            mVibrator.vibrate(pattern, repeat);
        }

    }

    /**
     * 建立一个NotificationCompat.Builder，并返回
     */
    public NotificationCompat.Builder getNotifyBuilder(
            String contentTitle, String contentText,
            String contentInfo, Bitmap largeIcon, int smallIcon,
            Class contentclass,
            PendingIntent deleteIntent, int defaults) {
        PendingIntent pendingIntent = PendingIntent.getActivity(App.getInstance().getApplicationContext(), 0,
                new Intent(), 0);
        if (contentclass != null) {
            pendingIntent = PendingIntent.getActivity(App.getInstance().getApplicationContext(), 0, new Intent(
                    App.getInstance().getApplicationContext(), contentclass), 0);
        }
        return new NotificationCompat.Builder(App.getInstance().getApplicationContext())
                .setLargeIcon(largeIcon).setSmallIcon(smallIcon)
                .setContentInfo(contentInfo).setContentTitle(contentTitle)
                .setContentText(contentText).setAutoCancel(true)
                .setContentIntent(pendingIntent).setDeleteIntent(deleteIntent)
                .setDefaults(defaults);


    }

    /**
     * 建一个带有ProgressBar的NotificationCompat.Builder，并返回
     */
    public NotificationCompat.Builder getNotifyBuilderProgress(
            String contentTitle,
            String contentText, String contentInfo, Bitmap largeIcon,
            int smallIcon, Class contentclass, int max, int progress,
            boolean indeterminate,
            PendingIntent deleteIntent, int defaults) {
        NotificationCompat.Builder builder = getNotifyBuilder(contentTitle, contentText,
                contentInfo, largeIcon, smallIcon, contentclass, deleteIntent, defaults);

        builder.setProgress(max, progress, indeterminate);


        return builder;

    }

    /**
     * 普通的Notification
     */
    public void showNotifyNormal(NotificationCompat.Builder builder, int notifyId) {

        Notification notification = builder.build();

        NotificationManager manager = (NotificationManager) App.getInstance().getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notifyId, notification);

    }

    /**
     * 大布局Notification
     */
    public Notification showNotifyBigView(
            NotificationCompat.Style style,
            NotificationCompat.Builder builder, int notifyId) {
        builder.setStyle(style);

        Notification notification = builder.build();

        NotificationManager manager = (NotificationManager) App.getInstance().getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notifyId, notification);
        return notification;
    }

    /**
     * BigTextStyle风格的Notification
     */
    public Notification showNotifyBigText(
            String bigContentTitle, String summaryText,
            String bigText, NotificationCompat.Builder builder, int notifyId) {

        NotificationCompat.BigTextStyle textStyle = new NotificationCompat.BigTextStyle();
        textStyle.setBigContentTitle(bigContentTitle)
                .setSummaryText(summaryText).bigText(bigText);
        return showNotifyBigView(textStyle, builder, notifyId);
    }

    /**
     * BigPictureStyle风格的Notification
     */
    public void showNotifyBigPic(
            String bigContentTitle, String summaryText,
            Bitmap bigPicture, NotificationCompat.Builder builder, int notifyId) {

        NotificationCompat.BigPictureStyle pictureStyle = new NotificationCompat.BigPictureStyle();
        pictureStyle.setBigContentTitle(bigContentTitle)
                .setSummaryText(summaryText).bigPicture(bigPicture);
        showNotifyBigView(pictureStyle, builder, notifyId);
    }

    /**
     * InboxStyle风格的Notification
     */
    public void showNotifyBigInbox(
            String bigContentTitle, String summaryText,
            String[] line, NotificationCompat.Builder builder, int notifyId) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(bigContentTitle).setSummaryText(
                summaryText);
        for (int i = 0; i < line.length; i++) {
            inboxStyle.addLine(line[i]);
        }

        showNotifyBigView(inboxStyle, builder, notifyId);
    }

    /**
     * 自定义Notification
     */
    public void showNotifyCustomView(
            int layoutId, Class contentclass,
            int smallIcon, Class clickCla, int clickId, int notifyId) {
        NotificationManager manager = (NotificationManager) App.getInstance().getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews remoteViews = new RemoteViews(App.getInstance().getApplicationContext().getPackageName(),
                layoutId);

        if (clickCla != null) {
            Intent intent = new Intent(App.getInstance().getApplicationContext(), clickCla);
            PendingIntent pendingIntent = PendingIntent.getActivity(App.getInstance().getApplicationContext(), 0,
                    intent, 0);
            remoteViews.setOnClickPendingIntent(clickId, pendingIntent);
        }

        NotificationCompat.Builder builder = getNotifyBuilder("", "", "", null, smallIcon,
                contentclass, null, Notification.DEFAULT_ALL).setContent(remoteViews);
        manager.notify(notifyId, builder.build());


    }


    /**
     * Retrieves application's version number from the manifest
     *
     * @return String
     */
    private static String version = "";

    public String getVersion() {
        if (!TextUtils.isEmpty(version)) {
            return version;
        }

        try {
            version = "1.0.0";
            PackageManager packageManager = App.getInstance().getApplicationContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    App.getInstance().getApplicationContext().getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * Retrieves application's version number from the manifest
     *
     * @return long
     */
    public long getVersionCode() {
        long version = 0;

        PackageManager packageManager = App.getInstance().getApplicationContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    App.getInstance().getApplicationContext().getPackageName(), 0);
            version = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * dp转px
     *
     * @param dpValue float
     * @return int
     */
    public int dp2Px(float dpValue) {


        final float scale = ScreenAdaptUtil.getNewDensity();
        return (int) (dpValue * scale + 0.5f);

//      return (int (dpValue * 2));
    }

    /**
     * px转dp
     */
    public int px2Dp(float pxValue) {
        final float scale = ScreenAdaptUtil.getNewDensity();

        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param spValue float
     * @return int
     */
    public int sp2Px(float spValue) {
        final float fontScale = ScreenAdaptUtil.getNewScaledDensity();
        return (int) (spValue * fontScale + 0.5f);
    }

    public void saveFile(byte[] b, File ret) throws IOException {
        InputStream instream = new ByteArrayInputStream(b);

        FileOutputStream buffer = new FileOutputStream(ret, true);

        try {
            byte[] tmp = new byte[4096];
            int l, count = 0;
            // do not send messages if request has been cancelled
            while ((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                count += l;
                buffer.write(tmp, 0, l);

            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            instream.close();
            buffer.flush();
            buffer.close();
        }

    }


    public File getFileFromBytes(byte[] b, File ret) {

        BufferedOutputStream stream = null;
        try {

            FileOutputStream fstream = new FileOutputStream(ret);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);


        } catch (Exception e) {
            //            log.error("helper:get file from byte process error!");
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    //                    log.error("helper:get file from byte process error!");
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }


    /**
     * @param context  Context
     * @param filePath String
     * @return int  0 不完整 1完整但包名错误 2 完整且正确
     */
    public int isApkCanInstall(Context context, String filePath) {
        int result = 0;
        try {
            PackageManager pm = context.getPackageManager();

            PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);

            if (info != null && !TextUtils.isEmpty(info.packageName)) {

                if (info.packageName.equals(context.getPackageName())) {
                    result = 2;//完整
                } else {
                    result = 1;//完整
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 是否是合法的密码
     *
     * @param pwd String
     * @return boolean
     */
    public boolean isLegalPwd(String pwd) {
        //匹配英文符号((?=[\x21-\x7e]+)[^A-Za-z0-9])
        Pattern symbolPa = Pattern.compile("[^\\da-zA-Z\\u4e00-\\u9fa5]");//非数字、字母、中文
        Pattern numPa = Pattern.compile("\\d");
        Pattern letterPa = Pattern.compile("[a-zA-Z]");
        int localInt = 0;
        Matcher symbolMa = symbolPa.matcher(pwd);
        Matcher numMa = numPa.matcher(pwd);
        Matcher letterMa = letterPa.matcher(pwd);
        if (symbolMa.find()) {
            localInt++;
        }
        if (numMa.find()) {
            localInt++;
        }
        if (letterMa.find()) {
            localInt++;
        }
        return localInt >= 2;
    }

    /**
     * 是否是邮箱
     *
     * @param email String
     * @return boolean
     */
    public boolean isEmail(String email) {
        Pattern pa = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );
        Matcher ma = pa.matcher(email);
        return ma.matches();
    }

    /**
     * @param context Context
     * @param apkPath String
     * @return String  包名
     */
    public String getApkFilePackageName(Context context, String apkPath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                    PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                appInfo.sourceDir = apkPath;
                appInfo.publicSourceDir = apkPath;
                return appInfo.packageName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean isInstallApp(String pkgName) {
        try {
            final PackageManager packageManager = App.getInstance().getPackageManager();// 获取packagemanager
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName.toLowerCase(Locale.ENGLISH);
                    if (pn.equals(pkgName)) {
                        return true;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }


}
