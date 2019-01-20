package music.app.my.music.types;

import java.util.ArrayList;
import java.util.Collections;

import music.app.my.music.types.Song;


public class plist extends Qbase{

	    //private SongLoader songloader;
	   // private ArrayList<HashMap<String, String>> songs;
	    private ArrayList<Song> songs;
	    private int songIndex=0;
	    private int repeatmode = 0;	//0 off, 1 repeat song, 2 repeat all
	    private boolean shuffled = false;//, repeatVal=false;
	    private ArrayList<Song> backup;

	    public plist(){
			super();
	    	//songs = new ArrayList<HashMap<String, String>>();
	    	songs = new ArrayList<Song>();
	    	songIndex = 0;
//	    	songloader = new SongLoader();
//	        songs = songloader.getPlayList();
	    }
	    public int repeat(){
	    	if(++repeatmode > 2) repeatmode = 0;
			//repeatVal = repeatmode > 0;
	    	return repeatmode;
	    }

		public int getRepeatMode(){
			return repeatmode;
		}

	public boolean isShuffled() {
		return shuffled;
	}

	public boolean shuffle(){
	    	if(songs.size() == 0)
	    		return false;
	    	if(shuffled)
	    	{
	    		songs = backup;
	    		shuffled = false;
	    	}
	    	else {
	    		backup = new ArrayList<Song>(songs);
	    		Song current = songs.remove(songIndex);
	    		Collections.shuffle(songs);
	    		songs.add(0, current);
	    		shuffled = true;;
	    		songIndex = 0;
	    	}
	    	return shuffled;

	    }
	    public void resetShuffle(){
	    	shuffled = false;
	    	backup = null;
	    }
	    public plist(ArrayList<Song> s){
	    	songs = s;
	    	songIndex =0;
	    }
	    public ArrayList<Song> getArray(){
	    	return songs;
	    }
	    public void addToTop(ArrayList<Song> s){
	    	if(getSize() > 0)
	    	songs.addAll(songIndex+1, s);
	    	else songs.addAll(s);
	    	if(shuffled)
	    		backup.addAll(s);
	    }
	    public void addTo(ArrayList<Song> s){
	    	songs.addAll(s);
	    	if(shuffled)
	    		backup.addAll(s);
	    }
	    public void addNextSong(Song s){
	    		if(getSize() > 0)
	    			songs.add(songIndex+1, s);
	    		else songs.add(s);
	    		if(shuffled)
	    			backup.add(s);
	    }
	    public void addSong(Song s){
	    	songs.add(s);
	    	if(shuffled)
    			backup.add(s);
	    }
	   
	    public void nextSong(){
	    	if(repeatmode == 1) return; //just play the same same. forever.

	    	if(hasNext())
	    		songIndex++;

			if(repeatmode == 2 && (songIndex == songs.size() )) {
				//last song and repeat is on
				songIndex = 0;
			}
	    }
	    public void previousSong(){
			if(repeatmode == 1) return; //just play the same same. forever.

			if(hasPrevious()) 		//regular
	    		songIndex--;

			//repeat list.
			if(repeatmode == 2 && (songIndex < 0 )) {
				//last song and repeat is on
				songIndex = songs.size() - 1;
			}
	    }
	    public boolean hasNext(){
	    	if(repeatmode == 1) return (songs.size() > 0); //either repeat mode is on as lnog as the queue isn't empty

			if(repeatmode == 2 && (songIndex == songs.size()-1 )) {
				//last song and repeat is on
				return true;
			}
			//regular mode, just make sure its in the list.
	    	return songs.size() > (songIndex + 1);
	    }
	    public boolean hasPrevious(){
	    	if(repeatmode == 1) return ( songs.size() > 0);
	    	//if its the first song
			if(repeatmode == 2 && (songIndex == 0 )) {
				//first song and repeat is on
				return true;
			}
	    	return (songIndex- 1) >= 0;
	    }
	    
	    public String nowPlayingPath(){
	    	return songs.get(songIndex).getFilePath();
	    }
	    public String nowPlayingTitle(){
	    	return songs.get(songIndex).getTitle();
	    }
	    
	    public String nextSongPath(){
	    	return songs.get(songIndex + 1).getFilePath();
	    }
	    public String nextSongTitle(){
	    	return songs.get(songIndex + 1).getTitle();
	    }
	    
	    public String getSongTitle(int i){
	    	if(i < songs.size() && i >= 0)
	    		return songs.get(i).getTitle();
	    	return null;
	    }
	    public String getSongPath(int i){
	    	if(i < songs.size() && i >= 0)
	    		return songs.get(i).getFilePath();
	    	return null;
	    }
	    public Song getCurrentSong(){
	    	if(songIndex < songs.size())
	    	return songs.get(songIndex);

	    	return null;
	    }
	    public int getIndex(){
	    	return songIndex;
	    
	    }
	    public void setIndex(int i){
	    	//if(i< songs.size())
	    	songIndex = i;
	    }
	    public int getSize(){
	    	return songs.size();
	    }
	    public Song removeSong(int i){
	    	
	    	Song s = songs.remove(i);
	      if(songIndex >= songs.size())	//in case you remove the last song.
	    		songIndex = songs.size()-1;
//	      else if(songIndex > i)
//				i--;

			if(shuffled)
	    		backup.remove(s);
	    	return s;
	    }
	    public void clearQueue(){
	    	songs.clear();
	    	resetShuffle();
	    	songIndex = 0;
	    }
	    public String getQueueInfo(){
	    	return songIndex+1 + " of " + (songs.size());
	    }
	    public Song getSong(int i){
	    	return songs.get(i);
	    	
	    }
}
