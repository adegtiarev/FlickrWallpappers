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
        // При миграции с версии 1 на 2 мы просто удаляем старые данные. 
        // В реальном приложении здесь была бы более сложная логика миграции.
        .fallbackToDestructiveMigration()
        .build()
    }
}