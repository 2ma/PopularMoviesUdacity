package hu.am2.popularmovies.data.repository.remote.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {

    private int page;
    @SerializedName("total_results")
    private
    int totalResults;
    @SerializedName("total_pages")
    private
    int totalPages;
    @SerializedName("results")
    private
    List<MovieModel> movies;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<MovieModel> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieModel> movies) {
        this.movies = movies;
    }
}
