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

NSArray *interstitialPlacement;
NSArray *videoPlacement;
NSArray *rewardedPlacement;

@interface TapdaqController : NSObject <TapdaqDelegate>
{
    UIView *bannerView;
    UIViewController *root;
}

- (id)initWithID:(NSString*)appID withClientKey:(NSString*)clientKey inTestMode:(NSString*)testmode withTags:(NSString*)tagsJSON;
- (void)mediationDebugger;
- (void)loadBannerAd:(NSString*)bannerType;
- (void)showBannerAd;
- (void)hideBannerAd;
- (void)setPosition:(NSString*)position;
- (BOOL)bannerAdIsReady;
- (void)loadInterstitialAd:(NSString*)placementTAG;
- (void)showInterstitialAd:(NSString*)placementTAG;
- (BOOL)interstitialAdIsReady:(NSString*)placementTAG;
- (void)loadVideoAd:(NSString*)placementTAG;
- (void)showVideoAd:(NSString*)placementTAG;
- (BOOL)videoAdIsReady:(NSString*)placementTAG;
- (void)loadRewardedAd:(NSString*)placementTAG;
- (void)showRewardedAd:(NSString*)placementTAG;
- (BOOL)rewardedAdIsReady:(NSString*)placementTAG;
- (void)loadMoreAppsAd;
- (void)showMoreAppsAd;
- (BOOL)moreAppsAdIsReady;

@end

@implementation TapdaqController


- (id)initWithID:(NSString*)appID withClientKey:(NSString*)clientKey inTestMode:(NSString*)testmode withTags:(NSString*)tagsJSON
{
    self = [super init];
    if(!self) return nil;
    
    NSLog(@"Tapdaq Init");
    root = [[[UIApplication sharedApplication] keyWindow] rootViewController];
    
    TDProperties *tapdaqProps = [[TDProperties alloc] init];
    
    //set test devices
    if ([testmode isEqualToString:@"YES"])
    {
        TDTestDevices *fbTestDevices = [[TDTestDevices alloc] initWithNetwork:TDMFacebookAudienceNetwork
                                                                  testDevices:@[ [self fbDeviceID] ]];
        [tapdaqProps registerTestDevices:fbTestDevices];
        
        TDTestDevices *amTestDevices = [[TDTestDevices alloc] initWithNetwork:TDMAdMob
                                                                  testDevices:@[ [self admobDeviceID] ]];
        [tapdaqProps registerTestDevices:amTestDevices];
    }
    
    //Register placementid's
    NSLog(@"Tags: %@", tagsJSON);
    
    //NSString *tagsJSON =  @"{\"TDAdTypeInterstitial\": [\"interTags1\",\"interTags2\"], \"TDAdTypeVideo\": [\"vidTags\"],\"TDAdTypeRewardedVideo\": [\"rewarTags\"]}";
    NSData *tagData = [tagsJSON dataUsingEncoding:NSUTF8StringEncoding];
    NSError *error;
    NSDictionary *tagArray = [NSJSONSerialization JSONObjectWithData:tagData options:NSJSONReadingMutableContainers error:&error];
    
    for (NSDictionary *dict in tagArray) {
        
        NSLog(@"NSDictionary %@", dict);
        NSArray *interstititalplacementTags = [tagArray objectForKey:@"TDAdTypeInterstitial"];
        NSArray *videoplacementTags = [tagArray objectForKey:@"TDAdTypeVideo"];
        NSArray *rewardedplacementTags = [tagArray objectForKey:@"TDAdTypeRewardedVideo"];
        NSArray *moreAppsplacementTags = [tagArray objectForKey:@"TDAdTypeMoreApps"];
        
        //NSLog(@"interstititalplacementTags: %@", interstititalplacementTags);
        //NSLog(@"videoplacementTags: %@", videoplacementTags);
        //NSLog(@"rewardedplacementTags: %@", rewardedplacementTags);
        
        if ([interstititalplacementTags count] > 0) {
            for (NSString *initplacementTag in interstititalplacementTags) {
                NSLog (@"InterstitialPlacement ID = %@", initplacementTag);
                @try {
                    TDPlacement *interstitialTag = [[TDPlacement alloc] initWithAdTypes:TDAdTypeInterstitial forTag:initplacementTag];
                    [tapdaqProps registerPlacement:interstitialTag];
                } @catch (NSException *exception) {
                    NSLog (@"Warning register interstitialTag: %@", exception);
                }
            }
        }
        if ([videoplacementTags count] > 0) {
            for (NSString *vidplacementTag in videoplacementTags) {
                NSLog (@"VideoPlacement ID = %@", vidplacementTag);
                @try {
                    TDPlacement *videoTag = [[TDPlacement alloc] initWithAdTypes:TDAdTypeVideo forTag:vidplacementTag];
                    [tapdaqProps registerPlacement:videoTag];
                } @catch (NSException *exception) {
                    NSLog (@"Warning register videoTag: %@", exception);
                }
            }
        }
        if ([rewardedplacementTags count] > 0) {
            for (NSString *rewaplacementTag in rewardedplacementTags) {
                NSLog (@"RewardedPlacement ID = %@", rewaplacementTag);
                @try {
                    TDPlacement *rewardedTag = [[TDPlacement alloc] initWithAdTypes:TDAdTypeRewardedVideo forTag:rewaplacementTag];
                    [tapdaqProps registerPlacement:rewardedTag];
                } @catch (NSException *exception) {
                    NSLog (@"Warning register rewardedTag: %@", exception);
                }
            }
        }
        if ([moreAppsplacementTags count] > 0) {
            for (NSString *moreplacementTag in moreAppsplacementTags) {
                NSLog (@"MoreAppsPlacement ID = %@", moreplacementTag);
                @try {
                    TDPlacement *moreappsTag = [[TDPlacement alloc] initWithAdTypes:TDAdType1x1Medium  forTag:moreplacementTag];
                    [tapdaqProps registerPlacement:moreappsTag];
                } @catch (NSException *exception) {
                    NSLog (@"Warning register MoreappsTag: %@", exception);
                }
            }
        }
    }
    //////End Register
    
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
    
    [[Tapdaq sharedSession] presentDebugViewController];
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
        frame.origin.x = (root.view.bounds.size.width - frame.size.width)/2;
        frame.origin.y = root.view.bounds.size.height - frame.size.height;
        bannerView.frame=frame;
       //[bannerView setFrame:CGRectMake(
                                             //(root.view.frame.size.width-bannerView.frame.size.width)/2,
                                             //root.view.frame.size.height-bannerView.frame.size.height,
                                            // bannerView.frame.size.width,
                                            // bannerView.frame.size.height
                                            // )];
        
    }else // Reposition the adView to the top of the screen
    {
        CGRect frame = bannerView.frame;
        frame.origin.x = (root.view.bounds.size.width - frame.size.width)/2;
        frame.origin.y = 0;
        bannerView.frame=frame;
        
        
        //[bannerView setFrame:CGRectMake(
                                       // (root.view.frame.size.width-bannerView.frame.size.width)/2,
                                       // 0,
                                       // bannerView.frame.size.width,
                                      //  bannerView.frame.size.height
                                      //  )];
    }
}

