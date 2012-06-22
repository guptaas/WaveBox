//
//  Album.m
//  PersonalMediaServer
//
//  Created by Benjamin Baron on 6/21/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "Album.h"

@implementation Album
@synthesize itemTypeId, artistId, albumId, albumName, releaseYear, artId;

- (id)initWithAlbumId:(NSInteger)albumId
{
	return nil;
}

- (id)initWithAlbumName:(NSString *)albumName
{
	return nil;
}

- (Artist *)artist
{
	return nil;
}

- (void)autoTag
{
	
}

- (NSArray *)listOfSongs
{
	return nil;
}

+ (Album *)albumForName:(NSString *)albumName artistId:(NSInteger)artistId
{
	return nil;
}

+ (NSArray *)allAlbums
{
	return nil;
}

@end
