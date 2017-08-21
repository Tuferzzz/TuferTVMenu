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
import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.network.ProxySettings.ProxyInfo;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;



/**
 * Proxy Settings Holder.
 */
public class ProxySettingsHolder {

    private static final String TAG = "MSettings.ProxySettingsHolder";

    private NetworkSettingsActivity mActivity;

    // root layout
    private LinearLayout mProxySettingsRootLayout;
    // toggle layout
    private RelativeLayout mProxyToggleLayout;
    private CheckBox mProxyToggleCheckBox;
    // proxy config infos
    private LinearLayout mConfigRootLayout;
    private EditText mProxyHostEditText;
    private EditText mProxyPortEditText;
    // authenticate toggle
    private RelativeLayout mAuthToggleLayout;
    private CheckBox mAuthCheckBox;
    // authenticate root
    private LinearLayout mAuthInfoLayout;
    private EditText mUserEditText;
    private EditText mPasswdEditText;
    // save or cancel layout
    private Button mSaveButton;
    private Button mCancelButton;

    public ProxySettingsHolder(NetworkSettingsActivity networkSettingActivity) {
        this.mActivity = networkSettingActivity;
        findViews();
    }

    private void findViews() {
        mProxySettingsRootLayout = (LinearLayout) mActivity.findViewById(R.id.proxy_setting_layout);
        mProxyToggleLayout = (RelativeLayout) mActivity.findViewById(R.id.proxy_switch_layout);
        mProxyToggleCheckBox = (CheckBox) mActivity.findViewById(R.id.proxy_switch);
        mProxyToggleCheckBox.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mProxyToggleLayout.setBackgroundResource(R.drawable.set_button);
                } else {
                    mProxyToggleLayout.setBackgroundResource(R.drawable.one_px);
                }
            }
        });

        // proxy infos
        mConfigRootLayout = (LinearLayout) mActivity.findViewById(R.id.proxy_info_config_layout);
        mProxyHostEditText = (EditText) mActivity.findViewById(R.id.proxy_address_edit);
        mProxyPortEditText = (EditText) mActivity.findViewById(R.id.proxy_port_edit);
        // authenticate toggle
        mAuthToggleLayout = (RelativeLayout) mActivity.findViewById(R.id.proxy_verify_layout);
        mAuthCheckBox = (CheckBox) mActivity.findViewById(R.id.proxy_verify);
        mAuthCheckBox.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAuthToggleLayout.setBackgroundResource(R.drawable.set_button);
                } else {
                    mAuthToggleLayout.setBackgroundResource(R.drawable.one_px);
                }
            }
        });
        mAuthCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mUserEditText.setEnabled(true);
                    mPasswdEditText.setEnabled(true);
                    mAuthInfoLayout.setVisibility(View.VISIBLE);
                } else {
                    mUserEditText.setEnabled(false);
                    mPasswdEditText.setEnabled(false);
                    mAuthInfoLayout.setVisibility(View.GONE);
                }
            }
        });
        //
        mAuthInfoLayout = (LinearLayout) mActivity.findViewById(R.id.proxy_auth_layout);
        mUserEditText = (EditText) mActivity.findViewById(R.id.proxy_username);
        mPasswdEditText = (EditText) mActivity.findViewById(R.id.proxy_password);
        // save config
        mSaveButton = (Button) mActivity.findViewById(R.id.proxy_save);
//        mSaveButton.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    mSaveButton.setBackgroundResource(R.drawable.edit_focus);
//                } else {
//                    mSaveButton.setBackgroundResource(R.drawable.edit_normal);
//                }
//            }
//        });
        mCancelButton = (Button) mActivity.findViewById(R.id.proxy_cancel);
