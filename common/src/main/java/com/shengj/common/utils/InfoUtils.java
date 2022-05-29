package com.shengj.common.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.shengj.common.App;
import com.socks.library.KLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * 本类中带Info字样的方法是返回字符串
 * 否则返回具体数据
 */

public class InfoUtils {

    private static boolean isGetOAID;

    public static void initPhoneInfo() {
        A_ID = EasyAES.encryptString(getAId());
        D_ID = EasyAES.encryptString(getDID());
        M_ID = EasyAES.encryptString(getMacFromHardware(App.getInstance()));

        DEVICE_ID = EasyAES.encryptString(Utils.getDeviceId());

        if (!TextUtils.isEmpty(DEVICE_ID)) {
            DEVICE_ID = DEVICE_ID.trim();
        } else {
            DEVICE_ID = "";
        }
        if (!TextUtils.isEmpty(A_ID)) {
            A_ID = A_ID.trim();
        }

        if (!TextUtils.isEmpty(D_ID)) {
            D_ID = D_ID.trim();
        }
        if (!TextUtils.isEmpty(M_ID)) {
            M_ID = M_ID.trim();
        }


    }


    /**
     * 获取传感器信息
     *
     * @param sensorManager
     * @return
     */
    public static String getSensorInfo(SensorManager sensorManager) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Sensor> sensorList = sensorManager.getSensorList(SensorManager.SENSOR_STATUS_NO_CONTACT);
        stringBuilder.append("传感器数量：" + sensorList.size() + "\n");
        for (int i = 0; i < sensorList.size(); i++) {
            Sensor sensor = sensorList.get(i);

            stringBuilder.append("> " + sensor.getName() + "(" + sensor.getType() + ")" + "\n");

        }

        return stringBuilder.toString();
    }


    /**
     * 获取传感器信息,用‘|’隔开
     *
     * @param sensorManager
     * @return
     */
    public static String getSensorInfoFormat(SensorManager sensorManager) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Sensor> sensorList = sensorManager.getSensorList(SensorManager.SENSOR_STATUS_NO_CONTACT);
        for (int i = 0; i < sensorList.size(); i++) {

            Sensor sensor = sensorList.get(i);

            if (i == sensorList.size() - 1) {
                //最后一项后不加‘|’
                stringBuilder.append(sensor.getName());
            } else {
                stringBuilder.append(sensor.getName() + "|");
            }
        }

        return stringBuilder.toString();
    }


    /**
     * 获取JVM prop信息
     *
     * @return
     */
    public static String getJVMPropInfo() {
        Map<String, String> map = getJVMProp();
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            stringBuilder.append(entry.getKey() + ":" + entry.getValue() + "\n");
        }
        return stringBuilder.toString();
    }


    /**
     * 获取JVM自带的prop
     *
     * @return
     */
    public static Map<String, String> getJVMProp() {
        Map<String, String> map = new HashMap<>();
        Properties properties = System.getProperties();
        Enumeration<?> em = properties.propertyNames();
        for (; em.hasMoreElements(); ) {
            String key = (String) em.nextElement();
            map.put(key, System.getProperty(key));
        }
        return map;
    }


    /**
     * 获取存储空间信息
     *
     * @return
     */
    public static String getStorageInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        String dataPath = Environment.getDataDirectory().getAbsolutePath();
        stringBuilder.append("路径：" + dataPath + "\n");
        StatFs statFs = new StatFs(dataPath);
        long blockCount = statFs.getBlockCount();
        long blockSize = statFs.getBlockSize();
        long storageSize = blockSize * blockCount;
        long e = (storageSize >> 10);
        int size = (int) ((((e) / 1024.0) / 1024.0) + 0.8);
        stringBuilder.append("BlockSize:" + blockSize + "\n");
        stringBuilder.append("BlockCount:" + blockCount + "\n");
        stringBuilder.append("内部存储大小：" + size + "G" + "\n");
        return stringBuilder.toString();
    }


    /**
     * 获取data分区的块大小
     *
     * @return
     */
    public static long getDataBockSize() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        long blockSize = statFs.getBlockSize();
        return blockSize;
    }

    /**
     * 获取data分区的块数量
     *
     * @return
     */
    public static long getDataBockCount() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        long blockCount = statFs.getBlockCount();
        return blockCount;
    }


    /**
     * 最大运行内存
     *
     * @return
     */
    public static String getSystemTotalMemorySizeInfo(Context context) {
        String availMemStr = Formatter.formatFileSize(context, getSystemTotalMemorySize(context));
        return availMemStr;
    }


    /**
     * 最大运行内存
     *
     * @return
     */
    public static long getSystemTotalMemorySize(Context context) {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.totalMem;
        return memSize;
    }


    /**
     * 可用运行内存
     *
     * @return
     */
    public static long getSystemAvailableMemorySize(Context context) {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.availMem;
        return memSize;
    }

    /**
     * 可用运行内存
     *
     * @return
     */
    public static String getSystemAvailableMemorySizeInfo(Context context) {
        String availMemStr = Formatter.formatFileSize(context, getSystemAvailableMemorySize(context));

        return availMemStr;
    }


