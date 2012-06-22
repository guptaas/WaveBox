//
//  MediaItem.m
//  PersonalMediaServer
//
//  Created by Benjamin Baron on 6/21/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "MediaItem.h"
#import "Playlist.h"

@implementation MediaItem
@synthesize itemTypeId, mediaItemType, itemId, artId, folderId, fileType, duration, bitrate, fileSize, lastModified, fileName, releaseYear;

/**
 * Add this song to the end of a playlist
 */
- (void)addToPlaylist:(Playlist *)thePlaylist
{
	
}

/**
 * Insert this song into a playlist at the desired position
 *
 * If index is longer than playlist count, then song is added to end.
 * If index is < 0, the song is inserted at the beginning
 */
- (void)addToPlaylist:(Playlist *)thePlaylist atIndex:(NSInteger)index
{
	
}

+ (BOOL)fileNeedsUpdating:(NSString *)filePath
{
	return NO;
}

@end
