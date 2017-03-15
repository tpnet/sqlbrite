package com.tpnet.sqlbrite_sqldelight_sample;

import android.app.Application;
import android.content.Context;

/**
 * Created by litp on 2017/3/15.
 */

public class BaseApplication extends Application {


    private AppComponent appComponent;


    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();


    }


    public static AppComponent getAppComponent(Context context) {
        return ((BaseApplication) context.getApplicationContext()).appComponent;

    }
}
