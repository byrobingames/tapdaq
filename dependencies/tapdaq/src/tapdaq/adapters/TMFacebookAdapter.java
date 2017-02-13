package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.ViewGroup;

import com.facebook.ads.*;

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
 * Created by dominicroberts on 02/09/2016.
 */
public class TMFacebookAdapter implements TMAdapter {

    private Activity mCurrentActivity;
    private AdapterListener mListener;

    private AdView mAd;
    private TMNetworkCredentials mKeys;
    private List<InterstitialAd> mInterstitialAd = new ArrayList<InterstitialAd>();

    private List<String> mPlacements;

    public TMFacebookAdapter(Context context) {
        super();
        mPlacements = new ArrayList<String>();
        clear(context);
    }

    private void clear(Context context) {
        Storage storage = new Storage(context);
        storage.remove("FB_BANNER_ID");
        storage.remove("FB_INTERSTITIAL_ID");
    }

    @Override
    public String getName() {
        return TMMediationNetworks.FACEBOOK_NAME + "_Adapter";
    }

    @Override
    public int getID() {
        return TMMediationNetworks.FACEBOOK;
    }

    @Override
    public void setAdapterListener(AdapterListener listener) {
        mListener = listener;
    }

    @Override
    public void initialise(Activity activity) {
        if (activity != null)
            mCurrentActivity = activity;

        if (mKeys != null && mCurrentActivity != null) {
            mListener.onInitSuccess(mCurrentActivity, TMMediationNetworks.FACEBOOK);

            Storage storage = new Storage(mCurrentActivity);
            storage.putString("FB_BANNER_ID", mKeys.getBanner_id());
            storage.putString("FB_INTERSTITIAL_ID", mKeys.getInterstitial_id());
            storage.putString("FB_VERSION_ID", mKeys.getVersion_id());
        }
    }

    @Override
    public void setCredentials(TMNetworkCredentials credentials) {
        mKeys = credentials;
    }

    public TMFacebookAdapter setTestDevices(List<String> devices) {
        AdSettings.addTestDevices(devices);
        return this;
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

    private String getBannerId(Context context) {
        if (mKeys != null && mKeys.getBanner_id() != null)
            return mKeys.getBanner_id();
        else
            return new Storage(context).getString("FB_BANNER_ID");
    }

    private String getInterstitialId(Context context) {
        if (mKeys != null && mKeys.getInterstitial_id() != null)
            return mKeys.getInterstitial_id();
        else
            return new Storage(context).getString("FB_INTERSTITIAL_ID");
    }

    @Override
    public String getVersionID(Context context) {
        if (mKeys != null && mKeys.getVersion_id() != null)
            return mKeys.getVersion_id();
        else
            return new Storage(context).getString("FB_VERSION_ID");
    }

    @Override
    public boolean isInitialised(Context context) {
        return getBannerId(context) != null || getInterstitialId(context) != null;
    }

    @Override
    public boolean isBannerAvailable(TMAdSize size) {
        if (mKeys != null && mKeys.getBanner_id() != null) {
            com.facebook.ads.AdSize adSize = new TMFacebookBannerSizes().getSize(size);
            if (adSize != null)
                return true;
        }
        return false;
    }

    @Override
    public boolean canDisplayInterstitial(Context context) {
        return getInterstitialId(context) != null && isActivityAvailable(context, InterstitialAdActivity.class);
    }

    private boolean isActivityAvailable(Context context, Class<?> type) {
        Intent intent = new Intent(context, type);
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if(list.size() > 0)
            return true;
        TLog.error(String.format(Locale.ENGLISH, "Class %s missing from AndroidManifest.xml", type.getName()));
        return false;
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return false;
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
        com.facebook.ads.AdSize adSize = new TMFacebookBannerSizes().getSize(size);
        if(adSize != null) {
            mAd = new AdView(context, getBannerId(context), adSize);
            mAd.setAdListener(new FBBannerListener(listener));
            mAd.loadAd();
            return mAd;
        }
        return null;
    }

    @Override
    public void loadInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        if (activity != null && getInterstitialId(activity) != null) {
            InterstitialAd ad = new InterstitialAd(activity, getInterstitialId(activity));
            ad.setAdListener(new FBInterstitialListener(placement, listener));
            mInterstitialAd.add(ad);
            ad.loadAd();
        }
    }

