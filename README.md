# Flickr Wallpapers

A simple, modern Android application built with an offline-first architecture. It fetches interesting photos from the Flickr API, displays them in a paginated list, and allows users to view them in full-screen with zoom capabilities and mark them as favorites.

## Key Features

-   **Offline-First:** The app first loads data from a local database. Network data is fetched, stored locally, and then displayed, ensuring content is always available, even without an internet connection.
-   **Endless Scrolling:** A paginated list of photos that seamlessly loads more items as the user scrolls.
-   **Photo Details & Zoom:** Tap on any photo to view it in full-screen with pinch-to-zoom functionality.
-   **Favorites:** Users can mark photos as favorites and view them on a separate screen.

## Tech Stack & Architecture

This project follows modern Android development best practices and demonstrates a robust MVVM architecture.

-   **Tech Stack:**
    -   [Kotlin](https://kotlinlang.org/): Official programming language for Android development.
    -   [Jetpack Compose](https://developer.android.com/jetpack/compose): Modern toolkit for building native Android UI.
    -   [Material 3](https://m3.material.io/): The latest version of Google's open-source design system.
    -   [Coroutines & Flow](https://kotlinlang.org/docs/coroutines-guide.html): For asynchronous programming.

-   **Architecture:**
    -   **MVVM (Model-View-ViewModel):** A robust architectural pattern that separates UI from business logic.
    -   **Repository Pattern:** A single source of truth for all application data.

-   **Libraries:**
    -   **Hilt:** For dependency injection.
    -   **Retrofit:** For type-safe HTTP requests to the Flickr API.
    -   **Room:** For local database storage, enabling offline capabilities.
    -   **Paging 3:** For implementing pagination and endless scrolling. The core of the offline-first strategy is implemented via `RemoteMediator`.
    -   **Coil:** For loading and caching images.
    -   **Jetpack Navigation:** For navigating between screens in a Compose-based app.

---

## Data Flow Explained

The application is built around a clear, unidirectional data flow, with the Paging 3 library orchestrating the offline-first logic.

Here is a step-by-step breakdown of the data journey from launch to display:

#### 1. App Launch & Initial Display

1.  **Entry Point:** The app launches `MainActivity`, which sets up a `NavHost` from Jetpack Navigation. The starting screen is `PhotoListScreen`.
2.  **ViewModel Request:** `PhotoListScreen` requests its `PhotoListViewModel` via Hilt.
3.  **Data Request:** The `ViewModel` immediately requests a `Flow<PagingData<Photo>>` from the `PhotoRepository`.

#### 2. The Repository and the Pager

4.  **Single Source of Truth:** The `PhotoRepository` is the central hub for data. It creates a `Pager` object from the Paging 3 library.
5.  **Pager Configuration:** The `Pager` is configured with two crucial components:
    -   **`pagingSourceFactory`**: This points to `photoDao.pagingSource()`. It is the **local source** of data. It directly queries the Room database and provides data to the UI.
    -   **`remoteMediator`**: This is our `FlickrRemoteMediator`. Its job is to fetch data from the **network** when the local data runs out.

#### 3. Offline-First in Action (The `RemoteMediator`)

6.  **Initial Load:** The `PagingSource` (from Room) is queried first.
    -   **If the database has data:** The saved photos are immediately displayed on the screen. The user sees content instantly.
    -   **If the database is empty (first launch):** The `PagingSource` returns no data. This triggers the `RemoteMediator`.

7.  **Network Fetch:** The `RemoteMediator`'s `load` function is called. It fetches the first page of photos from the Flickr API using `Retrofit`.
8.  **Cache to Database:** Upon a successful network response, the `RemoteMediator` performs a **single database transaction**:
    - It clears any old data from the database.
    - It maps the network DTOs to local `Photo` entities.
    - It creates and stores `RemoteKeys` to remember which page was just loaded.
    - It saves the new photos and keys into the Room database.

9.  **UI Update:** The `PagingSource`, which is actively observing the database, detects the newly inserted photos. It automatically pushes the new list to the UI (`PhotoListScreen`), which then displays the images.

#### 4. Scrolling and Pagination

10. **User Scrolls:** The user scrolls to the end of the currently displayed list.
11. **Pagination Trigger:** The Paging library detects that it needs more data and triggers the `RemoteMediator` again, but this time for an `APPEND` operation.
12. **Next Page Fetch:** The mediator checks its `RemoteKeys` table to find the next page number to fetch. It then makes another network call for that specific page.
13. **Append to Database:** The newly fetched photos are simply **added** to the Room database without clearing existing data.
14. **Seamless UI Update:** The `PagingSource` detects the new data and seamlessly appends it to the list in the UI, creating an "endless scrolling" effect.
