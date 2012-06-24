package in.benjamm.pms.DataModel.Singletons;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/24/12
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public enum StatsType
{
    SONG_PLAY(1), ALBUM_PLAY(2), ARTIST_PLAY(3);

    private int _typeId;
    public int getTypeId() { return _typeId; }

    StatsType(int typeId)
    {
        _typeId = typeId;
    }
}
