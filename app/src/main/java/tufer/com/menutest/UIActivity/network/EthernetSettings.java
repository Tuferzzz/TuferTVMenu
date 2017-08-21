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

import java.net.InterfaceAddress;
import java.net.NetworkInterface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;



import com.mstar.android.ethernet.EthernetDevInfo;
import com.mstar.android.ethernet.EthernetManager;
import com.mstar.android.ethernet.EthernetStateTracker;

import tufer.com.menutest.UIActivity.network.ProxySettings.ProxyInfo;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import tufer.com.menutest.Util.Tools;


public class EthernetSettings extends NetworkSettings implements INetworkSettingsListener {

    private static final String TAG = "MSettings.EthernetSettings";

    private NetworkSettingsActivity mNetworkSettingsActivity;

    private EthernetSettingsHolder mEthernetHolder;
    private CheckBox mEthernetToggle;
    private CheckBox mAutoIpToggle;
    private CheckBox mIPv6Toggle;

    private CheckBox mDevToggle;
    private boolean DeviceFlag  = false;

    public boolean isDeviceFlag() {
		return DeviceFlag;
	}

	private EthernetManager mEthernetManager;

    public EthernetManager getmEthernetManager() {
		return mEthernetManager;
	}

	// foucs item on the right
    private int mSettingItem = Constants.SETTING_ITEM_0;

    public EthernetSettings(NetworkSettingsActivity networkSettingsActivity) {
        super(networkSettingsActivity);

        mEthernetManager = getEthernetManager();


      //get device list flag, add by ken.bi [2013/5/6]
      String [] devices = mEthernetManager.getDeviceNameList();
      if(devices.length > 1){ // muti ether
      	DeviceFlag = true;
      }

      //FIXME test DUAL ETHER DEVICE
      //DeviceFlag = true;




        mNetworkSettingsActivity = networkSettingsActivity;
        mEthernetHolder = new EthernetSettingsHolder(networkSettingsActivity,DeviceFlag);

//        // add by ken.bi [2013/5/6]
//        mEthernetHolder.setDeviceFlag(this.isDeviceFlag());

        mEthernetToggle = mEthernetHolder.getEthernetToggleCheckBox();
        mAutoIpToggle = mEthernetHolder.getAutoIpCheckBox();
        mIPv6Toggle = mEthernetHolder.getIPv6CheckBox();
        setListener();
        addNetworkSettingListener(this);





        // register broadcast receiver.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        intentFilter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);

        mNetworkSettingsActivity.registerReceiver(mEthStateReceiver, intentFilter);
    }

    /**
     * ethernet setting layout visible.
     *
     * @param visible if visible.
     */
    public void setVisible(boolean visible) {
        Log.d(TAG, "visible, " + visible);
        mEthernetHolder.setEthernetVisible(visible);
        if (visible) {
            showEthernetInfo();
        }
    }

    @Override
    public void onExit() {
        mNetworkSettingsActivity.unregisterReceiver(mEthStateReceiver);
    }

