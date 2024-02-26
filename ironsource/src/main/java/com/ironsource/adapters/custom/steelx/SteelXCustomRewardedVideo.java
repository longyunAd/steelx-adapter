package com.ironsource.adapters.custom.steelx;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.longyun.steelx.sdk.SXAd;
import com.longyun.steelx.sdk.SXError;
import com.longyun.steelx.sdk.reward.SXReward;
import com.longyun.steelx.sdk.reward.SXRewardedAd;
import com.longyun.steelx.sdk.reward.SXRewardedAdListener;

public class SteelXCustomRewardedVideo extends BaseRewardedVideo<SteelXCustomAdapter>{

    private final String TAG = "SteelXCustomRewardedVideo";
    private SXAd mAd;
    private boolean isAdAvailable;
    private Activity mActivity;

    public SteelXCustomRewardedVideo(@NonNull NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull RewardedVideoAdListener listener) {
        mActivity = activity;
        String placementId = (String) adData.getConfiguration().get("placementID");
        Log.i(TAG, "loadAd->placementId:"+placementId);
        new SXRewardedAd().loadAd(placementId, null, new SXRewardedAdListener() {

            @Override
            public void onRewardedVideoStarted(SXAd ad) {

            }

            @Override
            public void onRewardedVideoCompleted(SXAd ad) {

            }

            @Override
            public void onUserRewarded(SXAd ad, SXReward reward) {
                if(listener != null) {
                    listener.onAdRewarded();
                }
            }

            @Override
            public void onAdLoaded(SXAd ad) {
                mAd = ad;
                isAdAvailable = true;

                if(listener != null){
                    listener.onAdLoadSuccess();
                }
            }

            @Override
            public void onAdFailedToLoad(String adUnitId, SXError error) {
                if(listener != null) {
                    //ADAPTER_ERROR_TYPE_NO_FILL 无填充
                    //ADAPTER_ERROR_TYPE_AD_EXPIRED 过期
                    //ADAPTER_ERROR_TYPE_INTERNAL 其他
                    listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onAdDisplayFailed(SXAd ad, SXError error) {
                if(listener != null) {
                    listener.onAdShowFailed(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onAdDisplayed(SXAd ad) {
                if(listener != null)
                    listener.onAdOpened();
            }

            @Override
            public void onAdClicked(SXAd ad) {
                if(listener != null)
                    listener.onAdClicked();
            }

            @Override
            public void onAdHidden(SXAd ad) {
                if(listener != null)
                    listener.onAdClosed();
            }
        });
    }

    @Override
    public void showAd(@NonNull AdData adData, @NonNull RewardedVideoAdListener listener) {
        if (mAd != null && mActivity != null) {
            mAd.show(mActivity);
            mAd = null;
        }
    }

    @Override
    public boolean isAdAvailable(@NonNull AdData adData) {
        return isAdAvailable;
    }
}
