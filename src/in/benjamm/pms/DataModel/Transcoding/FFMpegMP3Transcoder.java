package in.benjamm.pms.DataModel.Transcoding;

import in.benjamm.pms.DataModel.Model.MediaItem;
import in.benjamm.pms.DataModel.Singletons.TranscodeManager;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 7/10/12
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class FFMpegMP3Transcoder extends FFMpegTranscoder
{
    public TranscodeType getType() { return TranscodeType.MP3; }

    public FFMpegMP3Transcoder(MediaItem item, TranscodeQuality quality)
    {
        super(item, quality);
    }

    protected String _ffmpegOptions()
    {
        String codec = "libmp3lame";
        String options = null;
        switch (getQuality())
        {
            case LOW:
                // VBR - V9 quality (~64 kbps)
                options = _ffmpegOptionsWith(codec, 9);
            case MEDIUM:
                // VBR - V5 quality (~128 kbps)
                options = _ffmpegOptionsWith(codec, 5);
            case HIGH:
                // VBR - V2 quality (~192 kbps)
                options = _ffmpegOptionsWith(codec, 2);
            case EXTREME:
                // VBR - V0 quality (~224 kbps)
                options = _ffmpegOptionsWith(codec, 0);
        }
        return options;
    }

    public Integer estimatedBitrate()
    {
        Integer bitrate = null;
        switch (getQuality())
        {
            case LOW: bitrate = 64;
            case MEDIUM: bitrate = 128;
            case HIGH: bitrate = 192;
            case EXTREME: bitrate = 224;
        }
        return bitrate;
    }
}
