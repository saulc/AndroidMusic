# Fix FileNotFoundException for Album Art loading

The application logs `java.io.FileNotFoundException: No album art found` when attempting to load album artwork via the legacy magic URI `content://media/external/audio/albumart`. This is particularly common on Android 10 (API 29) and higher due to changes in Scoped Storage and the removal of the `ALBUM_ART` (path) column.

This plan updates the artwork loading logic to use the recommended `MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI` and modern `ContentResolver.loadThumbnail` API where applicable.

## Proposed Changes

### [Component] Music Player Service

#### [MODIFY] [MusicService.java](file:///Users/user/Dev/AndroidMusic/Music/app/src/main/java/music/app/sc/music/player/MusicService.java)
- Update `getAlbumArtwork` to use `MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI` instead of the hardcoded legacy string.
- Add a null check for `albumId` in `setUpAsForeground` to avoid `NumberFormatException`.
- Update `updateRemoteControls` to safely handle missing art and use the modern URI approach if we were to refactor it (but prioritizing the fix for the reported error).

### [Component] UI Adapters

#### [MODIFY] [AlbumAdapter.java](file:///Users/user/Dev/AndroidMusic/Music/app/src/main/java/music/app/sc/music/adapters/AlbumAdapter.java)
- Update the Glide load URI to use `MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI` instead of the magic `albumart` string.
- This allows Glide to use more modern fetching mechanisms on newer Android versions.

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors: `./gradlew assembleDebug`

### Manual Verification
- Deploy the app to an Android 10+ device or emulator.
- Scroll through the Album list and verify that album art (or placeholders) display without the `FileNotFoundException` appearing as a significant "Root cause" error in logs.
- Play a song and verify the notification shows album art correctly.
