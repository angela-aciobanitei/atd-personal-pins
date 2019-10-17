## Android Techdegree Treehouse Project 11: Personal Pins

In this project you will create a personal Pinterest-style app that allows users to record photos or videos and store them in collections. Your collections will organize related photos and videos and include cover photos, titles, tags, and more. You'll have some important layout choices to make, like how to display a set of photos in a RecyclerView, and how to transition thumbnails larger views. You'll also need to work with VideoView, which allows you to embed videos directly in your app instead of sending them to another app via an Intent. 

## Project Instructions

*   Create an app to allow users to capture or choose pictures or videos.
*   Individual pins (photos or videos) must include a title, an optional comments, and optional tags (zero or more). 
*   Users must be able to add new tags.
*   The app must persist data across sessions using SQLite, files, or a backend service like Firebase.
*   Create sets of pictures and/or videos called Boards that have titles and a cover image selected from the collection. Provide a default image if none is selected.
*   Display boards in a RecyclerView as either a list or grid.
*   Tapping on a specific board should display a secondary RecyclerView that displays the items in a list or grid.
*   Users should be able to tap on a photo to enlarge it. Use a transition to expand the small image into a larger one.
*   Users should be able to tap on a video to play it. Use a [VideoView](https://developer.android.com/reference/android/widget/VideoView.html) to display the video directly in your app (instead of playing it via an intent).

## Libraries
*   **[AndroidX](https://developer.android.com/jetpack/androidx/)**
*   **[Data Binding](https://developer.android.com/topic/libraries/data-binding/)**
*   **[Navigation](https://developer.android.com/guide/navigation/)**
*   **[Room](https://developer.android.com/topic/libraries/architecture/room)**
*   **[Glide](https://github.com/bumptech/glide)**
*   **[Timber](https://github.com/JakeWharton/timber)** 
*   **[Dagger 2](https://github.com/google/dagger)**
