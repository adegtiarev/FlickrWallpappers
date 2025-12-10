package arg.adegtiarev.flickrwallpappers.di

import android.content.Context
import androidx.room.Room
import arg.adegtiarev.flickrwallpappers.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "flickr_database"
        )
        // When migrating from version 1 to 2, we just delete the old data.
        // In a real application, there would be more complex migration logic here.
        .fallbackToDestructiveMigration()
        .build()
    }
}