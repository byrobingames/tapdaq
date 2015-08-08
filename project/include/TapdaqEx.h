#ifndef TAPDAQEX_H
#define TAPDAQEX_H


namespace tapdaq {
	
	
	void init(const char *appID, const char *clientKey, const char *testmode);
	void showInterstitial();
    bool interstitialLoaded();
    bool interstitialFailToLoad();
    bool interstitialClosed();
    
}


#endif