//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.shengj.common.utils;

import android.content.Context;
import android.text.TextUtils;

import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.socks.library.KLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class OAIdManager {
    private static String mOAId;

    public OAIdManager() {
    }

    public static String getOAId(Context context) {
        if (!TextUtils.isEmpty(mOAId)) {
            return mOAId;
        } else {
            try {
                int errorCode = MdidSdkHelper.InitSdk(context, true, new IIdentifierListener() {
                    public void OnSupport(boolean b, IdSupplier idSupplier) {
                        if (idSupplier != null) {
                            OAIdManager.mOAId = idSupplier.getOAID();
                        }
                    }
                });
                if (errorCode == 1008612) {
                    KLog.e("获取OAID：不支持的设备");
                } else if (errorCode == 1008613) {
                    KLog.e("获取OAID：加载配置文件出错");
                } else if (errorCode == 1008611) {
                    KLog.e("获取OAID：不支持的设备厂商");
                } else if (errorCode == 1008614) {
                    KLog.e("获取OAID：获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程");
                } else if (errorCode == 1008615) {
                    KLog.e("获取OAID：反射调用出错");
                }
            } catch (Throwable var2) {
                return getOAId2(context);
            }

            if (mOAId == null) {
                mOAId = "";
            }

            return mOAId;
        }
    }

    private static String getOAId2(Context context) {
        if (!TextUtils.isEmpty(mOAId)) {
            return mOAId;
        } else {
            try {
                Class<?> callbackClazz = Class.forName("com.bun.supplier.IIdentifierListener");
                OAIdManager.MyHandler myHandler = new OAIdManager.MyHandler();
                Object myCallback = Proxy.newProxyInstance(OAIdManager.class.getClassLoader(), new Class[]{callbackClazz}, myHandler);
                Class<?> jLibraryClazz = Class.forName("com.bun.miitmdid.core.JLibrary");
                jLibraryClazz.getMethod("InitEntry", Context.class).invoke((Object)null, context);
                Class<?> mdidSdkHelperClazz = Class.forName("com.bun.miitmdid.core.MdidSdkHelper");
                int var6 = (Integer)mdidSdkHelperClazz.getMethod("InitSdk", Context.class, Boolean.TYPE, callbackClazz).invoke((Object)null, context, true, myCallback);
            } catch (Throwable var7) {
                return getOAId3(context);
            }

            if (mOAId == null) {
                mOAId = "";
            }

            return mOAId;
        }
    }

    private static String getOAId3(Context context) {
        if (!TextUtils.isEmpty(mOAId)) {
            return mOAId;
        } else {
            try {
                Class<?> callbackClazz = Class.forName("com.bun.miitmdid.core.IIdentifierListener");
                OAIdManager.MyHandler myHandler = new OAIdManager.MyHandler();
                Object myCallback = Proxy.newProxyInstance(OAIdManager.class.getClassLoader(), new Class[]{callbackClazz}, myHandler);
                Class<?> jLibraryClazz = Class.forName("com.bun.miitmdid.core.JLibrary");
                jLibraryClazz.getMethod("InitEntry", Context.class).invoke((Object)null, context);
                Class<?> mdidSdkHelperClazz = Class.forName("com.bun.miitmdid.core.MdidSdkHelper");
                int var6 = (Integer)mdidSdkHelperClazz.getMethod("InitSdk", Context.class, Boolean.TYPE, callbackClazz).invoke((Object)null, context, true, myCallback);
            } catch (Throwable var7) {
                KLog.e("oaid Error" + var7.getMessage());
            }

            if (mOAId == null) {
                mOAId = "";
            }

            return mOAId;
        }
    }

    public static class MyHandler implements InvocationHandler {
        public MyHandler() {
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (args[1] == null) {
                return null;
            } else {
                OAIdManager.mOAId = (String)args[1].getClass().getMethod("getOAID").invoke(args[1]);
                return null;
            }
        }
    }
}
