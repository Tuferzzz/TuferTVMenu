/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tufer.com.menutest.UIActivity.bluetooth;



import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.network.ProgressCategory;


/**
 * BluetoothSettings is the Settings screen for Bluetooth configuration and
 * connection management.
 */
public final class BluetoothSettings extends DeviceListPreferenceFragment {

    private static final String TAG = "MSettings.BluetoothSettings";

    private static final int MENU_ID_SCAN = Menu.FIRST;

    private static final int MENU_ID_RENAME_DEVICE = Menu.FIRST + 1;

    private static final int MENU_ID_VISIBILITY_TIMEOUT = Menu.FIRST + 2;

    private static final int MENU_ID_SHOW_RECEIVED = Menu.FIRST + 3;

    /* Private intent to show the list of received files */
    private static final String BTOPP_ACTION_OPEN_RECEIVED_FILES = "android.btopp.intent.action.OPEN_RECEIVED_FILES";

    private BluetoothEnabler mBluetoothEnabler;

    private BluetoothDiscoverableEnabler mDiscoverableEnabler;

    private PreferenceGroup mPairedDevicesCategory;

    private PreferenceGroup mAvailableDevicesCategory;

    private boolean mAvailableDevicesCategoryIsPresent;

    private boolean mActivityStarted;

    private TextView mEmptyView;

    private final IntentFilter mIntentFilter;
    private final IntentFilter mBluetoothStateChangeFilter;

    private Dialog unpair_dialog = null;

    private UpdateContentHandler mHandler = null;

    private int mLastState = 0;

