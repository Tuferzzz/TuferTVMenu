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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;
import tufer.com.menutest.UIActivity.network.ProxySettings.ProxyInfo;
import tufer.com.menutest.Util.Tools;

import com.mstar.android.pppoe.PPPOE_STA;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;


import com.mstar.android.wifi.MWifiManager;

public class WifiApSettings extends NetworkSettings implements INetworkSettingsListener {

    private static final String TAG = "WifiApSettings";

    public static final int SECURE_TYPE_WPA = 0;
    public static final int SECURE_TYPE_WPA2 = 1;
    public static final int SECURE_TYPE_OPEN = 2;

    // current secure type
    private int mSecureType = SECURE_TYPE_WPA;

    private NetworkSettingsActivity mActivity;

    private WifiApSettingsHolder mWifiApSettingsHolder;

    // setting item on the right
    private int mSettingItem = Constants.SETTING_ITEM_0;

    public WifiApSettings(NetworkSettingsActivity activity) {
        super(activity);
        this.mActivity = activity;
        this.mWifiApSettingsHolder = new WifiApSettingsHolder(activity);

        // register receiver
        registerReceiver();

        Button saveConfig = mWifiApSettingsHolder.getSaveConfigButton();
        saveConfig.setOnClickListener(onClickListener);

        CheckBox checkBox = mWifiApSettingsHolder.getWifiApToggleCheckBox();
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void onExit() {
        mActivity.unregisterReceiver(mWifiApStateChange);
        mActivity.unregisterReceiver(mWifiHWReceiver);
    }

    @Override
    public boolean onKeyEvent(int keyCode, KeyEvent keyEvent) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mSettingItem == Constants.SETTING_ITEM_3) {
                    switchSecure(false);
                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mSettingItem == Constants.SETTING_ITEM_3) {
                    switchSecure(true);
                    return true;

                    // avoid focus change from checkbox to save button
                } else if (mSettingItem == Constants.SETTING_ITEM_2
                        || mSettingItem == Constants.SETTING_ITEM_4
                        || mSettingItem == Constants.SETTING_ITEM_5) {
                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mSettingItem == 0) {
                    return true;
                } else if (mSettingItem > 0 && mSettingItem <= Constants.SETTING_ITEM_6) {
                    mSettingItem--;
                    mWifiApSettingsHolder.requestFocus(mSettingItem);
                } else {
                    Tools.logd(TAG, "invalid position");
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mWifiApSettingsHolder.isConfigLayoutVisible()) {
                    if (mSettingItem >= 0 && mSettingItem < Constants.SETTING_ITEM_6) {
                        mSettingItem++;
                        mWifiApSettingsHolder.requestFocus(mSettingItem);
                    } else if (mSettingItem == Constants.SETTING_ITEM_6) {
                        return true;
                    } else {
                        Tools.logd(TAG, "invalid position");
                    }
                } else {
                    if (mSettingItem == Constants.SETTING_ITEM_0) {
                        mSettingItem++;
                        mWifiApSettingsHolder.requestFocus(mSettingItem);
                    } else if (mSettingItem == Constants.SETTING_ITEM_1) {
                        return true;
                    } else {
                        Tools.logd(TAG, "invalid position");
                    }
                }

                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onWifiHWChanged(boolean isOn) {
        if (isOn) {

        } else {
            if (WifiManager.WIFI_AP_STATE_ENABLED == getWifiManager().getWifiApConfiguration().status) {
                Log.d(TAG, "close wifiap");
                setSoftApEnabled(false);
            }
        }
    }

    public void setVisible(boolean visible) {
        mWifiApSettingsHolder.setVisible(visible);
        if (visible) {
            // refresh ui
            refreshWifiApUi();
        }
    }

