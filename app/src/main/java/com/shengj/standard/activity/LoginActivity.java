package com.shengj.standard.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shengj.common.base.BaseView;
import com.shengj.standard.R;
import com.shengj.standard.presenter.LoginPresenter;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseView<LoginPresenter> {

    @BindView(R.id.tv_hello)
    TextView tvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        KLog.e("Login_onCreate");
        ButterKnife.bind(this);
    }

    @Override
    public LoginPresenter getPresenter() {
        return new LoginPresenter();
    }

    public void toMain(View view) {

      p.toLogin("1","1");

    }

    @Override
    public void showData(String s) {
        tvHello.setText(s);
    }
}