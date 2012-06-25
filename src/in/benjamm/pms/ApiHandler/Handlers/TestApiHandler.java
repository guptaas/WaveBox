package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.ApiHandler;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.HttpServer.HttpServerHandler;

import java.util.List;
import java.util.Map;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/7/12
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestApiHandler extends ApiHandler
{
	private UriWrapper _uri;
	private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;
    private Map<String, String> _headers;

	public TestApiHandler(UriWrapper uri, Map<String, List<String>> parameters, Map<String, String> headers, HttpServerHandler sh, int userId)
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
        StringBuilder response = new StringBuilder();
        response.append("TestApiHandler  uri:" + _uri + "\n");
        response.append("Parameters:\n");
		for (String key : _parameters.keySet())
		{
            response.append(key + ": \n");
			for (String value : _parameters.get(key))
			{
                response.append(value + "   \n");
			}
            log2Out(TEST, "   \n");
		}
        response.append("Headers:\n");
        for (String key : _headers.keySet())
        {
            response.append(key + ": \n");
            String value = _headers.get(key);
            response.append(value + "   \n");
            response.append("   \n");
        }
		return response.toString();
	}
}
