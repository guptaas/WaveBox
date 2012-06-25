package in.benjamm.pms.ApiHandler.Handlers;

import in.benjamm.pms.ApiHandler.ApiHandler;
import in.benjamm.pms.ApiHandler.UriWrapper;
import in.benjamm.pms.DataModel.Model.Song;
import in.benjamm.pms.DataModel.Singletons.Stats;
import in.benjamm.pms.DataModel.Singletons.StatsType;
import in.benjamm.pms.HttpServer.HttpServerHandler;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;
import java.util.List;
import java.util.Map;

import static in.benjamm.pms.DataModel.Singletons.Log.*;
import static in.benjamm.pms.DataModel.Singletons.LogLevel.*;


/**
 * Created by IntelliJ IDEA.
 * User: bbaron
 * Date: 1/7/12
 * Time: 6:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class StreamApiHandler extends ApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;
    private Map<String, String> _headers;
    private int _userId;

    public StreamApiHandler(UriWrapper uri, Map<String, List<String>> parameters, Map<String, String> headers, HttpServerHandler sh, int userId)
    {
        _uri = uri;
        _parameters = parameters;
        _headers = headers;
        _sh = sh;
        _userId = userId;
    }

    public void process()
    {
        // Open the file on disk
        Song song = null;
        File file = null;
        try {
            if (_uri.getUriPart(2) != null)
            {
                int songId = Integer.parseInt(_uri.getUriPart(2));
                song = new Song(songId);
                file = song.file();
            }
        } catch (NumberFormatException e) {
            log2File(ERROR, e);
            _sh.sendError(HttpResponseStatus.NOT_FOUND);
            return;
        }

        // Determine the file offset
        long offset = 0;
        if (_headers.containsKey("Range"))
        {
            try {
                String range = _headers.get("Range");
                String[] ranges = range.substring("bytes=".length()).split("-");
                offset = Long.valueOf(ranges[0]);
            } catch (NumberFormatException e) {
                offset = 0;
                log2File(ERROR, e);
            }
        }

        // Log the action
        Stats.recordStat(StatsType.SONG_PLAY, song.getItemId(), _userId);
        if (song.getAlbumId() != null)
            Stats.recordStat(StatsType.ALBUM_PLAY, song.getAlbumId(), _userId);
        if (song.getArtistId() != null)
            Stats.recordStat(StatsType.ARTIST_PLAY, song.getAlbumId(), _userId);

        // Send the file to the client
        _sh.sendFile(file, offset);
    }
}