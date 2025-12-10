package arg.adegtiarev.flickrwallpappers.data.remote

import arg.adegtiarev.flickrwallpappers.data.remote.dto.FlickrResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for interacting with the Flickr API using Retrofit.
 */
interface FlickrApiService {

    /**
     * Fetches a list of interesting photos.
     *
     * @param page The page number for pagination.
     */
    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun fetchPhotos(@Query("page") page: Int = 1): FlickrResponse

    /**
     * Searches for photos by a text query.
     *
     * @param query The text to search for.
     * @param page The page number for pagination.
     */
    @GET("services/rest?method=flickr.photos.search")
    suspend fun searchPhotos(@Query("text") query: String, @Query("page") page: Int = 1): FlickrResponse
}
