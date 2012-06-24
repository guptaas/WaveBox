package in.benjamm.pms.DataModel.Singletons;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/24/12
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */
public enum LogLevel
{
    CRITICAL(4), ERROR(3), INFO(2), TEST(1);

    private int _level;
    public int getLevel() { return _level; }

    LogLevel(int level)
    {
        _level = level;
    }

    public boolean displayLog(int level)
    {
        // Display only all logs with level greater than the out log level
        return getLevel() > level;
    }
}
