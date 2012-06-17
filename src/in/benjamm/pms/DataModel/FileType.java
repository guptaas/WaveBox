package in.benjamm.pms.DataModel;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 5/9/12
 * Time: 12:03 AM
 * To change this template use File | Settings | File Templates.
 */
public enum FileType
{
    MP3(0, "MPEG Layer III", "MPEG-1 Layer 3"),
	AAC(1, "Advanced Audio Coding", "AAC"),
	OGG(2, "Ogg Vorbis", "OGG"),
	FLAC16(3, "Free Lossless Audio Codec", "FLAC 16 bits"),
    FLAC24(4, "Free Lossless Audio Codec", "FLAC 24 bits"),
	WAV(5, "Waveform Audio File Format", "WAV"),
	AIFF(6, "Audio Interchange File Format", "AIFF"),
	ALAC(7, "Apple Lossless Audio Codec", "Apple Lossless"),
	UNKNOWN(-1, "UNKNOWN", "");

	private int _fileTypeId;
	public int getFileTypeId() { return _fileTypeId; }

	private String _jAudioTaggerFormatString;
	public String getJAudioTaggerFormatString() { return _jAudioTaggerFormatString; }

	private String _longDescription;
	public String getLongDescription() { return _longDescription; }

	FileType(int fileTypeId, String longDescription, String jAudioTaggerFormatString)
	{
		_fileTypeId = fileTypeId;
		_longDescription = longDescription;
		_jAudioTaggerFormatString = jAudioTaggerFormatString;
	}

	public static FileType fileTypeForJAudioTaggerFormatString(String jAudioTaggerFormatString)
	{
        //System.out.println("format: " + jAudioTaggerFormatString);
		for (FileType type : FileType.values())
		{
			if (type.getJAudioTaggerFormatString().equals(jAudioTaggerFormatString))
			{
				return type;
			}
		}
		return UNKNOWN;
	}

    public static FileType fileTypeForId(int fileTypeId)
    {
        for (FileType type : FileType.values())
        {
            if (type.getFileTypeId() == fileTypeId)
            {
                return type;
            }
        }
        return UNKNOWN;
    }
}
