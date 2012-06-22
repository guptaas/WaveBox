//
//  Artist.h
//  PersonalMediaServer
//
//  Created by Benjamin Baron on 6/21/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Artist : NSObject

@property (nonatomic) NSInteger itemTypeId;

/**
 * Unique identifier
 */
@property (nonatomic) NSInteger artistId;

/**
 * Name from tags
 */
@property (nonatomic, strong) NSString *artistName;

/**
 * Associated cover art
 */
@property (nonatomic) NSInteger artId;


@end
