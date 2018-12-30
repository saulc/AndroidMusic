package music.app.my.music.types;

/**
 * Created by saul on 8/17/16.
 */
public class Genre {

    private String genre, id;

    public Genre(String genre, String id) {
        this.genre = genre;
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
