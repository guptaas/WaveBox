package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.Netty.HttpServerHandler;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/7/12
 * Time: 6:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorApiHandler implements IApiHandler
{
    private HttpServerHandler _sh;

    public ErrorApiHandler(HttpServerHandler sh)
    {
        _sh = sh;
    }

    public void process()
    {
        _sh.sendJson(_processRequest());
    }

	private String _processRequest()
	{
		return "Sorry this function is not supported";
	}
}
