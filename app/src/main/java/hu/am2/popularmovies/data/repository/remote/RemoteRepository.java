package hu.am2.popularmovies.data.repository.remote;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.am2.popularmovies.data.repository.remote.api.TMDBService;
import hu.am2.popularmovies.data.repository.remote.module.TMDBResponse;
import io.reactivex.Single;

@Singleton
public class RemoteRepository {

    private final TMDBService tmdbService;

    @Inject
    public RemoteRepository(TMDBService tmdbService) {
        this.tmdbService = tmdbService;
    }

    public Single<TMDBResponse> getPopularMovies() {
        return tmdbService.getPopularMovies();
    }

    public Single<TMDBResponse> getTopRatedMovies() {
        return tmdbService.getTopRatedMovies();
    }
}
