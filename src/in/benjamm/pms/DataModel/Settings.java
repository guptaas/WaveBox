package in.benjamm.pms.DataModel;

import java.io.File;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/7/12
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Settings
{
    public static final String databasePath = "/tmp/pms.db";

    public static Connection getDbConnection()
    {
        Connection conn = null;
        try
        {
            // load the sqlite-JDBC driver using the current class loader
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            return conn;
        }
    }

    public static Statement getDbStatement()
    {
        Statement statement = null;
        try
        {
            Connection conn = getDbConnection();
            statement = conn.createStatement();
            statement.setQueryTimeout(30);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            return statement;
        }
    }

    public static List<String> mediaFolders()
    {
        // Hard coded for testing
        ArrayList<String> folders = new ArrayList<String>();
        folders.add("/Users/bbaron/Music/subsonic-test");
        return folders;
    }
}
