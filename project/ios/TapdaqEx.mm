/*
 *
 * Created by Robin Schaafsma
 * www.byrobingames.com
 *
 */

#include "TapdaqEx.h"
#import <Tapdaq/Tapdaq.h>
#import <UIKit/UIKit.h>
#import <AdSupport/ASIdentifierManager.h>
#include <CommonCrypto/CommonDigest.h>

using namespace tapdaq;

extern "C" void sendTapdaqEvent(char* event);

@interface TapdaqController : NSObject <TapdaqDelegate>
{
    UIView *bannerView;
    UIViewController *root;
}

- (id)initWithID:(NSString*)appID withClientKey:(NSString*)clientKey inTestMode:(NSString*)testmode;
- (void)mediationDebugger;
- (void)loadBannerAd:(NSString*)bannerType;
- (void)showBannerAd;
- (void)hideBannerAd;
- (void)setPosition:(NSString*)position;
- (BOOL)bannerAdIsReady;
- (void)loadInterstitialAd;
- (void)showInterstitialAd;
- (BOOL)interstitialAdIsReady;
- (void)loadVideoAd;
- (void)showVideoAd;
- (BOOL)videoAdIsReady;
- (void)loadRewardedAd;
- (void)showRewardedAd;
- (BOOL)rewardedAdIsReady;

@end

@implementation TapdaqController


- (id)initWithID:(NSString*)appID withClientKey:(NSString*)clientKey inTestMode:(NSString*)testmode
{
    self = [super init];
    if(!self) return nil;
    
    NSLog(@"Tapdaq Init");
    root = [[[UIApplication sharedApplication] keyWindow] rootViewController];
    
    TDProperties *tapdaqProps = [[TDProperties alloc] init];
    
    if ([testmode isEqualToString:@"YES"])
    {
        TDTestDevices *fbTestDevices = [[TDTestDevices alloc] initWithNetwork:TDMFacebookAudienceNetwork
                                                                  testDevices:@[ [self fbDeviceID] ]];
        [tapdaqProps registerTestDevices:fbTestDevices];
        
        TDTestDevices *amTestDevices = [[TDTestDevices alloc] initWithNetwork:TDMAdMob
                                                                  testDevices:@[ [self admobDeviceID] ]];
        [tapdaqProps registerTestDevices:amTestDevices];
    }
    
    [[Tapdaq sharedSession] setApplicationId:appID
                                   clientKey:clientKey
                                  properties:tapdaqProps];
    
    [[Tapdaq sharedSession] launch];
    
    [[Tapdaq sharedSession] setDelegate:self];
    
    return self;

}

///////GetDeviceid's
- (NSString *) admobDeviceID
{
    NSUUID* adid = [[ASIdentifierManager sharedManager] advertisingIdentifier];
    const char *cStr = [adid.UUIDString UTF8String];
    unsigned char digest[16];
    CC_MD5( cStr, strlen(cStr), digest );
    
    NSMutableString *resultString = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH * 2];
    
    for(int i = 0; i < CC_MD5_DIGEST_LENGTH; i++)
        [resultString appendFormat:@"%02x", digest[i]];
    
    return  resultString;
    
}

- (NSString *)fbDeviceID
{
    NSUUID* adid = [[ASIdentifierManager sharedManager] advertisingIdentifier];
    const char *cStr = [adid.UUIDString UTF8String];
    unsigned char digest[20];
    
    CC_SHA1( cStr, strlen(cStr), digest ); // This is the SHA1 call
    
    NSMutableString *resultString = [NSMutableString stringWithCapacity:CC_SHA1_DIGEST_LENGTH * 2];
    
    for(int i = 0; i < CC_SHA1_DIGEST_LENGTH; i++)
        [resultString appendFormat:@"%02x", digest[i]];
    
    return  resultString;
}
//////


- (void)mediationDebugger
{
    NSLog(@"Mediation Debugger");
    
    [[Tapdaq sharedSession] launchMediationDebugger:root];
}

/// banners

