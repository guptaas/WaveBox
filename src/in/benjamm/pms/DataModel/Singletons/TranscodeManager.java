package in.benjamm.pms.DataModel.Singletons;

import in.benjamm.pms.DataModel.Model.MediaItem;
import in.benjamm.pms.DataModel.Transcoding.ITranscoder;
import in.benjamm.pms.DataModel.Transcoding.ITranscoderDelegate;
import in.benjamm.pms.DataModel.Transcoding.TranscodeQuality;
import in.benjamm.pms.DataModel.Transcoding.TranscodeType;
import in.benjamm.pms.DataModel.Transcoding.TranscoderFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 7/10/12
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class TranscodeManager implements ITranscoderDelegate
{
    public static final String TRANSCODE_PATH = "trans";

    private TranscodeManager _sharedInstance = new TranscodeManager();
    public TranscodeManager sharedInstance() { return _sharedInstance; }

    private List<ITranscoder> _activeTranscoders = new ArrayList<ITranscoder>();
    private Map<ITranscoder, Integer> _activeTranscoderCounts = new HashMap<ITranscoder, Integer>();

    private TranscodeManager()
    {

    }

    public ITranscoder transcodeItem(MediaItem item, TranscodeType type, TranscodeQuality quality)
    {
        ITranscoder activeTranscoder = null;
        for (ITranscoder transcoder : _activeTranscoders)
        {
            if (transcoder.getMediaItem() == item && transcoder.getType() == type && transcoder.getQuality() == quality)
            {
                activeTranscoder = transcoder;
                break;
            }
        }

        // No active transcoders found, so create one
        if (activeTranscoder == null)
        {
            activeTranscoder = TranscoderFactory.createTranscoder(item, type, quality);

            if (activeTranscoder != null)
            {
                _activeTranscoders.add(activeTranscoder);
                _activeTranscoderCounts.put(activeTranscoder, 1);
                activeTranscoder.startTranscode();
            }
        }
        else
        {
            Integer currentCount = _activeTranscoderCounts.get(activeTranscoder);
            _activeTranscoderCounts.put(activeTranscoder, currentCount+1);
        }

        return activeTranscoder;
    }

    public void consumedTranscode(ITranscoder transcoder)
    {
        Integer currentCount = _activeTranscoderCounts.get(transcoder);
        if (currentCount != null)
        {
            if (currentCount == 1)
            {
                // This is the last use of this transcoder so remove it
                _activeTranscoderCounts.remove(transcoder);
                _activeTranscoders.remove(transcoder);
            }
            else
            {
                // This transcoder is still being consumed by other streams
                _activeTranscoderCounts.put(transcoder, currentCount-1);
            }
        }
    }

    public void cancelTranscode(ITranscoder transcoder)
    {
        Integer currentCount = _activeTranscoderCounts.get(transcoder);
        if (currentCount != null)
        {
            if (currentCount == 1)
            {
                // This is the last use of this transcoder so cancel it
                transcoder.cancelTranscode();
                _activeTranscoderCounts.remove(transcoder);
                _activeTranscoders.remove(transcoder);
            }
            else
            {
                // This transcoder is still being consumed by other streams, so just decrement the count
                _activeTranscoderCounts.put(transcoder, currentCount-1);
            }
        }
        else
        {
            // This should never happen, but just cancel the transcoder
            transcoder.cancelTranscode();
            _activeTranscoders.remove(transcoder);
        }
    }

    /*
     * Transcoder delegate
     */

    public void transcodeFinished(ITranscoder transcoder)
    {
        // Do something
    }

    public void transcodeFailed(ITranscoder transcoder)
    {
        // Do something
    }
}
