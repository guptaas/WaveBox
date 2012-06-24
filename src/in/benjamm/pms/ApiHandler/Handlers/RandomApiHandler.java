package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.DataModel.Model.Album;
import in.benjamm.pms.DataModel.Model.Artist;
import in.benjamm.pms.DataModel.Model.Song;
import in.benjamm.pms.HttpServer.HttpServerHandler;

import java.io.IOException;
import java.io.StringWriter;
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
public class RandomApiHandler implements IApiHandler
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

        return "{\"error\":\"Invalid API call\"}";
    }

    private String _songs(int count)
    {
        List<Song> songs = Song.randomSongs(count);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, songs);
        } catch (IOException e) {
            log2File(ERROR, e);
        }

        return "{\"error\":null, \"songs\":" + writer.toString() + "}";
    }

    private String _albums(int count)
    {
        List<Album> albums = Album.randomAlbums(count);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, albums);
        } catch (IOException e) {
            log2File(ERROR, e);
        }

        return "{\"error\":null, \"albums\":" + writer.toString() + "}";
    }

    private String _artists(int count)
    {
        List<Artist> artists = Artist.randomArtists(count);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, artists);
        } catch (IOException e) {
            log2File(ERROR, e);
        }

        return "{\"error\":null, \"artists\":" + writer.toString() + "}";
    }
}
