package arg.adegtiarev.flickrwallpappers.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for storing remote pagination keys.
 * Helps Paging 3 understand which page of data to load next.
 *
 * @param photoId ID of the photo to which the key is attached. Primary key.
 * @param prevKey Key for loading the previous page. Null if it is the first page.
 * @param nextKey Key for loading the next page. Null if it is the last page.
 */
@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val photoId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
