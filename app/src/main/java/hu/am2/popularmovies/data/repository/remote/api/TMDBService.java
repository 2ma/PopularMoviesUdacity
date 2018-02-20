package hu.am2.popularmovies.data.repository.remote.api;


import hu.am2.popularmovies.data.repository.remote.module.TMDBResponse;
import io.reactivex.Single;
import retrofit2.http.GET;

public interface TMDBService {

    @GET("movie/popular")
    Single<TMDBResponse> getPopularMovies();

    @GET("movie/top_rated")
    Single<TMDBResponse> getTopRatedMovies();
}
