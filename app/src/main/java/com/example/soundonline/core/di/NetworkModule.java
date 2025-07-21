    package com.example.soundonline.core.di;
    
    import com.example.soundonline.core.utils.Constants;
    import com.example.soundonline.network.ApiService;
    import android.content.Context;
    import android.content.SharedPreferences;

    import dagger.hilt.android.qualifiers.ApplicationContext;


    import okhttp3.OkHttpClient;
    import okhttp3.Request;


    import java.util.concurrent.TimeUnit;



    
    import javax.inject.Singleton;
    
    import dagger.Module;
    import dagger.Provides;
    import dagger.hilt.InstallIn;

    import dagger.hilt.components.SingletonComponent;

    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;

    @Module
    @InstallIn(SingletonComponent.class)
    public class NetworkModule {

        @Provides
        @Singleton
        public static OkHttpClient provideOkHttpClient(@ApplicationContext Context context) {
            return new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();

                        // Lấy JWT từ SharedPreferences
                        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                        String token = prefs.getString("jwt_token", "");

                        // Đính kèm Authorization header
                        Request request = original.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    })
                    .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                    .build();
        }

        @Provides
        @Singleton
        public static Retrofit provideRetrofit(OkHttpClient okHttpClient) {
            return new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        @Provides
        @Singleton
        public static ApiService provideApiService(Retrofit retrofit) {
            return retrofit.create(ApiService.class);
        }
    }

