package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMAdapter;
import com.tapdaq.sdk.helpers.TLog;
import com.tapdaq.sdk.listeners.TMAdListenerBase;
import com.tapdaq.sdk.listeners.TMListenerHandler;
import com.tapdaq.sdk.listeners.TMRewardAdListenerBase;
import com.vungle.sdk.VungleAdvert;
import com.vungle.sdk.VunglePub;

import java.util.Locale;

/**
 * Created by dominicroberts on 24/01/2017.
 */

public class TMVungleAdapter extends TMAdapter {
    private String mRewardCurrency = "Reward";
    private double mRewardValue = 1.0;

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
        if (activity != null && mKeys != null) {
            VunglePub.init(activity, mKeys.getApp_id());
            mListener.onInitSuccess(activity, TMMediationNetworks.VUNGLE);
        }
    }

    @Override
    public boolean isInitialised(Context context) {
        return isActivityAvailable(context, VungleAdvert.class) && getAppId(context) != null;
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return isInitialised(context) && VunglePub.isVideoAvailable();
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return isInitialised(context) && VunglePub.isVideoAvailable();
    }

    @Override
    public void loadVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (VunglePub.isVideoAvailable()) {
            TMListenerHandler.DidLoad(listener);
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Vungle ad failed to load"));
        }
    }

    @Override
    public void loadRewardedVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (VunglePub.isVideoAvailable()) {
            TMListenerHandler.DidLoad(listener);
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Vungle reward ad failed to load"));
        }
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

    private class VungleEventListener implements VunglePub.EventListener {

        private TMAdListenerBase mListener;
        private boolean mReward = false;

        VungleEventListener(boolean reward, TMAdListenerBase listener) {
            mListener = listener;
            mReward = reward;
        }

        @Override
        public void onVungleAdEnd() {
            if (mListener != null && mCurrentActivity != null) {
                new Handler(mCurrentActivity.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidClose(mListener);
                        if (mReward && mListener instanceof TMRewardAdListenerBase) {
                            TMListenerHandler.DidVerify((TMRewardAdListenerBase) mListener, "", mRewardCurrency, mRewardValue);
                        }
                    }});
            }
        }

        @Override
        public void onVungleAdStart() {
            if (mListener != null && mCurrentActivity != null) {
                new Handler(mCurrentActivity.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        TMListenerHandler.DidDisplay(mListener);
                    }});
            }
        }

        @Override
        public void onVungleView(double v, double v1) {
            TLog.debug(String.format(Locale.ENGLISH, "onVungleView %d %d", v, v1));
        }
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        VunglePub.onResume();
    }

    @Override
    public void onPaused(Activity activity) {
        super.onPaused(activity);
        VunglePub.onPause();
    }
}
