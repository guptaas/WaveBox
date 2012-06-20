package in.benjamm.pms.ApiHandler;

import com.jolbox.bonecp.UsernamePassword;
import in.benjamm.pms.ApiHandler.Handlers.*;
import in.benjamm.pms.DataModel.Model.User;
import in.benjamm.pms.Netty.HttpServerHandler;

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
        // Verify the login credentials
        try {
            String username = parameters.get("u") .get(0);
            String password = parameters.get("p").get(0);

            User user = new User(username);
            if (!user.authenticate(password))
                return new ErrorApiHandler(sh, "Invalid username or password");

        } catch (Exception e) {
            return new ErrorApiHandler(sh, "Invalid username or password");
        }

        IApiHandler returnHandler = null;
		try
		{
			UriWrapper uriW = new UriWrapper(uri);

			// Choose the proper rest handler
			if (uriW.getFirstPart().equals("api"))// && uri.getUriPart(1).equals(uri.getLastPart()))
			{
                String part1 = uriW.getUriPart(1);
                System.out.println("part1 = " + part1 + " part2 = " + uriW.getUriPart(2));

				if (part1.equals("test"))
				{
					returnHandler = new TestApiHandler(uriW, parameters, headers, sh);
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
