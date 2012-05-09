package in.benjamm.pms.ApiHandler.Handlers;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/7/12
 * Time: 6:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class StreamApiHandler
{
	/*File file = new File(path);
        if (file.isHidden() || !file.exists())
		{
            sendError(ctx, NOT_FOUND);
            return;
        }
        if (!file.isFile())
		{
            sendError(ctx, FORBIDDEN);
            return;
        }

        RandomAccessFile raf;
        try
		{
            raf = new RandomAccessFile(file, "r");
        }
		catch (FileNotFoundException fnfe)
		{
            sendError(ctx, NOT_FOUND);
            return;
        }
        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        setContentLength(response, fileLength);

        Channel ch = e.getChannel();

        // Write the initial line and the header.
        ch.write(response);

        // Write the content.
        ChannelFuture writeFuture;
        if (ch.getPipeline().get(SslHandler.class) != null)
		{
            // Cannot use zero-copy with HTTPS.
            writeFuture = ch.write(new ChunkedFile(raf, 0, fileLength, 8192));
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
                    System.out.printf("%s: %d / %d (+%d)%n", path, current, total, amount);
                }
            });
        }

        // Decide whether to close the connection or not.
        if (!isKeepAlive(request))
		{
            // Close the connection when the whole content is written out.
            writeFuture.addListener(ChannelFutureListener.CLOSE);
        }*/



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
}
