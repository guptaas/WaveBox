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

import in.benjamm.pms.DataModel.Singletons.Database;
import in.benjamm.pms.DataModel.Singletons.FileManager;
import in.benjamm.pms.DataModel.Singletons.Log;
import in.benjamm.pms.DataModel.Singletons.Settings;
import in.benjamm.pms.HttpServer.HttpServer;

import static in.benjamm.pms.DataModel.Singletons.Log.*;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 */
public class Main
{
    public static void main(String[] args)
	{
        // Load settings
        Settings.reload();

        // Register a shutdown hook for resource cleanup
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                log2Out(TEST, "Shutdown hook started!");
                Database.shutdownPool();
                Log.cleanup();
                log2Out(TEST, "Shutdown hook finished!");
            }
        });

        // Start the HTTP server
        HttpServer server = new HttpServer(8080);
        server.bootstrap();

        // Initialize the FileManager
        FileManager.sharedInstance();
    }
}
