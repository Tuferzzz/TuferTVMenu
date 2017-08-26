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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import android.app.ActivityManagerNative;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumLanguage;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;
import tufer.com.menutest.Util.TvIntent;

public class LanguageSelectDialog extends Dialog {

    private static final String TAG = "LanguageSelectDialog";

    private ListView mLanguageListView;

    private int mBalanceIndex;

    private int mSelect;

    private String keyboadr;

    private InputMethodAndLanguageSettingsActivity mLanguageSettingActivity;

    private ArrayAdapter<String> mLanguageAdapter;

    private String mCountry;

    private String[] mShowCountry;

    private String[] mCountries;

    private Locale mCurrentLocale;

    private String mLocaleCountry;

    private ArrayList<Locale> mCurrentLocales = new ArrayList<Locale>();

    public LanguageSelectDialog(InputMethodAndLanguageSettingsActivity languageSettingActivity) {
        super(languageSettingActivity);
        this.mLanguageSettingActivity = languageSettingActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_setting);

        Window w = getWindow();
        Resources resources = mLanguageSettingActivity.getResources();
        Drawable drawable = resources.getDrawable(R.drawable.dialog_bg);
        w.setBackgroundDrawable(drawable);
        w.setTitle(null);

        Point point = new Point();
        Display display = w.getWindowManager().getDefaultDisplay();
        display.getSize(point);
        int width = (int) (point.x * 0.3);
        int height = (int) (point.y * 0.5);
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
        Configuration conf = mLanguageSettingActivity.getResources().getConfiguration();

        mCurrentLocale = conf.locale;
        mLocaleCountry = mCurrentLocale.getCountry();
        Log.d(TAG, "localeCountry, " + mLocaleCountry);

        mCountry = readValue("/system/build.prop", "persist.sys.country");
        Log.d(TAG, "mCountry, " + mCountry);

        mLanguageListView = (ListView) findViewById(R.id.language_select_list);
        mLanguageListView.setDivider(null);

        getCountryList();
        Log.d(TAG, "mBalanceIndex, " + mBalanceIndex);
        getLanguageList();
        Log.d(TAG, "mCountries.length, " + mCountries.length);

        mLanguageAdapter = new ArrayAdapter<String>(mLanguageSettingActivity,
                R.layout.list_item_city_select, mCountries);
        mLanguageListView.setDividerHeight(0);
        mLanguageListView.setItemsCanFocus(false);
        mLanguageListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mLanguageListView.setAdapter(mLanguageAdapter);
        Log.d(TAG, "mSelect, " + mSelect);
        mLanguageListView.setItemChecked(mSelect, true);
        mLanguageListView.setSelection(mSelect);
    }

    private void setLanguage(Locale locale) {
        int value = 0;
        String language = locale.getLanguage();
        if (language.equals("zh")) {
            value = EnumLanguage.E_CHINESE.ordinal();
        } else if (language.equals("en")) {
            value = EnumLanguage.E_ENGLISH.ordinal();
        } else if (language.equals("ja")) {
            value = EnumLanguage.E_JAPANESE.ordinal();
        } else if (language.equals("fi")) {
            value = EnumLanguage.E_FINNISH.ordinal();
        } else if (language.equals("da")) {
            value = EnumLanguage.E_DANISH.ordinal();
        } else if (language.equals("nb")) {
            value = EnumLanguage.E_NORWEGIAN.ordinal();
        } else if (language.equals("sv")) {
            value = EnumLanguage.E_SWEDISH.ordinal();
        } else {
            value = EnumLanguage.E_CHINESE.ordinal();
        }

        TvManager tvManager = TvManager.getInstance();
        try {
            Intent intent = new Intent("com.android.settings");
            intent.putExtra("lang", value);
            getContext().sendBroadcast(intent);
            //tvManager.setLanguage(EnumLanguage.values()[value]);
            tvManager.setLanguage(value);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void registerListeners() {
        mLanguageListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //InputMethodAndLanguageSettingsActivity.isChangeLanguage=true;
                keyboadr = Settings.Secure.getString(mLanguageSettingActivity.getContentResolver(),
                        Settings.Secure.DEFAULT_INPUT_METHOD);
                Log.d(TAG, "keyboadr---1------" + keyboadr);
                Locale locale = mCurrentLocales.get(position);
                try {
                    IActivityManager am = ActivityManagerNative.getDefault();
                    Configuration config = am.getConfiguration();
                    config.locale = locale;
                    setLanguage(locale);

                    // indicate this isn't some passing default - the user wants
                    // this remembered
                    config.userSetLocale = true;
                    am.updateConfiguration(config);

                    // Trigger the dirty bit for the Settings Provider.
                    BackupManager.dataChanged("com.android.providers.settings");
                } catch (RemoteException e) {
                    // Intentionally left blank
                }

                //fix bug 0357268
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.currentThread().sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Settings.Secure.putString(mLanguageSettingActivity.getContentResolver(),
                                Settings.Secure.DEFAULT_INPUT_METHOD, keyboadr);

                    }
                }).start();
                dismiss();
                MainActivity.myMainActivity.finish();

                //mLanguageSettingActivity.finish();