    @Override
    public void onFocusChange(boolean hasFocus) {
        // entry wifiap settings
        if (hasFocus) {
            mWifiApSettingsHolder.requestFocus(Constants.SETTING_ITEM_0);

            // exit from wifiap settings
        } else {
            mWifiApSettingsHolder.clearFocus(mSettingItem);
            mSettingItem = Constants.SETTING_ITEM_0;
        }
    }

    @Override
    public void onProxyChanged(boolean enabled, ProxyInfo proxyInfo) {

    }

    private void switchSecure(boolean increase) {
        if (increase) {
            mSecureType++;
            mSecureType %= 3;
        } else {
            mSecureType--;
            mSecureType = (mSecureType + 3) % 3;
        }
        Tools.logd(TAG, "mSecureType, " + mSecureType);
        mWifiApSettingsHolder.refreshSecureType(mSecureType);
        // hide passwd layout
        if (mSecureType == 2) {
            mWifiApSettingsHolder.setPasswdLayoutVisiable(false);
        } else {
            mWifiApSettingsHolder.setPasswdLayoutVisiable(true);
        }
    }

    private void refreshWifiApUi() {
        WifiManager wifiManager = getWifiManager();
        WifiConfiguration config = wifiManager.getWifiApConfiguration();
        if (config == null) {
            Tools.logd(TAG, "wifiap config is null.");
            mWifiApSettingsHolder.refreshWifiApInfo(null);
        } else {
            // show Wi-Fi Ap info
            mWifiApSettingsHolder.refreshWifiApInfo(config);
        }
    }

    private void saveWifiApConfig() {
        String passwd = mWifiApSettingsHolder.getPassword();
        // check passwd
        if (mSecureType != 3 && (TextUtils.isEmpty(passwd) || passwd.length() < 8)) {
            showToast(R.string.wifiap_pwd_notice);
            return;
        }

        String ssid = mWifiApSettingsHolder.getSSID();
        // check ssid
        if (TextUtils.isEmpty(ssid)) {
            showToast(R.string.please_input_ssid);
            return;
        }

        WifiConfiguration config = getApConfig();
        if (config == null) {
            return;
        }

        WifiManager wifiManager = getWifiManager();
        if (WifiManager.WIFI_AP_STATE_ENABLED == wifiManager.getWifiApState()) {
            // restart wifiap
            wifiManager.setWifiApEnabled(null, false);
            wifiManager.setWifiApEnabled(config, true);
        } else {
            wifiManager.setWifiApConfiguration(config);
        }
        // configure successful
        showToast(R.string.wifiap_config_success);
    }

    private WifiConfiguration getApConfig() {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = mWifiApSettingsHolder.getSSID();

        // set secure type
        switch (mSecureType) {
            case SECURE_TYPE_WPA:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.preSharedKey = mWifiApSettingsHolder.getPassword();
                return config;
            case SECURE_TYPE_WPA2:
                config.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.preSharedKey = mWifiApSettingsHolder.getPassword();
                return config;
            case SECURE_TYPE_OPEN:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                return config;
            default:
                break;
        }

        return null;
    }

    private boolean hasReady() {
        MWifiManager mWifiManager = MWifiManager.getInstance();
        // /no dongle plug
        if (!mWifiManager.isWifiDeviceExist()) {
            showToast(R.string.please_insert_dongle);
            return false;
        }

        // check is wifiDevice support wifi hotspot.
        if (!mWifiManager.isWifiDeviceSupportSoftap()) {
            showToast(R.string.device_do_not_support);
            return false;
        }

        // fix mantis bug 0328611
        // check whether wifi is open
        WifiManager wifiManager = getWifiManager();
        if (wifiManager.isWifiEnabled()) {
            showToast(R.string.close_wifi_txt);
            return false;
        }

        // check wifi pppoe
        if (PPPOE_STA.DISCONNECTED != getPPPoEManager().PppoeGetStatus() && isWifiPPPoE()) {
            showToast(R.string.please_hangup_pppoe);
            return false;
        }

        return true;
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
        mActivity.registerReceiver(mWifiApStateChange, filter);

        // wifi hw changed
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(MWifiManager.WIFI_DEVICE_ADDED_ACTION);
        wifiFilter.addAction(MWifiManager.WIFI_DEVICE_REMOVED_ACTION);
        mActivity.registerReceiver(mWifiHWReceiver, wifiFilter);
    }

