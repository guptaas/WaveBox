package in.benjamm.pms.DataModel.FolderScanning;

import java.util.Iterator;
import java.util.concurrent.DelayQueue;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 7/10/12
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class SerialOperationQueue
{
    public static int DEFAULT_DELAY = 10;

    private boolean _queueShouldLoop = true;
    private synchronized boolean _getQueueShouldLoop() { return _queueShouldLoop; }
    private synchronized void _setQueueShouldLoop(boolean queueShouldLoop) { _queueShouldLoop = queueShouldLoop; }

    private Thread _queueThread;
    private Object _queueSyncObject = new Object();
    private DelayQueue<IOperation> _queue = new DelayQueue<IOperation>();
    private IOperation _currentOperation;

    public void startQueue()
    {
        _queueThread = new Thread(new Runnable()
        {
            public void run()
            {
                while (_getQueueShouldLoop())
                {
                    // Scan the next folder
                    synchronized (_queueSyncObject)
                    {
                        _currentOperation = _queue.poll();
                    }

                    if (_currentOperation != null)
                    {
                        _currentOperation.run(); // This is blocking
                    }

                    // Sleep for a quarter of a second
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {}
                }
            }
        });
        _queueThread.start();
    }

    public void stopQueue()
    {
        _setQueueShouldLoop(false);
        _queueThread.interrupt();
        _queue.clear();
    }

    public void queueOperation(IOperation operation)
    {
        synchronized (_queueSyncObject)
        {
            if (operation.equals(_currentOperation))
            {
                // This folder has changed while it's being scanned so restart the scan
                operation.restart();
            }
            else if (_queue.contains(operation))
            {
                // This operation is already queued, so add to it's delay
                Iterator itr = _queue.iterator();
                while(itr.hasNext())
                {
                    AbstractOperation existingOperation = (AbstractOperation)itr.next();
                    existingOperation.resetDelay();
                }
            }
            else
            {
                // Not queued yet, so add it
                _queue.add(operation);
            }
        }
    }
}
