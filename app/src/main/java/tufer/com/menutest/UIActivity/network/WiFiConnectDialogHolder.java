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

import android.text.InputType;
import android.util.Log;
import android.view.View;
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

public class WiFiConnectDialogHolder {

    private static final String TAG = "MSettings.WiFiConnectDialogHolder";

    private WiFiConnectDialog mDialog;

    // wifi title
    private TextView mConnectTitleTextView;

    // add other ssid
    private RelativeLayout mAddSsidLayout;
    private EditText mAddSsidEditText;

    // secure type
    private RelativeLayout mSecureLayout;
    private Button mSecureTypeButton;
    private ImageView mSecureLeftImg;
    private ImageView mSecureRightImg;

    // password
    private RelativeLayout mPasswdLayout;
    private EditText mPasswdEditText;
    private RelativeLayout mShowPasswdLayout;
    private CheckBox mShowPasswdCheckBox;

    // auto ip layout
    private RelativeLayout mAutoIpLayout;
    private CheckBox mAutoIpCheckBox;
    // ip/dns/gateway/netmask configs layout
    private LinearLayout mIpConfigLayout;
    private EditText mIpEditText;
    private EditText mNetmaskEditText;
    private EditText mGatewayEditText;
    private EditText mDns1EditText;
    private EditText mDns2EditText;

    // save and cancel button
    private Button mSaveButton;
    private Button mCancelButton;
    private Button mForgetButton;

    private int[] SECURE_TYPE = {
            R.string.none, R.string.secure_wep, R.string.secure_wpa
    };

    public WiFiConnectDialogHolder(WiFiConnectDialog dialog) {
        super();
        this.mDialog = dialog;
        findViews();
        setListener();
    }

    public CheckBox getAutoIpCheckBox() {
        return mAutoIpCheckBox;
    }

    public Button getSaveButton() {
        return mSaveButton;
    }

    public Button getCancelButton() {
        return mCancelButton;
    }

    public Button getForgetButton() {
        return mForgetButton;
    }

    public String getSsid() {
        return mAddSsidEditText.getText().toString().trim();
    }

    public String getPasswd() {
        return mPasswdEditText.getText().toString().trim();
    }

    public void refreshPasswd(String passwd) {
        mPasswdEditText.setText(passwd);
    }

    public void refreshPasswdHint() {
        mPasswdEditText.setText("");
        mPasswdEditText.setHint(R.string.modify);
    }

    public String getSecure() {
        return mSecureTypeButton.getText().toString().trim();
    }

    public void setSecure(int type) {
        mSecureTypeButton.setText(SECURE_TYPE[type]);
    }

    public String getIp() {
        return mIpEditText.getText().toString().trim();
    }

    public void refreshIp(String ip) {
        if (ip == null) {
            mIpEditText.setText("");
        }
        mIpEditText.setText(ip);
    }

    public String getNetmask() {
        return mNetmaskEditText.getText().toString().trim();
    }

    public void refreshNetmask(String netmask) {
        if (netmask == null) {
            mNetmaskEditText.setText("");
        }
        mNetmaskEditText.setText(netmask);
    }

    public String getGateway() {
        return mGatewayEditText.getText().toString().trim();
    }

    public void refreshGateway(String gateway) {
        if (gateway == null) {
            mGatewayEditText.setText("");
        }
        mGatewayEditText.setText(gateway);
    }

    public String getDns1() {
        return mDns1EditText.getText().toString().trim();
    }

    public void refreshDns1(String dns) {
        if (dns == null) {
            mDns1EditText.setText("");
        }
        mDns1EditText.setText(dns);
    }

    public String getDns2() {
        return mDns2EditText.getText().toString().trim();
    }

    public void refreshDns2(String dns) {
        if (dns == null) {
            mDns2EditText.setText("");
        }
        mDns2EditText.setText(dns);
    }

    public void refreshConnectTitle(String title) {
        if (title == null) {
            mConnectTitleTextView.setText("");
        }
        mConnectTitleTextView.setText(title);
    }

    public void refreshConnectTitle(int id) {
        if (id <= 0) {
            mConnectTitleTextView.setText("");
        }
        mConnectTitleTextView.setText(id);
    }

    public void setIpConfigLayoutVisible(boolean visible) {
        if (visible) {
            mIpConfigLayout.setVisibility(View.VISIBLE);
        } else {
            mIpConfigLayout.setVisibility(View.GONE);
        }
    }

    public void setPasswdLayoutVisible(boolean visible) {
        if (visible) {
            mPasswdLayout.setVisibility(View.VISIBLE);
            mShowPasswdLayout.setVisibility(View.VISIBLE);
        } else {
            mPasswdLayout.setVisibility(View.GONE);
            mShowPasswdLayout.setVisibility(View.GONE);
        }
    }

