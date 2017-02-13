package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMAdapter;
import com.tapdaq.sdk.helpers.TLog;
import com.tapdaq.sdk.listeners.TMAdListenerBase;
import com.tapdaq.sdk.listeners.TMRewardAdListenerBase;
import com.tapdaq.sdk.model.TMAdSize;
import com.tapdaq.sdk.model.launch.TMNetworkCredentials;
import com.tapdaq.sdk.storage.Storage;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dominicroberts on 24/01/2017.
 */

public class TMUnityAdsAdapter implements TMAdapter {

    private String mRewardCurrency = "Reward";
    private double mRewardValue = 1.0;

    private AdapterListener mListener;

    private TMNetworkCredentials mKeys;
    private List<String> mPlacements;

    public TMUnityAdsAdapter(){
        super();
        mPlacements = new ArrayList<String>();
    }

    public TMUnityAdsAdapter(String rewardCurrency, double rewardValue) {
        super();
        mPlacements = new ArrayList<String>();

        if (mRewardCurrency != null)
            mRewardCurrency = rewardCurrency;
        mRewardValue = rewardValue;
    }

    @Override
    public String getName() {
        return TMMediationNetworks.UNITY_ADS_NAME  + "_Adapter";
    }

    @Override
    public int getID() {
        return TMMediationNetworks.UNITY_ADS;
    }

    @Override
    public String getVersionID(Context context) {
        if (mKeys != null && mKeys.getVersion_id() != null)
            return mKeys.getVersion_id();
        else
            return new Storage(context).getString("UNITYADS_VERSION_ID");
    }

    @Override
    public void setAdapterListener(AdapterListener listener) {
        mListener = listener;
    }

    @Override
    public void initialise(Activity activity) {
        if (mKeys != null) {
            UnityAds.initialize(activity, mKeys.getApp_id(), new UnityListener());
            mListener.onInitSuccess(activity, TMMediationNetworks.UNITY_ADS);
        }
    }

    @Override
    public void setCredentials(TMNetworkCredentials credentials) {
        mKeys = credentials;
    }

    @Override
    public boolean isInitialised(Context context) {
        return UnityAds.isInitialized();
    }

    @Override
    public boolean hasFailedRecently(Context context, int ad_type) {
        String failedKey = String.format(Locale.ENGLISH, "Failed_%s_%d", getName(), ad_type);
        Storage storage = new Storage(context);
        if(storage.contains(failedKey)) {
            long failedTimeStamp = storage.getLong(failedKey);
            if (new Date().getTime() - failedTimeStamp < 60000)
                return true;
        }
        return false;
    }

    @Override
    public boolean isBannerAvailable(TMAdSize size) {
        return false;
    }

    @Override
    public boolean canDisplayInterstitial(Context context) {
        return false;
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return mKeys != null && mKeys.getVideo_id() != null && UnityAds.isReady(mKeys.getVideo_id());
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return mKeys != null && mKeys.getRewarded_video_id() != null && UnityAds.isReady(mKeys.getRewarded_video_id());
    }

    @Override
    public void setPlacements(String[] placements) {
        for (String p : placements) {
            if (!mPlacements.contains(p))
                mPlacements.add(p);
        }
    }

    @Override
    public String[] getPlacements() {
        return mPlacements.toArray(new String[mPlacements.size()]);
    }

    @Override
    public ViewGroup loadAd(Context context, TMAdSize size, TMAdListenerBase listener) {
        return null;
    }

    @Override
    public void loadInterstitial(Activity activity, String placement, TMAdListenerBase listener) {

    }

    @Override
    public void loadVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (UnityAds.isReady(mKeys.getVideo_id())) {
            listener.didLoad();
        } else {
            listener.didFailToLoad(new TMAdError(0, "Unity ad failed to load"));
        }
    }

    @Override
    public void loadRewardedVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (UnityAds.isReady(mKeys.getRewarded_video_id())) {
            listener.didLoad();
        } else {
            listener.didFailToLoad(new TMAdError(0, "Unity ad failed to load"));
        }
    }

    @Override
    public void showInterstitial(Activity activity, String placement, TMAdListenerBase listener) {

    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (UnityAds.isReady(mKeys.getVideo_id())) {
            UnityAds.setListener(new UnityListener(listener));
            UnityAds.show(activity, mKeys.getVideo_id());
        } else {
            if (listener != null)
                listener.didFailToLoad(new TMAdError(0, "No unity ad available"));
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (UnityAds.isReady(mKeys.getRewarded_video_id())) {
            UnityAds.setListener(new UnityListener(listener));
            UnityAds.show(activity, mKeys.getRewarded_video_id());
        } else {
            if (listener != null)
                listener.didFailToLoad(new TMAdError(0, "No unity ad available"));
        }
    }

    @Override
    public void destroy() {

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
            TLog.debug("onUnityAdsStart: " + s);
            if (mListener != null)
                mListener.didDisplay();
        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
            TLog.debug("onUnityAdsFinish: " + s);
            if (mListener != null) {
                mListener.didClose();
                if (mListener instanceof TMRewardAdListenerBase) {
                    ((TMRewardAdListenerBase)mListener).didVerify("", mRewardCurrency, mRewardValue);
                }
            }
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
            TLog.debug("onUnityAdsError: " + s);
            if (mListener != null)
                mListener.didFailToLoad(new TMAdError(0, s));
        }

        @Override
        public void onUnityAdsClick(String s) {
            if (mListener != null)
                mListener.didClick();
        }
    }


    @Override
    public void onCreate(Activity activity) {

    }

    @Override
    public void onStart(Activity activity) {

    }

    @Override
    public void onResume(Activity activity) {

    }

    @Override
    public void onPaused(Activity activity) {

    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {

    }
}
