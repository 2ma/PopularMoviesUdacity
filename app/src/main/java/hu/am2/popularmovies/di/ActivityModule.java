package hu.am2.popularmovies.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import hu.am2.popularmovies.ui.browser.BrowseActivity;
import hu.am2.popularmovies.ui.detail.DetailActivity;

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract BrowseActivity contributesBrowseActivity();

    @ContributesAndroidInjector
    abstract DetailActivity contributesDetailActivity();
}
