package in.benjamm.pms.DataModel.FolderScanning;

import in.benjamm.pms.DataModel.Model.Folder;

import java.util.Iterator;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/17/12
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class FolderScanQueue
{
    public static final int DEFAULT_DELAY = 10;

    /**
     * The folder that is currently being scanned. NULL if not scanning
     */
    private String _currentScanningFolder;
    public String getCurrentScanningFolder() { return _currentScanningFolder; }
    public void setCurrentScanningFolder(String currentScanningFolder) { _currentScanningFolder = currentScanningFolder; }

    /**
     * The file that is currently being scanned. NULL if not scanning
     */
    private String _currentScanningFile;
    public String getCurrentScanningFile() { return _currentScanningFile; }
    public void setCurrentScanningFile(String currentScanningFile) { _currentScanningFile = currentScanningFile; }

    private boolean _scanQueueShouldLoop = true;
    private Thread _scanQueueThread;
    private Object _scanQueueSyncObject = new Object();
    private DelayQueue<FolderScanOperation> _scanQueue = new DelayQueue<FolderScanOperation>();
    private FolderScanOperation _currentOperation;

    public void startScanQueue()
    {
        System.out.println("Starting scan queue");
        _scanQueueThread = new Thread(new Runnable() {
            public void run()
            {
                System.out.println("Starting scan thread");
                while (_scanQueueShouldLoop)
                {
                    // Scan the next folder
                    synchronized (_scanQueueSyncObject)
                    {
                        System.out.println("Entering sync and getting operation");
                        _currentOperation = _scanQueue.poll();
                    }

                    if (_currentOperation != null)
                    {
                        System.out.println("Running operation");
                        _currentOperation.run(); // This is blocking
                    }

                    // Sleep for a second
                    try {
                        System.out.println("Scan thread sleeping");
                        Thread.sleep(TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS));
                    } catch (InterruptedException e) {}
                }
            }
        });
        _scanQueueThread.start();
    }

    public void stopScanQueue()
    {
        _scanQueueShouldLoop = false;
        _scanQueueThread.interrupt();
        _scanQueue.clear();
    }

    public void queueFolderScan(String folderPath, int secondsDelay)
    {
        if (folderPath == null)
            return;

        FolderScanOperation operation = new FolderScanOperation(folderPath, secondsDelay);
        synchronized (_scanQueueSyncObject)
        {
            if (operation.equals(_currentOperation))
            {
                // This folder has changed while it's being scanned so restart the scan
                operation.restart();
            }
            else if (_scanQueue.contains(operation))
            {
                // This operation is already queued, so add to it's delay
                Iterator itr = _scanQueue.iterator();
                while(itr.hasNext())
                {
                    FolderScanOperation existingOperation = (FolderScanOperation)itr.next();
                    existingOperation.resetDelay();
                }
            }
            else
            {
                // Not queued yet, so add it
                _scanQueue.add(operation);
            }
        }
    }

    public void queueFolderScan(Folder folder, int secondsDelay)
    {
        queueFolderScan(folder.getFolderPath(), secondsDelay);
    }

    public void queueFolderScan(int folderId, int secondsDelay)
    {
        Folder folder = new Folder(folderId);
        queueFolderScan(folder, secondsDelay);
    }
}
