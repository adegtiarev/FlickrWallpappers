package arg.adegtiarev.flickrwallpappers.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for working with Photo entities in the database.
 */
@Dao
interface PhotoDao {

    /**
     * Inserts a list of photos into the database.
     * If a photo with the same id already exists, it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<Photo>)

    /**
     * Updates an existing photo in the database.
     */
    @Update
    suspend fun update(photo: Photo)

    /**
     * Returns a PagingSource for getting photos as paged data.
     */
    @Query("SELECT * FROM photos")
    fun pagingSource(): PagingSource<Int, Photo>

    /**
     * Returns a Flow with a list of all photos marked as favorites.
     */
    @Query("SELECT * FROM photos WHERE isFavorite = 1")
    fun getFavoritePhotos(): Flow<List<Photo>>

    /**
     * Returns a Flow with a single photo by its ID.
     */
    @Query("SELECT * FROM photos WHERE id = :photoId")
    fun getPhotoById(photoId: String): Flow<Photo?>

    /**
     * Returns a list of photos matching the given IDs.
     */
    @Query("SELECT * FROM photos WHERE id IN (:photoIds)")
    suspend fun getPhotosByIds(photoIds: List<String>): List<Photo>

    /**
     * Deletes all photos from the table.
     */
    @Query("DELETE FROM photos")
    suspend fun clearAll()
}
