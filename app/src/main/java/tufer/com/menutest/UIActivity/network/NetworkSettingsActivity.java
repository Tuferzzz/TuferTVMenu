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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;
import tufer.com.menutest.UIActivity.bluetooth.BluetoothActivity;
import tufer.com.menutest.Util.Tools;


public class NetworkSettingsActivity extends Activity {

    private static final String TAG = "NetworkSettingsActivity";
    private static final int ethernetnumber = 0;
    private static final int WifiSettingsnumber = 1;
    private static final int WifiApSettingsnumber = 2;
    private static final int NetworkStatusSettingsnumber = 3;
    private NetworkSettingsViewHolder mNetworkSettingsHolder;
    private NetworkStatusSettings mNetworkStatusSettings;
    private EthernetSettings mEthernetSettings;
    private WifiSettings mWifiSettings;

    private WifiApSettings mWifiApSettings;

    //private MacAddressSettings mMacAddressSettings;
    //private ConnectivityListener mConnectivityListener;

    //private NetworkConfiguration mConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_setting);
        Intent intent = getIntent();
        int isnumberint = intent.getIntExtra("network_type_number",0);
        mNetworkSettingsHolder = new NetworkSettingsViewHolder(this);
//        mConfiguration = NetworkConfigurationFactory.createNetworkConfiguration(this,
//                NetworkConfigurationFactory.TYPE_ETHERNET);
        //((EthernetConfig) mConfiguration).load();
        setIntentinit(isnumberint);

    }
//    @Override
//    public void onConnectivityChange(Intent intent) {
//        // TODO Auto-generated method stub
//
//    }


    public void setIntentinit(int isnumberint) {
        switch (isnumberint){
            case ethernetnumber :
                mEthernetSettings = new EthernetSettings(this);
                mNetworkSettingsHolder.refreshSettingTitle(R.string.ethernet_settings_title);
                mEthernetSettings.setVisible(true);
                break;
            case WifiSettingsnumber :
                mWifiSettings = new WifiSettings(this);
                mNetworkSettingsHolder.refreshSettingTitle(R.string.wifi_settings_title);
                mWifiSettings.setVisible(true);
                break;
            case WifiApSettingsnumber :
                mWifiApSettings = new WifiApSettings(this);
                mNetworkSettingsHolder.refreshSettingTitle(R.string.wifiap_settings_title);
                mWifiApSettings.setVisible(true);
                break;
            case NetworkStatusSettingsnumber :
                mNetworkStatusSettings = new NetworkStatusSettings(this);
                mNetworkStatusSettings.setVisible(true);
                mNetworkSettingsHolder.refreshSettingTitle(R.string.network_status_title);
                break;
            default:
                mEthernetSettings = new EthernetSettings(this);
                mEthernetSettings.setVisible(true);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mNetworkStatusSettings != null) {
            mNetworkStatusSettings.onExit();
        }
        if(mWifiSettings != null) {
            mWifiSettings.onExit();
        }
        if(mWifiApSettings != null){
            mWifiApSettings.onExit();
        }
    }
    public WifiSettings getWifiSettings() {
        if(mWifiSettings == null){
            mWifiSettings = new WifiSettings(this);
        }
        return mWifiSettings;
    }


    @Override
    protected void onStop() {
        MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_NETWORK);
        super.onStop();
    }
}
