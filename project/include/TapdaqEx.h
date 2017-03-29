#ifndef TAPDAQEX_H
#define TAPDAQEX_H


namespace tapdaq {
	
	
	void init(const char *appID, const char *clientKey, const char *testmode, const char *tagsJSON);
    void debugger();
    void loadBanner(const char *bannerType);
    void showBanner();
    void hideBanner();
    void moveBanner(const char *gravity);
    void loadInterstitial(const char *tag);
	void showInterstitial(const char *tag);
    void loadVideo(const char *tag);
    void showVideo(const char *tag);
    void loadRewardedVideo(const char *tag);
    void showRewardedVideo(const char *tag);
    void openMediationDebugger();
    
    bool bannerIsReady();
    bool interstitialIsReady(const char *tag);
    bool videoIsReady(const char *tag);
    bool rewardedIsReady(const char *tag);
}


#endif
