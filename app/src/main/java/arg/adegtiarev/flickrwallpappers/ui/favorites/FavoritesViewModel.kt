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
     * Поток со списком избранных фотографий.
     * `stateIn` преобразует холодный Flow в горячий StateFlow, который
     * хранит последнее значение и отдает его новым подписчикам.
     */
    val favoritePhotos: StateFlow<List<Photo>> = photoRepository
        .getFavoritePhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Начинает и останавливает сбор данных при наличии/отсутствии подписчиков
            initialValue = emptyList() // Начальное значение - пустой список
        )
}
