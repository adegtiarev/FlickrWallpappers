package arg.adegtiarev.flickrwallpappers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import arg.adegtiarev.flickrwallpappers.ui.navigation.Screen
import arg.adegtiarev.flickrwallpappers.ui.photodetail.PhotoDetailScreen
import arg.adegtiarev.flickrwallpappers.ui.photolist.PhotoListScreen
import arg.adegtiarev.flickrwallpappers.ui.photolist.PhotoListViewModel
import arg.adegtiarev.flickrwallpappers.ui.theme.FlickrWallpappersTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlickrWallpappersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        // Create a nested navigation graph
                        navigation(startDestination = Screen.PhotoList.route, route = "home") {
                            composable(Screen.PhotoList.route) {
                                // Get the back stack entry for the parent graph
                                val parentEntry = remember(it) {
                                    navController.getBackStackEntry("home")
                                }
                                // Get the ViewModel scoped to the parent graph
                                val viewModel: PhotoListViewModel = hiltViewModel(parentEntry)

                                PhotoListScreen(
                                    navController = navController,
                                    viewModel = viewModel
                                )
                            }
                            composable(
                                route = Screen.PhotoDetail.route,
                                arguments = listOf(navArgument("photoId") { type = NavType.StringType })
                            ) {
                                PhotoDetailScreen(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
