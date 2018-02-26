package hu.am2.popularmovies.data.repository.remote.api;


import hu.am2.popularmovies.data.repository.remote.model.DetailResponse;
import hu.am2.popularmovies.data.repository.remote.model.MovieDetailModel;
import hu.am2.popularmovies.data.repository.remote.model.MovieResponse;
import hu.am2.popularmovies.data.repository.remote.model.ReviewModel;
import hu.am2.popularmovies.data.repository.remote.model.VideoModel;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TMDBService {

    @GET("movie/popular")
    Single<MovieResponse> getPopularMovies();

    @GET("movie/top_rated")
    Single<MovieResponse> getTopRatedMovies();

    @GET("movie/{id}/videos")
    Single<DetailResponse<VideoModel>> getVideosForMovieId(@Path("id") int movieId);

    @GET("movie/{id}/reviews")
    Single<DetailResponse<ReviewModel>> getReviewsForMovieId(@Path("id") int movieId);

    @GET("movie/{id}")
    Single<MovieDetailModel> getDetailForMovieId(@Path("id") int movieId);


}
