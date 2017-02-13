#ifndef TAPDAQEX_H
#define TAPDAQEX_H


namespace tapdaq {
	
	
	void init(const char *appID, const char *clientKey, const char *testmode);
    void debugger();
    void loadBanner(const char *bannerType);
    void showBanner();
    void hideBanner();
    void moveBanner(const char *gravity);
    void loadInterstitial();
	void showInterstitial();
    void loadVideo();
    void showVideo();
    void loadRewardedVideo();
    void showRewardedVideo();
    void openMediationDebugger();
    
    bool bannerIsReady();
    bool interstitialIsReady();
    bool videoIsReady();
    bool rewardedIsReady();
    
}


#endif
