package in.benjamm.pms.DataModel;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.sql.*;

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

    public Integer getItemTypeId() { return 3; }

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
        _album = Album.albumForName(tag.getFirst(FieldKey.ALBUM), _artist.getArtistId());
		_fileType = FileType.fileTypeForJAudioTaggerFormatString(header.getFormat());
		_songName = tag.getFirst(FieldKey.TITLE);

        String track = tag.getFirst(FieldKey.TRACK);
        if (track != null && !track.equals(""))
        {
            try {
                if (track.contains("/"))
                {
                    track = track.split("/")[0];
                }
                _trackNumber = Integer.valueOf(track);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        String disc = tag.getFirst(FieldKey.DISC_NO);
        if (disc != null && !disc.equals(""))
        {
            try {
                _discNumber = Integer.valueOf(disc);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

		_duration = header.getTrackLength();
		_bitrate = header.getBitRateAsNumber();
		File file = audioFile.getFile();
		_fileSize = file.length();
		_lastModified = file.lastModified();
		_fileName = file.getName();

        CoverArt art = new CoverArt(audioFile);
        if (art.getArtId() != null)
        {
            _artId = art.getArtId();
        }
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
		try {
            // Insert into the song table
            String query = "INSERT INTO song ";
            query += "(song_id, folder_id, artist_id, album_id, file_type_id, song_name, track_num";
            query += ", disc_num, duration, bitrate, file_size, last_modified, file_name) ";
            query += "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            Connection c = Database.getDbConnection();
            PreparedStatement s = c.prepareStatement(query);
            s.setNull(1, Types.INTEGER);
            s.setObject(2, getFolderId());
            s.setObject(3, getArtist().getArtistId());
            s.setObject(4, getAlbum().getAlbumId());
            s.setObject(5, getFileType().fileTypeId());
            s.setObject(6, getSongName());
            s.setObject(7, getTrackNumber());
            s.setObject(8, getDiscNumber());
            s.setObject(9, getDuration());
            s.setObject(10, getBitrate());
            s.setObject(11, getFileSize());
            s.setObject(12, getLastModified());
            s.setObject(13, getFileName());
            s.executeUpdate();

            // Get the song_id
            ResultSet r = s.getGeneratedKeys();
            if (r.next())
            {
                setItemId(r.getInt(1));
            }
            r.close();
            s.close();

            // Insert the art record
            if (getArtId() != null)
            {
                query = "INSERT OR IGNORE INTO item_type_art (item_type_id, item_id, art_id) VALUES (?, ?, ?)";
                s = c.prepareStatement(query);
                s.setObject(1, getItemTypeId());
                s.setObject(2, getItemId());
                s.setObject(3, getArtId());
                s.executeUpdate();
                s.close();
            }

            c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
