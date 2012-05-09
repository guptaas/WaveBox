package in.benjamm.pms.DataModel;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 5/9/12
 * Time: 12:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class Song extends MediaItem
{
    /*
     * Properties
     */

    /**
     * Artist object for this song
     */
    private Artist _artist;
    public Artist getArtist() { return _artist; }
    public void setArtist(Artist artist) { _artist = artist; }

    /**
     * Album object for this song
     */
    private Album _album;
    public Album getAlbum() { return _album; }
    public void setAlbum(Album album) { _album = album; }

    /**
     * Song name from tags
     */
    private String _songName;
    public String getSongName() { return _songName; }
    public void setSongName(String songName) { _songName = songName; }

    /**
     * Track number from tags (not primitive so it can be null)
     */
    private Integer _trackNumber;
    public Integer getTrackNumber() { return _trackNumber; }
    public void setTrackNumber(Integer trackNumber) { _trackNumber = trackNumber; }

    /**
     * Disc number from tags (not primitive so it can be null)
     */
    private Integer _discNumber;
    public Integer getDiscNumber() { return _discNumber; }
    public void setDiscNumber(Integer discNumber) { _discNumber = discNumber; }



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
     * Add or update a tag
     */
    public void updateTag(String tagName, String tagValue)
    {}

    /**
     * Use web service to tag this song automatically
     */
    public void autoTag()
    {}

    /**
     * Rescan the tags from this file
     */
    public void rescan()
    {}
}
