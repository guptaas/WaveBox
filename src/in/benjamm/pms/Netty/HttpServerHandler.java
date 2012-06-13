package in.benjamm.pms.Netty;

import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.ApiHandler.ApiHandlerFactory;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.jboss.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.*;
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
    private ChannelHandlerContext _ctx;
    private MessageEvent _e;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
        _ctx = ctx;
        _e = e;

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
		IApiHandler apiHandler = ApiHandlerFactory.createRestHandler(path, parameters, this);
        apiHandler.process();
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

    public void sendError(ChannelHandlerContext ctx, HttpResponseStatus status)
	{
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(
                "Failure: " + status.toString() + "\r\n",
                CharsetUtil.UTF_8));

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

    public void sendFile(final File file)
    {
        if (file == null)
            return;

        RandomAccessFile raf;
        try
        {
            raf = new RandomAccessFile(file, "r");
        }
        catch (FileNotFoundException fnfe)
        {
            sendError(_ctx, NOT_FOUND);
            return;
        }
        long fileLength = 0;
        try {
            fileLength = raf.length();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpHeaders.setContentLength(response, fileLength);

        Channel ch = _e.getChannel();

        // Write the initial line and the header.
        ch.write(response);

        // Write the content.
        ChannelFuture writeFuture = null;
        if (ch.getPipeline().get(SslHandler.class) != null)
        {
            // Cannot use zero-copy with HTTPS.
            try {
                writeFuture = ch.write(new ChunkedFile(raf, 0, fileLength, 8192));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            // No encryption - use zero-copy.
            final FileRegion region = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
            writeFuture = ch.write(region);
            writeFuture.addListener(new ChannelFutureProgressListener()
            {
                public void operationComplete(ChannelFuture future)
                {
                    region.releaseExternalResources();
                }

                public void operationProgressed(ChannelFuture future, long amount, long current, long total)
                {
                    System.out.printf("%s: %d / %d (+%d)%n", file.getName(), current, total, amount);
                }
            });
        }

        // Close the connection when the whole content is written out.
        if (writeFuture != null)
        {
            writeFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void sendFile(byte bytes[])
    {
        // Create the response
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        //response.setHeader(CONTENT_TYPE, "application/json; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(bytes));

        _ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

    public void sendJson(String json)
    {
        // Create the response
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.setHeader(CONTENT_TYPE, "application/json; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(json, CharsetUtil.UTF_8));

        _ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }
}
