//
//  Album.h
//  PersonalMediaServer
//
//  Created by Benjamin Baron on 6/21/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Artist;
@interface Album : NSObject

@property (nonatomic) NSInteger itemTypeId;

/**
 * Artist object for this album
 */
@property (nonatomic) NSInteger artistId;

/**
 * Unique identifier
 */
@property (nonatomic) NSInteger albumId;

/**
 * Name from tags
 */
@property (nonatomic, strong) NSString *albumName;

/**
 * Four digit release year from tags
 */
@property (nonatomic) NSInteger releaseYear;

/**
 * Associated cover art
 */
@property (nonatomic) NSInteger artId;

- (id)initWithAlbumId:(NSInteger)albumId;
- (id)initWithAlbumName:(NSString *)albumName;
- (Artist *)artist;
- (void)autoTag;
- (NSArray *)listOfSongs;

+ (Album *)albumForName:(NSString *)albumName artistId:(NSInteger)artistId;
+ (NSArray *)allAlbums;

@end
