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
 * Data Access Object (DAO) для работы с сущностями Photo в базе данных.
 */
@Dao
interface PhotoDao {

    /**
     * Вставляет список фотографий в базу данных.
     * Если фотография с таким же id уже существует, она будет заменена.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<Photo>)

    /**
     * Обновляет существующую фотографию в базе данных.
     */
    @Update
    suspend fun update(photo: Photo)

    /**
     * Возвращает PagingSource для получения фотографий в виде постраничных данных.
     */
    @Query("SELECT * FROM photos")
    fun pagingSource(): PagingSource<Int, Photo>

    /**
     * Возвращает Flow со списком всех фотографий, отмеченных как избранные.
     */
    @Query("SELECT * FROM photos WHERE isFavorite = 1")
    fun getFavoritePhotos(): Flow<List<Photo>>

    /**
     * Возвращает Flow с одной фотографией по её ID.
     */
    @Query("SELECT * FROM photos WHERE id = :photoId")
    fun getPhotoById(photoId: String): Flow<Photo?>

    /**
     * Удаляет все фотографии из таблицы.
     */
    @Query("DELETE FROM photos")
    suspend fun clearAll()
}
