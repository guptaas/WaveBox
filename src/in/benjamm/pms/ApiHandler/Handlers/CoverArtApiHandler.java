package in.benjamm.pms.ApiHandler.Handlers;

import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;
import in.benjamm.pms.ApiHandler.HelperObjects.UriWrapper;
import in.benjamm.pms.ApiHandler.IApiHandler;
import in.benjamm.pms.DataModel.CoverArt;
import in.benjamm.pms.Netty.HttpServerHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/12/12
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoverArtApiHandler implements IApiHandler
{
    private UriWrapper _uri;
    private Map<String, List<String>> _parameters;
    private HttpServerHandler _sh;

    public CoverArtApiHandler(UriWrapper uri, Map<String, List<String>> parameters, HttpServerHandler sh)
    {
        _uri = uri;
        _parameters = parameters;
        _sh = sh;
    }

    public void process()
    {
        if (_uri.getUriPart(2) != null)
        {
            try {
                int artId = Integer.parseInt(_uri.getUriPart(2));
                CoverArt art = new CoverArt(artId);

                if (_parameters.containsKey("size"))
                {
                    // Resize the image
                    try {
                        int size = Integer.parseInt(_parameters.get("size").get(0));
                        try {
                            BufferedImage image = ImageIO.read(art.artFile());

                            ResampleOp resize = new ResampleOp(DimensionConstrain.createMaxDimension(size, size));
                            resize.setFilter(ResampleFilters.getLanczos3Filter());
                            image = resize.filter(image, null);

                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            ImageIO.write(image, "jpg", out);
                            byte[] bytesOut = out.toByteArray();

                            _sh.sendFile(bytesOut);
                        } catch (IOException e) {
                            _sh.sendJson("{\"error\":\"Couldn't open file\"}");
                        }
                    } catch (NumberFormatException e) {
                        // Send the original file
                        _sh.sendFile(new CoverArt(artId).artFile(), 0);
                    }
                }
                else
                {
                    // Send the original file
                    _sh.sendFile(art.artFile(), 0);
                }
            } catch (NumberFormatException e) {
                _sh.sendJson("{\"error\":\"Invalid API call\"}");
            }
        }
        else
        {
            _sh.sendJson("{\"error\":\"Invalid API call\"}");
        }
    }
}
