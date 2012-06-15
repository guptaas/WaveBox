package in.benjamm.pms.Netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: bbaron
 * Date: 6/13/12
 * Time: 8:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpServer
{
    private int _port;

    public HttpServer(int port)
    {
        _port = port;
    }

    public void bootstrap()
    {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        bootstrap.setOption("backlog", 1000);

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpServerPipelineFactory());

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(_port));
    }
}
