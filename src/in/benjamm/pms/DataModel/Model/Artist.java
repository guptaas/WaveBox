package in.benjamm.pms.DataModel.Model;

import in.benjamm.pms.DataModel.Singletons.Database;

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
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Artist
{
    /*
     * Properties
     */

    public Integer getItemTypeId() { return ItemType.ARTIST.getItemTypeId(); }

    /**
     * Unique identifier
     */
    private Integer _artistId;
    public Integer getArtistId() { return _artistId; }
    public void setArtistId(Integer artistId) { _artistId = artistId; }

    /**
     * Name from tags
     */
    private String _artistName;
    public String getArtistName() { return _artistName; }
    public void setArtistName(String artistName) { _artistName = artistName; }

    /**
     * Associated cover art
     */
    private Integer _artId;
    public Integer getArtId() { return _artId; }
    public void setArtId(Integer artId) { _artId = artId; }



    /*
     * Constructor(s)
     */

    public Artist()
    {

    }

    public Artist(ResultSet rs)
    {
        _setPropertiesFromResultSet(rs);
    }

    public Artist(int artistId)
    {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT * FROM artist LEFT JOIN item_type_art ON item_type_art.item_type_id = ? AND item_id = artist_id WHERE artist_id = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, getItemTypeId());
            s.setObject(2, artistId);
            r = s.executeQuery();
            if (r.next())
            {
                // Return the existing artist
                _setPropertiesFromResultSet(r);
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }
    }

    public Artist(String artistName)
    {
        if (artistName == null || artistName.equals(""))
            return;

        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT * FROM artist LEFT JOIN item_type_art ON item_type_id = ? AND item_id = artist_id WHERE artist_name = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, getItemTypeId());
            s.setObject(2, artistName);
            r = s.executeQuery();
            if (r.next())
            {
                // Return the existing artist
                _setPropertiesFromResultSet(r);
            }
            else
            {
                _artistName = artistName;
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }
    }


    /*
     * Private methods
     */

    private void _setPropertiesFromResultSet(ResultSet rs)
    {
        try {
            _artistId = rs.getInt("artist_id");
            _artistName = rs.getString("artist_name");
            _artId = rs.getInt("art_id");
        } catch (SQLException e) {
            log2File(ERROR, e);
        }
    }

    private static boolean _insertArtist(String artistName)
    {
        //log2Out(TEST, "ARTIST: " + artistName);

        boolean success = false;
        Connection c = null;
        PreparedStatement s = null;

        try {
            String query = "INSERT INTO artist (artist_id, artist_name) VALUES (?, ?)";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setNull(1, Types.INTEGER);
            s.setObject(2, artistName);
            s.executeUpdate();
            success = true;
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s);
        }

        return success;
    }



    /*
     * Public methods
     */

    /**
     * Generate list of albums for this artist
     */
    public List<Album> listOfAlbums()
    {
        // Retrieve the albums for this artist
        List<Album> albums = new ArrayList<Album>();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT * FROM album LEFT JOIN item_type_art ON item_type_id = ? AND item_id = album_id WHERE artist_id = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, new Album().getItemTypeId());
            s.setInt(2, getArtistId());
            r = s.executeQuery();
            while(r.next())
            {
                albums.add(new Album(r));
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }

        // Sort the albums by name
        Collections.sort(albums, new Album.AlbumNameComparator());

        return albums;
    }

    /**
     * Generate list of all songs in this artist's albums
     */
    public List<Song> listOfSongs()
    {
        // Retrieve all the songs
        List<Song> songs = new ArrayList<Song>();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT song.*, artist.artist_name, album.album_name FROM song ";
                  query += "LEFT JOIN item_type_art ON item_type_art.item_type_id = ? AND item_id = song_id ";
                  query += "LEFT JOIN artist ON song_artist_id = artist_id ";
                  query += "LEFT JOIN album ON song_album_id = album_id ";
                  query += "WHERE song_artist_id = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, new Song().getItemTypeId());
            s.setInt(2, getArtistId());
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

        // Sort the songs by disc number, track number
        Collections.sort(songs, new Song.SongOrderComparator());

        return songs;
    }

	public static Artist artistForName(String artistName)
	{
        if (artistName == null || artistName.equals(""))
            return new Artist();

        // Check to see if this artist exists
        Artist anArtist = new Artist(artistName);
        if (anArtist.getArtistId() == null)
        {
            // This artist doesn't exist in the db, so insert it
            anArtist = null;
            if (_insertArtist(artistName))
            {
                anArtist = artistForName(artistName);
            }
        }

        return anArtist;
	}

    public static List<Artist> allArtists()
    {
        // Retrieve all the artists
        List<Artist> artists = new ArrayList<Artist>();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT * FROM artist LEFT JOIN item_type_art ON item_type_id = ? AND item_id = artist_id";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, ItemType.ARTIST.getItemTypeId());
            r = s.executeQuery();
            while(r.next())
            {
                artists.add(new Artist(r));
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }

        // Sort the folders alphabetically
        Collections.sort(artists, new ArtistNameComparator());

        return artists;
    }

    static class ArtistNameComparator implements Comparator<Artist>
    {
        public int compare(Artist artist1, Artist artist2)
        {
            return artist1.getArtistName().compareToIgnoreCase(artist2.getArtistName());
        }
    }
}
