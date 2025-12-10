package arg.adegtiarev.flickrwallpappers.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность для хранения ключей удаленной пагинации.
 * Помогает Paging 3 понять, какую страницу данных загружать следующей.
 *
 * @param photoId ID фотографии, к которой привязан ключ. Первичный ключ.
 * @param prevKey Ключ для загрузки предыдущей страницы. Null, если это первая страница.
 * @param nextKey Ключ для загрузки следующей страницы. Null, если это последняя страница.
 */
@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val photoId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
