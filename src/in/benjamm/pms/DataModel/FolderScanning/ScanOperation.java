package in.benjamm.pms.DataModel.FolderScanning;

import java.util.concurrent.*;

import static in.benjamm.pms.DataModel.Singletons.Log.*;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/17/12
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ScanOperation implements Delayed, Runnable
{
    private long _executionTime;
    private TimeUnit _unit = TimeUnit.NANOSECONDS;

    private int _secondsDelay;

    private boolean _isRestart;
    public synchronized boolean isRestart() { return _isRestart; }
    public synchronized void setIsRestart(boolean isRestart) { _isRestart = isRestart; }

    /////////////////////////////////////// Database Performance Tests /////////////////////////////////////////////////
    // SQLITE-JDBC /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Just use 2 threads (actually 3 because using CallerRunsPolicy so main thread is used as well)                  //
    // using more is actually slower, assuming because of db locking (tho should all be read locks, so not sure)      //
    // Tests re-scanning 30K songs: single threaded - 977 seconds, 1 - 605, 2 - 455, 3 - 477, 4 - 793                 //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // H2-DATABASE//////////////////////////////////////////////////////////////////////////////////////////////////////
    // H2 DB with proper index for song update checking:                                                              //
    // Fresh scan 30K songs: single t'd - 325 seconds, 2 - 288, 4 - 266/255, 8 - 247, 16 - 241                        //
    // Rescan 30K songs: single threaded - 6 seconds, 2 - 5, 4 - 5, 8 - 5                                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //private int _processors = Runtime.getRuntime().availableProcessors();
    //private int _numThreads = _processors * 2;
    private int _numThreads = 4; // 4 threads is optimal for H2 database
    private BlockingQueue<Runnable> _blockingQueue = new ArrayBlockingQueue<Runnable>(_numThreads);
    private RejectedExecutionHandler _rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
    private ExecutorService _executorService =  new ThreadPoolExecutor(_numThreads, _numThreads, 0L, TimeUnit.MILLISECONDS, _blockingQueue, _rejectedExecutionHandler);

    public ScanOperation(int secondsDelay)
    {
        _secondsDelay = secondsDelay;
        resetDelay();
        log2Out(TEST, "number of threads: " + _numThreads);
    }

    public void resetDelay()
    {
        _executionTime = System.nanoTime() + _unit.convert(_secondsDelay, TimeUnit.SECONDS);
    }

    public void addDelay(int secondsDelay)
    {
        _executionTime += System.nanoTime() + _unit.convert(secondsDelay, TimeUnit.SECONDS);
    }

    public long getDelay(TimeUnit timeUnit)
    {
        long delay = _executionTime - System.nanoTime();
        return timeUnit.convert(delay, _unit);
    }

    public int compareTo(Delayed otherOperation)
    {
        return (int)(getDelay(_unit) - otherOperation.getDelay(_unit));
    }

    public void run()
    {
        do
        {
            setIsRestart(false);

            long startTime = System.currentTimeMillis();
            start();

            long runTime = (System.currentTimeMillis() - startTime) / 1000;
            log2Out(TEST, "scanned in " + runTime + " seconds");
        }
        while (isRestart());
    }

    public abstract void start();

    public void restart()
    {
        log2Out(TEST, "Restarting scan");
        setIsRestart(true);
    }

    public void submitTask(Runnable r)
    {
        _executorService.submit(r);
    }

    public abstract int hashCode();

    public abstract boolean equals(Object obj);

}