    @Override
    public void loadVideo(Activity activity, String placement, TMAdListenerBase listener) {
        //Not Available
    }

    @Override
    public void loadRewardedVideo(Activity activity, String placement, TMAdListenerBase listener) {
        //Not Available
    }

    @Override
    public void showInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        InterstitialAd ad = (mInterstitialAd.isEmpty() ? null : mInterstitialAd.get(0));
        if (ad == null || !ad.isAdLoaded()) {
            loadInterstitial(activity, placement, listener);
        } else {
            ad.setAdListener(new FBInterstitialListener(placement, listener));
            ad.show();
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        //Not available for FB
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        //Not available for FB
    }

    @Override
    public void destroy() {
        if(mAd != null)
            mAd.destroy();
    }

    private TMAdError buildError(AdError error) {
        return new TMAdError(error.getErrorCode(), error.getErrorMessage());
    }

    private class FBBannerListener implements AdListener
    {
        private final TMAdListenerBase mAdListener;

        FBBannerListener(TMAdListenerBase listener) {
            mAdListener = listener;
        }

        @Override
        public void onError(Ad ad, AdError adError) {
            if (mAdListener != null)
                mAdListener.didFailToLoad(buildError(adError));
            TMServiceQueue.ServiceError(mCurrentActivity, getName(), TMAdType.BANNER);
        }

        @Override
        public void onAdLoaded(Ad ad) {
            if (mAdListener != null)
                mAdListener.didLoad();
        }

        @Override
        public void onAdClicked(Ad ad) {
            if (mAdListener != null)
                mAdListener.didClick();
        }
    }

    private class FBInterstitialListener implements InterstitialAdListener {
        private final TMAdListenerBase mAdListener;
        private String mPlacement;

        FBInterstitialListener(String placement, TMAdListenerBase listener) {
            mAdListener = listener;
            mPlacement = placement;
        }
        @Override
        public void onInterstitialDisplayed(Ad ad) {
            TLog.debug("onInterstitialDisplayed");

            if (mAdListener != null)
                mAdListener.didDisplay();
        }

        @Override
        public void onInterstitialDismissed(Ad ad) {
            TLog.debug("onInterstitialDismissed");
            if (mAdListener != null)
                mAdListener.didClose();
            ad.destroy();

            if (mInterstitialAd.contains(ad))
                mInterstitialAd.remove(ad);
        }

        @Override
        public void onError(Ad ad, AdError adError) {
            TLog.debug("onError");

            TMAdError error = buildError(adError);
            TMServiceErrorHandler.ServiceError(mCurrentActivity, getName(), TMAdType.STATIC_INTERSTITIAL, mPlacement, error, mAdListener);
            TLog.error(error.getErrorMessage());

            if (mInterstitialAd.contains(ad))
                mInterstitialAd.remove(ad);
        }

        @Override
        public void onAdLoaded(Ad ad) {
            TLog.debug("didLoad");
            if (mAdListener != null)
                mAdListener.didLoad();
        }

        @Override
        public void onAdClicked(Ad ad) {
            TLog.debug("onAdClicked");
        }
    }

    private class TMFacebookBannerSizes extends TMBannerAdSizes {
        com.facebook.ads.AdSize getSize(TMAdSize size) {
            if (size == STANDARD)
                return com.facebook.ads.AdSize.BANNER_320_50;
            else if(size == LARGE)
                return com.facebook.ads.AdSize.BANNER_HEIGHT_90;
            else if(size == MEDIUM_RECT)
                return com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250;
            TLog.error(String.format(Locale.getDefault(), "No Facebook Banner Available for size: %s", size.name));
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
        mCurrentActivity = null;
    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {

    }
}