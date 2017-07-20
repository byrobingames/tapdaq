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
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJPlacementVideoListener;
import com.tapjoy.Tapjoy;

/**
 * Created by dominicroberts on 30/03/2017.
 */

public class TMTapjoyAdapter extends TMAdapter {
    private static final String TAPDAQ_NETWORK_NAME = "tapdaq";
    private static final String TAPDAQ_ADAPTER_VERSION_NUMBER = "1.0.0";

    private TJPlacement mStaticAd;
    private TJPlacement mVideoAd;
    private TJPlacement mRewardedAd;

    private String mRewardCurrency;
    private double mRewardValue;

    public TMTapjoyAdapter(Context context) {
        super(context);
        mRewardCurrency = "Reward";
        mRewardValue = 1.0;
    }

    public TMTapjoyAdapter(Context context, String rewardCurrency, double rewardValue) {
        super(context);
        mRewardCurrency = rewardCurrency;
        mRewardValue = rewardValue;
    }

    public TMTapjoyAdapter setDebuggingEnabled(boolean enabled) {
        Tapjoy.setDebugEnabled(enabled);
        return this;
    }

    @Override
    public String getName() {
        return TMMediationNetworks.TAPJOY_NAME;
    }

    @Override
    public int getID() {
        return TMMediationNetworks.TAPJOY;
    }

    @Override
    public void initialise(final Activity activity) {
        super.initialise(activity);

        if (isInitialised(activity)) {
            if (activity != null) {
                mServiceListener.onInitSuccess(activity, getID());
            }
        } else if (activity != null) {
            if (getAppKey(activity) != null) {
                Tapjoy.connect(activity, getAppKey(activity), null, new TJConnectListener() {
                    @Override
                    public void onConnectSuccess() {
                        mServiceListener.onInitSuccess(activity, getID());
                    }

                    @Override
                    public void onConnectFailure() {
                        mServiceListener.onInitFailure(activity, getID(), new TMAdError(0, "Failed to connect to Tapjoy"));
                    }
                });
            } else {
                mServiceListener.onInitFailure(activity, getID(), new TMAdError(0, "Failed to connect to Tapjoy: Tapjoy SDK Key Missing"));
            }
            Tapjoy.setActivity(activity);
        } else {
            TLog.error("Failed to connect to Tapjoy: Activity is null");
        }
    }

    @Override
    public boolean isInitialised(Context context) {
        if (Tapjoy.isConnected()) {
            return true;
        } else {
            TLog.error("Tapjoy Not Connected");
            if (context != null) {
                if (getAppKey(context) == null) {
                    TLog.error("Tapjoy key missing");
                }
            }
        }
        return false;
    }

    @Override
    public boolean canDisplayInterstitial(Context context) {
        return getInterstitialId(context) != null;
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return getVideoId(context) != null;
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return getRewardedVideoId(context) != null;
    }

    @Override
    public boolean isStaticInterstitialReady(Activity activity) {
        return mStaticAd != null && mStaticAd.isContentReady();
    }

    @Override
    public boolean isVideoInterstitialReady(Activity activity) {
        return mVideoAd != null && mVideoAd.isContentReady();
    }

    @Override
    public boolean isRewardInterstitialReady(Activity activity) {
        return mRewardedAd != null && mRewardedAd.isContentReady();
    }

    @Override
    public void loadInterstitial(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        mStaticAd = Tapjoy.getPlacement(getInterstitialId(activity), new TapjoyListener(activity, shared_id, TMAdType.STATIC_INTERSTITIAL, placement, listener));
        mStaticAd.setMediationName(TAPDAQ_NETWORK_NAME);
        mStaticAd.setAdapterVersion(TAPDAQ_ADAPTER_VERSION_NUMBER);
        mStaticAd.requestContent();
    }

