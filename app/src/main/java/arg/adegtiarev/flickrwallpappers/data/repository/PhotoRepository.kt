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
 * Repository for working with photos. The single source of truth for ViewModels.
 */
@Singleton
class PhotoRepository @Inject constructor(
    private val database: AppDatabase,
    private val flickrApiService: FlickrApiService
) {
    private val photoDao = database.photoDao()

    /**
     * Returns a Flow of PagingData of photos.
     * Uses RemoteMediator to cache data from the network into the local database.
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
     * Returns a Flow with a list of favorite photos.
     */
    fun getFavoritePhotos(): Flow<List<Photo>> {
        return photoDao.getFavoritePhotos()
    }

    /**
     * Returns a Flow with a single photo by its ID.
     */
    fun getPhotoById(photoId: String): Flow<Photo?> {
        return photoDao.getPhotoById(photoId)
    }

    /**
     * Updates the "favorite" status for a photo.
     */
    suspend fun updatePhoto(photo: Photo) {
        photoDao.update(photo)
    }
}
