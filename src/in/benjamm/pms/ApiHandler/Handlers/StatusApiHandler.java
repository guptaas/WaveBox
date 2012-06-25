package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.ApiHandler;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.HttpServer.HttpServerHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/14/12
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusApiHandler extends ApiHandler
{
    private UriWrapper _uri;
   	private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;

   	public StatusApiHandler(UriWrapper uri, Map<String, List<String>> parameters, HttpServerHandler sh, int userId)
   	{
   		_uri = uri;
   		_parameters = parameters;
        _sh = sh;
   	}

    public void process()
    {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("version", 1);

        _sh.sendJson(_createJson(jsonMap));
    }
}
