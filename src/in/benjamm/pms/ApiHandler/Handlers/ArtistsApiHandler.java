package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.DataModel.Model.Artist;
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
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArtistsApiHandler implements IApiHandler
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

        return "{\"error\":\"Invalid API call\"}";
    }

    private String _allArtists()
    {
        List<Artist> artists = Artist.allArtists();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, artists);
        } catch (IOException e) {
            log2File(ERROR, e);
        }

        return "{\"error\":null, \"artists\":" + writer.toString() + "}";
    }

    private String _artist(int artistId)
    {
        Artist artist = new Artist(artistId);

        String response = "{\"error\":null, \"albums\":";

        // Get the folders
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, artist.listOfAlbums());
        } catch (IOException e) {
            log2File(ERROR, e);
        }

        response += writer.toString();

        // Get the songs
        // TODO: this needs to be MediaItems not Songs
        mapper = new ObjectMapper();
        writer = new StringWriter();
        try {
            mapper.writeValue(writer, artist.listOfSongs());
        } catch (IOException e) {
            log2File(ERROR, e);
        }

        response += ",\"songs\":" + writer.toString() + "}";

        return response;
    }
}
