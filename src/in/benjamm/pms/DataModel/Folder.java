package in.benjamm.pms.DataModel;

import java.io.File;
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
    private int _folderId;
    public int getFolderId() {  return _folderId; }
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
    private Folder _parentFolder;
    public Folder getParentFolder() { return _parentFolder; }
    public void setParentFolder(Folder parentFolder) { _parentFolder = parentFolder; }

    /**
     * The absolute path of the folder
     */
    private String _folderPath;
    public String getFolderPath() { return _folderPath; }
    public void setFolderPath(Folder folderPath) { _folderPath = folderPath; }

    /*
     * Constructor(s)
     */

	Folder(String path)
	{

	}
    // Constuctors here



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
    public void rescan()
    {}

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

    public String mediaFolder()
    {
        for (String mediaFolder : Settings.mediaFolders())
        {
            if (getFolderPath().startsWith(mediaFolder))
                return mediaFolder;
        }
    }
}
