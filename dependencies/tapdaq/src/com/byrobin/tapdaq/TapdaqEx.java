/*
 *
 * Created by Robin Schaafsma
 * www.byrobingames.com
 *
 */

package com.byrobin.tapdaq;

import com.tapdaq.Tapdaq;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.util.Log;


import org.haxe.extension.Extension;


public class TapdaqEx extends Extension {


	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
    
    private static boolean interstitialLoaded = false;
    private static boolean interstitialFailedToLoad = false;
    private static boolean interstitialClosed =false;
	private static String appId=null;
    private static String clientKey=null;
    private static String testMode=null;


	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////

	static public void init(final String appId, final String clientKey, final String testMode){
		TapdaqEx.appId=appId;
        TapdaqEx.clientKey=clientKey;
        TapdaqEx.testMode=testMode;
		
		Extension.mainActivity.runOnUiThread(new Runnable() {
            public void run() 
			{
                
				Log.d("TapdaqEx","Init Tapdaq");
                
                if(testMode == "YES"){
                    //to do
                }
                
                int orientation = getScreenOrientation();
                
                if(orientation == 1){
                    Tapdaq.tapdaq().initializeFixedPortrait(appId, clientKey, Extension.mainActivity);
                }else if(orientation == 2){
                    Tapdaq.tapdaq().initializeFixedLandscape(appId, clientKey, Extension.mainActivity);
                }else{
                    Tapdaq.tapdaq().initialize(appId, clientKey, Extension.mainActivity);
                }
                
                //setCallbacks
                //setupCallbacks();
                
			}
		});	
	}

	static public void showInterstitial() {
        Log.d("TapdagEx","Show Interstitial Begin");
		if(appId=="") return;
        if(clientKey=="") return;
		Extension.mainActivity.runOnUiThread(new Runnable() {
			public void run()
            {
                    Tapdaq.tapdaq().displayInterstitial(Extension.mainActivity);
                
            }
		});
		Log.d("TapdaqEx","Show Interstitial End");
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
    
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
    
    static public void setupCallbacks() {
        
        /* STATUS CALLBACKS
         * Listener callbacks apply to the general lifecycle of an ad.
         */
        
     //to do
        
    }
    
    static public int getScreenOrientation(){
        //get the orientation of device
        Display getOrient = mainActivity.getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if(getOrient.getWidth()==getOrient.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
        } else{
            if(getOrient.getWidth() < getOrient.getHeight()){
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }
    
}