    @Override
    public boolean onKeyEvent(int keyCode, KeyEvent keyEvent) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mSettingItem > Constants.SETTING_ITEM_0
                        && mSettingItem <= Constants.SETTING_ITEM_8) {
                    mSettingItem--;
                    mEthernetHolder.requestFocus(mSettingItem);
                } else if (mSettingItem == Constants.SETTING_ITEM_9) {
                    mSettingItem -= 2;
                    mEthernetHolder.requestFocus(mSettingItem);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mSettingItem == Constants.SETTING_ITEM_0) {
                    if (mEthernetHolder.isEthernetOpened()) {
                        mSettingItem++;
                        mEthernetHolder.requestFocus(mSettingItem);
                    } else {
                        return true;
                    }
                } else if (mSettingItem > Constants.SETTING_ITEM_0
                        && mSettingItem <= Constants.SETTING_ITEM_7) {
                    mSettingItem++;
                    mEthernetHolder.requestFocus(mSettingItem);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mSettingItem >= Constants.SETTING_ITEM_3
                        && mSettingItem <= Constants.SETTING_ITEM_7) {
                    return false;
                } else if (mSettingItem == Constants.SETTING_ITEM_9) {
                    mSettingItem--;
                    mEthernetHolder.requestFocus(mSettingItem);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mSettingItem >= Constants.SETTING_ITEM_0
                        && mSettingItem <= Constants.SETTING_ITEM_2) {
                    return true;
                } else if (mSettingItem >= Constants.SETTING_ITEM_3
                        && mSettingItem <= Constants.SETTING_ITEM_7) {
                    if (isLastFocused()) {
                        return true;
                    }
                } else if (mSettingItem == Constants.SETTING_ITEM_8) {
                    mSettingItem++;
                    mEthernetHolder.requestFocus(mSettingItem);
                    return true;
                } else if (mSettingItem == Constants.SETTING_ITEM_9) {
                    return true;
                }
                break;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onProxyChanged(boolean enabled, ProxyInfo proxyInfo) {
        Log.d(TAG, "onProxyChanged, " + enabled);
        if (isEthernetEnabled()) {
            // update proxy info
            updateEthernetProxy(proxyInfo);
        }
    }

    @Override
    public void onWifiHWChanged(boolean isOn) {
        Log.d(TAG, "isOn, " + isOn);
        if (!isOn) {
            getEthernetManager().setEnabled(true);
            showEthernetInfo();
        }
    }

    @Override
    public void onFocusChange(boolean hasFocus) {
        if (hasFocus) {
            mEthernetHolder.requestFocus(Constants.SETTING_ITEM_0);
        } else {
            mEthernetHolder.clearFocus(mSettingItem);
            mSettingItem = Constants.SETTING_ITEM_0;
        }
    }

    /**
     * Automatic acquisition IP.
     *
     * @return if got automatic.
     */
    public boolean isAutoIP() {
        EthernetDevInfo mEthInfo = mEthernetManager.getSavedConfig();
        if (null != mEthInfo
                && mEthInfo.getConnectMode().equals(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isV4FirstFocused() {
        if (mEthernetHolder.isV4FirstFocused() || mEthernetHolder.isV6Focus()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isConfigEditTextFocused() {
        if (mSettingItem >= Constants.SETTING_ITEM_3 && mSettingItem <= Constants.SETTING_ITEM_7) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isLastFocused() {
        if (mEthernetHolder.isV4LastFocused() || mEthernetHolder.isV6Focus()) {
            return true;
        } else {
            return false;
        }
    }

    private void handleEthStateChanged(int ethState, int previousEthState) {
        Log.d(TAG, "ethState, " + ethState + " ,previousEthState, " + previousEthState);
        switch (ethState) {
            case EthernetStateTracker.EVENT_HW_CONNECTED:
                Log.d(TAG, "EVENT_HW_CONNECTED");
                break;
            case EthernetStateTracker.EVENT_INTERFACE_CONFIGURATION_SUCCEEDED:
                Log.d(TAG, "EVENT_INTERFACE_CONFIGURATION_SUCCEEDED");
                showEthernetInfo();
                break;
            case EthernetStateTracker.EVENT_HW_DISCONNECTED:
                Log.d(TAG, "EVENT_HW_DISCONNECTED");
                break;
            case EthernetStateTracker.EVENT_INTERFACE_CONFIGURATION_FAILED:
                Log.d(TAG, "EVENT_INTERFACE_CONFIGURATION_FAILED");
                break;
            default:
                break;
        }
    }

    private void handleNetworkStateChanged(NetworkInfo networkInfo) {
        Log.d(TAG, "network state changed, " + networkInfo.toString());
    }

    private void configEthernetV4(boolean auto) {
        EthernetDevInfo devInfo = new EthernetDevInfo();
        // config the eth0/eth1
        devInfo.setIfName("eth0");
        //modify by ken.bi [2013/5/7]
       // devInfo.setIfName(mEthernetHolder.getDeviceName());

        Log.d(TAG, "device name is "+mEthernetHolder.getDeviceName());
        // auto config
        if (auto) {
            devInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP);
            devInfo.setIpAddress(null);
            devInfo.setNetMask(null);
            devInfo.setRouteAddr(null);
            devInfo.setDnsAddr(null);
            devInfo.setDns2Addr(null);
        } else {
        	devInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL);
            
            String ip = mEthernetHolder.getEthernetV4Address();
            if (Tools.matchIP(ip)) {
                devInfo.setIpAddress(ip);
            } else {
                devInfo.setIpAddress(null);
            }

            String netmask = mEthernetHolder.getEthernetV4Netmask();
            if (Tools.matchIP(netmask)) {
                devInfo.setNetMask(netmask);
            } else {
                devInfo.setNetMask(null);
            }

            String gateway = mEthernetHolder.getEthernetV4Gateway();
            if (Tools.matchIP(gateway)) {
                devInfo.setRouteAddr(gateway);
            } else {
                devInfo.setRouteAddr(null);
            }

            String dns1 = mEthernetHolder.getEthernetV4Dns1();
            if (Tools.matchIP(dns1)) {
                devInfo.setDnsAddr(dns1);
            } else {
                devInfo.setDnsAddr(null);
            }

            String dns2 = mEthernetHolder.getEthernetV4Dns2();
            if (Tools.matchIP(dns2)) {
                devInfo.setDns2Addr(dns2);
            } else {
                devInfo.setDns2Addr(null);
            }
        }

        // chech proxy
//        if (hasProxy()) {
//            Tools.logd(TAG, "hasProxy");
//            ProxyInfo proxyInfo = getProxyInfo();
//            devInfo.setProxyHost(proxyInfo.mHost);
//            devInfo.setProxyPort(String.valueOf(proxyInfo.mPort));
//            devInfo.setProxyOn(true);
//        } else {
//        	devInfo.setProxyOn(false);
//            devInfo.setProxyHost(null);
//            devInfo.setProxyPort(null);
            
//        }
        // update devInfo	
        mEthernetManager.updateDevInfo(devInfo);
        
    }

    private void configEthernetV6(boolean auto) {
        // FIXME
    }

    private void showEthernetInfoV6(String ifName) {
        InterfaceAddress address = getIPv6Address(ifName);
        if (address == null) {
            mEthernetHolder.refreshNetworkInfoV6("", "", "", "", "");
        } else {
            String ip = formatIPv6Address(address.getAddress().getHostAddress());
            mEthernetHolder.refreshNetworkInfoV6(ip, String.valueOf(address.getNetworkPrefixLength()), "", "", "");
        }
    }

    private InterfaceAddress getIPv6Address(final String interfacename) {
        try {
            NetworkInterface info = NetworkInterface.getByName(interfacename);
            for (InterfaceAddress address : info.getInterfaceAddresses()) {
                // search ipv6 address
                if (address.getBroadcast() == null) {
                    Log.d(TAG, "address, " + address.getAddress().getHostAddress()
                            + " preFixLength, " + address.getNetworkPrefixLength());
                    return address;
                }
            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "NetworkInterface.getByName");

            return null;
        }
    }

    private String formatIPv6Address(final String ip) {
        if (ip == null) {
            return "";
        }

        if (ip.contains("%")) {
            return ip.substring(0, ip.indexOf("%"));
        }

        return ip;
    }

    private void updateEthernetProxy(ProxyInfo proxy) {
        EthernetDevInfo devInfo = mEthernetManager.getSavedConfig();
        if (devInfo != null) {
            EthernetDevInfo newInfo = new EthernetDevInfo();
            newInfo.setIfName("eth0");
            String connectMode = devInfo.getConnectMode();
            Log.d(TAG, "connectMode, " + connectMode);
            if (EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL.equals(connectMode)) {
                newInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL);
                newInfo.setIpAddress(devInfo.getIpAddress());
                newInfo.setNetMask(devInfo.getNetMask());
                newInfo.setRouteAddr(devInfo.getRouteAddr());
                newInfo.setDnsAddr(devInfo.getDnsAddr());
                newInfo.setDns2Addr(devInfo.getDns2Addr());

            } else if (EthernetDevInfo.ETHERNET_CONN_MODE_DHCP.equals(connectMode)) {
                newInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP);
                newInfo.setIpAddress(null);
                newInfo.setNetMask(null);
                newInfo.setRouteAddr(null);
                newInfo.setDnsAddr(null);
                newInfo.setDns2Addr(null);
            }
            
//            // update proxy info
//            if (proxy == null) {
//                newInfo.setProxyHost(null);
//                newInfo.setProxyPort(null);
//                newInfo.setProxyOn(false);
//            } else {
//                newInfo.setProxyHost(proxy.mHost);
//                newInfo.setProxyPort(String.valueOf(proxy.mPort));
//                newInfo.setProxyOn(true);
//            }

            // update devInfo
            mEthernetManager.updateDevInfo(newInfo);
        }
    }

    private void showEthernetInfo() {
        // ethernet is enabled
        if (isEthernetEnabled()) {
            mEthernetToggle.setChecked(true);
        } else {
            mEthernetToggle.setChecked(false);
            return;
        }

        boolean autoIp = false;
        // ethernet had configured but network may not connect
        if (mEthernetManager.isConfigured()) {
            // network information:
            EthernetDevInfo devInfo = mEthernetManager.getSavedConfig();
            String ifName = devInfo.getIfName();
            Log.d(TAG, "ifName, " + ifName);

            String ip = devInfo.getIpAddress();
            String netmask = devInfo.getNetMask();
            String defaultWay = devInfo.getRouteAddr();
            String firstDNS = devInfo.getDnsAddr();
            String secDNS = devInfo.getDns2Addr();

            // set network info:
            InputIPAddress.isForwardRightWithTextChange = false;
            mEthernetHolder.refreshNetworkInfo(ip, netmask, defaultWay, firstDNS, secDNS);
            InputIPAddress.isForwardRightWithTextChange = true;

            if (EthernetDevInfo.ETHERNET_CONN_MODE_DHCP.equals(devInfo.getConnectMode())) {
                autoIp = true;
            }
        }

        // auto ip.
        if (autoIp) {
            Log.d(TAG, "autoIp, " + autoIp);
            mEthernetHolder.setAutoIpOpend(true);
        } else {
            mEthernetHolder.setAutoIpOpend(false);
        }
        mEthernetHolder.setV4EditTextWritable(!autoIp);

        // FIXME ipv4 or ipv6.
    }

    private void showToast(int id) {
        if (id <= 0) {
            return;
        }

        Toast.makeText(mNetworkSettingsActivity, id, Toast.LENGTH_SHORT).show();
    }

    private void setListener() {
        mEthernetToggle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;
                    // check wifi state
                    /*
                     * Modified by gerard.jiang for "0390976" in 2013/05/06.
                     * Add a condition to the checkbox so it will save the state.
                     */
                    if (getWifiManager().isWifiEnabled() && !mEthernetManager.isNetworkConnected()) {
                    /***** Ended by gerard.jiang 2013/05/06 *****/
                    	// modify by ken.bi
                        //showToast(R.string.open_ethernet_hint);
                        //mEthernetToggle.setChecked(!checkBox.isChecked());
                    	mEthernetToggle.setChecked(checkBox.isChecked());
                        return;

                    } else {
                        boolean enabled = isEthernetEnabled();
                        if (enabled) {
                            // set ethernet enable or disable
                            mEthernetManager.setEnabled(false);
                        } else {
                            // set ethernet enable or disable
                            mEthernetManager.setEnabled(true);
                        }
                        showEthernetInfo();
                    }
                }
            }
        });

        mAutoIpToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // may be unuseful
            }
        });

        mIPv6Toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // may be unuseful
                // FIXME show ipv6 info
                if (isChecked) {
                    // hard code
                    showEthernetInfoV6("eth0");
                }
            }
        });

        mEthernetHolder.getSaveButton().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                // update ethernet v6 config
                if (mIPv6Toggle.isChecked()) {
                    configEthernetV6(mAutoIpToggle.isChecked());
                } else {
                    configEthernetV4(mAutoIpToggle.isChecked());
                }
            }
        });

        mEthernetHolder.getCancelButton().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                // refresh ip, dns, etc
                showEthernetInfo();
            }
        });
    }

    private BroadcastReceiver mEthStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action, " + intent.getAction());
            if (EthernetManager.ETHERNET_STATE_CHANGED_ACTION.equals(action)) {
                handleEthStateChanged(intent.getIntExtra(EthernetManager.EXTRA_ETHERNET_STATE,
                        EthernetManager.ETHERNET_STATE_UNKNOWN), intent.getIntExtra(
                        EthernetManager.EXTRA_PREVIOUS_ETHERNET_STATE,
                        EthernetManager.ETHERNET_STATE_UNKNOWN));

            } else if (EthernetManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                handleNetworkStateChanged((NetworkInfo) intent
                        .getParcelableExtra(EthernetManager.EXTRA_NETWORK_INFO));
            }
        }
    };

}
