package hu.am2.popularmovies.data.repository.remote.module;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailResponse<T> {
    private int id;
    @SerializedName("results")
    private List<T> data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
