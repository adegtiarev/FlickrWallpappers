package arg.adegtiarev.flickrwallpappers.ui.photodetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import arg.adegtiarev.flickrwallpappers.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get photoId from navigation arguments.
    // "photoId" is the argument name we will define in the navigation graph.
    private val photoId: String = checkNotNull(savedStateHandle["photoId"])

    /**
     * A stream with the data of the current photo.
     */
    val photo: StateFlow<Photo?> = photoRepository
        .getPhotoById(photoId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Initial value is null until the data is loaded
        )

    /**
     * Toggles the "favorite" status for the current photo.
     */
    fun toggleFavorite() {
        viewModelScope.launch {
            photo.value?.let { currentPhoto ->
                val updatedPhoto = currentPhoto.copy(isFavorite = !currentPhoto.isFavorite)
                photoRepository.updatePhoto(updatedPhoto)
            }
        }
    }
}
