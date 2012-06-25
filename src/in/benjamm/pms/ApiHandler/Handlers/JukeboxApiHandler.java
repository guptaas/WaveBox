package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.ApiHandler;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.DataModel.Singletons.Jukebox;
import in.benjamm.pms.HttpServer.HttpServerHandler;

import java.io.IOException;
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

        if (_uri.getUriPart(2).equals("play"))
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
        else if (_uri.getUriPart(2).equals("pause"))
        {
            _pause();
        }
        else if (_uri.getUriPart(2).equals("stop"))
        {
            _stop();
        }
        else if (_uri.getUriPart(2).equals("prev"))
        {
            _prev();
        }
        else if (_uri.getUriPart(2).equals("next"))
        {
            _next();
        }
        else if (_uri.getUriPart(2).equals("status"))
        {
            response = _status();
        }
        else if (_uri.getUriPart(2).equals("playlist"))
        {
            response = _playlist();
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
