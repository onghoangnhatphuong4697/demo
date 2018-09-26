package com.example.nhatphuong.demodagger2andrealm.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import com.example.nhatphuong.demodagger2andrealm.R;
import com.example.nhatphuong.demodagger2andrealm.app.MainApplication;
import com.example.nhatphuong.demodagger2andrealm.data.model.MoreMovie;
import com.example.nhatphuong.demodagger2andrealm.data.model.Movie;
import com.example.nhatphuong.demodagger2andrealm.data.source.MovieDataSource;
import java.util.List;
import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    @Inject
    MainContract.Presenter mMainPresenter;
    @Inject
    MovieDataSource.RemoteDataSoure mRemoteDataSoure;

    RecyclerView mRecyclerView;
    MainAdapter mMainAdapter = new MainAdapter();
    List<Movie> mMoreMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerMainComponent.builder()
                .appComponent(((MainApplication) getApplication()).getAppComponent())
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainPresenter.getMovie(1);
        mRecyclerView = findViewById(R.id.recycle_view1);
        mRecyclerView.setAdapter(mMainAdapter);
        //  mMainPresenter.getMovie(1);
    }

    @Override
    public void onSuccess(Object o) {
        if (o instanceof MoreMovie) {
            mMoreMovie = ((MoreMovie) o).getMovieList();
            System.out.println(mMoreMovie.get(1).getId());
            mMainAdapter.updateMovieList(mMoreMovie);
        }
    }

    @Override
    public void onFail(String message) {
        System.out.println(message);
    }
}
