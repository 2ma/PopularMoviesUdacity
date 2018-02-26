package hu.am2.popularmovies.ui.detail;


import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.florent37.glidepalette.GlidePalette;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import hu.am2.popularmovies.R;
import hu.am2.popularmovies.Utils;
import hu.am2.popularmovies.data.repository.remote.model.MovieDetailModel;
import hu.am2.popularmovies.data.repository.remote.model.MovieModel;
import hu.am2.popularmovies.data.repository.remote.model.ReviewModel;
import hu.am2.popularmovies.data.repository.remote.model.VideoModel;
import hu.am2.popularmovies.databinding.DetailLayoutBinding;
import hu.am2.popularmovies.domain.Result;
import hu.am2.popularmovies.ui.browser.BrowseActivity;

public class DetailActivity extends AppCompatActivity implements DetailVideoAdapter.VideoClickListener {

    private static final String LARGE_IMG_URL = "https://image.tmdb.org/t/p/w1280";

    private static final String TAG = "DetailActivity";


    @BindingAdapter({"imageUrl", "collapsingToolbar", "context"})
    public static void loadImageFromUrl(ImageView imageView, String imageUrl, CollapsingToolbarLayout collapsingToolbarLayout, Context context) {

        String url = LARGE_IMG_URL + imageUrl;

        Log.d(TAG, "loadImageFromUrl: " + imageUrl);

        Glide.with(imageView)
            .load(url)
            .listener(GlidePalette.with(url)
                .intoCallBack(palette -> {
                    int scrimColor = palette.getVibrantColor(GlidePalette.Swatch.RGB) == 0 ? ContextCompat.getColor(imageView.getContext(), R.color
                        .colorPrimary) : palette.getVibrantColor(GlidePalette.Swatch.RGB);
                    collapsingToolbarLayout.setContentScrimColor(scrimColor);
                    collapsingToolbarLayout.setStatusBarScrimColor(scrimColor);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ((Activity) context).getWindow().setStatusBarColor(scrimColor);
                    }
                })
            )
            .into(imageView);
    }

    private static final String VIDEO_URL = "https://www.youtube.com/watch?v=";

    private DetailLayoutBinding binding;
    private DetailViewModel viewModel;
    private DetailVideoAdapter videoAdapter;
    private boolean isFavorite;
    private boolean appbarCollapsed;
    private MovieModel movie;

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.detail_layout);

        binding.videosList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        videoAdapter = new DetailVideoAdapter(getLayoutInflater(), this);

        binding.videosList.setAdapter(videoAdapter);

        /*
            Based on stackoverflow answer:
                    https://stackoverflow.com/questions/31872653/how-can-i-determine-that-collapsingtoolbar-is-collapsed
        */
        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            boolean old = appbarCollapsed;
            appbarCollapsed = Math.abs(verticalOffset) == binding.appbar.getTotalScrollRange();
            if (old != appbarCollapsed) {
                changeToolbar();

            }
        });

        binding.favoriteFAB.setOnClickListener(v -> viewModel.changeFavorite());

        viewModel = ViewModelProviders.of(this, viewModelProviderFactory).get(DetailViewModel.class);

        viewModel.getVideos().observe(this, this::handleVideoResult);

        viewModel.getReviews().observe(this, this::handleReviewResult);

        viewModel.getFavoriteStatus().observe(this, this::handleFavorite);

        viewModel.getDetail().observe(this, this::handleDetails);


        Intent intent = getIntent();

        movie = intent.getParcelableExtra(BrowseActivity.EXTRA_MOVIE);

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (movie != null) {
            binding.setMovie(movie);
            viewModel.loadDetailsForMovie(movie);
        } else {
            Toast.makeText(this, R.string.movie_error, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleDetails(Result<MovieDetailModel> movieDetailModelResult) {
        if (movieDetailModelResult.data.size() > 0) {
            binding.setDetail(movieDetailModelResult.data.get(0));
        }
    }

    private void changeToolbar() {
        invalidateOptionsMenu();
        if (appbarCollapsed) {
            binding.collapsingToolbar.setTitle(movie.getTitle());
        } else {
            binding.collapsingToolbar.setTitle(" ");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        if (appbarCollapsed) {
            MenuItem favorite = menu.findItem(R.id.menu_favorite);
            if (favorite == null) {
                favorite = menu.add(Menu.NONE, R.id.menu_favorite, 0, R.string.favorite_menu);
            }

            favorite.setIcon(isFavorite ? R.drawable.ic_star_24dp : R.drawable.ic_star_border_24dp);
        } else {
            menu.removeItem(R.id.menu_favorite);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_favorite) {
            viewModel.changeFavorite();
        }
        return false;
    }

    private void handleVideoResult(Result<VideoModel> result) {
        switch (result.state) {
            case Result.STATE_LOADING: {
                binding.videoProgressBar.setVisibility(View.VISIBLE);
                binding.videosList.setVisibility(View.GONE);
                binding.emptyVideoList.setVisibility(View.GONE);
                break;
            }
            case Result.STATE_SUCCESS: {
                binding.videoProgressBar.setVisibility(View.GONE);
                displayVideos(result.data);
                break;
            }
            case Result.STATE_ERROR: {
                binding.videosList.setVisibility(View.VISIBLE);
                binding.videoProgressBar.setVisibility(View.GONE);
                displayVideos(result.data);
                if (Utils.isConnected(this)) {
                    Snackbar.make(binding.videosList, R.string.error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, v -> viewModel.retry())
                        .show();
                } else {
                    Snackbar.make(binding.videosList, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, v -> viewModel.retry())
                        .show();
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Invalid state: " + result.state);
        }

    }

    private void handleReviewResult(Result<ReviewModel> result) {
        switch (result.state) {
            case Result.STATE_LOADING: {
                binding.reviewProgressBar.setVisibility(View.VISIBLE);
                binding.review.setVisibility(View.GONE);
                binding.reviewAuthor.setVisibility(View.GONE);
                binding.moreReviewsBtn.setVisibility(View.GONE);
                break;
            }
            case Result.STATE_SUCCESS: {
                binding.reviewProgressBar.setVisibility(View.GONE);
                displayReview(result.data);
                break;
            }
            case Result.STATE_ERROR: {
                binding.review.setVisibility(View.VISIBLE);
                binding.reviewAuthor.setVisibility(View.GONE);
                binding.reviewProgressBar.setVisibility(View.GONE);
                displayReview(result.data);
                //TODO handle double snackbar if review and movie both send one
                if (Utils.isConnected(this)) {
                    Snackbar.make(binding.videosList, R.string.error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, v -> viewModel.retry())
                        .show();
                } else {
                    Snackbar.make(binding.videosList, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, v -> viewModel.retry())
                        .show();
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Invalid state: " + result.state);
        }

    }

    private void handleFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
        binding.favoriteFAB.setImageResource(isFavorite ? R.drawable.ic_star_24dp : R.drawable.ic_star_border_24dp);
        invalidateOptionsMenu();
    }

    private void displayVideos(List<VideoModel> videos) {

        if (videos == null || videos.isEmpty()) {
            videoAdapter.setVideos(Collections.emptyList());
            binding.videosList.setVisibility(View.GONE);
            binding.emptyVideoList.setVisibility(View.VISIBLE);
        } else {
            videoAdapter.setVideos(videos);
            binding.videosList.setVisibility(View.VISIBLE);
            binding.emptyVideoList.setVisibility(View.GONE);
        }
    }

    private void displayReview(List<ReviewModel> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            binding.review.setText(R.string.no_reviews);
            binding.review.setVisibility(View.VISIBLE);
            binding.moreReviewsBtn.setVisibility(View.GONE);
            binding.reviewAuthor.setVisibility(View.GONE);
        } else {
            final ReviewModel review = reviews.get(0);
            binding.review.setVisibility(View.VISIBLE);
            binding.review.setText(review.getContent());
            binding.reviewAuthor.setVisibility(View.VISIBLE);
            binding.reviewAuthor.setText(review.getAuthor());
            if (reviews.size() > 1) {
                binding.moreReviewsBtn.setVisibility(View.VISIBLE);
                reviews.remove(0);
                binding.moreReviewsBtn.setOnClickListener(v -> showMoreReviews(reviews));
            } else {
                binding.moreReviewsBtn.setVisibility(View.GONE);
            }
        }
    }

    private void showMoreReviews(List<ReviewModel> reviews) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new DetailReviewAdapter(getLayoutInflater(), reviews));
        builder.setView(recyclerView);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
        });
        builder.create().show();
    }

    @Override
    public void onVideoClick(VideoModel video) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(VIDEO_URL + video.getKey())));
    }
}
