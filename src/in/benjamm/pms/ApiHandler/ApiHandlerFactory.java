package in.benjamm.pms.ApiHandler;

import in.benjamm.pms.ApiHandler.Handlers.ErrorApiHandler;
import in.benjamm.pms.ApiHandler.Handlers.TestApiHandler;
import in.benjamm.pms.ApiHandler.HelperObjects.UriWrapper;

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
	public static IApiHandler createRestHandler(String $uri, Map<String, List<String>> $parameters)
	{
		IApiHandler returnHandler = null;

		try
		{
			UriWrapper uri = new UriWrapper($uri);

			// Choose the proper rest handler
			if (uri.getFirstPart().equals("api") && uri.getUriPart(1).equals(uri.getLastPart()))
			{
				if (uri.getUriPart(1).equals("test"))
				{
					returnHandler = new TestApiHandler($uri, $parameters);
				}
				else if (uri.getUriPart(1).equals("getFolders"))
				{

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
			returnHandler = new ErrorApiHandler();
		}

		return returnHandler;
	}
}
