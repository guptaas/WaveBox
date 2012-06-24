package in.benjamm.pms.DataModel.Singletons;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import in.benjamm.pms.DataModel.Model.CoverArt;

import java.io.*;
import java.sql.*;

import static in.benjamm.pms.DataModel.Singletons.Log.*;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/10/12
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class Database
{
    //public static final String DATABASE_PATH = "pms.db";
    public static final String DATABASE_PATH = "pms";
    public static final String DATABASE_EXT = ".h2.db";

    private static BoneCP _connectionPool = null;

    static
    {
        // Make sure the DB file exists on disk
        _databaseFileSetup();

        // Create the connection pool
        try {
            // load the database driver (make sure this is in your classpath!)
            Class.forName("org.sqlite.JDBC");
            //Class.forName("org.h2.Driver");

            // setup the connection pool
            final BoneCPConfig config = new BoneCPConfig();
            //config.setJdbcUrl("jdbc:sqlite:" + DATABASE_PATH);
            config.setJdbcUrl("jdbc:h2:file:" + DATABASE_PATH + ";IFEXISTS=TRUE;FILE_LOCK=SOCKET");
            config.setUsername("pms");
            config.setPassword("pms");
            config.setMinConnectionsPerPartition(5);
            config.setMaxConnectionsPerPartition(100);
            config.setPartitionCount(1);
            //config.setCloseConnectionWatch(true); // Only enable for debugging orphaned connections, creates tons of threads
            _connectionPool = new BoneCP(config); // Just create default connections
            /*_connectionPool = new BoneCP(config) // Create custom connections
            {
                @Override
                protected Connection obtainRawInternalConnection() throws SQLException
                {
                    SQLiteConfig liteConfig = new SQLiteConfig();
                    liteConfig.setOpenMode(SQLiteOpenMode.NOMUTEX);
                    liteConfig.setTempStore(SQLiteConfig.TempStore.MEMORY);
                    return DriverManager.getConnection(config.getJdbcUrl(), liteConfig.toProperties());
                }
            };*/
        } catch (ClassNotFoundException e) {
            log2File(ERROR, e);
        } catch (SQLException e) {
            log2File(ERROR, e);
        }
    }

    private static void _databaseFileSetup()
    {
        File dbFile = new File(DATABASE_PATH + DATABASE_EXT);
        log2Out(TEST, "db file: " + DATABASE_PATH + DATABASE_EXT);
        if (!dbFile.exists())
        {
            log2Out(TEST, "doesn't exist, copying");
            try
            {
                InputStream inStream = Database.class.getResourceAsStream("/res/pms.h2.db");
                File outFile = new File(DATABASE_PATH + DATABASE_EXT);
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

        File artFolder = new File(CoverArt.TMP_ART_PATH);
        if (!artFolder.exists())
        {
            artFolder.mkdirs();
        }
    }

    public static Connection getDbConnection() throws SQLException
    {
        //log2Out("Connections used: " + _connectionPool.getTotalLeased() + " / " + _connectionPool.getTotalCreatedConnections());
        return _connectionPool.getConnection();
    }

    public static void shutdownPool()
    {
        _connectionPool.shutdown();
    }

    public static void close(Connection c, Statement s, ResultSet r)
    {
        if (r != null)
        {
            try { r.close(); } catch (SQLException e) { }
        }

        if (s != null)
        {
            try { s.close(); } catch (SQLException e) { }
        }

        if (c != null)
        {
            try { c.close(); } catch (SQLException e) { }
        }
    }

    public static void close(Connection c)
    {
        close(c, null, null);
    }

    public static void close(Statement s)
    {
        close(null, s, null);
    }

    public static void close(ResultSet r)
    {
        close(null, null, r);
    }
}