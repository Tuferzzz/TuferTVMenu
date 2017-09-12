/* 
 * Copyright (C) 2011 Hisense Electric Co., Ltd. 
 * All Rights Reserved.
 *
 * ALL RIGHTS ARE RESERVED BY HISENSE ELECTRIC CO., LTD. ACCESS TO THIS
 * SOURCE CODE IS STRICTLY RESTRICTED UNDER CONTRACT. THIS CODE IS TO
 * BE KEPT STRICTLY CONFIDENTIAL.
 *
 * UNAUTHORIZED MODIFICATION OF THIS FILE WILL VOID YOUR SUPPORT CONTRACT
 * WITH HISENSE ELECTRIC CO., LTD. IF SUCH MODIFICATIONS ARE FOR THE PURPOSE
 * OF CIRCUMVENTING LICENSING LIMITATIONS, LEGAL ACTION MAY RESULT.
 */

package tufer.com.menutest.UIActivity.system.city;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import tufer.com.menutest.R;


public class CitySelectDialog extends Dialog {

    private CitySettingActivity mCitySettingActivity;

    private ListView mCityListView;

    private CitySettingViewHolder mHolder;

    private int mSelect = 0;

    private String[] mCities;

    private String mProvinceName;

    private ArrayList<String> mCitiesCNList = new ArrayList<String>();

    private ArrayList<String> mCitiesTWList = new ArrayList<String>();

    private ArrayList<String> mCitiesEnList = new ArrayList<String>();;

    private String mCity;

    private ArrayAdapter<String> mCitiesAdapter;

    private String[] mStrings;

    private int mPostion = 0;

    public CitySelectDialog(CitySettingActivity citySettingActivity, CitySettingViewHolder holder,
            int select, String provinecName, String city) {
        super(citySettingActivity);
        this.mCitySettingActivity = citySettingActivity;
        this.mHolder = holder;
        this.mSelect = select;
        this.mPostion = select;
        this.mProvinceName = provinecName;
        this.mCity = city;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cities);
        Window w = getWindow();

        Display display = w.getWindowManager().getDefaultDisplay();

        Resources resources = mCitySettingActivity.getResources();
        Drawable drawable = resources.getDrawable(R.drawable.set_bg);
        w.setBackgroundDrawable(drawable);
        Point point = new Point();
        display.getSize(point);
        int width = (int) (point.x * 0.3);
        int height = (int) (point.y * 0.4);
        w.setLayout(width, height);
        w.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams wl = w.getAttributes();
        w.setAttributes(wl);

        findViews();
        registerListeners();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mHolder.mListView.setSelection(mHolder.mPositon);
            mHolder.mListView.setItemChecked(mHolder.mPositon, true);
            dismiss();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void findViews() {
        mCityListView = (ListView) findViewById(R.id.cities_lv);
        mCityListView.setDividerHeight(0);
        mCities = mHolder.mCitiesList.get(mSelect).split("，");
        getCNAndENCities();
        int position = 0;
        if (mCity != null) {
            for (int i = 0; i < mCities.length; i++) {
                if (mCities[i].contains(mCity)) {
                    position = i;
                    break;
                }
            }
        }

        if (mCitySettingActivity.mpLocale.toString().equals("en_US")) {
            mStrings = (String[]) mCitiesEnList.toArray(new String[mCitiesEnList.size()]);
        } else {
            if (mCitySettingActivity.mpLocale.toString().equals("zh_TW")) {
                mStrings = (String[]) mCitiesTWList.toArray(new String[mCitiesCNList.size()]);
            } else {
                mStrings = (String[]) mCitiesCNList.toArray(new String[mCitiesCNList.size()]);
            }
        }

        mCitiesAdapter = new ArrayAdapter<String>(mCitySettingActivity,
                R.layout.list_item_city_select, mStrings);
        mCityListView.setDividerHeight(0);
        mCityListView.setItemsCanFocus(false);
        mCityListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mCityListView.setAdapter(mCitiesAdapter);
        mCityListView.setItemChecked(position, true);
        mCityListView.setSelection(position);
    }

    /**
     * Choice of city.
     */
    private void registerListeners() {
        mCityListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                saveCity(position);
            }
        });

        mCityListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelect = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Get all the Chinese province name and the English provincial name.
     */
    private void getCNAndENCities() {
        for (int i = 0; i < mCities.length; i++) {
            String[] strings = mCities[i].toString().split("\\(");
            mCitiesCNList.add(strings[0]);
            mCitiesEnList.add(strings[1]);
            mCitiesTWList.add(strings[2]);
            // Log.d("CitySelectDialog","cn="+strings[0]+"  en="+strings[1]+" tw="+strings[2]);
        }
    }

    /**
     * save City name.
     */
    private void saveCity(int position) {
        if (mCitySettingActivity.mpLocale.toString().equals("en_US")) {
            mCity = mCitiesEnList.get(position).toString();
        } else {
            if (mCitySettingActivity.mpLocale.toString().equals("zh_TW")) {
                mCity = mCitiesTWList.get(position).toString();
            } else {
                mCity = mCitiesCNList.get(position).toString();
            }
        }

        mHolder.mCurrentCityTextView.setText(mCity + "  "
                + mCitySettingActivity.getString(R.string.current_city_city));

        saveProvinceName(position);
        if (mCitySettingActivity.mIsEnglish) {
            mCity = mCitiesCNList.get(position).toString();
        }
        mHolder.sendMsg(mCity);
        mHolder.mListView.setSelection(mPostion);
        mHolder.mListView.setItemChecked(mPostion, true);
        mHolder.mPositon = mPostion;
        dismiss();
    }

    /**
     * save province name.
     */
    private void saveProvinceName(int position) {
        SharedPreferences preferences = mCitySettingActivity.getSharedPreferences(
                CitySettingActivity.SHARE_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("province_name", mProvinceName.split("\\(")[0]);
        editor.putString("cn_city_name", mCitiesCNList.get(position).toString());
        editor.putString("en_city_name", mCitiesEnList.get(position).toString());
        editor.commit();
    }

}
