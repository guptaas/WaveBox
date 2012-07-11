package in.benjamm.pms.DataModel.Transcoding;

import in.benjamm.pms.DataModel.Model.MediaItem;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 7/10/12
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class TranscoderFactory
{
    public static ITranscoder createTranscoder(MediaItem item, TranscodeType type, TranscodeQuality quality)
    {
        switch (type)
        {
            case MP3: return new FFMpegMP3Transcoder(item, quality);
            case AAC: return new FFMpegAACTranscoder(item, quality);
            case OGG: return new FFMpegOGGTranscoder(item, quality);
            case HLS: return new FFMpegHLSTranscoder(item, quality);
        }
        return null;
    }
}
