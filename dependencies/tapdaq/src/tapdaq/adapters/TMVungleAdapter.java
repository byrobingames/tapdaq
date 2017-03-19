package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.analytics.TMStatsManager;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMAdType;
import com.tapdaq.sdk.common.TMAdapter;
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
            mEventListener = new VungleEventListener(activity, false, null, null);
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
    public void initialise(Activity activity) {
        super.initialise(activity);

        if (activity != null && mKeys != null) {
            getVunglePub().init(activity, mKeys.getApp_id());
            getVunglePub().setEventListeners(getEventListener(activity));
            mListener.onInitSuccess(activity, TMMediationNetworks.VUNGLE);
        }
    }

    @Override
    public boolean isInitialised(Context context) {
        return isActivityAvailable(context, VideoFullScreenAdActivity.class) && isActivityAvailable(context, MraidFullScreenAdActivity.class) && getAppId(context) != null;
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return isInitialised(context) && getVunglePub().isAdPlayable();
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return isInitialised(context) && getVunglePub().isAdPlayable();
    }

    @Override
    public void loadVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (getVunglePub().isAdPlayable()) {
            TMListenerHandler.DidLoad(listener);
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Vungle ad failed to load"));
        }
    }

    @Override
    public void loadRewardedVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (getVunglePub().isAdPlayable()) {
            TMListenerHandler.DidLoad(listener);
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Vungle reward ad failed to load"));
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (getVunglePub().isAdPlayable()) {
            getVunglePub().setEventListeners(new VungleEventListener(activity, false, placement, listener), getEventListener(activity));
            getVunglePub().playAd();
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (getVunglePub().isAdPlayable()) {
            getVunglePub().setEventListeners(new VungleEventListener(activity, true, placement, listener), getEventListener(activity));
            AdConfig config = new AdConfig();
            config.setBackButtonImmediatelyEnabled(false);
            config.setIncentivized(true);
            getVunglePub().playAd(config);
        }
    }

    private class VungleEventListener implements com.vungle.publisher.EventListener {

        private TMAdListenerBase mListener;
        private boolean mReward = false;
        private String mPlacement;
        private Activity mActivity;

        VungleEventListener(Activity activity, boolean reward, String tag, TMAdListenerBase listener) {
            mActivity = activity;
            mListener = listener;
            mReward = reward;
            mPlacement = tag;
        }

        @Override
        public void onAdEnd(final boolean wasSuccessfulView, final boolean wasCallToActionClicked) {

            if (mListener != null && mActivity != null) {
                new Handler(mActivity.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (wasCallToActionClicked)
                            TMListenerHandler.DidClick(mListener);

                        TMListenerHandler.DidClose(mListener);

                        if (wasSuccessfulView && mReward && mListener instanceof TMRewardAdListenerBase) {
                            TMListenerHandler.DidVerify((TMRewardAdListenerBase) mListener, "", mRewardCurrency, mRewardValue);
                        }
                    }});
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
                new TMStatsManager(mActivity).sendImpression(mActivity, getName(), TMAdType.getString((mReward ? TMAdType.REWARD_INTERSTITIAL : TMAdType.STATIC_INTERSTITIAL)), mPlacement, getVersionID(mActivity));
            }
        }

        @Override
        public void onAdUnavailable(String s) {
            TLog.debug(String.format(Locale.ENGLISH, "onAdUnavailable %s", s));

            if (mActivity != null && mListener == null)
                new TMStatsManager(mActivity).sendDidFailToLoad(mActivity, getName(), TMAdType.getString((mReward ? TMAdType.REWARD_INTERSTITIAL : TMAdType.STATIC_INTERSTITIAL)), mPlacement, getVersionID(mActivity), s);
        }

        @Override
        public void onAdPlayableChanged(boolean isAdPlayable) {
            TLog.debug(String.format(Locale.ENGLISH, "onAdPlayableChanged %b", isAdPlayable));
            if (isAdPlayable && mListener == null && mActivity != null) {
                new TMStatsManager(mActivity).sendDidLoad(mActivity, getName(), TMAdType.getString(TMAdType.VIDEO_INTERSTITIAL), null, getVersionID(mActivity));
            }
        }

        @Override
        public void onVideoView(boolean b, int i, int i1) {
            TLog.debug(String.format(Locale.ENGLISH, "onVungleView %b %d %d", b, i, i1));
        }
    }
}
