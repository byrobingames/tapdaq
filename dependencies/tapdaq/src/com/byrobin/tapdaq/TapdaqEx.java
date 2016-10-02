/*
 *
 * Created by Robin Schaafsma
 * www.byrobingames.com
 *
 */

package com.byrobin.tapdaq;

import com.tapdaq.sdk.*;

import android.content.Context;
import android.util.Log;

import org.haxe.extension.Extension;


public class TapdaqEx extends Extension {


	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
    
    public static boolean interstitialLoaded = false;
    public static boolean interstitialFailedToLoad = false;
    public static boolean interstitialClosed =false;
	private static String appId=null;
    private static String clientKey=null;
    private static String testMode=null;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    

	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////

	static public void init(final String appId, final String clientKey, final String testMode){
        
		TapdaqEx.appId=appId;
        TapdaqEx.clientKey=clientKey;
        TapdaqEx.testMode=testMode;
		
		Extension.mainActivity.runOnUiThread(new Runnable() {
            public void run() 
			{
				Log.d("TapdaqEx","Init Tapdaq" + testMode);
                
                if (testMode.equals("YES")){
                    
                    Log.d("TapdaqEx","Testmode");
                    TapdaqConfig config = new TapdaqConfig(Extension.mainActivity);
                    config.withTestAdvertsEnabled(true);
                    
                    Tapdaq.tapdaq().initialize(Extension.mainActivity,
                                               appId,
                                               clientKey,
                                               new TapCallbacks(Extension.mainActivity),
                                               config);
                }else{
                    Log.d("TapdaqEx","Releasemode");
                    TapdaqConfig config = new TapdaqConfig(Extension.mainActivity);
                    config.withTestAdvertsEnabled(false);
                    
                    Tapdaq.tapdaq().initialize(Extension.mainActivity,
                                               appId,
                                               clientKey,
                                               new TapCallbacks(Extension.mainActivity),
                                               config);
                    
                }
                
			}
		});	
	}

	static public void showInterstitial()
    {
        Log.d("TapdaqEx","Show Interstitial Begin");
		if(appId=="") return;
        if(clientKey=="") return;
		Extension.mainActivity.runOnUiThread(new Runnable() {
			public void run()
            {
                Tapdaq.tapdaq().displayInterstitial(Extension.mainActivity);
            }
		});
		Log.d("TapdaqEx","Show Interstitial End ");
	}
	
    
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
    static public boolean interstitialIsLoaded()
    {
        if (interstitialLoaded)
        {
            interstitialLoaded = false;
            return true;
        }
        return false;
    }
    
    static public boolean interstitialFailedToLoad()
    {
        if (interstitialFailedToLoad)
        {
            interstitialFailedToLoad = false;
            return true;
        }
        return false;
    }
    
    static public boolean interstitialClosed()
    {
        if (interstitialClosed)
        {
            interstitialClosed = false;
            return true;
        }
        return false;
    }

    
}

class TapCallbacks extends TapdaqCallbacks {
    
    public final Context context;
    
    public TapCallbacks(final Context context) {
        
        this.context = context;
    }
    
    @Override
    public void hasLandscapeInterstitialAvailable()
    {
        TapdaqEx.interstitialLoaded = true;
    }
    
    @Override
    public void hasPortraitInterstitialAvailable()
    {
        TapdaqEx.interstitialLoaded = true;
    }
     
    @Override
    public void didFailToDisplayInterstitial()
    {
        TapdaqEx.interstitialFailedToLoad = true;
    }
     
    @Override
    public void didCloseInterstitial()
    {
        TapdaqEx.interstitialClosed = true;
    }
    
}
