package hu.am2.popularmovies.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hu.am2.popularmovies.R;
import hu.am2.popularmovies.data.repository.remote.api.TMDBService;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
class NetworkModule {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(final Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(chain -> {

            Request request = chain.request();
            HttpUrl url = request.url();

            HttpUrl newUrl = url.newBuilder()
                .addQueryParameter("api_key", context.getString(R.string.api_key))
                .build();

            return chain.proceed(request.newBuilder().url(newUrl).build());
        })
            .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC));

        return builder.build();
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build();
    }

    @Provides
    @Singleton
    TMDBService providesTMDBService(Retrofit retrofit) {
        return retrofit.create(TMDBService.class);
    }
}
