package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;

import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.analytics.TMStatsManager;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMAdType;
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
    private UnityListener mUnityListener;

    private UnityListener getUnityListener(Activity activity) {
        if (mUnityListener != null)
            mUnityListener.destroy();
        mUnityListener = new UnityListener(activity);
        return mUnityListener;
    }

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
            UnityAds.initialize(activity, getAppId(activity), getUnityListener(activity));
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
            UnityAds.setListener(new UnityListener(activity, false, placement, listener));
            UnityAds.show(activity, getVideoId(activity));
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Unity ad failed to load"));
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (UnityAds.isReady(getRewardedVideoId(activity))) {
            UnityAds.setListener(new UnityListener(activity, true, placement, listener));
            UnityAds.show(activity, getRewardedVideoId(activity));
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Unity ad failed to load"));
        }
    }

    private class UnityListener implements IUnityAdsExtendedListener {

        private TMAdListenerBase mListener;
        private boolean mRewarded;
        private String mPlacement;
        private Activity mActivity;

        private UnityListener(Activity activity) {
            mActivity = activity;
        }

        private UnityListener(Activity activity, boolean rewarded, String tag, TMAdListenerBase listenerBase) {
            mActivity = activity;
            mRewarded = rewarded;
            mListener = listenerBase;
            mPlacement = tag;
        }

        void destroy() {
            mActivity = null;
        }

        @Override
        public void onUnityAdsReady(String s) {
            TLog.debug("onUnityAdsReady: " + s);

            if (mActivity != null)
                new TMStatsManager(mActivity).sendDidLoad(mActivity, getName(), TMAdType.getString((mRewarded ? TMAdType.REWARD_INTERSTITIAL : TMAdType.VIDEO_INTERSTITIAL)), mPlacement, getVersionID(mActivity));
        }

        @Override
        public void onUnityAdsStart(String s) {
            TMListenerHandler.DidDisplay(mListener);
            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, getName(), TMAdType.getString((mRewarded ? TMAdType.REWARD_INTERSTITIAL : TMAdType.VIDEO_INTERSTITIAL)), mPlacement, getVersionID(mActivity));
        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
            TMListenerHandler.DidClose(mListener);
            if (mRewarded && mListener instanceof TMRewardAdListenerBase) {
                TMListenerHandler.DidVerify((TMRewardAdListenerBase)mListener, "", mRewardCurrency, mRewardValue);
            }
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
            TLog.debug("onUnityAdsError: " + s);
            TMListenerHandler.DidFailToLoad(mListener, new TMAdError(0, s));
            if (mActivity != null)
                new TMStatsManager(mActivity).sendDidFailToLoad(mActivity, getName(), TMAdType.getString(TMAdType.VIDEO_INTERSTITIAL), mPlacement, getVersionID(mActivity), unityAdsError.name());
        }

        @Override
        public void onUnityAdsClick(String s) {
            TMListenerHandler.DidClick(mListener);
        }
    }
}
