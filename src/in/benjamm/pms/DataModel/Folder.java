package in.benjamm.pms.DataModel;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 5/9/12
 * Time: 12:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class Folder
{
    /*
     * Properties
     */

    /**
     * Unique identifier
     */
    private Integer _folderId;
    public Integer getFolderId() {  return _folderId; }
    public void setFolderId(int folderId) { _folderId = folderId; }

    /**
     * Name on disk
     */
    private String _folderName;
    public String getFolderName() { return _folderName; }
    public void setFolderName(String folderName) { _folderName = folderName; }

    /**
     * Folder this folder is inside, null if top level folder
     */
    private Integer _parentFolderId;
    public Integer getParentFolderId() { return _parentFolderId; }
    public void setParentFolderId(Integer parentFolderId) { _parentFolderId = parentFolderId; }

    /**
     * The absolute path of the folder
     */
    private String _folderPath;
    public String getFolderPath() { return _folderPath; }
    public void setFolderPath(String folderPath) { _folderPath = folderPath; }

    /*
     * Constructor(s)
     */

    /*Folder(File file)
    {
        if (!file.isDirectory())
            return;

        _folderName = file.getName();


    }*/

    public Folder(int folderId)
    {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        try {
            c = Database.getDbConnection();
            String query = "SELECT * FROM folder WHERE folder_id = ?";
            s = c.prepareStatement(query);
            s.setObject(1, folderId);
            r = s.executeQuery();
            if (r.next())
            {
                _folderId = r.getInt("folder_id");
                _folderName = r.getString("folder_name");
                _folderPath = r.getString("folder_path");
                _parentFolderId = r.getInt("parent_folder_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.close(c, s, r);
        }
    }

    public Folder(String path)
	{
        _folderPath = path;
        File folder = new File(_folderPath);
        _folderName = folder.getName();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        try {
            c = Database.getDbConnection();
            String query = null;
            if (isMediaFolder())
            {
                query = "SELECT folder_id FROM folder WHERE folder_name = ? AND parent_folder_id IS NULL";
                s = c.prepareStatement(query);
                s.setObject(1, _folderName);
            }
            else
            {
                _parentFolderId = new Folder(folder.getParent()).getFolderId();
                query = "SELECT folder_id FROM folder WHERE folder_name = ? AND parent_folder_id = ?";
                s = c.prepareStatement(query);
                s.setObject(1, _folderName);
                s.setObject(2, _parentFolderId);
            }

            r = s.executeQuery();
            if (r.next())
            {
                _folderId = r.getInt("folder_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.close(c, s, r);
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
     * Scan the contents of this folder
     */
    public void scan()
    {

    }

    /**
     * Generate list of media items contained in this folder
     */
    // TODO: this needs to be MediaItems not Songs
    public List<Song> listOfSongs()
    {
        List<Song> songs = new ArrayList<Song>();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        try {
            String query = "SELECT * FROM song WHERE folder_id = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, getFolderId());
            r = s.executeQuery();
            while (r.next())
            {
                songs.add(new Song(r));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.close(c, s, r);
        }

        return songs;
    }

    /**
     * Generate list of sub-folders contained in this folder
     */
    public List<Folder> listOfSubFolders()
    {
        List<Folder> folders = new ArrayList<Folder>();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        try {
            String query = "SELECT * FROM folder WHERE parent_folder_id = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setInt(1, getFolderId());
            r = s.executeQuery();
            while (r.next())
            {
                folders.add(new Folder(r.getInt("folder_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.close(c, s, r);
        }

        return folders;
    }

    /**
     * Check if this is a top level media folder or not
     */
    public boolean isMediaFolder()
    {
        return getFolderPath().equals(mediaFolder().getFolderPath());
    }

    /**
     * Get the top level media folder that this folder resides in.
     * If this is a media folder, this will return the same as getFolderPath()
     */
    public Folder mediaFolder()
    {
        for (Folder mediaFolder : Folder.mediaFolders())
        {
            if (getFolderPath().startsWith(mediaFolder.getFolderPath()))
                return mediaFolder;
        }
        return null;
    }

    public Folder parentFolder() { return new Folder(getParentFolderId()); }


    public void addToDatabase()
    {
        Connection c = null;
        PreparedStatement s = null;
        try {
            c = Database.getDbConnection();
            String query = "INSERT INTO folder (folder_name, folder_path, parent_folder_id) VALUES (?, ?, ?)";
            s = c.prepareStatement(query);
            s.setObject(1, getFolderName());
            s.setObject(2, getFolderPath());
            s.setObject(3, getParentFolderId());
            s.executeUpdate();

            PreparedStatement s1 = null;
            ResultSet r = null;
            try {
                query = "SELECT folder_id FROM folder WHERE parent_folder_id = ? AND folder_name = ?";
                s1 = c.prepareStatement(query);
                s1.setObject(1, getParentFolderId());
                s1.setObject(2, getFolderName());
                r = s1.executeQuery();
                if (r.next())
                {
                    setFolderId(r.getInt("folder_id"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Database.close(null, s1, r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.close(c, s, null);
        }
    }

    public void removeFromDatabase()
    {
        Connection c = null;
        PreparedStatement s = null;
        try {
            String query = "DELETE FROM folder WHERE folder_id = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, getFolderId());
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.close(c, s, null);
        }
    }

    public static List<Folder> mediaFolders()
    {
        List<Folder> folders = new ArrayList<Folder>();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        try {
            String query = "SELECT * FROM folder WHERE parent_folder_id IS NULL";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            r = s.executeQuery();
            while (r.next())
            {
                folders.add(new Folder(r.getInt("folder_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.close(c, s, r);
        }

        return folders;
    }

    // Rename this method
    public static List<Folder> topLevelFolders()
    {
        // Retrieve all the folders
        List<Folder> folders = new ArrayList<Folder>();
        for (Folder mediaFolder : mediaFolders())
        {
            folders.addAll(mediaFolder.listOfSubFolders());
        }

        // Sort the folders alphabetically
        Collections.sort(folders, new FolderNameComparator());

        return folders;
    }

    static class FolderNameComparator implements Comparator<Folder>
    {
        public int compare(Folder folder1, Folder folder2)
        {
            return folder1.getFolderName().compareToIgnoreCase(folder2.getFolderName());
        }
    }
}
