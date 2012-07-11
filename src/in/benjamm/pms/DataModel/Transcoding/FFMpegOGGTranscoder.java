package in.benjamm.pms.DataModel.Transcoding;

import in.benjamm.pms.DataModel.Model.MediaItem;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 7/10/12
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class FFMpegOGGTranscoder extends FFMpegTranscoder
{
    public TranscodeType getType() { return TranscodeType.OGG; }

    public FFMpegOGGTranscoder(MediaItem item, TranscodeQuality quality)
    {
        super(item, quality);
    }

    protected String _ffmpegOptions()
    {
        String codec = "libvorbis";
        String options = null;
        switch (getQuality())
        {
            case LOW:
                // VBR
                options = _ffmpegOptionsWith(codec, 2);
            case MEDIUM:
                // VBR
                options = _ffmpegOptionsWith(codec, 4);
            case HIGH:
                // VBR
                options = _ffmpegOptionsWith(codec, 6);
            case EXTREME:
                // VBR
                options = _ffmpegOptionsWith(codec, 8);
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
