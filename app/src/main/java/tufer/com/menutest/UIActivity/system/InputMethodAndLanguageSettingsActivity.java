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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;
import tufer.com.menutest.Util.TVRootApp;


public class InputMethodAndLanguageSettingsActivity extends Activity {

    private static final String TAG = "InputMethodAndLanguageSettingsActivity";

    private final static int LANGUAGE_SETTING = 0;

    private final static int KEYBOARD_SETTING = 1;

    private ListView mListView;

    //private ListView mLanguage_setting_ls;

    // get all input method
    List<InputMethodInfo> mInputMethodProperties;

    // the default input method
    String mLastInputMethodId;

    private boolean flag = false;

    private List<String> inputMethodID = new ArrayList<String>();

    private List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

    private List<Map<String, Object>> inputMethodSetting = new ArrayList<Map<String, Object>>();

    List<Map<String, Object>> inputMethodLists = new ArrayList<Map<String, Object>>();

    private SimpleAdapter languageAdapter;

    //private SimpleAdapter inputMethodSettingAdapter;

    private int count = 0;

    int select = 0;

    //public static boolean isChangeLanguage=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_input_setting);
        //isChangeLanguage=false;
        findView();
        registerListener();
    }

    /**
     * init component.
     */
    private void findView() {
        mListView = (ListView) findViewById(R.id.language_imput_ls);
        mListView.setDivider(null);

//        mLanguage_setting_ls = (ListView) findViewById(R.id.language_setting_ls);
//        mLanguage_setting_ls.setDivider(null);
        // add the first data
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("nameItem", getString(R.string.language_setting));
        map1.put("imgItem", R.drawable.set_drop);
        items.add(map1);

        // add the second data
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("nameItem", getString(R.string.keyboard_setting));
        map2.put("imgItem", R.drawable.set_drop);
        items.add(map2);

        languageAdapter = new SimpleAdapter(this, items, R.layout.date_format_item_list,
                new String[] {
                        "nameItem", "imgItem"
                }, new int[] {
                        R.id.date_format_item, R.id.date_format_item_iv
                });
        mListView.setAdapter(languageAdapter);

        onCreateIMM();
//        inputMethodSettingAdapter = new SimpleAdapter(this, inputMethodSetting,
//                R.layout.date_format_item_list, new String[] {
//                        "nameItem", "imgItem"
//                }, new int[] {
//                        R.id.date_format_item, R.id.date_format_item_iv
//                });
//        mLanguage_setting_ls.setAdapter(inputMethodSettingAdapter);
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        Toast.makeText(this,isChangeLanguage+"",Toast.LENGTH_SHORT).show();
//        if(event.getAction()==KeyEvent.KEYCODE_BACK&&!isChangeLanguage){
//            Toast.makeText(this,isChangeLanguage+":::::::::::::::",Toast.LENGTH_SHORT).show();
//            MainActivity.myMainActivity.finish();
//        }
//        return super.dispatchKeyEvent(event);
//    }

    private void registerListener() {
        // select language and modify the default input method
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case LANGUAGE_SETTING:
                        LanguageSelectDialog selectDialog = new LanguageSelectDialog(
                                InputMethodAndLanguageSettingsActivity.this);
                        selectDialog.show();
                        break;
                    case KEYBOARD_SETTING:
                        KeyboadrSelectDialog keyboadrSelectDialog = new KeyboadrSelectDialog(
                                InputMethodAndLanguageSettingsActivity.this);
                        keyboadrSelectDialog.show();
                        break;
                }
            }
        });

        // modify input methods' setting
//        mLanguage_setting_ls.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String activityName = inputMethodID.get(position);
//                String packageName = activityName.substring(0, activityName.lastIndexOf("."));
//                int slash = activityName.indexOf("/");
//                if (slash > 0) {
//                    packageName = activityName.substring(0, slash);
//                    activityName = activityName.substring(slash + 1);
//                }
//
//                if (activityName.length() > 0) {
//                    Intent i = new Intent(Intent.ACTION_MAIN);
//                    i.setClassName(packageName, activityName);
//                    startActivity(i);
//                }
//            }
//        });
    }

    /**
     * get the default input method and all input methods.
     */
    private void onCreateIMM() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodProperties = imm.getInputMethodList();

        // the id of default input method
        mLastInputMethodId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);

        count = (mInputMethodProperties == null ? 0 : mInputMethodProperties.size());
        for (int i = 0; i < count; ++i) {
            InputMethodInfo property = mInputMethodProperties.get(i);
            // the id of input method
            String id = property.getId();
            // the package of input method
            String packageName = property.getPackageName();
            // the name of input method
            CharSequence label = property.loadLabel(getPackageManager());
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("nameItem", label + getString(R.string.setting));
            map.put("imgItem", R.drawable.set_drop);
            HashMap<String, Object> inputMap = new HashMap<String, Object>();
            inputMap.put("nameItem", label + getString(R.string.setting));
            boolean isDefault = isDefaultInputMethod(mLastInputMethodId, id);
            if (isDefault) {
                inputMap.put("imgItem", R.drawable.selected);
                select = i;
            } else {
                inputMap.put("imgItem", R.drawable.unselected);
            }

            // If setting activity is available, add a setting.
            if (null != property.getSettingsActivity()) {
                String settingsActivity = property.getSettingsActivity();
                if (settingsActivity.lastIndexOf("/") < 0) {
                    settingsActivity = property.getPackageName() + "/" + settingsActivity;
                    Log.d(TAG, "settingsActivity, " + settingsActivity);
                }
                inputMethodID.add(settingsActivity);
            }
            inputMethodLists.add(inputMap);
            inputMethodSetting.add(map);
        }
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
    @Override
    protected void onStop() {
        MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_SYSTEM);
        super.onStop();
    }


}
