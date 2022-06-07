package com.shengj.standard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shengj.standard.R;
import com.socks.library.KLog;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_hello)
    TextView tvHello;

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

        KLog.e("Main_onCreate");

    }

    public void toOther(View view) {

        Intent intent = new Intent(this , OtherActivity.class);
        startActivity(intent);

    }

}