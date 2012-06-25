package in.benjamm.pms.HttpServer;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 */
public class HttpServerPipelineFactory implements ChannelPipelineFactory
{
    public ChannelPipeline getPipeline() throws Exception
	{
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

		// Create the decoder and encoder
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

		// Create a connection handler
        pipeline.addLast("handler", new HttpServerHandler());

		// Return the pipeline
        return pipeline;
    }
}