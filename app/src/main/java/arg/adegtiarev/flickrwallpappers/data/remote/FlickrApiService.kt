package arg.adegtiarev.flickrwallpappers.data.remote

import arg.adegtiarev.flickrwallpappers.data.remote.dto.FlickrResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Интерфейс для взаимодействия с Flickr API с помощью Retrofit.
 */
interface FlickrApiService {

    /**
     * Получает список интересных фотографий.
     *
     * @param page Номер страницы для пагинации.
     */
    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun fetchPhotos(@Query("page") page: Int = 1): FlickrResponse

    /**
     * Выполняет поиск фотографий по текстовому запросу.
     *
     * @param query Текст для поиска.
     * @param page Номер страницы для пагинации.
     */
    @GET("services/rest?method=flickr.photos.search")
    suspend fun searchPhotos(@Query("text") query: String, @Query("page") page: Int = 1): FlickrResponse
}
