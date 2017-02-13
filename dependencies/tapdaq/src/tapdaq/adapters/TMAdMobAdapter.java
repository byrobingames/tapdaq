package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.google.android.gms.ads.*;

import com.tapdaq.sdk.adnetworks.TMServiceQueue;
import com.tapdaq.sdk.common.*;
import com.tapdaq.sdk.helpers.TLog;
import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.listeners.*;
import com.tapdaq.sdk.model.TMAdSize;
import com.tapdaq.sdk.storage.Storage;
import com.tapdaq.sdk.model.launch.TMNetworkCredentials;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by dominicroberts on 01/09/2016.
 */
public class TMAdMobAdapter implements TMAdapter {

    private final static String ERROR_CODE_INTERNAL_ERROR = "Something happened internally; for instance, an invalid response was received from the ad server.";//CODE 0
    private final static String ERROR_CODE_INVALID_REQUEST = "The ad request was invalid; for instance, the ad unit ID was incorrect.";                         //CODE 1
    private final static String ERROR_CODE_NETWORK_ERROR = "The ad request was unsuccessful due to network connectivity.";                                      //CODE 2
    private final static String ERROR_CODE_NO_FILL = "The ad request was successful, but no ad was returned due to lack of ad inventory.";                      //CODE 3
    private final static String ERROR_UNKNOWN = "UNKNOWN ERROR";

    private Activity mCurrentActivity;
    private AdapterListener mListener;

    private TMAdMobBannerSizes mBannerSizes = new TMAdMobBannerSizes();

    private List<InterstitialAd> mInterstitialAd = new ArrayList<InterstitialAd>();
    private List<InterstitialAd> mVideoInterstitialAd = new ArrayList<InterstitialAd>();

    private TMNetworkCredentials mKeys;
    private List<String> mPlacements;

    public TMAdMobAdapter(Context context){
        super();
        mPlacements = new ArrayList<String>();
        clear(context);
    }

    private void clear(Context context) {
        Storage storage = new Storage(context);
        storage.remove("ADMOB_TEST_DEVICES");
        storage.remove("ADMOB_BANNER_ID");
        storage.remove("ADMOB_STATIC_ID");
        storage.remove("ADMOB_VIDEO_ID");
    }

    @Override
    public void initialise(Activity activity) {
        if (activity != null)
            mCurrentActivity = activity;

        if (mCurrentActivity != null && mKeys != null) {
            MobileAds.initialize(mCurrentActivity);
            mListener.onInitSuccess(mCurrentActivity, TMMediationNetworks.AD_MOB);

            Storage storage = new Storage(mCurrentActivity);
            storage.putString("ADMOB_BANNER_ID", mKeys.getBanner_id());
            storage.putString("ADMOB_STATIC_ID", mKeys.getInterstitial_id());
            storage.putString("ADMOB_VIDEO_ID", mKeys.getVideo_id());
            storage.putString("ADMOB_VERSION_ID", mKeys.getVersion_id());
        }
    }

    @Override
    public void setCredentials(TMNetworkCredentials credentials) {
        mKeys = credentials;
    }

    public TMAdMobAdapter setTestDevices(Context context, List<String> devices) {
        Storage storage = new Storage(context);
        String devicesStr = TextUtils.join(", ", devices);
        storage.putString("ADMOB_TEST_DEVICES",devicesStr);
        return this;
    }

