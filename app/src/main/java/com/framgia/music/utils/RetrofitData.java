package com.framgia.music.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitData {
    private static int READ_WRITE_TIME_OUT = 5000;
    private static int CONNECT_TIME_OUT = 10000;
    private static Retrofit sRetrofit;

    public static Retrofit getDataFromUrl(String url) {
        if (sRetrofit == null) {
            OkHttpClient okHttpClient =
                new OkHttpClient.Builder().readTimeout(READ_WRITE_TIME_OUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(READ_WRITE_TIME_OUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
            Gson gson = new GsonBuilder().setLenient().create();
            sRetrofit = new Retrofit.Builder().baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        }
        return sRetrofit;
    }
}
