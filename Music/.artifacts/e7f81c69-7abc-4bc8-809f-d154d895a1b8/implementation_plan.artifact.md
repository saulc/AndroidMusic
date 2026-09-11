# Fix Firebase Plugin Sync Error

The project is failing to sync because the Firebase Crashlytics and Performance Monitoring plugins are declared in the top-level `build.gradle` without versions, and are incorrectly placed within the `dependencies` block in the app-level `build.gradle`.

## Proposed Changes

### Build Configuration

#### [MODIFY] [build.gradle](file:///Users/user/Dev/AndroidMusic/Music/build.gradle)
- Add versions and `apply false` to the Crashlytics and Performance plugins in the `plugins` block to allow them to be resolved globally.

#### [MODIFY] [app/build.gradle](file:///Users/user/Dev/AndroidMusic/Music/app/build.gradle)
- Apply the Crashlytics and Performance plugins in the `plugins` block.
- Remove the invalid plugin declarations from the `dependencies` block.
- Consolidate Firebase BoM declarations and remove duplicates.

## Verification Plan

### Automated Tests
- Run `gradle sync` to ensure the project builds and plugins are correctly resolved.

### Manual Verification
- Verify that the Firebase plugins are correctly applied by checking the Gradle task list for Crashlytics-related tasks (e.g., `uploadCrashlyticsSymbolFile`).
