package in.benjamm.pms.DataModel;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/10/12
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class Database
{
    public static final String DATABASE_PATH = "/tmp/pms.db";

    private static BoneCP _connectionPool = null;

    static
    {
        // Make sure the DB file exists on disk
        _databaseFileSetup();

        // Create the connection pool
        try {
            // load the database driver (make sure this is in your classpath!)
            Class.forName("org.sqlite.JDBC");

            // setup the connection pool
            final BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl("jdbc:sqlite:" + DATABASE_PATH);
            config.setMinConnectionsPerPartition(5);
            config.setMaxConnectionsPerPartition(10);
            config.setPartitionCount(1);
            _connectionPool = new BoneCP(config) // setup the connection pool
            {
                @Override
                protected Connection obtainRawInternalConnection() throws SQLException
                {
                    SQLiteConfig liteConfig = new SQLiteConfig();
                    liteConfig.setOpenMode(SQLiteOpenMode.NOMUTEX);
                    return DriverManager.getConnection(config.getJdbcUrl(), liteConfig.toProperties());
                }
            };
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void _databaseFileSetup()
    {
        File dbFile = new File(DATABASE_PATH);
        if (!dbFile.exists())
        {
            try
            {
                InputStream inStream = Database.class.getResourceAsStream("/res/pms.db");
                File outFile = new File(DATABASE_PATH);
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
                System.out.println(ex.getMessage() + " in the specified directory.");
                System.exit(0);
            }
            catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    public static Connection getDbConnection() throws SQLException
    {
        return _connectionPool.getConnection();
    }

    public static void shutdownPool()
    {
        _connectionPool.shutdown();
    }
}