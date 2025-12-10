package arg.adegtiarev.flickrwallpappers.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import arg.adegtiarev.flickrwallpappers.data.local.model.RemoteKeys

/**
 * Data Access Object (DAO) для работы с ключами удаленной пагинации [RemoteKeys].
 */
@Dao
interface RemoteKeysDao {

    /**
     * Вставляет список ключей. Если ключ для photoId уже существует, он будет заменен.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    /**
     * Получает ключ удаленной пагинации по ID фотографии.
     */
    @Query("SELECT * FROM remote_keys WHERE photoId = :photoId")
    suspend fun getRemoteKeysByPhotoId(photoId: String): RemoteKeys?

    /**
     * Очищает всю таблицу с ключами.
     */
    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}
