package in.benjamm.pms.DataModel.Model;

import in.benjamm.pms.DataModel.Singletons.Database;

import java.io.File;
import java.sql.*;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;


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

    public Integer getItemTypeId() { return null; }

    /**
     * Type of media (song, video, etc)
     */
    protected MediaItemType _mediaItemType;
    public MediaItemType getMediaItemType() { return _mediaItemType; }
    public void setMediaItemType(MediaItemType type) { _mediaItemType = type; }

    /**
     * Unique identifier
     */
	protected Integer _itemId;
    public Integer getItemId() { return _itemId; }
    public void setItemId(int itemId) { _itemId = itemId; }

    /**
     * Associated cover art
     */
	protected Integer _artId;
    public Integer getArtId()  { return _artId; }
    public void setArtId(int artId) { _artId = artId; }

    /**
     * Folder containing this media
     */
	protected Integer _folderId;
    public Integer getFolderId() { return _folderId; }
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
	protected Integer _duration;
    public Integer getDuration() { return _duration; }
    public void setDuration(int duration) { _duration = duration; }

    /**
     * Bitrate in bits per second
     */
	protected Long _bitrate;
    public Long getBitrate() { return _bitrate; }
    public void setBitrate(Long bitrate) { _bitrate = bitrate; }

    /**
     * Size on disk in bytes
     */
	protected Long _fileSize;
    public Long getFileSize() { return _fileSize; }
    public void setFileSize(Long fileSize) { _fileSize = fileSize; }

    /**
     * File's last modified date in Unix timestamp
     */
	protected Long _lastModified;
    public Long getLastModified() { return _lastModified; }
    public void setLastModified(Long lastModified) { _lastModified = lastModified; }

    /**
     * Name of file on disk
     */
	protected String _fileName;
    public String getFileName() { return _fileName; }
    public void setFileName(String fileName) { _fileName = fileName; }

    protected Integer _releaseYear;
    public Integer getReleaseYear() { return _releaseYear; }
    public void setReleaseYear(int releaseYear) { _releaseYear = releaseYear; }

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

    public File file()
    {
        String fullPath = new Folder(getFolderId()).getFolderPath() + File.separator + getFileName();
        return new File(fullPath);
    }

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
        boolean needsUpdating = true;

        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT COUNT(*) AS count FROM song WHERE song_folder_id = ? AND song_file_name = ? AND song_last_modified = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, folderId);
            s.setObject(2, fileName);
            s.setObject(3, lastModified);
            r = s.executeQuery();

            if (r.next())
            {
                if (r.getInt("count") >= 1)
                    needsUpdating = false;
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }

		return needsUpdating;
	}
}
