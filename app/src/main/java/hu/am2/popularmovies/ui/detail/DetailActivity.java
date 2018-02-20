package hu.am2.popularmovies.ui.detail;


import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.florent37.glidepalette.GlidePalette;

import hu.am2.popularmovies.R;
import hu.am2.popularmovies.data.repository.remote.module.MovieModel;
import hu.am2.popularmovies.databinding.DetailLayoutBinding;
import hu.am2.popularmovies.ui.browser.BrowseActivity;
import hu.am2.popularmovies.ui.browser.BrowserAdapter;

public class DetailActivity extends AppCompatActivity {

    private DetailLayoutBinding binding;


    @BindingAdapter({"app:imageUrl", "app:mainView"})
    public static void loadImageFromUrl(ImageView imageView, String imageUrl, View mainView) {

        String url = BrowserAdapter.IMAGE_URL + imageUrl;

        Glide.with(imageView)
            .load(url)
            .listener(GlidePalette.with(url)
                .use(GlidePalette.Profile.VIBRANT_LIGHT).intoBackground(mainView)
            )
            .into(imageView);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.detail_layout);

        Intent intent = getIntent();

        MovieModel movie = intent.getParcelableExtra(BrowseActivity.EXTRA_MOVIE);

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (movie != null) {
            binding.setMovie(movie);
        } else {
            Toast.makeText(this, R.string.movie_error, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
