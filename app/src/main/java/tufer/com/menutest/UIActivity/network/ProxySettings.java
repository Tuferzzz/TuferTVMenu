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

import android.net.LinkProperties;
import android.net.ProxyProperties;


import com.mstar.android.ethernet.EthernetDevInfo;
import com.mstar.android.ethernet.EthernetManager;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import tufer.com.menutest.R;


public class ProxySettings extends NetworkSettings implements INetworkSettingsListener {

    private static final String TAG = "MSettings.ProxySettings";

    private NetworkSettingsActivity mActivity;

    private ProxySettingsHolder mProxySettingsHolder;
    private CheckBox mProxyToggleCheckBox;
    private Button mSaveButton;
    private Button mCancelButton;

    private ProxyInfo mProxyInfo = null;

    private int mSettingItem = Constants.SETTING_ITEM_0;

    public ProxySettings(NetworkSettingsActivity activity) {
        super(activity);
        this.mActivity = activity;
        this.mProxyInfo = new ProxyInfo();

        mProxySettingsHolder = new ProxySettingsHolder(activity);
        mProxyToggleCheckBox = mProxySettingsHolder.getProxyToggleCheckBox();
        mSaveButton = mProxySettingsHolder.getSaveButton();
        mCancelButton = mProxySettingsHolder.getCancelButton();
        setListener();
    }

    public void setVisible(boolean visible) {
        mProxySettingsHolder.setVisible(visible);
        if (visible) {
            showProxyInfo();
        }
    }

    @Override
    public void onExit() {
    }

