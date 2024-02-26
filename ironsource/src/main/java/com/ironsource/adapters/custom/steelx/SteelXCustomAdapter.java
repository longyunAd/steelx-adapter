package com.ironsource.adapters.custom.steelx;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.longyun.steelx.sdk.SXConfig;
import com.longyun.steelx.sdk.SXSdk;

public class SteelXCustomAdapter extends BaseAdapter {

    private static final String TAG = "SteelXCustomAdapter";

    @Override
    public void init(@NonNull AdData adData, @NonNull Context context, @Nullable NetworkInitializationListener networkInitializationListener) {
       String appId = (String) adData.getConfiguration().get("appID");
//        Log.i(TAG, "init->appId:"+appId +" "+ GsonUtils.toJson(adData));

        SXConfig config = new SXConfig.Builder()
                .setAppId(appId)
                .build();
        SXSdk.init(context, config, new SXSdk.SLInitCallback() {

            @Override
            public void success() {
                Log.i(TAG, "steelx init success: ");
                if(networkInitializationListener != null)
                    networkInitializationListener.onInitSuccess();
            }

            @Override
            public void fail(int code, String msg) {
                Log.i(TAG, "steelx init fail: " + code + " " + msg);
                if(networkInitializationListener != null)
                    networkInitializationListener.onInitFailed(code, msg);
            }
        });
    }

    @Nullable
    @Override
    public String getNetworkSDKVersion() {
        return SXSdk.getSDKVersion();
    }

    @NonNull
    @Override
    public String getAdapterVersion() {
        return "1.0.0";
    }
}
