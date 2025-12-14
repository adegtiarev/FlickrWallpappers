package arg.adegtiarev.flickrwallpappers.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import arg.adegtiarev.flickrwallpappers.data.remote.dto.toEntity
import retrofit2.HttpException
import java.io.IOException

private const val FLICKR_STARTING_PAGE_INDEX = 1

/**
 * A PagingSource that fetches search results from the Flickr API.
 * This is used for online-only search.
 */
class FlickrSearchPagingSource(
    private val flickrApiService: FlickrApiService,
    private val query: String
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        // Don't search for an empty query
        if (query.isBlank()) {
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }

        val page = params.key ?: FLICKR_STARTING_PAGE_INDEX
        return try {
            val response = flickrApiService.searchPhotos(query = query, page = page)
            val photos = response.photos.photo.map { it.toEntity() }
            LoadResult.Page(
                data = photos,
                prevKey = if (page == FLICKR_STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (photos.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
