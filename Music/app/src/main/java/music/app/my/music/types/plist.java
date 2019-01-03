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
	    private boolean shuffled = false, repeatVal=false;
	    private ArrayList<Song> backup;

	    public plist(){
			super();
	    	//songs = new ArrayList<HashMap<String, String>>();
	    	songs = new ArrayList<Song>();
	    	songIndex = 0;
//	    	songloader = new SongLoader();
//	        songs = songloader.getPlayList();
	    }
	    public boolean repeat(){
	    	if(repeatmode == 0){
	    		repeatmode = 1;

			}else if(repeatmode == 1){

				repeatmode = 2;
			}else if(repeatmode == 2){

				repeatmode = 0;
			}

			repeatVal = repeatmode > 0;
	    	return repeatVal;
	    }

		public int repeatMode(){
			return repeatmode;
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
	    	if(repeatmode == 1) return;

	    	if(hasNext())
	    		songIndex++;
			if(repeatmode == 2 && (songIndex == songs.size() )) {
				//last song and repeat is on
				songIndex = 0;
			}
	    }
	    public void previousSong(){
	    	if(hasPrevious())
	    		songIndex--;
	    }
	    public boolean hasNext(){
	    	if(repeatmode == 1) return true;

	    	return songs.size() > (songIndex + 1);
	    }
	    public boolean hasPrevious(){
	    	if(repeatVal && (songIndex <= 0))
	    		songIndex = songs.size();
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
	    }
	    public String getQueueInfo(){
	    	return songIndex+1 + " of " + (songs.size());
	    }
	    public Song getSong(int i){
	    	return songs.get(i);
	    	
	    }
}
