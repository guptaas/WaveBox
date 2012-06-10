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
    MP3(0, "MPEG Layer III", "MP3"),
	AAC(1, "Advanced Audio Coding", "AAC"),
	OGG(2, "Ogg Vorbis", "OGG"),
	FLAC(3, "Free Lossless Audio Codec", "FLAC"),
	WAV(4, "Waveform Audio File Format", "WAV"),
	AIFF(5, "Audio Interchange File Format", "AIFF"),
	ALAC(6, "Apple Lossless Audio Codec", "ALAC"),
	UNKNOWN(-1, "UNKNOWN", "");

	private int _fileTypeId;
	public int fileTypeId() { return _fileTypeId; }

	private String _jAudioTaggerFormatString;
	public String jAudioTaggerFormatString() { return _jAudioTaggerFormatString; }

	private String _longDescription;
	public String longDescription() { return _longDescription; }

	FileType(int fileTypeId, String longDescription, String jAudioTaggerFormatString)
	{
		_fileTypeId = fileTypeId;
		_longDescription = longDescription;
		_jAudioTaggerFormatString = jAudioTaggerFormatString;
	}

	public static FileType fileTypeForJAudioTaggerFormatString(String jAudioTaggerFormatString)
	{
		for (FileType type : FileType.values())
		{
			if (type.jAudioTaggerFormatString().equals(jAudioTaggerFormatString))
			{
				return type;
			}
		}
		return UNKNOWN;
	}
}
