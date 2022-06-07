package com.shengj.common.base;

import java.lang.ref.WeakReference;

// Presenter基类
public abstract class BasePresenter<V extends BaseView> {

    // 绑定View层弱引用
    private WeakReference<V> vWeakReference;

    public void bindView(V v) {
        vWeakReference = new WeakReference<>(v);
    }

    public void unBindView() {
        if (vWeakReference != null) {
            vWeakReference.clear();
            vWeakReference = null;
            System.gc();
        }
    }

    // 获取View，P -- V
    public V getView() {
        if (vWeakReference != null) {
            return vWeakReference.get();
        }
        return null;
    }

}