package music.app.my.music.helpers;

import music.app.my.music.player.myPlayer;
import music.app.my.music.types.Song;

public interface MixListener {


    void onMixViewCreated();

    void onMixItemClicked(String mItem, int position);

    void onMixItemLongClicked(myPlayer mItem, int position);
}