- (BOOL)bannerAdIsReady
{
    return [[Tapdaq sharedSession] isBannerReady];
}

///// interstitialads
- (void)loadInterstitialAd:(NSString*)placementTAG
{
    NSLog(@"LoadInterstial");
    
    //[[Tapdaq sharedSession] loadInterstitial];
    [[Tapdaq sharedSession] loadInterstitialForPlacementTag:placementTAG];
}

- (void)showInterstitialAd:(NSString*)placementTAG
{
    NSLog(@"ShowInterstial");
    
    //[[Tapdaq sharedSession] showInterstitial];
    [[Tapdaq sharedSession] showInterstitialForPlacementTag:placementTAG];
}

- (BOOL)interstitialAdIsReady:(NSString*)placementTAG
{
    //return [[Tapdaq sharedSession] isInterstitialReady];
    return [[Tapdaq sharedSession] isInterstitialReadyForPlacementTag:placementTAG];
}

///// Videoads
- (void)loadVideoAd:(NSString*)placementTAG
{
    NSLog(@"LoadVideo");
    
    //[[Tapdaq sharedSession] loadVideo];
     [[Tapdaq sharedSession] loadVideoForPlacementTag:placementTAG];
}

- (void)showVideoAd:(NSString*)placementTAG
{
    NSLog(@"ShowVideo");
    
    //[[Tapdaq sharedSession] showVideo];
    [[Tapdaq sharedSession] showVideoForPlacementTag:placementTAG];
}

- (BOOL)videoAdIsReady:(NSString*)placementTAG
{
    //return [[Tapdaq sharedSession] isVideoReady];
    return [[Tapdaq sharedSession] isVideoReadyForPlacementTag:placementTAG];
}

///// rewardedVideoads
- (void)loadRewardedAd:(NSString*)placementTAG
{
    NSLog(@"LoadRewarded");
    
    //[[Tapdaq sharedSession] loadRewardedVideo];
    [[Tapdaq sharedSession] loadRewardedVideoForPlacementTag:placementTAG];
}

