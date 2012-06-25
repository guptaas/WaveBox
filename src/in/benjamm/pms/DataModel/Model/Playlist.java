package in.benjamm.pms.DataModel.Model;

import in.benjamm.pms.DataModel.Singletons.Database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
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

    public Playlist()
    {

    }

    public Playlist(ResultSet rs)
    {
        _setPropertiesFromResultSet(rs);
    }

    public Playlist(int playlistId)
    {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT * FROM playlist WHERE playlist_id = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, playlistId);
            r = s.executeQuery();
            if (r.next())
            {
                // Return the existing playlist
                _setPropertiesFromResultSet(r);
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }
    }

    public Playlist(String playlistName)
    {
        if (playlistName == null || playlistName.equals(""))
            return;

        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT * FROM playlist WHERE playlist_name = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setString(1, playlistName);
            r = s.executeQuery();
            if (r.next())
            {
                // Return the existing playlist
                _setPropertiesFromResultSet(r);
            }
            else
            {
                playlistName = playlistName;
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
            _playlistId = (Integer)rs.getObject("playlist_id");
            _playlistName = rs.getString("playlist_name");
            _playlistCount = (Integer)rs.getObject("playlist_count");
            _playlistDuration = (Integer)rs.getObject("playlist_duration");
            _md5Hash = rs.getString("md5_hash");
            _lastUpdateTime = (Long)rs.getObject("last_update");
        } catch (SQLException e) {
            log2File(ERROR, e);
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

    public synchronized String _calculateHash()
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
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }

        return hash;
    }

    /*public void _updateHash()
    {

    }*/

    public synchronized void _updateProperties(int itemsAdded, int durationAdded)
    {
        // Update the playlist database
        setPlaylistCount(getPlaylistCount() + itemsAdded);
        setLastUpdateTime(System.currentTimeMillis());
        setMd5Hash(_calculateHash());
        setPlaylistDuration(getPlaylistDuration() + durationAdded);
        _updateDatabase();
    }

    public synchronized void _updateDatabase()
    {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            // Insert into the playlist table
            String query = "MERGE INTO playlist ";
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
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }
    }

    /*
     * Public methods
     */

    public synchronized Integer indexOfMediaItem(MediaItem item)
    {
        Integer index = null;
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            // Insert into the playlist table
            String query = "SELECT item_position FROM playlist_item ";
                  query += "WHERE playlist_id = ? AND item_id = ? AND item_type_id = ? ";
                  query += "ORDER BY item_position LIMIT 1";

            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, getPlaylistId());
            s.setObject(2, item.getItemId());
            s.setObject(3, item.getItemTypeId());
            r = s.executeQuery();

            if (r.next())
            {
                index = r.getInt("item_position");
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }

        return index;
    }

    /**
     * Media item at the specified index, null if invalid index
     */
    public synchronized MediaItem mediaItemAtIndex(int index)
    {
        MediaItem item = null;
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            // Insert into the playlist table
            String query = "SELECT * FROM playlist_item WHERE playlist_id = ? AND item_position = ?";

            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, getPlaylistId());
            s.setObject(2, index);
            r = s.executeQuery();

            if (r.next())
            {
                int itemTypeId = r.getInt("item_type_id");
                int itemId = r.getInt("item_id");
                if (itemTypeId == ItemType.SONG.getItemTypeId())
                {
                    item = new Song(itemId);
                }
                else if (itemTypeId == ItemType.VIDEO.getItemTypeId())
                {
                    // Fill in later
                }
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }

        return item;
    }

    /**
     * List of media items in this playlist
     */
    public synchronized List<MediaItem> listOfMediaItems()
    {
        List<MediaItem> mediaItems = new ArrayList<MediaItem>();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            // Insert into the playlist table
            String query = "SELECT *, artist.artist_name, album.album_name FROM playlist_item ";
                  query += "LEFT JOIN song ON item_id = song_id AND item_type_id = ? ";
                  query += "LEFT JOIN artist ON song_artist_id = artist.artist_id ";
                  query += "LEFT JOIN album ON song_album_id = album.album_id ";
                  query += "LEFT JOIN video ON item_id = video_id AND item_type_id = ? ";
                  query += "WHERE playlist_id = ? ORDER BY item_position";

            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, ItemType.SONG.getItemTypeId());
            s.setObject(2, ItemType.VIDEO.getItemTypeId());
            s.setObject(3, getPlaylistId());
            r = s.executeQuery();

            while (r.next())
            {
                int itemTypeId = r.getInt("item_type_id");
                if (itemTypeId == ItemType.SONG.getItemTypeId())
                {
                    mediaItems.add(new Song(r));
                }
                else if (itemTypeId == ItemType.VIDEO.getItemTypeId())
                {
                    // Fill in later
                }
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }

        return mediaItems;
    }

    /**
     * Remove media item from playlist
     *
     * Removes the first occurrence of this item if the playlist
     * contains duplicates
     */
    public synchronized void removeMediaItem(MediaItem item)
    {
        removeMediaItemAtIndex(indexOfMediaItem(item));
    }

    /**
         * Remove media item from playlist
         *
         * Removes the first occurrence of this item if the playlist
         * contains duplicates
         */
    public synchronized void removeMediaItems(List<MediaItem> items)
    {
        if (getPlaylistId() == null || items == null)
            return;

        List<Integer> indexes = new ArrayList<Integer>();
        for (MediaItem item : items)
        {
            indexes.add(indexOfMediaItem(item));
        }

        removeMediaItemsAtIndexes(indexes);
    }

    /**
     * Remove media item at specified index
     */
    public synchronized void removeMediaItemAtIndex(int index)
    {
        if (getPlaylistId() == null)
            return;

        Connection c = null;
        PreparedStatement s = null;

        try {
            // Delete the item
            String query = "DELETE FROM playlist_item WHERE playlist_id = ? AND item_position = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, getPlaylistId());
            s.setInt(2, index);
            s.executeUpdate();
            Database.close(s);

            // Fix the track numbers
            query = "UPDATE playlist_item SET item_position = item_position - 1 ";
            query += "WHERE playlist_id = ? AND item_position > ?";
            s = c.prepareStatement(query);
            s.setObject(1, getPlaylistId());
            s.setInt(2, index);
            s.executeUpdate();

        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s);
        }
    }

    /**
     * Remove media item at specified index
     */
    public synchronized void removeMediaItemsAtIndexes(List<Integer> indexes)
    {
        if (getPlaylistId() == null || indexes == null || indexes.size() == 0)
            return;

        String query;
        Connection c = null;
        PreparedStatement s = null;

        try {
            // Get the single connection for all of these operations
            c = Database.getDbConnection();

            // Begin the transaction
            query = "BEGIN";
            s = c.prepareStatement(query);
            s.executeUpdate();
            Database.close(s);

            /*StringBuilder query = new StringBuilder("DELETE FROM playlist_item WHERE playlist_id = ? AND (");
            boolean isFirst = true;
            for (Integer index : indexes)
            {
                if (!isFirst)
                    query.append(" OR ");
                isFirst = false;

                query.append("item_position = ?");
            }
            query.append(")");

            c = Database.getDbConnection();
            s = c.prepareStatement(query.toString());
            s.setInt(1, getPlaylistId());
            int i = 2;
            for (Integer index : indexes)
            {
                s.setInt(i, index);
                i++;
            }
            s.executeUpdate();*/

            // ^^ this could be dangerous if deleting many rows, do single delete statements instead
            for (Integer index : indexes)
            {
                query = "DELETE FROM playlist_item WHERE playlist_id = ? AND item_position = ?";
                s = c.prepareStatement(query.toString());
                s.setInt(1, getPlaylistId());
                s.setInt(2, index);
                s.executeUpdate();
                Database.close(s);
            }

            // First remove temp table if exists
            query = "DROP TABLE IF EXISTS playlist_item_temp";
            s = c.prepareStatement(query);
            s.executeUpdate();
            Database.close(s);

            // Create the temp table
            query  = "CREATE TEMP TABLE playlist_item_temp ";
            query += "(item_position INTEGER AUTO_INCREMENT PRIMARY KEY, playlist_item_id INTEGER)";
            s = c.prepareStatement(query);
            s.executeUpdate();
            Database.close(s);

            // Grab the playlist items
            query  = "INSERT INTO playlist_item_temp SELECT playlist_item_id FROM playlist_item ";
            query += "WHERE playlist_id = ? ORDER BY item_position";
            s = c.prepareStatement(query);
            s.setInt(1, getPlaylistId());
            Database.close(s);

            // Update the positions
            query  = "MERGE INTO playlist_item VALUES (item_position) ";
            query += "SELECT item_position FROM playlist_item_temp ";
            query += "WHERE playlist_item.playlist_item_id = playlist_item_temp.playlist_item_id";
            s = c.prepareStatement(query);
            s.setInt(1, getPlaylistId());
            Database.close(s);

            // Complete the transaction and commit the changes
            query = "END";
            s = c.prepareStatement(query);

        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s);
        }
    }

    /**
     * Move a playlist item
     */
    public synchronized void moveMediaItem(int fromIndex, int toIndex)
    {
        // If out of bounds or no-op, then bail
        if (getPlaylistId() == null ||
            fromIndex == toIndex ||
            fromIndex < 0 || fromIndex > getPlaylistCount() ||
            toIndex < 0 || fromIndex > getPlaylistCount())
            return;

        String query;
        Connection c = null;
        PreparedStatement s = null;

        try {
            // Get the single connection for all of these operations
            c = Database.getDbConnection();

            // Begin the transaction
            query = "BEGIN";
            s = c.prepareStatement(query);
            s.executeUpdate();
            Database.close(s);

            // Update playlist positions
            query = "UPDATE playlist_item SET item_position = item_position + 1 ";
            query += "WHERE playlist_id = ? AND item_position >= ?";
            s = c.prepareStatement(query);
            s.setInt(1, getPlaylistId());
            s.setInt(2, toIndex);
            s.executeUpdate();
            Database.close(s);

            // If the fromIndex is higher than toIndex, compensate for the position update above
            fromIndex = fromIndex < toIndex ? fromIndex : fromIndex - 1;

            // Change the position of the item we're moving
            query = "UPDATE playlist_item SET item_position = ? ";
            query += "WHERE playlist_id = ? AND item_position = ?";
            s = c.prepareStatement(query);
            s.setInt(1, toIndex);
            s.setInt(2, getPlaylistId());
            s.setInt(3, fromIndex);
            s.executeUpdate();
            Database.close(s);

            // End the transaction and commit the changes
            query = "END";
            s = c.prepareStatement(query);
            s.executeUpdate();
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s);
        }
    }

    public synchronized void addMediaItem(MediaItem item, boolean updateDatabase)
    {
        if (getPlaylistId() == null || item == null)
            return;

        Connection c = null;
        PreparedStatement s = null;

        try {
            // Add the item
            String query = "INSERT INTO playlist_item VALUES (?, ?, ?, ?, ?)";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setNull(1, Types.INTEGER);
            s.setObject(2, getPlaylistId());
            s.setObject(3, item.getItemTypeId());
            s.setObject(4, item.getItemId());
            s.setObject(5, getPlaylistCount());
            s.executeUpdate();
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s);
        }

        if (updateDatabase)
        {
            // Update the playlist database
            _updateProperties(1, item.getDuration());
            _updateDatabase();
        }
    }

    public synchronized void addMediaItems(List<MediaItem> mediaItems)
    {
        if (getPlaylistId() == null || mediaItems == null)
            return;

        int duration = 0;
        for (MediaItem item : mediaItems)
        {
            addMediaItem(item, false);
            duration += item.getDuration();
        }

        // Update the playlist database
        _updateProperties(mediaItems.size(), duration);
        _updateDatabase();
    }

    public synchronized void insertMediaItem(MediaItem item, int index)
    {
        if (getPlaylistId() == null || item == null)
            return;

        String query;
        Connection c = null;
        PreparedStatement s = null;

        try {
            // Begin the transaction
            query = "BEGIN";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.executeUpdate();
            Database.close(s);

            // Update the positions of items above this item
            query  = "UPDATE playlist_item SET item_position = item_position + 1 ";
            query += "WHERE playlist_id = ? AND item_position > ?";
            s = c.prepareStatement(query);
            s.setObject(1, getPlaylistId());
            s.setInt(2, index);
            s.executeUpdate();
            Database.close(s);

            // Insert the new item
            query = "INSERT INTO playlist_item VALUES (?, ?, ?, ?, ?)";
            s = c.prepareStatement(query);
            s.setNull(1, Types.INTEGER);
            s.setObject(2, getPlaylistId());
            s.setObject(3, item.getItemTypeId());
            s.setObject(4, item.getItemId());
            s.setObject(5, index > getPlaylistCount() ? getPlaylistCount() : index);
            s.executeUpdate();
            Database.close(s);

            // Complete the transaction and commit the changes
            query = "END";
            s = c.prepareStatement(query);

        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s);
        }
    }

    public synchronized void clearPlaylist()
    {
        String query;
        Connection c = null;
        PreparedStatement s = null;

        try {
            // Delete the playlist items
            query = "DELETE FROM playlist_item WHERE playlist_id = ?";
            s = c.prepareStatement(query);
            s.setObject(1, getPlaylistId());
            s.executeUpdate();
            Database.close(s);

        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s);
        }
    }

    public synchronized void createPlaylist()
    {
        // TODO: See where we use this and add more to this method

        _updateDatabase();
    }

    public synchronized void deletePlaylist()
    {
        String query;
        Connection c = null;
        PreparedStatement s = null;

        try {
            // Begin the transaction
            query = "BEGIN";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.executeUpdate();
            Database.close(s);

            // Delete the playlist items
            query = "DELETE FROM playlist_item WHERE playlist_id = ?";
            s = c.prepareStatement(query);
            s.setObject(1, getPlaylistId());
            s.executeUpdate();
            Database.close(s);

            // Delete the playlist
            query = "DELETE FROM playlist WHERE playlist_id = ?";
            s = c.prepareStatement(query);
            s.setObject(1, getPlaylistId());
            s.executeUpdate();
            Database.close(s);

            // End the transaction
            query = "END";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.executeUpdate();
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s);
        }
    }
}
