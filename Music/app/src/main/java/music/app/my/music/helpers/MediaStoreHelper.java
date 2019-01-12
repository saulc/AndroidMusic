package music.app.my.music.helpers;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;


import java.util.ArrayList;

import music.app.my.music.types.Album;
import music.app.my.music.types.Artist;
import music.app.my.music.types.Genre;
import music.app.my.music.types.Playlist;
import music.app.my.music.types.Song;

import static java.lang.Long.parseLong;

/*
	Media store helper handles query and loader response to android mediastore db.
 */

public class  MediaStoreHelper extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int mLOADER = -12;



	public  enum Media { songs, artists, playlists};

	private MediaHelperListener mListener;
	private enum LOADER_TYPE {QUEUE,  PLAYLIST, PLAYLISTITEMS, SONGS, ALBUMS,ALBUMITEMS, ARTISTS,ARTISTITEMS, GENRE, GENREITEMS, QUERY };
	private LOADER_TYPE myType = LOADER_TYPE.SONGS;
	private Context mContext;
	private String pid = null;
	private String pname = null;
	private String qid = "";

    private final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }
    public MediaStoreHelper() {
        super();
    }

	public MediaStoreHelper(Context c){
		super();
		mContext = c;
	}



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Media store helper fragment qFragCreated");
	if(mListener !=null)
        mListener.helperReady();

    }

	public void setListener(MediaHelperListener l){

		mListener = l;
	}

	public void search(String q){
		myType = LOADER_TYPE.QUERY;
		pname = q;

		getLoaderManager().initLoader(mLOADER, null, this);
	}

	public void loadSongs(){
		myType = LOADER_TYPE.SONGS;
		getLoaderManager().initLoader(mLOADER, null, this);

	}
	public void loadQueue(){

        log("Loading Queue...");
		myType = LOADER_TYPE.QUEUE;
		getLoaderManager().initLoader(mLOADER, null, this);

	}
	public void loadPlaylists(){

		myType = LOADER_TYPE.PLAYLIST;
		getLoaderManager().initLoader(mLOADER, null, this);
		
	}
	public void loadGenres() {
		myType = LOADER_TYPE.GENRE;
		getLoaderManager().initLoader(mLOADER, null, this);

	}
	public void loadGenreItems(String id) {
		myType = LOADER_TYPE.GENREITEMS;
		pid = id;
		getLoaderManager().initLoader(mLOADER, null, this);

	}

    public void loadArtists() {
        myType = LOADER_TYPE.ARTISTS;
        getLoaderManager().initLoader(mLOADER, null, this);

    }
	public void loadAlbums() {
		myType = LOADER_TYPE.ALBUMS;
		getLoaderManager().initLoader(mLOADER, null, this);

	}
	public void loadGenreItems(String id, String pname) {
		myType = LOADER_TYPE.GENREITEMS;
		pid = id;
		getLoaderManager().initLoader(mLOADER, null, this);

	}
	public void loadAlbumItems(String id, String pname) {
		myType = LOADER_TYPE.ALBUMITEMS;
		pid = id;
		getLoaderManager().initLoader(mLOADER, null, this);

	}


	public void loadArtistItems(String id, String pname) {
		myType = LOADER_TYPE.ARTISTITEMS;
		pid = id;
		getLoaderManager().initLoader(mLOADER, null, this);

	}

    public void loadPlaylistItems(String id, String name){
        log("Loading playlist: " + name);
        myType = LOADER_TYPE.PLAYLISTITEMS;
		pid = id;
		pname = name;
        getLoaderManager().initLoader(mLOADER, null, this);

    }

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        if(myType == LOADER_TYPE.PLAYLIST || myType == LOADER_TYPE.QUEUE) {
            return new CursorLoader(mContext, playlistUri, playlistProjection, null, null, playlistSortOrder);
        }
        else  if(myType == LOADER_TYPE.PLAYLISTITEMS) {

            log("Playlist Item loader Created");
			try {
				long lpid = parseLong(pid);
				Uri uri =  MediaStore.Audio.Playlists.Members.getContentUri("external", lpid);
				return new CursorLoader(mContext, uri, playlistMemberProjection, null, null, playlistMemberSort);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		else if(myType == LOADER_TYPE.GENREITEMS)
		{
			try {
				long lpid = parseLong(pid);
				Uri uri =  MediaStore.Audio.Genres.Members.getContentUri("external", lpid);

				return new CursorLoader(mContext, uri, genreMembersProjection, null, null, genreMembersSortOrder);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

		}
		else if(myType == LOADER_TYPE.GENRE)
		{
			return new CursorLoader(mContext, genreUri, genreProjection, null, null, genreSortOrder);
		}
        else if(myType == LOADER_TYPE.ARTISTS)
        {
            return new CursorLoader(mContext, artistUri, artistProjection, null, null, artistSortOrder);
        }
        else if(myType == LOADER_TYPE.ARTISTITEMS)
		{
			log("Artist Item loader Created");
			String arg[] = {pid};

			return new CursorLoader(mContext, songUri, defaultProjection, artistMemberSelection, arg , defaultSort);
		}

        else if(myType == LOADER_TYPE.ALBUMS)
        {
			log("Albums loader Created");
            return new CursorLoader(mContext, albumUri, albumProjection, null, null, albumSort);
        }
		else if(myType == LOADER_TYPE.ALBUMITEMS)
		{
			log("Album Item loader Created");
			String arg[] = {pid};

			return new CursorLoader(mContext, songUri, albumMemberProjection, albumMemberSelection, arg , albumMemeberSort);
		}

		else  if(myType == LOADER_TYPE.SONGS) {

			log("Song Item loader Created");

				return new CursorLoader(mContext, songUri, defaultProjection , defaultSelection, null, defaultSort);

		}
		else  if(myType == LOADER_TYPE.QUERY) {

			log("Query loader Created");

			return new CursorLoader(mContext, songUri, defaultProjection , defaultSelection, null, defaultSort);

		}
            return new CursorLoader(mContext, playlistUri, playlistProjection, null, null, playlistSortOrder);


	}
	public boolean findQueuePlaylist(){
		Log.d("M6", "Looking for queue playlist");
		ContentResolver resolver = mContext.getContentResolver();
		String[] playlistProjection = { MediaStore.Audio.Playlists.NAME,
				MediaStore.Audio.Playlists._ID};
		Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		Cursor cur = resolver.query(uri, playlistProjection, null, null, null);

		while(cur.moveToNext()){
			if(cur.getString(0).equals("QUEUE")){
				qid = cur.getString(1);
				Log.d("m6", "queue playlist id: " + cur.getString(1));
				return true;
			}
		}
		return false;
	}

	public void saveQueue(){
		Log.i("m6", "Saving Queue");
		Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		ContentValues values = new ContentValues();
		values.put(MediaStore.Audio.Playlists.NAME, "QUEUE");
		ContentResolver resolver = mContext.getContentResolver();
		resolver.insert(uri, values);

	}


	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {

		if(myType == LOADER_TYPE.QUEUE){
			if(qid.compareTo("") == 0)
				if(!findQueuePlaylist())
					saveQueue();

			log("Searching for Queue id " + qid);

			while(cursor.moveToNext()){
				if( cursor.getString(1).compareTo(qid) == 0) {
                    log("Found Queue Playlist Id.");
					//qid = cursor.getString(1);
                    loadPlaylistItems(qid, "QUEUE");
					return;
				}
			}

		}
		else if(myType == LOADER_TYPE.PLAYLIST)
		{

			ArrayList<Playlist> pl = new ArrayList<Playlist>();
		  while(cursor.moveToNext()){
	    	  pl.add( new Playlist( cursor.getString(0), cursor.getString(1) ) );
		       // adapter.add(cursor.getString(0) + "||" + cursor.getString(1) + "||" +   cursor.getString(2) + "||" +   cursor.getString(3) + "||" +  cursor.getString(4) + "||" +  cursor.getString(5));
			}

			mListener.playlistLoaderFinished(pl);
			return;
		}
		else  if(myType == LOADER_TYPE.PLAYLISTITEMS) {
			log("Playlist Items loaded");
			ArrayList<Song> songs = new ArrayList<Song>();
			while(cursor.moveToNext()) {
				songs.add(new Song(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3), cursor.getString(4)
						, cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8)));
			}
			log("Returning Playlist with "+ songs.size() + " Items to activity");
			if(pid.compareTo(qid) == 0) mListener.queueitemLoaderFinished(songs);
			else mListener.playlistItemLoaderFinished(songs);

			return;
		}
		else if(myType == LOADER_TYPE.GENRE)
		{

			ArrayList<Genre> pl = new ArrayList<Genre>();
			while(cursor.moveToNext()){
				pl.add( new Genre( cursor.getString(0), cursor.getString(1) ) );
				// adapter.add(cursor.getString(0) + "||" + cursor.getString(1) + "||" +   cursor.getString(2) + "||" +   cursor.getString(3) + "||" +  cursor.getString(4) + "||" +  cursor.getString(5));
			}

			mListener.genreLoaderFinished(pl);
			return;
		}

		else  if(myType == LOADER_TYPE.GENREITEMS) {
			log("Genre Items loaded");
			ArrayList<Song> songs = new ArrayList<Song>();
			while(cursor.moveToNext()) {
				songs.add(new Song(cursor.getString(0), cursor.getString(1)
						,cursor.getString(2), cursor.getString(3), cursor.getString(4)
						, cursor.getString(5), cursor.getString(6) ) );
			}
			log("Returning Genre with "+ songs.size() + " Items to activity");
			 mListener.genreItemLoaderFinished(songs);

			return;
		}


		else  if(myType == LOADER_TYPE.ALBUMITEMS) {
			log("Album Items loaded");
			ArrayList<Song> songs = new ArrayList<Song>();
			while(cursor.moveToNext()) {
				songs.add(new Song(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3), cursor.getString(4)
						, cursor.getString(5), cursor.getString(6) , cursor.getString(7) ));
			}
			log("Returning Album with "+ songs.size() + " Items to activity");
			 mListener.albumItemLoaderFinished(songs);

			return;
		}


		else  if(myType == LOADER_TYPE.ARTISTITEMS) {
			log("Artist Items loaded");
			ArrayList<Song> songs = new ArrayList<Song>();
			while(cursor.moveToNext()) {
				songs.add(new Song(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3), cursor.getString(4)
						, cursor.getString(5), cursor.getString(6), cursor.getString(7) ));
			}
			log("Returning Artist with "+ songs.size() + " Items to activity");
			mListener.artistItemLoaderFinished(songs);

			return;
		}

        else if(myType == LOADER_TYPE.ARTISTS)
        {
			log("Artists  loaded");
            ArrayList<Artist> ar = new ArrayList<>();
            while(cursor.moveToNext()){
               // ar.add( new Artist( cursor.getString(0), cursor.getString(1) ) );
				ar.add( new Artist( cursor.getString(0), cursor.getString(1 ), cursor.getString(2), cursor.getString(3) ));
                // adapter.add(cursor.getString(0) + "||" + cursor.getString(1) + "||" +   cursor.getString(2) + "||" +   cursor.getString(3) + "||" +  cursor.getString(4) + "||" +  cursor.getString(5));
            }
			log("found " + ar.size() + " Artist(s)");
            mListener.artistLoaderFinished(ar);
            return;
        }
		else if(myType == LOADER_TYPE.ALBUMS)
		{
			log("Albums loaded");
			ArrayList<Album> ar = new ArrayList<>();
			while(cursor.moveToNext()){
				//ar.add( new Album( cursor.getString(4), cursor.getString(1) ) );
				ar.add( new Album( cursor.getString(0), cursor.getString(1) , cursor.getString(2), cursor.getString(3 ), cursor.getString(4)    ));
				// adapter.add(cursor.getString(0) + "||" + cursor.getString(1) + "||" +   cursor.getString(2) + "||" +   cursor.getString(3) + "||" +  cursor.getString(4) + "||" +  cursor.getString(5));
			}
			log("found " + ar.size() + " Album(s)");
			mListener.albumLoaderFinished(ar);
			return;
		}


		else  if(myType == LOADER_TYPE.SONGS) {
			log("SONG Items loaded");
			ArrayList<Song> songs = new ArrayList<Song>();
			while(cursor.moveToNext()) {
				songs.add(new Song(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3), cursor.getString(4)
						, cursor.getString(5), cursor.getString(6), cursor.getString(7 )));
			}
			log("Returning SONGs  to activity");
			mListener.songLoadedFinished(songs);
			return;
		}


		else  if(myType == LOADER_TYPE.QUERY) {
			log("SONG Items loaded");
			ArrayList<Song> songs = new ArrayList<Song>();
			while(cursor.moveToNext()) {
				songs.add(new Song(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3), cursor.getString(4)
						, cursor.getString(5), cursor.getString(6), cursor.getString(7 )));
			}
			log("Querying " + songs.size() + " songs for: " + pname);

			mListener.queryLoaderFinished(songs);
			return;
		}

		
	}
	
	 
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

		log("Loader Reset");
	}
	
