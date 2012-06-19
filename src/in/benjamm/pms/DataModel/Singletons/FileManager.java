package in.benjamm.pms.DataModel.Singletons;

import in.benjamm.pms.DataModel.FolderScanning.ScanQueue;
import in.benjamm.pms.DataModel.Model.Folder;
import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/7/12
 * Time: 7:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileManager implements JNotifyListener
{
    /*
     * Properties
     */

    static private IJNotify _jnotifyInstance;

    private Map<String, Integer> _folderWatchIds = new HashMap<String, Integer>();
    public Map<String, Integer> getFolderWatchIds() { return _folderWatchIds; };

    private static FileManager _sharedInstance = new FileManager();
    public static FileManager sharedInstance() {return _sharedInstance;}

    private ScanQueue _folderScanQueue = new ScanQueue();
    public ScanQueue getFolderScanQueue() { return _folderScanQueue; }

    /*
     * Constructor(s)
     */

    private FileManager()
    {
        // Setup JNotify
        try {
            _bootstrapJNotify();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        // Scan for orphaned files in the database
        _folderScanQueue.queueOrphanScan(0);

        // Scan and watch all media folders
        for (Folder folder : Folder.mediaFolders())
        {
            // Queue folder scan
            _folderScanQueue.queueFolderScan(folder, 0);
        }

        //-------- Remove this later -------------
        System.out.println("Press any key to scan files");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String userName = null;
        try {
            userName = br.readLine();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        //----------------------------------------

        // Start the folder scan
        System.out.println("starting folder scan");
        _folderScanQueue.startScanQueue();

        // Watch media folders for changes
        // Do this here instead of in the loop above because it can take a while for many subfolders
        // so no need to hold off the folder scan
        //for (Folder folder : Folder.mediaFolders())
        for (Folder folder : Settings.getMediaFolders())
        {
            try {
                addFolderWatch(folder);
            } catch (JNotifyException e) {
                e.printStackTrace();
            }
        }
    }



    /*
     * Private methods
     */

    // Private methods here



    /*
     * JNotify
     */

    private void _bootstrapJNotify() throws RuntimeException
    {
/*        System.out.println("java.library.path: " + System.getProperty("java.library.path", "unset"));
        System.setProperty("java.library.path", ".");
        System.out.println("java.library.path: " + System.getProperty("java.library.path", "unset"));*/

        String overrideClass = System.getProperty("jnotify.impl.override");
        if (overrideClass != null)
        {
            try {
                _jnotifyInstance = (IJNotify) Class.forName(overrideClass).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else
        {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.equals("linux"))
            {
                try {
                    _jnotifyInstance = (IJNotify) Class.forName("net.contentobjects.jnotify.linux.JNotifyAdapterLinux").newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (osName.startsWith("windows"))
            {
                try {
                    _jnotifyInstance = (IJNotify) Class.forName("net.contentobjects.jnotify.win32.JNotifyAdapterWin32").newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (osName.startsWith("mac os x"))
            {
                try {
                    _jnotifyInstance = (IJNotify) Class.forName("net.contentobjects.jnotify.macosx.JNotifyAdapterMacOSX").newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                throw new RuntimeException("Unsupported OS : " + osName);
            }
        }
    }

    public void addFolderWatch(String folderPath) throws JNotifyException
    {
        int watchId = _jnotifyInstance.addWatch(folderPath, JNotify.FILE_ANY, true, this);
        getFolderWatchIds().put(folderPath, watchId);
    }

    /**
     * Add a folder path for file change notifications
     */
    public void addFolderWatch(Folder folder) throws JNotifyException
    {
        addFolderWatch(folder.getFolderPath());
    }

    public void removeFolderWatch(String folderPath) throws JNotifyException
    {
        Integer watchId = getFolderWatchIds().get(folderPath);
        _jnotifyInstance.removeWatch(watchId);
    }

    /**
     * Remove a folder path for file change notifications
     */
    public void removeFolderWatch(Folder folder) throws JNotifyException
    {
        removeFolderWatch(folder.getFolderPath());
    }

    /* JNotify delegate */

    public void fileCreated(int wd, String rootPath, String name)
    {
        File file = new File(rootPath + File.separator + name);
        System.out.println("fileCreated - path: " + file.getParent());
        _folderScanQueue.queueFolderScan(file.getParent(), ScanQueue.DEFAULT_DELAY);
    }

   	public void fileDeleted(int wd, String rootPath, String name)
    {
        File file = new File(rootPath + File.separator + name);
        System.out.println("fileCreated - path: " + file.getParent());
        _folderScanQueue.queueFolderScan(file.getParent(), ScanQueue.DEFAULT_DELAY);
    }

    public void fileModified(int wd, String rootPath, String name)
    {
        File file = new File(rootPath + File.separator + name);
        System.out.println("fileCreated - path: " + file.getParent());
        _folderScanQueue.queueFolderScan(file.getParent(), ScanQueue.DEFAULT_DELAY);
    }

   	public void fileRenamed(int wd, String rootPath, String oldName, String newName)
    {
        File oldFile = new File(rootPath + File.separator + oldName);
        File newFile = new File(rootPath + File.separator + newName);
        System.out.println("fileCreated - oldPath: " + oldFile.getParent() + " newPath: " + newFile.getParent());
        _folderScanQueue.queueFolderScan(oldFile.getParent(), ScanQueue.DEFAULT_DELAY);
        _folderScanQueue.queueFolderScan(newFile.getParent(), ScanQueue.DEFAULT_DELAY);
    }
}
