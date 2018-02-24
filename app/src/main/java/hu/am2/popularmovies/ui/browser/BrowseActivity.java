package hu.am2.popularmovies.ui.browser;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import hu.am2.popularmovies.R;
import hu.am2.popularmovies.Utils;
import hu.am2.popularmovies.data.repository.remote.module.MovieModel;
import hu.am2.popularmovies.databinding.ActivityBrowseBinding;
import hu.am2.popularmovies.domain.Result;
import hu.am2.popularmovies.ui.detail.DetailActivity;
import io.reactivex.disposables.CompositeDisposable;

public class BrowseActivity extends AppCompatActivity implements BrowserAdapter.GridClickListener {

    public static final int FILTER_POPULAR = 0;
    public static final int FILTER_TOP_RATED = 1;
    public static final int FILTER_FAVORITES = 2;
    public static final String EXTRA_MOVIE = "hu.am2.popularmovies.extra.MOVIE";
    private final CompositeDisposable disposables = new CompositeDisposable();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private BrowserViewModel viewModel;
    private BrowserAdapter adapter;
    private ActivityBrowseBinding binding;

    private boolean scrollToTop = false;

    private int filter = FILTER_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_browse);

        adapter = new BrowserAdapter(getLayoutInflater(), this);

        setSupportActionBar(binding.toolbar);

        binding.movieListView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.movieListView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BrowserViewModel.class);

        viewModel.getMovies().observe(this, this::handleResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_browser, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        switch (filter) {
            case FILTER_POPULAR: {
                changeMenuText(menu, R.id.menu_filter_popular, Color.RED);
                changeMenuText(menu, R.id.menu_filter_top_rated, Color.BLACK);
                changeMenuText(menu, R.id.menu_filter_favorite, Color.BLACK);
                break;
            }
            case FILTER_TOP_RATED: {
                changeMenuText(menu, R.id.menu_filter_top_rated, Color.RED);
                changeMenuText(menu, R.id.menu_filter_popular, Color.BLACK);
                changeMenuText(menu, R.id.menu_filter_favorite, Color.BLACK);
                break;
            }
            case FILTER_FAVORITES: {
                changeMenuText(menu, R.id.menu_filter_favorite, Color.RED);
                changeMenuText(menu, R.id.menu_filter_popular, Color.BLACK);
                changeMenuText(menu, R.id.menu_filter_top_rated, Color.BLACK);
                break;
            }

        }
        return true;
    }

    private void handleResult(Result<MovieModel> result) {
        switch (result.state) {
            case Result.STATE_LOADING: {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.emptyView.setVisibility(View.GONE);
                break;
            }
            case Result.STATE_SUCCESS: {
                binding.progressBar.setVisibility(View.GONE);
                displayMovies(result.data);
                break;
            }
            case Result.STATE_ERROR: {
                binding.progressBar.setVisibility(View.GONE);
                displayMovies(result.data);
                if (Utils.isConnected(this)) {
                    Snackbar.make(binding.movieListView, R.string.error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, v -> viewModel.retry())
                        .show();
                } else {
                    Snackbar.make(binding.movieListView, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, v -> viewModel.retry())
                        .show();
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Invalid state: " + result.state);
        }
        if (scrollToTop) {
            scrollToTop = false;
            binding.movieListView.scrollToPosition(0);
        }

        filter = result.filter;
        invalidateOptionsMenu();
    }

    private void changeMenuText(Menu menu, int id, int color) {
        MenuItem item = menu.findItem(id);
        SpannableString title = new SpannableString(item.getTitle());
        title.setSpan(new ForegroundColorSpan(color), 0, title.length(), 0);
        item.setTitle(title);
    }

    private void displayMovies(List<MovieModel> movies) {
        if (movies == null || movies.isEmpty()) {
            binding.emptyView.setVisibility(View.VISIBLE);
            binding.movieListView.setVisibility(View.GONE);
            adapter.setMovies(Collections.emptyList());
        } else {
            binding.emptyView.setVisibility(View.GONE);
            binding.movieListView.setVisibility(View.VISIBLE);
            adapter.setMovies(movies);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        disposables.add(RxToolbar.itemClicks(binding.toolbar)
            .filter(menuItem -> menuItem.getItemId() != 0)
            .map(menuItem -> {
                scrollToTop = true;
                switch (menuItem.getItemId()) {
                    case R.id.menu_filter_popular:
                        return FILTER_POPULAR;
                    case R.id.menu_filter_top_rated:
                        return FILTER_TOP_RATED;
                    default:
                        return FILTER_FAVORITES;
                }
            })
            .subscribe(menuItem -> viewModel.getFilterObserver().onNext(menuItem)));

    }

    @Override
    protected void onPause() {
        super.onPause();
        disposables.clear();
    }

    @Override
    public void onMovieClick(MovieModel movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        startActivity(intent);
    }
}
