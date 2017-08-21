//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2012 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>

package tufer.com.menutest.UIActivity.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.ProxyProperties;
import com.mstar.android.pppoe.PPPOE_STA;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.ProxySettings;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;
import tufer.com.menutest.UIActivity.network.ProxySettings.ProxyInfo;
import tufer.com.menutest.Util.Tools;

import com.mstar.android.wifi.MWifiManager;

public class WifiSettings extends NetworkSettings implements INetworkSettingsListener {

    private static final String TAG = "WifiSettings";
    // Combo scans can take 5-6s to complete - set to 10s.
    private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;
    private static final boolean LOG = true;

    private NetworkSettingsActivity mNetworkSettingsActivity;

    private WifiSettingsHolder mWifiSettingsHolder;
    private ListView mWifiSignalListView;
    private CheckBox mWifiToggleCheckBox;
    private LinearLayout mFooterView;

    private WifiManager mWifiManager;
    private List<ScanResult> mResults = new ArrayList<ScanResult>();
    private AtomicBoolean mConnected = new AtomicBoolean(false);
    // scan wifiap around
    private Scanner mScanner;
    // add Wi-Fi dialog
    private WiFiSignalListAdapter mAdapter;

    // setting item on the right
    private int mSettingItem = Constants.SETTING_ITEM_0;

    public WifiSettings(NetworkSettingsActivity networkSettingsActivity) {
        super(networkSettingsActivity);

        mWifiManager = getWifiManager();
        mScanner = new Scanner();

        mNetworkSettingsActivity = networkSettingsActivity;
        mWifiSettingsHolder = new WifiSettingsHolder(networkSettingsActivity);
        mWifiSignalListView = mWifiSettingsHolder.getWifiListView();
        mWifiToggleCheckBox = mWifiSettingsHolder.getWifiToogleCheckBox();
        mFooterView = mWifiSettingsHolder.getFooterView();
        mWifiSignalListView.addFooterView(mFooterView, null, true);
        setListener();
        addNetworkSettingListener(this);
        registerReceiver();
    }

    public void setVisible(boolean visible) {
        mWifiSettingsHolder.setVisible(visible);
        if (visible) {
            showWifiInfo();
        }
    }

    @Override
    public void onExit() {
        mNetworkSettingsActivity.unregisterReceiver(mWifiReceiver);
        mNetworkSettingsActivity.unregisterReceiver(mWifiHWReceiver);
        mScanner.pause();
    }

    @Override
    public boolean onKeyEvent(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (mSettingItem < Constants.SETTING_ITEM_1) {
                mSettingItem++;
                mWifiSignalListView.setSelection(0);
            }
            mWifiSettingsHolder.requestFocus(mSettingItem);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (mSettingItem > 0) {
                mSettingItem--;
            }
            mWifiSettingsHolder.requestFocus(mSettingItem);
            return true;
        }else if (keyCode ==KeyEvent.KEYCODE_DPAD_LEFT){
        	
			return false;
		}

