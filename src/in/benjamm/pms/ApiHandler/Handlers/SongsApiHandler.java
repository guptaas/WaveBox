package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.DataModel.Model.Song;
import in.benjamm.pms.HttpServer.HttpServerHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;


/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/11/12
 * Time: 11:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class SongsApiHandler implements IApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;

    public SongsApiHandler(UriWrapper uri, Map<String, List<String>> parameters, HttpServerHandler sh, int userId)
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
        return _allSongs();
    }

    private String _allSongs()
    {
        List<Song> songs = Song.allSongs();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, songs);
        } catch (IOException e) {
            log2File(ERROR, e);
        }

        return "{\"error\":null, \"albums\":" + writer.toString() + "}";
    }
}
