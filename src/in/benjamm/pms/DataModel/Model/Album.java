package in.benjamm.pms.DataModel.Model;

import in.benjamm.pms.DataModel.Singletons.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 5/9/12
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Album
{
    /*
     * Properties
     */

    public Integer getItemTypeId() { return ItemType.ALBUM.getItemTypeId(); }

    /**
     * Artist object for this album
     */
    private Integer _artistId;
    public Integer getArtistId() { return _artistId; }
    public void setArtistId(Integer artistId) { _artistId = artistId; }

    /**
     * Unique identifier
     */
    private Integer _albumId;
    public Integer getAlbumId() { return _albumId; }
    public void setAlbumId(Integer albumId) { _albumId = albumId; }

    /**
     * Name from tags
     */
    private String _albumName;
    public String getAlbumName() { return _albumName; }
    public void setAlbumName(String albumName) { _albumName = albumName; }

    /**
     * Four digit release year from tags
     */
    private Integer _releaseYear;
    public Integer getReleaseYear() { return _releaseYear; }
    public void setReleaseYear(Integer releaseYear) { _releaseYear = releaseYear; }

    /**
     * Associated cover art
     */
    private Integer _artId;
    public Integer getArtId() { return _artId; }
    public void setArtId(Integer artId) { _artId = artId; }



    /*
     * Constructor(s)
     */

    public Album()
    {

    }

    public Album(ResultSet rs)
    {
        _setPropertiesFromResultSet(rs);
    }

    public Album(int albumId)
    {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT * FROM album LEFT JOIN item_type_art ON item_type_art.item_type_id = ? AND item_id = album_id WHERE album_id = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, getItemTypeId());
            s.setObject(2, albumId);
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

    public Album(String albumName)
    {
        if (albumName == null || albumName.equals(""))
            return;

        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT * FROM album LEFT JOIN item_type_art ON item_type_id = ? AND item_id = album_id WHERE album_name = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, getItemTypeId());
            s.setObject(2, albumName);
            r = s.executeQuery();
            if (r.next())
            {
                // Return the existing album
                //log2Out(TEST, "ALBUM " + albumName + " exists in database");
                _setPropertiesFromResultSet(r);
            }
            else
            {
                //log2Out(TEST, "ALBUM " + albumName + " does NOT exist in database");
                _albumName = albumName;
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

    private static boolean _insertAlbum(String albumName, int artistId)
    {
        //log2Out(TEST, "ALBUM: " + albumName);

        boolean success = false;
        Connection c = null;
        PreparedStatement s = null;

        try {
            String query = "INSERT INTO album (album_id, album_name, artist_id) VALUES (?, ?, ?)";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setNull(1, Types.INTEGER);
            s.setObject(2, albumName);
            s.setObject(3, Integer.valueOf(artistId));
            s.executeUpdate();
            success = true;
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, null);
        }

        return success;
    }

    private void _setPropertiesFromResultSet(ResultSet rs)
    {
        try {
            _artistId = rs.getInt("artist_id");
            _albumId = rs.getInt("album_id");
            _albumName = rs.getString("album_name");
            _artId = rs.getInt("art_id");
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

    /**
     * Use web service to tag this album's files automatically
     */
    public void autoTag()
    {}

    /**
     * Generate list of all songs in this albums
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
                  query += "LEFT JOIN artist ON song_artist_id = artist.artist_id ";
                  query += "LEFT JOIN album USING song_album_id = album.album_id ";
                  query += "WHERE song_album_id = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, new Song().getItemTypeId());
            s.setInt(2, getAlbumId());
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

    public static Album albumForName(String albumName, Integer artistId)
    {
        //log2Out(TEST, "albumForName(" + albumName + ", " + artistId + ")");

        if (albumName == null || albumName.equals("") || artistId == null)
            return new Album();

        // Check to see if this album exists
        Album anAlbum = new Album(albumName);
        //log2Out(TEST, "checking if album " + anAlbum.getAlbumName() + " has an id: " + anAlbum.getAlbumId());
        if (anAlbum.getAlbumId() == null)
        {
            //log2Out(TEST, "ALBUM " + albumName + " doesn't exist in database, so adding it");
            // This album doesn't exist in the db, so insert it
            anAlbum = null;
            if (_insertAlbum(albumName, artistId))
            {
                //log2Out(TEST, "ALBUM added " + albumName + " so grabbing id");
                anAlbum = albumForName(albumName, artistId);
            }
        }

        return anAlbum;
    }

    public static List<Album> allAlbums()
    {
        // Retrieve all the albums
        List<Album> albums = new ArrayList<Album>();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT * FROM album LEFT JOIN item_type_art ON item_type_id = ? AND item_id = album_id";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, ItemType.ALBUM.getItemTypeId());
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

        // Sort the albums alphabetically
        Collections.sort(albums, new AlbumNameComparator());

        return albums;
    }

    static class AlbumNameComparator implements Comparator<Album>
    {
        public int compare(Album album1, Album album2)
        {
            return album1.getAlbumName().compareToIgnoreCase(album2.getAlbumName());
        }
    }
}
