# Fix FileNotFoundException and missing Album Art on modern Android

The application is experiencing `java.io.FileNotFoundException: No album art found` when loading artwork via Glide and legacy `MediaStore` APIs. This is primarily caused by the use of the legacy "magic" URI `content://media/external/audio/albumart` and the deprecated `ALBUM_ART` (file path) column, which are unreliable or removed in Android 10 (API 29) and higher.

## Proposed Changes

### [Component] UI Adapters

#### [MODIFY] [AlbumAdapter.java](file:///Users/user/Dev/AndroidMusic/Music/app/src/main/java/music/app/sc/music/adapters/AlbumAdapter.java)
- Update Glide to load album art using the official `MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI` with the appended album ID.
- This allows Glide to use modern APIs like `ContentResolver.loadThumbnail` internally on newer Android versions.

### [Component] UI Fragments

#### [MODIFY] [ControlFragment.java](file:///Users/user/Dev/AndroidMusic/Music/app/src/main/java/music/app/sc/music/ui/ControlFragment.java)
- Refactor `findAlbumArt` to return a `Uri` instead of a file path.
- Update `updateProgressBar` to use Glide for loading the artwork instead of `Drawable.createFromPath(path)`, ensuring compatibility with Scoped Storage.

### [Component] Music Player Service

#### [MODIFY] [MusicService.java](file:///Users/user/Dev/AndroidMusic/Music/app/src/main/java/music/app/sc/music/player/MusicService.java)
- Update `getAlbumArtwork` to use `MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI` instead of the legacy string.
- Wrap `loadThumbnail` and `getBitmap` calls in a try-catch block to gracefully handle `FileNotFoundException` when an album has no artwork, returning null instead of propagating the error.
- Add null/empty check for `albumId` to prevent `NumberFormatException`.

## Verification Plan

### Automated Tests
- Build the project: `./gradlew assembleDebug`
- Check for regression in loading logic by running unit tests if available.

### Manual Verification
- Deploy to an Android 10+ device/emulator.
- Verify album list shows artwork or placeholders without `FileNotFoundException` in logs.
- Verify the playback control bar (ControlFragment) shows the correct artwork.
- Verify the notification shows artwork correctly.