- (void)showRewardedAd:(NSString*)placementTAG
{
    NSLog(@"ShowRewarded");
    
    //[[Tapdaq sharedSession] showRewardedVideo];
    [[Tapdaq sharedSession] showRewardedVideoForPlacementTag:placementTAG];
}

- (BOOL)rewardedAdIsReady:(NSString*)placementTAG
{
    //return [[Tapdaq sharedSession] isRewardedVideoReady];
    return [[Tapdaq sharedSession] isRewardedVideoReadyForPlacementTag:placementTAG];

}
    
    ///// moreapps
- (void)loadMoreAppsAd
    {
        NSLog(@"LoadMoreApps");
        
        [[Tapdaq sharedSession] loadMoreAppsWithConfig:[self customMoreAppsConfig]];
    }
    
- (void)showMoreAppsAd
    {
        NSLog(@"ShowMoreApps");
        
        [[Tapdaq sharedSession] showMoreApps];
    }
    
- (BOOL)moreAppsAdIsReady
    {
        return [[Tapdaq sharedSession] isMoreAppsReady];
        
    }
    
- (TDMoreAppsConfig *)customMoreAppsConfig
    {
        TDMoreAppsConfig *moreAppsConfig = [[TDMoreAppsConfig alloc] init];
        
        moreAppsConfig.headerText = @"More Games";
        moreAppsConfig.installedAppButtonText = @"Play";
        
        moreAppsConfig.headerTextColor = [UIColor whiteColor];
        moreAppsConfig.headerColor = [UIColor darkGrayColor];
        moreAppsConfig.headerCloseButtonColor = [UIColor blackColor];
        moreAppsConfig.backgroundColor = [UIColor grayColor];
        
        moreAppsConfig.appNameColor = [UIColor blackColor];
        moreAppsConfig.appButtonColor = [UIColor blackColor];
        moreAppsConfig.appButtonTextColor = [UIColor whiteColor];
        moreAppsConfig.installedAppButtonColor = [UIColor whiteColor];
        moreAppsConfig.installedAppButtonTextColor = [UIColor blackColor];
        
        return moreAppsConfig;
    }

#pragma mark - TapdaqDelegate

//Called immediately after the SDK is ready to begin loading adverts
- (void)didLoadConfig
{
    sendTapdaqEvent("configdidload");
}
    
- (void)didLoadMoreApps
{
    sendTapdaqEvent("moreappsdidload");
}
    
- (void)didFailToLoadMoreApps
{
    sendTapdaqEvent("moreappsdidfailtoload");
}
    
- (void)willDisplayMoreApps
{
    sendTapdaqEvent("moreappswilldisplay");
}
    
- (void)didDisplayMoreApps
{
   sendTapdaqEvent("moreappsdiddisplay");
}
    
- (void)didCloseMoreApps
{
    sendTapdaqEvent("moreappsdidclose");
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
- (void)willDisplayInterstitialForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Interstital will display");
    sendTapdaqEvent("interstitialwilldisplay");
}

// Called after interstitial is shown
- (void)didDisplayInterstitialForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Interstital is shown");
    sendTapdaqEvent("interstitialdiddisplay");
}

// Called when interstitial is closed
- (void)didCloseInterstitialForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Interstital Closed");
    sendTapdaqEvent("interstitialdidclose");
}

- (void)didClickInterstitialForPlacementTag:(NSString *)placementTag
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
- (void)didLoadVideoForPlacementTag:(NSString *)placementTag
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
- (void)willDisplayVideoForPlacementTag:(NSString *)placementTag
{
     NSLog(@"Tapdaq Video will display");
    sendTapdaqEvent("videowilldisplay");
}

/**
 Called immediately after the video is displayed to the user.
 */
- (void)didDisplayVideoForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Video did display");
    sendTapdaqEvent("videodiddisplay");
}

/**
 Called when the user closes the video.
 */
- (void)didCloseVideoForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Video did close");
    sendTapdaqEvent("videodidclose");
}

/**
 Called when the user clicks the video ad.
 */
- (void)didClickVideoForPlacementTag:(NSString *)placementTag
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
- (void)willDisplayRewardedVideoForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Rewarded will display");
    sendTapdaqEvent("rewardedwilldisplay");
}

/**
 Called immediately after the rewarded video is displayed to the user.
 */
- (void)didDisplayRewardedVideoForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Rewarded did display");
    sendTapdaqEvent("rewardeddiddisplay");
}

/**
 Called when the user closes the rewarded video.
 */
