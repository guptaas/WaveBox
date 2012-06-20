package in.benjamm.pms.ApiHandler.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.DataModel.Model.Folder;
import in.benjamm.pms.Netty.HttpServerHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/8/12
 * Time: 6:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class FoldersApiHandler implements IApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;

    public FoldersApiHandler(UriWrapper uri, Map<String, List<String>> parameters, HttpServerHandler sh)
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

        return "{\"error\":\"Invalid API call\"}";
    }

    private String _mediaFolders()
    {
        List<Folder> folders = Folder.mediaFolders();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, folders);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{\"error\":null, \"folders\":" + writer.toString() + "}";
    }

    private String _allFolders()
    {
        List<Folder> folders = Folder.topLevelFolders();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, folders);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{\"error\":null, \"folders\":" + writer.toString() + "}";
    }

    private String _folder(int folderId)
    {
        Folder folder = new Folder(folderId);
        if (folder.getFolderId() == null)
            return "{\"error\":\"folder doesn't exist\"}";

        String response = "{\"error\":null, \"folders\":";

        // Get the folders
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, folder.listOfSubFolders());
        } catch (IOException e) {
            e.printStackTrace();
        }

        response += writer.toString();

        // Get the songs
        // TODO: this needs to be MediaItems not Songs
        mapper = new ObjectMapper();
        writer = new StringWriter();
        try {
            mapper.writeValue(writer, folder.listOfSongs());
        } catch (IOException e) {
            e.printStackTrace();
        }

        response += ",\"songs\":" + writer.toString() + "}";

        return response;
    }
}
