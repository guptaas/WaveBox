package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.HelperObjects.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.DataModel.Song;
import in.benjamm.pms.Netty.HttpServerHandler;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.jboss.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.channels.Channel;
import java.util.List;
import java.util.Map;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/7/12
 * Time: 6:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class StreamApiHandler implements IApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;
    private Map<String, String> _headers;

    public StreamApiHandler(UriWrapper uri, Map<String, List<String>> parameters, Map<String, String> headers, HttpServerHandler sh)
    {
        _uri = uri;
        _parameters = parameters;
        _headers = headers;
        _sh = sh;
    }

    public void process()
    {
        File file = null;
        try {
            if (_uri.getUriPart(2) != null)
            {
                int songId = Integer.parseInt(_uri.getUriPart(2));
                Song song = new Song(songId);
                file = song.songFile();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //_sh.sendError(_ctx, NOT_FOUND);
            return;
        }

        long offset = 0;
        if (_headers.containsKey("Range"))
        {
            try {
                String range = _headers.get("Range");
                String[] ranges = range.substring("bytes=".length()).split("-");
                offset = Long.valueOf(ranges[0]);
                //long to = Long.valueOf(ranges[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        _sh.sendFile(file, offset);
    }
}



	/*private String sanitizeUri(String uri)
	{
		// Decode the path.
		try
		{
			uri = URLDecoder.decode(uri, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			try
			{
				uri = URLDecoder.decode(uri, "ISO-8859-1");
			}
			catch (UnsupportedEncodingException e1)
			{
				throw new Error();
			}
		}

		// Convert file separators.
		uri = uri.replace('/', File.separatorChar);

		// Simplistic dumb security check.
		// You will have to do something serious in the production environment.
		if (uri.contains(File.separator + ".") ||
				uri.contains("." + File.separator) ||
				uri.startsWith(".") || uri.endsWith("."))
		{
			return null;
		}

		// Convert to absolute path.
		return System.getProperty("user.dir") + File.separator + uri;
	}*/
