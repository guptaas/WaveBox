package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.HelperObjects.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.Netty.HttpServerHandler;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/14/12
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusApiHandler implements IApiHandler
{
    private UriWrapper _uri;
   	private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;

   	public StatusApiHandler(UriWrapper uri, Map<String, List<String>> parameters, HttpServerHandler sh)
   	{
   		_uri = uri;
   		_parameters = parameters;
        _sh = sh;
   	}

    public void process()
    {
        String response = "{\"error\":null,version:1}";
        _sh.sendJson(response);
    }
}
