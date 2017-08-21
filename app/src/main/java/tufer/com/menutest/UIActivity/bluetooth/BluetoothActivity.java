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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;


public class BluetoothActivity extends Activity {

    private BluetoothDiscoverableEnabler mDiscoverableEnabler;

    public static final int CHANGE_DEVICE_NAME = 100000002;

    public static final int BLUETOOTH_DISCOVERABLE_TIMEOUT_VIEW = 100000004;

    private BluetoothViewHolder bluetoothViewHolder;

    private BluetoothListeners bluetoothListeners;

    private BlueToothStateReceiver blueToothStateReceiver;

    public LocalBluetoothAdapter mAdapter;

    private LocalBluetoothManager manager;

    private int timeout_count;

    public int timeout;

    private Dialog setTimeoutDialog = null;

    int state = 1;

    int bluetooth_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);
        bluetoothViewHolder = new BluetoothViewHolder(this);
        bluetoothListeners = new BluetoothListeners(this, bluetoothViewHolder);
        bluetoothListeners.setListener();
        manager = LocalBluetoothManager.getInstance(this);
        mAdapter = manager.getBluetoothAdapter();

        blueToothStateReceiver = new BlueToothStateReceiver();
        IntentFilter blueToothFilter = new IntentFilter();
        blueToothFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(blueToothStateReceiver, blueToothFilter);
        init();
    }

    public void setText() {
        timeout_count = mAdapter.getDiscoverableTimeout();
        bluetoothViewHolder.bluetooth_devicename_text.setText(mAdapter.getName());
        setTimeoutText();
    }

    public final class BlueToothStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int extra_state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            int extra_previous_state = intent
                    .getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1);
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)
                    && extra_state == BluetoothAdapter.STATE_OFF
                    && extra_previous_state == BluetoothAdapter.STATE_ON) {
                if (bluetooth_state == 1) {
                    mAdapter.disable();
                    bluetooth_state = 0;
                    bluetoothViewHolder.bluetooth_button_image
                            .setBackgroundResource(R.drawable.close);
                }
            }
        }
    }

    private void setTimeoutText() {
        int discoverabletimeout = mAdapter.getDiscoverableTimeout();
        switch (discoverabletimeout) {
            case 120:
                bluetoothViewHolder.bluetooth_discoverable_timeout_text.setText(getResources()
                        .getString(R.string.twominute));
                break;
            case 300:
                bluetoothViewHolder.bluetooth_discoverable_timeout_text.setText(getResources()
                        .getString(R.string.fiveminute));
                break;
            case 3600:
                bluetoothViewHolder.bluetooth_discoverable_timeout_text.setText(getResources()
                        .getString(R.string.onehour));
                break;
            case 0:
                bluetoothViewHolder.bluetooth_discoverable_timeout_text.setText(getResources()
                        .getString(R.string.never));
            default:
                bluetoothViewHolder.bluetooth_discoverable_timeout_text.setText("");
                break;
        }
    }

    private void init() {
        if (!mAdapter.isEnabled()) {
            bluetooth_state = 0;
            bluetoothViewHolder.bluetooth_button_image.setBackgroundResource(R.drawable.close);
            mAdapter.disable();
        } else {
            setText();
            bluetoothViewHolder.bluetooth_button_image.setBackgroundResource(R.drawable.open);
            bluetooth_state = 1;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CHANGE_DEVICE_NAME:
                // change_device_name
                final EditText editText = new EditText(this);
                editText.setWidth(200);
                editText.setHeight(50);

                return new AlertDialog.Builder(this).setView(editText)
                        .setTitle(R.string.bluetooth_devicename)
                        .setPositiveButton(R.string.ok, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = editText.getText().toString();
                                if (name.equals("") || name.equals(null)) {
                                    Toast.makeText(BluetoothActivity.this,
                                            getResources().getString(R.string.input_not_null), 1)
                                            .show();
                                } else {
                                    mAdapter.setName(name);
                                    bluetoothViewHolder.bluetooth_devicename_text.setText(name);
                                }
                            }
                        }).setNegativeButton(R.string.cancle, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            case BLUETOOTH_DISCOVERABLE_TIMEOUT_VIEW:
                if (mDiscoverableEnabler == null) {
                    mDiscoverableEnabler = LocalBluetoothManager.getInstance(this)
                            .getDiscoverableEnabler();
                }
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.buletooth_discoverable_timeout, null);
                Button button = (Button) view.findViewById(R.id.set_timeout_cancle_button);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (setTimeoutDialog != null) {
                            setTimeoutDialog.cancel();
                        }
                    }
                });
                RelativeLayout twominute = (RelativeLayout) view
                        .findViewById(R.id.twomin_timeout_re);
                RelativeLayout fiveminute = (RelativeLayout) view
                        .findViewById(R.id.fivemin_timeout_re);
                RelativeLayout onehour = (RelativeLayout) view
                        .findViewById(R.id.onehour_timeout_re);
                RelativeLayout never = (RelativeLayout) view.findViewById(R.id.never_timeout_re);
                twominute.setOnClickListener(setTimeoutListener);
                twominute.setOnFocusChangeListener(timeoutFocusedListener);
                fiveminute.setOnClickListener(setTimeoutListener);
                fiveminute.setOnFocusChangeListener(timeoutFocusedListener);
                onehour.setOnClickListener(setTimeoutListener);
                onehour.setOnFocusChangeListener(timeoutFocusedListener);
                never.setOnClickListener(setTimeoutListener);
                never.setOnFocusChangeListener(timeoutFocusedListener);

                return setTimeoutDialog = new AlertDialog.Builder(this).setView(view).show();
            default:
                break;
        }

        return null;
    }

    View.OnClickListener setTimeoutListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.twomin_timeout_re:
                    mDiscoverableEnabler.setDiscoverableTimeout(0);
                    bluetoothViewHolder.bluetooth_discoverable_timeout_text.setText(getResources()
                            .getString(R.string.twominute));
                    break;
                case R.id.fivemin_timeout_re:
                    mDiscoverableEnabler.setDiscoverableTimeout(1);
                    bluetoothViewHolder.bluetooth_discoverable_timeout_text.setText(getResources()
                            .getString(R.string.fiveminute));
                    break;
                case R.id.onehour_timeout_re:
                    mDiscoverableEnabler.setDiscoverableTimeout(2);
                    bluetoothViewHolder.bluetooth_discoverable_timeout_text.setText(getResources()
                            .getString(R.string.onehour));
                    break;
                case R.id.never_timeout_re:
                    mDiscoverableEnabler.setDiscoverableTimeout(3);
                    bluetoothViewHolder.bluetooth_discoverable_timeout_text.setText(getResources()
                            .getString(R.string.never));
                    break;
                default:
                    break;
            }

            if (setTimeoutDialog != null) {
                setTimeoutDialog.cancel();
            }
        }
    };

    private View.OnFocusChangeListener timeoutFocusedListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                v.setBackgroundResource(R.drawable.button_bg);
            } else {
                v.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    };

    protected void onDestroy() {
        this.unregisterReceiver(blueToothStateReceiver);
        super.onDestroy();
    };

    @Override
    protected void onStop() {
        MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_NETWORK);
        super.onStop();
    }
}
