package arg.adegtiarev.flickrwallpappers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import arg.adegtiarev.flickrwallpappers.ui.favorites.FavoritesScreen
import arg.adegtiarev.flickrwallpappers.ui.navigation.Screen
import arg.adegtiarev.flickrwallpappers.ui.photodetail.PhotoDetailScreen
import arg.adegtiarev.flickrwallpappers.ui.photolist.PhotoListScreen
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
                        startDestination = Screen.PhotoList.route
                    ) {
                        composable(Screen.PhotoList.route) {
                            PhotoListScreen(navController = navController)
                        }
                        composable(Screen.Favorites.route) {
                            FavoritesScreen(navController = navController)
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
