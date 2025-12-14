package arg.adegtiarev.flickrwallpappers.ui.photolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import arg.adegtiarev.flickrwallpappers.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class SelectedTab {
    HOME, FAVORITES
}

@HiltViewModel
class PhotoListViewModel @Inject constructor(
    photoRepository: PhotoRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(SelectedTab.HOME)
    val selectedTab: StateFlow<SelectedTab> = _selectedTab.asStateFlow()

    /**
     * A stream of paged photo data for the home screen.
     * `cachedIn(viewModelScope)` ensures the data survives configuration changes
     * and navigation, as long as the ViewModel lives.
     */
    val pagedPhotos: Flow<PagingData<Photo>> = photoRepository
        .getPhotos()
        .cachedIn(viewModelScope)

    /**
     * A stream of favorite photos.
     * `stateIn` converts it to a hot StateFlow, suitable for collection in the UI.
     */
    val favoritePhotos: StateFlow<List<Photo>> = photoRepository
        .getFavoritePhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onTabSelected(tab: SelectedTab) {
        _selectedTab.value = tab
    }
}
