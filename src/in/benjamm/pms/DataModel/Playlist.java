package in.benjamm.pms.DataModel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 5/9/12
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Playlist
{
    /*
     * Properties
     */

    /**
     * Unique identifier
     */
    private Integer _playlistId;
    public int getPlaylistId() { return _playlistId; }
    public void setPlaylistId(int playlistId) { _playlistId = playlistId; }

    /**
     * Name as chosen by user
     */
    private String _playlistName;
    public String getPlaylistName() { return _playlistName; }
    public void setPlaylistName(String playlistName) { _playlistName = playlistName; }

    /**
     * Number of media items in this playlist
     */
    private Integer _playlistCount;
    public int getPlaylistCount() { return _playlistCount; }
    public void setPlaylistCount(int playlistCount) { _playlistCount = playlistCount; }



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
     * Media item at the specified index, null if invalid index
     */
    public MediaItem mediaItemAtIndex(int index)
    {
        return null;
    }

    /**
     * List of media items in this playlist
     */
    public List<MediaItem> listOfMediaItems()
    {
        return null;
    }

    /**
     * Remove media item from playlist
     *
     * Removes the first occurrence of this item if the playlist
     * contains duplicates
     */
    public void removeMediaItem(MediaItem item)
    {}

    /**
     * Remove media item at specified index
     */
    public void removeMediaItemAtIndex(int index)
    {}

    /**
     * Move a playlist item
     */
    public void moveMediaItem(int fromIndex, int toIndex)
    {}
}
