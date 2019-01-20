package music.app.my.music.helpers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

import music.app.my.music.types.Song;

public class PlaylistHelper {

    private static final String TAG = "PlaylistHelper";
    private static void log(String s){
        Log.d(TAG, s);
    }


    //add multiple songs to playlist
    public static void addListToPlaylist(Context context, Long pid, ArrayList<Long> ids, boolean top) {

        log("adding songs to playlist Ids: " + ids.size());

        String[] cols = new String[]{
                MediaStore.Audio.Playlists.Members.PLAY_ORDER, MediaStore.Audio.Playlists.Members.AUDIO_ID
        };
        ContentValues values = new ContentValues();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);

        Cursor cur = resolver.query(uri, cols, null, null, null);

        //ids already has our new items at the top, just add the old ones to it
        // if adding to the end, just insert the old items. at 0
        cur.moveToFirst();
        while(cur.moveToNext()){
            long l = Long.parseLong(cur.getString(1));
            if(top) ids.add(0, l);
            else ids.add( l );

        }
        cur.close();
        log("songs added to playlist Ids: " + ids.size());

        //todo delete old items
        //add the all the items in the new order


        for(int i=0; i<ids.size(); i++) {
            values = new ContentValues();
            // Log.d("Music service", i +" saving song: " + t.getTitle() + songid);
            values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, i);
            values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, ids.get(i));
            resolver.insert(uri, values);
        }

    }

    //add 1 song to playlist
    public static void addToPlaylist(Context context, String pname, Long pid, Long sid, boolean top){
        String[] cols = new String[] {
                MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                MediaStore.Audio.Playlists.Members.AUDIO_ID
        };

        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        int base = 0;
        if(!top &&  cur.moveToLast()) {

            base = cur.getInt(0);
            base += 1;
            String id = cur.getString(1);
        }
        cur.close();
        log("adding item --->>>>>base: " + base + " to " + pname);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base);
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, sid);
        resolver.insert(uri, values);


    }

    //remove 1 song from playlist
    public static void deleleFromPlaylist(Context context,  Long pid, String pname,  String sid, int pos) {
        String[] cols = new String[]{
                MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                MediaStore.Audio.Playlists.Members.AUDIO_ID
        };
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);

        String[] arg = { sid, pos+"" };
        String where = MediaStore.Audio.Playlists.Members.AUDIO_ID+"=? AND " +
                MediaStore.Audio.Playlists.Members.PLAY_ORDER+"=?";
        resolver.delete(uri, where, arg );

        log(sid + " deleted from playlist: " + pname + " pos: "+ pos);
    }


    public static void deletePlaylist(Context context, String id){
        Log.i("m6", "Deleting playlist "+ id);
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        String[] arg = { id};
        resolver.delete(uri, MediaStore.Audio.Playlists._ID+ "=?", arg);

        Log.i("m6", id + " Playlist delete sucessful id: "+ id);


    }



    public static void newPlaylist(Context context, String name){
        Log.i("m6", "Saving playlist "+ name);
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.NAME, name);
        ContentResolver resolver = context.getContentResolver();
        resolver.insert(uri, values);
        long id = findPlaylistId(context, name);
        if( id > 0){
            Log.i("m6", name + " Playlist saved sucessful id: "+ id);


        }

    }



    //get id from playlist name.
    public static long findPlaylistId(Context context, String name){
        Log.d("M6", "Looking for playlist: " + name);
        ContentResolver resolver = context.getContentResolver();
        String[] playlistProjection = { MediaStore.Audio.Playlists.NAME,
                MediaStore.Audio.Playlists._ID};
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cur = resolver.query(uri, playlistProjection, null, null, null);

        long id = 0;
        while(cur.moveToNext()){
            if(cur.getString(0).equals(name)){
                id = Long.parseLong(cur.getString(1));
                Log.d("m6", "queue playlist id: " + id);
                return id;
            }
        }
        return id;
    }


    //find and play a random' song on ? action.
    //switch to play random playlist/album?
    public static Song getRandomSong(Context context){
        Log.d("M6", "Looking for 'random' song..." );
        ContentResolver resolver = context.getContentResolver();

        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String defaultSort =  MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
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
        while(cursor.moveToNext()) {
            songs.add(new Song(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getString(4)
                    , cursor.getString(5), cursor.getString(6), cursor.getString(7)));
        }
        cursor.close();
        int i = ((int) (Math.random()*100) );
        i = i % songs.size();

        Log.d("M6", " 'random' song: " + i );

        return songs.get(i);

    }



}