    @Override
    public boolean onKeyEvent(int keyCode, KeyEvent keyEvent) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mSettingItem == Constants.SETTING_ITEM_5) {
                    if (!mProxySettingsHolder.isAuthToggleOpen()) {
                        mSettingItem--;
                        mProxySettingsHolder.requestFocus(mSettingItem);

                        return true;
                    }
                } else if (mSettingItem == Constants.SETTING_ITEM_7) {
                    mSettingItem--;
                    mProxySettingsHolder.requestFocus(mSettingItem);

                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mSettingItem == Constants.SETTING_ITEM_1
                        || mSettingItem == Constants.SETTING_ITEM_2
                        || mSettingItem == Constants.SETTING_ITEM_3) {
                    return true;
                } else if (mSettingItem == Constants.SETTING_ITEM_4) {
                    if (!mProxySettingsHolder.isAuthToggleOpen()) {
                        mSettingItem++;
                        mProxySettingsHolder.requestFocus(mSettingItem);
                    }
                    return true;
                } else if (mSettingItem == Constants.SETTING_ITEM_5
                        || mSettingItem == Constants.SETTING_ITEM_7) {
                    return true;
                } else if (mSettingItem == Constants.SETTING_ITEM_6) {
                    mSettingItem++;
                    mProxySettingsHolder.requestFocus(mSettingItem);

                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mSettingItem > Constants.SETTING_ITEM_0
                        && mSettingItem < Constants.SETTING_ITEM_5) {
                    mSettingItem--;
                    mProxySettingsHolder.requestFocus(mSettingItem);
                } else if (mSettingItem == Constants.SETTING_ITEM_5) {
                    if (!mProxySettingsHolder.isAuthToggleOpen()) {
                        mSettingItem -= 2;
                        mProxySettingsHolder.requestFocus(mSettingItem);
                    } else {
                        mSettingItem--;
                        mProxySettingsHolder.requestFocus(mSettingItem);
                    }
                } else if (mSettingItem == Constants.SETTING_ITEM_6) {
                    mSettingItem--;
                    mProxySettingsHolder.requestFocus(mSettingItem);
                } else if (mSettingItem == Constants.SETTING_ITEM_7) {
                    mSettingItem -= 2;
                    mProxySettingsHolder.requestFocus(mSettingItem);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mSettingItem == Constants.SETTING_ITEM_0) {
                    if (mProxySettingsHolder.isProxyToggleOpen()) {
                        mSettingItem++;
                        mProxySettingsHolder.requestFocus(mSettingItem);
                    }
                } else if (mSettingItem > Constants.SETTING_ITEM_0
                        && mSettingItem <= Constants.SETTING_ITEM_3) {
                    mSettingItem++;
                    mProxySettingsHolder.requestFocus(mSettingItem);
                } else if (mSettingItem == Constants.SETTING_ITEM_4
                        || mSettingItem == Constants.SETTING_ITEM_5) {
                    if (mProxySettingsHolder.isAuthToggleOpen()) {
                        mSettingItem++;
                        mProxySettingsHolder.requestFocus(mSettingItem);
                    }
                }
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onFocusChange(boolean hasFocus) {
        if (hasFocus) {
            mProxySettingsHolder.requestFocus(mSettingItem);

        } else {
            mProxySettingsHolder.clearFocus(mSettingItem);
            mSettingItem = Constants.SETTING_ITEM_0;
        }
    }

    @Override
    public void onProxyChanged(boolean enabled, ProxyInfo proxyInfo) {
    }

    @Override
    public void onWifiHWChanged(boolean isOn) {
    }

    /**
     * Submit proxy settings.
     *
     * @param proxyInfo {@link ProxyInfo}
     * @return true if proxy is valid, otherwise false.
     */
    public boolean submit() {
        String host = mProxySettingsHolder.getHost();
        String port = mProxySettingsHolder.getPort();
        // FIXME check host and port
        if (TextUtils.isEmpty(host)) {
            showToast(R.string.proxy_error_invalid_host);
            return false;
        }

        if (TextUtils.isEmpty(port)) {
            showToast(R.string.proxy_error_invalid_port);
            return false;
        }

        mProxyInfo.mHost = host;
        mProxyInfo.mPort = Integer.parseInt(port);
        updateProxy(mProxyInfo);

        return true;
    }

    /**
     * Cancel proxy settings.
     */
    public void cancel() {
        // FIXME refresh ui
    }

    /**
     * Test proxy whether valid.
     *
     * @return true if proxy is valid, otherwise false.
     */
    public boolean test() {
        return true;
    }

    private void showProxyInfo() {
        EthernetManager ethernetManager = getEthernetManager();
        if (ethernetManager.isConfigured()) {
            EthernetDevInfo devInfo = ethernetManager.getSavedConfig();
            if (devInfo != null) {
                Log.d(TAG,
                        "ifname, " + devInfo.getIfName() + ", host, " + devInfo.getProxyHost()
                                + ", port, " + devInfo.getProxyPort() + ", proxy on, "
                                + devInfo.getProxyOn());
                mProxyInfo.mHost = devInfo.getProxyHost();
                String port = devInfo.getProxyPort();
                if (port != null) {
                    mProxyInfo.mPort = Integer.parseInt(port);
                }
                if (TextUtils.isEmpty(mProxyInfo.mHost) || TextUtils.isEmpty(port)) {
                    Log.d(TAG, "proxy is empty");
                } else {
                    mProxySettingsHolder.refreshProxyInfo(mProxyInfo);
                }
            }
            return;
        }

        WifiConfiguration config = getWifiConfiguredNetwork();
        if (config != null) {
            LinkProperties linkProperties = config.linkProperties;
            ProxyProperties proxyProperties = linkProperties.getHttpProxy();
            if (proxyProperties != null) {
                mProxyInfo.mHost = proxyProperties.getHost();
                mProxyInfo.mPort = proxyProperties.getPort();
                Log.d(TAG, "mProxyInfo.mHost," + mProxyInfo.mHost + ", mProxyInfo.mPort,"
                        + mProxyInfo.mPort);
                if (TextUtils.isEmpty(mProxyInfo.mHost) || mProxyInfo.mPort <= 0) {
                    Log.d(TAG, "proxy is empty");
                } else {
                    mProxySettingsHolder.refreshProxyInfo(mProxyInfo);
                }
            }
        }
    }

    private void showToast(int id) {
        if (id <= 0) {
            return;
        }

        Toast.makeText(mActivity, id, Toast.LENGTH_SHORT).show();
    }

    private void setListener() {
        mProxyToggleCheckBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;
                    boolean isChecked = checkBox.isChecked();
                    mProxySettingsHolder.setProxyConfigVisible(isChecked);
                    if (isChecked) {
                        // showProxyInfo();
                    } else {
                        updateProxy(null);
                    }
                }
            }
        });

        mSaveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                submit();
            }
        });

        mCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                // refresh current proxy info
                mProxySettingsHolder.refreshProxyInfo(mProxyInfo);
            }
        });
    }

    /**
     * Proxy Info class.
     */
    class ProxyInfo {
        String mHost;

        int mPort;

        String mExclusionList;

        String mUsr;

        String mPwd;

        ProxyInfo() {
        }

        ProxyInfo(ProxyInfo proxyInfo) {
        }

        ProxyInfo(String host, int port, String exclusion) {
            this(host, port, exclusion, null, null);
        }

        ProxyInfo(String host, int port, String exclusion, String user, String passwd) {
            this.mHost = host;
            this.mPort = port;
            this.mExclusionList = exclusion;
            this.mUsr = user;
            this.mPwd = passwd;
        }

        @Override
        public String toString() {
            return "host , " + mHost + " port, " + mPort + " usr, " + mUsr + " pwd, " + mPwd
                    + " exclusionList, " + mExclusionList;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ProxyInfo) {
                ProxyInfo other = (ProxyInfo) o;
                if (this.mHost == other.mHost && this.mPort == other.mPort
                        && this.mExclusionList == other.mExclusionList && this.mUsr == other.mUsr
                        && this.mPwd == other.mPwd) {
                    Log.d(TAG, "proxy is equal");
                    return true;
                }
            }

            return false;
        }
    }
}
