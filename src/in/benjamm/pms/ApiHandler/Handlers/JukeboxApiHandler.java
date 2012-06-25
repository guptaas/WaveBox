package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.ApiHandler;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.DataModel.Model.Song;
import in.benjamm.pms.DataModel.Singletons.Jukebox;
import in.benjamm.pms.HttpServer.HttpServerHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;


/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/24/12
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class JukeboxApiHandler extends ApiHandler
{
    private Jukebox _jukebox = Jukebox.sharedInstance();

    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;
    private int _userId;

    public JukeboxApiHandler(UriWrapper uri, Map<String, List<String>> parameters, HttpServerHandler sh, int userId)
    {
        _uri = uri;
        _parameters = parameters;
        _sh = sh;
        _userId = userId;
    }

    public void process()
    {
        String response = _defaultResponse();

        String part2 = _uri.getUriPart(2);
        String part3 = _uri.getUriPart(3);

        if (part2.equals("play"))
        {
            Integer index = null;
            if (_uri.getUriPart(3) != null)
            {
                try {
                    index = Integer.parseInt(_uri.getUriPart(3));
                } catch (Exception e) {
                    log2File(ERROR, e);
                }
            }

            if (index == null)
            {
                _play();
            }
            else
            {
                _playSongAtIndex(index);
            }
        }
        else if (part2.equals("pause"))
        {
            _pause();
        }
        else if (part2.equals("stop"))
        {
            _stop();
        }
        else if (part2.equals("prev"))
        {
            _prev();
        }
        else if (part2.equals("next"))
        {
            _next();
        }
        else if (part2.equals("status"))
        {
            response = _status();
        }
        else if (part2.equals("playlist"))
        {
            response = _playlist();
        }
        else if (part2.equals("add"))
        {
            if (_parameters.containsKey("i"))
            {
                _addSongs(_parameters.get("i"));
            }
        }
        else if (part2.equals("remove"))
        {
            if (_parameters.containsKey("i"))
            {
                _addSongs(_parameters.get("i"));
            }
        }
        else if (part2.equals("move"))
        {
            if (_parameters.containsKey("from") && _parameters.containsKey("to") )
            {
                try {
                    _move(_parameters.get("from").get(0), _parameters.get("to").get(0));
                } catch (Exception e) {
                    log2File(ERROR, e);
                }
            }
        }
        else if (part2.equals("clear"))
        {
            _clear();
        }

        _sh.sendJson(response);
    }

    private void _play()
    {
        _jukebox.play();
    }

    private void _pause()
    {
        _jukebox.pause();
    }

    private void _stop()
    {
        _jukebox.stop();
    }

    private void _prev()
    {
        _jukebox.prev();
    }

    private void _next()
    {
        _jukebox.next();
    }

    private void _clear()
    {
        _jukebox.clearPlaylist();
    }

    private void _playSongAtIndex(int index)
    {
        _jukebox.playSongAtIndex(index);
    }

    private void _addSongs(List<String> parameter)
    {
        List<Song> songs = new ArrayList<Song>();
        for (String songIdString : parameter)
        {
            try {
                int songId = Integer.parseInt(songIdString);
                songs.add(new Song(songId));
            } catch (NumberFormatException e) {
                log2File(ERROR, e);
            }
        }
        _jukebox.addSongs(songs);
    }

    private void _removeSongs(List<String> parameter)
    {
        List<Integer> indexes = new ArrayList<Integer>();
        for (String indexString : parameter)
        {
            try {
                Integer index = Integer.valueOf(indexString);
                indexes.add(index);
            } catch (NumberFormatException e) {
                log2File(ERROR, e);
            }
        }
        _jukebox.removeSongsAtIndexes(indexes);
    }

    private void _move(String from, String to)
    {
        try {
            int fromIndex = Integer.parseInt(from);
            int toIndex = Integer.parseInt(to);
            _jukebox.moveSong(fromIndex, toIndex);
        } catch (NumberFormatException e) {
            log2File(ERROR, e);
        }
    }

    private String _status()
    {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("isPlaying", _jukebox.isPlaying());
        jsonMap.put("currentIndex", _jukebox.getCurrentIndex());
        jsonMap.put("progress", _jukebox.progress());

        return _createJson(jsonMap);
    }

    private String _playlist()
    {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("isPlaying", _jukebox.isPlaying());
        jsonMap.put("currentIndex", _jukebox.getCurrentIndex());
        jsonMap.put("songs", _jukebox.listOfSongs());

        return _createJson(jsonMap);
    }
}
