package hu.am2.popularmovies.data.repository.remote;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.am2.popularmovies.data.repository.remote.api.TMDBService;
import hu.am2.popularmovies.data.repository.remote.model.DetailResponse;
import hu.am2.popularmovies.data.repository.remote.model.MovieDetailModel;
import hu.am2.popularmovies.data.repository.remote.model.MovieResponse;
import hu.am2.popularmovies.data.repository.remote.model.ReviewModel;
import hu.am2.popularmovies.data.repository.remote.model.VideoModel;
import io.reactivex.Single;

@Singleton
public class RemoteRepository {

    private final TMDBService tmdbService;

    @Inject
    public RemoteRepository(TMDBService tmdbService) {
        this.tmdbService = tmdbService;
    }

    public Single<MovieResponse> getPopularMovies() {
        return tmdbService.getPopularMovies();
    }

    public Single<MovieResponse> getTopRatedMovies() {
        return tmdbService.getTopRatedMovies();
    }

    public Single<DetailResponse<VideoModel>> getVideosForMovieId(int movieId) {
        return tmdbService.getVideosForMovieId(movieId);
    }

    public Single<DetailResponse<ReviewModel>> getReviewsForMovieId(int movieId) {
        return tmdbService.getReviewsForMovieId(movieId);
    }

    public Single<MovieDetailModel> getDetailForMovieId(int movieId) {
        return tmdbService.getDetailForMovieId(movieId);
    }
}
