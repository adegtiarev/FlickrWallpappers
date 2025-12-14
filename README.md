# Flickr Wallpapers

![App Screenshot](images/screen1.png)

A modern, offline-first Android application built entirely with Jetpack Compose. The app fetches photos from the Flickr API, allows users to search for specific images, view them in full-screen, set them as a device wallpaper, and manage a local collection of favorites.

## Key Features

-   **Offline-First Architecture:** The main feed is always available. The app loads data from a local Room database first, ensuring instant startup and offline access. New data is fetched from the network, cached locally, and then displayed.
-   **Modern Navigation:** A clean UI with a `BottomAppBar` to seamlessly switch between the main feed and the user's favorite photos.
-   **Live Search:** A dynamic, online-only search feature that allows users to find photos on Flickr. Search results are displayed in a paginated list.
-   **Set as Wallpaper:** Users can set any photo as their device wallpaper using a system `Intent`, which provides a familiar UI for cropping and previewing.
-   **Photo Details & Zoom:** A dedicated detail screen with smooth pinch-to-zoom functionality for every image.
-   **Favorites:** Any photo (from the main feed or search results) can be added to a local "Favorites" collection.

## Tech Stack & Architecture

This project is built with a modern tech stack, adhering to Google's recommended best practices and a robust MVVM architecture.

-   **Tech Stack:**
    -   [Kotlin](https://kotlinlang.org/): 100% Kotlin codebase.
    -   [Jetpack Compose](https://developer.android.com/jetpack/compose): For building the entire UI declaratively.
    -   [Material 3](https://m3.material.io/): For modern UI components and theming.
    -   [Coroutines & Flow](https://kotlinlang.org/docs/coroutines-guide.html): For managing all asynchronous operations.

-   **Architecture:**
    -   **MVVM (Model-View-ViewModel):** A clean separation between the UI and business logic.
    -   **Repository Pattern:** A single source of truth for all application data.
    -   **SOLID Principles:** Code is structured to be scalable, testable, and maintainable.

-   **Libraries:**
    -   **Hilt:** For dependency injection across the app.
    -   **Retrofit:** For type-safe HTTP requests to the Flickr API.
    -   **Room:** For local database storage, enabling offline capabilities and favorites persistence.
    -   **Paging 3:** For implementing pagination. This is the core of the data layer, with two distinct strategies:
        -   `RemoteMediator` for the offline-first "Home" feed.
        -   A custom `PagingSource` for the online-only "Search" feature.
    -   **Coil:** For efficient image loading, caching, and processing.
    -   **Jetpack Navigation:** For navigating between screens in a Compose-based app.

---

## Data Flow Explained

The application cleverly manages three distinct data sources (Home, Favorites, and Search) within a single screen, orchestrated by the `PhotoListViewModel`.

#### 1. Home Feed (Offline-First)

This is the default data flow, designed for robustness and offline availability.

1.  **Request:** The `ViewModel` requests its `pagedPhotos` Flow, which originates from the `PhotoRepository`.
2.  **Pager Setup:** The repository creates a `Pager` with two key components:
    -   **Local Source (`pagingSourceFactory`):** Points to a Room query (`photoDao.pagingSource()`). The UI **always** listens to this source.
    -   **Network Source (`remoteMediator`):** The `FlickrRemoteMediator` is responsible for fetching data from the network when the local source is empty or depleted.
3.  **Execution:**
    -   **On launch:** The UI instantly displays any photos already in the Room database. If the database is empty, the `RemoteMediator` triggers.
    -   **Network Fetch:** The mediator fetches a page of photos from the Flickr API.
    -   **Smart Caching:** Instead of blindly overwriting data, the mediator checks if any newly fetched photos are already marked as favorites in the local database. It preserves the `isFavorite` status before inserting the new data.
    -   **UI Update:** Room notifies its observers. The `PagingSource` provides the updated list to the UI, which displays the new images.

#### 2. Search (Online-Only)

When the user activates the search bar, the data flow switches to a simpler, online-only strategy.

1.  **User Input:** The user types a query into the `TopAppBar`.
2.  **Debounce & Request:** The `ViewModel` waits for 500ms of inactivity (`debounce`) before triggering a new search with the current query.
3.  **New Pager:** The `PhotoRepository` creates a new, simpler `Pager` that uses a custom `FlickrSearchPagingSource`. This source does **not** interact with the `RemoteMediator` or the database cache for the main feed.
4.  **Direct Fetch:** `FlickrSearchPagingSource` directly calls the `flickr.photos.search` API endpoint for each page requested by the UI.
5.  **Display:** The results are immediately displayed on the screen. These items are **not** saved to the main database table (unless the user explicitly clicks on one to view its details).

#### 3. Favorites

This is the simplest data flow.

1.  **Selection:** The user taps the "Favorites" icon in the `BottomAppBar`.
2.  **State Change:** The `ViewModel` updates its `selectedTab` state.
3.  **Data Source:** The UI switches to collecting data from the `favoritePhotos` `StateFlow`, which is a direct, non-paginated query to the Room database for all photos where `isFavorite = true`.
