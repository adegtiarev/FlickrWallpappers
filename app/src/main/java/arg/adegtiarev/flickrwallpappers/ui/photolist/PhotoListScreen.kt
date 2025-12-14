package arg.adegtiarev.flickrwallpappers.ui.photolist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import arg.adegtiarev.flickrwallpappers.R
import arg.adegtiarev.flickrwallpappers.data.local.model.Photo
import arg.adegtiarev.flickrwallpappers.ui.navigation.Screen
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListScreen(
    navController: NavController,
    viewModel: PhotoListViewModel
) {
    val selectedTab by viewModel.selectedTab.collectAsState()

    // Create and remember the grid state. It will be saved and restored across configuration changes and navigation.
    val gridState: LazyGridState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedTab == SelectedTab.HOME) "Flickr Wallpapers" else "Favorites") },
                actions = {
                    if (selectedTab == SelectedTab.HOME) {
                        IconButton(onClick = { /* TODO: Implement search functionality */ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "Search"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.onTabSelected(SelectedTab.HOME) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_home),
                            contentDescription = "Home"
                        )
                    }
                    IconButton(onClick = { viewModel.onTabSelected(SelectedTab.FAVORITES) }) {
                        Icon(
                            painter = painterResource(id = if (selectedTab == SelectedTab.FAVORITES) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border),
                            contentDescription = "Favorites"
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                SelectedTab.HOME -> {
                    val photos = viewModel.pagedPhotos.collectAsLazyPagingItems()
                    if (photos.itemCount == 0) {
                        CircularProgressIndicator()
                    } else {
                        LazyVerticalGrid(
                            state = gridState, // Pass the saved state to the grid
                            columns = GridCells.Adaptive(minSize = 128.dp),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(photos.itemCount, key = { index -> photos.peek(index)?.id ?: index }) { index ->
                                val photo = photos[index]
                                if (photo != null) {
                                    PhotoItem(photo = photo) {
                                        navController.navigate(Screen.PhotoDetail.createRoute(photo.id))
                                    }
                                }
                            }
                        }
                    }
                }

                SelectedTab.FAVORITES -> {
                    val favoritePhotos by viewModel.favoritePhotos.collectAsState()
                    if (favoritePhotos.isEmpty()) {
                        Text("You have no favorite photos yet.")
                    } else {
                        LazyVerticalGrid(
                            state = gridState, // Pass the saved state to the grid
                            columns = GridCells.Adaptive(minSize = 128.dp),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(favoritePhotos, key = { it.id }) { photo ->
                                PhotoItem(photo = photo) {
                                    navController.navigate(Screen.PhotoDetail.createRoute(photo.id))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoItem(photo: Photo, onClick: () -> Unit) {
    AsyncImage(
        model = photo.url,
        contentDescription = photo.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    )
}
