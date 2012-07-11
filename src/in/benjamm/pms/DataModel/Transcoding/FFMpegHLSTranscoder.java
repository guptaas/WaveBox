package in.benjamm.pms.DataModel.Transcoding;

import in.benjamm.pms.DataModel.Model.MediaItem;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/25/12
 * Time: 9:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class FFMpegHLSTranscoder extends FFMpegTranscoder
{
    public TranscodeType getType() { return TranscodeType.HLS; }

    public FFMpegHLSTranscoder(MediaItem item, TranscodeQuality quality)
    {
        super(item, quality);
    }

    protected String _ffmpegOptions()
    {
        switch (getQuality())
        {
            case LOW:
            case MEDIUM:
            case HIGH:
            case EXTREME:
        }

        return null;
    }

    public Integer estimatedBitrate()
    {
        return 0;
    }
}
