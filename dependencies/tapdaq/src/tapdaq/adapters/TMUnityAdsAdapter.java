package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;

import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.analytics.TMStatsManager;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMAdType;
import com.tapdaq.sdk.common.TMAdapter;
import com.tapdaq.sdk.common.TMServiceErrorHandler;
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
    private boolean mHasInitialised = false;

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

        if (activity != null && getAppId(activity) != null) {
            UnityAds.initialize(activity, getAppId(activity), getUnityListener(activity));
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
        return isInitialised(context) && getVideoId(context) != null;
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return isInitialised(context) && getRewardedVideoId(context) != null;
    }


    public boolean isVideoInterstitialReady(Activity activity) {
        return UnityAds.isReady(getVideoId(activity));
    }

    @Override
    public boolean isRewardInterstitialReady(Activity activity) {
        return UnityAds.isReady(getRewardedVideoId(activity));
    }

    @Override
    public void loadVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        TMStatsManager statsManager = new TMStatsManager(activity);

        if (isVideoInterstitialReady(activity)) {
            TMListenerHandler.DidLoad(listener);
            setSharedId(getSharedKey(TMAdType.VIDEO_INTERSTITIAL, placement), shared_id);
            statsManager.finishAdRequest(activity, shared_id, true);
        } else {
            if(UnityAds.getPlacementState().equals(UnityAds.PlacementState.WAITING)) {
                //Waiting
                UnityAds.setListener(new UnityListener(activity, shared_id, TMAdType.VIDEO_INTERSTITIAL, placement, true, listener));
            } else {
                TMServiceErrorHandler.ServiceError(activity, shared_id, getName(), TMAdType.VIDEO_INTERSTITIAL, placement, new TMAdError(0, "Unity video ad failed to load"), listener);
                statsManager.finishAdRequest(activity, shared_id, false);
            }
        }
    }

    @Override
    public void loadRewardedVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        TMStatsManager statsManager = new TMStatsManager(activity);

        if (isRewardInterstitialReady(activity)) {
            TMListenerHandler.DidLoad(listener);
            setSharedId(getSharedKey(TMAdType.REWARD_INTERSTITIAL, placement), shared_id);
            statsManager.finishAdRequest(activity, shared_id, true);
        } else {
            if(UnityAds.getPlacementState().equals(UnityAds.PlacementState.WAITING)) {
                //Waiting
                UnityAds.setListener(new UnityListener(activity, shared_id, TMAdType.REWARD_INTERSTITIAL, placement, true, listener));
            } else {
                TMServiceErrorHandler.ServiceError(activity, shared_id, getName(), TMAdType.REWARD_INTERSTITIAL, placement, new TMAdError(0, "Unity rewarded video ad failed to load"), listener);
                statsManager.finishAdRequest(activity, shared_id, false);
            }
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (UnityAds.isReady(getVideoId(activity))) {
            UnityAds.setListener(new UnityListener(activity, getSharedId(getSharedKey(TMAdType.VIDEO_INTERSTITIAL, placement)), TMAdType.VIDEO_INTERSTITIAL, placement, false, listener));
            UnityAds.show(activity, getVideoId(activity));
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Unity video ad failed to show"));
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (UnityAds.isReady(getRewardedVideoId(activity))) {
            UnityAds.setListener(new UnityListener(activity, getSharedId(getSharedKey(TMAdType.REWARD_INTERSTITIAL, placement)), TMAdType.REWARD_INTERSTITIAL, placement, false, listener));
            UnityAds.show(activity, getRewardedVideoId(activity));
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Unity rewarded video ad failed to show"));
        }
    }

    private class UnityListener implements IUnityAdsExtendedListener {

        private TMAdListenerBase mListener;
        private int mType;
        private String mPlacement;
        private Activity mActivity;
        private String mShared_id;
        private Boolean mIsLoading = false;

        private UnityListener(Activity activity) {
            mActivity = activity;
        }

        private UnityListener(Activity activity, String shared_id, int type, String tag, boolean isLoading, TMAdListenerBase listenerBase) {
            mActivity = activity;
            mType = type;
            mListener = listenerBase;
            mPlacement = tag;
            mShared_id = shared_id;
            mIsLoading = isLoading;
        }

        void destroy() {
            mActivity = null;
        }

        @Override
        public void onUnityAdsReady(String s) {
            TLog.debug("onUnityAdsReady: " + s);
        }

        @Override
        public void onUnityAdsStart(String s) {
            TMListenerHandler.DidDisplay(mListener);
            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, mShared_id, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
            if (mType == TMAdType.REWARD_INTERSTITIAL && mListener instanceof TMRewardAdListenerBase) {
                TMListenerHandler.DidVerify((TMRewardAdListenerBase)mListener, "", mRewardCurrency, mRewardValue);
            }
            TMListenerHandler.DidClose(mListener);
            reloadAd(mActivity, mType, mPlacement, mListener);
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String errorStr) {
            TLog.debug("onUnityAdsError: " + errorStr);
            if(!mHasInitialised) {
                mServiceListener.onInitFailure(mActivity, TMMediationNetworks.UNITY_ADS, new TMAdError(0, errorStr));
            }
        }


                @Override
        public void onUnityAdsPlacementStateChanged(String placement, UnityAds.PlacementState oldState, UnityAds.PlacementState newState) {
            TLog.debug("onUnityAdsPlacementStateChanged: " + placement + " OldState: " + oldState.name() + " NewState: " + newState.name());

            if (!mHasInitialised) {
                if(newState == UnityAds.PlacementState.WAITING) {
                    mServiceListener.onInitSuccess(mActivity, TMMediationNetworks.UNITY_ADS);
                } else {
                    mServiceListener.onInitFailure(mActivity, TMMediationNetworks.UNITY_ADS, new TMAdError(0, placement));
                }
                mHasInitialised = true;
                mActivity = null;
                mListener = null;
            } else if(mIsLoading) {
                if(newState == UnityAds.PlacementState.READY) {
                    if (mListener != null) {
                        TMListenerHandler.DidLoad(mListener);
                        mListener = null;
                    }

                    if (mActivity != null) {
                        TMStatsManager statsManager = new TMStatsManager(mActivity);
                        statsManager.finishAdRequest(mActivity, mShared_id, true);
                        statsManager.sendDidLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
                    }

                    mActivity = null;
                    mListener = null;
                } else {
                    if(mListener != null)
                        TMListenerHandler.DidFailToLoad(mListener, new TMAdError(0, placement));
                    if (mActivity != null) {
                        TMStatsManager statsManager = new TMStatsManager(mActivity);
                        statsManager.finishAdRequest(mActivity, mShared_id, false);
                        TMServiceErrorHandler.ServiceError(mActivity, mShared_id, getName(), mType, placement, new TMAdError(0, "Unity ad failed to load"), mListener);
                    }
                    mActivity = null;
                    mListener = null;
                }
            }
        }

        @Override
        public void onUnityAdsClick(String s) {
            TMListenerHandler.DidClick(mListener);
        }
    }
}
