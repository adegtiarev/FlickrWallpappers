package arg.adegtiarev.flickrwallpappers.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import arg.adegtiarev.flickrwallpappers.data.local.AppDatabase
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import arg.adegtiarev.flickrwallpappers.data.remote.FlickrApiService
import arg.adegtiarev.flickrwallpappers.data.remote.FlickrRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с фотографиями. Единственный источник данных для ViewModel.
 */
@Singleton
class PhotoRepository @Inject constructor(
    private val database: AppDatabase,
    private val flickrApiService: FlickrApiService
) {
    private val photoDao = database.photoDao()

    /**
     * Возвращает Flow с постраничными данными фотографий.
     * Использует RemoteMediator для кэширования данных из сети в локальную базу.
     */
    @OptIn(ExperimentalPagingApi::class)
    fun getPhotos(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false
            ),
            remoteMediator = FlickrRemoteMediator(
                flickrApiService = flickrApiService,
                database = database
            ),
            pagingSourceFactory = { photoDao.pagingSource() }
        ).flow
    }

    /**
     * Возвращает Flow со списком избранных фотографий.
     */
    fun getFavoritePhotos(): Flow<List<Photo>> {
        return photoDao.getFavoritePhotos()
    }

    /**
     * Возвращает Flow с одной фотографией по её ID.
     */
    fun getPhotoById(photoId: String): Flow<Photo?> {
        return photoDao.getPhotoById(photoId)
    }

    /**
     * Обновляет статус "избранное" для фотографии.
     */
    suspend fun updatePhoto(photo: Photo) {
        photoDao.update(photo)
    }
}
