package hu.am2.popularmovies.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import hu.am2.popularmovies.App;

@Singleton
@Component(modules = {NetworkModule.class, AppModule.class, AndroidInjectionModule.class, ActivityModule.class, ViewModelModule.class})
public interface AppComponent {

    void inject(App app);

    @Component.Builder
    interface Builder {
        AppComponent build();

        @BindsInstance
        Builder context(Context context);
    }
}