//    /**
//     * 获取蓝牙地址
//     * @param bluetoothManager
//     * @return
//     */
//    public static  String getBtAddress(BluetoothManager bluetoothManager){
//        StringBuilder stringBuilder=new StringBuilder();
//        stringBuilder.append("蓝牙地址：" + bluetoothManager.getAdapter().getAddress()+"\n");
//        return stringBuilder.toString();
//    }

    /**
     * 获取wifi信息
     *
     * @param wifiManager
     * @return
     */
    public static String getWifiInfo(WifiManager wifiManager) {
        StringBuilder stringBuilder = new StringBuilder();
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        stringBuilder.append("MAC：" + wifiInfo.getMacAddress() + "\n");
        stringBuilder.append("ConnectionInfo BSSID：\n" + wifiInfo.getBSSID() + "\n");
        stringBuilder.append("SSID：" + wifiInfo.getSSID() + "\n");
        stringBuilder.append("IP：" + intIP2StringIP(wifiInfo.getIpAddress()) + "\n");
        stringBuilder.append("\n");
        if (wifiManager.isWifiEnabled()) {
            List scanResults = wifiManager.getScanResults();
            if (scanResults != null || scanResults.size() != 0) {
                int i = 0;
                while (i < scanResults.size() && i < 7) {
                    ScanResult scanResult = (ScanResult) scanResults.get(i);
                    stringBuilder.append("BSSID" + "(" + scanResult.SSID + ")：\n" + scanResult.BSSID + "\n");
                    i++;
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 获取设备主要信息
     *
     * @param context
     * @param telephonyManager
     * @return
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getDeviceInfo(Context context, TelephonyManager telephonyManager) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IMEI(0):" + telephonyManager.getImei(0) + "\n");
        stringBuilder.append("IMEI(1):" + telephonyManager.getImei(1) + "\n");
        stringBuilder.append("ro.serialno:" + Build.SERIAL + "\n");
        stringBuilder.append("getSubscrierId:" + telephonyManager.getSubscriberId() + "\n");
        stringBuilder.append("getSimSerialNumber:" + telephonyManager.getSimSerialNumber() + "\n");
        stringBuilder.append("AndroidId:" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) + "\n");
        stringBuilder.append("getLine1Number:" + telephonyManager.getLine1Number() + "\n");
        return stringBuilder.toString();
    }

    /**
     * 打印系统常用的prop
     *
     * @return
     */
    public static String getSystemPropInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ro.product.manufacturer:" + getSystemProp("ro.product.manufacturer") + "\n");
        stringBuilder.append("ro.board.platform:" + getSystemProp("ro.board.platform") + "\n");
        stringBuilder.append("ro.product.cpu.abi:" + getSystemProp("ro.product.cpu.abi") + "\n");
        return stringBuilder.toString();
    }

    /**
     * 中间层获取SystemProperties
     *
     * @param key
     * @return
     */
    public static String getSystemProp(String key) {
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Method get = clazz.getDeclaredMethod("get", String.class);
            String value = (String) get.invoke(null, key);
            return value;

        } catch (ClassNotFoundException e) {
            System.out.println("找不到类");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("参数错误");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.out.println("目标错误");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.out.println("找不到方法");
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取Build的主要信息
     *
     * @return
     */
    public static String getBuildInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ro.build.id:" + Build.ID + "\n");
        stringBuilder.append("ro.build.display.id:" + Build.DISPLAY + "\n");
        stringBuilder.append("ro.build.fingerprint:" + Build.FINGERPRINT + "\n");
        stringBuilder.append("ro.product.brand:" + Build.BRAND + "\n");
        stringBuilder.append("ro.product.name:" + Build.PRODUCT + "\n");
        stringBuilder.append("ro.product.device:" + Build.DEVICE + "\n");
        stringBuilder.append("ro.product.board:" + Build.BOARD + "\n");
        stringBuilder.append("ro.product.model:" + Build.MODEL + "\n");
        stringBuilder.append("ro.product.manufacturer:" + Build.MANUFACTURER + "\n");
        stringBuilder.append("Build.getRadioVersion(基带版本):" + Build.getRadioVersion() + "\n");
        stringBuilder.append("ro.hardware:" + Build.HARDWARE + "\n");
        stringBuilder.append("cpu_abi:" + Build.CPU_ABI + "\n");
        stringBuilder.append("cpu_abi2:" + Build.CPU_ABI2 + "\n");
        return stringBuilder.toString();
    }


    /**
     * 屏幕分辨率
     *
     * @param windowManager
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int[] getScreenSize(WindowManager windowManager) {
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return new int[]{screenWidth, screenHeight};
    }

    /**
     * 屏幕分辨率方式2
     *
     * @param windowManager
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int[] getScreenSize2(WindowManager windowManager) {
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        int screenWidth = point.x;
        int screenHeight = point.y;
        return new int[]{screenWidth, screenHeight};
    }

    /**
     * 屏幕分辨率
     *
     * @param windowManager
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static String getScreenSizeInfo(WindowManager windowManager) {
        int[] screenSize1 = getScreenSize(windowManager);
        int[] screenSize2 = getScreenSize2(windowManager);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("分辨率方式1：" + screenSize1[0] + "," + screenSize1[1] + "\n");
        stringBuilder.append("分辨率方式2：" + screenSize2[0] + "," + screenSize2[1]);
        return stringBuilder.toString();
    }

    /**
     * 获取时间信息
     *
     * @return
     */
    public static String getTimeInfo() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("时间戳(1)：" + System.currentTimeMillis() + "\n");
        stringBuilder.append("时间戳(2)：" + Calendar.getInstance().getTimeInMillis() + "\n");
        stringBuilder.append("时间戳(3)：" + new Date().getTime() + "\n");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateText = simpleDateFormat.format(new Date());
        stringBuilder.append("具体时间：" + dateText);

        return stringBuilder.toString();
    }

    /**
     * 获得IP地址，分为两种情况，一是wifi下，二是移动网络下，得到的ip地址是不一样的
     */
    public static String getIPAddress() {
        Context context = App.getInstance().getApplicationContext();
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //调用方法将int转换为地址字符串
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);

    }


