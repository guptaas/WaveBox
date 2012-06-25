package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.ApiHandler;
import in.benjamm.pms.HttpServer.HttpServerHandler;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/7/12
 * Time: 6:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorApiHandler extends ApiHandler
{
    private HttpServerHandler _sh;
    private String _response = _invalidApiResponse();

    public ErrorApiHandler(HttpServerHandler sh)
    {
        _sh = sh;
    }

    public ErrorApiHandler(HttpServerHandler sh, String message)
    {
        _sh = sh;
        _response = _errorResponse(message);
    }

    public void process()
    {
        _sh.sendJson(_response);
    }
}
