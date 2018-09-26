package com.example.nhatphuong.demodagger2andrealm.main;

public interface MainContract {
    interface View {
        void onSuccess(Object o);

        void onFail(String message);
    }

    interface Presenter {
        void getMovie(int numPage);
    }
}
