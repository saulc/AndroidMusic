package music.app.sc.music.helpers;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PlaylistHelperTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_AUDIO
    );

    private Context context;
    private long testPlaylistId;
    private String testPlaylistName;
    private ArrayList<Long> testSongIds = new ArrayList<>();

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        testPlaylistName = "TestPlaylist_" + System.currentTimeMillis();
        
        // 1. Create a test playlist
        PlaylistHelper.newPlaylist(context, testPlaylistName);
        testPlaylistId = PlaylistHelper.findPlaylistId(context, testPlaylistName);
        assertTrue("Failed to create test playlist", testPlaylistId > 0);

        // 2. Find some songs to use for testing
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = resolver.query(uri, new String[]{MediaStore.Audio.Media._ID}, 
                null, null, null);
        
        if (cur != null) {
            while (cur.moveToNext() && testSongIds.size() < 5) {
                testSongIds.add(cur.getLong(0));
            }
            cur.close();
        }
        
        assertTrue("Not enough songs found in MediaStore for testing. Please ensure the device has music files.", testSongIds.size() >= 3);
    }

    @After
    public void tearDown() {
        if (testPlaylistId > 0) {
            PlaylistHelper.deletePlaylist(context, String.valueOf(testPlaylistId));
        }
    }

    @Test
    public void testAddToPlaylistBottom() {
        long sid1 = testSongIds.get(0);
        long sid2 = testSongIds.get(1);

        PlaylistHelper.addToPlaylist(context, testPlaylistName, testPlaylistId, sid1, false);
        PlaylistHelper.addToPlaylist(context, testPlaylistName, testPlaylistId, sid2, false);

        ArrayList<Long> members = getPlaylistMembers(testPlaylistId);
        assertEquals(2, members.size());
        assertEquals(Long.valueOf(sid1), members.get(0));
        assertEquals(Long.valueOf(sid2), members.get(1));
    }

    @Test
    public void testAddToPlaylistTop() {
        long sid1 = testSongIds.get(0);
        long sid2 = testSongIds.get(1);

        PlaylistHelper.addToPlaylist(context, testPlaylistName, testPlaylistId, sid1, false);
        PlaylistHelper.addToPlaylist(context, testPlaylistName, testPlaylistId, sid2, true);

        ArrayList<Long> members = getPlaylistMembers(testPlaylistId);
        assertEquals(2, members.size());
        assertEquals(Long.valueOf(sid2), members.get(0));
        assertEquals(Long.valueOf(sid1), members.get(1));
    }

    @Test
    public void testAddListToPlaylistTop() {
        long sid1 = testSongIds.get(0);
        long sid2 = testSongIds.get(1);
        long sid3 = testSongIds.get(2);
        long sid4 = testSongIds.get(3);

        // Add two initially
        ArrayList<Long> initial = new ArrayList<>();
        initial.add(sid1);
        initial.add(sid2);
        PlaylistHelper.addListToPlaylist(context, testPlaylistId, initial, false);

        // Add two to top
        ArrayList<Long> newItems = new ArrayList<>();
        newItems.add(sid3);
        newItems.add(sid4);
        PlaylistHelper.addListToPlaylist(context, testPlaylistId, newItems, true);

        ArrayList<Long> members = getPlaylistMembers(testPlaylistId);
        assertEquals(4, members.size());
        assertEquals(Long.valueOf(sid3), members.get(0));
        assertEquals(Long.valueOf(sid4), members.get(1));
        assertEquals(Long.valueOf(sid1), members.get(2));
        assertEquals(Long.valueOf(sid2), members.get(3));
    }

    private ArrayList<Long> getPlaylistMembers(long pid) {
        ArrayList<Long> ids = new ArrayList<>();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);
        Cursor cur = context.getContentResolver().query(uri, 
                new String[]{MediaStore.Audio.Playlists.Members.AUDIO_ID}, 
                null, null, MediaStore.Audio.Playlists.Members.PLAY_ORDER + " ASC");
        
        if (cur != null) {
            while (cur.moveToNext()) {
                ids.add(cur.getLong(0));
            }
            cur.close();
        }
        return ids;
    }
}
