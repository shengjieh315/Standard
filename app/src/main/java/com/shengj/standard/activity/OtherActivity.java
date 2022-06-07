package com.shengj.standard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shengj.standard.R;
import com.shengj.standard.activity.LoginActivity;
import com.socks.library.KLog;

import butterknife.BindView;

public class OtherActivity extends AppCompatActivity {

    @BindView(R.id.tv_hello)
    TextView tvHello;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        KLog.e("Other_onNewIntent");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        KLog.e("Other_onCreate");

    }

    public void toLogin(View view) {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
}