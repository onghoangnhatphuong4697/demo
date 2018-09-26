package com.example.nhatphuong.demodagger2andrealm.app;

import android.content.Context;
import com.example.nhatphuong.demodagger2andrealm.utils.AppScope;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private Context mContext;

    public ApplicationModule(Context context) {
        mContext = context;
    }

    @Provides
    @AppScope
    public Context provideApplicationContext() {
        return mContext;
    }
}