- (void)loadBannerAd:(NSString*)bannerType
{
    if([bannerType isEqualToString:@"TDMBannerStandard"]){
        [[Tapdaq sharedSession] loadBanner:TDMBannerStandard];
    }else if([bannerType isEqualToString:@"TDMBannerLarge"]){
        [[Tapdaq sharedSession] loadBanner:TDMBannerLarge];
    }else if([bannerType isEqualToString:@"TDMBannerMedium"]){
        [[Tapdaq sharedSession] loadBanner:TDMBannerMedium];
    }else if([bannerType isEqualToString:@"TDMBannerFull"]){
        [[Tapdaq sharedSession] loadBanner:TDMBannerFull];
    }else if([bannerType isEqualToString:@"TDMBannerLeaderboard"]){
        [[Tapdaq sharedSession] loadBanner:TDMBannerLeaderboard];
    }else if([bannerType isEqualToString:@"TDMBannerSmartPortrait"]){
        [[Tapdaq sharedSession] loadBanner:TDMBannerSmartPortrait];
    }else if([bannerType isEqualToString:@"TDMBannerSmartLandscape"]){
        [[Tapdaq sharedSession] loadBanner:TDMBannerSmartLandscape];
    }
    
}

- (void)showBannerAd
{
    bannerView.hidden=false;
}

- (void)hideBannerAd
{
    bannerView.hidden=true;
}

-(void)setPosition:(NSString*)position
{
    
    BOOL bottom=[position isEqualToString:@"BOTTOM"];
    
    if (bottom) // Reposition the adView to the bottom of the screen
    {
        CGRect frame = bannerView.frame;
        frame.origin.y = root.view.bounds.size.height - frame.size.height;
        bannerView.frame=frame;
        
    }else // Reposition the adView to the top of the screen
    {
        CGRect frame = bannerView.frame;
        frame.origin.y = 0;
        bannerView.frame=frame;
    }
}

- (BOOL)bannerAdIsReady
{
    return [[Tapdaq sharedSession] isBannerReady];
}

///// interstitialads
- (void)loadInterstitialAd
{
    NSLog(@"LoadInterstial");
    
    [[Tapdaq sharedSession] loadInterstitial];
}

- (void)showInterstitialAd
{
    NSLog(@"ShowInterstial");
    
    [[Tapdaq sharedSession] showInterstitial];
}

- (BOOL)interstitialAdIsReady
{
    return [[Tapdaq sharedSession] isInterstitialReady];
}

///// Videoads
- (void)loadVideoAd
{
    NSLog(@"LoadVideo");
    
    [[Tapdaq sharedSession] loadVideo];
}

- (void)showVideoAd
{
    NSLog(@"ShowVideo");
    
    [[Tapdaq sharedSession] showVideo];
}

- (BOOL)videoAdIsReady
{
    return [[Tapdaq sharedSession] isVideoReady];
}

///// rewardedVideoads
- (void)loadRewardedAd
{
    NSLog(@"LoadRewarded");
    
    [[Tapdaq sharedSession] loadRewardedVideo];
}

- (void)showRewardedAd
{
    NSLog(@"ShowRewarded");
    
    [[Tapdaq sharedSession] showRewardedVideo];
}

- (BOOL)rewardedAdIsReady
{
    return [[Tapdaq sharedSession] isRewardedVideoReady];
}


//Delegate interstitial
//Called immediately after the SDK is ready to begin loading adverts
- (void)didLoadConfig
{
    sendTapdaqEvent("configdidload");
}

#pragma mark Banner delegate methods

/**
 Called immediately after the banner is loaded.
 This method should be used in conjunction with -getBanner:.
 */
- (void)didLoadBanner
{

    bannerView = [[Tapdaq sharedSession] getBanner];
    [root.view addSubview:bannerView];
    
    bannerView.hidden=true;
    [self setPosition:@"BOTTOM"];
    
    sendTapdaqEvent("bannerdidload");
}

/**
 Called when, for whatever reason, the banner was not able to be loaded.
 Tapdaq will automatically attempt to load a banner again with a 1 second delay.
 */
- (void)didFailToLoadBanner
{
    sendTapdaqEvent("bannerfailtoload");
}

