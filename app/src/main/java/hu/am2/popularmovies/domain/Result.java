package hu.am2.popularmovies.domain;


import java.util.Collections;
import java.util.List;

public class Result<T> {
    public static final int STATE_LOADING = 0;
    public static final int STATE_SUCCESS = 1;
    public static final int STATE_ERROR = 2;

    public final List<T> data;
    public final int state;
    public final int filter;
    public final String errorMessage;

    private Result(List<T> data, int state, int filter, String errorMessage) {
        this.data = data;
        this.state = state;
        this.filter = filter;
        this.errorMessage = errorMessage;
    }

    public static <D> Result<D> success(List<D> data, int filter) {
        return new Result<>(data, STATE_SUCCESS, filter, null);
    }

    public static <D> Result<D> loading(int filter) {
        return new Result<>(Collections.emptyList(), STATE_LOADING, filter, null);
    }

    public static <D> Result<D> error(int filter, String errorMessage) {
        return new Result<>(Collections.emptyList(), STATE_ERROR, filter, errorMessage);
    }


}
