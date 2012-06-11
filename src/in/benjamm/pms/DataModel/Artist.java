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
public class Artist
{
    /*
     * Properties
     */

    public Integer getItemTypeId() { return 1; }

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

    Artist()
    {

    }

    Artist(int artistId)
    {
        try {
            String query = "SELECT * FROM artist LEFT JOIN item_type_art ON item_type_art.item_type_id = " + getItemTypeId() + ", item_id = artist_id WHERE artist_id = ?";
            Connection c = Database.getDbConnection();
            PreparedStatement s = c.prepareStatement(query);
            s.setObject(1, artistId);
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

    Artist(String artistName)
    {
        if (artistName == null || artistName.equals(""))
            return;

        try {
            String query = "SELECT * FROM artist LEFT JOIN item_type_art ON item_type_id = 1 AND item_id = artist_id WHERE artist_name = ?";
            Connection c = Database.getDbConnection();
            PreparedStatement s = c.prepareStatement(query);
            s.setObject(1, artistName);
            ResultSet r = s.executeQuery();
            if (r.next())
            {
                // Return the existing artist
                _setPropertiesFromResultSet(r);
            }
            else
            {
                _artistName = artistName;
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

    private void _setPropertiesFromResultSet(ResultSet rs)
    {
        try {
            _artistId = rs.getInt("artist_id");
            _artistName = rs.getString("artist_name");
            _artId = rs.getInt("art_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean _insertArtist(String artistName)
    {
        //System.out.println("ARTIST: " + artistName);

        boolean success = false;
        try {
            String query = "INSERT INTO artist (artist_id, artist_name) VALUES (?, ?)";
            Connection c = Database.getDbConnection();
            PreparedStatement s = c.prepareStatement(query);
            s.setNull(1, Types.INTEGER);
            s.setObject(2, artistName);
            s.executeUpdate();
            success = true;
            s.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
        return null;
    }

    /**
     * Generate list of all songs in this artist's albums
     */
    public List<Song> listOfSongs()
    {
        return null;
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
}
