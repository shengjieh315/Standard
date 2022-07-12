package com.shengj.designatterns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.shengj.designatterns.databinding.ActivityFilePreviewBinding;

public class FilePreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_preview);
        init();
    }

    private void init(){
//        String url = "https://shigongbang.oss-cn-hangzhou.aliyuncs.com/member_center/610000/20220718217165980659871.AVI";
        ActivityFilePreviewBinding binding = DataBindingUtil.setContentView(this , R.layout.activity_file_preview);
//        binding.vvPlayer.setVideoPath(url);
//        binding.vvPlayer.start();
      binding.textView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              new TestDialog(FilePreviewActivity.this).show();
          }
      });
    }

}