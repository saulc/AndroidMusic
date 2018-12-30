package music.app.my.music.types;

/**
 * Created by saul on 7/26/16.
 */
public class Playlist {

    private String name, id;


    public Playlist(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
