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

import in.benjamm.pms.DataModel.Database;
import in.benjamm.pms.DataModel.FileManager;
import in.benjamm.pms.DataModel.Folder;
import in.benjamm.pms.Netty.HttpServer;
import in.benjamm.pms.Netty.HttpServerPipelineFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 */
public class Main
{
    public static void main(String[] args)
	{
        // Register a shutdown hook for resource cleanup
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("Shutdown hook started!");
                Database.shutdownPool();
                System.out.println("Shutdown hook finished!");
            }
        });

        HttpServer server = new HttpServer(8080);
        server.bootstrap();

        /*System.out.println("Press any key to scan files");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String userName = null;
        try {
            userName = br.readLine();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        System.out.println("Scanning files");*/

        // Scan the files
        Thread t = new Thread()
        {
            public void run()
            {
                long startTime = System.currentTimeMillis();
                FileManager.sharedInstance().scanFolder(Folder.mediaFolders().get(0).getFolderPath());
                long runTime = (System.currentTimeMillis() - startTime) / 1000;
                System.out.println("All files scanned! It took " + runTime + " seconds");
            }
        };
        t.start();
    }
}
