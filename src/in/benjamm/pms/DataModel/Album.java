package in.benjamm.pms.DataModel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 5/9/12
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Album
{
    /*
     * Properties
     */

    /**
     * Artist object for this album
     */
    private Artist _artist;
    public Artist getArtist() { return _artist; }
    public void setArtist(Artist artist) { _artist = artist; }

    /**
     * Unique identifier
     */
    private int _albumId;
    public int getAlbumId() { return _albumId; }
    public void setAlbumId(int albumId) { _albumId = albumId; }

    /**
     * Name from tags
     */
    private String _albumName;
    public String getAlbumName() { return _albumName; }
    public void setAlbumName(String albumName) { _albumName = albumName; }

    /**
     * Four digit release year from tags
     */
    private int _releaseYear;
    public int getReleaseYear() { return _releaseYear; }
    public void setReleaseYear(int releaseYear) { _releaseYear = releaseYear; }

    /**
     * Associated cover art
     */
    private int _artId;
    public int getArtId() { return _artId; }
    public void setArtId(int artId) { _artId = artId; }



    /*
     * Constructor(s)
     */

    // Constuctors here



    /*
     * Private methods
     */

    // Private methods here



    /*
     * Public methods
     */

    /**
     * Use web service to tag this album's files automatically
     */
    public void autoTag()
    {}

    /**
     * Generate list of all songs in this albums
     */
    public List<Song> listOfSongs()
    {
        return null;
    }

	static public int albumIdForName(String albumName)
	{
		return 0;
	}

	static public Album albumForName(String albumName)
	{
		return new Album();
	}
}