        return false;
    }

    @Override
    public void onProxyChanged(boolean enabled, ProxyInfo proxyInfo) {
        Log.d(TAG, "onProxyChanged, enabled, " + enabled);
        if (isWifiConnected()) {
            // update proxy
            updateWifiProxy(proxyInfo);
        }
    }

    @Override
    public void onWifiHWChanged(boolean isOn) {
        Log.d(TAG, "onWifiHWChanged isOn, " + isOn);
        // wifi hw removed
        if (!isOn) {
            Log.d(TAG, "wifi dongle removed, disable wifi setting");
            mWifiToggleCheckBox.setChecked(false);
            mWifiSignalListView.setVisibility(View.INVISIBLE);
            mFooterView.setVisibility(View.INVISIBLE);
        } else {
            try {
                Thread.sleep(3000);
                Log.d(TAG, "sleep 3s for wifi framework str");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            int currentState = mWifiManager.getWifiState();
            Log.d(TAG, "wifi dongle added with wifi " + currentState);
            Log.d(TAG, "str need: " + WifiManager.WIFI_STATE_ENABLED + WifiManager.WIFI_STATE_ENABLING);
            if (currentState == WifiManager.WIFI_STATE_ENABLED ||
                currentState == WifiManager.WIFI_STATE_ENABLING) {
                Log.d(TAG, "wifi dongle added with str, enable wifi setting");
                mWifiToggleCheckBox.setChecked(true);
                mWifiSignalListView.setVisibility(View.VISIBLE);
                mFooterView.setVisibility(View.VISIBLE);
                refreshWifiSignal();
            } else {
                Log.d(TAG, "wifi dongle add without str, wifi setting do nothing");
            }
        }
    }

    @Override
    public void onFocusChange(boolean hasFocus) {
        if (hasFocus) {
            mWifiSettingsHolder.requestFocus(Constants.SETTING_ITEM_0);
        } else {
            mWifiSettingsHolder.clearFocus(mSettingItem);
            mSettingItem = Constants.SETTING_ITEM_0;
        }
    }

    public void setWifiEnabled(boolean enable) {
        if (enable) {
            if (!mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
                mScanner.resume();
            }
        } else {
            int wifiState = mWifiManager.getWifiState();
            if ((wifiState == WifiManager.WIFI_STATE_ENABLING)
                    || (wifiState == WifiManager.WIFI_STATE_ENABLED)) {
                mWifiManager.setWifiEnabled(false);
                mScanner.pause();
            }
        }
    }

    private void showWifiInfo() {
        // init wifiSwitch checkbox:
        int state = mWifiManager.getWifiState();
        if (WifiManager.WIFI_STATE_ENABLING == state || WifiManager.WIFI_STATE_ENABLED == state) {
            mWifiToggleCheckBox.setChecked(true);
            toggleNetwork(true);
        } else {
            mWifiToggleCheckBox.setChecked(false);
            toggleNetwork(false);
        }
    }

    private void refreshWifiSignal() {
        // save current selection
        int selected = mWifiSignalListView.getSelectedItemPosition();

        ArrayList<ScanResult> accessPoints = new ArrayList<ScanResult>();
        // get all scaned wifiap around
        final List<ScanResult> results = mWifiManager.getScanResults();
        if (results != null) {
            for (ScanResult result : results) {
                // Ignore hidden and ad-hoc networks.
                if (TextUtils.isEmpty(result.SSID) || result.capabilities.contains("[IBSS]")) {
                    continue;
                }
                accessPoints.add(result);
            }
        }
        mResults.clear();
        // save access point
        mResults.addAll(accessPoints);

        mAdapter = new WiFiSignalListAdapter(mNetworkSettingsActivity, accessPoints);
        updateConnectionLable();
        mWifiSignalListView.setAdapter(mAdapter);
        mWifiSignalListView.setDividerHeight(0);
        if (selected > (mResults.size() - 1)) {
            mWifiSignalListView.setSelection(mResults.size() - 1);
        } else {
            mWifiSignalListView.setSelection(selected);
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        // filter.addAction(WifiManager.ERROR_ACTION);
        mNetworkSettingsActivity.registerReceiver(mWifiReceiver, filter);

        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(MWifiManager.WIFI_DEVICE_ADDED_ACTION);
        wifiFilter.addAction(MWifiManager.WIFI_DEVICE_REMOVED_ACTION);
        mNetworkSettingsActivity.registerReceiver(mWifiHWReceiver, wifiFilter);
    }

    private void updateConnectionState(DetailedState state) {
        WifiConfiguration config = getWifiConfiguredNetwork();
        if (config != null && config.status == WifiConfiguration.Status.DISABLED) {
            Log.d(TAG, "config, " + config.toString());
            switch (config.disableReason) {
                case WifiConfiguration.DISABLED_AUTH_FAILURE:
                    // showt(R.string.wifi_disabled_password_failure));
                    break;
                case WifiConfiguration.DISABLED_DHCP_FAILURE:
                case WifiConfiguration.DISABLED_DNS_FAILURE:
                    // setSummary(context.getString(R.string.wifi_disabled_network_failure));
                    break;
                case WifiConfiguration.DISABLED_UNKNOWN_REASON:
                    // setSummary(context.getString(R.string.wifi_disabled_generic));
            }
        }
    }

    private void updateWifiState(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLED:
                MainActivity.isWifiOn=true;
                mScanner.resume();
                return; // not break, to avoid the call to pause() below
            case WifiManager.WIFI_STATE_ENABLING:
                // addMessagePreference(R.string.wifi_starting);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
            case WifiManager.WIFI_STATE_DISABLED:
                MainActivity.isWifiOn=false;
                // addMessagePreference(R.string.wifi_empty_list_wifi_off);
                if (mAdapter != null) {
                    mAdapter.updateConnectedSsid("", false);
                }
                break;
        }
        mScanner.pause();
    }

    private void updateConnectionLable() {
        // wifi is connected
        WifiInfo info = mWifiManager.getConnectionInfo();
        if (info != null && info.getNetworkId() != WifiConfiguration.INVALID_NETWORK_ID) {
            Log.d(TAG, "info.getSSID, " + info.getSSID());
            if (mAdapter != null) {
                mAdapter.updateConnectedSsid(info.getSSID(), true);
            }
        }
    }

    /**
     * Description: toggle network.
     *
     * @param enable true for wifi, false for ethernet.
     */
    private void toggleNetwork(boolean enable) {
        if (enable) {
            // open wifi and close ethernet
            setWifiEnabled(true);
            // close ethernet
            //getEthernetManager().setEnabled(false);
            mWifiSignalListView.setVisibility(View.VISIBLE);
            mFooterView.setVisibility(View.VISIBLE);
            refreshWifiSignal();
        } else {
            // close wifi and open ethernet
            setWifiEnabled(false);
            //modify by ken.bi [2013-4-12]
            //getEthernetManager().setEnabled(true);
            mWifiSignalListView.setVisibility(View.INVISIBLE);
            mFooterView.setVisibility(View.INVISIBLE);
        }
    }

    private void updateWifiProxy(ProxyInfo proxy) {
        WifiConfiguration currentConfig = getWifiConfiguredNetwork();
        if (currentConfig == null) {
            Tools.logd(TAG, "unbelievable");
            return;
        }
        Tools.logd(TAG, "currentConfig, " + currentConfig.toString());

        LinkProperties linkProperties = new LinkProperties();
        // set proxy
        if (proxy == null) {
            currentConfig.proxySettings = ProxySettings.NONE;
        } else {
            ProxyProperties pp = new ProxyProperties(proxy.mHost, proxy.mPort, "");
            linkProperties.setHttpProxy(pp);
            currentConfig.proxySettings = ProxySettings.STATIC;
        }
        currentConfig.linkProperties = new LinkProperties(linkProperties);

        Tools.logd(TAG, "currentConfig, " + currentConfig.toString());
        // save config
        mWifiManager.save(currentConfig, mSaveListener);
    }

    private void showToast(int id) {
        if (id <= 0) {
            Log.d(TAG, "id < 0");
            return;
        }

        Toast.makeText(mNetworkSettingsActivity, id, Toast.LENGTH_SHORT).show();
    }

    private void setListener() {
        mWifiSignalListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == (mResults.size())) {
                    // show dialog to add hidden SSID.
                    Log.d(TAG, "clicked position:" + position);
                    WiFiConnectDialog connectDialog = new WiFiConnectDialog(
                            mNetworkSettingsActivity, WifiSettings.this, null);
                    
                    connectDialog.show();

                } else {
                    ScanResult scanResult = mResults.get(position);
                    WiFiConnectDialog connectDialog = new WiFiConnectDialog(
                            mNetworkSettingsActivity, WifiSettings.this, scanResult);
                    connectDialog.show();
                }
            }
        });

        mWifiToggleCheckBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "wifi toggle check box onClick");

                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;

