package in.benjamm.pms.DataModel.Singletons;

import in.benjamm.pms.DataModel.FolderScanning.FolderScanQueue;
import in.benjamm.pms.DataModel.Model.Folder;
import in.benjamm.pms.DataModel.Model.MediaItem;
import in.benjamm.pms.DataModel.Model.Song;
import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;


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

    private FolderScanQueue _folderScanQueue = new FolderScanQueue();
    public FolderScanQueue getFolderScanQueue() { return _folderScanQueue; }

    /*
     * Constructor(s)
     */

    private FileManager()
    {
        System.out.println("FileManager()");

        /*// Setup JNotify
        try {
            _bootstrapJNotify();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }*/

        // Scan and watch all media folders
        for (Folder folder : Folder.mediaFolders())
        {
            System.out.println("media folder: " + folder.getFolderName());

            // Queue folder scan
            _folderScanQueue.queueFolderScan(folder, 0);

            System.out.println("queued folder: " + folder.getFolderName());

            /*// Watch this folder for changes
            try {
                addFolderWatch(folder);
            } catch (JNotifyException e) {
                e.printStackTrace();
            }*/
        }

        // Start the folder scan
        System.out.println("starting folder scan");
        _folderScanQueue.startScanQueue();
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
        _folderScanQueue.queueFolderScan(rootPath, FolderScanQueue.DEFAULT_DELAY);
    }

   	public void fileDeleted(int wd, String rootPath, String name)
    {
        _folderScanQueue.queueFolderScan(rootPath, FolderScanQueue.DEFAULT_DELAY);
    }

    public void fileModified(int wd, String rootPath, String name)
    {
        _folderScanQueue.queueFolderScan(rootPath, FolderScanQueue.DEFAULT_DELAY);
    }

   	public void fileRenamed(int wd, String rootPath, String oldName, String newName)
    {
        _folderScanQueue.queueFolderScan(rootPath, FolderScanQueue.DEFAULT_DELAY);
    }
}