    private void setSoftApEnabled(boolean enable) {
        int apState = getWifiManager().getWifiApState();
        if (enable) {
            // wifi ap is enabling or has enabled
            if (apState == WifiManager.WIFI_AP_STATE_ENABLING
                    || apState == WifiManager.WIFI_AP_STATE_ENABLED) {
                return;
            }
        } else {
            // wifi ap is disablinig or has disabled
            if (apState == WifiManager.WIFI_AP_STATE_DISABLING
                    || apState == WifiManager.WIFI_AP_STATE_DISABLED) {
                return;
            }
        }

        CheckBox checkBox = mWifiApSettingsHolder.getWifiApToggleCheckBox();
        if (apState == WifiManager.WIFI_AP_STATE_ENABLED) {
            checkBox.setChecked(true);
            MainActivity.isWifiHotspotOn=true;
        } else {
            checkBox.setChecked(false);
            MainActivity.isWifiHotspotOn=false;
        }

        if (getWifiManager().setWifiApEnabled(null, enable)) {
            // Disable here, enabled on receiving success broadcast
            checkBox.setEnabled(false);
        }
    }

    private void showToast(int id) {
        Toast.makeText(mActivity, id, Toast.LENGTH_SHORT).show();
    }

    // wifiap state change receiver
    private BroadcastReceiver mWifiApStateChange = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Tools.logd(TAG, "action, " + action);
            if (WifiManager.WIFI_AP_STATE_CHANGED_ACTION.equals(action)) {
                CheckBox checkBox = mWifiApSettingsHolder.getWifiApToggleCheckBox();
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_AP_STATE,
                        WifiManager.WIFI_AP_STATE_FAILED);
                switch (state) {
                    case WifiManager.WIFI_AP_STATE_ENABLING:
                        Log.d(TAG, "WIFI_AP_STATE_ENABLING");
                        checkBox.setEnabled(false);
                        break;
                    case WifiManager.WIFI_AP_STATE_ENABLED:
                        // on enable is handled by tether broadcast notice
                        Log.d(TAG, "WIFI_AP_STATE_ENABLED");
                        checkBox.setChecked(true);
                        // Doesnt need the airplane check
                        checkBox.setEnabled(true);
                        MainActivity.isWifiHotspotOn=true;
                        break;
                    case WifiManager.WIFI_AP_STATE_DISABLING:
                        Log.d(TAG, "WIFI_AP_STATE_DISABLING");
                        checkBox.setEnabled(false);
                        mWifiApSettingsHolder.refreshWifiApInfo(null);
                        break;
                    case WifiManager.WIFI_AP_STATE_DISABLED:
                        Log.d(TAG, "WIFI_AP_STATE_DISABLED");
                        checkBox.setEnabled(true);
                        checkBox.setChecked(false);
                        MainActivity.isWifiHotspotOn=false;
                        break;
                    default:
                        checkBox.setChecked(false);
                        MainActivity.isWifiHotspotOn=false;
                        break;
                }
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

    // listener for save config
    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            // save wifiap config
            if (R.id.hotspot_save_btn == view.getId()) {
                if (mWifiApSettingsHolder.getWifiApToggleCheckBox().isChecked()) {
                    saveWifiApConfig();
                }
            }
        }
    };

    // listener for checkbox
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!hasReady()) {
                Tools.logd(TAG, "wifiap have not ready.");
                buttonView.setChecked(false);
                MainActivity.isWifiHotspotOn=false;
                return;
            }
            Tools.logd(TAG, "wifiap has ready.");
            MainActivity.isWifiHotspotOn=true;
            setSoftApEnabled(isChecked);
        }
    };

}
