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

package tufer.com.menutest.UIActivity.general.datetime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;


import com.mstar.android.MIntent;
import com.mstar.android.tvapi.common.TimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.factory.FactoryManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import tufer.com.menutest.R;

public class DateTimeSettings extends Activity {

    private static final String TAG = "DateTimeSettings";

    private static final String HOURS_12 = "12";

    private static final String HOURS_24 = "24";

    private DateTimeViewHolder mDateTimeViewHolder;

    private int mCurrentPosition = 0;

    private boolean isAutoDateTime;

    private boolean is24Hour;

    private TimerManager timerMgr = TvManager.getInstance().getTimerManager();
	  
	 static short[] readAddress1 = { 0x00 };
	 static short[] readAddress2 = { 0x10 };
	 static short[] readAddress3 = { 0x20 };
	 static short[] readAddress4 = { 0x40 };
	 static short[] readAddress5 = { 0x50 };
	 static short[] readAddress6 = { 0x60 };
	 static short[] buffer1={ 0 };
	 static short[] buffer2={ 0 };
	 static short[] buffer3={ 0 };
	 static short[] buffer4={ 0 };
	 static short[] buffer5={ 0 };
	 static short[] buffer6={ 0 };

    private String dateFormat;
	  
	  private static FactoryManager fm;

