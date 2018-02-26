package hu.am2.popularmovies.di;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dagger.Module;
import dagger.Provides;

@Module
class AppModule {

    @Provides
    SharedPreferences providesSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    ContentResolver providerContentResolver(Context context) {
        return context.getContentResolver();
    }
}
