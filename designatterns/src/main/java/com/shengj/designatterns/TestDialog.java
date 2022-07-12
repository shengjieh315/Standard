package com.shengj.designatterns;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDialog;

public class TestDialog extends Dialog {

    public TestDialog(Context context) {
        super(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_test, null);
        setContentView(contentView);
        Window dialogWindow = getWindow();
        if (null != dialogWindow) {

            dialogWindow.setGravity(Gravity.CENTER);

            dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialogWindow.setAttributes(lp);


            setCancelable(true);
        }
    }

}
