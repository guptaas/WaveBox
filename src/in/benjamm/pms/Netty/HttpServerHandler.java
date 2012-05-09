package in.benjamm.pms.Netty;

import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.ApiHandler.ApiHandlerFactory;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpMethod.POST;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 */
public class HttpServerHandler extends SimpleChannelUpstreamHandler
{
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		// Check the request type
		// Only accept GET and POST requests
        HttpRequest request = (HttpRequest) e.getMessage();
        if (request.getMethod() != GET && request.getMethod() != POST)
		{
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }

		// Get the path
		QueryStringDecoder getDecoder = new QueryStringDecoder(request.getUri());
		String path = getDecoder.getPath();
		if (path == null)
		{
			sendError(ctx, FORBIDDEN);
			return;
		}

		// Get the parameters
		Map<String, List<String>> parameters = getDecoder.getParameters();
		if (request.getMethod() == POST)
		{
			QueryStringDecoder postDecoder = new QueryStringDecoder("?" +
					request.getContent().toString(CharsetUtil.UTF_8));
			Map<String, List<String>> postParameters = postDecoder.getParameters();

			if (parameters == null || parameters.size() == 0)
				parameters = postParameters;
			else
				parameters.putAll(postParameters);
		}

		// Create a rest handler
		IApiHandler apiHandler = ApiHandlerFactory.createRestHandler(path, parameters);
		String responseString = apiHandler.processRequest();

		// Create the response
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.setContent(ChannelBuffers.copiedBuffer(responseString, CharsetUtil.UTF_8));

		// Send the response
		// Close the connection as soon as the response is sent
		ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
	{
        Channel ch = e.getChannel();
        Throwable cause = e.getCause();
        if (cause instanceof TooLongFrameException)
		{
            sendError(ctx, BAD_REQUEST);
            return;
        }

        cause.printStackTrace();
        if (ch.isConnected())
		{
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status)
	{
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(
                "Failure: " + status.toString() + "\r\n",
                CharsetUtil.UTF_8));

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }
}
