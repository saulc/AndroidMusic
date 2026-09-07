# Fix PlaylistHelper Add to Playlist Logic

The user reported that `PlaylistHelper.addtoplaylist` is not working. Investigation of `PlaylistHelper.java` revealed several critical bugs in both `addToPlaylist` and `addListToPlaylist` methods, including incorrect re-ordering logic and skipping the first item in existing playlists.

## User Review Required

> [!IMPORTANT]
> The proposed changes will refactor `addListToPlaylist` to be more robust by reading all items, clearing the playlist members, and re-inserting in the correct order. This ensures that `PLAY_ORDER` is always sequential and unique.

## Proposed Changes

### [music.app.my.music.helpers]

#### [MODIFY] [PlaylistHelper.java](file:///Users/user/Dev/AndroidMusic/Music/app/src/main/java/music/app/my/music/helpers/PlaylistHelper.java)

- Fix `addListToPlaylist` to correctly process all items (the current implementation skips the first item due to an incorrect cursor move).
- Fix `addListToPlaylist` logic for `top` parameter:
    - If `top` is true: New items should be at the start of the list.
    - If `top` is false: New items should be at the end of the list.
- Update `addListToPlaylist` to safely re-order the entire playlist by deleting all current members and re-inserting them with fresh `PLAY_ORDER` values.
- Update `addToPlaylist` to use `addListToPlaylist` internally when adding to the top, ensuring consistent re-ordering logic.

## Verification Plan

### Automated Tests
- I will create a new instrumentation test `PlaylistHelperTest.java` to verify:
    - Adding a single song to an empty playlist.
    - Adding a single song to the top of a non-empty playlist.
    - Adding a single song to the bottom of a non-empty playlist.
    - Adding multiple songs to the top/bottom.
- Run the test using `./gradlew connectedAndroidTest`.

### Manual Verification
- Deploy the app to the emulator.
- Try adding a song to a playlist from the "Songs" fragment.
- Verify the song appears in the playlist in the expected position.
