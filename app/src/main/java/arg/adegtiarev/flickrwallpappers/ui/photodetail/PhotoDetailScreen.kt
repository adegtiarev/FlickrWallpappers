package arg.adegtiarev.flickrwallpappers.ui.photodetail

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import arg.adegtiarev.flickrwallpappers.R
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    navController: NavController,
    viewModel: PhotoDetailViewModel = hiltViewModel()
) {
    val photo by viewModel.photo.collectAsState()
    val context = LocalContext.current

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(Unit) {
        viewModel.setWallpaperEvent.collectLatest { event ->
            when (event) {
                is SetWallpaperEvent.Success -> {
                    context.startActivity(event.intent)
                }
                is SetWallpaperEvent.Error -> {
                    Toast.makeText(context, context.getString(R.string.failed_to_set_wallpaper), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(photo?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        val favoriteIcon = if (photo?.isFavorite == true) {
                            painterResource(id = R.drawable.ic_favorite_filled)
                        } else {
                            painterResource(id = R.drawable.ic_favorite_border)
                        }
                        Icon(
                            painter = favoriteIcon,
                            contentDescription = stringResource(id = R.string.favorites)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onSetWallpaperClicked() }) {
                Icon(painterResource(id = R.drawable.ic_set_wallpaper), contentDescription = stringResource(id = R.string.set_wallpaper))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (photo == null) {
                CircularProgressIndicator()
            } else {
                AsyncImage(
                    model = photo?.largeImageUrl,
                    contentDescription = photo?.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .transformable(state = transformableState)
                )
            }
        }
    }
}
