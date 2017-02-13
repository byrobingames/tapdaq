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
import com.vungle.sdk.VunglePub;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dominicroberts on 24/01/2017.
 */

public class TMVungleAdapter implements TMAdapter {

    private String mRewardCurrency = "Reward";
    private double mRewardValue = 1.0;

    private AdapterListener mListener;

    private TMNetworkCredentials mKeys;
    private List<String> mPlacements;

    public TMVungleAdapter(){
        super();
        mPlacements = new ArrayList<String>();
    }

    public TMVungleAdapter(String rewardCurrency, double rewardValue) {
        super();
        mPlacements = new ArrayList<String>();

        if (mRewardCurrency != null)
            mRewardCurrency = rewardCurrency;
        mRewardValue = rewardValue;
    }

    @Override
    public String getName() {
        return TMMediationNetworks.VUNGLE_NAME  + "_Adapter";
    }

    @Override
    public int getID() {
        return TMMediationNetworks.VUNGLE;
    }

    @Override
    public String getVersionID(Context context) {
        if (mKeys != null && mKeys.getVersion_id() != null)
            return mKeys.getVersion_id();
        else
            return new Storage(context).getString("VUNGLE_VERSION_ID");
    }

    @Override
    public void setAdapterListener(AdapterListener listener) {
        mListener = listener;
    }

    @Override
    public void initialise(Activity activity) {
        if (activity != null && mKeys != null) {
            VunglePub.init(activity, mKeys.getApp_id());
            mListener.onInitSuccess(activity, TMMediationNetworks.VUNGLE);
        }
    }

    @Override
    public void setCredentials(TMNetworkCredentials credentials) {
        if (credentials != null)
            mKeys = credentials;
    }

    @Override
    public boolean isInitialised(Context context) {
        return mKeys != null && mKeys.getApp_id() != null;
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
        return VunglePub.isVideoAvailable();
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return VunglePub.isVideoAvailable();
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
        if (VunglePub.isVideoAvailable()) {
            listener.didLoad();
        } else {
            listener.didFailToLoad(new TMAdError(0, "Vungle ad failed to load"));
        }
    }

    @Override
    public void loadRewardedVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (VunglePub.isVideoAvailable()) {
            listener.didLoad();
        } else {
            listener.didFailToLoad(new TMAdError(0, "Vungle reward ad failed to load"));
        }
    }

    @Override
    public void showInterstitial(Activity activity, String placement, TMAdListenerBase listener) {

    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (VunglePub.isVideoAvailable()) {
            VunglePub.setEventListener(new VungleEventListener(false, listener));
            VunglePub.displayAdvert();
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (VunglePub.isVideoAvailable()) {
            VunglePub.setEventListener(new VungleEventListener(true, listener));
            VunglePub.displayIncentivizedAdvert(false);
        }
    }

    @Override
    public void destroy() {

    }

    private class VungleEventListener implements VunglePub.EventListener {

        private TMAdListenerBase mListener;
        private boolean mReward = false;

        public VungleEventListener(boolean reward, TMAdListenerBase listener) {
            mListener = listener;
            mReward = reward;
        }

        @Override
        public void onVungleAdEnd() {
            if (mListener != null) {
                mListener.didClose();
                if (mReward && mListener instanceof TMRewardAdListenerBase) {
                    ((TMRewardAdListenerBase)mListener).didVerify("", mRewardCurrency, mRewardValue);
                }
            }
        }

        @Override
        public void onVungleAdStart() {
            if (mListener != null) {
                mListener.didDisplay();
            }
        }

        @Override
        public void onVungleView(double v, double v1) {
            TLog.debug(String.format(Locale.ENGLISH, "onVungleView %d %d", v, v1));
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
        VunglePub.onResume();
    }

    @Override
    public void onPaused(Activity activity) {
        VunglePub.onPause();
    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {

    }
}
