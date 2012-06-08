package in.benjamm.pms.DataModel;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    /**
     * The folder that is currently being scanned. NULL if not scanning
     */
    private String _currentScanningFolder;
    public String getCurrentScanningFolder() { return _currentScanningFolder; }
    public void setCurrentScanningFolder(String currentScanningFolder) { _currentScanningFolder = currentScanningFolder; }

    /**
     * The file that is currently being scanned. NULL if not scanning
     */
    private String _currentScanningFile;
    public String getCurrentScanningFile() { return _currentScanningFile; }
    public void setCurrentScanningFile(String currentScanningFile) { _currentScanningFile = currentScanningFile; }

    static private IJNotify _jnotifyInstance;

    private Map<String, Integer> _folderWatchIds = new HashMap<String, Integer>();
    public Map<String, Integer> getFolderWatchIds() { return _folderWatchIds; };

    private static FileManager _sharedInstance = new FileManager();
    public static FileManager sharedInstance() {return _sharedInstance;}



    /*
     * Constructor(s)
     */

    private FileManager()
    {
        String overrideClass = System.getProperty("jnotify.impl.override");
        if (overrideClass != null)
        {
            try
            {
                _jnotifyInstance = (IJNotify) Class.forName(overrideClass).newInstance();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.equals("linux"))
            {
                try
                {
                    _jnotifyInstance = (IJNotify) Class.forName("net.contentobjects.jnotify.linux.JNotifyAdapterLinux").newInstance();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            if (osName.startsWith("windows"))
            {
                try
                {
                    _jnotifyInstance = (IJNotify) Class.forName("net.contentobjects.jnotify.win32.JNotifyAdapterWin32").newInstance();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            if (osName.startsWith("mac os x"))
            {
                try
                {
                    _jnotifyInstance = (IJNotify) Class.forName("net.contentobjects.jnotify.macosx.JNotifyAdapterMacOSX").newInstance();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                throw new RuntimeException("Unsupported OS : " + osName);
            }
        }
    }



    /*
     * Private methods
     */

    // Private methods here



    /*
     * Public methods
     */

    public void fileCreated(int wd, String rootPath, String name)
    {
        this.scanFolder(rootPath);
    }

   	public void fileDeleted(int wd, String rootPath, String name)
    {
        this.scanFolder(rootPath);
    }

    public void fileModified(int wd, String rootPath, String name)
    {
        this.scanFolder(rootPath);
    }

   	public void fileRenamed(int wd, String rootPath, String oldName, String newName)
    {
        this.scanFolder(rootPath);
    }

    /**
     * Scan a folder for file changes
     */
    public void scanFolder(String folderPath)
    {
        // Need some kind of scan queue so that if a folder path receives multiple
        // notifications quickly, it will only be scanned once

        File topFile = new File(folderPath);

        if (topFile.isDirectory())
        {
            for (File subFile : topFile.listFiles())
            {
                if (subFile.isDirectory())
                {
                    // This is a folder, so scan it
                    scanFolder(subFile.getAbsolutePath());
                }
                else
                {
                    // This is a file, so scan it
                    processFile(subFile);
                }
            }
        }
    }

    public void processFolder(File folder) throws SQLException
    {
        // Get parent id
        // TODO: maybe escape out the path properly
        String parent = folder.getParent();
        String query = "SELECT parent_folder_id FROM folders WHERE folder_path = " + parent;
        Integer parentFolderId = Settings.getDbStatement().executeQuery(query).getInt(0);

        // Add to the folders table
        query = "INSERT INTO folders";
        query += " SET folder_name = " + folder.getName();
        query += ", folder_path = " + folder.getAbsolutePath();
        query += ", parent_folder_id = " + parentFolderId;
        Settings.getDbStatement().executeUpdate(query);
    }

    public void processFile(File file, int folderId) throws SQLException
    {
        /*
        song_id INTEGER PRIMARY KEY ASC AUTOINCREMENT,
        folder_id INTEGER,
        artist_id INTEGER,

        album_id INTEGER,
        file_type_id INTEGER,
        song_name TEXT,
        track_num INTEGER,
        disc_num INTEGER,
        duration INTEGER,
        bitrate INTEGER,
        file_size INTEGER,
        last_modified INTEGER,
        file_name TEXT
         */

        AudioFile f = null;
        try {
            f = AudioFileIO.read(file);
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }

        if (f != null)
        {
            // This is an audio file
            AudioHeader header = f.getAudioHeader();
            Tag tag = f.getTag();

            String query = "INSERT INTO song";
            query += "folder_id = " + folderId;
            query += "artist_id = "; // +
            query += "album_id = "; // +
            query += ""
        }
    }

    /**
     * Add a folder path for file change notifications
     */
    public void addFolderWatch(String folderPath) throws JNotifyException
    {
        int watchId = _jnotifyInstance.addWatch(folderPath, JNotify.FILE_ANY, true, this);
        getFolderWatchIds().put(folderPath, watchId);
    }

    /**
     * Remove a folder path for file change notifications
     */
    public void removeFolderWatch(String folderPath) throws JNotifyException
    {
        Integer watchId = getFolderWatchIds().get(folderPath);
        _jnotifyInstance.removeWatch(watchId);
    }
}
