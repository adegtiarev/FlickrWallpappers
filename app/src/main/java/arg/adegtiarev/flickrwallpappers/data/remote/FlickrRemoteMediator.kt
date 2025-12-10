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
 * RemoteMediator для управления загрузкой данных из сети в локальную базу данных.
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
            // 1. Определяем, какую страницу загружать
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1 // При обновлении всегда начинаем с первой страницы
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true) // Мы не загружаем данные в начало списка
                LoadType.APPEND -> {
                    val remoteKeys = getLastRemoteKey(state)
                    remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            // 2. Загружаем данные из сети
            val response = flickrApiService.fetchPhotos(page = loadKey)
            val photosDto = response.photos.photo
            val endOfPaginationReached = photosDto.isEmpty()

            // 3. Сохраняем данные в базу данных в рамках одной транзакции
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    photoDao.clearAll()
                    remoteKeysDao.clearRemoteKeys()
                }

                val prevKey = if (loadKey == 1) null else loadKey - 1
                val nextKey = if (endOfPaginationReached) null else loadKey + 1

                val keys = photosDto.map {
                    RemoteKeys(photoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                val entities = photosDto.map { it.toEntity() }

                remoteKeysDao.insertAll(keys)
                photoDao.insertAll(entities)
            }

            // 4. Возвращаем результат
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
