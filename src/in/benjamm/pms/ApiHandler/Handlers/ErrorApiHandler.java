package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.HttpServer.HttpServerHandler;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/7/12
 * Time: 6:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorApiHandler implements IApiHandler
{
    private HttpServerHandler _sh;
    private String _message = "Sorry this function is not supported";

    public ErrorApiHandler(HttpServerHandler sh)
    {
        _sh = sh;
    }

    public ErrorApiHandler(HttpServerHandler sh, String message)
    {
        _sh = sh;
        _message = message;
    }

    public void process()
    {
        _sh.sendJson(_processRequest());
    }

	private String _processRequest()
	{
		return _message;
	}
}
