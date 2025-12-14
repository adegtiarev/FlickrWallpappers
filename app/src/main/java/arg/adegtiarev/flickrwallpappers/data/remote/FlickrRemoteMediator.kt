package arg.adegtiarev.flickrwallpappers.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import arg.adegtiarev.flickrwallpappers.data.local.AppDatabase
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import arg.adegtiarev.flickrwallpappers.data.local.model.RemoteKeys
import arg.adegtiarev.flickrwallpappers.data.remote.dto.toEntity
import retrofit2.HttpException
import java.io.IOException

/**
 * RemoteMediator to manage loading data from the network into the local database.
 */
@OptIn(ExperimentalPagingApi::class)
class FlickrRemoteMediator(
    private val flickrApiService: FlickrApiService,
    private val database: AppDatabase,
) : RemoteMediator<Int, Photo>() {

    private val photoDao = database.photoDao()
    private val remoteKeysDao = database.remoteKeysDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Photo>): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getLastRemoteKey(state)
                    remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = flickrApiService.fetchPhotos(page = loadKey)
            val photosDto = response.photos.photo
            val endOfPaginationReached = photosDto.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // Don't clear the whole table, as it would delete all favorites.
                    // We will just replace the items from the network.
                }

                // Get current favorite statuses from the DB
                val photoIds = photosDto.map { it.id }
                val favoritePhotos = photoDao.getPhotosByIds(photoIds).filter { it.isFavorite }
                val favoritePhotoIds = favoritePhotos.map { it.id }.toSet()

                val prevKey = if (loadKey == 1) null else loadKey - 1
                val nextKey = if (endOfPaginationReached) null else loadKey + 1

                val keys = photosDto.map {
                    RemoteKeys(photoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                // Merge network data with local favorite status
                val entities = photosDto.map {
                    val isFavorite = it.id in favoritePhotoIds
                    it.toEntity().copy(isFavorite = isFavorite)
                }

                remoteKeysDao.insertAll(keys)
                photoDao.insertAll(entities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, Photo>): RemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { photo -> remoteKeysDao.getRemoteKeysByPhotoId(photo.id) }
    }
}
