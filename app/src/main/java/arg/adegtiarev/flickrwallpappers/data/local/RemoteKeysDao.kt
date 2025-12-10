package arg.adegtiarev.flickrwallpappers.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import arg.adegtiarev.flickrwallpappers.data.local.model.RemoteKeys

/**
 * Data Access Object (DAO) for working with remote pagination keys [RemoteKeys].
 */
@Dao
interface RemoteKeysDao {

    /**
     * Inserts a list of keys. If a key for a photoId already exists, it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    /**
     * Gets the remote pagination key by photo ID.
     */
    @Query("SELECT * FROM remote_keys WHERE photoId = :photoId")
    suspend fun getRemoteKeysByPhotoId(photoId: String): RemoteKeys?

    /**
     * Clears the entire table with keys.
     */
    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}
