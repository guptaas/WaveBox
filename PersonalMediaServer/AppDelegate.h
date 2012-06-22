//
//  AppDelegate.h
//  PersonalMediaServer
//
//  Created by Benjamin Baron on 5/6/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Cocoa/Cocoa.h>

@class HTTPServer;
@interface AppDelegate : NSObject <NSApplicationDelegate>

@property (assign) IBOutlet NSWindow *window;

@property (strong) HTTPServer *httpServer;

@end