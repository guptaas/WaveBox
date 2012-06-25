package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.ApiHandler;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.DataModel.Model.Folder;
import in.benjamm.pms.HttpServer.HttpServerHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;


/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/8/12
 * Time: 6:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class FoldersApiHandler extends ApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;

    public FoldersApiHandler(UriWrapper uri, Map<String, List<String>> parameters, HttpServerHandler sh, int userId)
    {
        _uri = uri;
        _parameters = parameters;
        _sh = sh;
    }

    public void process()
    {
        _sh.sendJson(_processRequest());
    }

	private String _processRequest()
	{
        if (_uri.getUriPart(2) == null)
        {
            return _allFolders();
        }
        else if (_uri.getUriPart(2).equals("top"))
        {
            return _mediaFolders();
        }
        else
        {
            try {
                int folderId = Integer.parseInt(_uri.getUriPart(2));
                return _folder(folderId);
            } catch (NumberFormatException e) { }
        }

        return _invalidApiResponse();
    }

    private String _mediaFolders()
    {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("folders", Folder.mediaFolders());

        return _createJson(jsonMap);
    }

    private String _allFolders()
    {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("folders", Folder.topLevelFolders());

        return _createJson(jsonMap);
    }

    private String _folder(int folderId)
    {
        Folder folder = new Folder(folderId);
        if (folder.getFolderId() == null)
            return _errorResponse("Folder doesn't exist");

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("folders", folder.listOfSubFolders());
        jsonMap.put("songs", folder.listOfSongs());
        jsonMap.put("videos", folder.listOfVideos());

        return _createJson(jsonMap);
    }
}
