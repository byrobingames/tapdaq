/*
 *
 * Created by Robin Schaafsma
 * www.byrobingames.com
 *
 */

#include "TapdaqEx.h"
#import <Tapdaq/Tapdaq.h>
#import <UIKit/UIKit.h>

using namespace tapdaq;

@interface TapdaqController : NSObject <TapdaqDelegate>
{
 
    BOOL interstitialLoaded;
    BOOL interstitialFailToLoad;
    BOOL interstitialClosed;
}

@property (nonatomic, assign) BOOL interstitialLoaded;
@property (nonatomic, assign) BOOL interstitialFailToLoad;
@property (nonatomic, assign) BOOL interstitialClosed;
@end

@implementation TapdaqController

@synthesize interstitialLoaded;
@synthesize interstitialFailToLoad;
@synthesize interstitialClosed;

- (void)initWithID:(NSString*)appID withClientKey:(NSString*)clientKey inTestMode:(NSString*)testmode
{
    NSLog(@"Tapdaq Init");
    
    NSMutableDictionary *tapdaqConfig = [[NSMutableDictionary alloc] init];
    
    if( [UIApplication sharedApplication].statusBarOrientation == UIInterfaceOrientationLandscapeLeft ||
       [UIApplication sharedApplication].statusBarOrientation == UIInterfaceOrientationLandscapeRight )
    {
        [tapdaqConfig setObject:[NSNumber numberWithInteger:TDOrientationLandscape] forKey:@"orientation"];
        
    }else{
        [tapdaqConfig setObject:[NSNumber numberWithInteger:TDOrientationPortrait] forKey:@"orientation"];
    }
    
    if ([testmode isEqualToString:@"YES"])
    {
        [tapdaqConfig setObject:@YES forKey:@"testAdvertsEnabled"];
    }
    
    [(Tapdaq *)[Tapdaq sharedSession] setApplicationId:appID clientKey:clientKey config:tapdaqConfig];
    
    [[Tapdaq sharedSession] launch];
    
    [(Tapdaq *)[Tapdaq sharedSession] setDelegate: self];

}

- (void)showInterstitialAd
{
    NSLog(@"ShowInterstial");

    [[Tapdaq sharedSession] showInterstitial];
}

// Called when the request for interstitials was successful
// and 1 or more interstitials were found
- (void)hasInterstitialsAvailable
{
    interstitialLoaded = YES;
    interstitialFailToLoad = NO;
    
    NSLog(@"Tapdaq Interstital Available");
}

// Called before interstitial is shown
- (void)willDisplayInterstitial
{
    
}

// Called with an error occurs when requesting
// interstitials from the Tapdaq servers
- (void)didFailToLoadInterstitial
{
    interstitialFailToLoad = YES;
    interstitialLoaded = NO;
    
     NSLog(@"Tapdaq Interstital Fail to load");
    
}

// Called when the request for interstitials was successful,
// but no interstitials were found
- (void)hasNoInterstitialsAvailable
{
    interstitialFailToLoad = YES;
    interstitialLoaded = NO;
    
     NSLog(@"Tapdaq Interstital not Available");
}

// Called when interstitial is closed
- (void)didCloseInterstitial
{
    
    interstitialClosed = YES;
    
     NSLog(@"Tapdaq Interstital Closed");

}

// Called after interstitial is shown
- (void)didDisplayInterstitial
{
    NSLog(@"Tapdaq Interstital is shown");
    
}


@end

namespace tapdaq {
	
	static TapdaqController *adController;
    
	void init(const char *appID, const char *clientKey, const char *testmode){
        
        if(adController == NULL)
        {
            adController = [[TapdaqController alloc] init];
        }
        
        NSString *appIDnew = [NSString stringWithUTF8String:appID];
        NSString *clientKeynew = [NSString stringWithUTF8String:clientKey];
        NSString *testmodenew = [NSString stringWithUTF8String:testmode];
        
        [adController initWithID:appIDnew withClientKey:clientKeynew inTestMode:testmodenew];
    }
    
    void showInterstitial()
    {
        if(adController!=NULL)
        {
            [adController showInterstitialAd];
        }
    }
    
    
//Callbacks
    
    bool interstitialLoaded()
    {
        if(adController != NULL)
        {
            if (adController.interstitialLoaded)
            {
                adController.interstitialLoaded = NO;
                return true;
            }
        }
        return false;
    }
    
    bool interstitialFailToLoad()
    {
        if(adController != NULL)
        {
            if (adController.interstitialFailToLoad)
            {
                adController.interstitialFailToLoad = NO;
                return true;
            }
        }
        return false;
    }

    bool interstitialClosed()
    {
        if(adController != NULL)
        {
            if (adController.interstitialClosed)
            {
                adController.interstitialClosed = NO;
                return true;
            }
        }
        return false;
    }
    
}
