package hu.am2.popularmovies.ui.detail;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import javax.inject.Inject;

import hu.am2.popularmovies.data.repository.local.LocalRepository;
import hu.am2.popularmovies.data.repository.remote.RemoteRepository;
import hu.am2.popularmovies.data.repository.remote.module.DetailResponse;
import hu.am2.popularmovies.data.repository.remote.module.MovieModel;
import hu.am2.popularmovies.data.repository.remote.module.ReviewModel;
import hu.am2.popularmovies.data.repository.remote.module.VideoModel;
import hu.am2.popularmovies.domain.Result;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DetailViewModel extends ViewModel {
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final RemoteRepository remoteRepository;
    private final LocalRepository localRepository;

    private final MutableLiveData<Result<VideoModel>> videos = new MutableLiveData<>();
    private final MutableLiveData<Result<ReviewModel>> reviews = new MutableLiveData<>();

    private final MutableLiveData<Boolean> favorite = new MutableLiveData<>();

    private static final String TAG = "DetailViewModel";

    private MovieModel movie = null;

    @Inject
    public DetailViewModel(RemoteRepository remoteRepository, LocalRepository localRepository) {
        this.remoteRepository = remoteRepository;
        this.localRepository = localRepository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    void loadDetailsForMovie(MovieModel movie) {

        if (this.movie == null || this.movie.getId() != movie.getId()) {
            disposables.clear();
            this.movie = movie;
            final int id = movie.getId();
            loadVideos(id);
            loadReviews(id);
            checkFavorite(id);
        }
    }

    private void loadReviews(int movieId) {
        postReviewResult(Result.loading(-1));
        disposables.add(remoteRepository.getReviewsForMovieId(movieId).subscribeOn(Schedulers.io())
            .map(DetailResponse::getData)
            .subscribe(reviewModels -> postReviewResult(Result.success(reviewModels, -1)),
                throwable -> postReviewResult(Result.error(-1, throwable.getMessage()))));
    }

    private void loadVideos(int movieId) {
        postVideoResult(Result.loading(-1));
        disposables.add(remoteRepository.getVideosForMovieId(movieId).subscribeOn(Schedulers.io())
            .map(DetailResponse::getData)
            .subscribe(videoModules -> postVideoResult(Result.success(videoModules, -1)),
                throwable -> postVideoResult(Result.error(-1, throwable.getMessage()))));
    }

    private void checkFavorite(int movieId) {
        disposables.add(Single.fromCallable(() -> localRepository.isMovieFavorite(movieId)).subscribe(favorite::postValue));
    }

    public void changeFavorite() {
        Log.d(TAG, "changeFavorite: ");
        boolean isFavorite = favorite.getValue() == null ? false : favorite.getValue();
        if (isFavorite) {
            Completable.fromAction(() -> localRepository.deleteFavorite(movie.getId())).subscribeOn(Schedulers.io()).subscribe();
        } else {
            Completable.fromAction(() -> localRepository.setFavorite(movie)).subscribeOn(Schedulers.io()).subscribe();
        }
        favorite.setValue(!isFavorite);
    }

    void retry() {
        disposables.clear();
        final int id = movie.getId();
        loadVideos(id);
        loadReviews(id);
    }

    private void postVideoResult(Result<VideoModel> result) {
        videos.postValue(result);
    }

    private void postReviewResult(Result<ReviewModel> result) {
        reviews.postValue(result);
    }

    LiveData<Result<VideoModel>> getVideos() {
        return videos;
    }

    LiveData<Result<ReviewModel>> getReviews() {
        return reviews;
    }

    LiveData<Boolean> getFavoriteStatus() {
        return favorite;
    }
}
