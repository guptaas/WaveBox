package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.IApiHandler;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/7/12
 * Time: 6:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorApiHandler implements IApiHandler
{
	public String processRequest()
	{
		return "Sorry this function is not supported";
	}
}
