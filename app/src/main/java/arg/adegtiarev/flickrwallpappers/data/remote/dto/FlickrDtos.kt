package arg.adegtiarev.flickrwallpappers.data.remote.dto

import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import com.google.gson.annotations.SerializedName

/**
 * Top-level class for the Flickr API response.
 */
data class FlickrResponse(
    val photos: PhotosDto,
    val stat: String
)

/**
 * Contains metadata and a list of DTOs for photos.
 */
data class PhotosDto(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: Int,
    val photo: List<PhotoDto>
)

/**
 * DTO for a single photo. Contains fields received from the server.
 */
data class PhotoDto(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val farm: Int,
    val title: String,
    @SerializedName("url_s")
    val url: String
)

/**
 * Mapper function to convert the network model [PhotoDto] to the local entity [Photo].
 */
fun PhotoDto.toEntity(): Photo {
    return Photo(
        id = this.id,
        title = this.title,
        url = this.url,
        owner = this.owner
    )
}