    class UpdateContentHandler extends Handler {
        public UpdateContentHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d(TAG, "handleMessage " + msg);
            super.handleMessage(msg);
            if (mLastState != mLocalAdapter.getBluetoothState()) {
                Log.d(TAG, "1  >>>>>>>"+mLastState);
                mLastState = mLocalAdapter.getBluetoothState();
                updateContent(mLastState, mActivityStarted);
            }
            Message nmsg = mHandler.obtainMessage(111);
            mHandler.sendMessageDelayed(nmsg, 1000);
        }
    }

    // accessed from inner class (not private to avoid thunks)
    Preference mMyDevicePreference;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED)) {
                updateDeviceName();
            }

            // Added by gerard.jiang for "0405230" in 2013/06/03
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                updateContent(mLocalAdapter.getBluetoothState(), true);
            }
            /***** Ended by gerard.jiang 2013/06/03 *****/
        }

        private void updateDeviceName() {
            if (mLocalAdapter.isEnabled() && mMyDevicePreference != null) {
                mMyDevicePreference.setTitle(mLocalAdapter.getName());
            }
        }
    };

    public BluetoothSettings() {
        mIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        // Added by gerard.jiang for "0405230" in 2013/06/03
        mBluetoothStateChangeFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        /***** Ended by gerard.jiang 2013/06/03 *****/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // don't auto start scan after rotation
        mActivityStarted = (savedInstanceState == null);

        mHandler = new UpdateContentHandler();

        mEmptyView = (TextView) getView().findViewById(android.R.id.empty);
        getListView().setEmptyView(mEmptyView);

        RelativeLayout bluetooth_scan_for_devices = (RelativeLayout) getActivity().findViewById(
                R.id.bluetooth_scan_for_devices);
        bluetooth_scan_for_devices.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mLocalAdapter.getBluetoothState() == BluetoothAdapter.STATE_ON) {
                    startScanning();
                }
            }
        });
    }

    @Override
    void addPreferencesForActivity() {
        addPreferencesFromResource(R.xml.bluetooth_settings);

        Activity activity = getActivity();

        Switch actionBarSwitch = new Switch(activity);

        if (activity instanceof PreferenceActivity) {
            PreferenceActivity preferenceActivity = (PreferenceActivity) activity;
            if (preferenceActivity.onIsHidingHeaders() || !preferenceActivity.onIsMultiPane()) {
                final int padding = activity.getResources().getDimensionPixelSize(
                        R.dimen.action_bar_switch_padding);
                actionBarSwitch.setPadding(0, 0, padding, 0);
                activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                        ActionBar.DISPLAY_SHOW_CUSTOM);
                activity.getActionBar().setCustomView(
                        actionBarSwitch,
                        new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL
                                        | Gravity.RIGHT));
            }
        }

        mBluetoothEnabler = new BluetoothEnabler(activity, actionBarSwitch);
    }

    @Override
    public void onResume() {
        // resume BluetoothEnabler before calling super.onResume() so we don't
        // get any onDeviceAdded() callbacks before setting up view in
        // updateContent()
        mBluetoothEnabler.resume();
        super.onResume();

        if (mDiscoverableEnabler != null) {
            mDiscoverableEnabler.resume();
        }
        getActivity().registerReceiver(mReceiver, mIntentFilter);
        // Added by gerard.jiang for "0405230" in 2013/06/03
        getActivity().registerReceiver(mReceiver, mBluetoothStateChangeFilter);
        /***** Ended by gerard.jiang 2013/06/03 *****/
        Log.d(TAG, ">>>>>>111>>>>>>>"+mLocalAdapter.getBluetoothState());
        Log.d(TAG, ">>>>>>222>>>>>>>"+mActivityStarted);
        updateContent(mLocalAdapter.getBluetoothState(), mActivityStarted);
        Message msg = mHandler.obtainMessage(111);
        mHandler.sendMessageDelayed(msg, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBluetoothEnabler.pause();
        mHandler.removeMessages(111);
        getActivity().unregisterReceiver(mReceiver);
        if (mDiscoverableEnabler != null) {
            mDiscoverableEnabler.pause();
        }
    }

    private void startScanning() {
        if (!mAvailableDevicesCategoryIsPresent) {
        	PreferenceScreen pfScreen = getPreferenceScreen();
        	if(pfScreen != null && mAvailableDevicesCategory != null){
        		pfScreen.addPreference(mAvailableDevicesCategory);
        	}
        }
        mLocalAdapter.startScanning(true);
    }

    @Override
    void onDevicePreferenceClick(BluetoothDevicePreference btPreference) {
        mLocalAdapter.stopScanning();
        super.onDevicePreferenceClick(btPreference);
    }

    private void addDeviceCategory(PreferenceGroup preferenceGroup, int titleId,
            BluetoothDeviceFilter.Filter filter) {
        preferenceGroup.setTitle(titleId);
        getPreferenceScreen().addPreference(preferenceGroup);
        setFilter(filter);
        setDeviceListGroup(preferenceGroup);
        addCachedDevices();
        preferenceGroup.setEnabled(true);
    }

    /**
     * @param bluetoothState
     * @param scanState
     */
    private void updateContent(int bluetoothState, boolean scanState) {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        int messageId = 0;

        switch (bluetoothState) {
            case BluetoothAdapter.STATE_ON:
                preferenceScreen.removeAll();
                preferenceScreen.setOrderingAsAdded(true);
                mDevicePreferenceMap.clear();

                // This device
                if (mMyDevicePreference == null) {
                    mMyDevicePreference = new Preference(getActivity());
                }
                mMyDevicePreference.setTitle(mLocalAdapter.getName());
                if (getResources().getBoolean(com.android.internal.R.bool.config_voice_capable)) {
                    // for phones
                    mMyDevicePreference.setIcon(R.drawable.ic_bt_cellphone);
                } else {
                    // for tablets, etc.
                    mMyDevicePreference.setIcon(R.drawable.ic_bt_laptop);
                }
                mMyDevicePreference.setPersistent(false);
                mMyDevicePreference.setEnabled(true);
                preferenceScreen.addPreference(mMyDevicePreference);

                if (mDiscoverableEnabler == null) {
                    mDiscoverableEnabler = new BluetoothDiscoverableEnabler(getActivity(),
                            mLocalAdapter, mMyDevicePreference);
                    mDiscoverableEnabler.resume();
                    LocalBluetoothManager.getInstance(getActivity()).setDiscoverableEnabler(
                            mDiscoverableEnabler);
                }

                // Paired devices category
                if (mPairedDevicesCategory == null) {
                    mPairedDevicesCategory = new PreferenceCategory(getActivity(), null);
                } else {
                    mPairedDevicesCategory.removeAll();
                }

                // Add a paired device module
                Log.d(TAG, "<<<<<<<<<<<"+mPairedDevicesCategory);
                addDeviceCategory(mPairedDevicesCategory,
                        R.string.bluetooth_preference_paired_devices,
                        BluetoothDeviceFilter.BONDED_DEVICE_FILTER);
                int numberOfPairedDevices = mPairedDevicesCategory.getPreferenceCount();

                mDiscoverableEnabler.setNumberOfPairedDevices(numberOfPairedDevices);

                // Available devices category
                if (mAvailableDevicesCategory == null) {
                    mAvailableDevicesCategory = new ProgressCategory(getActivity(), null);
                } else {
                    mAvailableDevicesCategory.removeAll();
                }

                // Add module unknown device
                addDeviceCategory(mAvailableDevicesCategory,
                        R.string.bluetooth_preference_found_devices,
                        BluetoothDeviceFilter.UNBONDED_DEVICE_FILTER);
                int numberOfAvailableDevices = mAvailableDevicesCategory.getPreferenceCount();
                mAvailableDevicesCategoryIsPresent = true;

                if (numberOfAvailableDevices == 0) {
                    preferenceScreen.removePreference(mAvailableDevicesCategory);
                    mAvailableDevicesCategoryIsPresent = false;
                }

                if (numberOfPairedDevices == 0) {
                    preferenceScreen.removePreference(mPairedDevicesCategory);
                    if (scanState == true) {
                        mActivityStarted = false;
                        startScanning();
                    } else {
                        if (!mAvailableDevicesCategoryIsPresent) {
                            getPreferenceScreen().addPreference(mAvailableDevicesCategory);
                        }
                    }
                }
                getActivity().invalidateOptionsMenu();
                return; // not break
            /*
             * Modified by gerard.jiang for "0405230" in 2013/06/03.
             * Received STATE_TURNING_OFF only when turn off the BT
             * So put them together.
             */
//            case BluetoothAdapter.STATE_TURNING_OFF:
//                messageId = R.string.bluetooth_turning_off;
//                break;

            case BluetoothAdapter.STATE_TURNING_OFF:
            case BluetoothAdapter.STATE_OFF:
            /***** Ended by gerard.jiang 2013/05/02 *****/
                messageId = R.string.bluetooth_empty_list_bluetooth_off;
                break;

            case BluetoothAdapter.STATE_TURNING_ON:
                messageId = R.string.bluetooth_turning_on;
                break;
            default:
                break;
        }

        setDeviceListGroup(preferenceScreen);
        removeAllDevices();
        mEmptyView.setText(messageId);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onBluetoothStateChanged(int bluetoothState) {
        super.onBluetoothStateChanged(bluetoothState);
        updateContent(bluetoothState, true);
    }

    @Override
    public void onScanningStateChanged(boolean started) {
        super.onScanningStateChanged(started);
        // Update options' enabled state
        getActivity().invalidateOptionsMenu();
    }

    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedDevice, int bondState) {
        setDeviceListGroup(getPreferenceScreen());
        removeAllDevices();
        updateContent(mLocalAdapter.getBluetoothState(), false);
    }

    private final View.OnKeyListener mDevicekeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                final CachedBluetoothDevice device = (CachedBluetoothDevice) v.getTag();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.bluetooth_unpair, null);
                Button button = (Button) view.findViewById(R.id.unpair_button);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        device.unpair();
                        if (unpair_dialog != null) {
                            unpair_dialog.cancel();
                        }
                    }
                });

                unpair_dialog = new AlertDialog.Builder(getActivity())
                        .setView(view)
                        .setTitle(
                                getActivity().getResources().getString(
                                        R.string.bluetooth_device_context_unpair)).show();
            }

            return false;
        }
    };

    /**
     * Add a listener, which enables the advanced settings icon.
     * 
     * @param preference the newly added preference
     */
    @Override
    void initDevicePreference(BluetoothDevicePreference preference) {
        CachedBluetoothDevice cachedDevice = preference.getCachedDevice();
        if (cachedDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            // Only paired device have an associated advanced settings screen
            // preference.setOnSettingsClickListener(mDeviceProfilesListener);
            preference.setOnkeyListener(mDevicekeyListener);
        }
    }

}
