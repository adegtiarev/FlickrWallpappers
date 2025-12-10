package arg.adegtiarev.flickrwallpappers.ui.photolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import arg.adegtiarev.flickrwallpappers.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    /**
     * Поток постраничных данных фотографий для отображения в UI.
     * `cachedIn(viewModelScope)` кэширует данные, чтобы они переживали
     * изменения конфигурации.
     */
    val photos: Flow<PagingData<Photo>> = photoRepository
        .getPhotos()
        .cachedIn(viewModelScope)
}