/**
 Called when the user clicks the banner.
 */
- (void)didClickBanner
{
    sendTapdaqEvent("bannerdidclick");
}

/**
 Called when the ad within the banner view loads another ad.
 */
- (void)didRefreshBanner
{
    sendTapdaqEvent("bannerdidrefresh");
}

#pragma mark Interstitial delegate methods

// Called when the request for interstitials was successful
// and 1 or more interstitials were found
- (void)didLoadInterstitialForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Interstital Available");
}


// Called with an error occurs when requesting
// interstitials from the Tapdaq servers
- (void)didFailToLoadInterstitialForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Interstital Fail to load");
}

// Called before interstitial is shown
- (void)willDisplayInterstitial
{
    NSLog(@"Tapdaq Interstital will display");
    sendTapdaqEvent("interstitialwilldisplay");
}

// Called after interstitial is shown
- (void)didDisplayInterstitial
{
    NSLog(@"Tapdaq Interstital is shown");
    sendTapdaqEvent("interstitialdiddisplay");
}

// Called when interstitial is closed
- (void)didCloseInterstitial
{
    NSLog(@"Tapdaq Interstital Closed");
    sendTapdaqEvent("interstitialdidclose");
}

- (void)didClickInterstitial
{
    NSLog(@"Tapdaq Interstital did click");
    sendTapdaqEvent("interstitialdidclick");
}

#pragma mark Video delegate methods

/**
 Called immediately after a video is available to the user for a specific placement tag.
 This method should be used in conjunction with -showVideoForPlacementTag:.
 @param tag A placement tag.
 */
- (void)didLoadVideoForPlacementTag:(NSString *)tag
{
     NSLog(@"Tapdaq Video is loaded");
}

/**
 Called when, for whatever reason, the video was not able to be loaded.
 Tapdaq will automatically attempt to load a video again with a 1 second delay.
 @param placementTag A placement tag.
 */
- (void)didFailToLoadVideoForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Video is failed to load");
}

/**
 Called immediately before the video is to be displayed to the user.
 */
- (void)willDisplayVideo
{
     NSLog(@"Tapdaq Video will display");
    sendTapdaqEvent("videowilldisplay");
}

/**
 Called immediately after the video is displayed to the user.
 */
- (void)didDisplayVideo
{
    NSLog(@"Tapdaq Video did display");
    sendTapdaqEvent("videodiddisplay");
}

/**
 Called when the user closes the video.
 */
- (void)didCloseVideo
{
    NSLog(@"Tapdaq Video did close");
    sendTapdaqEvent("videodidclose");
}

/**
 Called when the user clicks the video ad.
 */
- (void)didClickVideo
{
    NSLog(@"Tapdaq Video did click");
    sendTapdaqEvent("videodidclick");
}

#pragma mark Rewarded Video delegate methods

/**
 Called immediately after a rewarded video is available to the user for a specific placement tag.
 This method should be used in conjunction with -showRewardedVideoForPlacementTag:.
 @param tag A placement tag.
 */
- (void)didLoadRewardedVideoForPlacementTag:(NSString *)tag
{
    NSLog(@"Tapdaq Rewarded is loaded");
}

/**
 Called when, for whatever reason, the rewarded video was not able to be loaded.
 Tapdaq will automatically attempt to load a rewarded video again with a 1 second delay.
 @param placementTag A placement tag.
 */
- (void)didFailToLoadRewardedVideoForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Rewarded did failed to load");
}

/**
 Called immediately before the rewarded video is to be displayed to the user.
 */
- (void)willDisplayRewardedVideo
{
    NSLog(@"Tapdaq Rewarded will display");
    sendTapdaqEvent("rewardedwilldisplay");
}

/**
 Called immediately after the rewarded video is displayed to the user.
 */
- (void)didDisplayRewardedVideo
{
    NSLog(@"Tapdaq Rewarded did display");
    sendTapdaqEvent("rewardeddiddisplay");
}

/**
 Called when the user closes the rewarded video.
 */
- (void)didCloseRewardedVideo
{
    NSLog(@"Tapdaq Rewarded did close");
    sendTapdaqEvent("rewardeddidclose");
}

