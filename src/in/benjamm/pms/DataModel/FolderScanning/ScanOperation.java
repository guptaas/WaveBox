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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/17/12
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScanOperation implements Delayed, Runnable
{
    private String _folderPath;
    public String getFolderPath() { return _folderPath; }

    private long _executionTime;
    private TimeUnit _unit = TimeUnit.NANOSECONDS;

    private int _secondsDelay;

    private boolean _isRestart;
    public synchronized boolean isRestart() { return _isRestart; }
    public synchronized void setIsRestart(boolean isRestart) { _isRestart = isRestart; }

    private String[] _validExtensions = {"mp3", "aac", "m4a", "mp4", "flac", "wv", "mpc", "ogg"};
    private List<String> _validExtensionsList = Arrays.asList(_validExtensions);

    /////////////////////////////////////// Database Performance Tests /////////////////////////////////////////////////
    // SQLITE-JDBC /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Just use 2 threads (actually 3 because using CallerRunsPolicy so main thread is used as well)                  //
    // using more is actually slower, assuming because of db locking (tho should all be read locks, so not sure)      //
    // Tests re-scanning 30K songs: single threaded - 977 seconds, 1 - 605, 2 - 455, 3 - 477, 4 - 793                 //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // H2-DATABASE//////////////////////////////////////////////////////////////////////////////////////////////////////
    // H2 DB with proper index for song update checking:                                                              //
    // Fresh scan 30K songs: single t'd - 325 seconds, 2 - 288, 4 - 266/255, 8 - 247, 16 - 241                        //
    // Rescan 30K songs: single threaded - 6 seconds, 2 - 5, 4 - 5, 8 - 5                                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //private int _processors = Runtime.getRuntime().availableProcessors();
    //private int _numThreads = _processors * 2;
    private int _numThreads = 4; // 4 threads is optimal for H2 database
    private BlockingQueue<Runnable> _blockingQueue = new ArrayBlockingQueue<Runnable>(_numThreads);
    private RejectedExecutionHandler _rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
    private ExecutorService _executorService =  new ThreadPoolExecutor(_numThreads, _numThreads, 0L, TimeUnit.MILLISECONDS, _blockingQueue, _rejectedExecutionHandler);

    ScanOperation(String folderPath, int secondsDelay)
    {
        _folderPath = folderPath;
        _secondsDelay = secondsDelay;
        resetDelay();
        System.out.println("number of threads: " + _numThreads);
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
            setIsRestart(false);
            long startTime = System.currentTimeMillis();
            processFolder(getFolderPath());
            long runTime = (System.currentTimeMillis() - startTime) / 1000;
            System.out.println(getFolderPath() + " scanned in " + runTime + " seconds");
        }
        while (isRestart());
    }

    public void restart()
    {
        System.out.println("Restarting folder scan for " + getFolderPath());
        setIsRestart(true);
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

        ScanOperation otherOperation = (ScanOperation)obj;
        return getFolderPath().equals(otherOperation.getFolderPath());
    }

    /*
     * Public methods
     */

    public void processFolder(int folderId)
    {
        Folder folder = new Folder(folderId);
        processFolder(folder.getFolderPath());
    }

    /**
     * Scan a folder for file changes
     */

    public void processFolder(String folderPath)
    {
        if (isRestart())
            return;

        File topFile = new File(folderPath);
        if (topFile.isDirectory())
        {
            // Retreive the folder object for this folder path
            final Folder topFolder = new Folder(topFile.getAbsolutePath());
            System.out.println("scanning " + topFolder.getFolderName() + "  id: " + topFolder.getFolderId());

            // Start recursively scanning the subfolders and files
            // Note: These won't be in any particular order
            for (final File subFile : topFile.listFiles())
            {
                if (isRestart())
                    return;

                if (subFile.isDirectory())
                {
                    if (!subFile.getName().startsWith(".AppleDouble"))
                    {
                        // This is a folder, so scan it
                        Folder folder = new Folder(subFile.getAbsolutePath());
                        if (folder.getFolderId() == null)
                        {
                            // This folder isn't in the database, so add it
                            folder.addToDatabase();
                        }
                        processFolder(subFile.getAbsolutePath());
                    }
                }
                else
                {
                    _executorService.submit(new Runnable()
                    {
                        public void run()
                        {
                            processFile(subFile, topFolder.getFolderId());
                        }
                    });
                }
            }
        }
    }

    public void processFile(File file, int folderId)
    {
        if (isRestart())
            return;

        if (file.getName().startsWith(".DS_Store"))
            return;

        // Make sure the extension is valid
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        String extension = fileName.substring(index+1, fileName.length());
        if (!_validExtensionsList.contains(extension.toLowerCase()))
            return;

        //System.out.println("processing file " + file.getName());

		if (MediaItem.fileNeedsUpdating(file))
		{
			AudioFile f = null;
			try {
				f = AudioFileIO.read(file);
			} catch (CannotReadException e) {
                System.out.println("Can't read file " + file.getName());
                //e.printStackTrace();
			} catch (IOException e) {
                System.out.println("Can't read file " + file.getName());
				//e.printStackTrace();
			} catch (TagException e) {
                System.out.println("Can't read file " + file.getName());
				//e.printStackTrace();
			} catch (ReadOnlyFileException e) {
                System.out.println("Can't read file " + file.getName());
				//e.printStackTrace();
			} catch (InvalidAudioFrameException e) {
                System.out.println("Can't read file " + file.getName());
				//e.printStackTrace();
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
