package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.Netty.HttpServerHandler;

import java.util.List;
import java.util.Map;

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
    private Map<String, String> _headers;

	public TestApiHandler(UriWrapper uri, Map<String, List<String>> parameters, Map<String, String> headers, HttpServerHandler sh)
	{
		_uri = uri;
		_parameters = parameters;
        _sh = sh;
        _headers = headers;
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
        System.out.println("Headers:");
        for (String key : _headers.keySet())
        {
            System.out.print(key + ": ");
            String value = _headers.get(key);
            System.out.print(value + "   ");
            System.out.println("   ");
        }
		System.out.println("   ");
		return "Yay it worked!";
	}
}
