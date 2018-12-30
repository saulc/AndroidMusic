package music.app.my.music.helpers;

import music.app.my.music.types.Song; /**
 * Created by saul on 7/27/16.
 */
public interface QueueListener  {


    void qFragCreated();

    void onQueueItemClicked(Song mItem, int position);
}
