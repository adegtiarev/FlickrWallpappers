package arg.adegtiarev.flickrwallpappers.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo

/**
 * Основной класс базы данных приложения.
 * Определяет список сущностей и версию базы данных.
 */
@Database(
    entities = [Photo::class],
    version = 1,
    exportSchema = false // Схему экспортировать не будем для упрощения
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Предоставляет доступ к DAO для работы с фотографиями.
     */
    abstract fun photoDao(): PhotoDao
}
