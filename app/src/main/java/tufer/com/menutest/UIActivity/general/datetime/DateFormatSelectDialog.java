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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import tufer.com.menutest.R;


public class DateFormatSelectDialog extends Dialog {

    private final static String NAME = "share_pres";

    private final static String DATEFORMAT_INDEX = "date_format_index";

    private DateTimeSettings mDateTimeSettingActivity;

    private List<Map<String, Object>> dateFormatDatas = new ArrayList<Map<String, Object>>();

    private SimpleAdapter dateFormatAdapter;

    private String dateFormatString;

    private int mDateFormatIndex;

    private Handler onItemClickHandler;

    private String[] dateFormatStrings = {
            "MM-dd-yyyy", "dd-MM-yyyy", "yyyy-MM-dd"
    };

    public DateFormatSelectDialog(DateTimeSettings dateTimeSettingActivity, Handler handler) {
        super(dateTimeSettingActivity);
        this.mDateTimeSettingActivity = dateTimeSettingActivity;
        this.onItemClickHandler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_format_list);

        Window w = getWindow();
        DisplayMetrics outMetrics = new DisplayMetrics();
        w.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        Resources resources = mDateTimeSettingActivity.getResources();
        Drawable drawable = resources.getDrawable(R.drawable.set_bg);
        w.setBackgroundDrawable(drawable);
        w.setTitle("                     "
                + mDateTimeSettingActivity.getResources().getString(R.string.choose_date_format));

        int width = (int) (outMetrics.widthPixels * 0.3);
        int height = (int) (outMetrics.heightPixels * 0.4);
        w.setLayout(width, height);
        w.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams wl = w.getAttributes();
        w.setAttributes(wl);

        getDateFormat();
        findViews();
    }

    private void findViews() {
        ListView dateFormat = (ListView) findViewById(R.id.date_format_list);
        dateFormat.setDivider(null);

        final String items[] = mDateTimeSettingActivity.getResources().getStringArray(
                R.array.date_format_values);

        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("txtItem", items[0]);
        if (mDateFormatIndex == 0) {
            map1.put("imgItem", R.drawable.selected_language);
        } else {
            map1.put("imgItem", R.drawable.unselected);
        }
        dateFormatDatas.add(map1);

        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("txtItem", items[1]);
        if (mDateFormatIndex == 1) {
            map2.put("imgItem", R.drawable.selected_language);
        } else {
            map2.put("imgItem", R.drawable.unselected);
        }
        dateFormatDatas.add(map2);

        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("txtItem", items[2]);
        if (mDateFormatIndex == 2) {
            map3.put("imgItem", R.drawable.selected_language);
        } else {
            map3.put("imgItem", R.drawable.unselected);
        }
        dateFormatDatas.add(map3);

        dateFormatAdapter = new SimpleAdapter(mDateTimeSettingActivity, dateFormatDatas,
                R.layout.date_format_item_list, new String[] {
                        "txtItem", "imgItem"
                }, new int[] {
                        R.id.date_format_item, R.id.date_format_item_iv
                });

        dateFormat.setAdapter(dateFormatAdapter);
        dateFormat.requestFocus();
        dateFormat.setSelection(mDateFormatIndex);

        dateFormat.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeRadioImg(mDateFormatIndex, false);
                changeRadioImg(position, true);
                mDateFormatIndex = position;
                commitDateFormat();
                dateFormatString = dateFormatStrings[position];
                Message msg = new Message();
                Bundle mBundle = new Bundle();
                mBundle.putString("dateFormat", dateFormatString);
                msg.setData(mBundle);
                onItemClickHandler.sendMessage(msg);
                dismiss();
            }
        });
    }

    private void changeRadioImg(int selectedItem, boolean b) {
        SimpleAdapter la = dateFormatAdapter;
        HashMap<String, Object> map = (HashMap<String, Object>) la.getItem(selectedItem);
        if (b) {
            map.put("imgItem", R.drawable.selected_language);
        } else {
            map.put("imgItem", R.drawable.unselected);
        }
        la.notifyDataSetChanged();
    }

    private void commitDateFormat() {
        SharedPreferences preference = mDateTimeSettingActivity.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putInt(DATEFORMAT_INDEX, mDateFormatIndex);
        edit.commit();
    }

    private void getDateFormat() {
        SharedPreferences preference = mDateTimeSettingActivity.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        mDateFormatIndex = preference.getInt(DATEFORMAT_INDEX, 2);
    }

}
