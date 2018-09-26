package com.example.nhatphuong.demodagger2andrealm.data.source.repository;

import android.app.Application;
import com.example.nhatphuong.demodagger2andrealm.data.source.remote.ApiMovie;
import com.example.nhatphuong.demodagger2andrealm.utils.AppScope;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import dagger.Module;
import dagger.Provides;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
    public static final String BASE_URL = "https://api.themoviedb.org/3/";

    public static Retrofit sRetrofit;

    private Application mApplication;

    public NetworkModule(Application application) {
        mApplication = application;
    }

    @AppScope
    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @AppScope
    @Provides
    Cache provideOkHttpConnectCache(Application application) {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @AppScope
    @Provides
    OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(cache)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();
        return okHttpClient;
    }

    @AppScope
    @Provides
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        if (sRetrofit == null) {
            Gson gson = new GsonBuilder().setLenient().create();
            sRetrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return sRetrofit;
    }

    @AppScope
    @Provides
    ApiMovie provideApiMovie(Retrofit retrofit) {
        return retrofit.create(ApiMovie.class);
    }
}
