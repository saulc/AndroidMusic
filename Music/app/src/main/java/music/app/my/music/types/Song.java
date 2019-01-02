package music.app.my.music.types;


public class Song extends Qbase{
	private int playOrder, key;
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

	public Song(String title, String gid, String artist, String album, String id, String filePath,  String artistId ){
		super();
		this.title = title;
		this.filePath = filePath;
		this.artist = artist;
		this.album = album;
		this.id = id;
		this.genre = gid;

		this.artistId = artistId;
		//this.duration = duration;
	}

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
	public Song(String title, String filePath, String artist, String album, String albumArt, String id, String genre, String duration, String artistId){
		super();
		this.title = title;
		this.filePath = filePath;
		this.artist = artist;
		this.album = album;
		this.albumArt = albumArt;
		this.id = id;
		this.genre = genre;
		this.duration = duration;
		this.artistId = artistId;
	}
	
	public int getKey(){
		return key;
	}
	public int getPlayOrder(){
		return playOrder;
	}
	public void setKey(int k){
		key = k;
	}
	public void setPlayOrder(int i){
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
