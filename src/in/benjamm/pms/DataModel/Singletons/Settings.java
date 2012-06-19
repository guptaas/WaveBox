package in.benjamm.pms.DataModel.Singletons;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.DataModel.Model.Folder;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/18/12
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class Settings
{
    public static final String SETTINGS_PATH = "pms.conf";

    static
    {
        // Load initial settings
        reload();
    }

    private static void _parseSettings()
    {
        File settingsFile = new File(SETTINGS_PATH);
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getJsonFactory();
        JsonParser jp = null;
        try {
            jp = factory.createJsonParser(settingsFile);
            JsonNode actualObj = mapper.readTree(jp);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public static void reload()
    {
        _parseSettings();
    }

    /*
     * Settings
     */

    public static List<Folder> mediaFolders()
    {
        List<Folder> folders = new ArrayList<Folder>();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        boolean retry = false;
        do
        {
            try {
                String query = "SELECT * FROM folder WHERE parent_folder_id IS NULL";
                c = Database.getDbConnection();
                s = c.prepareStatement(query);
                r = s.executeQuery();
                while (r.next())
                {
                    folders.add(new Folder(r.getInt("folder_id")));
                }
            } catch (SQLException e) {
                //System.out.println("TABLE LOCKED, RETRYING QUERY");
                e.printStackTrace();
                //retry = true;
            } finally {
                Database.close(c, s, r);
            }
        }
        while (retry);

        return folders;
    }
}

