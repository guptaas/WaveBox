package in.benjamm.pms.DataModel.Singletons;

import java.sql.*;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/24/12
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class Stats
{
    public static void recordStat(StatsType type, Integer itemId, int userId)
    {
        Connection c = null;
        PreparedStatement s = null;

        try {
            String query = "INSERT INTO stats_record VALUES (?, ?, ?, ?, ?)";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setNull(1, Types.INTEGER);
            s.setInt(2, type.getTypeId());
            s.setObject(3, itemId);
            s.setInt(4, userId);
            s.setLong(5, System.currentTimeMillis());
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s);
        }
    }
}
