package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.ApiHandler;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.DataModel.Model.Artist;
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
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArtistsApiHandler extends ApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;

    public ArtistsApiHandler(UriWrapper uri, Map<String, List<String>> parameters, HttpServerHandler sh, int userId)
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
            return _allArtists();
        }
        else
        {
            try {
                int artistId = Integer.parseInt(_uri.getUriPart(2));
                return _artist(artistId);
            } catch (NumberFormatException e) { }
        }

        return _invalidApiResponse();
    }

    private String _allArtists()
    {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("artists", Artist.allArtists());

        return _createJson(jsonMap);
    }

    private String _artist(int artistId)
    {
        Map<String, Object> jsonMap = new HashMap<String, Object>();

        Artist artist = new Artist(artistId);
        jsonMap.put("albums", artist.listOfAlbums());
        jsonMap.put("songs", artist.listOfSongs());

        return _createJson(jsonMap);
    }
}
