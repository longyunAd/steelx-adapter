package com.ironsource.adapters.custom.udx;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.longyun.udx.sdk.UDXAd;
import com.longyun.udx.sdk.UDXError;
import com.longyun.udx.sdk.reward.UDXReward;
import com.longyun.udx.sdk.reward.UDXRewardedAd;
import com.longyun.udx.sdk.reward.UDXRewardedAdListener;

public class UDXCustomRewardedVideo extends BaseRewardedVideo<UDXCustomAdapter>{

    private final String TAG = UDXCustomRewardedVideo.class.getSimpleName();
    private UDXAd mAd;
    private boolean isAdAvailable;
    private Activity mActivity;

    public UDXCustomRewardedVideo(@NonNull NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull RewardedVideoAdListener listener) {
        mActivity = activity;
        String placementId = (String) adData.getConfiguration().get("placementID");
        Log.i(TAG, "loadAd->placementId:"+placementId);
        new UDXRewardedAd().loadAd(placementId, null, new UDXRewardedAdListener() {

            @Override
            public void onRewardedVideoStarted(UDXAd ad) {

            }

            @Override
            public void onRewardedVideoCompleted(UDXAd ad) {

            }

            @Override
            public void onUserRewarded(UDXAd ad, UDXReward reward) {
                if(listener != null) {
                    listener.onAdRewarded();
                }
            }

            @Override
            public void onAdLoaded(UDXAd ad) {
                mAd = ad;
                isAdAvailable = true;

                if(listener != null){
                    listener.onAdLoadSuccess();
                }
            }

            @Override
            public void onAdFailedToLoad(String adUnitId, UDXError error) {
                if(listener != null) {
                    //ADAPTER_ERROR_TYPE_NO_FILL 无填充
                    //ADAPTER_ERROR_TYPE_AD_EXPIRED 过期
                    //ADAPTER_ERROR_TYPE_INTERNAL 其他
                    listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onAdDisplayFailed(UDXAd ad, UDXError error) {
                if(listener != null) {
                    listener.onAdShowFailed(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onAdDisplayed(UDXAd ad) {
                if(listener != null)
                    listener.onAdOpened();
            }

            @Override
            public void onAdClicked(UDXAd ad) {
                if(listener != null)
                    listener.onAdClicked();
            }

            @Override
            public void onAdHidden(UDXAd ad) {
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
