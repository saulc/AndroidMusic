package music.app.my.music.types;


public class Song extends Qbase{
	private int  key;
	private String playOrder;
	private String title;
	private String filePath;
	private String artist;
	private String album;
	//private String albumCover;
	private String id;
	private String genre;
	private String duration;
	private String albumArt;
	private String albumId;
	private String artistId;
	private String year;
//	
//	public Song(String title, String filePath, String artist, String album){
//		this.title = title;
//		this.filePath = filePath;
//		this.artist = artist;	
//		this.album = album;
//	}

	public Song(String title, String gid ) {
		super();
		this.title = title;
		this.genre = gid;
	}

	/*
	genreMembersProjection = {
			MediaStore.Audio.Genres.Members.TITLE,
			MediaStore.Audio.Genres.Members.GENRE_ID,
			MediaStore.Audio.Genres.Members.AUDIO_ID,
			MediaStore.Audio.Genres.Members.DURATION,
			MediaStore.Audio.Genres.Members.DATA,
			MediaStore.Audio.Genres.Members.ARTIST,
			MediaStore.Audio.Genres.Members.ARTIST_ID,
			MediaStore.Audio.Genres.Members.ALBUM,
			MediaStore.Audio.Genres.Members.ALBUM_ID,
			MediaStore.Audio.Genres.Members.YEAR

	};		genre memebers. 10 args.
	 */

	public Song(String title, String gid, String id, String duration, String filePath,
				String artist, String artistId, String album, String albumId, String year){
		super();
		this.title = title;
		this.filePath = filePath;
		this.artist = artist;
		this.album = album;
		this.id = id;
		this.genre = gid;
		this.albumId = albumId;
		this.year = year;
		this.artistId = artistId;
		this.duration = duration;
	}

	//default projection/ all songs
	public Song(String title, String filePath, String artist, String album, String duration, String id, String albumId, String artistId ){
		super();
		this.title = title;
		this.filePath = filePath;
		this.artist = artist;
		this.album = album;
		this.id = id;
		this.duration = duration;
		this.albumId = albumId;

		this.artistId = artistId;
	}

	/*
	playlistMemberProjection = {
			MediaStore.Audio.Playlists.Members.TITLE,
			MediaStore.Audio.Playlists.Members.DATA,
			MediaStore.Audio.Playlists.Members.ARTIST,
			MediaStore.Audio.Playlists.Members.ALBUM,
			MediaStore.Audio.Playlists.Members.DURATION,
			MediaStore.Audio.Playlists.Members.AUDIO_ID,
			MediaStore.Audio.Playlists.Members.ALBUM_ID,
			MediaStore.Audio.Playlists.Members.PLAY_ORDER,
			MediaStore.Audio.Playlists.Members.ARTIST_ID

		playlists and album memebers. 9 args.
	 */

	public Song(String title, String filePath, String artist, String album, String duration,
				String id, String albumId, String play, String artistId){
		super();
		this.title = title;
		this.filePath = filePath;
		this.artist = artist;
		this.album = album;
		this.albumId = albumId;
		this.id = id;
		this.playOrder = play;
		this.duration = duration;
		this.artistId = artistId;
	}

	public String getYear(){ return  year; }
	public void setYear(String y){ year = y; }
	public int getKey(){
		return key;
	}
	public String getPlayOrder(){
		return playOrder;
	}
	public void setKey(int k){
		key = k;
	}
	public void setPlayOrder(String i){
		playOrder = i;
	}
	public String getAlbumArt(){
	
		return albumArt;
	}
	public void setAlbumArt(String a){
		albumArt = a;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtistId(String artistId) {
		this.artistId = artistId;
	}
	public String getArtistId() {
		return artistId;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
//	public String getAlbumCover() {
//		return albumCover;
//	}
//	public void setAlbumCover(String albumCover) {
//		this.albumCover = albumCover;
//	}
	public String getAlbumId(){
		return albumId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public Long getLongDuration(){
		if(duration == null)
			return (long) 0;
		return Long.parseLong(duration);
	}
	public int getDuration() {
		if (duration == null)
			return 0;
		//String temp = "";
		return (int) Long.parseLong(duration) / 1000;
	
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}

	public int compareTo(Song other){
		return key-other.key;
	}
	
	@Override
	public String toString(){
		return title;
	}
}
