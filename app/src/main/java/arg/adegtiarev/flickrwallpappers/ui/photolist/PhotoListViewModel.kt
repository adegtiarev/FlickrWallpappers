package arg.adegtiarev.flickrwallpappers.ui.photolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import arg.adegtiarev.flickrwallpappers.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SelectedTab {
    HOME, FAVORITES
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(SelectedTab.HOME)
    val selectedTab: StateFlow<SelectedTab> = _selectedTab.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val pagedPhotos: Flow<PagingData<Photo>> = photoRepository
        .getPhotos()
        .cachedIn(viewModelScope)

    val favoritePhotos: StateFlow<List<Photo>> = photoRepository
        .getFavoritePhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val searchedPhotos: Flow<PagingData<Photo>> = _searchQuery
        .debounce(500) // Wait for 500ms of no new input before triggering the search
        .flatMapLatest { query ->
            photoRepository.searchPhotos(query)
        }
        .cachedIn(viewModelScope)

    fun onTabSelected(tab: SelectedTab) {
        _selectedTab.value = tab
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearchActiveChange(isActive: Boolean) {
        _isSearchActive.value = isActive
        if (!isActive) {
            _searchQuery.value = ""
        }
    }

    /**
     * Caches the selected photo before navigating to the detail screen.
     * This ensures that the detail screen can find the photo in the local database.
     */
    fun onPhotoClicked(photo: Photo) {
        viewModelScope.launch {
            photoRepository.cachePhoto(photo)
        }
    }
}
