package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.HelperObjects.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.Netty.HttpServerHandler;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/7/12
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestApiHandler implements IApiHandler
{
	private UriWrapper _uri;
	private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;

	public TestApiHandler(UriWrapper $uri, Map<String, List<String>> $parameters, HttpServerHandler sh)
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
		System.out.println("TestApiHandler  uri:" + _uri);
		System.out.println("Parameters:");
		for (String key : _parameters.keySet())
		{
			System.out.print(key + ": ");
			for (String value : _parameters.get(key))
			{
				System.out.print(value + "   ");
			}
			System.out.println("   ");
		}
		System.out.println("   ");
		return "Yay it worked!";
	}
}
