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

package tufer.com.menutest.UIActivity.system.city;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import tufer.com.menutest.R;


public class CitySettingViewHolder {

    private static final String TAG = "CitySettingViewHolder";

    private static final String CITY_CHANGE = "com.android.settings.city_change";

    private static final String CITY = "city";

    private static final int PARSE_SUCCESS = 0;

    private static final int GET_CITY_SUCCESS = 1;

    private static final int GET_CITY_FAILURE = 2;

    private CitySettingActivity mCitySettingActivity;

    private ArrayList<String> mProvinecsList;

    protected ArrayList<String> mCitiesList;

    private ArrayList<String> mProvinecesListCN = new ArrayList<String>();
    private ArrayList<String> mProvinecesListCN_tr = new ArrayList<String>();

    private ArrayList<String> mProvinecsListEN = new ArrayList<String>();

    protected ListView mListView;

    private String[] mProvinecs;

    private String mResult = null;

    private ArrayAdapter<String> mProvincesAdapter;

    private String mProvinceName;

    private String mChCityName;

    private String mEnCityName;

    public int mPositon = 0;

    private int mIsSelected = 0;

    protected TextView mCurrentCityTextView;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == PARSE_SUCCESS) {
                refresh();
            } else if (msg.what == GET_CITY_SUCCESS) {
                Log.d(TAG, "mResult, " + mResult);
                String[] results = mResult.split("\t");
                if (results.length >= 5) {
                    mProvinceName = results[4];
                    mChCityName = results[5];
                }
                saveProvineAndName(mProvinceName, mChCityName);
            }
        };
    };

    public CitySettingViewHolder(CitySettingActivity citySettingActivity) {
        this.mCitySettingActivity = citySettingActivity;
        findViews();
    }

    private void findViews() {
        mCurrentCityTextView = (TextView) mCitySettingActivity.findViewById(R.id.current_city);
        mListView = (ListView) mCitySettingActivity.findViewById(R.id.city_select_lv);
        getValuesFromPreferences();
        if (mProvinceName == null) {
            new CityThread().start();
        }

        new ParseProviceAndCityThread().start();

        mListView.setDividerHeight(0);
        mListView.setItemsCanFocus(false);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mListView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIsSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void refresh() {
	
	if(mCitySettingActivity.mpLocale.toString().equals("en_US")){
		mProvinecs = (String[]) mProvinecsListEN.toArray(new String[mProvinecsListEN.size()]);
		Log.d(TAG,"-----------en_US");
	}
	else {
	if(mCitySettingActivity.mpLocale.toString().equals("zh_TW")){
		Log.d(TAG,"-----------zh_TW");
		mProvinecs = (String[]) mProvinecesListCN_tr.toArray(new String[mProvinecesListCN_tr.size()]);
	}
	else { 
		mProvinecs = (String[]) mProvinecesListCN.toArray(new String[mProvinecesListCN.size()]);
		Log.d(TAG,"-----------zh_CN");
	}
	}

        mProvincesAdapter = new ArrayAdapter<String>(mCitySettingActivity,
                R.layout.list_item_province_select, mProvinecs);
        mListView.setAdapter(mProvincesAdapter);
        setListViewSelect();
        mProvincesAdapter.notifyDataSetChanged();
    }

    /**
     * Parse out all of the provinces.
     */
    private void getAllProvinecs() throws Exception {
        HashMap<String, LinkedHashMap<String, String>> v2 = new HashMap<String, LinkedHashMap<String, String>>();
        InputStream inputStream = mCitySettingActivity.getResources().openRawResource(
                R.raw.weather_province);

        mProvinecsList = new ArrayList<String>();
        mCitiesList = new ArrayList<String>();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        String key = null;
        while ((str = bufferedReader.readLine()) != null) {
            str = str.trim();
            if (str.indexOf("=") == -1 && (str.indexOf("[") == -1 && str.indexOf("]") == -1)) {
                continue;
            }

            if (str.indexOf("[") != -1 && str.indexOf("]") != -1) {
                key = str.replace("[", "").replace("]", "");
            }

            String[] strs = null;
            if (v2.containsKey(key)) {
                LinkedHashMap<String, String> mp = v2.get(key);
                strs = str.split("=");
                if (strs == null || strs.length != 2) {
                    continue;
                }
                mp.put(strs[0].trim(), strs[1].trim());
                if (key.length() == 13) {
                    mProvinecsList.add(strs[1].trim());
                } else {
                    mCitiesList.add(strs[1].trim());
                }
            } else {
                LinkedHashMap<String, String> mp = new LinkedHashMap<String, String>();
                v2.put(key, mp);
                strs = str.split("=");
                if (strs == null || strs.length != 2) {
                    continue;
                }
                mp.put(strs[0].trim(), strs[1].trim());
            }
        }
    }

    private void getCNAndENProvinecs() {
        for (int i = 0; i < mProvinecsList.size(); i++) {
            String[] strings = mProvinecsList.get(i).toString().split("\\(");
            mProvinecesListCN.add(strings[0]);
            mProvinecsListEN.add(strings[1]);
	        mProvinecesListCN_tr.add(strings[2]);
        }
    }

    private void saveProvineAndName(String provinceName, String cityName) {
        SharedPreferences preferences = mCitySettingActivity.getSharedPreferences(
                CitySettingActivity.SHARE_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("province_name", provinceName);
        editor.putString("cn_city_name", cityName);
        editor.commit();
    }

    private void getValuesFromPreferences() {
        SharedPreferences preferences = mCitySettingActivity.getSharedPreferences(
                CitySettingActivity.SHARE_NAME, Context.MODE_PRIVATE);
        mProvinceName = preferences.getString("province_name", null);
        mChCityName = preferences.getString("cn_city_name", null);
        mEnCityName = preferences.getString("en_city_name", null);
    }

    private void getCurrentProvinceAndCity() {
        final String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?";
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine != null && statusLine.getStatusCode() == 200) {
                mResult = EntityUtils.toString(httpResponse.getEntity()).trim();
                mHandler.sendEmptyMessage(GET_CITY_SUCCESS);
            }
        } catch (Exception e) {
            Log.d(TAG, "getCurrentProvinceAndCity, Exception");
            e.printStackTrace();
            mHandler.sendEmptyMessage(GET_CITY_FAILURE);
        }
    }

    private void setListViewSelect() {
        if (mProvinecs != null) {
            for (int i = 0; i < mProvinecs.length; i++) {
                if (mProvinceName != null) {
                    if (mProvinceName.equals(mProvinecesListCN.get(i))
                            || mProvinceName.equals(mProvinecsListEN.get(i))|| mProvinceName.equals(mProvinecesListCN_tr.get(i))) {
                        mPositon = i;
                        mListView.setItemChecked(mPositon, true);
                        mListView.setSelection(mPositon);
                        break;
                    }
                }
            }
        }

        if (mChCityName == null) {
            mCurrentCityTextView.setText(mCitySettingActivity
                    .getString(R.string.current_city_no_setting) + "");
        } else {
            if (mCitySettingActivity.mIsEnglish) {
                if (mEnCityName != null) {
                    mCurrentCityTextView.setText(mEnCityName + "  "
                            + mCitySettingActivity.getString(R.string.current_city_city));
                } else {
                    String[] cities = mCitiesList.get(mPositon).split("，");

                    for (int i = 0; i < cities.length; i++) {
                        if (cities[i].contains(mChCityName)) {
                            mCurrentCityTextView.setText(cities[i].split("\\(")[1].replace(")", "")
                                    + "  "
                                    + mCitySettingActivity.getString(R.string.current_city_city));
                            break;
                        }
                    }
                }
            } else {
                mCurrentCityTextView.setText(mChCityName + "  "
                        + mCitySettingActivity.getString(R.string.current_city_city));
            }
        }
    }

    protected void response(int select) {
        if (select > 5) {
            getValuesFromPreferences();
            CitySelectDialog citySelectDialog = new CitySelectDialog(mCitySettingActivity, this,
                    select, provinecsList().get(select), mChCityName);
            citySelectDialog.show();
        } else {
		String cityName = null;
		if(mCitySettingActivity.mpLocale.toString().equals("en_US")){
			cityName = mProvinecsListEN.get(select).toString();
		} else {
			if(mCitySettingActivity.mpLocale.toString().equals("zh_TW")){
				cityName = mProvinecesListCN_tr.get(select).toString();
			}
			else{
				cityName = mProvinecesListCN.get(select).toString();
			}
		}
		mCurrentCityTextView.setText(cityName + "  "
                    + mCitySettingActivity.getString(R.string.current_city_city));
            saveProvineAndName(cityName, cityName);
            if (mCitySettingActivity.mpLocale.toString().equals("en_US")) {
		}
	    else {
		    if(mCitySettingActivity.mpLocale.toString().equals("zh_TW")){
			    cityName = mProvinecesListCN_tr.get(select).toString();
		    }
		    else{
			    cityName = mProvinecesListCN.get(select).toString();
		    }
            }
            mPositon = select;
            sendMsg(cityName);
        }
    }

    protected void sendMsg(String city) {
        Intent intent = new Intent(CITY_CHANGE);
        intent.putExtra(CITY, city);
        mCitySettingActivity.sendBroadcast(intent);
        Log.d(TAG, "send City : "+city);
    }

    private ArrayList<String> provinecsList() {
        return mProvinecsList;
    }

    class CityThread extends Thread {

        @Override
        public void run() {
            getCurrentProvinceAndCity();
        }
    }

    class ParseProviceAndCityThread extends Thread {

        @Override
        public void run() {
            try {
                getAllProvinecs();
                getCNAndENProvinecs();
                mHandler.sendEmptyMessage(PARSE_SUCCESS);
            } catch (Exception e) {
                Log.d(TAG, "ParseProviceAndCityThread, Exception");
                e.printStackTrace();
            }
        }
    }

}
