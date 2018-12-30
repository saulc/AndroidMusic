package music.app.my.music.types;

import static java.lang.Integer.parseInt;

/**
 * Created by saul on 8/17/16.
 */
public class Artist {


    private String artist, id;
    private int albums, songs;

    public Artist(String mId, String artistName){
        artist = artistName;
        id = mId;
        albums = 0;
        songs = 0;
    }
    public Artist(String mId, String artistName, String nalbums,  String nsongs){
        artist = artistName;
        id = mId;
        albums = parseInt(nalbums);
        songs = parseInt(nsongs);
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAlbums() {
        return albums;
    }

    public void setAlbums(int albums) {
        this.albums = albums;
    }

    public int getSongs() {
        return songs;
    }

    public void setSongs(int songs) {
        this.songs = songs;
    }


}
