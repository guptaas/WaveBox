package in.benjamm.pms.DataModel.Transcoding;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 7/10/12
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITranscoderDelegate
{
    public void transcodeFinished(ITranscoder transcoder);
    public void transcodeFailed(ITranscoder transcoder);
}
