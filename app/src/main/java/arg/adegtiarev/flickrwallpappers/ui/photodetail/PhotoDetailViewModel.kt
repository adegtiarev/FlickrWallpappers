package arg.adegtiarev.flickrwallpappers.ui.photodetail

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arg.adegtiarev.flickrwallpappers.R
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import arg.adegtiarev.flickrwallpappers.data.repository.PhotoRepository
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed class SetWallpaperEvent {
    data class Success(val intent: Intent) : SetWallpaperEvent()
    object Error : SetWallpaperEvent()
}

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val photoRepository: PhotoRepository,
    private val imageLoader: ImageLoader,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val photoId: String = checkNotNull(savedStateHandle["photoId"])

    val photo: StateFlow<Photo?> = photoRepository
        .getPhotoById(photoId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _setWallpaperEvent = Channel<SetWallpaperEvent>()
    val setWallpaperEvent = _setWallpaperEvent.receiveAsFlow()

    fun toggleFavorite() {
        viewModelScope.launch {
            photo.value?.let { currentPhoto ->
                val updatedPhoto = currentPhoto.copy(isFavorite = !currentPhoto.isFavorite)
                photoRepository.updatePhoto(updatedPhoto)
            }
        }
    }

    fun onSetWallpaperClicked() {
        viewModelScope.launch {
            val currentPhoto = photo.value ?: return@launch

            val request = ImageRequest.Builder(context)
                .data(currentPhoto.largeImageUrl)
                .allowHardware(false) // Required for converting to Bitmap
                .build()

            val result = imageLoader.execute(request).drawable
            if (result !is BitmapDrawable) {
                _setWallpaperEvent.send(SetWallpaperEvent.Error)
                return@launch
            }

            val bitmap = result.bitmap

            try {
                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs()
                val file = File(cachePath, "wallpaper.png")
                file.outputStream().use {
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
                }

                val contentUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

                val intent = Intent(Intent.ACTION_ATTACH_DATA).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    setDataAndType(contentUri, "image/png")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                _setWallpaperEvent.send(SetWallpaperEvent.Success(Intent.createChooser(intent, context.getString(R.string.set_as))))

            } catch (e: Exception) {
                _setWallpaperEvent.send(SetWallpaperEvent.Error)
            }
        }
    }
}
