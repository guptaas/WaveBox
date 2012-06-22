//
//  MediaItem.h
//  PersonalMediaServer
//
//  Created by Benjamin Baron on 6/21/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MediaItemType.h"
#import "FileType.h"

@class Playlist;
@interface MediaItem : NSObject

@property (nonatomic, readonly) NSInteger itemTypeId;

/**
 * Type of media (song, video, etc)
 */
@property (nonatomic) MediaItemType mediaItemType;

/**
 * Unique identifier
 */
@property (nonatomic) NSInteger itemId;

/**
 * Associated cover art
 */
@property (nonatomic) NSInteger artId;

/**
 * Folder containing this media
 */
@property (nonatomic) NSInteger folderId;

/**
 * Media format (mp3, aac, etc)
 */
@property (nonatomic) FileType fileType;

/**
 * Duration in seconds
 */
@property (nonatomic) NSInteger duration;

/**
 * Bitrate in bits per second
 */
@property (nonatomic) NSInteger bitrate;

/**
 * Size on disk in bytes
 */
@property (nonatomic) unsigned long long fileSize;

/**
 * File's last modified date in Unix timestamp
 */
@property (nonatomic) unsigned long long lastModified;

/**
 * Name of file on disk
 */
@property (nonatomic, strong) NSString *fileName;

@property (nonatomic) NSInteger releaseYear;

/**
 * Add this song to the end of a playlist
 */
- (void)addToPlaylist:(Playlist *)thePlaylist;

/**
 * Insert this song into a playlist at the desired position
 *
 * If index is longer than playlist count, then song is added to end.
 * If index is < 0, the song is inserted at the beginning
 */
- (void)addToPlaylist:(Playlist *)thePlaylist atIndex:(NSInteger)index;

+ (BOOL)fileNeedsUpdating:(NSString *)filePath;

@end
