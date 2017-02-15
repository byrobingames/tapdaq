package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;

import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMAdapter;
import com.tapdaq.sdk.helpers.TLog;
import com.tapdaq.sdk.listeners.TMAdListenerBase;
import com.tapdaq.sdk.listeners.TMListenerHandler;
import com.tapdaq.sdk.listeners.TMRewardAdListenerBase;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.adunit.AdUnitActivity;
import com.unity3d.ads.adunit.AdUnitSoftwareActivity;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;

/**
 * Created by dominicroberts on 24/01/2017.
 */

public class TMUnityAdsAdapter extends TMAdapter {

    private String mRewardCurrency = "Reward";
    private double mRewardValue = 1.0;

    public TMUnityAdsAdapter(Context context){
        super(context);
    }

    public TMUnityAdsAdapter(Context context, String rewardCurrency, double rewardValue) {
        super(context);

        if (mRewardCurrency != null)
            mRewardCurrency = rewardCurrency;
        mRewardValue = rewardValue;
    }

    @Override
    public String getName() { return TMMediationNetworks.UNITY_ADS_NAME; }

    @Override
    public int getID() {
        return TMMediationNetworks.UNITY_ADS;
    }

    @Override
    public void initialise(Activity activity) {
        super.initialise(activity);

        if (mKeys != null) {
            UnityAds.initialize(activity, getAppId(activity), new UnityListener());
            mListener.onInitSuccess(activity, TMMediationNetworks.UNITY_ADS);
        }
    }

    @Override
    public boolean isInitialised(Context context) {
        return isActivityAvailable(context, AdUnitActivity.class)
                && isActivityAvailable(context, AdUnitSoftwareActivity.class)
                && UnityAds.isInitialized();
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return isInitialised(context) && getVideoId(context) != null && UnityAds.isReady(getVideoId(context));
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return isInitialised(context) && getRewardedVideoId(context) != null && UnityAds.isReady(getRewardedVideoId(context));
    }

    @Override
    public void loadVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (UnityAds.isReady(getVideoId(activity))) {
            TMListenerHandler.DidLoad(listener);
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Unity ad failed to load"));
        }
    }

    @Override
    public void loadRewardedVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (UnityAds.isReady(getRewardedVideoId(activity))) {
            TMListenerHandler.DidLoad(listener);
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Unity ad failed to load"));
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (UnityAds.isReady(getVideoId(activity))) {
            UnityAds.setListener(new UnityListener(listener));
            UnityAds.show(activity, getVideoId(activity));
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Unity ad failed to load"));
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (UnityAds.isReady(getRewardedVideoId(activity))) {
            UnityAds.setListener(new UnityListener(listener));
            UnityAds.show(activity, getRewardedVideoId(activity));
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Unity ad failed to load"));
        }
    }

    private class UnityListener implements IUnityAdsExtendedListener {

        private TMAdListenerBase mListener;

        private UnityListener() {

        }

        private UnityListener(TMAdListenerBase listenerBase) {
            mListener = listenerBase;
        }

        @Override
        public void onUnityAdsReady(String s) {
            TLog.debug("onUnityAdsReady: " + s);
        }

        @Override
        public void onUnityAdsStart(String s) {
            TMListenerHandler.DidDisplay(mListener);
        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
            TMListenerHandler.DidClose(mListener);
            if (mListener instanceof TMRewardAdListenerBase) {
                TMListenerHandler.DidVerify((TMRewardAdListenerBase)mListener, "", mRewardCurrency, mRewardValue);
            }

        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
            TLog.debug("onUnityAdsError: " + s);
            TMListenerHandler.DidFailToLoad(mListener, new TMAdError(0, s));
        }

        @Override
        public void onUnityAdsClick(String s) {
            TMListenerHandler.DidClick(mListener);
        }
    }
}
