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
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tufer.com.menutest.R;
import tufer.com.menutest.Util.Tools;

/**
 * Wi-Fi Ap Settings Holder.
 */
public class WifiApSettingsHolder {

    private static final String TAG = "MSettings.WifiApSettingsHolder";

    private static final int[] SECURE_TYPE = {
            R.string.wifi_security_wpa, R.string.wifi_security_wpa2, R.string.wifi_security_open
    };

    private NetworkSettingsActivity mActivity;

    private LinearLayout mWifiApSettingsRootLayout;

    // wifiap toggle layout
    private RelativeLayout mWifiApToggleLayout;
    private CheckBox mWifiApToggleCheckBox;
    private TextView mWifiApInfoTextView;

    // wifiap config toggle
    private Button mConfigToggleButton;

    // wifiap config items
    private LinearLayout mConfigRootLayout;
    private EditText mSsidEditText;
    private RelativeLayout mSecureLayout;
    private ImageView mLeftArrowImageView;
    private ImageView mRightArrowImageView;
    private Button mSecureButton;
    private LinearLayout mPasswdConfigLayout;
    private EditText mPasswdEditText;
    private RelativeLayout mShowPasswdLayout;
    private CheckBox mShowPasswdCheckBox;
    private Button mSaveConfigButton;

    public WifiApSettingsHolder(NetworkSettingsActivity networkSettingActivity) {
        this.mActivity = networkSettingActivity;
        // init all controller
        findViews();
    }

    private void findViews() {
        mWifiApSettingsRootLayout = (LinearLayout) mActivity.findViewById(R.id.wifi_hotspot_layout);
        mWifiApToggleLayout = (RelativeLayout) mActivity.findViewById(R.id.wifi_hotspot_switch);
        mWifiApToggleCheckBox = (CheckBox) mActivity.findViewById(R.id.wifi_hotspot_checkbox);
        mWifiApToggleCheckBox.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mWifiApToggleLayout.setBackgroundResource(R.drawable.set_button);
                } else {
                    mWifiApToggleLayout.setBackgroundResource(R.drawable.one_px);
                }
            }
        });
        mWifiApInfoTextView = (TextView) mActivity.findViewById(R.id.wifi_hotspot_info);

        // config toggle
        mConfigToggleButton = (Button) mActivity.findViewById(R.id.wifi_hotspot_config);
        mConfigToggleButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int visibility = mConfigRootLayout.getVisibility();
                if (View.VISIBLE == visibility) {
                    mConfigRootLayout.setVisibility(View.GONE);
                } else if (View.GONE == visibility) {
                    mConfigRootLayout.setVisibility(View.VISIBLE);
                }
            }
        });
//        mConfigToggleButton.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    mConfigToggleButton.setBackgroundResource(R.drawable.edit_focus);
//                } else {
//                    mConfigToggleButton.setBackgroundResource(R.drawable.edit_normal);
//                }
//            }
//        });

        // config items
        mConfigRootLayout = (LinearLayout) mActivity.findViewById(R.id.wifi_hotspot_config_layout);
        mSsidEditText = (EditText) mActivity.findViewById(R.id.wifi_hotspot_ssid);
        // secure
        mSecureLayout = (RelativeLayout) mActivity.findViewById(R.id.wifiap_secure_root);
        mSecureButton = (Button) mActivity.findViewById(R.id.secure_type);
        mSecureButton.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSecureLayout.setBackgroundResource(R.drawable.desktop_button);
                    mLeftArrowImageView.setVisibility(View.VISIBLE);
                    mRightArrowImageView.setVisibility(View.VISIBLE);
                } else {
                    mSecureLayout.setBackgroundResource(R.drawable.one_px);
                    mLeftArrowImageView.setVisibility(View.GONE);
                    mRightArrowImageView.setVisibility(View.GONE);
                }
            }
        });
        mLeftArrowImageView = (ImageView) mActivity.findViewById(R.id.secure_left_arrowhead);
        mRightArrowImageView = (ImageView) mActivity.findViewById(R.id.secure_right_arrowhead);
        // passwd
        mPasswdConfigLayout = (LinearLayout) mActivity.findViewById(R.id.wifiap_passwd_config);
        mPasswdEditText = (EditText) mActivity.findViewById(R.id.wifi_hotspot_pwd);
        // show passwd
        mShowPasswdLayout = (RelativeLayout) mActivity.findViewById(R.id.wifiap_show_password_rl);
        mShowPasswdCheckBox = (CheckBox) mActivity.findViewById(R.id.hotspot_show_password);
        mShowPasswdCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPasswdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    mPasswdEditText.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        mShowPasswdCheckBox.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mShowPasswdLayout.setBackgroundResource(R.drawable.desktop_button);
                } else {
                    mShowPasswdLayout.setBackgroundResource(R.drawable.one_px);
                }
            }
        });

        // save config
        mSaveConfigButton = (Button) mActivity.findViewById(R.id.hotspot_save_btn);

