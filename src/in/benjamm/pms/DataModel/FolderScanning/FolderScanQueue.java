package in.benjamm.pms.DataModel.FolderScanning;

import in.benjamm.pms.DataModel.Model.Folder;

import java.util.Iterator;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/17/12
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class FolderScanQueue extends SerialOperationQueue
{
    public void queueFolderScan(String folderPath, int secondsDelay)
    {
        if (folderPath == null)
            return;

        log2Out(TEST, "Queuing folder scan for " + folderPath + " in " + secondsDelay + " seconds");

        FolderScanOperation operation = new FolderScanOperation(folderPath, secondsDelay);
        queueOperation(operation);
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

    public void queueOrphanScan(int secondsDelay)
    {
        OrphanScanOperation operation = new OrphanScanOperation(secondsDelay);
        queueOperation(operation);
    }
}