    @Override
    public boolean isInitialised(Context context) {
        return mCurrentActivity != null && (getBannerId(context) != null || getStaticId(context) != null || getVideoId(context) != null) ;
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
    public String getVersionID(Context context) {
        if (mKeys != null && mKeys.getVersion_id() != null)
            return mKeys.getVersion_id();
        else
            return new Storage(context).getString("ADMOB_VERSION_ID");
    }

    private String getBannerId(Context context) {
        if (mKeys != null && mKeys.getBanner_id() != null)
            return mKeys.getBanner_id();
        else
            return new Storage(context).getString("ADMOB_BANNER_ID");
    }

    private String getStaticId(Context context) {
        if (mKeys != null && mKeys.getInterstitial_id() != null)
            return mKeys.getInterstitial_id();
        else
            return new Storage(context).getString("ADMOB_STATIC_ID");
    }

    private String getVideoId(Context context) {
        if (mKeys != null && mKeys.getVideo_id() != null)
            return mKeys.getVideo_id();
        else
            return new Storage(context).getString("ADMOB_VIDEO_ID");
    }

    @Override
    public String getName() {
        return TMMediationNetworks.AD_MOB_NAME + "_Adapter";
    }

    @Override
    public int getID() {
        return TMMediationNetworks.AD_MOB;
    }

    @Override
    public void setAdapterListener(AdapterListener listener) {
        mListener = listener;
    }

    @Override
    public boolean isBannerAvailable(TMAdSize size) {
        return getBannerId(mCurrentActivity) != null && mBannerSizes.getSize(size) != null;
    }

    @Override
    public boolean canDisplayInterstitial(Context context) {
        return getStaticId(context) != null; //Has keys
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return getVideoId(context) != null; //Has keys
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return false;
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
        com.google.android.gms.ads.AdSize adSize = mBannerSizes.getSize(size);
        if(adSize != null) {
            AdView view = new AdView(context);
            view.setAdUnitId(getBannerId(context));
            view.setAdSize(adSize);
            view.setAdListener(new AdMobAdListener(listener));
            AdRequest.Builder builder = new AdRequest.Builder();

            Storage storage = new Storage(context);
            if (storage.contains("ADMOB_TEST_DEVICES")) {
                String[] devices = TextUtils.split(storage.getString("ADMOB_TEST_DEVICES"), ", ");
                for (String d : devices) {
                    builder.addTestDevice(d);
                }
            }

            view.loadAd(builder.build());
            return view;
        }
        return null;
    }

    @Override
    public void loadInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        if (activity != null && getStaticId(activity) != null) {
            InterstitialAd ad = new InterstitialAd(activity);
            ad.setAdUnitId(getStaticId(activity));
            ad.setAdListener(new AdMobInterstitialAdListener(ad, listener, TMAdType.STATIC_INTERSTITIAL, placement));

            AdRequest.Builder builder = new AdRequest.Builder();

            Storage storage = new Storage(activity);
            if (storage.contains("ADMOB_TEST_DEVICES")) {
                String[] devices = TextUtils.split(storage.getString("ADMOB_TEST_DEVICES"), ", ");

                for (String d : devices) {
                    builder.addTestDevice(d);
                }
            }

            ad.loadAd(builder.build());
            mInterstitialAd.add(ad);
        }
    }

