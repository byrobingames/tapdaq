/*
 *
 * Created by Robin Schaafsma
 * www.byrobingames.com
 *
 */

package com.byrobin.tapdaq;

import org.haxe.extension.Extension;
import org.haxe.lime.HaxeObject;

import com.tapdaq.sdk.*;
import com.tapdaq.sdk.ads.*;
import com.tapdaq.sdk.common.TMBannerAdSizes;
import com.tapdaq.sdk.helpers.TLog;
import com.tapdaq.sdk.helpers.TLogLevel;
import com.tapdaq.sdk.listeners.TMInitListener;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.listeners.TMAdListener;
import com.tapdaq.sdk.moreapps.TMMoreAppsConfig;
import com.tapdaq.sdk.moreapps.TMMoreAppsListener;
import com.tapdaq.sdk.adnetworks.TMMediationNetworks;

import java.util.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;

import android.content.Context;
import android.util.Log;


import android.provider.Settings.Secure;
import android.content.SharedPreferences;

import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AlphaAnimation;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

::if ENV_TPFACEBOOK_ENABLED::
import com.facebook.ads.internal.util.*;//used to get facebook hashkey
::end::

public class TapdaqEx extends Extension {


	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	private static String appId=null;
    private static String clientKey=null;
    private static String testMode=null;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    protected static HaxeObject haxeCallback;
    private static TMBannerAdView banner;
    private static LinearLayout layout;
    private static TapdaqEx instance=null;
    private static int gravity=Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
    
    static public TapdaqEx getInstance(){
        if(instance==null && appId!=null) instance = new TapdaqEx();
        if(appId==null){
            Log.e("Tapdaq","You tried to get Instance without calling INIT first on Tapdaq class!");
        }
        return instance;
    }

