package com.example.nhatphuong.demodagger2andrealm.main;

import android.app.Activity;
import com.example.nhatphuong.demodagger2andrealm.data.source.MovieDataSource;
import com.example.nhatphuong.demodagger2andrealm.data.source.remote.ApiMovie;
import com.example.nhatphuong.demodagger2andrealm.data.source.remote.MovieRemoteDataSource;
import com.example.nhatphuong.demodagger2andrealm.data.source.repository.MovieRepositoryImp;
import com.example.nhatphuong.demodagger2andrealm.utils.ActivityScope;
import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    private MainContract.View mView;

    public MainModule(MainContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    public MainContract.Presenter providePresenter(MovieDataSource.RemoteDataSoure remoteSoure) {
        MainContract.Presenter presenter = new MainPresenter(mView, remoteSoure);
        return presenter;
    }

    @ActivityScope
    @Provides
    public MovieDataSource.RemoteDataSoure provideRemote(ApiMovie apiMovie) {
        return new MovieRepositoryImp(new MovieRemoteDataSource(apiMovie));
    }
}