//        mCancelButton.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    mCancelButton.setBackgroundResource(R.drawable.edit_focus);
//                } else {
//                    mCancelButton.setBackgroundResource(R.drawable.edit_normal);
//                }
//            }
//        });
    }

    public void setVisible(boolean visible) {
        if (visible) {
            mProxySettingsRootLayout.setVisibility(View.VISIBLE);
        } else {
            mProxySettingsRootLayout.setVisibility(View.GONE);
        }
    }

    public void setProxyConfigVisible(boolean visible) {
        if (visible) {
            mConfigRootLayout.setVisibility(View.VISIBLE);
        } else {
            mConfigRootLayout.setVisibility(View.GONE);
        }
    }

    public void clearFocus(int position) {
        switch (position) {
            case Constants.SETTING_ITEM_0:
                mProxyToggleCheckBox.clearFocus();
                break;
            case Constants.SETTING_ITEM_1:
                if (mProxyToggleCheckBox.isChecked()) {
                    mProxyHostEditText.clearFocus();
                }
                break;
            case Constants.SETTING_ITEM_2:
                if (mProxyToggleCheckBox.isChecked()) {
                    mProxyPortEditText.clearFocus();
                }
                break;
            case Constants.SETTING_ITEM_3:
                if (mProxyToggleCheckBox.isChecked()) {
                    mAuthCheckBox.clearFocus();
                }
                break;
            case Constants.SETTING_ITEM_4:
                if (mProxyToggleCheckBox.isChecked()) {
                    if (mAuthCheckBox.isChecked()) {
                        mUserEditText.clearFocus();
                    } else {
                        mSaveButton.clearFocus();
                    }
                }
                break;
            case Constants.SETTING_ITEM_5:
                if (mProxyToggleCheckBox.isChecked()) {
                    if (mAuthCheckBox.isChecked()) {
                        mPasswdEditText.clearFocus();
                    } else {
                        mCancelButton.clearFocus();
                    }
                }
                break;
            case Constants.SETTING_ITEM_6:
                if (mProxyToggleCheckBox.isChecked() && mAuthCheckBox.isChecked()) {
                    mSaveButton.clearFocus();
                }
                break;
            case Constants.SETTING_ITEM_7:
                if (mProxyToggleCheckBox.isChecked() && mAuthCheckBox.isChecked()) {
                    mCancelButton.clearFocus();
                }
                break;
            default:
                break;
        }
    }

    public void requestFocus(int position) {
        switch (position) {
            case Constants.SETTING_ITEM_0:
                mProxyToggleCheckBox.requestFocus();
                break;
            case Constants.SETTING_ITEM_1:
                if (mProxyToggleCheckBox.isChecked()) {
                    mProxyHostEditText.requestFocus();
                }
                break;
            case Constants.SETTING_ITEM_2:
                if (mProxyToggleCheckBox.isChecked()) {
                    mProxyPortEditText.requestFocus();
                }
                break;
            case Constants.SETTING_ITEM_3:
                if (mProxyToggleCheckBox.isChecked()) {
                    mAuthCheckBox.requestFocus();
                }
                break;
            case Constants.SETTING_ITEM_4:
                if (mProxyToggleCheckBox.isChecked() && mAuthCheckBox.isChecked()) {
                    mUserEditText.requestFocus();
                } else {
                    mSaveButton.requestFocus();
                }
                break;
            case Constants.SETTING_ITEM_5:
                if (mProxyToggleCheckBox.isChecked() && mAuthCheckBox.isChecked()) {
                    mPasswdEditText.requestFocus();
                } else {
                    mCancelButton.requestFocus();
                }
                break;
            case Constants.SETTING_ITEM_6:
                if (mProxyToggleCheckBox.isChecked() && mAuthCheckBox.isChecked()) {
                    mSaveButton.requestFocus();
                }
                break;
            case Constants.SETTING_ITEM_7:
                if (mProxyToggleCheckBox.isChecked() && mAuthCheckBox.isChecked()) {
                    mCancelButton.requestFocus();
                }
                break;
            default:
                break;
        }
    }

    public boolean isProxyToggleOpen() {
        return mProxyToggleCheckBox.isChecked();
    }

    public boolean isAuthToggleOpen() {
        return mAuthCheckBox.isChecked();
    }

    public CheckBox getProxyToggleCheckBox() {
        return mProxyToggleCheckBox;
    }

    public CheckBox getAuthCheckBox() {
        return mAuthCheckBox;
    }

    public Button getSaveButton() {
        return mSaveButton;
    }

    public Button getCancelButton() {
        return mCancelButton;
    }

    public String getHost() {
        return mProxyHostEditText.getText().toString().trim();
    }

    public String getPort() {
        return mProxyPortEditText.getText().toString().trim();
    }

    public void refreshProxyInfo(ProxyInfo proxy) {
        if (proxy == null) {
            return;
        }
        Log.d(TAG, "proxy, " + proxy.toString());

        // refresh hostname
        if (TextUtils.isEmpty(proxy.mHost)) {
            mProxyHostEditText.setText("");
            mConfigRootLayout.setVisibility(View.GONE);
            mProxyToggleCheckBox.setChecked(false);
        } else {
            mProxyHostEditText.setText(proxy.mHost);
            // open proxy layout
            mProxyToggleCheckBox.setChecked(true);
            mConfigRootLayout.setVisibility(View.VISIBLE);
        }

        // refresh port
        if (proxy.mPort <= 0) {
            mProxyPortEditText.setText("");
        } else {
            mProxyPortEditText.setText(String.valueOf(proxy.mPort));
        }

        // refresh username
        if (TextUtils.isEmpty(proxy.mUsr)) {
            mUserEditText.setText("");
            mAuthCheckBox.setChecked(false);
            mAuthInfoLayout.setVisibility(View.GONE);
        } else {
            mUserEditText.setText(proxy.mUsr);
            mAuthCheckBox.setChecked(true);
            mAuthInfoLayout.setVisibility(View.VISIBLE);
        }

        // refresh passwd
        if (TextUtils.isEmpty(proxy.mPwd)) {
            mPasswdEditText.setText("");
        } else {
            mPasswdEditText.setText(proxy.mPwd);
        }
    }

}
