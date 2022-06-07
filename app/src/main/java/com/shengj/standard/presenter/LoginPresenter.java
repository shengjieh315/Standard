package com.shengj.standard.presenter;

import android.annotation.SuppressLint;

import com.shengj.common.base.BasePresenter;
import com.shengj.standard.activity.LoginActivity;
import com.socks.library.KLog;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginPresenter extends BasePresenter<LoginActivity> {

    @SuppressLint("CheckResult")
    public void toLogin(String name, String password) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Exception {
                Thread.sleep(10000);
                emitter.onNext("成功");
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        KLog.e("onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        getView().showData(s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        KLog.e("onComplete");
                    }
                });


    }


}
