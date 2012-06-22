//
//  AppDelegate.m
//  PersonalMediaServer
//
//  Created by Benjamin Baron on 5/6/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "AppDelegate.h"
#import "HTTPServer.h"
#import "DDLog.h"
#import "DDTTYLogger.h"
#import "PMSHTTPConnection.h"

// Log levels: off, error, warn, info, verbose
static const int ddLogLevel = LOG_LEVEL_VERBOSE;


@implementation AppDelegate
@synthesize window, httpServer;

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
	// Configure our logging framework.
	// To keep things simple and fast, we're just going to log to the Xcode console.
	[DDLog addLogger:[DDTTYLogger sharedInstance]];
	
	// Initalize our http server
	self.httpServer = [[HTTPServer alloc] init];
	
	// Tell server to use our custom MyHTTPConnection class.
	[httpServer setConnectionClass:[PMSHTTPConnection class]];
	
	// Tell the server to broadcast its presence via Bonjour.
	// This allows browsers such as Safari to automatically discover our service.
	[httpServer setType:@"_http._tcp."];
}

@end