//                    if(!mNetworkSettingsActivity.getPPPoESettings().getPPPoEManager().PppoeGetStatus().equals(PPPOE_STA.DISCONNECTED)){
//                            showToast(R.string.please_hangup_pppoe);
//                            if(mWifiToggleCheckBox.isChecked()){
//                                mWifiToggleCheckBox.setChecked(false);
//                            }else{
//                                mWifiToggleCheckBox.setChecked(true);
//                            }
//                            return;
//                    }

                    // wifi dongle is ready
                    if (!MWifiManager.getInstance().isWifiDeviceExist()) {
                        showToast(R.string.please_insert_dongle);
                        checkBox.setChecked(false);
                        MainActivity.isWifiOn=false;
                        return;
                    }

                    // wifi pppoe is active
                    if (PPPOE_STA.DISCONNECTED != getPPPoEManager().PppoeGetStatus()
                            && isWifiPPPoE()) {
                        showToast(R.string.please_hangup_pppoe);
                        checkBox.setChecked(!checkBox.isChecked());
                        MainActivity.isWifiOn=false;
                        return;
                    }

                    // fix mantis bug 0328611
                    // wifi ap is active
                    if (mWifiManager.isWifiApEnabled()) {
                        showToast(R.string.close_wifiap_txt);
                        checkBox.setChecked(false);
                        MainActivity.isWifiOn=false;
                        return;
                    }

                    // open wifi
                    toggleNetwork(checkBox.isChecked());
                    MainActivity.isWifiOn=true;
                }
            }
        });
    }

    private class Scanner extends Handler {
        private int mRetry = 0;

        void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        void pause() {
            mRetry = 0;
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message message) {
            if (mWifiManager.isWifiEnabled()) {
                Log.d(TAG, "startScanActive");
                if (mWifiManager.startScan()) {
                    mRetry = 0;
                } else if (++mRetry >= 3) {
                    mRetry = 0;
                    return;
                }
                sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
            }
        }
    }

    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action, " + action);
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN));
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                mConnected.set(networkInfo.isConnected());
                updateConnectionState(networkInfo.getDetailedState());
                refreshWifiSignal();

            } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                SupplicantState state = (SupplicantState) intent
                        .getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if (!mConnected.get() && SupplicantState.isHandshakeState(state)) {
                    updateConnectionState(WifiInfo.getDetailedStateOf(state));
                }

            } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
                updateConnectionState(null);

                // scan available wifiap
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
                    || WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action)
                    || WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
                refreshWifiSignal();
            }
        }
    };

    private BroadcastReceiver mWifiHWReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MWifiManager.WIFI_DEVICE_REMOVED_ACTION.equals(action)) {
                onWifiHWChanged(false);
            } else if (MWifiManager.WIFI_DEVICE_ADDED_ACTION.equals(action)) {
                onWifiHWChanged(true);
            }
        }
    };

    private WifiManager.ActionListener mSaveListener = new WifiManager.ActionListener() {

        public void onSuccess() {
            Log.d(TAG, "save success");
        }

        public void onFailure(int reason) {
            showToast(R.string.wifi_failed_save_message);
        }
    };

}
