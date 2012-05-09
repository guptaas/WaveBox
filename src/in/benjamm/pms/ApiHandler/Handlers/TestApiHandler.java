package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.IApiHandler;

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
	private String _uri;
	private Map<String, List<String>> _parameters;

	public TestApiHandler(String $uri, Map<String, List<String>> $parameters)
	{
		_uri = $uri;
		_parameters = $parameters;
	}

	public String processRequest()
	{
		System.out.println("TestApiHandler");
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
		System.out.println("   ");
		return "Yay it worked!";
	}
}
