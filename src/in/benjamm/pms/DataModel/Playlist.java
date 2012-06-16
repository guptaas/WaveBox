package in.benjamm.pms.DataModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 5/9/12
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Playlist
{
    /*
     * Properties
     */

    /**
     * Unique identifier
     */
    private Integer _playlistId;
    public Integer getPlaylistId() { return _playlistId; }
    public void setPlaylistId(Integer playlistId) { _playlistId = playlistId; }

    /**
     * Name as chosen by user
     */
    private String _playlistName;
    public String getPlaylistName() { return _playlistName; }
    public void setPlaylistName(String playlistName) { _playlistName = playlistName; }

    /**
     * Number of media items in this playlist
     */
    private Integer _playlistCount;
    public Integer getPlaylistCount() { return _playlistCount; }
    public void setPlaylistCount(Integer playlistCount) { _playlistCount = playlistCount; }

    /**
         * Number of media items in this playlist
         */
    private Integer _playlistDuration;
    public Integer getPlaylistDuration() { return _playlistDuration; }
    public void setPlaylistDuration(Integer playlistDuration) { _playlistDuration = playlistDuration; }

    /**
     * Number of media items in this playlist
     */
    private String _md5Hash;
    public String getMd5Hash() { return _md5Hash; }
    public void setMd5Hash(String md5Hash) { _md5Hash = md5Hash; }

    /**
     * Number of media items in this playlist
     */
    private Long _lastUpdateTime;
    public Long getLastUpdateTime() { return _lastUpdateTime; }
    public void setLastUpdateTime(Long lastUpdateTime) { _lastUpdateTime = lastUpdateTime; }

    /*
     * Constructor(s)
     */

    // Constuctors here



    /*
     * Private methods
     */

    private void _setPropertiesFromResultSet(ResultSet rs)
    {
        try {
            _playlistId = (Integer)rs.getObject("playlist_id");
            _playlistName = rs.getString("playlist_name");
            _playlistCount = (Integer)rs.getObject("playlist_count");
            _playlistDuration = (Integer)rs.getObject("playlist_duration");
            _md5Hash = rs.getString("md5_hash");
            _lastUpdateTime = (Long)rs.getObject("last_update");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String _md5OfString(String input)
    {
        StringBuilder result = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(input.getBytes());
            byte[] md5 = algorithm.digest();
            String tmp = "";
            for (int i = 0; i < md5.length; i++)
            {
               tmp = (Integer.toHexString(0xFF & md5[i]));
               if (tmp.length() == 1)
               {
                   result.append("0").append(tmp);
               }
               else
               {
                   result.append(tmp);
               }
            }
        } catch (NoSuchAlgorithmException ex) {}
        return result.toString();
    }

    public String _calculateHash()
    {
        String hash = null;
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        try {
            // Insert into the playlist table
            String query = "SELECT * FROM playlist_item WHERE playlist_id = ?";

            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, getPlaylistId());
            r = s.executeQuery();

            StringBuilder itemIds = new StringBuilder();
            while (r.next())
            {
                itemIds.append(r.getInt("item_id")).append(",");
            }
            hash = _md5OfString(itemIds.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.close(c, s, r);
        }

        return hash;
    }

    public void _updateHash()
    {

    }

    public void _updateDatabase()
    {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        try {
            // Insert into the playlist table
            String query = "REPLACE INTO playlist ";
                  query += "(playlist_id, playlist_name, playlist_count, playlist_duration, adler_hash, last_update)";
                  query += "VALUES (?, ?, ?, ?, ?, ?)";

            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, getPlaylistId());
            s.setObject(2, getPlaylistName());
            s.setObject(3, getPlaylistCount());
            s.setObject(4, getPlaylistDuration());
            s.setObject(5, getMd5Hash());
            s.setObject(6, getLastUpdateTime());
            s.executeUpdate();

            if (getPlaylistId() == null)
            {
                // Get the playlist_id
                r = s.getGeneratedKeys();
                if (r.next())
                {
                    setPlaylistId(r.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.close(c, s, r);
        }
    }

    /*
     * Public methods
     */

    /**
     * Media item at the specified index, null if invalid index
     */
    public MediaItem mediaItemAtIndex(int index)
    {
        return null;
    }

    /**
     * List of media items in this playlist
     */
    public List<MediaItem> listOfMediaItems()
    {
        return null;
    }

    /**
     * Remove media item from playlist
     *
     * Removes the first occurrence of this item if the playlist
     * contains duplicates
     */
    public void removeMediaItem(MediaItem item)
    {

    }

    /**
         * Remove media item from playlist
         *
         * Removes the first occurrence of this item if the playlist
         * contains duplicates
         */
    public void removeMediaItems(List<MediaItem> items)
    {

    }

    /**
     * Remove media item at specified index
     */
    public void removeMediaItemAtIndex(int index)
    {

    }

    /**
         * Remove media item at specified index
         */
    public void removeMediaItemAtIndexes(List<Integer> indexes)
    {

    }

    /**
     * Move a playlist item
     */
    public void moveMediaItem(int fromIndex, int toIndex)
    {

    }
}