    private Handler updateDateAndTimeHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            
            Bundle data = msg.getData();
            dateFormat = data.getString("dateFormat");
            Settings.System
                    .putString(getContentResolver(), Settings.System.DATE_FORMAT, dateFormat);
            updateTimeAndDateDisplay();
        }
    };

    private Handler updateDateHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            
            Bundle data = msg.getData();
            String year = data.getString("year");
            String month = data.getString("month");
            String day = data.getString("day");
            onDateSet(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        }
    };

    private Handler updateTimeHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            
            Bundle data = msg.getData();
            String hour = data.getString("hour");
            String minute = data.getString("minute");
            onTimeSet(Integer.parseInt(hour), Integer.parseInt(minute));
        }
    };

    private Handler updateZoneTimeHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {                
                updateTimeAndDateDisplay();
                changeTimeZoneUpdataRTCTime();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.date_time_setting);
        fm = TvManager.getInstance().getFactoryManager();
        findViews();
        registerListeners();
    }
    

    private void findViews() {
        mDateTimeViewHolder = new DateTimeViewHolder(this);
        isAutoDateTime = getAutoState();
        Log.d(TAG, "isAutoDateTime, " + isAutoDateTime);
        mDateTimeViewHolder.mAutoTimeDateCheckBox.setChecked(isAutoDateTime);
        //if (isAutoDateTime) {
        //    mDateTimeViewHolder.mAutoTimeDateCheckBox.setChecked(true);
        //} else {
        //    mDateTimeViewHolder.mAutoTimeDateCheckBox.setChecked(false);
        //}

        is24Hour = is24Hour();
        Log.d(TAG, "is24Hour, " + is24Hour);
        mDateTimeViewHolder.mTimeFormatCheckBox.setChecked(is24Hour);
        //if (is24Hour) {
        //    mDateTimeViewHolder.mTimeFormatCheckBox.setChecked(true);
       // } else {
        //    mDateTimeViewHolder.mTimeFormatCheckBox.setChecked(false);
        //}

        updateTimeAndDateDisplay();
    }

    private void registerListeners() {
        mDateTimeViewHolder.mAutoTimeDateLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mDateTimeViewHolder.setNoneBackground();
                mCurrentPosition = 0;
                mDateTimeViewHolder.setBackground(mCurrentPosition);
                changeCheckedValue();
            }
        });

        mDateTimeViewHolder.mDateSettingsLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isAutoDateTime) {
                    mDateTimeViewHolder.setNoneBackground();
                    mCurrentPosition = 1;
                    mDateTimeViewHolder.setBackground(mCurrentPosition);
                    showSettingDialog();
                }
            }
        });

        mDateTimeViewHolder.mTimeSettingsLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isAutoDateTime) {
                    mDateTimeViewHolder.setNoneBackground();
                    mCurrentPosition = 2;
                    mDateTimeViewHolder.setBackground(mCurrentPosition);
                    showSettingDialog();
                }
            }
        });

        mDateTimeViewHolder.mTimeZoneSettingsLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isAutoDateTime) {
                    mDateTimeViewHolder.setNoneBackground();
                    mCurrentPosition = 3;
                    mDateTimeViewHolder.setBackground(mCurrentPosition);
                    showSettingDialog();
                }
            }
        });

        mDateTimeViewHolder.mTimeFormatSettingsLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDateTimeViewHolder.setNoneBackground();
                mCurrentPosition = 4;
                mDateTimeViewHolder.setBackground(mCurrentPosition);
                changeCheckedValue();
            }
        });

        mDateTimeViewHolder.mDateFormatSettingsLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDateTimeViewHolder.setNoneBackground();
                mCurrentPosition = 5;
                mDateTimeViewHolder.setBackground(mCurrentPosition);
                showSettingDialog();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            return true;
		}
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            dropDown();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            dropUp();
        }

        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            Log.d(TAG, "onKeyUp");
            showSettingDialog();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {


        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.i("tianky", "<<<<-----------------onResume--------------->>>>>");
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        timeFilter.addAction(Intent.ACTION_TIME_CHANGED);
        timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(mTimeChangeReceiver, timeFilter, null, null);
    }
   
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mTimeChangeReceiver);
    }

    private boolean getAutoState() {
        try {
            return Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME) > 0;
        } catch (SettingNotFoundException snfe) {
            Log.d(TAG, "can not find autoTime");
            return false;
        }
    }

    private boolean is24Hour() {
        return DateFormat.is24HourFormat(this);
    }

    private void setTVTime(long millitime) {
        Time time = new Time();
        time.set(millitime);
        time.month += 1;
        try {
            timerMgr.setClkTime(time, true);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    private void onDateSet(int year, int month, int day) {
		     Calendar c = Calendar.getInstance();
         int temp=0;
		     temp=(day/10)*16+(day%10);
		     buffer1[0]=(short) temp;
		     temp=((month+1)/10)*16+((month+1)%10);
		     buffer2[0]=(short) temp;
		     temp=((year%100)/10)*16+((year%100)%10);
		     buffer3[0]=(short) temp;
		     try {
			      fm.writeBytesToI2C(0x82, readAddress4, buffer1);
			      fm.writeBytesToI2C(0x82, readAddress5, buffer2);
			      fm.writeBytesToI2C(0x82, readAddress6, buffer3);
		      } catch (TvCommonException e) {
			   // TODO Auto-generated catch block
			     e.printStackTrace();
		     }

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
            setTVTime(when);
        }
        Log.d(TAG, "getTimeInMillis, " + c.getTimeInMillis());

        updateTimeAndDateDisplay();
    }

    public void onTimeSet(int hourOfDay, int minute) {
		    Calendar c = Calendar.getInstance();
        
        int temp=0;
		    try {		
		         temp=(minute/10)*16+(minute%10);
		         buffer1[0]=(short) temp;
		         temp=(hourOfDay/10)*16+(hourOfDay%10);
		         buffer2[0]=(short) temp;
		         fm.writeBytesToI2C(0x82, readAddress2, buffer1);
		         fm.writeBytesToI2C(0x82, readAddress3, buffer2);
		    } catch (TvCommonException e) {
			  // TODO Auto-generated catch block
			    e.printStackTrace();
		    }
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
            setTVTime(when);
        }
        updateTimeAndDateDisplay();
    }
    
    private void changeTimeZoneUpdataRTCTime() {
    	  final Calendar time = Calendar.getInstance();
    	  int temp=0;
    	  
    	  temp=(time.get(Calendar.SECOND)/10)*16+(time.get(Calendar.SECOND)%10);
    	  buffer1[0]=(short) temp;
    	  
    	  temp=(time.get(Calendar.MINUTE)/10)*16+(time.get(Calendar.MINUTE)%10);
    	  buffer2[0]=(short) temp;
    	 
    	  temp=(time.get(Calendar.HOUR_OF_DAY)/10)*16+(time.get(Calendar.HOUR_OF_DAY)%10);
    	  buffer3[0]=(short) temp;
    	  
    	  temp=(time.get(Calendar.DAY_OF_MONTH)/10)*16+(time.get(Calendar.DAY_OF_MONTH)%10);
    	  buffer4[0]=(short) temp;
    	 
    	  temp=((time.get(Calendar.MONTH)+1)/10)*16+((time.get(Calendar.MONTH)+1)%10);
        buffer5[0]=(short) temp;
       
        temp=((time.get(Calendar.YEAR)%100)/10)*16+((time.get(Calendar.YEAR)%100)%10);
        buffer6[0]=(short) temp;
        //System.out.println("tianky YEAR:" + time.get(Calendar.YEAR));
        try {
			      fm.writeBytesToI2C(0x82, readAddress1, buffer1);
			      fm.writeBytesToI2C(0x82, readAddress2, buffer2);
			      fm.writeBytesToI2C(0x82, readAddress3, buffer3);
			      fm.writeBytesToI2C(0x82, readAddress4, buffer4);
			      fm.writeBytesToI2C(0x82, readAddress5, buffer5);
			      fm.writeBytesToI2C(0x82, readAddress6, buffer6);
		      } catch (TvCommonException e) {
			   // TODO Auto-generated catch block
			     e.printStackTrace();
		     }
    }

    private void dropDown() {
        switch (mCurrentPosition) {
            case 0:
                if (isAutoDateTime) {
                    mCurrentPosition = 4;
                    mDateTimeViewHolder.mAutoTimeDateLayout
                            .setBackgroundResource(R.drawable.one_px);
                    mDateTimeViewHolder.mTimeFormatSettingsLayout
                            .setBackgroundResource(R.drawable.set_button);
                    mDateTimeViewHolder.mTimeFormatSettingsLayout.requestFocus();
//                    mDateTimeViewHolder.mTimeFormatCheckBox.requestFocus();
                } else {
                    mCurrentPosition = 1;
                    mDateTimeViewHolder.mAutoTimeDateLayout
                            .setBackgroundResource(R.drawable.one_px);
                    mDateTimeViewHolder.mDateSettingsLayout
                            .setBackgroundResource(R.drawable.set_button);
                    mDateTimeViewHolder.mDateSettingsLayout.requestFocus();
                }
                break;
            case 1:
                mCurrentPosition = 2;
                mDateTimeViewHolder.mDateSettingsLayout.setBackgroundResource(R.drawable.one_px);
                mDateTimeViewHolder.mTimeSettingsLayout
                        .setBackgroundResource(R.drawable.set_button);
                mDateTimeViewHolder.mTimeSettingsLayout.requestFocus();
                break;
            case 2:
                mCurrentPosition = 3;
                mDateTimeViewHolder.mTimeSettingsLayout.setBackgroundResource(R.drawable.one_px);
                mDateTimeViewHolder.mTimeZoneSettingsLayout
                        .setBackgroundResource(R.drawable.set_button);
                mDateTimeViewHolder.mTimeZoneSettingsLayout.requestFocus();
                break;
            case 3:
                mCurrentPosition = 4;
                mDateTimeViewHolder.mTimeZoneSettingsLayout
                        .setBackgroundResource(R.drawable.one_px);
                mDateTimeViewHolder.mTimeFormatSettingsLayout
                        .setBackgroundResource(R.drawable.set_button);
                mDateTimeViewHolder.mTimeFormatSettingsLayout.requestFocus();
                break;
            case 4:
                mCurrentPosition = 5;
                mDateTimeViewHolder.mTimeFormatSettingsLayout
                        .setBackgroundResource(R.drawable.one_px);
                mDateTimeViewHolder.mDateFormatSettingsLayout
                        .setBackgroundResource(R.drawable.set_button);
                mDateTimeViewHolder.mDateFormatSettingsLayout.requestFocus();
                break;
            default:
                break;
        }
    }

    private void dropUp() {
        switch (mCurrentPosition) {
            case 1:
                mCurrentPosition = 0;
                mDateTimeViewHolder.mDateSettingsLayout.setBackgroundResource(R.drawable.one_px);
                mDateTimeViewHolder.mAutoTimeDateLayout
                        .setBackgroundResource(R.drawable.set_button);
                mDateTimeViewHolder.mAutoTimeDateLayout.requestFocus();
                break;
            case 2:
                mCurrentPosition = 1;
                mDateTimeViewHolder.mTimeSettingsLayout.setBackgroundResource(R.drawable.one_px);
                mDateTimeViewHolder.mDateSettingsLayout
                        .setBackgroundResource(R.drawable.set_button);
                mDateTimeViewHolder.mDateSettingsLayout.requestFocus();
                break;
            case 3:
                mCurrentPosition = 2;
                mDateTimeViewHolder.mTimeZoneSettingsLayout
                        .setBackgroundResource(R.drawable.one_px);
                mDateTimeViewHolder.mTimeSettingsLayout
                        .setBackgroundResource(R.drawable.set_button);
                mDateTimeViewHolder.mTimeSettingsLayout.requestFocus();
                break;
            case 4:
                if (isAutoDateTime) {
                    mCurrentPosition = 0;
                    mDateTimeViewHolder.mTimeFormatSettingsLayout
                            .setBackgroundResource(R.drawable.one_px);
                    mDateTimeViewHolder.mAutoTimeDateLayout
                            .setBackgroundResource(R.drawable.set_button);
                    mDateTimeViewHolder.mAutoTimeDateLayout.requestFocus();
//                    mDateTimeViewHolder.mAutoTimeDateCheckBox.requestFocus();
                } else {
                    mCurrentPosition = 3;
                    mDateTimeViewHolder.mTimeZoneButton.requestFocus();
                    mDateTimeViewHolder.mTimeFormatSettingsLayout
                            .setBackgroundResource(R.drawable.one_px);
                    mDateTimeViewHolder.mTimeZoneSettingsLayout
                            .setBackgroundResource(R.drawable.set_button);
                    mDateTimeViewHolder.mTimeZoneSettingsLayout.requestFocus();
                }
                break;
            case 5:
                mCurrentPosition = 4;
                mDateTimeViewHolder.mTimeFormatCheckBox.requestFocus();
                mDateTimeViewHolder.mDateFormatSettingsLayout
                        .setBackgroundResource(R.drawable.one_px);
                mDateTimeViewHolder.mTimeFormatSettingsLayout
                        .setBackgroundResource(R.drawable.set_button);
                mDateTimeViewHolder.mTimeFormatSettingsLayout.requestFocus();
                break;
            default:
                break;
        }
    }

    private void updateTimeAndDateDisplay() {
        java.text.DateFormat shortDateFormat = DateFormat.getDateFormat(this);
        Date now = Calendar.getInstance().getTime();
        String stringDateFormat = shortDateFormat.format(now);
        mDateTimeViewHolder.mDateButton.setText(shortDateFormat.format(now));
        mDateTimeViewHolder.mTimeButton.setText(DateFormat.getTimeFormat(this).format(now));
        mDateTimeViewHolder.mTimeZoneButton.setText(getTimeZoneText());
        mDateTimeViewHolder.mTimeFormatButton.setText(stringDateFormat);
    }

    private void saveAutoTimeValue() {
        Log.d(TAG, "saveAutoTimeValue, isAutoDateTime, " + isAutoDateTime);
        Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME, isAutoDateTime ? 1
                : 0);
        Intent timeChanged = new Intent(MIntent.ACTION_TV_AUTO_TIME_SYNC);
        timeChanged.putExtra(MIntent.EXTRA_KEY_TV_AUTO_TIME, !isAutoDateTime);
        sendBroadcast(timeChanged);
    }

    private void saveTimeFormatValue() {
        Log.d(TAG, "saveTimeFormatValue, is24Hour, " + is24Hour);
        Settings.System.putString(getContentResolver(), Settings.System.TIME_12_24,
                is24Hour ? HOURS_24 : HOURS_12);
        updateTimeAndDateDisplay();

        Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
        sendBroadcast(timeChanged);
    }

    private String getTimeZoneText() {
        TimeZone tz = java.util.Calendar.getInstance().getTimeZone();    
        //SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ, zzzz");
        //sdf.setTimeZone(tz);

        boolean daylight = tz.inDaylightTime(new Date());
        if (daylight) {
            try {
                timerMgr.setDaylightSavingState(true);
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
        } else {
            try {
                timerMgr.setDaylightSavingState(false);
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
        }
         
        StringBuilder sb = new StringBuilder();
        sb.append(formatOffset(tz.getRawOffset())).append(", ")
                .append(tz.getDisplayName(daylight, TimeZone.LONG));        
        return sb.toString();
    }
   
    private char[] formatOffset(int off) {
        off = off / 1000 / 60;

        char[] buf = new char[9];
        buf[0] = 'G';
        buf[1] = 'M';
        buf[2] = 'T';

        if (off < 0) {
            buf[3] = '-';
            off = -off;
        } else {
            buf[3] = '+';
        }

        int hours = off / 60;
        int minutes = off % 60;

        buf[4] = (char) ('0' + hours / 10);
        buf[5] = (char) ('0' + hours % 10);

        buf[6] = ':';

        buf[7] = (char) ('0' + minutes / 10);
        buf[8] = (char) ('0' + minutes % 10);

        return buf;
    }

    private BroadcastReceiver mTimeChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateTimeAndDateDisplay();       
        }
    };

    private void showSettingDialog() {
        switch (mCurrentPosition) {
            case 0:
                changeCheckedValue();
                break;
            case 1:
                if (!isAutoDateTime) {
                    DateSettingDialog dateSettingDialog = new DateSettingDialog(
                            DateTimeSettings.this, updateDateHandler);
                    dateSettingDialog.show();
                }
                break;
            case 2:
                if (!isAutoDateTime) {
                    TimeSettingDialog timeSettingDialog = new TimeSettingDialog(
                            DateTimeSettings.this, updateTimeHandler, is24Hour);
                    timeSettingDialog.show();
                }
                break;
            case 3:
                if (!isAutoDateTime) {
                    ZoneListSelectDialog zoneListSelectDialog = new ZoneListSelectDialog(
                            DateTimeSettings.this, updateZoneTimeHandler);
                    zoneListSelectDialog.show();
                }
                break;
            case 4:
                changeCheckedValue();
                break;
            case 5:
                DateFormatSelectDialog dateFormatSelectDialog = new DateFormatSelectDialog(
                        DateTimeSettings.this, updateDateAndTimeHandler);
                dateFormatSelectDialog.show();
                break;
            default:
                break;
        }
    }

    private void changeCheckedValue() {
        if (mCurrentPosition == 0) {
            isAutoDateTime = !isAutoDateTime;
            mDateTimeViewHolder.mAutoTimeDateCheckBox.setChecked(isAutoDateTime);
            saveAutoTimeValue();
        } else if (mCurrentPosition == 4) {
            is24Hour = !is24Hour;
            mDateTimeViewHolder.mTimeFormatCheckBox.setChecked(is24Hour);
            saveTimeFormatValue();
        }
    }
}
