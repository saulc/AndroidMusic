package music.app.my.music.types;

import static java.lang.Integer.parseInt;

/**
 * Created by saul on 8/17/16.
 */
public class Album {
    private String album, artist, id, mart, artistId;
    private int songs;

    public Album(String mid, String albumName){
        album = albumName;
        id = mid;
        songs = 0;
    }
    public Album( String albumName, String artistname, String art, String song, String mid){
        album = albumName;
        artist = artistname;
        id = mid;
        songs = parseInt(song);
        mart = art;
        //artistId = artistid;
    }

    public String artistId(){ return artistId; }
    public String getArt(){ return  mart; }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
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

    public int getSongs() {
        return songs;
    }

    public void setSongs(int songs) {
        this.songs = songs;
    }




}
