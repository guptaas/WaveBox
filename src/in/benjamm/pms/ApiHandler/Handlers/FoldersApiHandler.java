package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.HelperObjects.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.DataModel.Folder;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/8/12
 * Time: 6:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class FoldersApiHandler implements IApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;

    public FoldersApiHandler(UriWrapper $uri, Map<String, List<String>> $parameters)
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
        if (_uri.getUriPart(2) == null)
        {
            return _allFolders();
        }
        else
        {
            try {
                int folderId = Integer.parseInt(_uri.getUriPart(2));
                return _folder(folderId);
            } catch (NumberFormatException e) { }
        }

        return "{\"error\":\"Invalid API call\"}";
    }

    private String _allFolders()
    {
        List<Folder> folders = Folder.topLevelFolders();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, folders);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{\"error\":null, \"folders\":" + writer.toString() + "}";
    }

    private String _folder(int folderId)
    {
        Folder folder = new Folder(folderId);

        String response = "{\"error\":null, \"folders\":";

        // Get the folders
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, folder.listOfSubFolders());
        } catch (IOException e) {
            e.printStackTrace();
        }

        response += writer.toString();

        // Get the songs
        // TODO: this needs to be MediaItems not Songs
        mapper = new ObjectMapper();
        writer = new StringWriter();
        try {
            mapper.writeValue(writer, folder.listOfSongs());
        } catch (IOException e) {
            e.printStackTrace();
        }

        response += ",\"songs\":" + writer.toString() + "}";

        return response;
    }
}
