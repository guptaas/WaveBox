package in.benjamm.pms.DataModel.Singletons;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/24/12
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Log
{
    public static final String LOG_PATH = "pms.log";

    public static int outLogLevel = 0; // Log everything
    public static int fileLogLevel = 0; // Log everything


    // Log levels for easy use in other classes
    public static LogLevel CRITICAL = LogLevel.CRITICAL;
    public static LogLevel ERROR = LogLevel.ERROR;
    public static LogLevel INFO = LogLevel.INFO;
    public static LogLevel TEST = LogLevel.TEST;

    private static FileOutputStream _outStream;
    private static OutputStreamWriter _writer;

    static
    {
        try {
            _outStream = new FileOutputStream(LOG_PATH);
        } catch (FileNotFoundException e) {
            try {
                File file = new File(LOG_PATH);
                file.createNewFile();
                _outStream = new FileOutputStream(LOG_PATH);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        try {
            _writer = new OutputStreamWriter(_outStream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            _writer = new OutputStreamWriter(_outStream);
        }
    }

    public static void cleanup()
    {
        if (_writer != null)
        {
            try {
                _writer.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }

        if (_outStream != null)
        {
            try {
                _outStream.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static void log2Out(LogLevel level, String s)
    {
        if (level.displayLog(outLogLevel))
        {
            System.out.println(s);
        }
    }

    public static void log2File(LogLevel level, String s)
    {
        if (level.displayLog(fileLogLevel))
        {
            // Log the string
            try {
                _writer.write(s);
                _writer.flush();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static void log2File(LogLevel level, Throwable t)
    {
        if (level.displayLog(fileLogLevel))
        {
            // Get the stack trace as a string
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            t.printStackTrace(printWriter);
            String s = writer.toString();

            // Log the stack trace string
            log2File(level, s);
        }
    }

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
}