/**
 Called when the user clicks the rewarded video ad.
 */
- (void)didClickRewardedVideo
{
    NSLog(@"Tapdaq Rewarded did click");
    sendTapdaqEvent("rewardeddidclick");
}

/**
 Called when a reward is ready for the user.
 @param rewardName The name of the reward.
 @param rewardAmount The value of the reward.
 */
- (void)rewardValidationSucceededForRewardName:(NSString *)rewardName
                                  rewardAmount:(int)rewardAmount
{
    NSLog(@"Tapdaq Rewarded validated");
    sendTapdaqEvent("rewardedsucceeded");
}

/**
 Called if an error occurred when rewarding the user.
 */
- (void)rewardValidationErrored
{
    NSLog(@"Tapdaq Rewarded failed to validate");
}


@end

namespace tapdaq {
	
	static TapdaqController *tapdaqController;
    
	void init(const char *appID, const char *clientKey, const char *testmode){
        
        if(tapdaqController == NULL)
        {
            tapdaqController = [[TapdaqController alloc] init];
        }
        
        NSString *appIDnew = [NSString stringWithUTF8String:appID];
        NSString *clientKeynew = [NSString stringWithUTF8String:clientKey];
        NSString *testmodenew = [NSString stringWithUTF8String:testmode];
        
        [tapdaqController initWithID:appIDnew withClientKey:clientKeynew inTestMode:testmodenew];
    }
    
    void debugger()
    {
        if(tapdaqController!=NULL)
        {
            [tapdaqController mediationDebugger];
        }
    }
    
    void loadBanner(const char *bannerType)
    {
        if(tapdaqController!=NULL)
        {
            NSString *type = [NSString stringWithUTF8String:bannerType];
            
            [tapdaqController loadBannerAd:type];
        }
    }
    
    void showBanner()
    {
        if(tapdaqController!=NULL)
        {
            [tapdaqController showBannerAd];
        }
    }
    
    void hideBanner()
    {
        if(tapdaqController!=NULL)
        {
            [tapdaqController hideBannerAd];
        }
    }
    
    void moveBanner(const char *gravity)
    {
        if(tapdaqController!=NULL)
        {
            NSString *position = [NSString stringWithUTF8String:gravity];
            
            [tapdaqController setPosition:position];
        }
    }
    
    void loadInterstitial()
    {
        if(tapdaqController!=NULL)
        {
            [tapdaqController loadInterstitialAd];
        }
    }
    
    void showInterstitial()
    {
        if(tapdaqController!=NULL)
        {
            [tapdaqController showInterstitialAd];
        }
    }
    
    void loadVideo()
    {
        if(tapdaqController!=NULL)
        {
            [tapdaqController loadVideoAd];
        }
    }
    
    void showVideo()
    {
        if(tapdaqController!=NULL)
        {
            [tapdaqController showVideoAd];
        }
    }
    
    void loadRewardedVideo()
    {
        if(tapdaqController!=NULL)
        {
            [tapdaqController loadRewardedAd];
        }
    }
    
    void showRewardedVideo()
    {
        if(tapdaqController!=NULL)
        {
            [tapdaqController showRewardedAd];
        }
    }
    
    void openMediationDebugger()
    {
        if(tapdaqController!=NULL)
        {
            [tapdaqController mediationDebugger];
        }
    }
    
    
//Callbacks
    
    bool bannerIsReady()
    {
        if(tapdaqController != NULL)
        {
            return [tapdaqController bannerAdIsReady];
        }
        return false;
    }
    
    bool interstitialIsReady()
    {
        if(tapdaqController != NULL)
        {
            return [tapdaqController interstitialAdIsReady];
        }
        return false;
    }

    bool videoIsReady()
    {
        if(tapdaqController != NULL)
        {
           return [tapdaqController videoAdIsReady];
        }
        return false;
    }
    
    bool rewardedIsReady()
    {
        if(tapdaqController != NULL)
        {
            return [tapdaqController rewardedAdIsReady];
        }
        return false;
    }
    
}
