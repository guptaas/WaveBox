package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.HelperObjects.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.DataModel.Artist;
import in.benjamm.pms.DataModel.Folder;
import org.jboss.netty.buffer.ChannelBuffers;
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
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArtistsApiHandler implements IApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;

    public ArtistsApiHandler(UriWrapper $uri, Map<String, List<String>> $parameters)
    {
        _uri = $uri;
        _parameters = $parameters;
    }

    public HttpResponse createResponse()
    {
        // Create the response
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(_processRequest(), CharsetUtil.UTF_8));
        return response;
    }

    private String _processRequest()
    {
        return _allArtists();
    }

    private String _allArtists()
    {
        List<Artist> artists = Artist.allArtists();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, artists);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{\"error\":null, \"artists\":" + writer.toString() + "}";
    }
}
