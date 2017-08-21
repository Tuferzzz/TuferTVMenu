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

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import tufer.com.menutest.R;
import tufer.com.menutest.Util.DateUtil;
import tufer.com.menutest.Util.InvalidDateFormatException;


public class DateSettingDialog extends Dialog {

    private static final int YEAR_OF_MONTH_MAX = 12;

    private static final int YEAR_OF_MONTH_MIN = 1;

    private DateTimeSettings mDateTimeSettings;

    private Handler mUpdateDateHandler;

    private Button mConfirmButton;

    private Button mCancleButton;

    private EditText mYearEditText;

    private EditText mMonthEditText;

    private EditText mDayEditText;

    public DateSettingDialog(DateTimeSettings dateTimeSettings, Handler handler) {
        super(dateTimeSettings);
        this.mDateTimeSettings = dateTimeSettings;
        this.mUpdateDateHandler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_setting);

        Window w = getWindow();

        Resources resources = mDateTimeSettings.getResources();
        Drawable drawable = resources.getDrawable(R.drawable.dialog_bg);
        w.setBackgroundDrawable(drawable);
        w.setTitle(null);

        Display display = w.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = (int) (point.x * 0.5);
        int height = (int) (point.y * 0.6);
        w.setLayout(width, height);
        w.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams wl = w.getAttributes();
        w.setAttributes(wl);