//	public void 
	
	public Cursor getMedia(String type, String where, CursorAdapter adapter){
	//	if(type.equals("albums")){
		//	getLoaderManager().initLoader(0, null, adapter);
			//initLoader(0, null, adapter);
	//	}
		return null;
	}

	private Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	private String defaultSort =  MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
	private String defaultSelection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
	private String[] defaultProjection = {
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.ARTIST_ID
            
    };

	   private String[] albumProjection = {
	            MediaStore.Audio.Albums.ALBUM,
	            MediaStore.Audio.Albums.ARTIST,
	            MediaStore.Audio.Albums.ALBUM_ART,
	            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
	            MediaStore.Audio.Albums._ID
	    };
	   private Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
	   private String albumSort = MediaStore.Audio.Albums.ALBUM + " COLLATE LOCALIZED ASC";

	   private String[] albumMemberProjection = {
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ALBUM_ID,
				MediaStore.Audio.Media.TRACK,
			    MediaStore.Audio.Media.ARTIST_ID

	   };
	   private String albumMemeberSort =  MediaStore.Audio.Media.TRACK + " COLLATE LOCALIZED ASC";
	   private String albumMemberSelection =  MediaStore.Audio.Media.ALBUM_ID + "=?";


	   private String artistMemberSelection =  MediaStore.Audio.Media.ARTIST_ID + "=?";

	   private Uri artistUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
	   private String artistSortOrder = MediaStore.Audio.Artists.ARTIST   + " COLLATE LOCALIZED ASC";
	   private String[] artistProjection = {
				MediaStore.Audio.Artists._ID,
				MediaStore.Audio.Artists.ARTIST,
				MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
				MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
				};

	private String[] playlistMemberProjection = {
			MediaStore.Audio.Playlists.Members.TITLE,
			MediaStore.Audio.Playlists.Members.DATA,
			MediaStore.Audio.Playlists.Members.ARTIST,
			MediaStore.Audio.Playlists.Members.ALBUM,
			MediaStore.Audio.Playlists.Members.DURATION,
			MediaStore.Audio.Playlists.Members.AUDIO_ID,
			MediaStore.Audio.Playlists.Members.ALBUM_ID,
			MediaStore.Audio.Playlists.Members.PLAY_ORDER,
			MediaStore.Audio.Playlists.Members.ARTIST_ID

	};

	private String playlistMemberSort =  MediaStore.Audio.Playlists.Members.PLAY_ORDER + " COLLATE LOCALIZED ASC";


	private  String[] playlistProjection = { MediaStore.Audio.Playlists.NAME,
			MediaStore.Audio.Playlists._ID,
	};
	private Uri playlistUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
	private String playlistSortOrder = MediaStore.Audio.Playlists.NAME   + " COLLATE LOCALIZED ASC";

	private Uri genreUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
	private String genreSortOrder = MediaStore.Audio.Genres.NAME   + " COLLATE LOCALIZED ASC";
	private String[] genreProjection = {
			MediaStore.Audio.Genres.NAME,
			MediaStore.Audio.Genres._ID

	};


	private String genreMembersSortOrder = MediaStore.Audio.Genres.Members.TITLE   + " COLLATE LOCALIZED ASC";
	private String[] genreMembersProjection = {
			MediaStore.Audio.Genres.Members.TITLE,
			MediaStore.Audio.Genres.Members.GENRE_ID,
			MediaStore.Audio.Genres.Members.ARTIST,
			MediaStore.Audio.Genres.Members.ALBUM,
			MediaStore.Audio.Genres.Members.AUDIO_ID,
			MediaStore.Audio.Genres.Members.DATA,
			MediaStore.Audio.Genres.Members.ARTIST_ID

	};

}


