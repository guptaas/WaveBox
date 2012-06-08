package in.benjamm.pms;
/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import in.benjamm.pms.DataModel.Settings;
import in.benjamm.pms.Netty.HttpServerPipelineFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 */
public class Main
{
    public static void main(String[] args)
	{
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpServerPipelineFactory());

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(8080));
    }

    /**
     * Copy the database from the jar if necessary
     */
    private static void databaseSetup
    {
        File dbFile = new File(Settings.databasePath);
        if (!dbFile.exists())
        {
            try
            {
                InputStream inStream = getClass().getResourceAsStream("/res/pms.db");
                File outFile = new File(Settings.databasePath);
                OutputStream outStream = new FileOutputStream(outFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = inStream.read(buf)) > 0)
                {
                    outStream.write(buf, 0, len);
                }
                inStream.close();
                outStream.close();
            }
            catch(FileNotFoundException ex)
            {
                System.out.println(ex.getMessage() + " in the specified directory.");
                System.exit(0);
            }
            catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }
}
