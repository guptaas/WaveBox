package in.benjamm.pms.ApiHandler;

import java.util.LinkedList;
import java.util.List;

import static in.benjamm.pms.DataModel.Singletons.Log.*;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/8/12
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class UriWrapper
{
	private List<String> _uriParts;
	
	public UriWrapper(String uri)
	{
		if (uri != null)
		{
            log2Out(TEST, "uri: " + uri);
			_uriParts = _removeEmptyElements(uri.split("/"));
		}
	}
	
	public String getUriPart(int index)
	{
		if (_uriParts.size() > index)
		{
			return _uriParts.get(index);
		}
		return null;
	}
	
	public String getFirstPart()
	{
		return getUriPart(0);
	}
	
	public String getLastPart()
	{
		return getUriPart(_uriParts.size()-1);
	}
	
	private List<String> _removeEmptyElements(String[] input)
	{
		List<String> result = new LinkedList<String>();
		String blank = "";
		
		for(String item : input)
		{
			if (item != null)
			{
				if(!blank.equals(item))
				{
					result.add(item);
				}
			}
		}

		return result;
	}
}
