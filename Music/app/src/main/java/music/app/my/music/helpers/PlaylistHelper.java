package music.app.my.music.helpers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import music.app.my.music.types.Playlist;
import music.app.my.music.types.Song;

public class  PlaylistHelper {

    private static final String TAG = "PlaylistHelper";

    private static void log(String s) {
        Log.d(TAG, s);
    }


    //add multiple songs to playlist
    public static void addListToPlaylist(Context context, Long pid, ArrayList<Long> ids, boolean top) {

        log("adding songs to playlist pid: " + pid + " ids: " + ids.size());

        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);

        // 1. Get current items in order
        ArrayList<Long> currentIds = new ArrayList<>();
        Cursor cur = resolver.query(uri, new String[]{MediaStore.Audio.Playlists.Members.AUDIO_ID}, 
                null, null, MediaStore.Audio.Playlists.Members.PLAY_ORDER + " ASC");
        if (cur != null) {
            while (cur.moveToNext()) {
                currentIds.add(cur.getLong(0));
            }
            cur.close();
        }

        // 2. Clear the playlist
        // Try multiple ways to clear, as behavior varies by Android version/device
        int deletedCount = resolver.delete(uri, null, null);
        if (deletedCount == 0) {
            // Try with a selection that should match everything
            deletedCount = resolver.delete(uri, "1=1", null);
        }
        if (deletedCount == 0) {
            // Try the general members URI with a selection on playlist ID
            try {
                Uri allMembersUri = Uri.parse("content://media/external/audio/playlists/members");
                deletedCount = resolver.delete(allMembersUri, "playlist_id=?", new String[]{String.valueOf(pid)});
            } catch (Exception e) {
                log("Failed to delete from general members URI: " + e.getMessage());
            }
        }
        log("Clear playlist pid " + pid + " result: " + deletedCount);

        // 3. Build the new full list of IDs
        ArrayList<Long> fullList = new ArrayList<>();
        if (top) {
            fullList.addAll(ids);
            fullList.addAll(currentIds);
        } else {
            fullList.addAll(currentIds);
            fullList.addAll(ids);
        }

        // 4. Re-insert everything in the correct order

        for (int i = 0; i < fullList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, i);
            values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, fullList.get(i));
            resolver.insert(uri, values);
        }
        log("Songs added. Total songs now: " + fullList.size());
    }

    //add 1 song to playlist
    public static void addToPlaylist(Context context, String pname, Long pid, Long sid, boolean top) {
        log("addToPlaylist: " + pname + " pid:" + pid + " sid:" + sid + " top:" + top);
        if (!top) {
            // Simple append to bottom
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);
            String[] cols = new String[]{ MediaStore.Audio.Playlists.Members.PLAY_ORDER };
            Cursor cur = context.getContentResolver().query(uri, cols, null, null, MediaStore.Audio.Playlists.Members.PLAY_ORDER + " DESC");
            int base = 0;
            if (cur != null && cur.moveToFirst()) {
                base = cur.getInt(0) + 1;
                cur.close();
            }
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base);
            values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, sid);
            context.getContentResolver().insert(uri, values);
            log("Added to bottom at pos: " + base);
        } else {
            ArrayList<Long> ids = new ArrayList<>();
            ids.add(sid);
            addListToPlaylist(context, pid, ids, top);
        }
    }

    //remove 1 song from playlist
    public static boolean deleteFromPlaylist(Context context, Long pid, String pname, String sid, int pos) {
        String[] cols = new String[]{
                MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                MediaStore.Audio.Playlists.Members.AUDIO_ID
        };
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);

        String[] arg = {sid, pos + ""};
        String where = MediaStore.Audio.Playlists.Members.AUDIO_ID + "=? AND " +
                MediaStore.Audio.Playlists.Members.PLAY_ORDER + "=?";
        resolver.delete(uri, where, arg);

        log(sid + " deleted from playlist: " + pname + " pos: " + pos);
        return true;
    }


    public static void deletePlaylist(Context context, String id) {
        Log.i("m6", "Deleting playlist " + id);
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        String[] arg = {id};
        resolver.delete(uri, MediaStore.Audio.Playlists._ID + "=?", arg);

        Log.i("m6", id + " Playlist delete sucessful id: " + id);


    }


    public static void newPlaylist(Context context, String name) {
        Log.i("m6", "Saving playlist " + name);
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.NAME, name);
        ContentResolver resolver = context.getContentResolver();
        resolver.insert(uri, values);
        long id = findPlaylistId(context, name);
        if (id > 0) {
            Log.i("m6", name + " Playlist saved sucessful id: " + id);


        }

    }


    //get id from playlist name.
    public static long findPlaylistId(Context context, String name) {
        Log.d("M6", "Looking for playlist: " + name);
        ContentResolver resolver = context.getContentResolver();
        String[] playlistProjection = {MediaStore.Audio.Playlists.NAME,
                MediaStore.Audio.Playlists._ID};
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cur = resolver.query(uri, playlistProjection, null, null, null);

        long id = 0;
        while (cur.moveToNext()) {
            if (cur.getString(0).equals(name)) {
                id = Long.parseLong(cur.getString(1));
                Log.d("m6", "queue playlist id: " + id);
                return id;
            }
        }
        return id;
    }


    //find and play a random' song on ? action.
    //switch to play random playlist/album?
    public static Song getRandomSong(Context context) {
        Log.d("M6", "Looking for 'random' song...");
        ContentResolver resolver = context.getContentResolver();

        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String defaultSort = MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC";
        String defaultSelection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] defaultProjection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST_ID

        };
        Cursor cursor = resolver.query(songUri, defaultProjection, defaultSelection, null, defaultSort);
        cursor.moveToFirst();
        ArrayList<Song> songs = new ArrayList<Song>();
        while (cursor.moveToNext()) {
            songs.add(new Song(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getString(4)
                    , cursor.getString(5), cursor.getString(6), cursor.getString(7)));
        }
        cursor.close();
        int i = ((int) (Math.random() * 50000));
        i = i % songs.size();

        Log.d("M6", " 'random' song: " + i);

        return songs.get(i);

    }

    public static ArrayList<Playlist> readPlaylist(Context context){
        String path = Environment.getExternalStorageDirectory().toString()+"/Music";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        ArrayList<String> s = new ArrayList<>();
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
            if(files[i].getName().contains(".m3u"))
                s.add(files[i].getName().substring(0, files[i].getName().length()-4 ));
        }
        Collections.sort(s);
        ArrayList<Playlist> r = new ArrayList<>();

        for (int i = 0; i < s.size(); i++)
            r.add(new Playlist(s.get(i), i+""));

        return r;
}

    public static void viewPlaylist(Context context, String pname){

        String path = Environment.getExternalStorageDirectory().toString()+"/Music/"+pname+".m3u";
        Log.d("Files", "Path: " + path);
//        File f = new File(path);
        String aBuffer = "";
        ArrayList<String> dat = new ArrayList<>();
        try {
            File myFile = new File(path);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = "";
            while ((aDataRow = myReader.readLine()) != null) {
//                aBuffer += aDataRow;
                dat.add(aDataRow);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String l : dat)
            log(l);

    }
}
