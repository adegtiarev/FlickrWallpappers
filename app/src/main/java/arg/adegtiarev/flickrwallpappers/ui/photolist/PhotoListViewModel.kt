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
     * A stream of paged photo data for display in the UI.
     * `cachedIn(viewModelScope)` caches the data so that it survives
     * configuration changes.
     */
    val photos: Flow<PagingData<Photo>> = photoRepository
        .getPhotos()
        .cachedIn(viewModelScope)
}
