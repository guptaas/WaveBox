package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.HelperObjects.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.DataModel.Album;
import in.benjamm.pms.DataModel.Song;
import in.benjamm.pms.Netty.HttpServerHandler;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;
import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

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
 * Time: 11:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class SongsApiHandler implements IApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;

    public SongsApiHandler(UriWrapper $uri, Map<String, List<String>> $parameters, HttpServerHandler sh)
    {
        _uri = $uri;
        _parameters = $parameters;
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
            e.printStackTrace();
        }

        return "{\"error\":null, \"albums\":" + writer.toString() + "}";
    }
}
