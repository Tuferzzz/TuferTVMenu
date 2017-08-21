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

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Toast;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.network.ProxySettings.ProxyInfo;
import tufer.com.menutest.Util.Tools;

import com.mstar.android.wifi.MWifiManager;

public class WifiP2pSettings extends NetworkSettings implements PeerListListener, INetworkSettingsListener {

    private static final String TAG = "MSettings.WifiP2pSettings";

    private NetworkSettingsActivity mActivity;
    private WifiP2pSettingsHolder mWifiP2pSettingsHolder;
    private CheckBox mCheckBox;
    private ListView mDeviceListView;
    private WifiP2pListAdapter mDeviceAdater;

    private boolean mIsDirectEnable;
    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pDevice mThisDevice;
    private IntentFilter mIntentFilter = new IntentFilter();
    private WifiP2pDeviceList mPeers = new WifiP2pDeviceList();

    private static final int WPS_PBC = 0;
    private static final int WPS_KEYPAD = 1;
    private static final int WPS_DISPLAY = 2;
    private int mWpsSetupIndex = WPS_PBC; //default is pbc
    // current select
    private int mIndex ;

    // setting item on the right
    private int mSettingItem = Constants.SETTING_ITEM_0;

    public WifiP2pSettings(NetworkSettingsActivity activity) {
        super(activity);
        this.mActivity = activity;
        mWifiP2pSettingsHolder = new WifiP2pSettingsHolder(activity);
        mCheckBox = mWifiP2pSettingsHolder.getWifiP2pCheckBox();
        mDeviceListView = mWifiP2pSettingsHolder.getWifiP2pDeviceListView();
        mIsDirectEnable = false;

        // init the manager
        mWifiP2pManager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        if (mWifiP2pManager != null) {
            mChannel = mWifiP2pManager.initialize(activity, activity.getMainLooper(), null);
            if (mChannel == null) {
                // Failure to set up connection
                Log.e(TAG, "Failed to set up connection with wifi p2p service");
                mCheckBox.setEnabled(false);
            }
        } else {
            Log.e(TAG, "mWifiP2pManager is null!");
        }

        // register receiver
        registerReceiver();
        setListeners();
    }

    public void setVisible(boolean visible) {
        Log.d(TAG, "visible, " + visible);
        mWifiP2pSettingsHolder.setVisible(visible);
        if (visible) {
            // opened with wifi open
            if (getWifiManager().isWifiEnabled()) {
                mCheckBox.setChecked(true);
                // refresh device info
                mWifiP2pSettingsHolder.refreshDeviceInfo(mThisDevice);

                // discover p2p devices
                if (mCheckBox.isChecked()) {
                    mWifiP2pManager.discoverPeers(mChannel, new ActionListener() {

                        public void onSuccess() {
                            Log.d(TAG, " discover success");
                            discoverNotice(1);
                        }

                        public void onFailure(int reason) {
                            Log.e(TAG, " discover fail " + reason);
                            discoverNotice(2);
                        }
                    });
                }
            } else {
                mCheckBox.setChecked(false);
                // refresh device info
                mWifiP2pSettingsHolder.refreshDeviceInfo(null);
            }
        }
    }

    @Override
    public void onExit() {
        mActivity.unregisterReceiver(mReceiver);
        mActivity.unregisterReceiver(mWifiHWReceiver);
    }

