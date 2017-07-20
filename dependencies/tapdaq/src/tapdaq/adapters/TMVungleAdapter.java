package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

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
import com.vungle.publisher.AdConfig;
import com.vungle.publisher.MraidFullScreenAdActivity;
import com.vungle.publisher.VideoFullScreenAdActivity;
import com.vungle.publisher.VunglePub;


import java.util.Locale;

/**
 * Created by dominicroberts on 24/01/2017.
 */

public class TMVungleAdapter extends TMAdapter {
    private String mRewardCurrency = "Reward";
    private double mRewardValue = 1.0;

    private VungleEventListener mEventListener;

    private VunglePub getVunglePub() { return VunglePub.getInstance(); }

    private VungleEventListener getEventListener(Activity activity) {
        if (mEventListener == null)
            mEventListener = new VungleEventListener(activity, null, TMAdType.VIDEO_INTERSTITIAL, null, null);
        return mEventListener;
    }

    public TMVungleAdapter(Context context){
        super(context);
    }

    public TMVungleAdapter(Context context, String rewardCurrency, double rewardValue) {
        super(context);

        if (mRewardCurrency != null)
            mRewardCurrency = rewardCurrency;
        mRewardValue = rewardValue;
    }

    @Override
    public String getName() {
        return TMMediationNetworks.VUNGLE_NAME;
    }

    @Override
    public int getID() {
        return TMMediationNetworks.VUNGLE;
    }

    @Override
    public void initialise(final Activity activity) {
        super.initialise(activity);

        if (activity != null && getAppId(activity) != null) {
            try {
                final VunglePub vunglePub = getVunglePub();
                final String appid = getAppId(activity);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vunglePub.init(activity, appid);
                        vunglePub.setEventListeners(getEventListener(activity));
                        mServiceListener.onInitSuccess(activity, getID());
                    }
                });


            } catch (Exception e) {
                TLog.error(e);
                mServiceListener.onInitFailure(activity, getID(), new TMAdError(0, "Vungle init failed"));
            }
        }
    }

    @Override
    public boolean isInitialised(Context context) {
        return isActivityAvailable(context, VideoFullScreenAdActivity.class) && isActivityAvailable(context, MraidFullScreenAdActivity.class) && getAppId(context) != null;
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return isInitialised(context);
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return isInitialised(context);
    }

    public boolean isVideoInterstitialReady(Activity activity) {
        return getVunglePub().isAdPlayable();
    }

    @Override
    public boolean isRewardInterstitialReady(Activity activity) {
        return getVunglePub().isAdPlayable();
    }

    @Override
    public void loadVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        TMStatsManager statsManager = new TMStatsManager(activity);

        if (isVideoInterstitialReady(activity)) {
            TMListenerHandler.DidLoad(listener);
            setSharedId(getSharedKey(TMAdType.VIDEO_INTERSTITIAL, placement), shared_id);
            statsManager.finishAdRequest(activity, shared_id, true);
        } else {
            TMAdError error =  new TMAdError(0, "Vungle video ad failed to load");
            TMServiceErrorHandler.ServiceError(activity, shared_id, getName(), TMAdType.VIDEO_INTERSTITIAL, placement, error, listener);
            statsManager.finishAdRequest(activity, shared_id, false);
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
            TMAdError error =  new TMAdError(0, "Vungle reward video ad failed to load");
            TMServiceErrorHandler.ServiceError(activity, shared_id, getName(), TMAdType.REWARD_INTERSTITIAL, placement, error, listener);
            statsManager.finishAdRequest(activity, shared_id, false);
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (isVideoInterstitialReady(activity)) {
            getVunglePub().setEventListeners(new VungleEventListener(activity, getSharedId(getSharedKey(TMAdType.VIDEO_INTERSTITIAL, placement)), TMAdType.VIDEO_INTERSTITIAL, placement, listener), getEventListener(activity));
            getVunglePub().playAd();
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (isRewardInterstitialReady(activity)) {
            getVunglePub().setEventListeners(new VungleEventListener(activity, getSharedId(getSharedKey(TMAdType.REWARD_INTERSTITIAL, placement)), TMAdType.REWARD_INTERSTITIAL, placement, listener), getEventListener(activity));

            AdConfig config = new AdConfig();
            config.setBackButtonImmediatelyEnabled(false);
            config.setIncentivized(true);
            getVunglePub().playAd(config);
        }
    }

    private class VungleEventListener implements com.vungle.publisher.EventListener {

        private TMAdListenerBase mListener;
        private int mType;
        private String mPlacement;
        private Activity mActivity;
        private String mShared_id;

        VungleEventListener(Activity activity, String shared_id, int type, String tag, TMAdListenerBase listener) {
            mActivity = activity;
            mListener = listener;
            mType = type;
            mPlacement = tag;
            mShared_id = shared_id;
        }

        @Override
        public void onAdEnd(final boolean wasSuccessfulView, final boolean wasCallToActionClicked) {

            if (mListener != null) {
                if (mActivity != null) {
                    new Handler(mActivity.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (wasCallToActionClicked)
                                TMListenerHandler.DidClick(mListener);

                            if (wasSuccessfulView && mType == TMAdType.REWARD_INTERSTITIAL && mListener instanceof TMRewardAdListenerBase) {
                                TMListenerHandler.DidVerify((TMRewardAdListenerBase) mListener, "", mRewardCurrency, mRewardValue);
                            }
                            
                            TMListenerHandler.DidClose(mListener);
                        }
                    });
                    reloadAd(mActivity, mType, mPlacement, mListener);
                }
            }

            getVunglePub().removeEventListeners(this);
        }

        @Override
        public void onAdStart() {
            if (mListener != null && mActivity != null) {
                new Handler(mActivity.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidDisplay(mListener);
                    }});
                new TMStatsManager(mActivity).sendImpression(mActivity, mShared_id, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
            }
        }

        @Override
        public void onAdUnavailable(String s) {
            TLog.debug(String.format(Locale.ENGLISH, "onAdUnavailable %s", s));

            if (mActivity != null && mListener == null)
                new TMStatsManager(mActivity).sendDidFailToLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity), s);
        }

        @Override
        public void onAdPlayableChanged(boolean isAdPlayable) {
            TLog.debug(String.format(Locale.ENGLISH, "onAdPlayableChanged %b", isAdPlayable));
            if (isAdPlayable && mListener == null && mActivity != null) {
                new TMStatsManager(mActivity).sendDidLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), null, getVersionID(mActivity));
            }
        }

        @Override
        public void onVideoView(boolean b, int i, int i1) {
            TLog.debug(String.format(Locale.ENGLISH, "onVungleView %b %d %d", b, i, i1));
        }
    }
}
