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

import static in.benjamm.pms.DataModel.Singletons.Log.*;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/18/12
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class FolderScanOperation extends ScanOperation
{
    private String _folderPath;
    public String getFolderPath() { return _folderPath; }

    private String[] _validExtensions = {"mp3", "aac", "m4a", "mp4", "flac", "wv", "mpc", "ogg"};
    private List<String> _validExtensionsList = Arrays.asList(_validExtensions);

    public FolderScanOperation(String folderPath, int secondsDelay)
    {
        super(secondsDelay);
        _folderPath = folderPath;
    }

    public void start()
    {
        processFolder(getFolderPath());
    }

    /**
     * Scan a folder for file changes
     */

    /*
     * Public methods
     */

    public void processFolder(int folderId)
    {
        Folder folder = new Folder(folderId);
        processFolder(folder.getFolderPath());
    }

    public void processFolder(String folderPath)
    {
        if (isRestart())
            return;

        File topFile = new File(folderPath);
        if (topFile.isDirectory())
        {
            // Retreive the folder object for this folder path
            final Folder topFolder = new Folder(topFile.getAbsolutePath());
            log2Out(TEST, "scanning " + topFolder.getFolderName() + "  id: " + topFolder.getFolderId());

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
                    submitTask(new Runnable() {
                        public void run() {
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

        //log2Out(TEST, "processing file " + file.getName());

		if (MediaItem.fileNeedsUpdating(file))
		{
			AudioFile f = null;
			try {
				f = AudioFileIO.read(file);
			} catch (CannotReadException e) {
                log2Out(TEST, "Can't read file " + file.getName());
			} catch (IOException e) {
                log2Out(TEST, "Can't read file " + file.getName());
			} catch (TagException e) {
                log2Out(TEST, "Can't read file " + file.getName());
			} catch (ReadOnlyFileException e) {
                log2Out(TEST, "Can't read file " + file.getName());
			} catch (InvalidAudioFrameException e) {
                log2Out(TEST, "Can't read file " + file.getName());
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
}
