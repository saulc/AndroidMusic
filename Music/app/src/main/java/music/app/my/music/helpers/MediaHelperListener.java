package music.app.my.music.helpers;

import java.util.ArrayList;

import music.app.my.music.types.Album;
import music.app.my.music.types.Artist;
import music.app.my.music.types.Genre;
import music.app.my.music.types.Playlist;
import music.app.my.music.types.Song;

/**
 * Created by saul on 7/26/16.
 */
public interface MediaHelperListener {


    void playlistLoaderFinished(ArrayList<Playlist> p);
    void artistLoaderFinished(ArrayList<Artist> a);
    void genreLoaderFinished(ArrayList<Genre> a);

    void playlistItemLoaderFinished(ArrayList<Song> s);
    void artistItemLoaderFinished(ArrayList<Song> s);
    void albumItemLoaderFinished(ArrayList<Song> s);
    void genreItemLoaderFinished(ArrayList<Song> s);
    void helperReady();

    void songLoadedFinished(ArrayList<Song> songs);

    void queueitemLoaderFinished(ArrayList<Song> songs);

    void albumLoaderFinished(ArrayList<Album> ar);
}