//                PackageManager manager = mLanguageSettingActivity.getPackageManager();
//                Intent openApp = manager.getLaunchIntentForPackage("tufer.com.menutest");
//                mLanguageSettingActivity.startActivity(openApp);
//
////                Intent intent = new Intent(TvIntent.MENU);
////                if (intent.resolveActivity(mLanguageSettingActivity.getPackageManager()) != null) {
////                    mLanguageSettingActivity.startActivity(intent);
////                }
//                mLanguageSettingActivity.finish();
            }
        });
    }

    private void getCountryList() {
        String[] southeastAsia = mLanguageSettingActivity.getResources().getStringArray(
                R.array.southeast_asia);
        String[] russia = mLanguageSettingActivity.getResources().getStringArray(R.array.russia);
        String[] middleEast = mLanguageSettingActivity.getResources().getStringArray(
                R.array.middle_east);
        String[] eastAsia = mLanguageSettingActivity.getResources().getStringArray(
                R.array.east_asia);
        String[] japan = mLanguageSettingActivity.getResources().getStringArray(R.array.japan);
        String[] Nordic = mLanguageSettingActivity.getResources().getStringArray(R.array.Nordic);
        String[] sourthAmerice = mLanguageSettingActivity.getResources().getStringArray(R.array.south_america);

        if (mCountry != null) {
            if (mCountry.equals("VC")) {
                mBalanceIndex = 5;
                return;
            }
            for (int i = 0; i < southeastAsia.length; i++) {
                if (southeastAsia[i].equals(mCountry)) {
                    mShowCountry = southeastAsia;
                    mBalanceIndex = 0;
                    return;
                }
            }

            for (int j = 0; j < russia.length; j++) {
                if (russia[j].equals(mCountry)) {
                    mShowCountry = russia;
                    mBalanceIndex = 1;
                    return;
                }
            }
            for (int j2 = 0; j2 < middleEast.length; j2++) {
                if (middleEast[j2].equals(mCountry)) {
                    mShowCountry = middleEast;
                    mBalanceIndex = 2;
                    return;
                }
            }
            for (int k = 0; k < eastAsia.length; k++) {
                if (eastAsia[k].endsWith(mCountry)) {
                    mShowCountry = eastAsia;
                    mBalanceIndex = 3;
                    return;
                }
            }

            for (int k = 0; k < Nordic.length; k++) {
                if (Nordic[k].endsWith(mCountry)) {
                    mShowCountry = Nordic;
                    mBalanceIndex = 6;
                    return;
                }
            }
            
            for (int k = 0; k < sourthAmerice.length; k++) {
                if (sourthAmerice[k].endsWith(mCountry)) {
                    mShowCountry = sourthAmerice;
                    mBalanceIndex = 7;
                    return;
                }
            }            
        }
        mBalanceIndex = 4;
        mShowCountry = new String[] {
            "en_US"
        };
    }

    private void getLanguageList() {
        String[] southeastAsiaLanguage = mLanguageSettingActivity.getResources().getStringArray(
                R.array.southeast_asia_language);
        String[] russiaLanguage = mLanguageSettingActivity.getResources().getStringArray(
                R.array.russia_language);
        String[] middleEastLanguage = mLanguageSettingActivity.getResources().getStringArray(
                R.array.middle_east_language);
        String[] eastAsiaLanguage = mLanguageSettingActivity.getResources().getStringArray(
                R.array.east_asia_language);
        String[] eastAsiaLanguageName = mLanguageSettingActivity.getResources().getStringArray(
                R.array.language_names);
        String[] japaneseLanguage = mLanguageSettingActivity.getResources().getStringArray(
                R.array.japan_language);
        String[] nordicLanguage = mLanguageSettingActivity.getResources().getStringArray(
                R.array.Nordic_language);
        String[] sourthAmericaLanguage = mLanguageSettingActivity.getResources().getStringArray(
                R.array.south_america_language);        

        if (mBalanceIndex == 0) {
            getCountryName(southeastAsiaLanguage);
        } else if (mBalanceIndex == 1) {
            getCountryName(russiaLanguage);
        } else if (mBalanceIndex == 2) {
            getCountryName(middleEastLanguage);
        } else if (mBalanceIndex == 3) {
            getCountryName(eastAsiaLanguage);
            mCountries = eastAsiaLanguageName;
        } else if (mBalanceIndex == 4) {
            String s = "en_US";
            String language = s.substring(0, 2);
            String country = s.substring(3, 5);
            final Locale locale = new Locale(language, country);
            mCurrentLocales.add(locale);
            mCountries = new String[] {
                locale.getDisplayLanguage()
            };
        } else if (mBalanceIndex == 5) {
            Set<String> allLanguage = new HashSet<String>();
            allLanguage.addAll(new ArrayList(Arrays.asList(southeastAsiaLanguage)));
            allLanguage.addAll(new ArrayList(Arrays.asList(russiaLanguage)));
            allLanguage.addAll(new ArrayList(Arrays.asList(middleEastLanguage)));
            allLanguage.addAll(new ArrayList(Arrays.asList(eastAsiaLanguage)));
            allLanguage.addAll(new ArrayList(Arrays.asList(japaneseLanguage)));
            allLanguage.addAll(new ArrayList(Arrays.asList(nordicLanguage)));
            getCountryName((String[]) allLanguage.toArray(new String[allLanguage.size()]));
        } else if (mBalanceIndex == 6) {
            Set<String> allLanguage = new HashSet<String>();
            allLanguage.addAll(new ArrayList(Arrays.asList(southeastAsiaLanguage)));
            allLanguage.addAll(new ArrayList(Arrays.asList(nordicLanguage)));
            getCountryName((String[]) allLanguage.toArray(new String[allLanguage.size()]));
	    } else if (mBalanceIndex == 7) {
	        Set<String> allLanguage = new HashSet<String>();
	        allLanguage.addAll(new ArrayList(Arrays.asList(sourthAmericaLanguage)));
	        getCountryName((String[]) allLanguage.toArray(new String[allLanguage.size()]));
	    }        
    }

    private void getCountryName(String[] languages) {
        mCountries = new String[languages.length];

        String language = null;
        String country = null;
        for (int i = 0; i < languages.length; i++) {
            language = languages[i].substring(0, 2);
            country = languages[i].substring(3, 5);
            final Locale locale = new Locale(language, country);
            if (country.equals(mLocaleCountry)) {
                mSelect = i;
            }
            mCurrentLocales.add(locale);
            mCountries[i] = locale.getDisplayLanguage();

            if (mBalanceIndex == 5) {
                if (language.equals("zh") && country.equals("CN")) {
                    mCountries[i] = mLanguageSettingActivity.getResources().getStringArray(
                            R.array.language_names)[0];
                } else if (language.equals("zh") && country.equals("TW")) {
                    mCountries[i] = mLanguageSettingActivity.getResources().getStringArray(
                            R.array.language_names)[1];
                }
            }
        }
    }

    private String readValue(String filePath, String key) {
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(filePath));
            props.load(in);
            String value = props.getProperty(key);

            return value;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
    }

}
