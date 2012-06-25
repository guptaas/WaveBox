package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.ApiHandler;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.DataModel.Model.Album;
import in.benjamm.pms.DataModel.Model.Artist;
import in.benjamm.pms.DataModel.Model.Song;
import in.benjamm.pms.HttpServer.HttpServerHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static in.benjamm.pms.DataModel.Singletons.Log.log2File;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.ERROR;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/24/12
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomApiHandler extends ApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;

    private static final int _DEFAULT_COUNT = 20;

    public RandomApiHandler(UriWrapper uri, Map<String, List<String>> parameters, HttpServerHandler sh, int userId)
    {
        _uri = uri;
        _parameters = parameters;
        _sh = sh;
    }

    public void process()
    {
        _sh.sendJson(_processRequest());
    }

    private String _processRequest()
    {
        int count = _DEFAULT_COUNT;
        try {
            count = Integer.parseInt(_parameters.get("count").get(0));
        } catch (Exception e) {
            log2File(ERROR, e);
        }

        if (_uri.getUriPart(2).equals("songs"))
        {
            return _songs(count);
        }
        else if (_uri.getUriPart(2).equals("albums"))
        {
            return _albums(count);
        }
        else if (_uri.getUriPart(2).equals("artists"))
        {
            return _artists(count);
        }

        return _invalidApiResponse();
    }

    private String _songs(int count)
    {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("songs", Song.randomSongs(count));

        return _createJson(jsonMap);
    }

    private String _albums(int count)
    {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("albums", Album.randomAlbums(count));

        return _createJson(jsonMap);
    }

    private String _artists(int count)
    {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("artists", Artist.randomArtists(count));

        return _createJson(jsonMap);
    }
}
