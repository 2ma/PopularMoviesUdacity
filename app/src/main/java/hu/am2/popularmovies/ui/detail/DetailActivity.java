package hu.am2.popularmovies.ui.detail;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import hu.am2.popularmovies.data.repository.remote.module.MovieModel;
import hu.am2.popularmovies.data.repository.remote.module.ReviewModel;
import hu.am2.popularmovies.data.repository.remote.module.VideoModel;
import hu.am2.popularmovies.databinding.DetailLayoutBinding;
import hu.am2.popularmovies.domain.Result;
import hu.am2.popularmovies.ui.browser.BrowseActivity;
import hu.am2.popularmovies.ui.browser.BrowserAdapter;

public class DetailActivity extends AppCompatActivity implements DetailVideoAdapter.VideoClickListener {

    @BindingAdapter({"app:imageUrl", "app:mainView"})
    public static void loadImageFromUrlWithPalette(ImageView imageView, String imageUrl, View mainView) {

        String url = BrowserAdapter.IMAGE_URL + imageUrl;

        Glide.with(imageView)
            .load(url)
            .listener(GlidePalette.with(url)
                .use(GlidePalette.Profile.VIBRANT_LIGHT).intoBackground(mainView)
            )
            .into(imageView);
    }

    @BindingAdapter({"app:imageUrl"})
    public static void loadImageFromUrl(ImageView imageView, String imageUrl) {

        String url = BrowserAdapter.IMAGE_URL + imageUrl;

        Glide.with(imageView)
            .load(url)
            .into(imageView);
    }

    private static final String VIDEO_URL = "https://www.youtube.com/watch?v=";

    private DetailLayoutBinding binding;
    private DetailViewModel viewModel;
    private DetailVideoAdapter videoAdapter;
    private boolean isFavorite;

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

        viewModel = ViewModelProviders.of(this, viewModelProviderFactory).get(DetailViewModel.class);

        viewModel.getVideos().observe(this, this::handleVideoResult);

        viewModel.getReviews().observe(this, this::handleReviewResult);

        viewModel.getFavoriteStatus().observe(this, this::handleFavorite);


        Intent intent = getIntent();

        MovieModel movie = intent.getParcelableExtra(BrowseActivity.EXTRA_MOVIE);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem favorite = menu.findItem(R.id.menu_favorite);

        favorite.setIcon(isFavorite ? R.drawable.ic_star_24dp : R.drawable.ic_star_border_24dp);

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
