package arg.adegtiarev.flickrwallpappers.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Представляет сущность фотографии в локальной базе данных.
 *
 * @param id Уникальный идентификатор фотографии (используется как первичный ключ).
 * @param title Название фотографии.
 * @param url URL для загрузки изображения (маленький размер).
 * @param owner Имя владельца фотографии.
 * @param isFavorite Флаг, указывающий, добавлена ли фотография в избранное.
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
