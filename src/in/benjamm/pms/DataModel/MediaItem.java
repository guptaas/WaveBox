package in.benjamm.pms.DataModel;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    protected MediaItemType _mediaItemType;
    public MediaItemType getMediaItemType() { return _mediaItemType; }
    public void setMediaItemType(MediaItemType type) { _mediaItemType = type; }

    /**
     * Unique identifier
     */
	protected int _itemId;
    public int getItemId() { return _itemId; }
    public void setItemId(int itemId) { _itemId = itemId; }

    /**
     * Associated cover art
     */
	protected int _artId;
    public int getArtId()  { return _artId; }
    public void setArtId(int artId) { _artId = artId; }

    /**
     * Folder containing this media
     */
	protected int _folderId;
    public int getFolderId() { return _folderId; }
    public void setFolderId(int folderId) { _folderId = folderId; }

    /**
     * Media format (mp3, aac, etc)
     */
	protected FileType _fileType;
    public FileType getFileType() { return _fileType; }
    public void setFileType(FileType fileType) { _fileType = fileType; }

    /**
     * Duration in seconds
     */
	protected int _duration;
    public int getDuration() { return _duration; }
    public void setDuration(int duration) { _duration = duration; }

    /**
     * Bitrate in bits per second
     */
	protected long _bitrate;
    public long getBitrate() { return _bitrate; }
    public void setBitrate(int bitrate) { _bitrate = bitrate; }

    /**
     * Size on disk in bytes
     */
	protected long _fileSize;
    public long getFileSize() { return _fileSize; }
    public void setFileSize(long fileSize) { _fileSize = fileSize; }

    /**
     * File's last modified date in Unix timestamp
     */
	protected long _lastModified;
    public long getLastModified() { return _lastModified; }
    public void setLastModified(long lastModified) { _lastModified = lastModified; }

    /**
     * Name of file on disk
     */
	protected String _fileName;
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

	public static boolean fileNeedsUpdating(File file)
	{
		int folderId = (new Folder(file.getParent())).getFolderId();
		String fileName = file.getName();
		long lastModified = file.lastModified();

		String query = "SELECT COUNT(*) FROM songs WHERE folder_id = " + folderId + ", file_name = " + fileName + ", last_modified = " + lastModified;
		ResultSet result = null;
		try
		{
			result = Settings.getDbStatement().executeQuery(query);
			if (result.next())
			{
				if (result.getInt(0) >= 1)
					return false;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return true;
	}
}
