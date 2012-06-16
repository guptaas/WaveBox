package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.HelperObjects.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.DataModel.Album;
import in.benjamm.pms.DataModel.Folder;
import in.benjamm.pms.Netty.HttpServerHandler;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/11/12
 * Time: 10:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlbumsApiHandler implements IApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;

    public AlbumsApiHandler(UriWrapper uri, Map<String, List<String>> parameters, HttpServerHandler sh)
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

        return "{\"error\":\"Invalid API call\"}";
    }

    private String _allAlbums()
    {
        List<Album> albums = Album.allAlbums();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, albums);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{\"error\":null, \"albums\":" + writer.toString() + "}";
    }

    private String _album(int albumId)
    {
        Album album = new Album(albumId);

        // Get the folders
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, album.listOfSongs());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{\"error\":null, \"songs\":" + writer.toString() + "}";
    }
}