- (void)didCloseRewardedVideoForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Rewarded did close");
    sendTapdaqEvent("rewardeddidclose");
}

/**
 Called when the user clicks the rewarded video ad.
 */
- (void)didClickRewardedVideoForPlacementTag:(NSString *)placementTag
{
    NSLog(@"Tapdaq Rewarded did click");
    sendTapdaqEvent("rewardeddidclick");
}

/**
 Called when a reward is ready for the user.
 @param rewardName The name of the reward.
 @param rewardAmount The value of the reward.
 */
/**- (void)rewardValidationSucceededForPlacementTag:(NSString *)placementTag
                                      rewardName:(NSString *)rewardName
                                    rewardAmount:(int)rewardAmount
                                         payload:(NSDictionary *)payload
{
    NSLog(@"Tapdaq Rewarded validated");
    sendTapdaqEvent("rewardedsucceeded");
}*/
- (void)rewardValidationSucceeded:(TDReward *)reward {
    NSString *eventId = reward.eventId;
    NSString *name = reward.name;
    int value = reward.value;
    NSString *tag = reward.tag;
    id customJson = reward.customJson;
    
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
    
	void init(const char *appID, const char *clientKey, const char *testmode, const char *tagsJSON){
        
        NSLog(@"First call Init");
        
        if(tapdaqController == NULL)
        {
            tapdaqController = [[TapdaqController alloc] init];
        }
        
        NSString *appIDnew = [NSString stringWithUTF8String:appID];
        NSString *clientKeynew = [NSString stringWithUTF8String:clientKey];
        NSString *testmodenew = [NSString stringWithUTF8String:testmode];
        NSString *tagsJSONnew = [NSString stringWithUTF8String:tagsJSON];
        
        [tapdaqController initWithID:appIDnew withClientKey:clientKeynew inTestMode:testmodenew withTags:tagsJSONnew];
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
    
    void loadInterstitial(const char *tag)
    {
        if(tapdaqController!=NULL)
        {
            NSString *tagnew = [NSString stringWithUTF8String:tag];
            
            [tapdaqController loadInterstitialAd:tagnew];
        }
    }
    
    void showInterstitial(const char *tag)
    {
        if(tapdaqController!=NULL)
        {
            NSString *tagnew = [NSString stringWithUTF8String:tag];
            [tapdaqController showInterstitialAd:tagnew];
        }
    }
    
    void loadVideo(const char *tag)
    {
        if(tapdaqController!=NULL)
        {
            NSString *tagnew = [NSString stringWithUTF8String:tag];
            [tapdaqController loadVideoAd:tagnew];
        }
    }
    
    void showVideo(const char *tag)
    {
        if(tapdaqController!=NULL)
        {
            NSString *tagnew = [NSString stringWithUTF8String:tag];
            [tapdaqController showVideoAd:tagnew];
        }
    }
    
    void loadRewardedVideo(const char *tag)
    {
        if(tapdaqController!=NULL)
        {
            NSString *tagnew = [NSString stringWithUTF8String:tag];
            [tapdaqController loadRewardedAd:tagnew];
        }
    }
    
    void showRewardedVideo(const char *tag)
    {
        if(tapdaqController!=NULL)
        {
            NSString *tagnew = [NSString stringWithUTF8String:tag];
            [tapdaqController showRewardedAd:tagnew];
        }
    }
    
    void loadMoreApps()
    {
        if(tapdaqController!=NULL)
        {
            [tapdaqController loadMoreAppsAd];
        }
    }
    
    void showMoreApps()
    {
        if(tapdaqController!=NULL)
        {
            [tapdaqController showMoreAppsAd];
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
    
    bool interstitialIsReady(const char *tag)
    {
        if(tapdaqController != NULL)
        {
            NSString *tagnew = [NSString stringWithUTF8String:tag];
            return [tapdaqController interstitialAdIsReady:tagnew];
        }
        return false;
    }

    bool videoIsReady(const char *tag)
    {
        if(tapdaqController != NULL)
        {
            NSString *tagnew = [NSString stringWithUTF8String:tag];
           return [tapdaqController videoAdIsReady:tagnew];
        }
        return false;
    }
    
    bool rewardedIsReady(const char *tag)
    {
        if(tapdaqController != NULL)
        {
            NSString *tagnew = [NSString stringWithUTF8String:tag];
            return [tapdaqController rewardedAdIsReady:tagnew];
        }
        return false;
    }
    
    bool moreAppsIsReady()
    {
        if(tapdaqController != NULL)
        {
            return [tapdaqController moreAppsAdIsReady];
        }
        return false;
    }
    
}
