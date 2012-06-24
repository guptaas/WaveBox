package in.benjamm.pms.DataModel.Singletons;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.benjamm.pms.DataModel.Model.Folder;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static in.benjamm.pms.DataModel.Singletons.Log.*;

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

    private static List<Folder> _mediaFolders;
    public static List<Folder> getMediaFolders() { return _mediaFolders; }

    public static void reload()
    {
        _parseSettings();
    }

    private static void _parseSettings()
    {
        JsonNode settingsNode = _settingsNode();
        _mediaFolders = _mediaFolders(settingsNode);
    }

    private static void _databaseFileSetup()
    {
        File dbFile = new File(SETTINGS_PATH);
        if (!dbFile.exists())
        {
            log2Out(TEST, "settings don't exist, copying");
            try
            {
                InputStream inStream = Database.class.getResourceAsStream("/res/pms.conf");
                File outFile = new File(SETTINGS_PATH);
                OutputStream outStream = new FileOutputStream(outFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = inStream.read(buf)) > 0)
                {
                    outStream.write(buf, 0, len);
                }
                inStream.close();
                outStream.close();
            }
            catch(FileNotFoundException ex)
            {
                log2File(ERROR, ex);
                System.exit(0);
            }
            catch(IOException e)
            {
                log2File(ERROR, e);
            }
        }
    }

    private static JsonNode _settingsNode()
    {
        _databaseFileSetup();
        File settingsFile = new File(SETTINGS_PATH);
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getJsonFactory();
        try {
            JsonParser jp = factory.createJsonParser(settingsFile);
            return mapper.readTree(jp);
        } catch (IOException e) {
            log2File(ERROR, e);
            return null;
        }
    }

    /*
     * Settings
     */

    public static List<Folder> _mediaFolders(JsonNode settingsNode)
    {
        log2Out(TEST, "loading media folders");
        List<Folder> folders = new ArrayList<Folder>();

        JsonNode mediaFoldersNode = settingsNode.get("mediaFolders");
        if (mediaFoldersNode != null && mediaFoldersNode.isArray())
        {
            Iterator<JsonNode> iter = mediaFoldersNode.elements();
            while (iter.hasNext())
            {
                JsonNode folderPath = iter.next();
                if (folderPath.isTextual())
                {
                    log2Out(TEST, "media folder: " + folderPath.textValue());
                    Folder mediaFolder = new Folder(folderPath.textValue());
                    if (mediaFolder.getFolderId() == null)
                    {
                        mediaFolder.addToDatabase();
                    }
                    folders.add(mediaFolder);
                }
            }
        }

        return folders;
    }
}

