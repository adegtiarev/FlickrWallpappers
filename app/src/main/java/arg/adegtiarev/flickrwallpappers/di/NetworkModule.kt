package arg.adegtiarev.flickrwallpappers.di

import arg.adegtiarev.flickrwallpappers.BuildConfig
import arg.adegtiarev.flickrwallpappers.data.remote.FlickrApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.flickr.com/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest: Request = chain.request()
            val originalHttpUrl: HttpUrl = originalRequest.url

            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", BuildConfig.FLICKR_API_KEY) // Используем ключ из BuildConfig
                .addQueryParameter("format", "json")
                .addQueryParameter("nojsoncallback", "1")
                .addQueryParameter("extras", "url_s")
                .build()

            val requestBuilder: Request.Builder = originalRequest.newBuilder()
                .url(url)

            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFlickrApiService(retrofit: Retrofit): FlickrApiService {
        return retrofit.create(FlickrApiService::class.java)
    }
}