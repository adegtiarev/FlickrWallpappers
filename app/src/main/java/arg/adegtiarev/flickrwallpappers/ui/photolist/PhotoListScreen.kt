package arg.adegtiarev.flickrwallpappers.ui.photolist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
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
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val gridState: LazyGridState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }

    BackHandler(enabled = isSearchActive) {
        viewModel.onSearchActiveChange(false)
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchAppBar(query = searchQuery,
                    onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                    onBackPressed = { viewModel.onSearchActiveChange(false) })
            } else {
                DefaultTopAppBar(selectedTab = selectedTab,
                    onSearchClicked = { viewModel.onSearchActiveChange(true) })
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = !isSearchActive, enter = fadeIn(), exit = fadeOut()) {
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
                                painter = painterResource(
                                    id = if (selectedTab == SelectedTab.FAVORITES) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                                ),
                                contentDescription = "Favorites"
                            )
                        }
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
            if (isSearchActive) {
                val photos = viewModel.searchedPhotos.collectAsLazyPagingItems()
                PhotoGrid(
                    gridState = gridState,
                    photos = photos,
                    onPhotoClick = { photo ->
                        viewModel.onPhotoClicked(photo)
                        navController.navigate(Screen.PhotoDetail.createRoute(photo.id))
                    }
                )
            } else {
                when (selectedTab) {
                    SelectedTab.HOME -> {
                        val photos = viewModel.pagedPhotos.collectAsLazyPagingItems()
                        PhotoGrid(
                            gridState = gridState,
                            photos = photos,
                            onPhotoClick = { photo ->
                                viewModel.onPhotoClicked(photo)
                                navController.navigate(Screen.PhotoDetail.createRoute(photo.id))
                            }
                        )
                    }

                    SelectedTab.FAVORITES -> {
                        val favoritePhotos by viewModel.favoritePhotos.collectAsState()
                        if (favoritePhotos.isEmpty()) {
                            Text("You have no favorite photos yet.")
                        } else {
                            LazyVerticalGrid(
                                state = gridState,
                                columns = GridCells.Adaptive(minSize = 128.dp),
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(favoritePhotos, key = { it.id }) { photo ->
                                    PhotoItem(photo = photo) {
                                        viewModel.onPhotoClicked(photo)
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
}

@Composable
private fun PhotoGrid(
    gridState: LazyGridState,
    photos: LazyPagingItems<Photo>,
    onPhotoClick: (Photo) -> Unit
) {
    if (photos.itemCount == 0) {
        CircularProgressIndicator()
    } else {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Adaptive(minSize = 128.dp),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(photos.itemCount, key = { index -> photos.peek(index)?.id ?: index }) { index ->
                val photo = photos[index]
                if (photo != null) {
                    PhotoItem(photo = photo) {
                        onPhotoClick(photo)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopAppBar(selectedTab: SelectedTab, onSearchClicked: () -> Unit) {
    TopAppBar(
        title = { Text(if (selectedTab == SelectedTab.HOME) "Flickr Wallpapers" else "Favorites") },
        actions = {
            if (selectedTab == SelectedTab.HOME) {
                IconButton(onClick = onSearchClicked) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search"
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAppBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChanged,
                placeholder = { Text("Search photos") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back"
                )
            }
        }
    )
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
