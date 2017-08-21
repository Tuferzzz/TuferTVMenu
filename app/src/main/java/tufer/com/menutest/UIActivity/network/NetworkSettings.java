

package tufer.com.menutest.UIActivity.network;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;


import com.mstar.android.ethernet.EthernetDevInfo;
import com.mstar.android.ethernet.EthernetManager;
import com.mstar.android.pppoe.PppoeManager;


import tufer.com.menutest.UIActivity.network.ProxySettings.ProxyInfo;
import tufer.com.menutest.Util.Tools;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


public class NetworkSettings {

    private static final String TAG = "NetworkSettings";

    private Context mContext;

    private WifiManager mWifiManager;

    private EthernetManager mEthernetManager;

    private PppoeManager mPPPoEManager;

    private ProxyInfo mProxyInfo = null;

    private boolean mHasProxySettigns = false;

    private static List<INetworkSettingsListener> mSettingListener = new ArrayList<INetworkSettingsListener>();

    public NetworkSettings(Context context) {
        this.mContext = context;
    }

    public void updateProxy(ProxyInfo proxyInfo) {
        this.mHasProxySettigns = proxyInfo == null ? false : true;

        // update proxy info
        if (proxyInfo != mProxyInfo || mProxyInfo == null) {
            for (INetworkSettingsListener item : mSettingListener) {
                Log.d(TAG, "dispatch proxy changed");
                item.onProxyChanged(mHasProxySettigns, proxyInfo);
            }
            // save proxy
            this.mProxyInfo = proxyInfo;
        }
    }

    public ProxyInfo getProxyInfo() {
        return mProxyInfo;
    }

    public WifiManager getWifiManager() {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        }

        return mWifiManager;
    }

    public EthernetManager getEthernetManager() {
        if (mEthernetManager == null) {
            mEthernetManager = EthernetManager.getInstance();
        }

        return mEthernetManager;
    }

    public PppoeManager getPPPoEManager() {
        if (mPPPoEManager == null) {
            mPPPoEManager = PppoeManager.getInstance();
        }

        return mPPPoEManager;
    }

    public boolean hasProxy() {
        return mHasProxySettigns;
    }

    public boolean isWifiPPPoE() {
        EthernetDevInfo ethInfo = getEthernetManager().getSavedConfig();
        String ifName = ethInfo.getIfName();
        if (null == ifName) {
            ifName = "eth0";
        }

        String pppoeInterface = getPPPoEManager().PppoeGetInterface();
        Tools.logd(TAG, "pppoe interface, " + pppoeInterface);
        if (ifName.equals(pppoeInterface)) {
            return false;
        }

        return true;
    }

    public boolean isWifiConnected() {
        WifiManager wifiManager = getWifiManager();
        // wifi is disabled
        if (!wifiManager.isWifiEnabled()) {
            return false;
        }

        // wifi have not connected
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info == null || info.getSSID() == null
                || info.getNetworkId() == WifiConfiguration.INVALID_NETWORK_ID) {
            return false;
        }

        return true;
    }

    public boolean isEthernetEnabled() {
        EthernetManager ethernet = getEthernetManager();
        if (EthernetManager.ETHERNET_STATE_ENABLED == ethernet.getState()) {
            return true;
        }

        return false;
    }

    public WifiConfiguration getWifiConfiguredNetwork() {
        if (mWifiManager == null) {
            getWifiManager();
        }
        //
        if (mWifiManager.isWifiEnabled()) {
            WifiInfo wifi = mWifiManager.getConnectionInfo();
            Log.d(TAG, "ssid, " + wifi.getSSID());

            List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
            if (configs == null) {
                return null;
            }
            String ssid = wifi.getSSID();
            for (WifiConfiguration config : configs) {
                Log.d(TAG, "config.SSID, " + config.SSID);
                if (ssid.equals(config.SSID)) {
                    return config;
                }
            }
        }

        return null;
    }

    public void addNetworkSettingListener(INetworkSettingsListener listener) {
        mSettingListener.add(listener);
        Log.d(TAG, "size, " + mSettingListener.size());
    }

}
