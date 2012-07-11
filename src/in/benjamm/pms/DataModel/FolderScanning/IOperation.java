package in.benjamm.pms.DataModel.FolderScanning;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 7/10/12
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IOperation extends Runnable, Delayed
{
    public boolean isRestart();
    public void setIsRestart(boolean isRestart);

    public void resetDelay();
    public void addDelay(int secondsDelay);
    public long getDelay(TimeUnit timeUnit);

    public void start();
    public void restart();

    public void submitAsyncTask(Runnable r);
}
