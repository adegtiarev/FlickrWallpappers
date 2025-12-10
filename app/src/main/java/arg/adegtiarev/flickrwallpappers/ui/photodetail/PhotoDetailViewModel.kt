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

    // Получаем photoId из аргументов навигации. 
    // "photoId" - это имя аргумента, которое мы определим в графе навигации.
    private val photoId: String = savedStateHandle.get<String>("photoId")!!

    /**
     * Поток с данными о текущей фотографии.
     */
    val photo: StateFlow<Photo?> = photoRepository
        .getPhotoById(photoId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Начальное значение - null, пока данные не загрузятся
        )

    /**
     * Переключает статус "избранное" для текущей фотографии.
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
