package com.example.nhatphuong.demodagger2andrealm.main;

import com.example.nhatphuong.demodagger2andrealm.app.AppComponent;
import com.example.nhatphuong.demodagger2andrealm.utils.ActivityScope;
import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = MainModule.class)
public interface MainComponent {
    void inject(MainActivity mainActivity);
}
