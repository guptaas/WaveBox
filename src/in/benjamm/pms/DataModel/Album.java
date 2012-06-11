package in.benjamm.pms.DataModel;

import java.sql.*;
import java.util.List;

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

    public Integer getItemTypeId() { return 2; }

    /**
     * Artist object for this album
     */
    private Artist _artist;
    public Artist getArtist() { return _artist; }
    public void setArtist(Artist artist) { _artist = artist; }

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

    Album()
    {

    }

    Album(int albumId)
    {
        try {
            String query = "SELECT * FROM album LEFT JOIN item_type_art ON item_type_art.item_type_id = 2, item_id = album_id WHERE album_id = ?";
            Connection c = Database.getDbConnection();
            PreparedStatement s = c.prepareStatement(query);
            s.setObject(1, albumId);
            ResultSet r = s.executeQuery();
            if (r.next())
            {
                // Return the existing artist
                _setPropertiesFromResultSet(r);
            }
            r.close();
            s.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    Album(String albumName)
    {
        if (albumName == null || albumName.equals(""))
            return;

        try {
            String query = "SELECT * FROM album LEFT JOIN item_type_art ON item_type_id = " + getItemTypeId() + " AND item_id = album_id WHERE album_name = ?";
            Connection c = Database.getDbConnection();
            PreparedStatement s = c.prepareStatement(query);
            s.setObject(1, albumName);
            ResultSet r = s.executeQuery();
            if (r.next())
            {
                // Return the existing album
                System.out.println("ALBUM " + albumName + " exists in database");
                _setPropertiesFromResultSet(r);
            }
            else
            {
                System.out.println("ALBUM " + albumName + " does NOT exist in database");
                _albumName = albumName;
            }
            r.close();
            s.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /*
     * Private methods
     */

    private static boolean _insertAlbum(String albumName, int artistId)
    {
        System.out.println("ALBUM: " + albumName);

        boolean success = false;
        try {
            String query = "INSERT INTO album (album_id, album_name, artist_id) VALUES (?, ?, ?)";
            Connection c = Database.getDbConnection();
            PreparedStatement s = c.prepareStatement(query);
            s.setNull(1, Types.INTEGER);
            s.setObject(2, albumName);
            s.setObject(3, Integer.valueOf(artistId));
            s.executeUpdate();
            success = true;
            s.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }

    private void _setPropertiesFromResultSet(ResultSet rs)
    {
        try {
            _albumId = rs.getInt("album_id");
            _albumName = rs.getString("album_name");
            _artId = rs.getInt("art_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /*
     * Public methods
     */

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
        return null;
    }

    public static Album albumForName(String albumName, Integer artistId)
    {
        System.out.println("albumForName(" + albumName + ", " + artistId + ")");

        if (albumName == null || albumName.equals("") || artistId == null)
            return new Album();

        // Check to see if this album exists
        Album anAlbum = new Album(albumName);
        System.out.println("checking if album " + anAlbum.getAlbumName() + " has an id: " + anAlbum.getAlbumId());
        if (anAlbum.getAlbumId() == null)
        {
            System.out.println("ALBUM " + albumName + " doesn't exist in database, so adding it");
            // This album doesn't exist in the db, so insert it
            anAlbum = null;
            if (_insertAlbum(albumName, artistId))
            {
                System.out.println("ALBUM added " + albumName + " so grabbing id");
                anAlbum = albumForName(albumName, artistId);
            }
        }

        return anAlbum;
    }
}
