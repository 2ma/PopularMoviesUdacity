package hu.am2.popularmovies.ui.browser;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import hu.am2.popularmovies.R;
import hu.am2.popularmovies.data.repository.remote.module.MovieModel;

public class BrowserAdapter extends RecyclerView.Adapter<BrowserAdapter.MovieViewHolder> {

    public static final String IMAGE_URL = "https://image.tmdb.org/t/p/w300";
    private final LayoutInflater inflater;
    private final GridClickListener listener;
    private List<MovieModel> movies = Collections.emptyList();

    public BrowserAdapter(LayoutInflater inflater, GridClickListener listener) {
        this.inflater = inflater;
        this.listener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.browser_item, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bindMovie(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }

    public void setMovies(List<MovieModel> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public interface GridClickListener {
        void onMovieClick(MovieModel movieModel);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView poster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.moviePoster);
            itemView.setOnClickListener(this);
        }

        //TODO handle glide loading error
        public void bindMovie(MovieModel movie) {
            Glide.with(poster).load(IMAGE_URL + movie.getPosterUrl()).into(poster);
        }

        @Override
        public void onClick(View v) {
            listener.onMovieClick(movies.get(getAdapterPosition()));
        }
    }
}
