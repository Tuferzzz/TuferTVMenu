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
import tufer.com.menutest.UIActivity.network.ProxySettings.ProxyInfo;
import com.mstar.android.ethernet.EthernetDevInfo;
import com.mstar.android.pppoe.PPPOE_STA;
import com.mstar.android.pppoe.PppoeManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import tufer.com.menutest.Util.PreferancesTools;


public class PPPoESettings extends NetworkSettings implements INetworkSettingsListener {

    private static final String TAG = "MSettings.PPPoESettings";

    private static final int PPPOE_UPDATE_MASSAGE = 0;
    private static final int PPPOE_UPDATE_THREAD_EXIT = 1;
    private static final int PPPOE_TIMER_OUT = 60;

    private NetworkSettingsActivity mActivity;

    private PPPoESettingsHolder mPPPoESettingsHolder;

    private PppoeManager mPppoeManager;

    private CheckPPPoEThread mCheckThread = null;

    private PreferancesTools mPreferance;

    private boolean isWirePPPOE;

    private int mSettingItem = Constants.SETTING_ITEM_0;

    public PPPoESettings(NetworkSettingsActivity activity) {
        super(activity);
        this.mActivity = activity;

        mPreferance = new PreferancesTools(activity);
        mPppoeManager = getPPPoEManager();
        mPPPoESettingsHolder = new PPPoESettingsHolder(activity);
        setListener();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PppoeManager.PPPOE_STATE_ACTION);
        activity.registerReceiver(mPppoeReceiver, intentFilter);
    }

    public void setVisible(boolean visible) {
        mPPPoESettingsHolder.setVisible(visible);
        if (visible) {
            showPPPoEInfo();
        }
    }

    @Override
    public void onExit() {
        if (null != mCheckThread) {
            mCheckThread.cancelSelf();
            mCheckThread = null;
        }
        mActivity.unregisterReceiver(mPppoeReceiver);
    }

    @Override
    public boolean onKeyEvent(int keyCode, KeyEvent keyEvent) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mSettingItem > Constants.SETTING_ITEM_0
                        && mSettingItem <= Constants.SETTING_ITEM_5) {
                    mSettingItem--;
                    mPPPoESettingsHolder.requestFocus(mSettingItem);
                } else if (mSettingItem == Constants.SETTING_ITEM_6) {
                    mSettingItem -= 2;
                    mPPPoESettingsHolder.requestFocus(mSettingItem);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mSettingItem >= Constants.SETTING_ITEM_0
                        && mSettingItem <= Constants.SETTING_ITEM_4) {
                    mSettingItem++;
                    mPPPoESettingsHolder.requestFocus(mSettingItem);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mSettingItem == Constants.SETTING_ITEM_6) {
                    mSettingItem--;
                    mPPPoESettingsHolder.requestFocus(mSettingItem);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mSettingItem == Constants.SETTING_ITEM_5) {
                    mSettingItem++;
                    mPPPoESettingsHolder.requestFocus(mSettingItem);
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
            mPPPoESettingsHolder.requestFocus(mSettingItem);
        } else {
            mPPPoESettingsHolder.clearFocus(mSettingItem);
            mSettingItem = Constants.SETTING_ITEM_0;
        }
    }

    @Override
    public void onProxyChanged(boolean enabled, ProxyInfo proxyInfo) {

    }

    @Override
    public void onWifiHWChanged(boolean isOn) {
        Log.d(TAG, "isOn, " + isOn);
        if (!isOn) {

        }
    }

    public PPPOE_STA getPPPOEstatus() {
        return mPppoeManager.PppoeGetStatus();
    }

    private void showPPPoEInfo() {
        // refresh connect type
        if (getWifiManager().isWifiEnabled()) {
            isWirePPPOE = false;
            mPPPoESettingsHolder.refreshConnectType(R.string.wireless_connect);
        } else {
            isWirePPPOE = true;
            mPPPoESettingsHolder.refreshConnectType(R.string.wire_connect);
        }

        // show user and passwd
        String user = mPppoeManager.PppoeGetUser();
        String passwd = mPppoeManager.PppoeGetPW();
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(passwd)) {
            mPPPoESettingsHolder.getUsernameEditText().setHint(R.string.input_username);
            mPPPoESettingsHolder.getPasswordEditText().setHint(R.string.input_password);
        } else {
            mPPPoESettingsHolder.getUsernameEditText().setText(user);
            mPPPoESettingsHolder.getPasswordEditText().setText(passwd);
        }
        updatePppoeStatus();

        CheckBox checkBox = mPPPoESettingsHolder.getAutoDialCheckBox();
        // update auto dial checkbox
        boolean isAuto = mPreferance.getBooleanPref(PreferancesTools.PPPOE_IS_AUTO_DIALER, false);
        checkBox.setChecked(isAuto);
    }

    private void updatePppoeStatus() {
        PPPOE_STA status = getPPPOEstatus();
        if (status == PPPOE_STA.CONNECTED) {
            Log.d(TAG, "PPPOE_STA.CONNECTED");
            mPPPoESettingsHolder.refreshStatus(R.string.pppoe_connect_success);

            // FIXME showPppoeStatus();
        } else if (status == PPPOE_STA.DISCONNECTED) {
            mPPPoESettingsHolder.refreshStatus(R.string.pppoe_disconnect);
            Log.d(TAG, "PPPOE_STA.DISCONNECTED");

        } else {
            Log.d(TAG, "is_dialing");
            mPPPoESettingsHolder.refreshStatus(R.string.is_dialing);
        }
    }

    private void pppoeDialer() {
        if (null != mCheckThread) {
            mCheckThread.cancelSelf();
            mCheckThread = null;
        }

        // check CONNECTING
        mCheckThread = new CheckPPPoEThread(PPPOE_STA.CONNECTING);
        mCheckThread.start();
        Log.d(TAG, "ThreadStart id, " + mCheckThread.getId());
    }

    private void dial() {
        if (PPPOE_STA.CONNECTING == getPPPOEstatus()) {
            Log.d(TAG, "CONNECTING.....");
            return;
        }

        // check username and password
        String user = mPPPoESettingsHolder.getUsername();
        String passwd = mPPPoESettingsHolder.getPasswd();
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(passwd)) {
            return;

        } else {
            String ifName = null;
            // check pppoe type
            if (isWirePPPOE) {
                Log.d(TAG, "wire pppoe");
                EthernetDevInfo ethInfo = getEthernetManager().getSavedConfig();
                ifName = ethInfo.getIfName();
                if (null == ifName) {
                    ifName = "eth0"; //
                }
            } else {
                Log.d(TAG, "wifi pppoe");
                ifName = "wlan0";
            }

            mPppoeManager.PppoeSetInterface(ifName);
            mPppoeManager.PppoeSetUser(user);
            mPppoeManager.PppoeSetPW(passwd);
            mPppoeManager.PppoeDialup();
            mPPPoESettingsHolder.refreshStatus(R.string.is_dialing);
            pppoeDialer();
        }
    }

    private void hangup() {
        if (null != mCheckThread) {
            mCheckThread.cancelSelf();
            mCheckThread = null;
        }

        // check CONNECTED
        mCheckThread = new CheckPPPoEThread(PPPOE_STA.CONNECTED);
        mCheckThread.start();
        Log.d(TAG, "ThreadStart id, " + mCheckThread.getId());

        mPppoeManager.PppoeHangUp();
        Log.d(TAG, "ppppoe hang up");
    }

    private void setListener() {
        CheckBox checkBox = mPPPoESettingsHolder.getAutoDialCheckBox();
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // save preferances
                mPreferance.setBooleanPref(PreferancesTools.PPPOE_IS_AUTO_DIALER, isChecked);
            }
        });

        // handup button
        Button hangupButton = mPPPoESettingsHolder.getHangupButton();
        hangupButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                hangup();
            }
        });

        //
        Button dialButton = mPPPoESettingsHolder.getDialButton();
        dialButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                dial();
            }
        });
    }

    class CheckPPPoEThread extends Thread {
        int loopCount;

        PPPOE_STA mCheckStatus;

        public CheckPPPoEThread(PPPOE_STA status) {
            // /check which status
            Log.d(TAG, "CheckPPPoEThread INIT");
            loopCount = 0;
            mCheckStatus = status;
        }

        public void run() {
            while (mPppoeManager.PppoeGetStatus() == mCheckStatus && loopCount++ < PPPOE_TIMER_OUT) {
                Log.d(TAG, "CheckPPPoEThread");
                Message message = new Message();
                message.what = PPPOE_UPDATE_MASSAGE;
                mHandler.sendMessage(message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            Log.d(TAG, "ThreadEnd id=" + this.getId());
            Message message = new Message();
            message.what = PPPOE_UPDATE_THREAD_EXIT;
            mHandler.sendMessage(message);
        }

        public void cancelSelf() {
            loopCount = 2 * PPPOE_TIMER_OUT;
        }
    }

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PPPOE_UPDATE_MASSAGE:
                    updatePppoeStatus();
                    break;
                case PPPOE_UPDATE_THREAD_EXIT:
                    mCheckThread = null;
                    updatePppoeStatus();
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    };

    private BroadcastReceiver mPppoeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action, " + action);
            if (!action.equals("com.mstar.android.pppoe.PPPOE_STATE_ACTION")) {
                return;
            }

            String status = intent.getStringExtra(PppoeManager.PPPOE_STATE_STATUE);
            Log.d(TAG, "#pppoestatus=" + status);
            if (null == status)
                return;

            if (status.equals(PppoeManager.PPPOE_STATE_CONNECT)) {
                Log.d(TAG, "@pppoe_connect");
                mPPPoESettingsHolder.refreshStatus(R.string.pppoe_connect_success);
            } else if (status.equals(PppoeManager.PPPOE_STATE_DISCONNECT)) {
                Log.d(TAG, "@pppoe_disconnect");
                mPPPoESettingsHolder.refreshStatus(R.string.pppoe_disconnect);
            } else if (status.equals(PppoeManager.PPPOE_STATE_CONNECTING)) {
                Log.d(TAG, "@pppoe_connecting");
            } else if (status.equals(PppoeManager.PPPOE_STATE_AUTHFAILED)) {
                Log.d(TAG, "@pppoe_authfailed");
            } else if (status.equals(PppoeManager.PPPOE_STATE_FAILED)) {
                Log.d(TAG, "@pppoe_failed");
            }
            //updatePppoeStatus();
        }
    };

}
