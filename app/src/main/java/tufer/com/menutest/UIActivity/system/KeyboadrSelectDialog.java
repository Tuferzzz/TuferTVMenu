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

package tufer.com.menutest.UIActivity.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;


public class KeyboadrSelectDialog extends Dialog {

    // show all input method
    private ListView mKeyboardListView;

    // the current using input method
    private int balanceIndex = 0;

    // get all input method
    private List<InputMethodInfo> mInputMethodProperties;

    // the default input method
    private String mLastInputMethodId;

    private InputMethodAndLanguageSettingsActivity mSettingActivity;

    private List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

    private SimpleAdapter inputMethodAdapter;

    private int select;

    private int count = 0;

    public KeyboadrSelectDialog(InputMethodAndLanguageSettingsActivity languageSettingActivity) {
        super(languageSettingActivity);
        this.mSettingActivity = languageSettingActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputmethod_setting);

        Window w = getWindow();
        DisplayMetrics outMetrics = new DisplayMetrics();
        w.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        Resources resources = mSettingActivity.getResources();
        Drawable drawable = resources.getDrawable(R.drawable.set_bg);
        w.setBackgroundDrawable(drawable);
        w.setTitle("                  "
                + mSettingActivity.getResources().getString(R.string.keyboard_setting));

        int width = (int) (outMetrics.widthPixels * 0.3);
        int height = (int) (outMetrics.heightPixels * 0.4);
        w.setLayout(width, height);
        w.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams wl = w.getAttributes();
        w.setAttributes(wl);

        findViews();
        registerListeners();
    }

    /**
     * init compontent.
     */
    private void findViews() {
        mKeyboardListView = (ListView) findViewById(R.id.language_select_list);
        mKeyboardListView.setDivider(null);

        onCreateIMM();

        inputMethodAdapter = new SimpleAdapter(mSettingActivity, items,
                R.layout.date_format_item_list, new String[] {
                        "nameItem", "imgItem"
                }, new int[] {
                        R.id.date_format_item, R.id.date_format_item_iv
                });
        mKeyboardListView.setAdapter(inputMethodAdapter);
        mKeyboardListView.setSelection(select);
    }

    private void registerListeners() {
        mKeyboardListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeRadioImg(balanceIndex, false);
                changeRadioImg(position, true);
                balanceIndex = position;
                settingInputMethod(position);
                dismiss();
            }
        });
    }

    /**
     * setting input method.
     */
    private void settingInputMethod(int position) {
        InputMethodInfo property = mInputMethodProperties.get(position);
        String id = property.getId();

        Settings.Secure.putString(mSettingActivity.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD, id != null ? id : mLastInputMethodId);
    }

    /**
     * get the default input method and all input methods.
     */
    private void onCreateIMM() {
        InputMethodManager imm = (InputMethodManager) mSettingActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        mInputMethodProperties = imm.getInputMethodList();

        // the id of default input method
        mLastInputMethodId = Settings.Secure
                .getString(mSettingActivity.getContentResolver(),
                        Settings.Secure.DEFAULT_INPUT_METHOD);

        count = (mInputMethodProperties == null ? 0 : mInputMethodProperties.size());
        for (int i = 0; i < count; ++i) {
            InputMethodInfo property = mInputMethodProperties.get(i);
            // the id of input method
            String id = property.getId();
            // the package of input method
            String packageName = property.getPackageName();
            // the name of input method
            CharSequence label = property.loadLabel(mSettingActivity.getPackageManager());
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("nameItem", label);
            boolean isDefault = isDefaultInputMethod(mLastInputMethodId, id);
            if (isDefault) {
                map.put("imgItem", R.drawable.selected);
                select = i;
            } else {
                map.put("imgItem", R.drawable.unselected);
            }
            items.add(map);
        }
    }

    /**
     * change the background of radio button.
     */
    private void changeRadioImg(int selectedItem, boolean b) {
        SimpleAdapter la = inputMethodAdapter;
        HashMap<String, Object> map = (HashMap<String, Object>) la.getItem(selectedItem);
        if (b) {
            map.put("imgItem", R.drawable.selected);
        } else {
            map.put("imgItem", R.drawable.unselected);
        }
        la.notifyDataSetChanged();
    }

    /**
     * whether is default input method.
     */
    private boolean isDefaultInputMethod(String default_method, String current_method) {
        if (default_method.equals(current_method)) {
            return true;
        } else {
            return false;
        }
    }

}
