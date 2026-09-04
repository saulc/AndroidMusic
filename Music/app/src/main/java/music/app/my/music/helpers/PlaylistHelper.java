package music.app.my.music.helpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import music.app.my.music.types.Playlist;
import music.app.my.music.types.Song;

public class  PlaylistHelper {

    private static final String TAG = "PlaylistHelper";

    public static class MemberSecurityException extends SecurityException {
        private final Uri memberUri;
        public MemberSecurityException(String message, Uri uri) {
            super(message);
            this.memberUri = uri;
        }
        public Uri getMemberUri() { return memberUri; }
    }

    private static void log(String s) {
        Log.d(TAG, s);
    }


    private static String getVolumeName(Context context, Long pid) {
        if (pid == null || pid <= 0) return "external";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check primary first
            if (checkVolume(context, pid, MediaStore.VOLUME_EXTERNAL_PRIMARY)) return MediaStore.VOLUME_EXTERNAL_PRIMARY;

            // Check all other volumes
            Set<String> volumes = MediaStore.getExternalVolumeNames(context);
            for (String vol : volumes) {
                if (vol.equals(MediaStore.VOLUME_EXTERNAL_PRIMARY) || vol.equals("external")) continue;
                if (checkVolume(context, pid, vol)) return vol;
            }
        }
        return "external";
    }

    private static boolean checkVolume(Context context, long pid, String volume) {
        Uri uri = MediaStore.Audio.Playlists.getContentUri(volume);
        try (Cursor cur = context.getContentResolver().query(uri, new String[]{MediaStore.Audio.Playlists._ID},
                MediaStore.Audio.Playlists._ID + "=?", new String[]{String.valueOf(pid)}, null)) {
            if (cur != null && cur.getCount() > 0) {
                log("Found playlist " + pid + " on volume: " + volume);
                return true;
            }
        } catch (Exception e) {
            log("Error checking volume " + volume + " for playlist " + pid + ": " + e.getMessage());
        }
        return false;
    }


    //add multiple songs to playlist
    public static void addListToPlaylist(Context context, Long pid, ArrayList<Long> ids, boolean top) {
        if (pid == null || pid <= 0) return;

        log("adding songs to playlist Ids: " + ids.size() + " pid: " + pid);

        String[] cols = new String[]{
                MediaStore.Audio.Playlists.Members.PLAY_ORDER, MediaStore.Audio.Playlists.Members.AUDIO_ID
        };
        ContentValues values = new ContentValues();
        ContentResolver resolver = context.getContentResolver();
        String volume = getVolumeName(context, pid);
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(volume, pid);

        log("Querying members for: " + uri);
        Cursor cur = null;
        try {
            cur = resolver.query(uri, cols, null, null, null);
        } catch (SecurityException e) {
            log("SecurityException querying playlist members: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log("Error querying playlist members: " + e.getMessage());
            return;
        }

        //ids already has our new items at the top, just add the old ones to it
        // if adding to the end, just insert the old items. at 0
        ArrayList<Long> old = new ArrayList<>();
        while (cur != null && cur.moveToNext()) {
            long l = Long.parseLong(cur.getString(1));
            old.add(l);
            if (top) ids.add(0, l);
            else ids.add(l);

        }
        if (cur != null) cur.close();
        log("songs added to playlist Ids: " + ids.size());

        //todo delete old items
//        int p=0;
        try {
            for (long l : old) {
                String[] arg = {l + ""};//, p++ + ""};
                String where = MediaStore.Audio.Playlists.Members.AUDIO_ID + "=? ";//AND " +
//                    MediaStore.Audio.Playlists.Members.PLAY_ORDER + "=?";
                log("Deleting member: " + l + " from " + uri);
                resolver.delete(uri, where, arg);
            }
            //add the all the items in the new order

            for (int i = 0; i < ids.size(); i++) {
                values = new ContentValues();
                // Log.d("Music service", i +" saving song: " + t.getTitle() + songid);
                values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, i);
                values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, ids.get(i));
                log("Inserting member: " + ids.get(i) + " at pos " + i + " into " + uri);
                resolver.insert(uri, values);
            }
            log("addListToPlaylist successful");
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException modifying list in playlist: " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException modifying list in playlist (possible missing playlist): " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Error modifying playlist members: " + e.getMessage());
        }

    }

    //add 1 song to playlist
    public static void addToPlaylist(Context context, String pname, Long pid, Long sid, boolean top) {
        log("addToPlaylist: pname=" + pname + ", pid=" + pid + ", sid=" + sid + ", top=" + top);
        String[] cols = new String[]{
                MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                MediaStore.Audio.Playlists.Members.AUDIO_ID
        };

        ContentResolver resolver = context.getContentResolver();
        String volume = getVolumeName(context, pid);
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(volume, pid);
        log("Querying for base position: " + uri);
        Cursor cur = null;
        int base = 0;
        try {
            cur = resolver.query(uri, cols, null, null, null);
            if (cur != null) {
                if (!top && cur.moveToLast()) {

                    base = cur.getInt(0);
                    base += 1;
                }
                cur.close();
            }
        } catch (SecurityException e) {
            log("SecurityException querying for base: " + e.getMessage());
            throw e;
        }
        log("adding item --->>>>>base: " + base + " to " + pname);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base);
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, sid);
        try {
            log("Inserting into: " + uri);
            resolver.insert(uri, values);
            log("Insert successful");
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException adding to playlist: " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException adding to playlist: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Error adding to playlist: " + e.getMessage());
        }


    }

    //remove 1 song from playlist
    public static boolean deleteFromPlaylist(Context context, Long pid, String pname, String sid, int pos) {
        log("deleteFromPlaylist: pid=" + pid + ", pname=" + pname + ", sid=" + sid + ", pos=" + pos);


        ContentResolver resolver = context.getContentResolver();
        String volume = getVolumeName(context, pid);
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(volume, pid);
        log("Members URI: " + uri);

        String[] projection = {MediaStore.Audio.Playlists.Members._ID, MediaStore.Audio.Playlists.Members.AUDIO_ID};
        String sortOrder = MediaStore.Audio.Playlists.Members.PLAY_ORDER + " ASC";

        Cursor cursor = null;
        try {
            cursor = resolver.query(uri, projection, null, null, sortOrder);
            if (cursor != null) {
                log("Cursor count: " + cursor.getCount());
                if (cursor.moveToPosition(pos)) {
                    long memberId = cursor.getLong(0);
                    String foundSid = cursor.getString(1);
                    log("Found memberId=" + memberId + ", foundSid=" + foundSid + " at pos=" + pos);
                    if (foundSid.equals(sid)) {
                        try {
                            Uri deleteUri = ContentUris.withAppendedId(uri, memberId);
                            log("Deleting from URI: " + deleteUri);
                            int deleted = resolver.delete(deleteUri, null, null);
                            log("Deleted from MediaStore (by URI). memberId=" + memberId + ", count=" + deleted);
                            if (deleted == 0) {
                                log("Retry delete with selection by _ID on base members URI");
                                deleted = resolver.delete(uri, MediaStore.Audio.Playlists.Members._ID + "=?", new String[]{String.valueOf(memberId)});
                                log("Retry delete count=" + deleted);
                            }
                            if (deleted == 0) {
                                log("Final attempt: delete by AUDIO_ID and PLAY_ORDER");
                                String where = MediaStore.Audio.Playlists.Members.AUDIO_ID + "=? AND " + MediaStore.Audio.Playlists.Members.PLAY_ORDER + "=?";
                                String[] args = {sid, String.valueOf(pos)};
                                deleted = resolver.delete(uri, where, args);
                                log("Final attempt count=" + deleted);
                            }
                            if (deleted == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                log("All delete attempts failed. Throwing MemberSecurityException to trigger recovery flow if needed.");
                                throw new MemberSecurityException("Unable to delete member. Possible permission issue.", deleteUri);
                            }
                            removeFromM3u(pname, pos);
                            return deleted > 0;
                        } catch (SecurityException e) {
                            Log.e(TAG, "SecurityException deleting song from playlist: " + e.getMessage());
                            throw e;
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "IllegalArgumentException deleting from playlist: " + e.getMessage());
                        } catch (Exception e) {
                            Log.e(TAG, "Error deleting from playlist: " + e.getMessage());
                        }
                    } else {
                        log("AUDIO_ID mismatch: expected " + sid + " but found " + foundSid + " at pos " + pos);
                        // Try to find by sid if pos is wrong
                        cursor.moveToPosition(-1);
                        while (cursor.moveToNext()) {
                            if (cursor.getString(1).equals(sid)) {
                                long mId = cursor.getLong(0);
                                log("Found sid match at different position. memberId=" + mId);
                                int deleted = resolver.delete(uri, MediaStore.Audio.Playlists.Members._ID + "=?", new String[]{String.valueOf(mId)});
                                log("Deleted match from MediaStore. count=" + deleted);
                                return deleted > 0;
                            }
                        }
                    }
                } else {
                    log("Could not move to pos " + pos + " (total count: " + cursor.getCount() + ")");
                }
            } else {
                log("Cursor was null for playlist " + pid);
            }
        } catch (SecurityException e) {
            log("SecurityException querying playlist members: " + e.getMessage());
            throw e;
        } finally {
            if (cursor != null) cursor.close();
        }
        return false;
    }

    private static void removeFromM3u(String pname, int pos) {
        String path = Environment.getExternalStorageDirectory().toString() + "/Music/" + pname + ".m3u";
        File file = new File(path);
        if (!file.exists()) {
            log("m3u file not found: " + path);
            return;
        }

        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            log("Error reading m3u file: " + e.getMessage());
            return;
        }

        if (pos >= 0 && pos < lines.size()) {
            lines.remove(pos);
            try (PrintWriter writer = new PrintWriter(new FileOutputStream(file))) {
                for (String l : lines) {
                    writer.println(l);
                }
            } catch (IOException e) {
                log("Error writing m3u file: " + e.getMessage());
            }
            log("Removed item at pos " + pos + " from m3u file: " + pname);
        } else {
            log("Pos " + pos + " out of bounds for m3u file (size: " + lines.size() + ")");
        }
    }


    public static boolean deletePlaylist(Context context, String id) {
        Log.i("m6", "Deleting playlist " + id);
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        String[] arg = {id};
        try {
            int count = resolver.delete(uri, MediaStore.Audio.Playlists._ID + "=?", arg);
            Log.i("m6", id + " Playlist delete successful count: " + count);
            return count > 0;
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException deleting playlist: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting playlist: " + e.getMessage());
        }
        return false;
    }


    public static void newPlaylist(Context context, String name) {
        Log.i("m6", "Saving playlist " + name);
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.NAME, name);
        ContentResolver resolver = context.getContentResolver();
        try {
            resolver.insert(uri, values);
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException creating new playlist: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Error creating new playlist: " + e.getMessage());
        }
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
        if (cur != null) {
            while (cur.moveToNext()) {
                if (cur.getString(0).equals(name)) {
                    id = Long.parseLong(cur.getString(1));
                    Log.d("m6", "queue playlist id: " + id);
                    break;
                }
            }
            cur.close();
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
        ArrayList<Song> songs = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            songs.add(new Song(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getString(4)
                    , cursor.getString(5), cursor.getString(6), cursor.getString(7)));
        }
        if (cursor != null) cursor.close();
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