	static public void init(HaxeObject cb, final String appId, final String clientKey, final String testMode, final String _tags){
        
        haxeCallback = cb;
		TapdaqEx.appId=appId;
        TapdaqEx.clientKey=clientKey;
        TapdaqEx.testMode=testMode;
        
		
		Extension.mainActivity.runOnUiThread(new Runnable() {
            public void run() 
			{
				Log.d("TapdaqEx","Init Tapdaq" + testMode);
                
                ///Register Placementtags
                JSONArray interTagsArray = new JSONArray();
                JSONArray videoTagsArray = new JSONArray();
                JSONArray rewardTagsArray = new JSONArray();
                JSONArray moreTagsArray = new JSONArray();
                try
                {
                    JSONObject obj = new JSONObject(_tags);
                    
                    Log.d("Tapdaq", "Json "+obj.toString());
                    Log.d("Tapdaq", "Json TDAdTypeInterstitial "+obj.getString("TDAdTypeInterstitial"));
                    
                    interTagsArray = obj.getJSONArray("TDAdTypeInterstitial");
                    videoTagsArray = obj.getJSONArray("TDAdTypeVideo");
                    rewardTagsArray = obj.getJSONArray("TDAdTypeRewardedVideo");
                    moreTagsArray = obj.getJSONArray("TDAdTypeMoreApps");
                    
                    Log.d("Tapdaq", "interTags1 "+interTagsArray);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                
                TapdaqConfig config = new TapdaqConfig(Extension.mainActivity);
                List<TapdaqPlacement> enabledPlacements = new ArrayList();
                
                if(interTagsArray != null){
                    for (int iTagNum=0; iTagNum < interTagsArray.length(); iTagNum++) {
                        try{
                            Log.d("Tapdaq","Interstitial tag: "+iTagNum+"= "+interTagsArray.getString(iTagNum));
                            enabledPlacements.add(TapdaqPlacement.createPlacement(Arrays.asList(CreativeType.INTERSTITIAL_PORTRAIT, CreativeType.INTERSTITIAL_LANDSCAPE), interTagsArray.getString(iTagNum)));
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                
                if(videoTagsArray != null){
                    for (int vTagNum=0; vTagNum < videoTagsArray.length(); vTagNum++) {
                        try{
                            Log.d("Tapdaq","vidoeTagsList: "+vTagNum+"= "+videoTagsArray.getString(vTagNum));
                            enabledPlacements.add(TapdaqPlacement.createPlacement(Arrays.asList(CreativeType.VIDEO_INTERSTITIAL), videoTagsArray.getString(vTagNum)));
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                
                if(rewardTagsArray != null){
                    for (int rTagNum=0; rTagNum < rewardTagsArray.length(); rTagNum++) {
                        try{
                            Log.d("Tapdaq","rewardTagsList: "+rTagNum+"= "+rewardTagsArray.getString(rTagNum));
                            enabledPlacements.add(TapdaqPlacement.createPlacement(Arrays.asList(CreativeType.REWARDED_VIDEO_INTERSTITIAL), rewardTagsArray.getString(rTagNum)));
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                
                if(moreTagsArray != null){
                    for (int mTagNum=0; mTagNum < moreTagsArray.length(); mTagNum++) {
                        try{
                            Log.d("Tapdaq","moreTagsList: "+mTagNum+"= "+moreTagsArray.getString(mTagNum));
                            enabledPlacements.add(TapdaqPlacement.createPlacement(Arrays.asList(CreativeType.SQUARE_MEDIUM), moreTagsArray.getString(mTagNum)));
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                
                config.withPlacementTagSupport((TapdaqPlacement[])enabledPlacements.toArray(new TapdaqPlacement[enabledPlacements.size()]));
                
                
                if (testMode.equals("YES")){
                    String android_id = Secure.getString(mainActivity.getContentResolver(), Secure.ANDROID_ID);
                    String admobDeviceId = getInstance().md5(android_id).toUpperCase();
                    config.registerTestDevices(TMMediationNetworks.AD_MOB,Arrays.asList(admobDeviceId));
                    Log.d("Tapdaq","Admob DEVICE ID: "+admobDeviceId);
                    ::if ENV_TPFACEBOOK_ENABLED::
                    String facebookDeviceId = getInstance().getDeviceIdHash(mainActivity);
                    config.registerTestDevices(TMMediationNetworks.FACEBOOK,Arrays.asList(facebookDeviceId));
                    Log.d("Tapdaq","Facebook DEVICE ID: "+facebookDeviceId);
                    ::end::
                }
                
                Tapdaq.getInstance().initialize(mainActivity,appId,clientKey,config, new InitListener());
                
			}
		});	
	}
    
    ////Admob get DeviceID
    private static String md5(String s)  {
        MessageDigest digest;
        try  {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(),0,s.length());
            return new java.math.BigInteger(1, digest.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    ::if ENV_TPFACEBOOK_ENABLED::
    ////facebook get DeviceID
    public static String getDeviceIdHash(Context var0) { //get's device hash id.
        
        SharedPreferences var1 = var0.getSharedPreferences("FBAdPrefs", 0);
        String deviceIdHash = var1.getString("deviceIdHash", null);
        if(deviceIdHash == null || deviceIdHash.length() <= 0){
            deviceIdHash = u.b(UUID.randomUUID().toString());
            var1.edit().putString("deviceIdHash", deviceIdHash).apply();
            
        }
        return deviceIdHash;
    }
    ::end::
    
    //////////////////////
    
    static public void openDebugger()
    {
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                
                Tapdaq.getInstance().startTestActivity(mainActivity);
            }
        });
        
    }
    
    static public void loadBanner(final String bannerType)
    {
        Log.d("TapdaqEx","Load Banner Begin");
        if(appId=="") return;
        if(clientKey=="") return;
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
        
                if(banner==null){ // if this is the first time we call this function
                    layout = new LinearLayout(mainActivity);
                    layout.setGravity(gravity);
                } else {
                    ViewGroup parent = (ViewGroup) layout.getParent();
                    parent.removeView(layout);
                    layout.removeView(banner);
                    banner.destroy(mainActivity);
                }
        
                banner = new TMBannerAdView(mainActivity); //Create Ad View
        
                mainActivity.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
                layout.addView(banner);
                layout.bringToFront();
                
                if(bannerType.equals("TDMBannerStandard")){
                    banner.load(mainActivity, TMBannerAdSizes.STANDARD, new BannerAdListener());
                }else if(bannerType.equals("TDMBannerLarge")){
                    banner.load(mainActivity, TMBannerAdSizes.LARGE, new BannerAdListener());
                }else if(bannerType.equals("TDMBannerMedium")){
                    banner.load(mainActivity, TMBannerAdSizes.MEDIUM_RECT, new BannerAdListener());
                }else if(bannerType.equals("TDMBannerFull")){
                    banner.load(mainActivity, TMBannerAdSizes.FULL, new BannerAdListener());
                }else if(bannerType.equals("TDMBannerLeaderboard")){
                    banner.load(mainActivity, TMBannerAdSizes.LEADERBOARD, new BannerAdListener());
                }else if(bannerType.equals("TDMBannerSmartPortrait")){
                    banner.load(mainActivity, TMBannerAdSizes.SMART, new BannerAdListener());
                }else if(bannerType.equals("TDMBannerSmartLandscape")){
                    banner.load(mainActivity, TMBannerAdSizes.SMART, new BannerAdListener());
                }
            }
        });
        
        Log.d("TapdaqEx","Load Banner End ");
    }
    
    static public void showBanner() {
        if(appId=="") return;
        if(clientKey=="") return;
        Log.d("TapdaqEx","Show Banner");
        
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                
                banner.setVisibility(TMBannerAdView.VISIBLE);
                
                Animation animation1 = new AlphaAnimation(0.0f, 1.0f);
                animation1.setDuration(1000);
                layout.startAnimation(animation1);
            }
        });
    }
    
    
    static public void hideBanner() {
        if(appId=="") return;
        if(clientKey=="") return;
        Log.d("TapdaqEx","Hide Banner");
        
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                
                Animation animation1 = new AlphaAnimation(1.0f, 0.0f);
                animation1.setDuration(1000);
                layout.startAnimation(animation1);
                
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        banner.setVisibility(TMBannerAdView.GONE);
                    }
                }, 1000);
                
            }
        });
    }
    
