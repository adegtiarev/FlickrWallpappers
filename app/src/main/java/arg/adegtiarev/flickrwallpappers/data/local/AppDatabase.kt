package arg.adegtiarev.flickrwallpappers.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import arg.adegtiarev.flickrwallpappers.data.local.model.RemoteKeys

/**
 * Основной класс базы данных приложения.
 * Определяет список сущностей и версию базы данных.
 */
@Database(
    entities = [Photo::class, RemoteKeys::class], // Добавляем RemoteKeys
    version = 2, // Увеличиваем версию из-за изменения схемы
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Предоставляет доступ к DAO для работы с фотографиями.
     */
    abstract fun photoDao(): PhotoDao

    /**
     * Предоставляет доступ к DAO для работы с ключами удаленной пагинации.
     */
    abstract fun remoteKeysDao(): RemoteKeysDao
}
