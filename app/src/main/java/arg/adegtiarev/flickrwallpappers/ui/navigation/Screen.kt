package arg.adegtiarev.flickrwallpappers.ui.navigation

/**
 * Sealed class для определения навигационных роутов в приложении.
 * Обеспечивает типобезопасность и централизацию путей.
 */
sealed class Screen(val route: String) {
    object PhotoList : Screen("photo_list")
    object Favorites : Screen("favorites")

    // Роут для детального экрана включает аргумент photoId
    object PhotoDetail : Screen("photo_detail/{photoId}") {
        fun createRoute(photoId: String) = "photo_detail/$photoId"
    }
}