    @Override
    public void loadVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        mVideoAd = Tapjoy.getPlacement(getVideoId(activity), new TapjoyListener(activity, shared_id, TMAdType.VIDEO_INTERSTITIAL, placement, listener));
        mVideoAd.setMediationName(TAPDAQ_NETWORK_NAME);
        mVideoAd.setAdapterVersion(TAPDAQ_ADAPTER_VERSION_NUMBER);
        mVideoAd.requestContent();
    }

    @Override
    public void loadRewardedVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        mRewardedAd = Tapjoy.getPlacement(getRewardedVideoId(activity), new TapjoyListener(activity, shared_id, TMAdType.REWARD_INTERSTITIAL, placement, listener));
        mRewardedAd.setMediationName(TAPDAQ_NETWORK_NAME);
        mRewardedAd.setAdapterVersion(TAPDAQ_ADAPTER_VERSION_NUMBER);
        mRewardedAd.requestContent();
    }

    @Override
    public void showInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        if (isStaticInterstitialReady(activity)) {
            TJPlacementListener tjListener = mStaticAd.getListener();
            if (tjListener instanceof TapjoyListener) {
                ((TapjoyListener)tjListener).setListener(listener);
            }
            mStaticAd.showContent();
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (isVideoInterstitialReady(activity)) {
            TJPlacementListener tjListener = mVideoAd.getListener();
            if (tjListener instanceof TapjoyListener) {
                ((TapjoyListener)tjListener).setListener(listener);
            }
            mVideoAd.showContent();
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (isRewardInterstitialReady(activity)) {
            TJPlacementListener tjListener = mRewardedAd.getListener();
            if (tjListener instanceof TapjoyListener) {
                mRewardedAd.setVideoListener((TJPlacementVideoListener)tjListener);
                ((TapjoyListener)tjListener).setListener(listener);
            }
            mRewardedAd.showContent();
        }
    }

    private class TapjoyListener implements TJPlacementListener, TJPlacementVideoListener {
        private Activity mActivity;
        private TMAdListenerBase mListener;
        private int mType;
        private String mPlacement;
        private String mShared_id;

        TapjoyListener(Activity activity, String shared_id, int type, String placement, TMAdListenerBase listener) {
            mActivity = activity;
            mListener = listener;
            mType = type;
            mPlacement = placement;
            mShared_id = shared_id;
        }

        void setListener(TMAdListenerBase listener) { mListener = listener; }

        @Override
        public void onRequestSuccess(TJPlacement tjPlacement) {
            TLog.debug("onRequestSuccess");

            if (mActivity != null && !tjPlacement.isContentAvailable()) {
                TMAdError error = new TMAdError(0, "No Fill");

                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidFailToLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity), error.getErrorMessage());
                statsManager.finishAdRequest(mActivity, mShared_id, false);

                TMServiceErrorHandler.ServiceError(mActivity, mShared_id, getName(), mType, mPlacement, error, mListener);
                mActivity = null;
            }
        }

        @Override
        public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {
            TLog.debug("onRequestFailure");

            TMAdError error = new TMAdError(tjError.code, tjError.message);

            if (mActivity != null) {
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidFailToLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity), error.getErrorMessage());
                statsManager.finishAdRequest(mActivity, mShared_id, false);

                TMServiceErrorHandler.ServiceError(mActivity, mShared_id, getName(), mType, mPlacement, error, mListener);
                mActivity = null;
            }
        }

        @Override
        public void onContentReady(TJPlacement tjPlacement) {
            TLog.debug("onContentReady");
            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidLoad(mListener);
                    }
                });
            }

            if (mActivity != null) {
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidLoad(mActivity, getName(),isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
                statsManager.finishAdRequest(mActivity, mShared_id, true);
            }
        }

        @Override
        public void onContentShow(TJPlacement tjPlacement) {
            TLog.debug("onContentShow");
            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidDisplay(mListener);
                    }
                });
            }

            if (mActivity != null) {
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendImpression(mActivity, mShared_id, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
            }
        }

        @Override
        public void onContentDismiss(TJPlacement tjPlacement) {
            TLog.debug("onContentDismiss");
            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidClose(mListener);
                    }
                });

                reloadAd(mActivity, mType, mPlacement, mListener);
                mActivity = null;
            }
        }

        @Override
        public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {
            TLog.debug("onPurchaseRequest");
        }

        @Override
        public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {
            TLog.debug("onRewardRequest");
        }

        @Override
        public void onVideoStart(TJPlacement tjPlacement) {
            TLog.debug("onVideoStart");
        }

        @Override
        public void onVideoError(TJPlacement tjPlacement, String s) {
            TLog.debug("onVideoError " + s);
        }

        @Override
        public void onVideoComplete(TJPlacement tjPlacement) {
            TLog.debug("onVideoComplete");
            if (mActivity != null && mType == TMAdType.REWARD_INTERSTITIAL && mListener != null && mListener instanceof TMRewardAdListenerBase) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidVerify((TMRewardAdListenerBase)mListener, "", mRewardCurrency, mRewardValue);
                    }
                });
            }
        }
    }
}