    static public void setBannerPosition(final String gravityMode)
    {
        mainActivity.runOnUiThread(new Runnable()
                                   {
            public void run()
            {
                
                if(gravityMode.equals("TOP"))
                {
                    if(banner==null)
                    {
                        TapdaqEx.gravity=Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    }else
                    {
                        TapdaqEx.gravity=Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                        layout.setGravity(gravity);
                    }
                }else
                {
                    if(banner==null)
                    {
                        TapdaqEx.gravity=Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                    }else
                    {
                        TapdaqEx.gravity=Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                        layout.setGravity(gravity);
                    }
                }
            }
        });
    }
    
    static public void loadInterstitial(final String tag)
    {
        Log.d("TapdaqEx","Load Interstitial Begin");
        if(appId=="") return;
        if(clientKey=="") return;
        Extension.mainActivity.runOnUiThread(new Runnable() {
            public void run()
            {
                //Tapdaq.getInstance().loadInterstitial(Extension.mainActivity, TapdaqPlacement.TDPTagDefault, new InterstitialAdListener());
                Tapdaq.getInstance().loadInterstitial(Extension.mainActivity, tag, new InterstitialAdListener());
            }
        });
        Log.d("TapdaqEx","Load Interstitial End ");
    }

	static public void showInterstitial(final String tag)
    {
        Log.d("TapdaqEx","Show Interstitial Begin");
		if(appId=="") return;
        if(clientKey=="") return;
		Extension.mainActivity.runOnUiThread(new Runnable() {
			public void run()
            {
                //Tapdaq.getInstance().showInterstitial(Extension.mainActivity, TapdaqPlacement.TDPTagDefault, new InterstitialAdListener());
                Tapdaq.getInstance().showInterstitial(Extension.mainActivity, tag, new InterstitialAdListener());
            }
		});
		Log.d("TapdaqEx","Show Interstitial End ");
	}
    
    static public void loadVideo(final String tag)
    {
        Log.d("TapdaqEx","Load Video Begin");
        if(appId=="") return;
        if(clientKey=="") return;
        Extension.mainActivity.runOnUiThread(new Runnable() {
            public void run()
            {
                //Tapdaq.getInstance().loadVideo(Extension.mainActivity, TapdaqPlacement.TDPTagDefault, new VideoAdListener());
                Tapdaq.getInstance().loadVideo(Extension.mainActivity, tag, new VideoAdListener());
            }
        });
        Log.d("TapdaqEx","Load Video End ");
    }
    
    static public void showVideo(final String tag)
    {
        Log.d("TapdaqEx","Show Video Begin");
        if(appId=="") return;
        if(clientKey=="") return;
        Extension.mainActivity.runOnUiThread(new Runnable() {
            public void run()
            {
                //Tapdaq.getInstance().showVideo(Extension.mainActivity, TapdaqPlacement.TDPTagDefault, new VideoAdListener());
                Tapdaq.getInstance().showVideo(Extension.mainActivity, tag, new VideoAdListener());
            }
        });
        Log.d("TapdaqEx","Show Video End ");
    }
    
