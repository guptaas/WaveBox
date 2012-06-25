package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.ApiHandler;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.DataModel.Model.Album;
import in.benjamm.pms.HttpServer.HttpServerHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;


/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/11/12
 * Time: 10:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlbumsApiHandler extends ApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;

    public AlbumsApiHandler(UriWrapper uri, Map<String, List<String>> parameters, HttpServerHandler sh, int userId)
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
        if (_uri.getUriPart(2) == null)
        {
            return _allAlbums();
        }
        else
        {
            try {
                int albumId = Integer.parseInt(_uri.getUriPart(2));
                return _album(albumId);
            } catch (NumberFormatException e) { }
        }

        return _invalidApiResponse();
    }

    private String _allAlbums()
    {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("albums", Album.allAlbums());

        return _createJson(jsonMap);
    }

    private String _album(int albumId)
    {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("songs", new Album(albumId).listOfSongs());

        return _createJson(jsonMap);
    }
}
