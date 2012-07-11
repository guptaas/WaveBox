package in.benjamm.pms.DataModel.Transcoding;

import in.benjamm.pms.DataModel.Model.MediaItem;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/25/12
 * Time: 9:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITranscoder
{
    public MediaItem getMediaItem();
    public TranscodeType getType();
    public TranscodeQuality getQuality();

    public String getOutputPath();
    public Integer estimatedBitrate();
    public Long estimatedOutputSize();

    public void startTranscode();
    public void cancelTranscode();
}
