package in.benjamm.pms.DataModel.Model;

import in.benjamm.pms.DataModel.Singletons.Database;
import org.jboss.netty.util.internal.ReusableIterator;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/19/12
 * Time: 7:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class User
{
    private Integer _userId;
    public Integer getUserId() { return _userId; }
    public void setUserId(Integer userId) { _userId = userId; }

    private String _userName;
    public String getUserName() { return _userName; }
    public void setUserName(String userName) { _userName = userName; }

    private String _passwordHash;
    public String getPasswordHash() { return _passwordHash; }
    public void setPasswordHash(String passwordHash) { _passwordHash = passwordHash; }

    private String _passwordSalt;
    public String getPasswordSalt() { return _passwordSalt; }
    public void setPasswordSalt(String passwordSalt) { _passwordSalt = passwordSalt; }

    public User()
    {

    }

    public User(int userId)
    {
        _userId = userId;

        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT * FROM user WHERE user_id = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, userId);
            r = s.executeQuery();
            if (r.next())
            {
                _setPropertiesFromResultSet(r);
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }
    }

    public User(String userName)
    {
        _userName = userName;

        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            String query = "SELECT * FROM user WHERE user_name = ?";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, userName);
            r = s.executeQuery();
            if (r.next())
            {
                _setPropertiesFromResultSet(r);
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s, r);
        }
    }

    private void _setPropertiesFromResultSet(ResultSet r)
    {
        try {
            setUserId(r.getInt("user_id"));
            setUserName(r.getString("user_name"));
            setPasswordHash(r.getString("password_hash"));
            setPasswordSalt(r.getString("password_salt"));
        } catch (SQLException e) {
            log2File(ERROR, e);
        }
    }

    private static String _sha1(String input)
    {
        String sha1 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.reset();
            md.update(input.getBytes("utf8"));
            sha1 = DatatypeConverter.printHexBinary(md.digest());
        } catch(UnsupportedEncodingException e) {
            log2File(ERROR, e);
        } catch(NoSuchAlgorithmException e) {
            log2File(ERROR, e);
        }

        return sha1;
    }

    private static String _calculatePasswordHash(String password, String salt)
    {
        // Hash the password + salt, then 50 passes of hash + salt
        long startTime = System.currentTimeMillis();

        String hash = _sha1(password + salt);
        for (int i = 0; i < 50; i++)
        {
            hash = _sha1(hash + salt);
        }

        log2Out(TEST, "hash calculated in " + (System.currentTimeMillis() - startTime) + "ms");

        return hash;
    }

    private static String _generatePasswordSalt()
    {
        // Use the SHA-1 hash of the current time
        return _sha1(String.valueOf(System.currentTimeMillis()));
    }

    public void updatePassword(String password)
    {
        Connection c = null;
        PreparedStatement s = null;

        String salt = _generatePasswordSalt();
        String hash = _calculatePasswordHash(password, salt);

        try {
            String query = "UPDATE user (password_hash, password_salt) VALUES (?, ?)";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setObject(1, hash);
            s.setObject(2, salt);
            s.executeUpdate();
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s);
        }

        setPasswordHash(hash);
        setPasswordSalt(salt);
    }

    public boolean authenticate(String password)
    {
        String hash = _calculatePasswordHash(password, getPasswordSalt());

        return hash.equals(getPasswordHash());
    }

    public static User createUser(String userName, String password)
    {
        User user = null;

        String salt = _generatePasswordSalt();
        String hash = _calculatePasswordHash(password, salt);

        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        try {
            String query = "INSERT INTO user VALUES (?, ?, ?, ?)";
            c = Database.getDbConnection();
            s = c.prepareStatement(query);
            s.setNull(1, Types.INTEGER);
            s.setObject(2, userName);
            s.setObject(3, hash);
            s.setObject(4, salt);
            s.executeUpdate();

            r = s.getGeneratedKeys();
            if (r.next())
            {
                Integer userId = r.getInt(1);
                user = new User(userId);
            }
        } catch (SQLException e) {
            log2File(ERROR, e);
        } finally {
            Database.close(c, s);
        }

        return user;
    }
}