        findViews();
        registerListeners();
    }

    private void findViews() {
        mYearEditText = (EditText) findViewById(R.id.year_et);
        mMonthEditText = (EditText) findViewById(R.id.month_et);
        mDayEditText = (EditText) findViewById(R.id.day_et);

        final Calendar c = Calendar.getInstance();
        mYearEditText.setText(c.get(Calendar.YEAR) + "");
        mMonthEditText.setText(c.get(Calendar.MONTH) + "");
        mMonthEditText.setText(DateUtil.getMonth(null, "m"));
        mDayEditText.setText(c.get(Calendar.DAY_OF_MONTH) + "");

        mConfirmButton = (Button) findViewById(R.id.setting_date_ok);
        mCancleButton = (Button) findViewById(R.id.cannel_date_setting);
    }

    private void registerListeners() {
        mConfirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String yearString = mYearEditText.getText().toString();
                if ("".equals(yearString) || yearString.length() != 4) {
                    if (!"".equals(yearString)) {
                        mYearEditText.setText("");
                    }
                    mYearEditText.setHint(mDateTimeSettings.getString(R.string.correct_year));
                    mYearEditText.requestFocus();

                    return;
                }

                if (yearString != null && !"".equals(yearString)) {
                    if (Integer.parseInt(yearString) > 2038 || Integer.parseInt(yearString) < 1970) {
                        if (!"".equals(yearString)) {
                            mYearEditText.setText("");
                        }
                        mYearEditText.setHint(mDateTimeSettings.getString(R.string.scope_year));
                        mYearEditText.requestFocus();
                        return;
                    }
                }

                String monthString = mMonthEditText.getText().toString();
                // fix mantis bug 0328507
                if (TextUtils.isEmpty(monthString)) {
                    mMonthEditText.setText("");
                    mMonthEditText.setHint(mDateTimeSettings.getString(R.string.correct_month));
                    mMonthEditText.requestFocus();
                    return;
                }

                String dayString = mDayEditText.getText().toString();
                if (TextUtils.isEmpty(dayString)) {
                    mDayEditText.setHint(mDateTimeSettings.getString(R.string.correct_day));
                    mDayEditText.requestFocus();
                    return;
                } else {
                    int day = Integer.parseInt(dayString);
                    if (day <= 0 || day > 31) {
                        mDayEditText.setText("");
                        mDayEditText.setHint(mDateTimeSettings.getString(R.string.correct_day));
                        mDayEditText.requestFocus();
                        return;
                    }
                }

                String dateString = yearString + monthString + dayString;
                try {
                    DateUtil.validate(dateString);
                } catch (InvalidDateFormatException e) {
                    if (!"".equals(dayString)) {
                        mDayEditText.setText("");
                    }
                    mDayEditText.setHint(mDateTimeSettings.getString(R.string.correct_day));
                    mDayEditText.requestFocus();
                    return;
                }

                Message msg = new Message();
                Bundle mBundle = new Bundle();
                mBundle.putString("year", yearString);
                mBundle.putString("month", Integer.toString(Integer.parseInt(monthString) - 1));
                mBundle.putString("day", dayString);
                msg.setData(mBundle);
                mUpdateDateHandler.sendMessage(msg);
                dismiss();
            }
        });

        mCancleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mYearEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mYearEditText.selectAll();
                }
            }
        });

        mMonthEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mMonthEditText.selectAll();
                }
            }
        });

        mDayEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mDayEditText.selectAll();
                }
            }
        });

        mYearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String yearString = mYearEditText.getText().toString();
                if ("".equals(yearString)) {
                    mYearEditText.setHint(mDateTimeSettings.getString(R.string.correct_year));
                    mYearEditText.requestFocus();
                    return;
                }
                if (yearString != null && !"".equals(yearString) && yearString.length() == 4) {
                    if (Integer.parseInt(yearString) > 2038 || Integer.parseInt(yearString) < 1970) {
                        if (!"".equals(yearString)) {
                            mYearEditText.setText("");
                        }
                        mYearEditText.setHint(mDateTimeSettings.getString(R.string.scope_year));
                        mYearEditText.requestFocus();
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mMonthEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                String monthString = text.toString().trim();
                String yearStrng = mYearEditText.getText().toString().trim();
                if ("".equals(yearStrng)) {
                    if (!"".equals(monthString)) {
                        mMonthEditText.setText("");
                    }
                    mYearEditText.setHint(mDateTimeSettings.getString(R.string.correct_year));
                    mYearEditText.requestFocus();
                    return;
                } else {
                    if (Integer.parseInt(yearStrng) == 2038) {
                        if (!monthString.equals("") && Integer.parseInt(monthString) > 1) {
                            mMonthEditText.setText("");
                            mMonthEditText.setHint(mDateTimeSettings
                                    .getString(R.string.correct_month));
                            Toast.makeText(mDateTimeSettings, R.string.system_support_max_date,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }

                if (monthString.length() != 0) {
                    int month = Integer.parseInt(monthString);
                    if (month < YEAR_OF_MONTH_MIN || month > YEAR_OF_MONTH_MAX) {
                        mMonthEditText.setText("");
                        mMonthEditText.setHint(mDateTimeSettings.getString(R.string.correct_month));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDayEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                String dayString = text.toString().trim();
                String monthString = mMonthEditText.getText().toString().trim();
                String yearString = mYearEditText.getText().toString().trim();
                if (yearString.length() != 4) {
                    if (!"".equals(dayString)) {
                        mDayEditText.setText("");
                        mDayEditText.setHint(mDateTimeSettings.getString(R.string.correct_day));
                    }
                    if (!"".equals(yearString)) {
                        mYearEditText.setText("");
                    }
                    mYearEditText.setHint(mDateTimeSettings.getString(R.string.correct_year));
                    mYearEditText.requestFocus();
                    return;
                }
                if ("".equals(monthString)) {
                    if (!"".equals(dayString)) {
                        mDayEditText.setText("");
                    }
                    mMonthEditText.setHint(mDateTimeSettings.getString(R.string.correct_month));
                    mMonthEditText.requestFocus();
                    return;
                }

                if (dayString.length() != 0) {
                    if (Integer.parseInt(dayString) == 0) {
                        mDayEditText.setText("");
                        mDayEditText.setHint(mDateTimeSettings.getString(R.string.correct_day));
                    }
                    if (Integer.parseInt(yearString) == 2038 && Integer.parseInt(monthString) == 1) {
                        if (!dayString.equals("") && Integer.parseInt(dayString) > 18) {
                            mDayEditText.setText("");
                            mDayEditText.setHint(mDateTimeSettings.getString(R.string.correct_day));
                            Toast.makeText(mDateTimeSettings, R.string.system_support_max_date,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    String dateString = yearString + monthString + dayString;
                    try {
                        DateUtil.validate(dateString);
                    } catch (InvalidDateFormatException e) {
                        if (!"".equals(dateString)) {
                            mDayEditText.setText("");
                        }
                        mDayEditText.setHint(mDateTimeSettings.getString(R.string.correct_day));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

}
