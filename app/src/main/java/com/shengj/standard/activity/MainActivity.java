package com.shengj.standard.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.as.capital.CapitalMainActivity;
import com.as.goods.GoodsMainActivity;
import com.shengj.standard.R;
import com.socks.library.KLog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        KLog.e("Main_onNewIntent");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_capital).setOnClickListener(view -> {
            startActivity(new Intent(this, CapitalMainActivity.class));
        });

        findViewById(R.id.tv_goods).setOnClickListener(view -> {
            startActivity(new Intent(this, GoodsMainActivity.class));
        });

    }


}