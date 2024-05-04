package hu.ait.tastebuddies.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.ait.tastebuddies.network.FoodAPI
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(
                Json{
                    ignoreUnknownKeys = true
                }.asConverterFactory("application/json".toMediaType())
            )
            .baseUrl("https://api.spoonacular.com/")
            .build()
    }

    @Provides
    @Singleton
    fun provideMarsAPI(retrofit: Retrofit): FoodAPI {
        return retrofit.create(FoodAPI::class.java) // Retrofit creates a Java instance from this interface
    }
}