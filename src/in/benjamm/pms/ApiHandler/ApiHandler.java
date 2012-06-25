package in.benjamm.pms.ApiHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static in.benjamm.pms.DataModel.Singletons.Log.log2File;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.ERROR;

/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/7/12
 * Time: 6:05 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ApiHandler implements IApiHandler
{
    protected String _createJson(Map<String, Object> jsonMap)
    {
        // Default error response on JSON creation fail
        String response = "{\"error\":\"Error creating JSON\"}";

        // Create the map if it doesn't exist so we can send a default response
        if (jsonMap == null)
        {
            jsonMap = new HashMap<String, Object>();
        }

        // Add null error field if no error
        if (!jsonMap.containsKey("error"))
        {
            jsonMap.put("error", null);
        }

        // Create the JSON
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(jsonMap);
        } catch (IOException e) {
            log2File(ERROR, e);
        }

        return response;
    }

    protected String _defaultResponse()
    {
        return "{\"error\":null}";
    }

    protected String _invalidApiResponse()
    {
        return "{\"error\":\"Invalid API call\"}";
    }

    protected String _errorResponse(String error)
    {
        return "{\"error\":\"" + error.replace("\"", "'") + "\"}";
    }
}