//        mSaveConfigButton.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    mSaveConfigButton.setBackgroundResource(R.drawable.edit_focus);
//                } else {
//                    mSaveConfigButton.setBackgroundResource(R.drawable.edit_normal);
//                }
//            }
//        });
    }

    protected void setVisible(boolean visible) {
        if (visible) {
            mWifiApSettingsRootLayout.setVisibility(View.VISIBLE);
        } else {
            mWifiApSettingsRootLayout.setVisibility(View.GONE);
        }
    }

    protected void clearFocus(int position) {
        Tools.logd(TAG, "position, " + position);
        switch (position) {
            case Constants.SETTING_ITEM_0:
                mWifiApToggleCheckBox.clearFocus();
                break;
            case Constants.SETTING_ITEM_1:
                mConfigToggleButton.clearFocus();
                break;
            case Constants.SETTING_ITEM_2:
                if (isConfigLayoutVisible()) {
                    mSsidEditText.clearFocus();
                } else {
                    mSaveConfigButton.clearFocus();
                }
                break;
            case Constants.SETTING_ITEM_3:
                if (isConfigLayoutVisible()) {
                    mSecureButton.clearFocus();
                }
                break;
            case Constants.SETTING_ITEM_4:
                if (isConfigLayoutVisible()) {
                    mPasswdEditText.clearFocus();
                }
                break;
            case Constants.SETTING_ITEM_5:
                if (isConfigLayoutVisible()) {
                    mShowPasswdCheckBox.clearFocus();
                }
                break;
            case Constants.SETTING_ITEM_6:
                if (isConfigLayoutVisible()) {
                    mSaveConfigButton.clearFocus();
                }
                break;
            default:
                break;
        }
    }

    protected void requestFocus(int position) {
        Tools.logd(TAG, "position, " + position);
        switch (position) {
            case Constants.SETTING_ITEM_0:
                mWifiApToggleCheckBox.requestFocus();
                break;
            case Constants.SETTING_ITEM_1:
                mConfigToggleButton.requestFocus();
                break;
            case Constants.SETTING_ITEM_2:
                if (isConfigLayoutVisible()) {
                    mSsidEditText.requestFocus();
                } else {
                    mSaveConfigButton.requestFocus();
                }
                break;
            case Constants.SETTING_ITEM_3:
                if (isConfigLayoutVisible()) {
                    mSecureButton.requestFocus();
                }
                break;
            case Constants.SETTING_ITEM_4:
                if (isConfigLayoutVisible()) {
                    if (mPasswdConfigLayout.getVisibility() == View.VISIBLE) {
                        mPasswdEditText.requestFocus();
                    } else {
                        mSaveConfigButton.requestFocus();
                    }
                }
                break;
            case Constants.SETTING_ITEM_5:
                if (isConfigLayoutVisible()) {
                    if (mPasswdConfigLayout.getVisibility() == View.VISIBLE) {
                        mShowPasswdCheckBox.requestFocus();
                    } else {
                        mSaveConfigButton.requestFocus();
                    }
                }
                break;
            case Constants.SETTING_ITEM_6:
                if (isConfigLayoutVisible()) {
                    mSaveConfigButton.requestFocus();
                }
                break;
            default:
                break;
        }
    }

    protected boolean isConfigLayoutVisible() {
        return View.VISIBLE == mConfigRootLayout.getVisibility() ? true : false;
    }

    protected void refreshSecureType(int type) {
        switch (type) {
            case WifiApSettings.SECURE_TYPE_WPA:
                mSecureButton.setText(SECURE_TYPE[0]);
                break;
            case WifiApSettings.SECURE_TYPE_WPA2:
                mSecureButton.setText(SECURE_TYPE[1]);
                break;
            case WifiApSettings.SECURE_TYPE_OPEN:
                mSecureButton.setText(SECURE_TYPE[2]);
                break;
            default:
                break;
        }
    }

    protected void setPasswdLayoutVisiable(boolean visibile) {
        if (visibile) {
            mPasswdConfigLayout.setVisibility(View.VISIBLE);
        } else {
            mPasswdConfigLayout.setVisibility(View.GONE);
        }
    }

    protected void refreshWifiApInfo(WifiConfiguration config) {
        if (config == null) {
            return;
//            mWifiApInfoTextView.setText("");
//            mSsidEditText.setText("");
//            mSecureButton.setText("");
//            mPasswdEditText.setText("");
        } else {
            StringBuffer info = new StringBuffer();
            Resources resource = mActivity.getResources();
            info.append(resource.getString(R.string.wifi_net_ssid));
            info.append(" : ");
            info.append(config.SSID);
            info.append("\n");
            info.append(resource.getString(R.string.wifi_hotspot_secure));
            info.append(" : ");
            if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
                info.append(resource.getString(R.string.wifi_security_wpa));
                // refresh secure
                mSecureButton.setText(R.string.wifi_security_wpa);
                // refresh passwd
                mPasswdEditText.setText(config.preSharedKey);
                setPasswdLayoutVisiable(true);

            } else if (config.allowedKeyManagement.get(KeyMgmt.WPA2_PSK)) {
                info.append(resource.getString(R.string.wifi_security_wpa2));
                mSecureButton.setText(R.string.wifi_security_wpa2);
                mPasswdEditText.setText(config.preSharedKey);
                setPasswdLayoutVisiable(true);

            } else {
                info.append(resource.getString(R.string.wifi_security_open));
                mSecureButton.setText(R.string.wifi_security_open);
                mPasswdEditText.setText("");
                // security is open, so hide passwd control
                setPasswdLayoutVisiable(false);
            }
            info.append("\n");
            mWifiApInfoTextView.setText(info.toString());

            // refresh ssid
            mSsidEditText.setText(config.SSID);
        }
    }

    protected String getSSID() {
        return mSsidEditText.getText().toString().trim();
    }

    protected String getPassword() {
        return mPasswdEditText.getText().toString().trim();
    }

    protected CheckBox getWifiApToggleCheckBox() {
        return mWifiApToggleCheckBox;
    }

    protected Button getSaveConfigButton() {
        return mSaveConfigButton;
    }

}
