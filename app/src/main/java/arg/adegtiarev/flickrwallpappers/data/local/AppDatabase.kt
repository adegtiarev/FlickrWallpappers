package arg.adegtiarev.flickrwallpappers.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import arg.adegtiarev.flickrwallpappers.data.local.model.RemoteKeys

/**
 * The main database class for the application.
 * Defines the list of entities and the database version.
 */
@Database(
    entities = [Photo::class, RemoteKeys::class], // Add RemoteKeys
    version = 2, // Increment version due to schema change
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to the DAO for working with photos.
     */
    abstract fun photoDao(): PhotoDao

    /**
     * Provides access to the DAO for working with remote pagination keys.
     */
    abstract fun remoteKeysDao(): RemoteKeysDao
}
