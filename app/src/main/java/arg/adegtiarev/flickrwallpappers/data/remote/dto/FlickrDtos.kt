package arg.adegtiarev.flickrwallpappers.data.remote.dto

import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import com.google.gson.annotations.SerializedName

/**
 * Верхнеуровневый класс для ответа от Flickr API.
 */
data class FlickrResponse(
    val photos: PhotosDto,
    val stat: String
)

/**
 * Содержит метаданные и список DTO для фотографий.
 */
data class PhotosDto(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: Int,
    val photo: List<PhotoDto>
)

/**
 * DTO для отдельной фотографии. Содержит поля, получаемые от сервера.
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
 * Функция-маппер для преобразования сетевой модели [PhotoDto] в локальную сущность [Photo].
 */
fun PhotoDto.toEntity(): Photo {
    return Photo(
        id = this.id,
        title = this.title,
        url = this.url,
        owner = this.owner
    )
}