    @Override
    public void loadVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (activity != null && getVideoId(activity) != null) {
            InterstitialAd ad = new InterstitialAd(activity);
            ad.setAdUnitId(getVideoId(activity));
            ad.setAdListener(new AdMobInterstitialAdListener(ad, listener, TMAdType.VIDEO_INTERSTITIAL, placement));
            mVideoInterstitialAd.add(ad);

            AdRequest.Builder builder = new AdRequest.Builder();

            Storage storage = new Storage(activity);
            if (storage.contains("ADMOB_TEST_DEVICES")) {
                String[] devices = TextUtils.split(storage.getString("ADMOB_TEST_DEVICES"), ", ");
                for (String d : devices) {
                    builder.addTestDevice(d);
                }
            }

            ad.loadAd(builder.build());
        }
    }

    @Override
    public void loadRewardedVideo(Activity activity, String placement, TMAdListenerBase listener) {
        //Not available
    }

    @Override
    public void showInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        InterstitialAd ad = (mInterstitialAd.isEmpty() ? null : mInterstitialAd.get(0));
        if (ad != null && ad.isLoaded()) {
            if (listener != null)
                ad.setAdListener(new AdMobInterstitialAdListener(ad, listener, TMAdType.STATIC_INTERSTITIAL, placement));
            ad.show();
        } else
            loadInterstitial(activity, placement, listener);
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        InterstitialAd ad = (mVideoInterstitialAd.isEmpty() ? null : mVideoInterstitialAd.get(0));
        if (ad != null && ad.isLoaded()) {
            if (listener != null)
                ad.setAdListener(new AdMobInterstitialAdListener(ad, listener, TMAdType.VIDEO_INTERSTITIAL, placement));
            ad.show();
        } else
            loadVideo(activity, placement, listener);
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {

    }

    @Override
    public void destroy() {

    }

    private TMAdError buildError(int code) {
        switch (code) {
            case 0:
                return new TMAdError(code, ERROR_CODE_INTERNAL_ERROR);
            case 1:
                return new TMAdError(code, ERROR_CODE_INVALID_REQUEST);
            case 2:
                return new TMAdError(code, ERROR_CODE_NETWORK_ERROR);
            case 3:
                return new TMAdError(code, ERROR_CODE_NO_FILL);
            default:
                return new TMAdError(code, ERROR_UNKNOWN);
        }
    }

    private class AdMobAdListener extends AdListener
    {
        private final TMAdListenerBase mAdListener;

        AdMobAdListener(TMAdListenerBase listener) {
            mAdListener = listener;
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            TLog.debug("didClose");

            if (mAdListener != null)
                mAdListener.didClose();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            TLog.debug("didFailToLoad");

            if (mAdListener != null)
                mAdListener.didFailToLoad(buildError(i));
            TMServiceQueue.ServiceError(mCurrentActivity, getName(), TMAdType.BANNER);
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
            TLog.debug("onAdLeftApplication");

            if (mAdListener != null)
                mAdListener.didClick();
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            TLog.debug("didDisplay");

            if (mAdListener != null)
                mAdListener.didDisplay();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            TLog.debug("didLoad");

            if (mAdListener != null)
                mAdListener.didLoad();
        }
    }

    private class AdMobInterstitialAdListener extends AdListener
    {
        private TMAdListenerBase mAdListener;
        private InterstitialAd mAd;
        private int mType;
        private String mPlacement;

        AdMobInterstitialAdListener(InterstitialAd ad, TMAdListenerBase listener, int type, String placement) {
            mAd = ad;
            mAdListener = listener;
            mType = type;
            mPlacement = placement;
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            TLog.debug("didClose");

            if (mAdListener != null)
                mAdListener.didClose();

            if (!mInterstitialAd.isEmpty() && mAd == mInterstitialAd.get(0))
                mInterstitialAd.remove(mAd);
            else if(!mVideoInterstitialAd.isEmpty() && mAd == mVideoInterstitialAd.get(0))
                mVideoInterstitialAd.remove(mAd);

            mAd = null;
            mAdListener = null;
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            TLog.debug("didFailToLoad");

            TMAdError error = buildError(i);
            TMServiceErrorHandler.ServiceError(mCurrentActivity, getName(), mType, mPlacement, error, mAdListener);
            TLog.error(error.getErrorMessage());

            if (!mInterstitialAd.isEmpty() && mAd == mInterstitialAd.get(0))
                mInterstitialAd.remove(mAd);
            else if(!mVideoInterstitialAd.isEmpty() && mAd == mVideoInterstitialAd.get(0))
                mVideoInterstitialAd.remove(mAd);
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
            TLog.debug("onAdLeftApplication");

            if (mAdListener != null)
                mAdListener.didClick();
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            TLog.debug("didDisplay");

            if (mAdListener != null)
                mAdListener.didDisplay();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            TLog.debug("didLoad");

            if (mAdListener != null)
                mAdListener.didLoad();
        }
    }

    private class TMAdMobBannerSizes extends TMBannerAdSizes {
        com.google.android.gms.ads.AdSize getSize(TMAdSize size) {
            if (size == STANDARD)
                return com.google.android.gms.ads.AdSize.BANNER;
            else if(size == LARGE)
                return com.google.android.gms.ads.AdSize.LARGE_BANNER;
            else if(size == MEDIUM_RECT)
                return com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE;
            else if(size == FULL)
                return com.google.android.gms.ads.AdSize.FULL_BANNER;
            else if(size == LEADERBOARD)
                return com.google.android.gms.ads.AdSize.LEADERBOARD;
            else if(size == SMART)
                return com.google.android.gms.ads.AdSize.SMART_BANNER;
            TLog.error(String.format(Locale.getDefault(), "No Ad Mob Banner Available for size: %s", size.name));
            return null;
        }
    }

    //Lifecycle Events
    @Override
    public void onCreate(Activity activity) {

    }

    @Override
    public void onStart(Activity activity) {

    }

    @Override
    public void onResume(Activity activity) {
        mCurrentActivity = activity;
    }

    @Override
    public void onPaused(Activity activity) {

    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {
        if (activity == mCurrentActivity)
            mCurrentActivity = null;
    }
}

