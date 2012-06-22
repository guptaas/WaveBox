//
//  FileType.h
//  PersonalMediaServer
//
//  Created by Benjamin Baron on 6/21/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

typedef enum 
{
	MP3 = 0,
	AAC = 1,
	OGG = 2,
	FLAC16 = 3,
	FLAC24 = 4,
	WAV = 5,
	AIFF = 6,
	ALAC = 7,
	UNKNOWN = -1
} FileType;
