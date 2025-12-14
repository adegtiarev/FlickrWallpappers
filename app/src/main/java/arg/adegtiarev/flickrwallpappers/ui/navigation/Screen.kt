package arg.adegtiarev.flickrwallpappers.ui.navigation

/**
 * Sealed class for defining navigation routes in the application.
 * Ensures type safety and centralization of paths.
 */
sealed class Screen(val route: String) {
    object PhotoList : Screen("photo_list")

    // Route for the detail screen includes the photoId argument
    object PhotoDetail : Screen("photo_detail/{photoId}") {
        fun createRoute(photoId: String) = "photo_detail/$photoId"
    }
}