    public void setSsidLayoutVisible(boolean visible) {
        if (visible) {
            mAddSsidLayout.setVisibility(View.VISIBLE);
        } else {
            mAddSsidLayout.setVisibility(View.GONE);
        }
    }

    public void setSecureTypeFocusable(boolean focusable) {
        mSecureTypeButton.setFocusable(focusable);
    }

    public boolean isSecureTypeFocused() {
        return mSecureTypeButton.isFocused();
    }

    public boolean isCancelButtonFocused() {
        return mCancelButton.isFocused();
    }

    private void findViews() {
        // Wi-Fi name.
        mConnectTitleTextView = (TextView) mDialog.findViewById(R.id.wifi_connect_ssid);

        // add ssid
        mAddSsidLayout = (RelativeLayout) mDialog.findViewById(R.id.wifi_add_ssid_layout);
        mAddSsidEditText = (EditText) mDialog.findViewById(R.id.wifi_add_ssid);

        // secure layout.
        mSecureLayout = (RelativeLayout) mDialog.findViewById(R.id.wifi_connect_secure_layout);
        mSecureTypeButton = (Button) mDialog.findViewById(R.id.wifi_connect_sec_type);
        mSecureLeftImg = (ImageView) mDialog.findViewById(R.id.wifi_connect_secure_left_img);
        mSecureRightImg = (ImageView) mDialog.findViewById(R.id.wifi_connect_secure_right_img);

        // password layout
        mPasswdLayout = (RelativeLayout) mDialog.findViewById(R.id.wifi_connect_psword_layout);
        mPasswdEditText = (EditText) mDialog.findViewById(R.id.wifi_connect_psword);
        mShowPasswdLayout = (RelativeLayout) mDialog
                .findViewById(R.id.wifi_connect_psword_visable_layout);
        mShowPasswdCheckBox = (CheckBox) mDialog.findViewById(R.id.wifi_connect_psword_visible);

        // auto ip layout
        mAutoIpLayout = (RelativeLayout) mDialog.findViewById(R.id.wifi_edit_auto_ip_layout);
        mAutoIpCheckBox = (CheckBox) mDialog.findViewById(R.id.wifi_edit_auto_ip);

        // ip /gateway / netmask etc config layout
        mIpConfigLayout = (LinearLayout) mDialog.findViewById(R.id.wifi_ip_config_layout);
        mIpEditText = (EditText) mDialog.findViewById(R.id.wifi_connect_ip);
        mNetmaskEditText = (EditText) mDialog.findViewById(R.id.wifi_connect_netmask);
        mGatewayEditText = (EditText) mDialog.findViewById(R.id.wifi_connect_gateway);
        mDns1EditText = (EditText) mDialog.findViewById(R.id.wifi_connect_dns1);
        mDns2EditText = (EditText) mDialog.findViewById(R.id.wifi_connect_dns2);

        // save
        mSaveButton = (Button) mDialog.findViewById(R.id.wifi_conenct_save);
        mCancelButton = (Button) mDialog.findViewById(R.id.wifi_conenct_cancel);
        mForgetButton = (Button) mDialog.findViewById(R.id.wifi_conenct_forget);
    }

    private void setListener() {
        mSecureTypeButton.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "mSecureTypeButton hasFocus, " + hasFocus);
                if (hasFocus) {
                    mSecureLayout.setBackgroundResource(R.drawable.desktop_button);
                    mSecureLeftImg.setVisibility(View.VISIBLE);
                    mSecureRightImg.setVisibility(View.VISIBLE);
                } else {
                    mSecureLayout.setBackgroundResource(R.drawable.one_px);
                    mSecureLeftImg.setVisibility(View.GONE);
                    mSecureRightImg.setVisibility(View.GONE);
                }
            }
        });

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

        mAutoIpCheckBox.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAutoIpLayout.setBackgroundResource(R.drawable.set_button);
                } else {
                    mAutoIpLayout.setBackgroundResource(R.drawable.one_px);
                }
            }
        });

        mSaveButton.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSaveButton.setBackgroundResource(R.drawable.left_bg);
                } else {
                    mSaveButton.setBackgroundResource(R.drawable.one_px);
                }
            }
        });

        mCancelButton.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mCancelButton.setBackgroundResource(R.drawable.left_bg);
                } else {
                    mCancelButton.setBackgroundResource(R.drawable.one_px);
                }
            }
        });
        mCancelButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mForgetButton.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mForgetButton.setBackgroundResource(R.drawable.left_bg);
                } else {
                    mForgetButton.setBackgroundResource(R.drawable.one_px);
                }
            }
        });
    }

}
