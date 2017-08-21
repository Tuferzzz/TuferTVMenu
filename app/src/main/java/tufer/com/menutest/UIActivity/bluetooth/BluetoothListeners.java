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

package tufer.com.menutest.UIActivity.bluetooth;



import android.R.color;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;
import tufer.com.menutest.Util.PreferancesTools;


public class BluetoothListeners {

    private BluetoothActivity bluetoothActivity;

    private BluetoothViewHolder bluetoothViewHolder;

    public static final int DISCOVERABLE_TIMEOUT_TWO_MINUTES = 120;

    public static final int DISCOVERABLE_TIMEOUT_FIVE_MINUTES = 300;

    public static final int DISCOVERABLE_TIMEOUT_ONE_HOUR = 3600;

    public static final int DISCOVERABLE_TIMEOUT_NEVER = 0;

    public PreferancesTools preferancesTools;

    private boolean isopening = false;

    public BluetoothListeners(BluetoothActivity bluetoothActivity,
            BluetoothViewHolder bluetoothViewHolder) {
        this.bluetoothActivity = bluetoothActivity;
        this.bluetoothViewHolder = bluetoothViewHolder;
        preferancesTools = new PreferancesTools(bluetoothActivity);
    }

    public void setListener() {
        bluetoothViewHolder.bluetooth_button
                .setOnClickListener(new RelativeLayout.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (bluetoothActivity.bluetooth_state == 0
                                && bluetoothActivity.mAdapter.isBluetoothExist()) {
                            if ((bluetoothActivity.mAdapter.getBluetoothState() != BluetoothAdapter.STATE_DISCONNECTED) &&
                                bluetoothActivity.mAdapter.getBluetoothState() != BluetoothAdapter.STATE_OFF) {
                                return;
                            }

                            bluetoothViewHolder.bluetooth_button.setEnabled(false);
                            bluetoothViewHolder.progressBar.setVisibility(View.VISIBLE);
                            bluetoothActivity.mAdapter.enable();

                            new Thread(new Runnable() {

                                public void run() {
                                    int i = 0;

                                    while (!bluetoothActivity.mAdapter.isEnabled()) {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        i++;
                                        /*
                                         * Modified by gerard.jiang for "0388655" in 2013/04/28.
                                         * If 4 seconds can not open Bluetooth, close it
                                         */
                                        if (i >= 8) {
                                        /***** Ended by gerard.jiang 2013/04/28 *****/
                                            bluetoothActivity.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(
                                                            bluetoothActivity,
                                                            bluetoothActivity
                                                                    .getResources()
                                                                    .getString(
                                                                            R.string.no_bluetooth_device),
                                                            1).show();
                                                    bluetoothViewHolder.progressBar
                                                            .setVisibility(View.GONE);
                                                    bluetoothViewHolder.bluetooth_button
                                                            .setEnabled(true);
                                                    MainActivity.isBuletoothOn=false;
                                                }
                                            });
                                            bluetoothActivity.mAdapter.disable();

                                            return;
                                        }
                                    }

                                    bluetoothActivity.runOnUiThread(new Runnable() {

                                        public void run() {
                                            bluetoothViewHolder.progressBar
                                                    .setVisibility(View.GONE);
                                            bluetoothViewHolder.bluetooth_button.setEnabled(true);
                                            bluetoothViewHolder.bluetooth_button_image
                                                    .setBackgroundResource(R.drawable.open);
                                            bluetoothActivity.bluetooth_state = 1;
                                            bluetoothActivity.setText();
                                            MainActivity.isBuletoothOn=true;
                                        }
                                    });
                                }
                            }).start();

                        } else if (bluetoothActivity.bluetooth_state == 1) {
                            bluetoothActivity.mAdapter.disable();
                            bluetoothActivity.bluetooth_state = 0;
                            bluetoothViewHolder.bluetooth_button_image
                                    .setBackgroundResource(R.drawable.close);
                            MainActivity.isBuletoothOn=false;
                        } else {
                            Toast.makeText(
                                    bluetoothActivity,
                                    bluetoothActivity.getResources().getString(
                                            R.string.no_bluetooth_device), 1).show();
                            MainActivity.isBuletoothOn=false;
                        }
                    }
                });

        bluetoothViewHolder.bluetooth_devicename.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (bluetoothActivity.bluetooth_state == 1) {
                    bluetoothActivity.showDialog(bluetoothActivity.CHANGE_DEVICE_NAME);
                } else {
                    Toast.makeText(
                            bluetoothActivity,
                            bluetoothActivity.getResources().getString(
                                    R.string.open_bluetooth_first), 1).show();
                }
            }
        });

        bluetoothViewHolder.bluetooth_discoverable_timeout
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (bluetoothActivity.bluetooth_state == 1) {
                            bluetoothActivity
                                    .showDialog(bluetoothActivity.BLUETOOTH_DISCOVERABLE_TIMEOUT_VIEW);

                        } else {
                            Toast.makeText(
                                    bluetoothActivity,
                                    bluetoothActivity.getResources().getString(
                                            R.string.open_bluetooth_first), 1).show();
                        }
                    }
                });

        bluetoothViewHolder.bluetooth_button.setOnFocusChangeListener(itemFocusedListener);
        bluetoothViewHolder.bluetooth_devicename.setOnFocusChangeListener(itemFocusedListener);
        bluetoothViewHolder.bluetooth_discoverable_timeout
                .setOnFocusChangeListener(itemFocusedListener);
        bluetoothViewHolder.bluetooth_scan_for_devices
                .setOnFocusChangeListener(itemFocusedListener);
    }

    public void setEnabled(boolean enable) {
        if (enable) {
            int timeout = getDiscoverableTimeout();
            setDiscoverableTimeout(timeout);
        } else {
            bluetoothActivity.mAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE);
        }
    }

    public int getDiscoverableTimeout() {
        int timeoutValue = preferancesTools.getIntPref(
                PreferancesTools.SYSTEM_PROPERTY_DISCOVERABLE_TIMEOUT,
                DISCOVERABLE_TIMEOUT_TWO_MINUTES);

        return timeoutValue;
    }

    public void setDiscoverableTimeout(int timeout) {
        bluetoothActivity.mAdapter.setDiscoverableTimeout(timeout);

        long endTimestamp = System.currentTimeMillis() + timeout * 1000L;
        preferancesTools.setLongPref(PreferancesTools.DISCOVERABLE_END_TIMESTAMP, endTimestamp);

        bluetoothActivity.mAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,
                timeout);
    }

    private View.OnFocusChangeListener itemFocusedListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                v.setBackgroundResource(R.drawable.desktop_button);
            } else {
                v.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    };

}
