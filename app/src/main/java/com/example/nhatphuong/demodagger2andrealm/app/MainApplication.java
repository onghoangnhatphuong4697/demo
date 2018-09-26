package com.example.nhatphuong.demodagger2andrealm.app;

import android.app.Application;
import com.example.nhatphuong.demodagger2andrealm.data.source.repository.NetworkModule;

public class MainApplication extends Application {
    private AppComponent mAppComponent;

    public AppComponent getAppComponent() {
        if (mAppComponent == null) {
            mAppComponent = DaggerAppComponent.builder()
                    .applicationModule(new ApplicationModule(getApplicationContext()))
                    .networkModule(new NetworkModule(this))
                    .build();
        }
        return mAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
