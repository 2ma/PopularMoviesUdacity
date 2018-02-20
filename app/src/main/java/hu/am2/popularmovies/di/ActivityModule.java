package hu.am2.popularmovies.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import hu.am2.popularmovies.ui.browser.BrowseActivity;

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract BrowseActivity contributesBrowseActivity();
}
