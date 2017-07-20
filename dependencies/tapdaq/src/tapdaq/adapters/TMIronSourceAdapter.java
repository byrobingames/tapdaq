package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.ironsource.sdk.controller.ControllerActivity;
import com.ironsource.sdk.controller.InterstitialActivity;
import com.ironsource.sdk.controller.OpenUrlActivity;
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

/**
 * Created by dominicroberts on 31/05/2017.
 */

public class TMIronSourceAdapter extends TMAdapter {
    public TMIronSourceAdapter(Context context) {
        super(context);
    }

    @Override
    public String getName() {
        return TMMediationNetworks.IRONSOURCE_NAME;
    }

    @Override
    public int getID() {
        return TMMediationNetworks.IRONSOURCE;
    }

    @Override
    public void initialise(Activity activity) {
        super.initialise(activity);

        if (activity != null && getAppKey(activity) != null) {
            IronSource.init(activity, getAppKey(activity));
            mServiceListener.onInitSuccess(activity, getID());
        }
    }
    
    @Override
    public boolean isInitialised(Context context) {
        return getAppKey(context) != null
                && isActivityAvailable(context, ControllerActivity.class)
                && isActivityAvailable(context, InterstitialActivity.class)
                && isActivityAvailable(context, OpenUrlActivity.class);
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return isInitialised(context);
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return isInitialised(context);
    }

    @Override
    public boolean isVideoInterstitialReady(Activity activity) {
        return IronSource.isInterstitialReady();
    }

    @Override
    public boolean isRewardInterstitialReady(Activity activity) {
        return IronSource.isRewardedVideoAvailable();
    }

    @Override
    public void loadVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        IronSource.setInterstitialListener(new IronSourceListener(activity, shared_id, TMAdType.VIDEO_INTERSTITIAL, placement, listener));
        IronSource.loadInterstitial();
    }

    @Override
    public void loadRewardedVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        TMStatsManager statsManager = new TMStatsManager(activity);
        if(IronSource.isRewardedVideoAvailable()) {
            //Success
            TMListenerHandler.DidLoad(listener);
            setSharedId(getSharedKey(TMAdType.REWARD_INTERSTITIAL, placement), shared_id);
            statsManager.finishAdRequest(activity, shared_id, true);
        } else {
            //Fail
            TMServiceErrorHandler.ServiceError(activity, shared_id, getName(), TMAdType.REWARD_INTERSTITIAL, placement, new TMAdError(0, "IronSource Rewarded Ad Unavailable"), listener);
            statsManager.finishAdRequest(activity, shared_id, false);
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (IronSource.isInterstitialReady()) {
            IronSource.setInterstitialListener(new IronSourceListener(activity, getSharedId(getSharedKey(TMAdType.VIDEO_INTERSTITIAL, placement)), TMAdType.VIDEO_INTERSTITIAL, placement, listener));
            IronSource.showInterstitial(getVideoId(activity));
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if(IronSource.isRewardedVideoAvailable()) {
            IronSourceListener l = new IronSourceListener(activity, getSharedId(getSharedKey(TMAdType.REWARD_INTERSTITIAL, placement)), TMAdType.REWARD_INTERSTITIAL, placement, listener);
            IronSource.setInterstitialListener(l);
            IronSource.setRewardedVideoListener(l);
            IronSource.showRewardedVideo(getRewardedVideoId(activity));
        }
    }

    private class IronSourceListener implements InterstitialListener, RewardedVideoListener {

        private Activity mActivity;
        private TMAdListenerBase mListener;
        private String mSharedId;
        private int mType;
        private String mPlacement;

        IronSourceListener(Activity activity, String sharedId, int type, String placement, TMAdListenerBase listener) {
            mActivity = activity;
            mListener = listener;
            mSharedId = sharedId;
            mType = type;
            mPlacement = placement;
        }
        @Override
        public void onInterstitialAdReady() {
            TLog.debug("onInterstitialAdReady");

            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidLoad(mListener);
                    }
                });
            }

            if (mActivity != null) {
                setSharedId(getSharedKey(mType, mPlacement), mSharedId);
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
                statsManager.finishAdRequest(mActivity, mSharedId, true);
            }
            mActivity = null;
        }

        @Override
        public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
            TLog.debug("onInterstitialAdLoadFailed");
            TMAdError error = new TMAdError(ironSourceError.getErrorCode(), ironSourceError.getErrorMessage());
            TMServiceErrorHandler.ServiceError(mActivity, mSharedId, getName(), mType, mPlacement, error, mListener);

            if (mActivity != null) {
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidFailToLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity), error.getErrorMessage());
                statsManager.finishAdRequest(mActivity, mSharedId, false);
            }
            mActivity = null;
        }

        @Override
        public void onInterstitialAdOpened() {
            TLog.debug("onInterstitialAdOpened");
        }

        @Override
        public void onInterstitialAdClosed() {
            TLog.debug("onInterstitialAdClosed");

            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidClose(mListener);
                        reloadAd(mActivity, mType, mPlacement, mListener);
                    }
                });
            }

            mActivity = null;
        }

        @Override
        public void onInterstitialAdShowSucceeded() {
            TLog.debug("onInterstitialAdShowSucceeded");
            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidDisplay(mListener);
                    }
                });
            }

            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, mSharedId, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
        }

        @Override
        public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
            TLog.debug("onInterstitialAdShowFailed " + ironSourceError.getErrorMessage());
            mActivity = null;
        }

        @Override
        public void onInterstitialAdClicked() {
            TLog.debug("onInterstitialAdClicked");

            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidClick(mListener);
                    }
                });
            }
        }

        //Rewarded Callbacks

        @Override
        public void onRewardedVideoAdOpened() {
            TLog.debug("onRewardedVideoAdOpened");
            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidDisplay(mListener);
                    }
                });
            }

            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, mSharedId, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
        }

        @Override
        public void onRewardedVideoAdClosed() {
            TLog.debug("onRewardedVideoAdClosed");
            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidClose(mListener);
                        reloadAd(mActivity, mType, mPlacement, mListener);
                    }
                });
            }


            mActivity = null;
        }

        @Override
        public void onRewardedVideoAvailabilityChanged(boolean b) {
            TLog.debug("onRewardedVideoAvailabilityChanged");
        }

        @Override
        public void onRewardedVideoAdStarted() {
            TLog.debug("onRewardedVideoAdStarted");
        }

        @Override
        public void onRewardedVideoAdEnded() {
            TLog.debug("onRewardedVideoAdEnded");
        }

        @Override
        public void onRewardedVideoAdRewarded(final Placement placement) {
            TLog.debug("onRewardedVideoAdRewarded");

            if (mActivity != null && mListener instanceof TMRewardAdListenerBase) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidVerify((TMRewardAdListenerBase) mListener, "", placement.getRewardName(), placement.getRewardAmount());
                    }
                });
            }
        }

        @Override
        public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {
            TLog.debug("onRewardedVideoAdShowFailed");
            mActivity = null;
        }
    }
}
