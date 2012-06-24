package in.benjamm.pms.DataModel.Model;

import in.benjamm.pms.DataModel.Singletons.Database;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.images.Artwork;

import java.io.*;
import java.sql.*;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

import static in.benjamm.pms.DataModel.Singletons.Log.*;


/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/10/12
 * Time: 8:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoverArt
{
    public static final String ART_PATH = "/tmp/pms/art/";
    public static final String TMP_ART_PATH = "/tmp/pms/art/tmp/";

    /*
     * Properties
     */

    /**
     * The ID of this art
     */
    private Integer _artId;
    public Integer getArtId() { return _artId; }
    public void setArtId(Integer artId) { _artId = artId; }

    /**
     * The Adler32 hash of this art
     */
    private Long _adlerHash;
    public Long getAdlerHash() { return _adlerHash; }
    public void setAdlerHash(Long adlerHash) { _adlerHash = adlerHash; }

    /**
     * The file object for this art
     */
    public File artFile()
    {
        return new File(ART_PATH + getAdlerHash());
    }



    /*
     * Constructor(s)
     */

    public CoverArt()
    {

    }

    public CoverArt (int artId)
    {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT * FROM art WHERE art_id = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, artId);
            r = s.executeQuery();
            if (r.next())
            {
                // The art is already in the database
                _artId = r.getInt("art_id");
                _adlerHash = r.getLong("adler_hash");
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }
    }

    public CoverArt(AudioFile af)
    {
        Artwork art = null;
        try {
            art = af.getTag().getFirstArtwork();
        } catch (NullPointerException e) {
            art = null;
        }

        if (art != null)
        {
            byte bytes[] = art.getBinaryData();
            Checksum checksum = new Adler32();
            checksum.update(bytes, 0, bytes.length);
            _adlerHash = checksum.getValue();

            String query;
            Connection c = null;
            PreparedStatement s = null;
            ResultSet r = null;

            try {
                c = Database.getDbConnection();

                query = "SELECT * FROM art WHERE adler_hash = ?";
                s = c.prepareStatement(query);
                s.setObject(1, _adlerHash);
                r = s.executeQuery();
                if (r.next())
                {
                    // The art is already in the database
                    _artId = r.getInt("art_id");
                    Database.close(null, s, r);
                }
                else
                {
                    Database.close(null, s, r);

                    // The art isn't in the database
                    // Save the art to disk
                    try {
                        OutputStream outStream = new FileOutputStream(ART_PATH + _adlerHash);
                        outStream.write(bytes, 0, bytes.length);
                        outStream.close();
                    } catch (FileNotFoundException e) {
                        log2File(ERROR, e);
                    } catch (IOException e) {
                        log2File(ERROR, e);
                    }

                    // Insert the record
                    query = "INSERT INTO art (adler_hash) VALUES (?)";
                    s = c.prepareStatement(query);
                    s.setLong(1, _adlerHash);
                    s.executeUpdate();

                    // Pull the art id
                    r = s.getGeneratedKeys();
                    if (r.next())
                    {
                        _artId = r.getInt(1);
                    }
                }
            } catch (SQLException e) {
                log2File(ERROR, e);
            } finally {
                Database.close(c, s, r);
            }
        }
    }



    /*
     * Private methods
     */




    /*
     * Public methods
     */


}
