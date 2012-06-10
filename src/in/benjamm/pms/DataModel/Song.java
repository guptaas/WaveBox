package in.benjamm.pms.DataModel;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.sql.SQLException;

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

	Song (AudioFile audioFile, int folderId)
	{
		// Create header and tag objects
		AudioHeader header = audioFile.getAudioHeader();
		Tag tag = audioFile.getTag();

		// Get the attributes
		_folderId = folderId;
		_artist = Artist.artistForName(tag.getFirst(FieldKey.ARTIST));
		_album = Album.albumForName(tag.getFirst(FieldKey.ALBUM));
		_fileType = FileType.fileTypeForJAudioTaggerFormatString(header.getFormat());
		_songName = tag.getFirst(FieldKey.TITLE);
		_trackNumber = Integer.valueOf(tag.getFirst(FieldKey.TRACK));
		_discNumber = Integer.valueOf(tag.getFirst(FieldKey.DISC_NO));
		_duration = header.getTrackLength();
		_bitrate = header.getBitRateAsNumber();
		File file = audioFile.getFile();
		_fileSize = file.length();
		_lastModified = file.lastModified();
		_fileName = file.getName();
	}



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

	public void updateDatabase()
	{
		// Prepare the query
		String query = "INSERT INTO song";
		query += "folder_id = " + getFolderId();
		query += "artist_id = " + getArtist().getArtistId();
		query += "album_id = " + getAlbum().getAlbumId();
		query += "file_type_id = " + getFileType().fileTypeId();
		query += "song_name = " + getSongName();
		query += "track_num = " + getTrackNumber();
		query += "disc_num = " + getDiscNumber();
		query += "duration = " + getDuration();
		query += "bitrate = " + getBitrate();
		query += "file_size = " + getFileSize();
		query += "last_modified = " + getLastModified();
		query += "file_name = " + getFileName();

		try {
			Settings.getDbStatement().executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