//    public static String getMacAddress(){
//        /*获取mac地址有一点需要注意的就是android 6.0版本后，以下注释方法不再适用，不管任何手机都会返回"02:00:00:00:00:00"这个默认的mac地址，这是googel官方为了加强权限管理而禁用了getSYstemService(Context.WIFI_SERVICE)方法来获得mac地址。*/
//        //        String macAddress= "";
////        WifiManager wifiManager = (WifiManager) MyApp.getContext().getSystemService(Context.WIFI_SERVICE);
////        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
////        macAddress = wifiInfo.getMacAddress();
////        return macAddress;
//
//        String macAddress = null;
//        StringBuffer buf = new StringBuffer();
//        NetworkInterface networkInterface = null;
//        try {
//            networkInterface = NetworkInterface.getByName("eth1");
//            if (networkInterface == null) {
//                networkInterface = NetworkInterface.getByName("wlan0");
//            }
//            if (networkInterface == null) {
//                return "02:00:00:00:00:02";
//            }
//            byte[] addr = networkInterface.getHardwareAddress();
//            for (byte b : addr) {
//                buf.append(String.format("%02X:", b));
//            }
//            if (buf.length() > 0) {
//                buf.deleteCharAt(buf.length() - 1);
//            }
//            macAddress = buf.toString();
//        } catch (SocketException e) {
//            e.printStackTrace();
//            return "02:00:00:00:00:02";
//        }
//        return macAddress;
//    }


    /**
     * Android  6.0 之前（不包括6.0）
     * 必须的权限  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     *
     * @param context
     * @return
     */
    private static String getMacDefault(Context context) {
        String mac = null;
        if (context == null) {
            return mac;
        }

        WifiManager wifi = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {

        }
        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     *
     * @return
     */
    private static String getMacAddress() {
        String WifiAddress = null;
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     *
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            KLog.d("Utils", "all:" + all.size());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }
                KLog.d("Utils", "macBytes:" + macBytes.length + "," + nif.getName());

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMacFromHardware(Context context) {

        try {
            String macAddress = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//5.0以下
                macAddress = getMacDefault(context);
                if (macAddress != null) {
                    KLog.d("Utils", "android 5.0以前的方式获取mac" + macAddress);
                    macAddress = macAddress.replaceAll(":", "");
                    if (!macAddress.equalsIgnoreCase("020000000000")) {
                        return macAddress;
                    }
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                macAddress = getMacAddress();
                if (macAddress != null) {
                    KLog.d("Utils", "android 6~7 的方式获取的mac" + macAddress);
                    macAddress = macAddress.replaceAll(":", "");
                    if (!macAddress.equalsIgnoreCase("020000000000")) {
                        return macAddress;
                    }
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                macAddress = getMacFromHardware();
                if (macAddress != null) {
                    KLog.d("Utils", "android 7以后 的方式获取的mac" + macAddress);
                    macAddress = macAddress.replaceAll(":", "");
                    if (!macAddress.equalsIgnoreCase("020000000000")) {
                        return macAddress;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }


        KLog.d("Utils", "没有获取到MAC");
        return null;
    }


    public static String getAId() {
        try {
            return Settings.Secure.getString(App.getInstance().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Throwable e) {
        }
        return "";
    }

    @SuppressLint("MissingPermission")
    public static String getDID() {

        try {
            TelephonyManager tm = (TelephonyManager) App.getInstance().getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);

            return tm.getDeviceId();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String A_ID;
    private static String D_ID;
    private static String M_ID;
    private static String P_ID;
    private static String OA_ID;

    private static String DEVICE_ID;

    public static String getA_Id() {
        return A_ID;
    }

    public static String getD_Id() {
        return D_ID;
    }

    public static String getM_Id() {
        return M_ID;
    }

    public static String getP_Id() {
        return P_ID;
    }

    public static String getDeviceId() {
        return DEVICE_ID;
    }
}
