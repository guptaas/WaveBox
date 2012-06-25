package in.benjamm.pms.ApiHandler;

import in.benjamm.pms.ApiHandler.Handlers.*;
import in.benjamm.pms.DataModel.Model.User;
import in.benjamm.pms.HttpServer.HttpServerHandler;

import java.util.List;
import java.util.Map;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/7/12
 * Time: 6:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApiHandlerFactory
{
	public static ApiHandler createRestHandler(String uri, Map<String, List<String>> parameters, Map<String, String> headers, HttpServerHandler sh)
	{
        int userId;

        // Verify the login credentials
        try {
            String username = parameters.get("u") .get(0);
            String password = parameters.get("p").get(0);

            User user = new User(username);
            if (!user.authenticate(password))
                return new ErrorApiHandler(sh, "Invalid username or password");

            userId = user.getUserId();
        } catch (Exception e) {
            return new ErrorApiHandler(sh, "Invalid username or password");
        }

        ApiHandler returnHandler = null;
		try
		{
			UriWrapper uriW = new UriWrapper(uri);

			// Choose the proper rest handler
			if (uriW.getFirstPart().equals("api"))// && uri.getUriPart(1).equals(uri.getLastPart()))
			{
                String part1 = uriW.getUriPart(1);
                log2Out(TEST, "part1 = " + part1 + " part2 = " + uriW.getUriPart(2));

				if (part1.equals("test"))
				{
					returnHandler = new TestApiHandler(uriW, parameters, headers, sh, userId);
				}
				else if (part1.equals("folders"))
				{
                    returnHandler = new FoldersApiHandler(uriW, parameters, sh, userId);
				}
                else if (part1.equals("artists"))
                {
                    returnHandler = new ArtistsApiHandler(uriW, parameters, sh, userId);
                }
                else if (part1.equals("albums"))
                {
                    returnHandler = new AlbumsApiHandler(uriW, parameters, sh, userId);
                }
                else if (part1.equals("songs"))
                {
                    returnHandler = new SongsApiHandler(uriW, parameters, sh, userId);
                }
                else if (part1.equals("stream"))
                {
                    returnHandler = new StreamApiHandler(uriW, parameters, headers, sh, userId);
                }
                else if (part1.equals("cover"))
                {
                    returnHandler = new CoverArtApiHandler(uriW, parameters, sh, userId);
                }
                else if (part1.equals("status"))
                {
                    returnHandler = new StatusApiHandler(uriW, parameters, sh, userId);
                }
                else if (part1.equals("random"))
                {
                    returnHandler = new RandomApiHandler(uriW, parameters, sh, userId);
                }
                else if (part1.equals("jukebox"))
                {
                    returnHandler = new JukeboxApiHandler(uriW, parameters, sh, userId);
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
