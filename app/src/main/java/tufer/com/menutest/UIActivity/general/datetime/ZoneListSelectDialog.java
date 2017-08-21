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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import tufer.com.menutest.R;


public class ZoneListSelectDialog extends Dialog {

    private static final String TAG = "MSettings.ZoneListSelectDialog";

    private static final String KEY_ID = "id";

    private static final String KEY_DISPLAYNAME = "name";

    private static final String KEY_GMT = "gmt";

    private static final String KEY_OFFSET = "offset";

    private static final String XMLTAG_TIMEZONE = "timezone";

    private static final int HOURS_1 = 60 * 60000;

    private static final int UPDATE_TIMEZONE = 1;

    private static final int COUNT_SIZE = 5;

    private DateTimeSettings mDateTimeSettingActivity;

    private SimpleAdapter mTimezoneAdapter;

    private Handler mUpdateHandler;

    private ListView mZoneList;

    private TextView mTotalPage;

    private TextView mCurrentPage;

    private int mTotalPageCount;

    private int mCurrentPageCount;

    private int mDefault = 0;

    private int mTotalCount = 0;

    public ZoneListSelectDialog(DateTimeSettings dateTimeSettingActivity, Handler mHandler) {
        super(dateTimeSettingActivity);
        this.mDateTimeSettingActivity = dateTimeSettingActivity;
        this.mUpdateHandler = mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_zone_list);

        Window w = getWindow();
        DisplayMetrics outMetrics = new DisplayMetrics();
        w.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        Resources resources = mDateTimeSettingActivity.getResources();
        Drawable drawable = resources.getDrawable(R.drawable.dialog_bg);
        w.setBackgroundDrawable(drawable);
        w.setTitle("                                          "
                + mDateTimeSettingActivity.getResources().getString(R.string.timezone_select));

        int width = (int) (outMetrics.widthPixels * 0.5);
        int height = (int) (outMetrics.heightPixels * 0.6);
        w.setLayout(width, height);
        w.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams wl = w.getAttributes();
        w.setAttributes(wl);

        findViews();
        registerListeners();
    }

    private void findViews() {
        mTotalPage = (TextView) findViewById(R.id.page_total_count);
        mCurrentPage = (TextView) findViewById(R.id.page_current_count);

        mZoneList = (ListView) findViewById(R.id.timezone_select);
        mZoneList.setDividerHeight(0);
        mZoneList.setVerticalScrollBarEnabled(false);

        String[] from = new String[] {
                KEY_DISPLAYNAME, KEY_GMT
        };
        int[] to = new int[] {
                R.id.aboutItem, R.id.content
        };

        Log.d(TAG, "mDefault  " + mDefault);

        List<HashMap<String, String>> timezoneSortedList = getZones();
        mTotalCount = timezoneSortedList.size();

        mCurrentPageCount = mDefault / COUNT_SIZE + 1;
        mCurrentPage.setText("" + mCurrentPageCount);
        mTotalPageCount = timezoneSortedList.size() / COUNT_SIZE + 1;
        mTotalPage.setText("/" + mTotalPageCount);

        mTimezoneAdapter = new SimpleAdapter(mDateTimeSettingActivity, timezoneSortedList,
                R.layout.about_item_text, from, to);
        mZoneList.setAdapter(mTimezoneAdapter);
        mZoneList.setSelection(mDefault);
    }

    private void registerListeners() {
        mZoneList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map map = (Map) mZoneList.getItemAtPosition(position);
                AlarmManager alarm = (AlarmManager) mDateTimeSettingActivity
                        .getSystemService(Context.ALARM_SERVICE);
                alarm.setTimeZone((String) map.get(KEY_ID));
                mUpdateHandler.sendEmptyMessage(UPDATE_TIMEZONE);
                dismiss();
            }
        });

        mZoneList.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDefault = position;
                mCurrentPageCount = mDefault / COUNT_SIZE + 1;
                mCurrentPage.setText("" + mCurrentPageCount);
                Log.d(TAG, "onItemSelected mDefault, " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            int restCount = mTotalCount - mDefault;
            if (COUNT_SIZE < restCount) {
                mZoneList.setSelection(mDefault + COUNT_SIZE);
            } else {
                mZoneList.setSelection(mDefault + restCount);

            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            int restCount = mTotalCount - mDefault;
            if (COUNT_SIZE < restCount) {
                mZoneList.setSelection(mDefault - COUNT_SIZE);
            } else {
                mZoneList.setSelection(mDefault - restCount);
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
        }

        return super.onKeyUp(keyCode, event);
    }

    private List<HashMap<String, String>> getZones() {
        List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();
        long date = Calendar.getInstance().getTimeInMillis();
        XmlResourceParser xrp = null;
        try {
            xrp = mDateTimeSettingActivity.getResources().getXml(R.xml.timezones);
            while (xrp.next() != XmlResourceParser.START_TAG)
                continue;
            xrp.next();
            while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                    if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return myData;
                    }
                    xrp.next();
                }
                if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
                    String id = xrp.getAttributeValue(0);
                    String displayName = xrp.nextText();
                    addItem(myData, id, displayName, date);
                }
                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                    xrp.next();
                }
                xrp.next();
            }
            xrp.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to read timezones.xml file");
        } finally {
            if (xrp != null) {
                xrp.close();
            }
        }

        return myData;
    }

    protected void addItem(List<HashMap<String, String>> myData, String id, String displayName,
            long date) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY_ID, id);
        map.put(KEY_DISPLAYNAME, displayName);
        TimeZone tz = TimeZone.getTimeZone(id);
        int offset = tz.getOffset(date);
        int p = Math.abs(offset);
        StringBuilder name = new StringBuilder();
        name.append("GMT");

        if (offset < 0) {
            name.append('-');
        } else {
            name.append('+');
        }

        name.append(p / (HOURS_1));
        name.append(':');

        int min = p / 60000;
        min %= 60;

        if (min < 10) {
            name.append('0');
        }
        name.append(min);

        map.put(KEY_GMT, name.toString());
        map.put(KEY_OFFSET, String.valueOf(offset));

        if (id.equals(TimeZone.getDefault().getID())) {
            mDefault = myData.size();
        }

        myData.add(map);
    }

}
