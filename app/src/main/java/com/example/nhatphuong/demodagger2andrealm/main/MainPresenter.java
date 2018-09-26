package com.example.nhatphuong.demodagger2andrealm.main;

import com.example.nhatphuong.demodagger2andrealm.data.model.MoreMovie;
import com.example.nhatphuong.demodagger2andrealm.data.source.MovieDataSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;
    private MovieDataSource.RemoteDataSoure mMovieRepository;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public MainPresenter(MainContract.View view, MovieDataSource.RemoteDataSoure movieRepository) {
        mView = view;
        mMovieRepository = movieRepository;
    }

    @Override
    public void getMovie(int numPage) {
        Disposable disposable = mMovieRepository.getMovieData(numPage)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MoreMovie>() {
                    @Override
                    public void accept(MoreMovie moreMovie) throws Exception {
                        mView.onSuccess(moreMovie);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onFail(throwable.getMessage());
                    }
                });
        mCompositeDisposable.add(disposable);
    }
}
