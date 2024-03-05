package com.ironsource.adapters.custom.udx;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.longyun.udx.sdk.UDXConfig;
import com.longyun.udx.sdk.UDXSdk;

public class UDXCustomAdapter extends BaseAdapter {

    private static final String TAG = UDXCustomAdapter.class.getCanonicalName();

    @Override
    public void init(@NonNull AdData adData, @NonNull Context context, @Nullable NetworkInitializationListener networkInitializationListener) {
       String appId = (String) adData.getConfiguration().get("appId");
//        Log.i(TAG, "init->appId:"+appId +" "+ GsonUtils.toJson(adData));

        UDXConfig config = new UDXConfig.Builder()
                .setAppId(appId)
                .build();
        UDXSdk.init(context, config, new UDXSdk.UDXInitCallback() {

            @Override
            public void success() {
                Log.i(TAG, "udx init success: ");
                if(networkInitializationListener != null)
                    networkInitializationListener.onInitSuccess();
            }

            @Override
            public void fail(int code, String msg) {
                Log.i(TAG, "udx init fail: " + code + " " + msg);
                if(networkInitializationListener != null)
                    networkInitializationListener.onInitFailed(code, msg);
            }
        });
    }

    @Nullable
    @Override
    public String getNetworkSDKVersion() {
        return UDXSdk.getSDKVersion();
    }

    @NonNull
    @Override
    public String getAdapterVersion() {
        return "1.0.0";
    }
}
