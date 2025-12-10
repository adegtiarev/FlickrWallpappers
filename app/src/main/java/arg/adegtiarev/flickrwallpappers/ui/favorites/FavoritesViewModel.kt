package arg.adegtiarev.flickrwallpappers.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import arg.adegtiarev.flickrwallpappers.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    photoRepository: PhotoRepository
) : ViewModel() {

    /**
     * A stream with a list of favorite photos.
     * `stateIn` converts a cold Flow into a hot StateFlow, which
     * stores the last value and provides it to new subscribers.
     */
    val favoritePhotos: StateFlow<List<Photo>> = photoRepository
        .getFavoritePhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Starts and stops data collection when subscribers are present/absent
            initialValue = emptyList() // Initial value is an empty list
        )
}
