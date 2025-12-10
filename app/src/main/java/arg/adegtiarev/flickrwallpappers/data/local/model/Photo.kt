package arg.adegtiarev.flickrwallpappers.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a photo entity in the local database.
 *
 * @param id Unique identifier of the photo (used as primary key).
 * @param title The title of the photo.
 * @param url URL for loading the image (small size).
 * @param owner The name of the photo owner.
 * @param isFavorite Flag indicating whether the photo has been added to favorites.
 */
@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey
    val id: String,
    val title: String,
    val url: String,
    val owner: String,
    val isFavorite: Boolean = false
)
