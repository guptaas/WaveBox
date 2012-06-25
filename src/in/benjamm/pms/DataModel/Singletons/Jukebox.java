package in.benjamm.pms.DataModel.Singletons;

import in.benjamm.pms.DataModel.Model.MediaItem;
import in.benjamm.pms.DataModel.Model.MediaItemType;
import in.benjamm.pms.DataModel.Model.Playlist;
import in.benjamm.pms.DataModel.Model.Song;
import jouvieje.bass.BassInit;
import jouvieje.bass.callbacks.SYNCPROC;
import jouvieje.bass.exceptions.BassException;
import jouvieje.bass.structures.HSTREAM;
import jouvieje.bass.structures.HSYNC;
import jouvieje.bass.utils.Pointer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;

import static jouvieje.bass.Bass.*;
import static jouvieje.bass.defines.BASS_STREAM.*;
import static jouvieje.bass.defines.BASS_SYNC.*;
import static jouvieje.bass.defines.BASS_POS.*;

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
        if (item != null)
        {
            if (item.getMediaItemType().equals(MediaItemType.MEDIA_ITEM_TYPE_SONG))
            {
                // (re)initialize BASS
                _bassInit();

                // Create the stream
                String path = item.file().getAbsolutePath();
                _currentStream = BASS_StreamCreateFile(false, path, 0, 0, BASS_STREAM_PRESCAN);

                // Set the end sync to continue the playlist after the song ends
                SYNCPROC endSync = new SYNCPROC(){
                    @Override
                    public void SYNCPROC(HSYNC handle, int channel, int data, Pointer user) {
                        // Just go to the next song
                        next();
                    }
                };
                BASS_ChannelSetSync(_currentStream.asInt(), BASS_SYNC_END, 0, endSync, null);
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

    private void _bassInit()
    {
        if (_isInitialized)
            _bassFree();

        /*
		 * NativeBass Init
		 */
		try {
			BassInit.loadLibraries();
		} catch(BassException e) {
			log2File(ERROR, e);
			return;
		}

		/*
		 * Checking NativeBass version
		 */
		if(BassInit.NATIVEBASS_LIBRARY_VERSION() != BassInit.NATIVEBASS_JAR_VERSION()) {
			log2File(ERROR, "Error!  NativeBass library version (" + BassInit.NATIVEBASS_LIBRARY_VERSION() + ") is different to jar version (" + BassInit.NATIVEBASS_JAR_VERSION() + ")");
			_bassFree();
            return;
		}

        _isInitialized = true;
    }

    private void _bassFree()
    {
        BASS_Free();

        _isInitialized = false;
        _isPlaying = false;
        _currentStream = null;
    }
}
