package in.benjamm.pms.DataModel.Singletons;

import in.benjamm.pms.DataModel.Model.*;
import jouvieje.bass.BassInit;
import jouvieje.bass.callbacks.SYNCPROC;
import jouvieje.bass.exceptions.BassException;
import jouvieje.bass.structures.BASS_PLUGININFO;
import jouvieje.bass.structures.HPLUGIN;
import jouvieje.bass.structures.HSTREAM;
import jouvieje.bass.structures.HSYNC;
import jouvieje.bass.utils.Pointer;
import org.jouvieje.libloader.LibLoader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;

import static jouvieje.bass.Bass.*;
import static jouvieje.bass.defines.BASS_STREAM.*;
import static jouvieje.bass.defines.BASS_SYNC.*;
import static jouvieje.bass.defines.BASS_POS.*;
import static jouvieje.bass.defines.BASS_CONFIG.*;
import static jouvieje.bass.defines.BASS_ERROR.*;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/24/12
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class Jukebox
{
    private static Jukebox _sharedInstance = new Jukebox();
    public static Jukebox sharedInstance() { return _sharedInstance; }

    private boolean _isInitialized;
    public boolean isInitialized() { return _isInitialized; }

    private boolean _isPlaying;
    public  boolean isPlaying() { return _isInitialized && _isPlaying; }

    private int _currentIndex;
    public int getCurrentIndex() { return _currentIndex; }

    private static final String _PLAYLIST_NAME = "jukeboxQPbjnbh2JPU5NGxhXiiQ"; // Randomish name to avoid conflicts
    private static Playlist _playlist;

    private HSTREAM _currentStream;

    static
    {
        _playlist = new Playlist(_PLAYLIST_NAME);
        if (_playlist.getPlaylistId() == null)
        {
            // This playlist doesn't exist yet, so create it
            _playlist.createPlaylist();
        }
    }

    public double progress()
    {
        if (_isInitialized && _currentStream != null)
        {
            long pcmBytePosition = BASS_ChannelGetPosition(_currentStream.asInt(), BASS_POS_BYTE | BASS_POS_DECODE);
            double seconds = BASS_ChannelBytes2Seconds(_currentStream.asInt(), pcmBytePosition);
            return seconds;
        }

        return 0.0;
    }

    public void play()
    {
        if (_currentStream != null)
        {
            BASS_Start();
            _isPlaying = true;
        }
    }

    public void pause()
    {
        if (_isPlaying && _currentStream != null)
        {
            BASS_Pause();
            _isPlaying = false;
        }
    }

    public void stop()
    {
        if (_isInitialized)
        {
            _bassFree();
        }
    }

    public void prev()
    {
        // Decrement the current index, but if already the first song, restart it
        _currentIndex = _currentIndex - 1 < 0 ? 0 : _currentIndex - 1;

        playSongAtIndex(_currentIndex);
    }

    public void next()
    {
        // Increment the current index, but if at the end of the playlist, stop
        _currentIndex = _currentIndex + 1 >= _playlist.getPlaylistCount() ? _currentIndex : _currentIndex + 1;

        if (_currentIndex >= _playlist.getPlaylistCount())
        {
            stop();
        }
        else
        {
            playSongAtIndex(_currentIndex);
        }
    }

    public void playSongAtIndex(int index)
    {
        MediaItem item = _playlist.mediaItemAtIndex(index);
        log2Out(TEST, "playing song: " + item.getFileName());
        if (item != null)
        {
            if (item.getItemTypeId() == ItemType.SONG.getItemTypeId())
            {
                // (re)initialize BASS
                _bassInit();

                // Create the stream
                String path = item.file().getAbsolutePath();
                log2Out(TEST, "path: " + path);
                _currentStream = BASS_StreamCreateFile(false, path, 0, 0, BASS_STREAM_PRESCAN);
                if (_currentStream == null)
                {
                    log2File(ERROR, "BASS Failed to create stream for " + path);
                    _bassLogError();
                }
                else
                {
                    log2Out(TEST, "currentStream: " + _currentStream.asInt());

                    // Set the end sync to continue the playlist after the song ends
                    SYNCPROC endSync = new SYNCPROC(){
                        @Override
                        public void SYNCPROC(HSYNC handle, int channel, int data, Pointer user) {
                            // Just go to the next song
                            next();
                        }
                    };
                    BASS_ChannelSetSync(_currentStream.asInt(), BASS_SYNC_END, 0, endSync, null);

                    // Play the stream
                    BASS_ChannelPlay(_currentStream.asInt(), true);
                    _isPlaying = true;
                }
            }
            else
            {
                // Only play songs, so skip this item
                next();
            }
        }
    }

    public List<MediaItem> listOfSongs()
    {
        return _playlist.listOfMediaItems();
    }

    public void removeSongAtIndex(int index)
    {
        _playlist.removeMediaItemAtIndex(index);
    }

    public void removeSongsAtIndexes(List<Integer> indexes)
    {
        _playlist.removeMediaItemsAtIndexes(indexes);
    }

    public void moveSong(int fromIndex, int toIndex)
    {
        _playlist.moveMediaItem(fromIndex, toIndex);
    }

    public void addSong(Song song)
    {
        _playlist.addMediaItem(song, true);
    }

    public void addSongs(List<Song> songs)
    {
        List<MediaItem> mediaItems = new ArrayList<MediaItem>();
        mediaItems.addAll(songs);
        _playlist.addMediaItems(mediaItems);
    }

    public void insertSong(Song song, int index)
    {
        _playlist.insertMediaItem(song, index);
    }

    public void clearPlaylist()
    {
        stop();
        _playlist.clearPlaylist();
    }

    private void _bassLoadPlugins()
    {
        File[] plugins = new File(".").listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name)
            {
                name = name.toLowerCase();

                final int platform = LibLoader.getPlatform();
                if(platform == LibLoader.PLATFORM_WINDOWS)
                {
                    if (System.getProperty("os.arch").equals("x86"))
                        return name.startsWith("bass") && name.endsWith(".dll");
                    else
                        return name.startsWith("bass") && name.endsWith("64.dll");
                }
                else if (platform == LibLoader.PLATFORM_LINUX)
                {
                    if (System.getProperty("os.arch").equals("x86"))
                        return name.startsWith("libbass") && name.endsWith(".so");
                    else
                        return name.startsWith("libbass") && name.endsWith("64.so");
                }
                else if (platform == LibLoader.PLATFORM_MAC)
                {
                    return name.startsWith("libbass") && name.endsWith(".dylib");
                }
                else
                {
                    return false;
                }
            }
        });

        if(plugins != null && plugins.length > 0)
        {
            for(File plugin : plugins)
            {
                // Load plugin
                log2File(TEST, "BASS Loading " + plugin.getName() + " plugin");
                HPLUGIN plug = BASS_PluginLoad(plugin.getAbsolutePath(), 0);
                if(plug == null)
                {
                    continue;
                }

                // Plugin loaded, log info
                BASS_PLUGININFO pluginInfo = BASS_PluginGetInfo(plug);
                for(int f = 0; f < pluginInfo.getNumFormats(); f++)
                {
                    //Format description
                    final String format = String.format("%s (%s) - %s",
                            pluginInfo.getFormats()[f].getName(),
                            pluginInfo.getFormats()[f].getExts(),
                            plugin.getName());

                    //Extension filter
                    final String exts = String.format("%s", pluginInfo.getFormats()[f].getExts());

                    log2File(TEST, "Loaded plugin: " + format + " " + exts);
                }
            }
        }
    }

    private void _bassInit()
    {
        if (_isInitialized)
            _bassFree();

        // NativeBass Init
		try {
			BassInit.loadLibraries();
		} catch(BassException e) {
			log2File(ERROR, e);
			return;
		}

		// Checking NativeBass version
		if(BassInit.NATIVEBASS_LIBRARY_VERSION() != BassInit.NATIVEBASS_JAR_VERSION()) {
			log2File(ERROR, "Error!  NativeBass library version (" + BassInit.NATIVEBASS_LIBRARY_VERSION() + ") is different to jar version (" + BassInit.NATIVEBASS_JAR_VERSION() + ")");
			_bassFree();
            return;
		}

        	// Initialize BASS
            BASS_SetConfig(BASS_CONFIG_BUFFER, BASS_GetConfig(BASS_CONFIG_UPDATEPERIOD) + 200); // set the buffer length to the minimum amount + 200ms
            log2File(TEST, "BASS buffer size: " + BASS_GetConfig(BASS_CONFIG_BUFFER));
            BASS_SetConfig(BASS_CONFIG_FLOATDSP, 1); // set DSP effects to use floating point math to avoid clipping within the effects chain
            if (!BASS_Init(-1, 44100, 0, new Pointer(), new Pointer())) 	// Initialize default device.
            {
                log2File(ERROR, "BASS Can't initialize device");
            }

            // Load the plugins
            _bassLoadPlugins();

        _isInitialized = true;
    }

    private void _bassFree()
    {
        BASS_Free();
        BASS_PluginFree(null);

        _isInitialized = false;
        _isPlaying = false;
        _currentStream = null;
    }

    private String _bassErrorCodeToString(int errorCode)
    {
    	switch (errorCode)
    	{
    		case BASS_OK:				return "No error! All OK";
    		case BASS_ERROR_MEM:		return "Memory error";
    		case BASS_ERROR_FILEOPEN:	return "Can't open the file";
    		case BASS_ERROR_DRIVER:		return "Can't find a free/valid driver";
    		case BASS_ERROR_BUFLOST:	return "The sample buffer was lost";
    		case BASS_ERROR_HANDLE:		return "Invalid handle";
    		case BASS_ERROR_FORMAT:		return "Unsupported sample format";
    		case BASS_ERROR_POSITION:	return "Invalid position";
    		case BASS_ERROR_INIT:		return "BASS_Init has not been successfully called";
    		case BASS_ERROR_START:		return "BASS_Start has not been successfully called";
    		case BASS_ERROR_ALREADY:	return "Already initialized/paused/whatever";
    		case BASS_ERROR_NOCHAN:		return "Can't get a free channel";
    		case BASS_ERROR_ILLTYPE:	return "An illegal type was specified";
    		case BASS_ERROR_ILLPARAM:	return "An illegal parameter was specified";
    		case BASS_ERROR_NO3D:		return "No 3D support";
    		case BASS_ERROR_NOEAX:		return "No EAX support";
    		case BASS_ERROR_DEVICE:		return "Illegal device number";
    		case BASS_ERROR_NOPLAY:		return "Not playing";
    		case BASS_ERROR_FREQ:		return "Illegal sample rate";
    		case BASS_ERROR_NOTFILE:	return "The stream is not a file stream";
    		case BASS_ERROR_NOHW:		return "No hardware voices available";
    		case BASS_ERROR_EMPTY:		return "The MOD music has no sequence data";
    		case BASS_ERROR_NONET:		return "No internet connection could be opened";
    		case BASS_ERROR_CREATE:		return "Couldn't create the file";
    		case BASS_ERROR_NOFX:		return "Effects are not available";
    		case BASS_ERROR_NOTAVAIL:	return "Requested data is not available";
    		case BASS_ERROR_DECODE:		return "The channel is a 'decoding channel'";
    		case BASS_ERROR_DX:			return "A sufficient DirectX version is not installed";
    		case BASS_ERROR_TIMEOUT:	return "Connection timedout";
    		case BASS_ERROR_FILEFORM:	return "Unsupported file format";
    		case BASS_ERROR_SPEAKER:	return "Unavailable speaker";
    		case BASS_ERROR_VERSION:	return "Invalid BASS version (used by add-ons)";
    		case BASS_ERROR_CODEC:		return "Codec is not available/supported";
    		case BASS_ERROR_ENDED:		return "The channel/file has ended";
    		case BASS_ERROR_BUSY:		return "The device is busy";
    		case BASS_ERROR_UNKNOWN:
    		default:					return "Unknown error.";
    	}
    }

    private void _bassLogError()
    {
        int errorCode = BASS_ErrorGetCode();
        log2File(ERROR, "BASS error: " + errorCode + " - " + _bassErrorCodeToString(errorCode));
    }
}
