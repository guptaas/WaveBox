package in.benjamm.pms.DataModel.FolderScanning;

import in.benjamm.pms.DataModel.Model.Folder;
import in.benjamm.pms.DataModel.Model.MediaItem;
import in.benjamm.pms.DataModel.Model.Song;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/17/12
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class FolderScanOperation implements Delayed, Runnable
{
    private String _folderPath;
    public String getFolderPath() { return _folderPath; }

    private long _executionTime;
    private TimeUnit _unit = TimeUnit.NANOSECONDS;

    private int _secondsDelay;

    private boolean _isRestart;

    FolderScanOperation(String folderPath, int secondsDelay)
    {
        _folderPath = folderPath;
        _secondsDelay = secondsDelay;
        resetDelay();
    }

    public void resetDelay()
    {
        _executionTime = System.nanoTime() + _unit.convert(_secondsDelay, TimeUnit.SECONDS);
    }

    public void addDelay(int secondsDelay)
    {
        _executionTime += System.nanoTime() + _unit.convert(secondsDelay, TimeUnit.SECONDS);
    }

    public long getDelay(TimeUnit timeUnit)
    {
        long delay = _executionTime - System.nanoTime();
        return timeUnit.convert(delay, _unit);
    }

    public int compareTo(Delayed otherOperation)
    {
        return (int)(getDelay(_unit) - otherOperation.getDelay(_unit));
    }

    public void run()
    {
        do
        {
            _isRestart = false;
            scanFolder(getFolderPath());
        }
        while (_isRestart);
    }

    public void restart()
    {
        System.out.println("Restarting folder scan for " + getFolderPath());
        _isRestart = true;
    }

    public int hashCode()
    {
        return getFolderPath().hashCode();
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        FolderScanOperation otherOperation = (FolderScanOperation)obj;
        return getFolderPath().equals(otherOperation.getFolderPath());
    }

    /*
     * Public methods
     */

    public void scanFolder(int folderId)
    {
        Folder folder = new Folder(folderId);
        scanFolder(folder.getFolderPath());
    }

    /**
     * Scan a folder for file changes
     */
    public void scanFolder(String folderPath)
    {
        if (_isRestart)
            return;

        File topFile = new File(folderPath);
        if (topFile.isDirectory())
        {
            // Retreive the folder object for this folder path
            Folder topFolder = new Folder(topFile.getAbsolutePath());
            System.out.println("scanning " + topFolder.getFolderName() + "  id: " + topFolder.getFolderId());

            // Start recursively scanning the subfolders and files
            // Note: These won't be in any particular order
            for (File subFile : topFile.listFiles())
            {
                if (_isRestart)
                    return;

                if (subFile.isDirectory())
                {
                    // This is a folder, so scan it
                    processFolder(subFile);
                }
                else
                {
                    // This is a file, so scan it
                    processFile(subFile, topFolder.getFolderId());
                }
            }
        }
    }

    public void processFolder(File folderFile)
    {
        if (_isRestart)
            return;

        System.out.println("processing folder " + folderFile.getName());

        Folder folder = new Folder(folderFile.getAbsolutePath());
        if (folder.getFolderId() == null)
        {
            // This folder isn't in the database, so add it
            folder.addToDatabase();
        }
        scanFolder(folderFile.getAbsolutePath());
    }

    public void processFile(File file, int folderId)
    {
        if (_isRestart)
            return;

        System.out.println("processing file " + file.getName());

        if (file.getName().endsWith(".DS_Store"))
            return;

		if (MediaItem.fileNeedsUpdating(file))
		{
            System.out.println("File needs updating");
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

			if (f == null)
            {
                // This is either another media type, a random file, or just not supported by jAudioTagger
            }
            else
			{
				// This is a song, process it
				Song song = new Song(f, folderId);
				song.updateDatabase();
			}
		}
    }

}
