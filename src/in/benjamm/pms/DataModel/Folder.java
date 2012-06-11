package in.benjamm.pms.DataModel;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
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

    public Folder getParentFolder() { return new Folder(getParentFolderId()); }

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

    Folder(int folderId)
    {
        try {
            Connection c = Database.getDbConnection();
            String query = "SELECT * FROM folder WHERE folder_id = ?";
            PreparedStatement s = c.prepareStatement(query);
            s.setObject(1, folderId);
            ResultSet r = s.executeQuery();
            if (r.next())
            {
                _folderId = r.getInt("folder_id");
                _folderName = r.getString("folder_name");
                _folderPath = r.getString("folder_path");
                _parentFolderId = r.getInt("parent_folder_id");
            }
            r.close();
            s.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	Folder(String path)
	{
        _folderPath = path;
        File folder = new File(_folderPath);
        _folderName = folder.getName();
        try {
            Connection c = Database.getDbConnection();
            String query;
            PreparedStatement s;
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

            ResultSet r = s.executeQuery();
            if (r.next())
            {
                _folderId = r.getInt("folder_id");
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
    public List<MediaItem> listOfMediaItems()
    {
        return null;
    }

    /**
     * Generate list of sub-folders contained in this folder
     */
    public List<Folder> listOfSubFolders()
    {
        return null;
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

    public void addToDatabase()
    {
        try {
            String query = "INSERT INTO folder (folder_name, folder_path, parent_folder_id) VALUES (?, ?, ?)";

            Connection c = Database.getDbConnection();
            PreparedStatement s = c.prepareStatement(query);
            s = c.prepareStatement(query);
            s.setObject(1, getFolderName());
            s.setObject(2, getFolderPath());
            s.setObject(3, getParentFolder().getFolderId());
            s.executeUpdate();
            s.close();

            query = "SELECT folder_id FROM folder WHERE parent_folder_id = ? AND folder_name = ?";
            s = c.prepareStatement(query);
            s.setObject(1, getParentFolder().getFolderId());
            s.setObject(2, getFolderName());
            ResultSet r = s.executeQuery();
            if (r.next())
            {
                setFolderId(r.getInt("folder_id"));
            }
            r.close();
            s.close();
            c.close();
        } catch (SQLException e) {

        }
    }

    public void removeFromDatabase()
    {
        try {
            String query = "DELETE FROM folder WHERE folder_id = ?";
            Connection c = Database.getDbConnection();
            PreparedStatement s = c.prepareStatement(query);
            s.setObject(1, getFolderId());
            s.executeUpdate();
            s.close();
            c.close();
        } catch (SQLException e) {

        }
    }

    public static List<Folder> mediaFolders()
    {
        List<Folder> folders = new ArrayList<Folder>();
        try {
            String query = "SELECT * FROM folder WHERE parent_folder_id IS NULL";
            Connection c = Database.getDbConnection();
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(query);
            while (r.next())
            {
                folders.add(new Folder(r.getInt("folder_id")));
            }
            r.close();
            s.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return folders;
    }
}
