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

import android.content.res.Resources;
import android.net.wifi.p2p.WifiP2pDevice;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tufer.com.menutest.R;

public class WifiP2pSettingsHolder {

    private NetworkSettingsActivity mActivity;

    // root layout
    private LinearLayout mWifiP2pSettingsRootLayout;

    // toggle
    private RelativeLayout mWifiP2pToggleLayout;
    private CheckBox mWifiP2pToggleCheckBox;

    // device info
    private TextView mWifiP2pDeviceInfo;
    // p2p device notice
    private TextView mDeviceNoticeTextView;

    // discover device
    private Button mWifiP2pDiscoverButton;
    private ListView mWifiP2pDeviceListView;

    public WifiP2pSettingsHolder(NetworkSettingsActivity networkSettingActivity) {
        this.mActivity = networkSettingActivity;
        findViews();
    }

    private void findViews() {
        mWifiP2pSettingsRootLayout = (LinearLayout) mActivity.findViewById(R.id.wifi_direct_layout);
        // toggle layout
        mWifiP2pToggleLayout = (RelativeLayout) mActivity.findViewById(R.id.wifi_direct_switch);
        mWifiP2pToggleCheckBox = (CheckBox) mActivity.findViewById(R.id.wifi_diect_checkbox);
        mWifiP2pToggleCheckBox.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mWifiP2pToggleLayout.setBackgroundResource(R.drawable.desktop_button);
                } else {
                    mWifiP2pToggleLayout.setBackgroundResource(R.drawable.one_px);
                }
            }
        });

        // wifip2p info
        mWifiP2pDeviceInfo = (TextView) mActivity.findViewById(R.id.wifi_diect_deviceinfo);
        mDeviceNoticeTextView = (TextView) mActivity.findViewById(R.id.wifi_direct_notice);

        // discovery
        mWifiP2pDiscoverButton = (Button) mActivity.findViewById(R.id.device_discover_btn);
//        mWifiP2pDiscoverButton.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    mWifiP2pDiscoverButton.setBackgroundResource(R.drawable.edit_focus);
//                } else {
//                    mWifiP2pDiscoverButton.setBackgroundResource(R.drawable.edit_normal);
//                }
//            }
//        });
        mWifiP2pDeviceListView = (ListView) mActivity.findViewById(R.id.direct_device_list);
        mWifiP2pDeviceListView.setDividerHeight(0);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            mWifiP2pSettingsRootLayout.setVisibility(View.VISIBLE);
        } else {
            mWifiP2pSettingsRootLayout.setVisibility(View.GONE);
        }
    }

    public void clearFocus(int position) {
        switch (position) {
            case Constants.SETTING_ITEM_0:
                mWifiP2pToggleCheckBox.clearFocus();
                break;
            case Constants.SETTING_ITEM_1:
                mWifiP2pDiscoverButton.clearFocus();
                break;
            case Constants.SETTING_ITEM_2:
                mWifiP2pDeviceListView.clearFocus();
                break;
            default:
                break;
        }
    }

    public void requestFocus(int position) {
        switch (position) {
            case Constants.SETTING_ITEM_0:
                mWifiP2pToggleCheckBox.requestFocus();
                break;
            case Constants.SETTING_ITEM_1:
                mWifiP2pDiscoverButton.requestFocus();
                break;
            case Constants.SETTING_ITEM_2:
                mWifiP2pDeviceListView.requestFocus();
                break;
            default:
                break;
        }
    }

    public void refreshDeviceInfo(WifiP2pDevice device) {
        StringBuffer info = new StringBuffer();
        if (device == null) {
            info.append("");
        } else {
            Resources resource = mActivity.getResources();
            // show device name
            if (TextUtils.isEmpty(device.deviceName)) {
                info.append(resource.getString(R.string.device_address));
                info.append(device.deviceAddress);
                info.append("\n");
            } else {
                info.append(resource.getString(R.string.device_name));
                info.append(device.deviceName);
                info.append("\n");
            }

            String[] statusArray = mActivity.getResources().getStringArray(R.array.wifi_p2p_status);
            info.append(resource.getString(R.string.device_status));
            if (device.status < statusArray.length) {
                info.append(statusArray[device.status]);
                info.append("\n");
            }
        }
        // show device info
        mWifiP2pDeviceInfo.setText(info.toString());
    }

    public void refreshNotice(String notice) {
        mDeviceNoticeTextView.setText(notice);
    }

    public CheckBox getWifiP2pCheckBox() {
        return mWifiP2pToggleCheckBox;
    }

    public Button getWifiSearchButton() {
        return mWifiP2pDiscoverButton;
    }

    public ListView getWifiP2pDeviceListView() {
        return mWifiP2pDeviceListView;
    }

}