    @Override
    public boolean onKeyEvent(int keyCode, KeyEvent keyEvent) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mSettingItem == Constants.SETTING_ITEM_0) {
                    return true;
                } else if (mSettingItem == Constants.SETTING_ITEM_1
                        || mSettingItem == Constants.SETTING_ITEM_2) {
                    mSettingItem--;
                    mWifiP2pSettingsHolder.requestFocus(mSettingItem);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mSettingItem == Constants.SETTING_ITEM_0) {
                    mSettingItem++;
                    mWifiP2pSettingsHolder.requestFocus(mSettingItem);
                } else if (mSettingItem == Constants.SETTING_ITEM_1) {
                    // check whether has wifip2pdevice
                    if (mPeers == null || mPeers.getDeviceList() == null
                            || mPeers.getDeviceList().size() <= 0) {
                        return true;
                    } else {
                        mSettingItem++;
                        mDeviceListView.setSelection(0);
                        mWifiP2pSettingsHolder.requestFocus(mSettingItem);
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
        Log.d(TAG, "isOn, " + isOn);
        if (!isOn) {
            mCheckBox.setChecked(false);
            // refresh device info
            mWifiP2pSettingsHolder.refreshDeviceInfo(null);
            mWifiP2pSettingsHolder.refreshNotice("");
        }
    }

    @Override
    public void onFocusChange(boolean hasFocus) {
        Tools.logd(TAG, "hasFocus, " + hasFocus);
        if (hasFocus) {
            mWifiP2pSettingsHolder.requestFocus(Constants.SETTING_ITEM_0);

        } else {
            mWifiP2pSettingsHolder.clearFocus(mSettingItem);
            mSettingItem = 0;
        }
    }

    @Override
    public void onProxyChanged(boolean enabled, ProxyInfo proxyInfo) {

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        Tools.logd(TAG, "peers, " + peers.toString() + " p2p num, " + peers.getDeviceList().size());
        mPeers = peers;
        mDeviceAdater = new WifiP2pListAdapter(mActivity, mPeers.getDeviceList());
        mDeviceListView.setAdapter(mDeviceAdater);
    }

    public void connectDirect(WifiP2pConfig config) {
        if (null == config) {
            return;
        }

        mWifiP2pManager.connect(mChannel, config, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "connect success ");
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(mActivity, "connect failed. Retry.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void disconnectDirect() {
        if (null == mChannel) {
            Log.e(TAG, "mChannel==null");
            return;
        }

        mWifiP2pManager.removeGroup(mChannel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.e(TAG, "disconnect failed. Reason :" + reasonCode);
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "disconnect success ");
            }
        });
    }

    public WifiP2pConfig getConfig(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps = new WpsInfo();
        switch (mWpsSetupIndex) {
            case WPS_PBC:
                config.wps.setup = WpsInfo.PBC;
                break;
            case WPS_KEYPAD:
                config.wps.setup = WpsInfo.KEYPAD;
                break;
            case WPS_DISPLAY:
                config.wps.setup = WpsInfo.DISPLAY;
                break;
            default:
                config.wps.setup = WpsInfo.PBC;
                break;
        }

        return config;
    }

    public void discoverDevice() {
        if (!mIsDirectEnable) {
            showToast(R.string.please_open_direct);
            return;
        }

        if (mWifiP2pManager != null) {
            discoverNotice(0);
            mWifiP2pManager.discoverPeers(mChannel, new ActionListener() {
                public void onSuccess() {
                    Log.d(TAG, "discover success");
                    discoverNotice(1);
                }

                public void onFailure(int reason) {
                    Log.e(TAG, "discover fail " + reason);
                    discoverNotice(2);
                }
            });
        }
    }

    private void handleP2pStateChanged(int state) {
        Log.d(TAG, "handleP2pStateChanged");
        mCheckBox.setEnabled(true);
        switch (state) {
            case WifiP2pManager.WIFI_P2P_STATE_ENABLED:
                Log.d(TAG, "WIFI_P2P_STATE_ENABLED");
                mIsDirectEnable = true;
                // mCheckBox.setChecked(true);
                break;
            case WifiP2pManager.WIFI_P2P_STATE_DISABLED:
                Log.d(TAG, "WIFI_P2P_STATE_DISABLED");
                mWifiP2pSettingsHolder.refreshNotice("");
                mIsDirectEnable = false;
                mThisDevice = null;
                // mCheckBox.setChecked(false);
                break;
            default:
                mIsDirectEnable = true;
                Log.d(TAG, "Unhandled wifi state " + state);
                break;
        }
    }

    private void discoverNotice(int status) {
        String str = "";
        switch (status) {
            case 0:
                str = mActivity.getResources().getString(R.string.wifi_direct_search);
                break;
            case 1:
                str = mActivity.getResources().getString(R.string.device_search_success);
                break;
            case 2:
                str = mActivity.getResources().getString(R.string.device_search_failed);
                break;
            default:
                break;
        }

        String txt = null;
        if (mIsDirectEnable) {
            txt = mActivity.getResources().getString(R.string.device_list) + "       " + str;
        } else {
            txt = mActivity.getResources().getString(R.string.device_list);
        }
        // FIXME mWifiP2pSettingsHolder.refreshNotice(txt);
    }

    private void registerReceiver() {
        // add action
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mActivity.registerReceiver(mReceiver, mIntentFilter);

        // wifi hw changed
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(MWifiManager.WIFI_DEVICE_ADDED_ACTION);
        wifiFilter.addAction(MWifiManager.WIFI_DEVICE_REMOVED_ACTION);
        mActivity.registerReceiver(mWifiHWReceiver, wifiFilter);
    }

    private void showToast(int id) {
        Toast.makeText(mActivity, id, Toast.LENGTH_SHORT).show();
    }

    private void showDialog() {
        Builder builder = new Builder(mActivity);
        builder.setMessage(R.string.wifi_direct_cancel);
        builder.setTitle(R.string.wifi_direct);

        WifiP2pDevice device = (WifiP2pDevice) (mPeers.getDeviceList().toArray()[mIndex]);
        if (WifiP2pDevice.CONNECTED == device.status) {
            builder.setPositiveButton(R.string.wifi_direct_disconnect, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    disconnectDirect();
                    dialog.dismiss();
                }
            });

        } else {
            builder.setPositiveButton(R.string.wifi_direct_connect, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    WifiP2pDevice device = (WifiP2pDevice) (mPeers.getDeviceList().toArray()[mIndex]);
                    WifiP2pConfig config = getConfig(device);
                    connectDirect(config);
                    dialog.dismiss();
                }
            });
        }
        // show dialog
        builder.create().show();
    }

    private void setListeners() {
        mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "isChecked, " + isChecked);
            }
        });
        mCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;
                    showToast(R.string.wifi_direct_toggle_hint);
                    checkBox.setChecked(!checkBox.isChecked());
                }
            }
        });

        mDeviceListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mIndex = position;
                showDialog();
            }
        });

        mWifiP2pSettingsHolder.getWifiSearchButton().setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        discoverDevice();
                    }
                });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Tools.logd(TAG, "action, " + action);
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "WIFI_P2P_STATE_CHANGED_ACTION");
                handleP2pStateChanged(intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,
                        WifiP2pManager.WIFI_P2P_STATE_DISABLED));
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION");
                if (mWifiP2pManager != null) {
                    mWifiP2pManager.requestPeers(mChannel, WifiP2pSettings.this);
                }
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "WIFI_P2P_CONNECTION_CHANGED_ACTION");
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
                mThisDevice = (WifiP2pDevice) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                Log.d(TAG, "mThisDevice.String()" + mThisDevice.toString());
                mWifiP2pSettingsHolder.refreshDeviceInfo(mThisDevice);
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

}
