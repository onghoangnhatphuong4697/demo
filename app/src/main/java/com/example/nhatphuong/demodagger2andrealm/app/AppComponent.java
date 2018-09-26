package com.example.nhatphuong.demodagger2andrealm.app;

import android.content.Context;
import com.example.nhatphuong.demodagger2andrealm.data.source.remote.ApiMovie;
import com.example.nhatphuong.demodagger2andrealm.data.source.repository.NetworkModule;
import com.example.nhatphuong.demodagger2andrealm.utils.AppScope;
import dagger.Component;

@AppScope
@Component(modules = { ApplicationModule.class, NetworkModule.class })
public interface AppComponent {
    Context applicationContext();

    ApiMovie apiMovie();
}
