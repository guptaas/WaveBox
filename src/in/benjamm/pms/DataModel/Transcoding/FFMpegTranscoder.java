package in.benjamm.pms.DataModel.Transcoding;

import in.benjamm.pms.DataModel.Model.MediaItem;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/25/12
 * Time: 9:52 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class FFMpegTranscoder extends Transcoder
{
    public FFMpegTranscoder(MediaItem item, TranscodeQuality quality)
    {
        super(item, quality);
    }

    private String _ffmpegPath()
    {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.equals("linux") || osName.equals("mac os x"))
        {
            return "ffmpeg";
        }
        else if (osName.startsWith("windows"))
        {
            return "ffmpeg.exe";
        }
        else
        {
            throw new RuntimeException("Unsupported OS : " + osName);
        }
    }

    protected abstract String _ffmpegOptions();

    protected String _ffmpegOptionsWith(String codec, int qualityLevel)
    {
        return "-i " + getMediaItem().filePath() + " -acodec " + codec + " -aq " + qualityLevel + " " + getOutputPath();
    }

    public String command()
    {
        return _ffmpegPath() + " some params here";
    }
}