    static public void loadRewarded(final String tag)
    {
        Log.d("TapdaqEx","Load rewarded Begin");
        if(appId=="") return;
        if(clientKey=="") return;
        Extension.mainActivity.runOnUiThread(new Runnable() {
            public void run()
            {
                //Tapdaq.getInstance().loadRewardedVideo(Extension.mainActivity, TapdaqPlacement.TDPTagDefault, new RewardedAdListener());
                Tapdaq.getInstance().loadRewardedVideo(Extension.mainActivity, tag, new RewardedAdListener());
            }
        });
        Log.d("TapdaqEx","Load rewarded End ");
    }
    
    static public void showRewarded(final String tag)
    {
        Log.d("TapdaqEx","Show rewarded Begin");
        if(appId=="") return;
        if(clientKey=="") return;
        Extension.mainActivity.runOnUiThread(new Runnable() {
            public void run()
            {
                //Tapdaq.getInstance().showRewardedVideo(Extension.mainActivity, TapdaqPlacement.TDPTagDefault, new RewardedAdListener());
                Tapdaq.getInstance().showRewardedVideo(Extension.mainActivity, tag, new RewardedAdListener());
            }
        });
        Log.d("TapdaqEx","Show rewarded End ");
    }
    
    /////MoreApps
    
    private TMMoreAppsConfig getCustomMoreAppsConfig() {
        TMMoreAppsConfig config = new TMMoreAppsConfig();
        config.setHeaderText("More Games");
        config.setInstalledButtonText("Play");
        
        config.setHeaderTextColor(Color.WHITE);
        config.setHeaderColor(Color.DKGRAY);
        config.setHeaderCloseButtonColor(Color.BLACK);
        config.setBackgroundColor(Color.GRAY);
        
        config.setAppNameColor(Color.BLACK);
        config.setAppButtonColor(Color.BLACK);
        config.setAppButtonTextColor(Color.WHITE);
        config.setInstalledAppButtonColor(Color.WHITE);
        config.setInstalledAppButtonTextColor(Color.BLACK);
        return config;
    }
    
    static public void loadMore()
    {
        Log.d("TapdaqEx","Load More Apps Begin");
        if(appId=="") return;
        if(clientKey=="") return;
        Extension.mainActivity.runOnUiThread(new Runnable() {
            public void run()
            {
                Tapdaq.getInstance().loadMoreApps(Extension.mainActivity, instance.getCustomMoreAppsConfig(), new MoreAppsListener());
            }
        });
        Log.d("TapdaqEx","Load More Apps End ");
    }
    
    static public void showMore()
    {
        Log.d("TapdaqEx","Show More Apps Begin");
        if(appId=="") return;
        if(clientKey=="") return;
        Extension.mainActivity.runOnUiThread(new Runnable() {
            public void run()
            {
                Tapdaq.getInstance().showMoreApps(Extension.mainActivity, new MoreAppsListener());
            }
        });
        Log.d("TapdaqEx","Show More Apps End ");
    }
    
    
    /////End MoreApps

	
    
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
    static public boolean isInterstitialReady(final String tag)
    {
        return Tapdaq.getInstance().isInterstitialReady(Extension.mainActivity, tag);
    }
    
    static public boolean isVideoReady(final String tag)
    {
        return Tapdaq.getInstance().isVideoReady(Extension.mainActivity, tag);
    }
    
    static public boolean isRewardedReady(final String tag)
    {
         return Tapdaq.getInstance().isRewardedVideoReady(Extension.mainActivity, tag);
    }
    
    static public boolean isMoreReady()
    {
        return Tapdaq.getInstance().isMoreAppsReady(Extension.mainActivity);
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    @Override
    public void onDestroy() {
        if (banner != null) {
            banner.destroy(mainActivity);
        }
        super.onDestroy();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        Tapdaq.getInstance().onPause(mainActivity);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Tapdaq.getInstance().onResume(mainActivity);
    }
    
}

class InitListener extends TMInitListener {
    
    @Override
    public void didInitialise() {
        super.didInitialise();
        Log.i("Tapdaq Initialise", "didInitialise");
    }
}

class BannerAdListener extends TMAdListener {
    
    @Override
    public void didLoad() {
        Log.i("Tapdaq Banner", "didLoad");
        TapdaqEx.hideBanner();
        TapdaqEx.haxeCallback.call("onBannerDidLoad", new Object[] {});
    }
    
