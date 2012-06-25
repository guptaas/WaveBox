package in.benjamm.pms.DataModel.Model;

import in.benjamm.pms.DataModel.Singletons.Database;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;

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

    private static final int _ITEM_TYPE_ID = 3;
    public Integer getItemTypeId() { return _ITEM_TYPE_ID; }

    /**
     * Artist object for this song
     */
    private Integer _artistId;
    public Integer getArtistId() { return _artistId; }
    public void setArtistId(Integer artistId) { _artistId = artistId; }

    private String _artistName;
    public String getArtistName() { return _artistName; }
    public void setArtistName(String artistName) { _artistName = artistName; }

    /**
     * Album object for this song
     */
    private Integer _albumId;
    public Integer getAlbumId() { return _albumId; }
    public void setAlbumId(Integer albumId) { _albumId = _albumId; }

    private String _albumName;
    public String getAlbumName() { return _albumName; }
    public void setAlbumName(String albumName) { _albumName = albumName; }

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

    public Song()
    {

    }

    public Song(int songId)
    {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT song.*, item_type_art.art_id, artist.artist_name, album.album_name FROM song ";
                  query += "LEFT JOIN item_type_art ON item_type_art.item_type_id = ? AND item_id = song_id ";
                  query += "LEFT JOIN artist ON song_artist_id = artist.artist_id ";
                  query += "LEFT JOIN album ON song_album_id = album.album_id ";
                  query += "WHERE song_id = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, getItemTypeId());
            s.setObject(2, songId);
            r = s.executeQuery();
            if (r.next())
            {
                _setPropertiesFromResultSet(r);
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }
    }

    public Song(ResultSet rs)
    {
        _setPropertiesFromResultSet(rs);
    }

    public Song (AudioFile audioFile, int folderId)
	{
		// Create header and tag objects
		AudioHeader header = audioFile.getAudioHeader();
		Tag tag = audioFile.getTag();

		// Get the attributes
		_folderId = folderId;

        try {
            Artist artist = Artist.artistForName(tag.getFirst(FieldKey.ARTIST));
            _artistId = artist.getArtistId();
            _artistName = artist.getArtistName();
        } catch (NullPointerException e) {
            _artistId = null;
            _artistName = null;
        }

        try {
            Album album = Album.albumForName(tag.getFirst(FieldKey.ALBUM), _artistId);
            _albumId = album.getAlbumId();
            _albumName = album.getAlbumName();
        } catch (NullPointerException e) {
            _albumId = null;
            _albumName = null;
        }

        try {
		    _fileType = FileType.fileTypeForJAudioTaggerFormatString(header.getFormat());
        } catch (NullPointerException e) {
            _fileType = FileType.UNKNOWN;
        }

        try {
		    _songName = tag.getFirst(FieldKey.TITLE);
        } catch (NullPointerException e) {
            _songName = null;
        }

        try {
            String track = tag.getFirst(FieldKey.TRACK);
            if (track != null)
            {
                try {
                    if (track.contains("/"))
                    {
                        track = track.split("/")[0];
                    }
                    _trackNumber = Integer.valueOf(track);
                } catch (NumberFormatException e) {
                    _trackNumber = null;
                    //log2File(ERROR, e);
                }
            }
        } catch (NullPointerException e) {
            _trackNumber = null;
        }

        try {
            String disc = tag.getFirst(FieldKey.DISC_NO);
            if (disc != null)
            {
                try {
                    _discNumber = Integer.valueOf(disc);
                } catch (NumberFormatException e) {
                    _discNumber = null;
                }
            }
        } catch (NullPointerException e) {
            _discNumber = null;
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

    private void _setPropertiesFromResultSet(ResultSet rs)
    {
        try {
            _itemId = (Integer)rs.getObject("song_id");
            _folderId = (Integer)rs.getObject("song_folder_id");
            _artistId = (Integer)rs.getObject("song_artist_id");
            _artistName = rs.getString("artist_name");
            _albumId = (Integer)rs.getObject("song_album_id");
            _albumName = rs.getString("album_name");
            _fileType = FileType.fileTypeForId(rs.getInt("song_file_type_id"));
            _songName = rs.getString("song_name");
            _trackNumber = (Integer)rs.getObject("song_track_num");
            _discNumber = (Integer)rs.getObject("song_disc_num");
            _duration = (Integer)rs.getObject("song_duration");
            _bitrate = rs.getLong("song_bitrate");
            _fileSize = rs.getLong("song_file_size");
            _lastModified = rs.getLong("song_last_modified");
            _fileName = rs.getString("song_file_name");
            _artId = (Integer)rs.getObject("art_id");
        } catch (SQLException e) {
            log2File(ERROR, e);
        }
    }



    /*
     * Public methods
     */

    public Artist artist()
    {
        return new Artist(getArtistId());
    }

    public Album album()
    {
        return new Album(getAlbumId());
    }

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
        String query;
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            c = Database.getDbConnection();

            // Insert into the song table
            query = "MERGE INTO song VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            s = c.prepareStatement(query);
            //s.setNull(1, Types.INTEGER);
            s.setObject(1, getItemId());
            s.setObject(2, getFolderId());
            s.setObject(3, getArtistId());
            s.setObject(4, getAlbumId());
            s.setObject(5, getFileType().getFileTypeId());
            s.setObject(6, getSongName());
            s.setObject(7, getTrackNumber());
            s.setObject(8, getDiscNumber());
            s.setObject(9, getDuration());
            s.setObject(10, getBitrate());
            s.setObject(11, getFileSize());
            s.setObject(12, getLastModified());
            s.setObject(13, getFileName());
            s.setObject(14, getReleaseYear());
            s.executeUpdate();

            if (getItemId() == null)
            {
                // Get the song_id
                r = s.getGeneratedKeys();
                if (r.next())
                {
                    setItemId(r.getInt(1));
                }
            }

            Database.close(null, s, r);

            // Insert the art record
            if (getArtId() != null)
            {
                query = "MERGE INTO item_type_art VALUES (?, ?, ?, ?)";
                s = c.prepareStatement(query);
                s.setNull(1, Types.INTEGER);
                s.setObject(2, getItemTypeId());
                s.setObject(3, getItemId());
                s.setObject(4, getArtId());
                s.executeUpdate();
                s.close();
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }
	}

    public static List<Song> allSongs()
    {
        // Retrieve all the songs
        List<Song> songs = new ArrayList<Song>();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT song.*, item_type_art.art_id, artist.artist_name, album.album_name FROM song ";
                  query += "LEFT JOIN item_type_art ON item_type_art.item_type_id = ? AND item_id = song_id ";
                  query += "LEFT JOIN artist ON song_artist_id = artist.artist_id ";
                  query += "LEFT JOIN album ON song_album_id = album.album_id";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, _ITEM_TYPE_ID);
            r = s.executeQuery();
            while(r.next())
            {
                songs.add(new Song(r));
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }

        // Sort the songs alphabetically
        Collections.sort(songs, new SongNameComparator());

        return songs;
    }

    public static List<Song> randomSongs(int count)
    {
        // Retrieve random songs
        List<Song> songs = new ArrayList<Song>();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT song.*, item_type_art.art_id, artist.artist_name, album.album_name FROM song ";
                  query += "LEFT JOIN item_type_art ON item_type_art.item_type_id = ? AND item_id = song_id ";
                  query += "LEFT JOIN artist ON song_artist_id = artist.artist_id ";
                  query += "LEFT JOIN album ON song_album_id = album.album_id ";
                  query += "ORDER BY RANDOM() LIMIT ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, _ITEM_TYPE_ID);
            s.setInt(2, count);
            r = s.executeQuery();
            while(r.next())
            {
                songs.add(new Song(r));
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }

        return songs;
    }

    static class SongNameComparator implements Comparator<Song>
    {
        public int compare(Song song1, Song song2)
        {
            return song1.getSongName().compareToIgnoreCase(song2.getSongName());
        }
    }

    static class SongOrderComparator implements Comparator<Song>
    {
        public int compare(Song song1, Song song2)
        {
            if (song1.getDiscNumber() != null && song2.getDiscNumber() != null)
            {
                return song1.getDiscNumber() - song2.getDiscNumber();
            }
            else if (song1.getTrackNumber() != null && song2.getTrackNumber() != null)
            {
                return song1.getTrackNumber() - song2.getTrackNumber();
            }
            else
            {
                // Compare by name
                return new SongNameComparator().compare(song1, song2);
            }
        }
    }
}
