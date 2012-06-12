package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.IApiHandler;
import org.jboss.netty.buffer.ChannelBuffers;
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
		return "Sorry this function is not supported";
	}
}
