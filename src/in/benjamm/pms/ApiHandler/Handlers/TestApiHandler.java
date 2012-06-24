package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.HttpServer.HttpServerHandler;

import java.util.List;
import java.util.Map;

import static in.benjamm.pms.DataModel.Singletons.Log.*;

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
        log2Out(TEST, "TestApiHandler  uri:" + _uri);
        log2Out(TEST, "Parameters:");
		for (String key : _parameters.keySet())
		{
            log2Out(TEST, key + ": ");
			for (String value : _parameters.get(key))
			{
                log2Out(TEST, value + "   ");
			}
            log2Out(TEST, "   ");
		}
        log2Out(TEST, "Headers:");
        for (String key : _headers.keySet())
        {
            log2Out(TEST, key + ": ");
            String value = _headers.get(key);
            log2Out(TEST, value + "   ");
            log2Out(TEST, "   ");
        }
        log2Out(TEST, "   ");
		return "Yay it worked!";
	}
}
