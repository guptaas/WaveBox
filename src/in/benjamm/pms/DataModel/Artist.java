package in.benjamm.pms.DataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 5/9/12
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Artist
{
    /*
     * Properties
     */

    /**
     * Unique identifier
     */
    private int _artistId;
    public int getArtistId() { return _artistId; }
    public void setArtistId(int artistId) { _artistId = artistId; }

    /**
     * Name from tags
     */
    private String _artistName;
    public String getArtistName() { return _artistName; }
    public void setArtistName(String artistName) { _artistName = artistName; }

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
     * Generate list of albums for this artist
     */
    public List<Album> listOfAlbums()
    {
        return null;
    }

    /**
     * Generate list of all songs in this artist's albums
     */
    public List<Song> listOfSongs()
    {
        return null;
    }
}
