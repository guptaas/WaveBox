package in.benjamm.pms.DataModel;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/17/12
 * Time: 11:12 AM
 * To change this template use File | Settings | File Templates.
 */
public enum ItemType
{
    ARTIST(1),
    ALBUM(2),
    SONG(3),
    VIDEO(4),
    UNKNOWN(-1);

    private int _itemTypeId;
    public int getItemTypeId() { return _itemTypeId; }

    ItemType(int itemTypeId)
    {
        _itemTypeId = itemTypeId;
    }

    public static ItemType itemTypeForId(int itemTypeId)
    {
        for (ItemType type : ItemType.values())
        {
            if (type.getItemTypeId() == itemTypeId)
            {
                return type;
            }
        }
        return UNKNOWN;
    }
}
