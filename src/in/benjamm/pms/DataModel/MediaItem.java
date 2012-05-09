package in.benjamm.pms.DataModel;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 5/8/12
 * Time: 11:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class MediaItem
{
    /*
     * Properties
     */

    /**
     * Type of media (song, video, etc)
     */
    private MediaItemType _mediaItemType;
    public MediaItemType getMediaItemType() { return _mediaItemType; }
    public void setMediaItemType(MediaItemType type) { _mediaItemType = type; }

    /**
     * Unique identifier
     */
    private int _itemId;
    public int getItemId() { return _itemId; }
    public void setItemId(int itemId) { _itemId = itemId; }

    /**
     * Associated cover art
     */
    private int _artId;
    public int getArtId()  { return _artId; }
    public void setArtId(int artId) { _artId = artId; }

    /**
     * Folder containing this media
     */
    private int _folderId;
    public int getFolderId() { return _folderId; }
    public void setFolderId(int folderId) { _folderId = folderId; }

    /**
     * Media format (mp3, aac, etc)
     */
    private FileType _fileType;
    public FileType getFileType() { return _fileType; }
    public void setFileType(FileType fileType) { _fileType = fileType; }

    /**
     * Duration in seconds
     */
    private int _duration;
    public int getDuration() { return _duration; }
    public void setDuration(int duration) { _duration = duration; }

    /**
     * Bitrate in bits per second
     */
    private int _bitrate;
    public int getBitrate() { return _bitrate; }
    public void setBitrate(int bitrate) { _bitrate = bitrate; }

    /**
     * Size on disk in bytes
     */
    private long _fileSize;
    public long getFileSize() { return _fileSize; }
    public void setFileSize(long fileSize) { _fileSize = fileSize; }

    /**
     * File's last modified date in Unix timestamp
     */
    private long _lastModified;
    public long getLastModified() { return _lastModified; }
    public void setLastModified(long lastModified) { _lastModified = lastModified; }

    /**
     * Name of file on disk
     */
    private String _fileName;
    public String getFileName() { return _fileName; }
    public void setFileName(String fileName) { _fileName = fileName; }



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
     * Add this song to the end of a playlist
     */
    public void addToPlaylist(Playlist thePlaylist)
    {}

    /**
     * Insert this song into a playlist at the desired position
     *
     * If index is longer than playlist count, then song is added to end.
     * If index is < 0, the song is inserted at the beginning
     */
    public void addToPlaylistAtIndex(Playlist thePlaylist, int index)
    {}
}
