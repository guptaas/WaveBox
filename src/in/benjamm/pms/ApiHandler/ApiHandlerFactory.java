package in.benjamm.pms.ApiHandler;

import in.benjamm.pms.ApiHandler.Handlers.*;
import in.benjamm.pms.ApiHandler.HelperObjects.UriWrapper;
import in.benjamm.pms.Netty.HttpServerHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/7/12
 * Time: 6:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApiHandlerFactory
{
	public static IApiHandler createRestHandler(String uri, Map<String, List<String>> parameters, Map<String, String> headers, HttpServerHandler sh)
	{
		IApiHandler returnHandler = null;

		try
		{
			UriWrapper uriW = new UriWrapper(uri);

			// Choose the proper rest handler
			if (uriW.getFirstPart().equals("api"))// && uri.getUriPart(1).equals(uri.getLastPart()))
			{
                String part1 = uriW.getUriPart(1);

				if (part1.equals("test"))
				{
					returnHandler = new TestApiHandler(uriW, parameters, sh);
				}
				else if (part1.equals("folders"))
				{
                    returnHandler = new FoldersApiHandler(uriW, parameters, sh);
				}
                else if (part1.equals("artists"))
                {
                    returnHandler = new ArtistsApiHandler(uriW, parameters, sh);
                }
                else if (part1.equals("albums"))
                {
                    returnHandler = new AlbumsApiHandler(uriW, parameters, sh);
                }
                else if (part1.equals("songs"))
                {
                    returnHandler = new SongsApiHandler(uriW, parameters, sh);
                }
                else if (part1.equals("stream"))
                {
                    returnHandler = new StreamApiHandler(uriW, parameters, headers, sh);
                }
                else if (part1.equals("cover"))
                {
                    returnHandler = new CoverArtApiHandler(uriW, parameters, sh);
                }
                else if (part1.equals("status"))
                {
                    returnHandler = new StatusApiHandler(uriW, parameters, sh);
                }
			}
		}
		catch (NullPointerException e)
		{
			// Ignore null pointers, an error handler will be returned
		}

		// If no handler to use, return the error handler
		if (returnHandler == null)
		{
			returnHandler = new ErrorApiHandler(sh);
		}

        return returnHandler;
	}
}
