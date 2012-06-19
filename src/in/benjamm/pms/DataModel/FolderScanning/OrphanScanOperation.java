package in.benjamm.pms.DataModel.FolderScanning;

import in.benjamm.pms.DataModel.Model.Song;
import in.benjamm.pms.DataModel.Singletons.Database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/18/12
 * Time: 5:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrphanScanOperation extends ScanOperation
{
    public OrphanScanOperation(int secondsDelay)
    {
        super(secondsDelay);
    }

    public void start()
    {
        checkFolders();
        checkSongs();
    }

    public void checkFolders()
    {
        if (isRestart())
            return;

        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        List<Integer> folderIds = new ArrayList<Integer>();
        try {
            // Get all orphaned folders
            String query = "SELECT * FROM folder";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            r = s.executeQuery();
            while (r.next())
            {
                if (isRestart())
                    return;

                String path = r.getString("folder_path");
                int folderId = r.getInt("folder_id");

                File folderFile = new File(path);
                if (!folderFile.exists())
                {
                    System.out.println("Folder " + folderId + " is orphaned");
                    folderIds.add(folderId);
                }
            }

            // Delete all orphaned folders, and their associated files
            for (Integer folderId : folderIds)
            {
                if (isRestart())
                    return;

                Connection c1 = null;
                PreparedStatement s1 = null;
                ResultSet r1 = null;
                try {
                    // Delete folder
                    System.out.println("Folder " + folderId + " deleted");
                    query = "DELETE FROM folder WHERE folder_Id = ?";
                    c1 = Database.getDbConnection();
                    s1 = c1.prepareStatement(query);
                    s1.setInt(1, folderId);
                    s1.executeUpdate();

                    // Delete songs with this folder id
                    Connection c2 = null;
                    PreparedStatement s2 = null;
                    ResultSet r2 = null;
                    try {
                        System.out.println("Songs for folder " + folderId + " deleted");
                        query = "DELETE FROM song WHERE song_folder_Id = ?";
                        c2 = Database.getDbConnection();
                        s2 = c2.prepareStatement(query);
                        s2.setInt(1, folderId);
                        s2.executeUpdate();
                    } catch (SQLException e) {
                        //System.out.println("TABLE LOCKED, RETRYING QUERY");
                        e.printStackTrace();
                        //retry = true;
                    } finally {
                        Database.close(c2, s2, r2);
                    }

                } catch (SQLException e) {
                    //System.out.println("TABLE LOCKED, RETRYING QUERY");
                    e.printStackTrace();
                    //retry = true;
                } finally {
                    Database.close(c1, s1, r1);
                }
            }

        } catch (SQLException e) {
            //System.out.println("TABLE LOCKED, RETRYING QUERY");
            e.printStackTrace();
            //retry = true;
        } finally {
            Database.close(c, s, r);
        }
    }

    public void checkSongs()
    {
        if (isRestart())
            return;

        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        List<Integer> songIds = new ArrayList<Integer>();
        try {
            // Get all orphaned folders
            String query = "SELECT * FROM song";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            r = s.executeQuery();
            while (r.next())
            {
                if (isRestart())
                    return;

                int songId = r.getInt("song_id");
                Song song = new Song(songId);
                if (!song.songFile().exists())
                {
                    System.out.println("Song " + songId + " is orphaned");
                    songIds.add(songId);
                }
            }
        } catch (SQLException e) {
            //System.out.println("TABLE LOCKED, RETRYING QUERY");
            e.printStackTrace();
            //retry = true;
        } finally {
            Database.close(c, s, r);
        }

        // Delete all orphaned songs
        for (Integer songId : songIds)
        {
            if (isRestart())
                return;

            Connection c1 = null;
            PreparedStatement s1 = null;
            ResultSet r1 = null;
            try {
                // Delete song
                System.out.println("Song " + songId + " deleted");
                String query = "DELETE FROM song WHERE song_id = ?";
                c1 = Database.getDbConnection();
                s1 = c1.prepareStatement(query);
                s1.setInt(1, songId);
                s1.executeUpdate();

            } catch (SQLException e) {
                //System.out.println("TABLE LOCKED, RETRYING QUERY");
                e.printStackTrace();
                //retry = true;
            } finally {
                Database.close(c1, s1, r1);
            }
        }
    }

    public int hashCode()
    {
        // All OrphanScanOperations are equal
        return 0;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        // All OrphanScanOperations are equal
        return true;
    }
}