    @Override
    public void didFailToLoad(TMAdError tmAdError) {
        Log.i("Tapdaq Banner", "didFailToLoad " + tmAdError.getErrorMessage());
        TapdaqEx.haxeCallback.call("onBannerFailToLoad", new Object[] {});
    }
    
    @Override
    public void didClick() {
        Log.i("Tapdaq Banner", "didClick");
        TapdaqEx.haxeCallback.call("onBannerDidClick", new Object[] {});
    }
    
}

class InterstitialAdListener extends TMAdListener {
    
    @Override
    public void willDisplay() {
        Log.i("Tapdaq Interstitial", "willDisplay");
        TapdaqEx.haxeCallback.call("onInterstitialWillDisplay", new Object[] {});
    }
    
    @Override
    public void didDisplay() {
        Log.i("Tapdaq Interstitial", "didDisplay");
        TapdaqEx.haxeCallback.call("onInterstitialDidDisplay", new Object[] {});
    }
    
    @Override
    public void didClick() {
        Log.i("Tapdaq Interstitial", "didClick");
        TapdaqEx.haxeCallback.call("onInterstitialDidClick", new Object[] {});
    }
    
    @Override
    public void didClose() {
        Log.i("Tapdaq Interstitial", "didClose");
        TapdaqEx.haxeCallback.call("onInterstitialDidClose", new Object[] {});
    }
    
}

class VideoAdListener extends TMAdListener {
    
    @Override
    public void willDisplay() {
        Log.i("Tapdaq Video", "willDisplay");
        TapdaqEx.haxeCallback.call("onVideoWillDisplay", new Object[] {});
    }
    
    @Override
    public void didDisplay() {
        Log.i("Tapdaq Video", "didDisplay");
        TapdaqEx.haxeCallback.call("onVideoDidDisplay", new Object[] {});
    }
    
    @Override
    public void didClick() {
        Log.i("Tapdaq Video", "didClick");
        TapdaqEx.haxeCallback.call("onVideoDidClick", new Object[] {});
    }
    
    @Override
    public void didClose() {
        Log.i("Tapdaq Video", "didClose");
        TapdaqEx.haxeCallback.call("onVideoDidClose", new Object[] {});
    }
    
}

class RewardedAdListener extends TMAdListener {
    
    @Override
    public void willDisplay() {
        Log.i("Tapdaq Rewarded Video", "willDisplay");
        TapdaqEx.haxeCallback.call("onRewardedWillDisplay", new Object[] {});
    }
    
    @Override
    public void didDisplay() {
        Log.i("Tapdaq Rewarded Video", "didDisplay");
        TapdaqEx.haxeCallback.call("onRewardedDidDisplay", new Object[] {});
    }
    
    @Override
    public void didClick() {
        Log.i("Tapdaq Rewarded Video", "didClick");
        TapdaqEx.haxeCallback.call("onRewardedDidClick", new Object[] {});
    }
    
    @Override
    public void didClose() {
        Log.i("Tapdaq Rewarded Video", "didClose");
        TapdaqEx.haxeCallback.call("onRewardedDidClose", new Object[] {});
    }
    
    @Override
    public void didVerify(String location, String reward, int value, boolean reward_valid, Map<Object, Object> custom_data) {
        Log.i("MEDIATION-SAMPLE", String.format(Locale.ENGLISH, "didVerify %s %d %b %s", reward, value, reward_valid, custom_data.toString()));
        TapdaqEx.haxeCallback.call("onRewardedSucceeded", new Object[] {});
    }
    
}

class MoreAppsListener extends TMMoreAppsListener {
    @Override
    public void didLoad() {
        super.didLoad();
        TapdaqEx.haxeCallback.call("onMoreAppsDidLoad", new Object[] {});
    }
    
    @Override
    public void didFailToLoad(TMAdError error) {
        super.didFailToLoad(error);
        TapdaqEx.haxeCallback.call("onMoreAppsDidFailToLoad", new Object[] {});
    }
    
    @Override
    public void willDisplay() {
        super.willDisplay();
        TapdaqEx.haxeCallback.call("onMoreAppsWillDisplay", new Object[] {});
    }
    
    @Override
    public void didDisplay() {
        super.didDisplay();
        TapdaqEx.haxeCallback.call("onMoreAppsDidDisplay", new Object[] {});
    }
    
    @Override
    public void didClose() {
        super.didClose();
        TapdaqEx.haxeCallback.call("onMoreAppsDidClose", new Object[] {});
    }
}



