package hu.am2.popularmovies.data.repository.remote.model;


import com.google.gson.annotations.SerializedName;

public class MovieDetailModel {
    @SerializedName("imdb_id")
    private String imdbId;

    private int runtime;

    @SerializedName("vote_count")
    private int voteCount;

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
