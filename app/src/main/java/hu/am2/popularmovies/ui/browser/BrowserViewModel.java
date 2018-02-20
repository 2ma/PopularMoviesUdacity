package hu.am2.popularmovies.ui.browser;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;

import javax.inject.Inject;

import hu.am2.popularmovies.data.repository.remote.RemoteRepository;
import hu.am2.popularmovies.data.repository.remote.module.TMDBResponse;
import hu.am2.popularmovies.domain.Result;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class BrowserViewModel extends ViewModel {

    private static final String PREFERENCE_FILTER = "filter";
    private final RemoteRepository remoteRepository;
    private final BehaviorSubject<Integer> filter = BehaviorSubject.create();
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Result> moviesLiveData = new MutableLiveData<>();

    @Inject
    public BrowserViewModel(RemoteRepository remoteRepository, SharedPreferences sharedPreferences) {
        this.remoteRepository = remoteRepository;
        filter.onNext(sharedPreferences.getInt(PREFERENCE_FILTER, BrowseActivity.FILTER_POPULAR));
        disposables.add(filter.subscribe(filter -> {
            sharedPreferences.edit().putInt(PREFERENCE_FILTER, filter).apply();
            postLoading(filter);

            if (filter == BrowseActivity.FILTER_POPULAR) {
                disposables.add(remoteRepository.getPopularMovies().subscribeOn(Schedulers.io()).map(TMDBResponse::getMovies).subscribe(movies ->
                    postResult(Result.success(movies, filter)), throwable -> handleError(filter, throwable)));
            } else if (filter == BrowseActivity.FILTER_TOP_RATED) {
                disposables.add(remoteRepository.getTopRatedMovies().subscribeOn(Schedulers.io()).map(TMDBResponse::getMovies).subscribe(movies ->
                    postResult(Result.success(movies, filter)), throwable -> handleError(filter, throwable)));
            }
        }));
    }

    private void handleError(int filter, Throwable throwable) {
        postResult(Result.error(filter, throwable.getMessage()));
    }

    private void postLoading(int filter) {
        postResult(Result.loading(filter));
    }

    public void retry() {
        filter.onNext(filter.getValue());
    }

    public LiveData<Result> getMovies() {
        return moviesLiveData;
    }

    private void postResult(Result result) {
        moviesLiveData.postValue(result);
    }

    public Observer<Integer> getFilterObserver() {
        return filter;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
