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

import java.net.InetAddress;
import java.util.Iterator;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.ProxyProperties;
import android.net.RouteInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.ProxySettings;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.ActionListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.network.ProxySettings.ProxyInfo;
import tufer.com.menutest.Util.Tools;

public class WiFiConnectDialog extends Dialog {

    private static final String TAG = "MSettings.WiFiConnectDialog";

    public static final int SECURE_OPEN = 0;
    public static final int SECURE_WEP = 1;
    public static final int SECURE_PSK = 2;
    public static final int SECURE_EAP = 3;

    private NetworkSettingsActivity mNetworkSettingsActivity;

    private WiFiConnectDialogHolder mWifiConnectDialogHolder;

    private WifiSettings mWifiSettings;

    private WifiManager mWifiManager;

    private ScanResult mScanResult;

    // auto ip config check box
    private CheckBox mAutoIpCheckBox;
    // save and cancel button
    private Button mSaveButton;
    private Button mCancelButton;
    private Button mForgetButton;

    private boolean mHasConfiged;
    // current secure
    private int mSecureType = SECURE_OPEN;

    /**
     * @param activity {@link NetworkSettingsActivity}.
     * @param scanResult {@link ScanResult}. null to connect hide ssid,
     *            otherwise connect or edit select.
     */
    public WiFiConnectDialog(NetworkSettingsActivity activity, WifiSettings wifiSettings,
            ScanResult scanResult) {
        super(activity);
        this.mNetworkSettingsActivity = activity;
        this.mWifiSettings = wifiSettings;
        this.mScanResult = scanResult;
        mWifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_connect);

