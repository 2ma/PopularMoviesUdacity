package hu.am2.popularmovies.ui.browser;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.ContentObserver;

import javax.inject.Inject;

import hu.am2.popularmovies.data.repository.local.LocalRepository;
import hu.am2.popularmovies.data.repository.local.database.PopularMoviesContract;
import hu.am2.popularmovies.data.repository.remote.RemoteRepository;
import hu.am2.popularmovies.data.repository.remote.module.MovieResponse;
import hu.am2.popularmovies.domain.Result;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class BrowserViewModel extends ViewModel {

    private static final String PREFERENCE_FILTER = "filter";
    private final RemoteRepository remoteRepository;
    private final LocalRepository localRepository;
    private final ContentResolver contentResolver;
    private final BehaviorSubject<Boolean> retry = BehaviorSubject.create();
    private final BehaviorSubject<Integer> filter = BehaviorSubject.create();
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Result> moviesLiveData = new MutableLiveData<>();

    private static final String TAG = "BrowserViewModel";

    private boolean contentObserverRegistered = false;

    @Inject
    public BrowserViewModel(LocalRepository localRepository, RemoteRepository remoteRepository, SharedPreferences sharedPreferences,
                            ContentResolver contentResolver) {
        this.remoteRepository = remoteRepository;
        this.localRepository = localRepository;
        this.contentResolver = contentResolver;
        retry.onNext(true);
        filter.onNext(sharedPreferences.getInt(PREFERENCE_FILTER, BrowseActivity.FILTER_POPULAR));
        disposables.add(
            Observable.combineLatest(retry, filter.distinctUntilChanged(), (retry, filter) -> filter
            ).subscribe(filter -> {
                sharedPreferences.edit().putInt(PREFERENCE_FILTER, filter).apply();
                postLoading(filter);

                if (filter == BrowseActivity.FILTER_POPULAR) {
                    disposables.add(remoteRepository.getPopularMovies().subscribeOn(Schedulers.io()).map(MovieResponse::getMovies).subscribe(movies ->
                        postResult(Result.success(movies, filter)), throwable -> handleError(filter, throwable)));
                    unregisterContentObserver();
                } else if (filter == BrowseActivity.FILTER_TOP_RATED) {
                    disposables.add(remoteRepository.getTopRatedMovies().subscribeOn(Schedulers.io()).map(MovieResponse::getMovies).subscribe
                        (movies ->
                            postResult(Result.success(movies, filter)), throwable -> handleError(filter, throwable)));
                    unregisterContentObserver();
                } else if (filter == BrowseActivity.FILTER_FAVORITES) {
                    disposables.add(
                        Single.fromCallable(localRepository::getAllFavorites).subscribeOn(Schedulers.io()).subscribe(movies -> postResult(Result
                            .success(movies, filter)), throwable -> postResult(Result.error(filter, throwable.getMessage()))));

                    if (!contentObserverRegistered) {
                        contentResolver.registerContentObserver(PopularMoviesContract.Favorites.CONTENT_URI, true, contentObserver);
                        contentObserverRegistered = true;
                    }
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
        retry.onNext(true);
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
        unregisterContentObserver();
    }

    private void unregisterContentObserver() {
        if (contentObserverRegistered) {
            contentResolver.unregisterContentObserver(contentObserver);
            contentObserverRegistered = false;
        }
    }

    private ContentObserver contentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            retry();
        }
    };
}
