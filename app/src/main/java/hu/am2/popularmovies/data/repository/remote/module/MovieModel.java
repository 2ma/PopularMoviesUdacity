package hu.am2.popularmovies.data.repository.remote.module;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MovieModel implements Parcelable {

    public static final Parcelable.Creator<MovieModel> CREATOR = new Parcelable.Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel source) {
            return new MovieModel(source);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };
    private String title;
    @SerializedName("poster_path")
    private String posterUrl;
    @SerializedName("overview")
    private String synopsis;
    @SerializedName("vote_average")
    private Double userRating;
    @SerializedName("release_date")
    private String releaseDate;

    public MovieModel() {
    }

    protected MovieModel(Parcel in) {
        this.title = in.readString();
        this.posterUrl = in.readString();
        this.synopsis = in.readString();
        this.userRating = (Double) in.readValue(Double.class.getClassLoader());
        this.releaseDate = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Double getUserRating() {
        return userRating;
    }

    public void setUserRating(Double userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.posterUrl);
        dest.writeString(this.synopsis);
        dest.writeValue(this.userRating);
        dest.writeString(this.releaseDate);
    }
}
