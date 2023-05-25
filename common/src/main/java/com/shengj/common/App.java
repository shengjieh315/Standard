package com.shengj.common;

import android.app.Application;

public class App extends Application {

    //    全局application
    private static App app;

    /**
     * 全局唯一的Application
     *
     * @return Application
     */
    public static App getInstance() {
        if (app == null) {
            app = new App();
        }
        return app;

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
