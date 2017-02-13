//
//  TDProperties.h
//  Tapdaq
//
//  Created by Tapdaq <support@tapdaq.com>
//  Copyright (c) 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "TDOrientationEnum.h"
#import "TDAdTypeEnum.h"
#import "TDPlacement.h"
#import "TDTestDevices.h"

@interface TDProperties : NSObject

/**
 Set how often the same advert should appear to the user.
 
 Defaults to 2.
 */
@property (nonatomic) NSInteger frequencyCap;

/**
 Set the number of days the frequencyCap should be applied to.
 For example, a frequencyDurationInDays = 2, frequencyCap = 3 means the same ad will be shown to the user a maximum of 3 times in 2 days.
 
 Defaults to 1.
 */
@property (nonatomic) NSInteger frequencyDurationInDays;

/**
 Set how many adverts per TDAdType should be cached on the device. The more adverts that are cached, the faster adverts are displayed to the user, but uses more storage.
 
 Defaults to 3.
 */
@property (nonatomic) NSUInteger maxCachedAdverts;

/**
 Note: For plugin developers only.
 Prefix the name of your library/plugin
 */
@property (nonatomic) NSString *sdkIdentifierPrefix;

/**
 To use placement tags, you must create a TDPlacement object and register it.
 If you do not register a placement tag but attempt to use a custom one elsewhere, adverts will not display.
 
 @param placement The TDPlacement object to be registered
 */
- (BOOL)registerPlacement:(TDPlacement *)placement;
- (BOOL)registerPlacements:(NSArray *)placements;

- (BOOL)registerTestDevices:(TDTestDevices *)testDevices;

- (NSArray *)registeredPlacements;

- (NSArray *)registeredTestDevices;

@end