        this.mWifiConnectDialogHolder = new WiFiConnectDialogHolder(this);
        this.mAutoIpCheckBox = mWifiConnectDialogHolder.getAutoIpCheckBox();
        this.mSaveButton = mWifiConnectDialogHolder.getSaveButton();
        this.mCancelButton = mWifiConnectDialogHolder.getCancelButton();
        this.mForgetButton = mWifiConnectDialogHolder.getForgetButton();
        // init all controller
        registerListener();
        initUi();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mWifiConnectDialogHolder.isSecureTypeFocused()) {
                mSecureType--;
                mSecureType = (mSecureType + 3) % 3;
                mWifiConnectDialogHolder.setSecure(mSecureType);
                setWindowSize();

                return true;
            }
            // make sure move focus to save button
            if (mCancelButton.isFocused() || mSaveButton.isFocused()) {
                return false;
            } else {
                return true;
            }

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mWifiConnectDialogHolder.isSecureTypeFocused()) {
                mSecureType++;
                mSecureType %= 3;
                mWifiConnectDialogHolder.setSecure(mSecureType);
                setWindowSize();

                return true;
            }
            // make sure move focus to cancel button
            if (mSaveButton.isFocused() || mForgetButton.isFocused()) {
                return false;
            } else {
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void initUi() {
        // add hide ssid
        if (mScanResult == null) {
            mHasConfiged = false;

            mWifiConnectDialogHolder.setSsidLayoutVisible(true);
            mWifiConnectDialogHolder.setSecureTypeFocusable(true);
            mWifiConnectDialogHolder.refreshConnectTitle(R.string.add_wifi_net);
            mAutoIpCheckBox.setChecked(true);
            mSecureType = SECURE_OPEN;

            // edit or connect select ssid
        } else {
            // wifi secirity type.
            mSecureType = getSecurity(mScanResult);
            String selectSsid = mScanResult.SSID;
            Tools.logd(TAG, "select ssid, " + selectSsid);

            mWifiConnectDialogHolder.refreshConnectTitle(selectSsid);
            mWifiConnectDialogHolder.setSsidLayoutVisible(false);

            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID().replace("\"", "");
            // show dialog to connect/edit the selected SSID.
            if (selectSsid.equals(ssid)) {
                mHasConfiged = true;
                // refresh ip fields and auto checkbox
                refreshConfigUi();
            } else {
                mHasConfiged = false;
                // connect
                mAutoIpCheckBox.setChecked(true);
                mForgetButton.setVisibility(View.GONE);
            }
        }
        mWifiConnectDialogHolder.setSecure(mSecureType);

        // calc height of window
        setWindowSize();
    }

    private void setWindowSize() {
        Window w = getWindow();
        w.setBackgroundDrawableResource(R.drawable.dialog_bg);
        w.setTitle(null);

        Point point = new Point();
        Display display = w.getWindowManager().getDefaultDisplay();
        display.getSize(point);
        int width = (int) (point.x * 0.5);
        int height = (int) (point.y * 0.6);

        // calc height of dialog according to the security type
        switch (mSecureType) {
            case SECURE_OPEN:
                mWifiConnectDialogHolder.setPasswdLayoutVisible(false);
                if (mScanResult == null) {
                    if (mAutoIpCheckBox.isChecked()) {
                        height = (int) (point.y * 0.4);
                    } else {
                        height = (int) (point.y * 0.7);
                    }
                } else {
                    if (mAutoIpCheckBox.isChecked()) {
                        height = (int) (point.y * 0.36);
                    } else {
                        height = (int) (point.y * 0.7);
                    }
                }
                break;
            case SECURE_WEP:
            case SECURE_PSK:
            case SECURE_EAP:
                mWifiConnectDialogHolder.setPasswdLayoutVisible(true);
                if (mAutoIpCheckBox.isChecked()) {
                    height = (int) (point.y * 0.5);
                } else {
                    height = (int) (point.y * 0.8);
                }
                break;
            default:
                break;
        }

        w.setLayout(width, height);
        w.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams wl = w.getAttributes();
        w.setAttributes(wl);
    }

    /**
     * get the security level.
     */
    private int getSecurity(ScanResult result) {
        Log.d(TAG, "ScanResult.capabilities, " + result.capabilities);
        if (result.capabilities.contains("WEP")) {
            return SECURE_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURE_PSK;
        }

        return SECURE_OPEN;
    }

    private WifiConfiguration getNewConfig() {
        WifiConfiguration config = new WifiConfiguration();
        if (mScanResult == null) {
            config.SSID = convertToQuotedString(mWifiConnectDialogHolder.getSsid());
            // If the user adds a network manually, assume that it is hidden.
            config.hiddenSSID = true;
        } else {
            config.SSID = convertToQuotedString(mScanResult.SSID);
        }

        String passwd = mWifiConnectDialogHolder.getPasswd();
        switch (mSecureType) {
            case SECURE_OPEN:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                break;
            case SECURE_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                if (passwd.length() != 0) {
                    int length = passwd.length();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58)
                            && passwd.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = passwd;
                    } else {
                        config.wepKeys[0] = '"' + passwd + '"';
                    }
                }
                break;
            case SECURE_PSK:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                if (passwd.length() != 0) {
                    if (passwd.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = passwd;
                    } else {
                        config.preSharedKey = '"' + passwd + '"';
                    }
                }
                break;
            default:
                return null;
        }

        return config;
    }

    private void refreshConfigUi() {
        WifiConfiguration config = mWifiSettings.getWifiConfiguredNetwork();
        if (config == null) {
            mAutoIpCheckBox.setChecked(true);
            mWifiConnectDialogHolder.refreshPasswd("");
            mWifiConnectDialogHolder.refreshIp("");
            mWifiConnectDialogHolder.refreshNetmask("");
            mWifiConnectDialogHolder.refreshGateway("");
            mWifiConnectDialogHolder.refreshDns1("");
            mWifiConnectDialogHolder.refreshDns2("");

        } else {
            mForgetButton.setVisibility(View.VISIBLE);
            // refresh password
            mWifiConnectDialogHolder.refreshPasswdHint();

            // is auto config
            if (IpAssignment.DHCP == config.ipAssignment) {
                mAutoIpCheckBox.setChecked(true);
            } else if (IpAssignment.STATIC == config.ipAssignment) {
                mAutoIpCheckBox.setChecked(false);
            }

            // link properties contain ip/gateway/dns
            LinkProperties linkProperties = config.linkProperties;
            Iterator<LinkAddress> iterator = linkProperties.getLinkAddresses().iterator();
            // ip info
            String ip = null;
            if (iterator.hasNext()) {
                LinkAddress linkAddress = iterator.next();
                ip = linkAddress.getAddress().getHostAddress();
                mWifiConnectDialogHolder.refreshIp(ip);
            }

            // gateway info
            String gateway = null;
            for (RouteInfo route : linkProperties.getRoutes()) {
                if (route.isDefaultRoute()) {
                    gateway = route.getGateway().getHostAddress();
                    mWifiConnectDialogHolder.refreshGateway(gateway);
                    break;
                }
            }
            // dns1
            Iterator<InetAddress> dnsIterator = linkProperties.getDnses().iterator();
            if (dnsIterator.hasNext()) {
                String dns = dnsIterator.next().getHostAddress();
                if (Tools.matchIP(dns)) {
                    mWifiConnectDialogHolder.refreshDns1(dns);
                }
            }
            // dns2
            if (dnsIterator.hasNext()) {
                String dns = dnsIterator.next().getHostAddress();
                if (Tools.matchIP(dns)) {
                    mWifiConnectDialogHolder.refreshDns2(dns);
                }
            }

            // spell netmask
            mWifiConnectDialogHolder.refreshNetmask(Tools.buildUpNetmask(ip, gateway));
        }
    }

    private boolean setSecurity(WifiConfiguration config, WifiConfiguration currentConfig) {
        if (currentConfig.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
        } else if (currentConfig.allowedKeyManagement.get(KeyMgmt.WPA_EAP)
                || currentConfig.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
            config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
            //JB 4.3 remove those parameters. start
//            config.eap.setValue(getSecurity(config));
//            config.phase2.setValue("");
//            config.ca_cert.setValue("");
//            config.client_cert.setValue("");
//            config.key_id.setValue("");
//            config.identity.setValue("");
//            config.anonymous_identity.setValue("");
            //JB 4.3 remove those parameters. end
        }

        if (currentConfig.wepKeys[0] == null) {
            config.allowedKeyManagement.set(KeyMgmt.NONE);
        } else {
            config.allowedKeyManagement.set(KeyMgmt.NONE);
            config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
        }

        return true;
    }

    private void setIpFields(WifiConfiguration config, LinkProperties linkProperties) {
        // is auto ip
        if (mAutoIpCheckBox.isChecked()) {
            config.ipAssignment = IpAssignment.DHCP;
        } else {
            config.ipAssignment = IpAssignment.STATIC;
            // config linkProperties

            String ip = mWifiConnectDialogHolder.getIp();
            String gateway = mWifiConnectDialogHolder.getGateway();
            String dns1 = mWifiConnectDialogHolder.getDns1();
            String dns2 = mWifiConnectDialogHolder.getDns2();
            try {
                InetAddress inetIp = NetworkUtils.numericToInetAddress(ip);
                linkProperties.addLinkAddress(new LinkAddress(inetIp, 24));

                InetAddress inetGateway = NetworkUtils.numericToInetAddress(gateway);
                linkProperties.addRoute(new RouteInfo(inetGateway));

                InetAddress inetDns1 = NetworkUtils.numericToInetAddress(dns1);
                linkProperties.addDns(inetDns1);

                InetAddress inetDns2 = NetworkUtils.numericToInetAddress(dns2);
                linkProperties.addDns(inetDns2);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Tools.logd(TAG, "ip fields invalid.");
            }
        }
    }

    private void setProxyFields(WifiConfiguration config, LinkProperties linkProperties) {
        if (mWifiSettings.hasProxy()) {
            ProxyInfo proxyInfo = mWifiSettings.getProxyInfo();
            ProxyProperties proxy = new ProxyProperties(proxyInfo.mHost, proxyInfo.mPort, "");
            linkProperties.setHttpProxy(proxy);
            config.proxySettings = ProxySettings.STATIC;
        } else {
            config.proxySettings = ProxySettings.NONE;
        }
    }

    private boolean checkIpFields() {
    	String ip = mWifiConnectDialogHolder.getIp();
    	String netMask = mWifiConnectDialogHolder.getNetmask();
    	String gateWay = mWifiConnectDialogHolder.getGateway();
    	String dns1 = mWifiConnectDialogHolder.getDns1();
    	String dns2 = mWifiConnectDialogHolder.getDns2();
    	
    	boolean flag = mWifiConnectDialogHolder.getAutoIpCheckBox().isChecked();
    	
    	// auto ip is checked
    	if(flag){
    		return true;
    	}
    	
    	if(ip==null || netMask==null || gateWay==null){
    		showToast(R.string.check_ip_failure);
    		return false;
    	}else if(ip.trim().equals("") || netMask.trim().equals("") || gateWay.trim().equals("")){
    		showToast(R.string.check_ip_failure);
    		return false;
    	}
    	
        return true;
    }

    private void editConfig() {
        WifiConfiguration currentConfig = mWifiSettings.getWifiConfiguredNetwork();
        if (currentConfig == null) {
            Tools.logd(TAG, "unbelievable");
            return;
        }

        WifiConfiguration config = new WifiConfiguration();
        boolean isSupportSecurity = setSecurity(config, currentConfig);
        if (isSupportSecurity) {
            config.networkId = mWifiManager.getConnectionInfo().getNetworkId();

            LinkProperties linkProperties = new LinkProperties();
            setProxyFields(config, linkProperties);
            setIpFields(config, linkProperties);
            // init linkProperties
            config.linkProperties = new LinkProperties(linkProperties);

            // save config
            mWifiManager.save(config, mSaveListener);
        } else {
            // hint
            showToast(R.string.not_support_security_hint);
        }
    }

    private void newConnect() {
        String ssid = mWifiConnectDialogHolder.getSsid();
        // connect hide ssid
        if (mScanResult == null) {
            // invalid ssid
            if (TextUtils.isEmpty(ssid)) {
                showToast(R.string.ssid_password_error);
                return;
            }
        }

        String passwd = mWifiConnectDialogHolder.getPasswd();
        // invalid passwd
        if (mSecureType != SECURE_OPEN && TextUtils.isEmpty(passwd)) {
            showToast(R.string.input_password);
            return;
        }

        WifiConfiguration config = getNewConfig();
        if (config == null) {
            Tools.logd(TAG, "unkown secure type");
        } else {
            LinkProperties linkProperties = new LinkProperties();
            setProxyFields(config, linkProperties);
            setIpFields(config, linkProperties);
            // init linkProperties
            config.linkProperties = new LinkProperties(linkProperties);

            // connect to ssid
            mWifiManager.connect(config, mConnectListener);
        }
    }

    private String getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return "WPA_PSK";
        } else if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP)
                || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return "WPA_EAP";
        }

        return (config.wepKeys[0] != null) ? "SECURITY_WEP" : "SECURITY_NONE";
    }

    private String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    private void showToast(int id) {
        if (id <= 0) {
            return;
        }

        Toast.makeText(mNetworkSettingsActivity, id, Toast.LENGTH_SHORT).show();
    }

    private void registerListener() {
        mAutoIpCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setWindowSize();
                    mWifiConnectDialogHolder.setIpConfigLayoutVisible(false);
                } else {
                    setWindowSize();
                    mWifiConnectDialogHolder.setIpConfigLayoutVisible(true);
                }
            }
        });

        mSaveButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // check ip/gateway
                if (checkIpFields()) {
                    // connect select ssid
                    if (mHasConfiged) {
                        editConfig();
                    } else {
                        newConnect();
                    }
                } else {
                    // FIXME invalid ip
                }
                dismiss();
            }
        });

        mForgetButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiConfiguration config = mWifiSettings.getWifiConfiguredNetwork();
                if (config != null) {
                    mWifiManager.forget(config.networkId, mForgetListener);
                }

                // dismiss dialog
                dismiss();
            }
        });
    }

    private WifiManager.ActionListener mSaveListener = new WifiManager.ActionListener() {

        public void onSuccess() {
            Log.d(TAG, "save success");
        }

        public void onFailure(int reason) {
            showToast(R.string.wifi_failed_save_message);
        }
    };

    private WifiManager.ActionListener mConnectListener = new WifiManager.ActionListener() {

        @Override
        public void onSuccess() {
            Log.d(TAG, "connect success");
        }

        @Override
        public void onFailure(int reason) {
            showToast(R.string.wifi_failed_connect_message);
        }
    };

    private WifiManager.ActionListener mForgetListener = new ActionListener() {

        @Override
        public void onSuccess() {
            Log.d(TAG, "forget success");
        }

        @Override
        public void onFailure(int reason) {
            showToast(R.string.wifi_failed_forget_message);
        }
    };

}
