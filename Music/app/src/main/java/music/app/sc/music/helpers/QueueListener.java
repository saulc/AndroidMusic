package music.app.sc.music.helpers;

import music.app.sc.music.types.Song; /**
 * Created by saul on 7/27/16.
 */
public interface QueueListener  {


    void qFragCreated();

    void onQueueItemClicked(Song mItem, int position);

    void onQueueItemLongClicked(Song mItem, int position);

    void swapItems(int i, int i1);

}
