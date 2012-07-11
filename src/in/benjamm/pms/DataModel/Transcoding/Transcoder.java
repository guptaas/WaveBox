package in.benjamm.pms.DataModel.Transcoding;

import in.benjamm.pms.DataModel.Model.MediaItem;
import in.benjamm.pms.DataModel.Singletons.Log;
import in.benjamm.pms.DataModel.Singletons.LogLevel;
import in.benjamm.pms.DataModel.Singletons.TranscodeManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static in.benjamm.pms.DataModel.Singletons.Log.log2File;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.INFO;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/25/12
 * Time: 9:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Transcoder extends Thread implements ITranscoder
{
    private ITranscoderDelegate _delegate;
    public ITranscoderDelegate getDelegate() { return _delegate; };
    private void setTranscoderDelegate(ITranscoderDelegate delegate) { _delegate = delegate; }

    private Process _process;
    public Process getProcess() { return _process; };
    private void setProcess(Process process) throws IllegalArgumentException
    {
        //_process = process;
        try {
            // test if the process is finished
            process.exitValue();
            throw new IllegalArgumentException("The process is already ended");
        } catch (IllegalThreadStateException exc) {
            _process = process;
        }
    }

    private MediaItem _mediaItem;
    public MediaItem getMediaItem() { return _mediaItem; }

    private TranscodeQuality _quality;
    public  TranscodeQuality getQuality() { return _quality; }

    public abstract TranscodeType getType();

    public String getOutputPath()
    {
        MediaItem item = getMediaItem();
        if (item != null)
        {
            String fileName = item.getItemTypeId() + "_" + item.getItemId() + "_" + getType() + "_" + getQuality();
            String path = TranscodeManager.TRANSCODE_PATH + File.separator + fileName;
            log2File(INFO, "transcoding to " + path);
        }

        return null;
    }

    public Transcoder(MediaItem item, TranscodeQuality quality)
    {
        _mediaItem = item;
        _quality = quality;
    }

    public abstract String command();

    public void run()
    {
        try {
            // wait for the process to finish
            try {
                Runtime run = Runtime.getRuntime();
                setProcess(run.exec(command()));
            } catch (Exception e) {
                // Inform the delegate
                getDelegate().transcodeFailed(this);
            }

            if (getProcess() != null)
            {
                getProcess().waitFor();

                int exitValue = getProcess().exitValue();
                if (exitValue == 0)
                    getDelegate().transcodeFinished(this);
                else
                    getDelegate().transcodeFailed(this);
            }
        } catch (InterruptedException e) { }
    }

    public void cancelTranscode()
    {
        if (getProcess() != null)
        {
            getProcess().destroy();
        }
    }

    public void startTranscode()
    {
        this.start();
    }

    public abstract Integer estimatedBitrate();

    public Long estimatedOutputSize()
    {
        if (getMediaItem() != null)
        {
            return getMediaItem().getDuration() * (long)(estimatedBitrate() / 8);
        }

        return null;
    }
}