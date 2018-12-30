package music.app.my.music.player;


import java.util.Comparator;
import java.util.PriorityQueue;

import music.app.my.music.types.Song;

public class pQueue {
	   //private ArrayList<Song> songs;
		private PriorityQueue<Song> songs;
	    private int songIndex=0;
	    private int key;
	    private boolean shuffled = false, repeatVal=false;
	 
	    
	    public pQueue(){
	    	//if(!shuffled)
	    	songs = new PriorityQueue<Song>(100, new SongOrderComparator());
	    	songIndex = 0;
	    }
	
	    
	    public int getKey(){
	    	return key;
	    }
	    public void setKey(int k){
	    	key = k;
	    }
	    public boolean repeat(){
	    	repeatVal = !repeatVal;
	    	return repeatVal;
	    }
	    public boolean shuffle(){
	    	if(songs.size() == 0)
	    		return false;
	    	if(shuffled)
	    	{
	    	
	    	}
	    	else {
	    	
	    	}
	    	return shuffled;
	    		
	    }
	    public void resetShuffle(){
	    	shuffled = false;
	    }
	 
	  

}


class SongOrderComparator implements Comparator<Song> {

	@Override
	public int compare(Song x, Song y) {
		
		return x.getPlayOrder() - y.getPlayOrder();
	}
	
}
