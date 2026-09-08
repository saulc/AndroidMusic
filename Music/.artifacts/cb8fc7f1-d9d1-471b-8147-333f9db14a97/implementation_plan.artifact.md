# Fix SQLiteException (no such column: _data) for Album Art

The application is crashing when trying to load album art on Android 10 (API 29) or higher because the `_data` (and `ALBUM_ART`) column has been removed from the `audio_albums` table in Scoped Storage. This plan replaces the deprecated usage of the album path with the recommended MediaStore URI approach.

## Proposed Changes

### [Component] UI Adapters

#### [MODIFY] [AlbumAdapter.java](file:///Users/user/Dev/AndroidMusic/Music/app/src/main/java/music/app/sc/music/adapters/AlbumAdapter.java)
- Update the album art URI to use `content://media/external/audio/albumart` instead of `MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI`. This "magic" URI is the standard way to load album art that works across Android versions and avoids direct queries to missing columns.

### [Component] Media Helpers

#### [MODIFY] [MediaStoreHelper.java](file:///Users/user/Dev/AndroidMusic/Music/app/src/main/java/music/app/sc/music/helpers/MediaStoreHelper.java)
- Remove `MediaStore.Audio.Albums.ALBUM_ART` from the `albumProjection`.
- Update `onLoadFinished` to skip the art path when creating `Album` objects.

### [Component] UI Fragments

#### [MODIFY] [ControlFragment.java](file:///Users/user/Dev/AndroidMusic/Music/app/src/main/java/music/app/sc/music/ui/ControlFragment.java)
- Remove the `ALBUM_ART` query from `findAlbumArt`.
- Update `updateProgressBar` to use the artwork URI with Glide instead of `Drawable.createFromPath`.

#### [MODIFY] [BubbleFragment.java](file:///Users/user/Dev/AndroidMusic/Music/app/src/main/java/music/app/sc/music/ui/browser/BubbleFragment.java)
- Similar fixes to `findAlbumArt` and art loading.

### [Component] Music Player

#### [MODIFY] [MusicService.java](file:///Users/user/Dev/AndroidMusic/Music/app/src/main/java/music/app/sc/music/player/MusicService.java)
- Update `getAlbumArtwork` to use the `albumart` URI.

## Verification Plan

### Automated Tests
- Run existing unit tests: `./gradlew test`
- Build the app to ensure no compilation errors: `./gradlew assembleDebug`

### Manual Verification
- Deploy the app to an Android 10+ device/emulator.
- Navigate to the Albums list and verify that album art loads without crashing.
- Verify that the now-playing notification and control panel show album art correctly.
