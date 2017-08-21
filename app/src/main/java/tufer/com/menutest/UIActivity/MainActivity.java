/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

 
package tufer.com.menutest.UIActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.mstar.android.tv.TvAudioManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tv.TvTimerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.about.SystemInfoActivity;
import tufer.com.menutest.UIActivity.about.SystemRestoreFactoryActivity;
import tufer.com.menutest.UIActivity.bluetooth.BluetoothActivity;
import tufer.com.menutest.UIActivity.channel.ChannelActivity;
import tufer.com.menutest.UIActivity.channel.ProgramListViewActivity;
import tufer.com.menutest.UIActivity.general.appinfo.AppInfo;
import tufer.com.menutest.UIActivity.general.appinfo.AppManagerActivity;
import tufer.com.menutest.UIActivity.general.appinfo.PackageSizeObserver;

import tufer.com.menutest.UIActivity.general.datetime.DateTimeSettings;
import tufer.com.menutest.UIActivity.general.weather.WeaterActivity;
import tufer.com.menutest.UIActivity.intelligence.SetTimeOffDialogActivity;
import tufer.com.menutest.UIActivity.intelligence.SetTimeOnDialogActivity;
import tufer.com.menutest.UIActivity.network.NetworkSettingsActivity;
import tufer.com.menutest.UIActivity.pictrue.SetLightActivity;
import tufer.com.menutest.UIActivity.sound.EqualizerActivity;
import tufer.com.menutest.UIActivity.system.InputMethodAndLanguageSettingsActivity;

import tufer.com.menutest.UIActivity.system.city.CitySettingActivity;
import tufer.com.menutest.UIActivity.update.SystemLocalUpdateActivity;
import tufer.com.menutest.UIActivity.update.SystemNetUpdateActivity;
import tufer.com.menutest.Util.TVRootApp;
import tufer.com.menutest.Util.Tools;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;


/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity implements View.OnClickListener {
    public static final int UPDATE_PICTURE = 1;
    public static final int UPDATE_GENERAL = 0;
    public static final int UPDATE_SOUND = 2;
    public static final int UPDATE_NETWORK= 4;
    public static final int UPDATE_INTELLIGENCE = 5;
    public static final int UPDATE_SYSTEM = 6;
    private static final int UPDATE_ABOUT = 7;
    private String TAG="MainActivity";
    private int time=0;
    protected long downTime=0;
    protected boolean isMenuShow=false;

    public static MainActivity myMainActivity=null;

    private static final String TVPLAYER_PACKAGE = "com.mstar.tv.tvplayer.ui";

    private static final String PROPERTY_TVPLAYER_STATUS = "persist.sys.istvplayer";


    private int menuDisplayTime=0;

    private LinearLayout MainMenu;
    /**
     * Called when the activity is first created.
     */
    private Button[] button;
    private TvAudioManager tvAudioManager;
    private TvPictureManager mTvPictureManager ;
    private TvCommonManager mTvCommonManager;
    private AudioManager audio;
    private TVRootApp mTvRootApp;
    private TextView[] intelligenceTextView;
    private TextView sleeptime_val,power_on_time_val,wifihotspot_val,location_val,wifi_val,bluetooth_val;
    private TextView powerinput_val,menu_display_time_val,powermusic_val,local_name;
    private TextView brightness_val,contrast_val,backlight_val,hue_val;


    private LinearLayout[] general;


    private LinearLayout[] channel;
    private LinearLayout[] network;

    private LinearLayout[] system;
    private LinearLayout[] about;

    /**
     * 智能模块数据
     */
    private LinearLayout[] intelligence;
    public static int[] intelligenceNameList={R.string.str_mainmenu_intelligence_identify,R.string.str_mainmenu_intelligence_childlock,
            R.string.str_mainmenu_intelligence_energysaving,R.string.str_mainmenu_intelligence_temperaturedet,
            R.string.str_mainmenu_intelligence_sensorlight,R.string.str_mainmenu_intelligence_protecteyes,
            R.string.str_mainmenu_intelligence_nosignalreturn,R.string.str_mainmenu_intelligence_nosignal_standby,};
    private LinearLayout intelligence_identify,intelligence_childlock,
            intelligence_energysaving,intelligence_temperaturedet,
            intelligence_sensorlight,intelligence_protecteyes,
            intelligence_nosignalreturn,intelligence_nosignalstandby,
            intelligence_sleeptime,intelligence_power_on_time,intelligence_menu_display_time;



    private int[] colorSystemList={R.string.str_mainmenu_channel_colorsystem_pal,R.string.str_mainmenu_channel_colorsystem_ntsc_m,
            R.string.str_mainmenu_channel_colorsystem_secam,R.string.str_mainmenu_channel_colorsystem_ntsc_44,
            R.string.str_mainmenu_channel_colorsystem_pal_m,R.string.str_mainmenu_channel_colorsystem_pal_n,
            R.string.str_mainmenu_channel_colorsystem_pal_60,R.string.str_mainmenu_channel_colorsystem_nostandard,
            R.string.str_mainmenu_channel_colorsystem_auto};








    //private String[] powerInputList;
//    private String[] languageList;

    private int[] pictrueTextViewId={R.id.textview_picture_brightness_val,R.id.textview_picture_contrast_val,
            R.id.textview_picture_backlight_val,R.id.textview_picture_hue_val};
    private int[] buttonId={R.id.generalButton,R.id.pictureButton,
                                R.id.soundButton,R.id.channelButton,
                                R.id.networkButton,R.id.intelligenceButton,
                                R.id.systemButton,R.id.aboutButton};
    private int[] intelligenceTextViewId={R.id.textview_intelligence_identify_val,
            R.id.textview_intelligence_childlock_val,
            R.id.textview_intelligence_energysaving_val,R.id.textview_intelligence_temperaturedet_val,
            R.id.textview_intelligence_sensorlight_val,R.id.textview_intelligence_protecteyes_val,
            R.id.textview_intelligence_nosignalreturn_val,R.id.textview_intelligence_nosignalstandby_val};
    private int[] buttonId_frist={R.id.linearlayout_general_powerinput,R.id.linearlayout_picture_picturemode,
            R.id.linearlayout_sound_soundmode,R.id.linearlayout_channel_autotuning,
            R.id.linearlayout_network_networkselection,R.id.linearlayout_intelligence_identify,
            R.id.linearlayout_system_inputmethod,R.id.linearlayout_about_systeminfo};
    private int[] generalId={R.id.linearlayout_general_powerinput,R.id.linearlayout_general_appmanager,
            R.id.linearlayout_general_timedate,R.id.linearlayout_general_weather};


    private int[] channelId={R.id.linearlayout_channel_autotuning,
            R.id.linearlayout_channel_programedit};
    private int[] networkId={R.id.linearlayout_network_networkselection,R.id.linearlayout_network_wifi,
            R.id.linearlayout_network_wifihotspot,
            R.id.linearlayout_network_networkdetails,
            R.id.linearlayout_network_bluetooth};
    private int[] intelligenceId={R.id.linearlayout_intelligence_identify,R.id.linearlayout_intelligence_childlock,
            R.id.linearlayout_intelligence_energysaving,R.id.linearlayout_intelligence_temperaturedet,
            R.id.linearlayout_intelligence_sensorlight,R.id.linearlayout_intelligence_protecteyes,
            R.id.linearlayout_intelligence_nosignalreturn,R.id.linearlayout_intelligence_nosignalstandby,
            R.id.linearlayout_intelligence_sleeptime,R.id.linearlayout_intelligence_power_on_time,
            R.id.linearlayout_intelligence_menu_display_time};
    private int[] systemId={R.id.linearlayout_system_inputmethod,R.id.linearlayout_system_location,
            R.id.linearlayout_system_powermusic};
    private int[] aboutId={R.id.linearlayout_about_systeminfo,R.id.linearlayout_about_instructions,
            R.id.linearlayout_about_localname,
            R.id.linearlayout_about_restore,R.id.linearlayout_about_local_upgrade,R.id.linearlayout_about_network_upgrade};

    private Button generalButton,pictureButton,soundButton,channelButton,networkButton,
            intelligenceButton,systemButton,aboutButton;
    private TextView identify_val,childlock_val,energysaving_val,temperaturedet_val,
            sensorlight_val,protecteyes_val,nosignalreturn_val,nosignalstandby_val;
    private LinearLayout general_powerinput,general_appmanager,general_timedate,general_weather;


    private LinearLayout channel_autotuning,channel_programedit;
    private LinearLayout network_networkselection,network_wifihotspot,network_networkdetails,network_wifi,network_bluetooth;

    private LinearLayout system_inputmethod,system_location,system_powermusic;
    private LinearLayout about_systeminfo,about_instructions,about_localname,about_restore,about_local_upgrade,about_network_upgrade;


    private ViewFlipper viewFlipper;
    protected LayoutInflater lf;
    private int posion ;
    private int flag;
    public static int generalInputPosition=0;



    public static int intelligencePosion=0;
    public static boolean isSleep;


    public static String localName= "";
    public static int colorSystemPosition=0;
    public static int soundSystemPosition=0;
    public static String powerInputString="";

    /**
     * 图像模块数据
     */
    public static int[] pictrueTitleNameList={R.string.str_mainmenu_picture_brightness,
            R.string.str_mainmenu_picture_contrast,
            R.string.str_mainmenu_picture_backlight,R.string.str_mainmenu_picture_hue};
    private String[] pictureModeList;
    public static String[] zoomModeList;
    private String[] colorTemperatureList;
    public static String[] noiseReductionList;
    private TextView picturemode_val,xvYCC_val,zoommode_val,color_temperature_val,imgnoisereduction_val,mpegnoisereduction_val;
    private int[] pictrueNumber;
    public static int pictruePosition;
    private TextView[] pictrueTextView;
    private LinearLayout[] picture;
    private LinearLayout picture_picturemode,picture_xvYCC,picture_zoommode,
            picture_color_temperature,picture_mpegnoisereduction,
            picture_imgnoisereduction,
            picture_brightness, picture_contrast,picture_backlight,
            picture_hue;
    private int[] pictureId={R.id.linearlayout_picture_picturemode,R.id.linearlayout_picture_xvYCC,
            R.id.linearlayout_picture_zoommode,R.id.linearlayout_picture_color_temperature,
            R.id.linearlayout_picture_mpegnoisereduction,R.id.linearlayout_picture_imgnoisereduction,
            R.id.linearlayout_picture_brightness,
            R.id.linearlayout_picture_contrast,R.id.linearlayout_picture_backlight,
            R.id.linearlayout_picture_hue};
    /**
     * 声音模块数据
     */
    private TextView soundmode_val,srs_val,avc_val,surround_val,autohoh_val,mute_val;
    private String[] soundModeList;
    private String[] trackNameList;
    private int volume;
    public static boolean isMute=false;
    private LinearLayout[] sound;
    private LinearLayout sound_soundmode,sound_srs,sound_equalizer,sound_avc,sound_surround,sound_autohoh,sound_mute;
    private int[] soundId={R.id.linearlayout_sound_soundmode,R.id.linearlayout_sound_srs,
            R.id.linearlayout_sound_equalizer,
            R.id.linearlayout_sound_avc,R.id.linearlayout_sound_surround,
            R.id.linearlayout_sound_auto_hoh,R.id.linearlayout_sound_mute};


//    public static String SSID="TUFER";
//    public static String password="123456789";
    public static boolean isWifiHotspotOn=false;
    public static boolean isWifiOn=false;
    public static boolean isBuletoothOn=false;
    //public static boolean isAutoAdjustOFF=false;




    public static List<AppInfo> myAppList;

    private boolean isBtnFocus=true;

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_GENERAL:
                    initGeneralCallback();
                    break;
                case UPDATE_PICTURE:
                    initPictureCallback();
                    break;
                case UPDATE_SOUND:
                    initSoundCallback();
                    break;
                case UPDATE_NETWORK:
                    initNetworkCallback();
                    break;
                case UPDATE_INTELLIGENCE:
                    initIntelligenceCallback();
                    break;
                case UPDATE_SYSTEM:
                    initSystemCallback();
                    break;
                case UPDATE_ABOUT:
                    initAboutCallback();
                    break;
            }
        }
    };



    View.OnFocusChangeListener btnOnFocusChangeListener=new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if(b){//获得焦点
                isBtnFocus=true;
                button[posion].setBackgroundResource(0);
                view.setBackgroundResource(R.drawable.mainmenu_button1_focus);

            }else{//失去焦点
                isBtnFocus=false;

            }
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        addView();
        initView();


    }



    @Override
    protected void onResume() {
        super.onResume();
        downTime=System.currentTimeMillis();
        setMenuDisPlayTime();
        sourceInTvFocus();

    }

    private void setMenuDisPlayTime() {
        SharedPreferences preferences=getSharedPreferences("TuferTvMenu", Context.MODE_PRIVATE);
        isMenuShow=preferences.getBoolean("isMenuShow", false);
        menuDisplayTime=preferences.getInt("menuDisplayTime", 0);
        if(isMenuShow){
            menu_display_time_val.setText(menuDisplayTime+"s");
        }else{
            downTime=0;
            menu_display_time_val.setText(getString(R.string.str_mainmenu_default_switch_off));
        }
        if (isMenuShow){
            new Thread(){
                @Override
                public void run() {
                    while(true){
                        super.run();
                        try {
                            Thread.sleep(1000);
                            time= (int)(System.currentTimeMillis()-downTime)/1000;
                            if(time>=menuDisplayTime&&isMenuShow){
                                System.exit(0);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }

    private void initView() {
        posion=0;
        flag=-1;
        myMainActivity=this;
        mTvRootApp= (TVRootApp) this.getApplication();
        SharedPreferences preferences=getSharedPreferences("TuferTvMenu", Context.MODE_PRIVATE);
        isMenuShow=preferences.getBoolean("isMenuShow", false);
        menuDisplayTime=preferences.getInt("menuDisplayTime", 0);


        audio=(AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mTvPictureManager = TvPictureManager.getInstance();
        mTvCommonManager = TvCommonManager.getInstance();
        volume =audio.getStreamVolume( AudioManager.STREAM_SYSTEM );
        tvAudioManager=TvAudioManager.getInstance();






        MainMenu= (LinearLayout) findViewById(R.id.MainMenu);



        /**
         * 通用模块初始化
         */
        initGeneral();


        /**
         * 声音模块初始化
         */
        initSound();



        /**
         * 图像模块初始化
         */
        initPicture();


        /**
         * 频道模块初始化
         */
        initChannel();


        /**
         * 网络模块初始化
         */
        initNetWork();


        /**
         * 智能模块初始化
         */
        initIntelligence();


        /**
         * 系统设置模块初始化
         */
        initSystem();


        /**
         * 关于模块初始化
         */
        initAbout();


        /**
         * 主界面模块按钮初始化
         */
        initButton();

    }

    private void initChannel() {
        channel=new LinearLayout[]{
                channel_autotuning,channel_programedit};
        for(int i=0;i<channel.length;i++){
            channel[i]= (LinearLayout) findViewById(channelId[i]);
            channel[i].setOnClickListener(this);
            if(i+1>=channel.length) channel[i].setNextFocusRightId(buttonId[3]);
            else channel[i].setNextFocusRightId(channelId[i+1]);
            if(i-1<0) channel[i].setNextFocusLeftId(buttonId[3]);
            else channel[i].setNextFocusLeftId(channelId[i-1]);
            if(i-4<0){
                int j=i+channel.length-channel.length%4;
                while (j>=channel.length) j=j-4;
                channel[i].setNextFocusUpId(channelId[j]);
            }
            if(i+4>=channel.length){
                channel[i].setNextFocusDownId(channelId[i%4]);
            }
        }
    }

    private void initNetWork() {
        network=new LinearLayout[]{network_networkselection,network_wifi,network_wifihotspot,network_networkdetails,network_bluetooth};
        for(int i=0;i<network.length;i++){
            network[i]= (LinearLayout) findViewById(networkId[i]);
            network[i].setOnClickListener(this);
            if(i+1>=network.length) network[i].setNextFocusRightId(buttonId[4]);
            else network[i].setNextFocusRightId(networkId[i+1]);
            if(i-1<0) network[i].setNextFocusLeftId(buttonId[4]);
            else network[i].setNextFocusLeftId(networkId[i-1]);
            if(i-4<0){
                int j=i+network.length-network.length%4;
                while (j>=network.length) j=j-4;
                network[i].setNextFocusUpId(networkId[j]);
            }
            if(i+4>=network.length){
                network[i].setNextFocusDownId(networkId[i%4]);
            }
        }
        wifihotspot_val= (TextView) findViewById(R.id.textview_network_wifihotspot_val);
        wifi_val= (TextView) findViewById(R.id.wifi_val);
        bluetooth_val= (TextView) findViewById(R.id.bluetooth_val);
    }

    private void initIntelligence() {
        intelligence=new LinearLayout[]{intelligence_identify,intelligence_childlock,
                intelligence_energysaving,intelligence_temperaturedet,intelligence_sensorlight,
                intelligence_protecteyes,intelligence_nosignalreturn,intelligence_nosignalstandby,
                intelligence_sleeptime,intelligence_power_on_time,intelligence_menu_display_time};
        for(int i=0;i<intelligence.length;i++){
            intelligence[i]= (LinearLayout) findViewById(intelligenceId[i]);
            intelligence[i].setOnClickListener(this);
            if(i+1>=intelligence.length) intelligence[i].setNextFocusRightId(buttonId[5]);
            else intelligence[i].setNextFocusRightId(intelligenceId[i+1]);
            if(i-1<0) intelligence[i].setNextFocusLeftId(buttonId[5]);
            else intelligence[i].setNextFocusLeftId(intelligenceId[i-1]);
            if(i-4<0){
                int j=i+intelligence.length-intelligence.length%4;
                while (j>=intelligence.length) j=j-4;
                intelligence[i].setNextFocusUpId(intelligenceId[j]);
            }
            if(i+4>=intelligence.length){
                intelligence[i].setNextFocusDownId(intelligenceId[i%4]);
            }
        }
        sleeptime_val= (TextView) findViewById(R.id.textview_intelligence_sleeptime_val);
        power_on_time_val= (TextView) findViewById(R.id.textview_intelligence_power_on_time_val);
        setSleepAndPowerTime();
        intelligenceTextView=new TextView[]{identify_val,childlock_val,energysaving_val,
                temperaturedet_val,sensorlight_val,protecteyes_val,nosignalreturn_val,nosignalstandby_val};
        for(int i=0;i<intelligenceTextView.length;i++){
            intelligenceTextView[i]=(TextView)findViewById(intelligenceTextViewId[i]);
        }
        menu_display_time_val= (TextView) findViewById(R.id.textview_intelligence_menu_display_time_val);
//        if(Settings.Global.getInt(getContentResolver(),Settings.Global.ON_INTELLIGENT_IDENTIFICATION, 0) == 0){
//            intelligenceTextView[0].setText(getString(R.string.str_mainmenu_default_switch_off));
//        }else{
//            intelligenceTextView[0].setText(getString(R.string.str_mainmenu_default_switch_on));
//        }
//        if(Settings.Global.getInt(getContentResolver(),Settings.Global.ON_SIGNAL_TV_STANBY, 0) == 0){
//            intelligenceTextView[7].setText(getString(R.string.str_mainmenu_default_switch_off));
//        }else{
//            intelligenceTextView[7].setText(getString(R.string.str_mainmenu_default_switch_on));
//        }
    }

    private void initSystem() {
        //getCityName();
        system=new LinearLayout[]{system_inputmethod,system_location,system_powermusic};
        for(int i=0;i<system.length;i++){
            system[i]= (LinearLayout) findViewById(systemId[i]);
            system[i].setOnClickListener(this);
            if(i+1>=system.length) system[i].setNextFocusRightId(buttonId[6]);
            else system[i].setNextFocusRightId(systemId[i+1]);
            if(i-1<0) system[i].setNextFocusLeftId(buttonId[6]);
            else system[i].setNextFocusLeftId(systemId[i-1]);
            if(i-4<0){
                int j=i+system.length-system.length%4;
                while (j>=system.length) j=j-4;
                system[i].setNextFocusUpId(systemId[j]);
            }
            if(i+4>=system.length){
                system[i].setNextFocusDownId(systemId[i%4]);
            }
        }
        powermusic_val= (TextView) findViewById(R.id.textview_system_powermusic_val);
        powermusic_val.setText(TvFactoryManager.getInstance().getPowerOnMusicMode()!=0?
                getString(R.string.str_mainmenu_default_switch_on):getString(R.string.str_mainmenu_default_switch_off));
        location_val= (TextView) findViewById(R.id.textview_system_location_val);
        location_val.setText(getSharedPreferences(
                CitySettingActivity.SHARE_NAME, Context.MODE_PRIVATE).getString("cn_city_name", null));
    }

    private void initAbout() {
        about=new LinearLayout[]{about_systeminfo,about_instructions,about_localname,about_restore,about_local_upgrade,about_network_upgrade};
        for(int i=0;i<about.length;i++){
            about[i]= (LinearLayout) findViewById(aboutId[i]);
            about[i].setOnClickListener(this);
            if(i+1>=about.length) about[i].setNextFocusRightId(buttonId[7]);
            else about[i].setNextFocusRightId(aboutId[i+1]);
            if(i-1<0) about[i].setNextFocusLeftId(buttonId[7]);
            else about[i].setNextFocusLeftId(aboutId[i-1]);
            if(i-4<0){
                int j=i+about.length-about.length%4;
                while (j>=about.length) j=j-4;
                about[i].setNextFocusUpId(aboutId[j]);
            }
            if(i+4>=about.length){
                about[i].setNextFocusDownId(aboutId[i%4]);
            }
        }
        localName=this.getString(R.string.str_mainmenu_about_localname_default_val);
        local_name= (TextView) findViewById(R.id.textview_local_name);
        local_name.setText(localName);
    }

    private void initButton() {
        button=new Button[]{generalButton,pictureButton,
                soundButton,channelButton,
                networkButton,intelligenceButton,
                systemButton,aboutButton};

        for (int i=0;i<button.length;i++){
            button[i]= (Button) findViewById(buttonId[i]);
            button[i].setOnClickListener(this);
            if(i+1>=button.length) button[i].setNextFocusDownId(buttonId[0]);
            else button[i].setNextFocusDownId(buttonId[i+1]);
            if(i-1<0) button[i].setNextFocusUpId(buttonId[button.length-1]);
            else button[i].setNextFocusUpId(buttonId[i-1]);
            button[i].setNextFocusRightId(buttonId_frist[i]);
            button[i].setOnFocusChangeListener(btnOnFocusChangeListener);
        }
        button[0].requestFocus();
        button[0].requestFocusFromTouch();
        button[0].setBackgroundResource(R.drawable.mainmenu_button1_focus);
        //Toast.makeText(this,posion+":"+flag,Toast.LENGTH_SHORT).show();

    }

    private void initPicture() {
        zoomModeList=getResources().getStringArray(R.array.str_arr_picture_zoommode_vals);
        noiseReductionList=getResources().getStringArray(R.array.str_arr_pic_imgnoisereduction_vals);
        colorTemperatureList=getResources().getStringArray(R.array.str_arr_picture_colortemperature_vals);
        pictureModeList=getResources().getStringArray(R.array.str_arr_picture_picturemode_vals);
        picture=new LinearLayout[]{picture_picturemode,picture_xvYCC,picture_zoommode,
                picture_color_temperature,picture_mpegnoisereduction,
                picture_imgnoisereduction,
                picture_brightness, picture_contrast,picture_backlight,
                picture_hue};
        for(int i=0;i<picture.length;i++){
            picture[i]= (LinearLayout) findViewById(pictureId[i]);
            picture[i].setOnClickListener(this);
            if(i+1>=picture.length) picture[i].setNextFocusRightId(buttonId[1]);
            else picture[i].setNextFocusRightId(pictureId[i+1]);
            if(i-1<0) picture[i].setNextFocusLeftId(buttonId[1]);
            else picture[i].setNextFocusLeftId(pictureId[i-1]);
            if(i-4<0){
                int j=i+picture.length-picture.length%4;
                while (j>=picture.length) j=j-4;
                picture[i].setNextFocusUpId(pictureId[j]);
            }
            if(i+4>=picture.length){
                picture[i].setNextFocusDownId(pictureId[i%4]);
            }

        }
        picturemode_val= (TextView) findViewById(R.id.textview_picture_picturemode_val);
        picturemode_val.setText(pictureModeList[mTvPictureManager.getPictureMode()]);
        if(mTvPictureManager.getPictureMode()!=3){
            enableSingleItemOrNot(picture[6],false);
            enableSingleItemOrNot(picture[7],false);
            enableSingleItemOrNot(picture[8],false);
            enableSingleItemOrNot(picture[9],false);

        }else {
            enableSingleItemOrNot(picture[6],true);
            enableSingleItemOrNot(picture[7],true);
            enableSingleItemOrNot(picture[8],true);
            enableSingleItemOrNot(picture[9],true);
        }
        setFocus(picture);
        zoommode_val= (TextView) findViewById(R.id.textview_picture_zoommode_val);
        zoommode_val.setText(zoomModeList[mTvPictureManager.getInstance().getVideoArcType()]);
        pictrueNumber=new int[]{mTvPictureManager.getVideoItem(0),mTvPictureManager.getVideoItem(1),
                mTvPictureManager.getBacklight(),mTvPictureManager.getVideoItem(4)};
        pictrueTextView=new TextView[]{brightness_val,contrast_val,backlight_val,hue_val};
        for (int i=0;i<pictrueTextView.length;i++){
            pictrueTextView[i]= (TextView) findViewById(pictrueTextViewId[i]);
            pictrueTextView[i].setText(pictrueNumber[i]+"");
        }
        color_temperature_val= (TextView) findViewById(R.id.textview_picture_color_temperature_val);
        color_temperature_val.setText(colorTemperatureList[mTvPictureManager.getColorTemprature()]);
        imgnoisereduction_val= (TextView) findViewById(R.id.textview_picture_imgnoisereduction_val);
        imgnoisereduction_val.setText(noiseReductionList[mTvPictureManager.getNoiseReduction()]);
        mpegnoisereduction_val= (TextView) findViewById(R.id.mpegnoisereduction_val);
        mpegnoisereduction_val.setText(getResources().getStringArray(R.array.str_arr_pic_mpegnoisereduction_vals)[mTvPictureManager.getMpegNoiseReduction()]);
        xvYCC_val= (TextView) findViewById(R.id.xvycc_val);
        try{
            xvYCC_val.setText(mTvPictureManager.getxvYCCEnable()?getString(R.string.str_mainmenu_default_switch_on):
                    getString(R.string.str_mainmenu_default_switch_off));
        }catch (NoSuchMethodError e){
            e.printStackTrace();
        }

    }

    private void initSound() {
        trackNameList=getResources().getStringArray(R.array.str_arr_sound_track_vals);
        soundModeList=getResources().getStringArray(R.array.str_arr_sound_soundmode_vals);
        sound=new LinearLayout[]{sound_soundmode,sound_srs,sound_equalizer,sound_avc,sound_surround,sound_autohoh,sound_mute};
        for(int i=0;i<sound.length;i++){
            sound[i]= (LinearLayout) findViewById(soundId[i]);
            sound[i].setOnClickListener(this);
            if(i+1>=sound.length) sound[i].setNextFocusRightId(buttonId[2]);
            else sound[i].setNextFocusRightId(soundId[i+1]);
            if(i-1<0) sound[i].setNextFocusLeftId(buttonId[2]);
            else sound[i].setNextFocusLeftId(soundId[i-1]);
            if(i-4<0){
                int j=i+sound.length-sound.length%4;
                while (j>=sound.length) j=j-4;
                sound[i].setNextFocusUpId(soundId[j]);
            }
            if(i+4>=sound.length){
                sound[i].setNextFocusDownId(soundId[i%4]);
            }
        }
        soundmode_val= (TextView) findViewById(R.id.textview_sound_soundmode_val);
        soundmode_val.setText(soundModeList[tvAudioManager.getAudioSoundMode()]);
        srs_val= (TextView) findViewById(R.id.textview_sound_srs_val);
        srs_val.setText(tvAudioManager.isSRSEnable()?
                getString(R.string.str_mainmenu_default_switch_on):
                getString(R.string.str_mainmenu_default_switch_off));
        surround_val= (TextView) findViewById(R.id.textview_sound_surround_val);
        surround_val.setText(tvAudioManager.getAudioSurroundMode()==1?
                getString(R.string.str_mainmenu_default_switch_on):getString(R.string.str_mainmenu_default_switch_off));
        avc_val= (TextView) findViewById(R.id.textview_sound_avc_val);
        avc_val.setText(tvAudioManager.getAvcMode()?
                getString(R.string.str_mainmenu_default_switch_on):getString(R.string.str_mainmenu_default_switch_off));
        autohoh_val= (TextView) findViewById(R.id.textview_sound_auto_hoh_val);
        autohoh_val.setText(tvAudioManager.getHOHStatus()?
                getString(R.string.str_mainmenu_default_switch_on):getString(R.string.str_mainmenu_default_switch_off));
        mute_val= (TextView) findViewById(R.id.mute_val);
        mute_val.setText(volume==0?getString(R.string.str_mainmenu_default_switch_on):getString(
                R.string.str_mainmenu_default_switch_off));
        isMute=(volume==0?true:false);
        if(isMute) volume=50;
    }

    private void initGeneral() {
        //powerInputList =getResources().getStringArray(R.array.str_arr_general_powerinput_vals);
        general=new LinearLayout[]{general_powerinput,general_appmanager,
                general_timedate,general_weather};
        for(int i=0;i<general.length;i++){
            general[i]= (LinearLayout) findViewById(generalId[i]);
            general[i].setOnClickListener(this);
            if(i+1>=general.length) general[i].setNextFocusRightId(buttonId[0]);
            else general[i].setNextFocusRightId(generalId[i+1]);
            if(i-1<0) general[i].setNextFocusLeftId(buttonId[0]);
            else general[i].setNextFocusLeftId(generalId[i-1]);
            if(i-4<0){
                int j=i+general.length-general.length%4;
                while (j>=general.length) j=j-4;
                general[i].setNextFocusUpId(generalId[j]);
            }
            if(i+4>=general.length){
                general[i].setNextFocusDownId(generalId[i%4]);
            }
        }
        powerinput_val= (TextView) findViewById(R.id.textview_general_powerinput_val);
    }

    private void setFocus(LinearLayout[] linearLayouts) {
        if(mTvPictureManager.getPictureMode()!=3){
            for(int i=0;i<picture.length;i++){
                if(linearLayouts[i].getNextFocusDownId()==-1) {
                    int j=i+4;
                    if(j>=linearLayouts.length) j=linearLayouts.length-1;
                    linearLayouts[i].setNextFocusDownId(linearLayouts[j].getId());
                }
                if(linearLayouts[i].getNextFocusUpId()==-1) linearLayouts[i].setNextFocusUpId(linearLayouts[i-4].getId());
                if(!(findViewById(linearLayouts[i].getNextFocusDownId())).isEnabled())
                    linearLayouts[i].setNextFocusDownId(linearLayouts[i].getId());
                if(!(findViewById(linearLayouts[i].getNextFocusUpId())).isEnabled())
                    linearLayouts[i].setNextFocusUpId(linearLayouts[i].getId());
                if(!(findViewById(linearLayouts[i].getNextFocusLeftId())).isEnabled())
                    linearLayouts[i].setNextFocusLeftId(linearLayouts[i].getId());
                if(!(findViewById(linearLayouts[i].getNextFocusRightId())).isEnabled())
                    linearLayouts[i].setNextFocusRightId(linearLayouts[i].getId());
            }
        }else{
            for(int j=0;j<picture.length;j++){
                if(j+1>=picture.length) picture[j].setNextFocusRightId(buttonId[1]);
                else picture[j].setNextFocusRightId(pictureId[j+1]);
                if(j-1<0) picture[j].setNextFocusLeftId(buttonId[1]);
                else picture[j].setNextFocusLeftId(pictureId[j-1]);
                if(j-4<0){
                    int k=j+picture.length-picture.length%4;
                    while (k>=picture.length) k=k-4;
                    picture[j].setNextFocusUpId(pictureId[k]);
                }
                if(j+4>=picture.length){
                    picture[j].setNextFocusDownId(pictureId[j%4]);
                }else{
                    picture[j].setNextFocusDownId(pictureId[j+4]);
                }

            }
        }

    }

    private void setSleepAndPowerTime() {
        if(TvTimerManager.getInstance().isOffTimerEnable()) {
            Time dateTime = TvTimerManager.getInstance().getOffTimer();
            sleeptime_val.setText(dateTime.hour+":"+dateTime.minute);
        }else{
            sleeptime_val.setText(getString(R.string.str_mainmenu_default_switch_off));
        }
        if(TvTimerManager.getInstance().isOnTimerEnable()) {
            Time dateTime = TvTimerManager.getInstance().getOnTimer();
            power_on_time_val.setText(dateTime.hour+":"+dateTime.minute);
        }else{
            power_on_time_val.setText(getString(R.string.str_mainmenu_default_switch_off));
        }
    }

    private void enableSingleItemOrNot(LinearLayout linearLayout, boolean isEnable) {
        if (!isEnable) {
            ((TextView) (linearLayout.getChildAt(1))).setTextColor(Color.GRAY);
            ((TextView) (linearLayout.getChildAt(2))).setTextColor(Color.GRAY);
            linearLayout.setEnabled(false);
            linearLayout.setFocusable(false);
        } else {
            ((TextView) (linearLayout.getChildAt(1))).setTextColor(Color.WHITE);
            ((TextView) (linearLayout.getChildAt(2))).setTextColor(Color.WHITE);
            linearLayout.setEnabled(true);
            linearLayout.setFocusable(true);
        }
    }

    protected void addView()
    {
        viewFlipper= (ViewFlipper) findViewById(R.id.view_flipper_main_menu);
        this.lf = LayoutInflater.from(this);
        viewFlipper.setInAnimation(MainActivity.this,
                R.anim.left_in);
        viewFlipper.setOutAnimation(MainActivity.this,
                R.anim.right_out);
        this.viewFlipper.addView(lf.inflate(R.layout.mainmenu_general,null),0);
        this.viewFlipper.addView(lf.inflate(R.layout.mainmenu_picture,null),1);
        this.viewFlipper.addView(lf.inflate(R.layout.mainmenu_sound,null),2);
        this.viewFlipper.addView(lf.inflate(R.layout.mainmenu_channel,null),3);
        this.viewFlipper.addView(lf.inflate(R.layout.mainmenu_network,null),4);
        this.viewFlipper.addView(lf.inflate(R.layout.mainmenu_intelligence,null),5);
        this.viewFlipper.addView(lf.inflate(R.layout.mainmenu_system,null),6);
        this.viewFlipper.addView(lf.inflate(R.layout.mainmenu_about,null),7);
//
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        downTime=System.currentTimeMillis();
        //Log.d("Tufer:downTime",downTime+"");
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_UP:
                if(isBtnFocus){
                    if(posion==0) button[0].setBackgroundResource(0);
                    if(posion==4){
                        sourceInTvFocus();
                    }
                    button[posion].setBackgroundResource(0);
                    --posion;
                    if(posion==3&&!isSourceInTv()) {
                        posion--;
                        Toast.makeText(this,getString(R.string.str_mainmenu_channal_tip),Toast.LENGTH_SHORT).show();
                    }
                    flag=-1;
                    if(posion==-1) posion=7;
                    viewFlipper.setDisplayedChild(posion);
                    if(posion+1>7) button[0].requestFocusFromTouch();
                    else button[posion+1].requestFocusFromTouch();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if(isBtnFocus){
                    if(posion==0) button[0].setBackgroundResource(0);
                    if(posion==2){
                        sourceInTvFocus();
                    }
                    button[posion].setBackgroundResource(0);
                    posion++;
                    if(posion==3&&!isSourceInTv()) {
                        posion++;
                        Toast.makeText(this,getString(R.string.str_mainmenu_channal_tip),Toast.LENGTH_SHORT).show();
                    }
                    flag=-1;
                    if(posion==8) posion=0;
                    viewFlipper.setDisplayedChild(posion);
                    if(posion-1<0) {
                        button[7].requestFocusFromTouch();
                    }
                    else {
                        button[posion-1].requestFocusFromTouch();
                    }
                }

                break;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        /**
//         * 通用模块回调
//         */
        initGeneralCallback();
//        if(WeaterActivity.mWeather!=null&&WeaterActivity.mWeather.getWeatherInfoList().size()>0){
//            TextView tv=(TextView) general[3].getChildAt(2);
//            tv.setText(WeaterActivity.mWeather.getWeatherInfoList().get(0).getType());
//            tv.setSelected(true);
//        }
//        /**
//        * 图像模块回调初始化
//        */
//        pictrueNumber=new int[]{mTvPictureManager.getVideoItem(0),mTvPictureManager.getVideoItem(1),
//                mTvPictureManager.getBacklight(),mTvPictureManager.getVideoItem(4)};
////        for (int i=0;i<pictrueNumber.length;i++){
////            pictrueTextView[i].setText(pictrueNumber[i]+"");
////        }
//        pictrueTextView[pictruePosition].setText(pictrueNumber[pictruePosition]+"");
//        Toast.makeText(this,pictrueNumber[pictruePosition]+"",Toast.LENGTH_SHORT).show();
        /**
         * 智能模块回调
         */
        setSleepAndPowerTime();
//        /**
//         * 网络模块回调
//         */
//        initNetworkCallback();

        initSystemCallback();
    }

    private void initNetworkCallback() {
        wifi_val.setText(isWifiOn?getString(R.string.str_mainmenu_default_switch_on):
                getString(R.string.str_mainmenu_default_switch_off));
        wifihotspot_val.setText(isWifiHotspotOn?getString(R.string.str_mainmenu_default_switch_on):
                getString(R.string.str_mainmenu_default_switch_off));
        bluetooth_val.setText(isBuletoothOn?getString(R.string.str_mainmenu_default_switch_on):
                getString(R.string.str_mainmenu_default_switch_off));

    }

    private void initSystemCallback() {
        powermusic_val.setText(TvFactoryManager.getInstance().getPowerOnMusicMode()!=0?
                getString(R.string.str_mainmenu_default_switch_on):getString(R.string.str_mainmenu_default_switch_off));
        location_val.setText(getSharedPreferences(
                CitySettingActivity.SHARE_NAME, Context.MODE_PRIVATE).getString("cn_city_name", null));
//        if(flag==0&&posion==6){
//            MainMenu.clearFocus();
//            system[0].requestFocus();
//            system[0].requestFocusFromTouch();
//        }
        //Toast.makeText(this,posion+":"+flag,Toast.LENGTH_SHORT).show();

    }
    private void initAboutCallback(){
        local_name.setText(localName);
    }

    private void initSoundCallback() {
        soundmode_val.setText(soundModeList[tvAudioManager.getAudioSoundMode()]);
        srs_val.setText(tvAudioManager.isSRSEnable()?R.string.str_mainmenu_default_switch_on:R.string.str_mainmenu_default_switch_off);
        avc_val.setText(tvAudioManager.getAvcMode()?
                getString(R.string.str_mainmenu_default_switch_on):getString(R.string.str_mainmenu_default_switch_off));
        surround_val.setText(tvAudioManager.getAudioSurroundMode()==1?
                getString(R.string.str_mainmenu_default_switch_on):getString(R.string.str_mainmenu_default_switch_off));
        autohoh_val.setText(tvAudioManager.getHOHStatus()?
                getString(R.string.str_mainmenu_default_switch_on):getString(R.string.str_mainmenu_default_switch_off));

    }

    private void initGeneralCallback() {
        /**
         * 通用模块回调
         */
        powerinput_val.setText(powerInputString);
        if(WeaterActivity.mWeather!=null&&WeaterActivity.mWeather.getWeatherInfoList().size()>0){
            TextView tv=(TextView) general[3].getChildAt(2);
            tv.setText(WeaterActivity.mWeather.getWeatherInfoList().get(0).getType());
            tv.setSelected(true);
        }

    }

    private void initIntelligenceCallback() {
        setSleepAndPowerTime();
//        if(Settings.Global.getInt(getContentResolver(),Settings.Global.ON_INTELLIGENT_IDENTIFICATION, 0) == 0){
//            intelligenceTextView[0].setText(getString(R.string.str_mainmenu_default_switch_off));
//        }else{
//            intelligenceTextView[0].setText(getString(R.string.str_mainmenu_default_switch_on));
//        }
//        if(Settings.Global.getInt(getContentResolver(),Settings.Global.ON_SIGNAL_TV_STANBY, 0) == 0){
//            intelligenceTextView[7].setText(getString(R.string.str_mainmenu_default_switch_off));
//        }else{
//            intelligenceTextView[7].setText(getString(R.string.str_mainmenu_default_switch_on));
//        }
        setMenuDisPlayTime();
    }

    private void initPictureCallback() {
        picturemode_val.setText(pictureModeList[mTvPictureManager.getPictureMode()]);
        zoommode_val.setText(zoomModeList[mTvPictureManager.getVideoArcType()]);
        pictrueNumber=new int[]{mTvPictureManager.getVideoItem(0),mTvPictureManager.getVideoItem(1),
                mTvPictureManager.getBacklight(),mTvPictureManager.getVideoItem(4)};
        for (int i=0;i<pictrueNumber.length;i++){
            pictrueTextView[i].setText(pictrueNumber[i]+"");
        }
        color_temperature_val.setText(colorTemperatureList[mTvPictureManager.getColorTemprature()]);
        imgnoisereduction_val.setText(noiseReductionList[mTvPictureManager.getNoiseReduction()]);
        mpegnoisereduction_val.setText(getResources().getStringArray(R.array.str_arr_pic_mpegnoisereduction_vals)[mTvPictureManager.getMpegNoiseReduction()]);
        try{
            xvYCC_val.setText(mTvPictureManager.getxvYCCEnable()?getString(R.string.str_mainmenu_default_switch_on):
                    getString(R.string.str_mainmenu_default_switch_off));
        }catch (NoSuchMethodError e){
            e.printStackTrace();
        }
        if(mTvPictureManager.getPictureMode()!=3){
            enableSingleItemOrNot(picture[6],false);
            enableSingleItemOrNot(picture[7],false);
            enableSingleItemOrNot(picture[8],false);
            enableSingleItemOrNot(picture[9],false);
        }else {
            enableSingleItemOrNot(picture[6],true);
            enableSingleItemOrNot(picture[7],true);
            enableSingleItemOrNot(picture[8],true);
            enableSingleItemOrNot(picture[9],true);
        }
        setFocus(picture);
    }

    @Override
    public void onClick(View v) {
        downTime=System.currentTimeMillis();
        //Log.d("Tufer:downTime",downTime+"");
        boolean b=false;
        for(int i=0;i<=7;i++){
            if(v.getId()==buttonId[i]){
                button[posion].setBackgroundResource(0);
                b=true;
                posion=i;
                flag=-1;
                viewFlipper.setDisplayedChild(posion);
//                button[i].requestFocus();
//                button[i].requestFocusFromTouch();
                button[posion].setBackgroundResource(R.drawable.mainmenu_button1_focus);
//                Log.d("posion",posion+"");
//                Log.i("onclick",this.getWindow().getDecorView().findFocus().getId()+"");
                break;
            }
        }
        if(!b){
            if(isMenuShow){
                isMenuShow=false;
            }
            Intent intent ;
            SelectDialog dialog = null;
            switch (v.getId()){
                case R.id.linearlayout_general_powerinput:
                    flag=0;
//                    showPowerinput();
                    dialog= new SelectDialog(MainActivity.this,"PowerInput");
                    dialog.show();
                    //Toast.makeText(this,"您点击了powerinput", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_general_appmanager:
                    intent = new Intent(MainActivity.this,AppManagerActivity.class);
                    startActivity(intent);

                    flag=1;
                    //Toast.makeText(this,"您点击了appmanager", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_general_timedate:
                    intent = new Intent(MainActivity.this,DateTimeSettings.class);
                    startActivity(intent);
                    flag=2;
                    //Toast.makeText(this,"您点击了timedate", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_general_weather:
                    intent = new Intent(MainActivity.this,WeaterActivity.class);
                    startActivity(intent);
                    flag=3;
                    //Toast.makeText(this,"您点击了weather", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_picture_picturemode:
//                    intent = new Intent(MainActivity.this,PictureModeActivity.class);
//                    startActivity(intent);
                    dialog= new SelectDialog(MainActivity.this,"PictureMode");
                    dialog.show();
                    flag=0;
                    //Toast.makeText(this,"您点击了picturemode", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_picture_xvYCC:
                    dialog= new SelectDialog(MainActivity.this,"XvYCC");
                    dialog.show();
                    flag=1;
                    //Toast.makeText(this,"您点击了geometricadjust", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_picture_zoommode:
//                    intent = new Intent(MainActivity.this,ZoomModeActivity.class);
//                    startActivity(intent);
                    dialog= new SelectDialog(MainActivity.this,"ZoomMode");
                    dialog.show();
                    flag=2;
                    //Toast.makeText(this,"您点击了zoommode", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_picture_color_temperature:
//                    intent = new Intent(MainActivity.this,ColorTemperatureActivity.class);
//                    startActivity(intent);
                    dialog= new SelectDialog(MainActivity.this,"ColorTemperature");
                    dialog.show();
                    flag=3;
                    //Toast.makeText(this,"您点击了color_temperature", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_picture_mpegnoisereduction:
                    dialog= new SelectDialog(MainActivity.this,"MPEGNoiseReduction");
                    dialog.show();
                    flag=4;
                    break;
                case R.id.linearlayout_picture_imgnoisereduction:
//                    intent = new Intent(MainActivity.this,ImgNoiseReductionActivity.class);
//                    startActivity(intent);
                    dialog= new SelectDialog(MainActivity.this,"ImgNoiseReduction");
                    dialog.show();
                    flag=5;
                    //Toast.makeText(this,"您点击了imgnoisereduction", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_picture_brightness:
                    pictruePosition=0;
                    intent = new Intent(MainActivity.this,SetLightActivity.class);
                    startActivity(intent);
                    flag=6;
                    //Toast.makeText(this,"您点击了brightness", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_picture_contrast:
                    pictruePosition=1;
                    intent = new Intent(MainActivity.this,SetLightActivity.class);
                    startActivity(intent);
                    flag=7;
                    //Toast.makeText(this,"您点击了contrast", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_picture_backlight:
                    pictruePosition=2;
                    intent = new Intent(MainActivity.this,SetLightActivity.class);
                    startActivity(intent);
                    flag=8;
                    //Toast.makeText(this,"您点击了backlight", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_picture_hue:
                    pictruePosition=3;
                    intent = new Intent(MainActivity.this,SetLightActivity.class);
                    startActivity(intent);
                    flag=9;
                    //Toast.makeText(this,"您点击了hue", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_sound_soundmode:
//                    intent = new Intent(MainActivity.this,SoundModeActivity.class);
//                    startActivity(intent);
                    dialog= new SelectDialog(MainActivity.this,"SoundMode");
                    dialog.show();
                    flag=0;
                    //Toast.makeText(this,"您点击了soundmode", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_sound_srs:
//                    intent = new Intent(MainActivity.this,SrsActivity.class);
//                    startActivity(intent);
                    dialog= new SelectDialog(MainActivity.this,"SoundSrs");
                    dialog.show();
                    flag=1;
                    //Toast.makeText(this,"您点击了srs", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_sound_equalizer:
                    intent = new Intent(MainActivity.this,EqualizerActivity.class);
                    startActivity(intent);
                    flag=2;
                    //Toast.makeText(this,"您点击了equalizer", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_sound_avc:
                    dialog= new SelectDialog(MainActivity.this,"AVC");
                    dialog.show();
                    flag=3;
                    //Toast.makeText(this,"您点击了track", LENGTH_SHORT).show();
                    break;
                case R.id.linearlayout_sound_surround:
                    dialog= new SelectDialog(MainActivity.this,"Surround");
                    dialog.show();
                    flag=4;
                    break;
                case R.id.linearlayout_sound_auto_hoh:
                    dialog= new SelectDialog(MainActivity.this,"AutoHoh");
                    dialog.show();
                    flag=5;
                    break;
                case R.id.linearlayout_sound_mute:
                    if(isMute){
                        isMute=!isMute;
                        audio.setStreamVolume(AudioManager.STREAM_SYSTEM,volume,AudioManager.FLAG_PLAY_SOUND
                                | AudioManager.FLAG_SHOW_UI);
                    }else{
                        isMute=!isMute;
                        audio.setStreamVolume(AudioManager.STREAM_SYSTEM,0,AudioManager.FLAG_PLAY_SOUND
                                | AudioManager.FLAG_SHOW_UI);
                    }
                    mute_val.setText(audio.getStreamVolume( AudioManager.STREAM_SYSTEM  )==0?R.string.str_mainmenu_default_switch_on:R.string.str_mainmenu_default_switch_off);
                    flag=6;
                    break;
                case R.id.linearlayout_channel_autotuning:
                    intent = new Intent(MainActivity.this,ChannelActivity.class);
                    startActivity(intent);
                    flag=0;
                    break;
                case R.id.linearlayout_channel_programedit:
                    intent=new Intent(this, ProgramListViewActivity.class);
                    startActivity(intent);
                    flag=1;
                    break;
                case R.id.linearlayout_network_networkselection:
                    flag=0;
                    intent = new Intent(this,NetworkSettingsActivity.class);
                    intent.putExtra("network_type_number",0);
                    startActivity(intent);
                    break;
                case R.id.linearlayout_network_wifi:
                    intent = new Intent(this,NetworkSettingsActivity.class);
                    intent.putExtra("network_type_number",1);
                    startActivity(intent);
                    flag = 1;
                    break;
                case R.id.linearlayout_network_wifihotspot:
                    intent = new Intent(this,NetworkSettingsActivity.class);
                    intent.putExtra("network_type_number",2);
                    startActivity(intent);
                    flag = 2;
                    break;
                case R.id.linearlayout_network_networkdetails:
                    intent = new Intent(this,NetworkSettingsActivity.class);
                    intent.putExtra("network_type_number",3);
                    startActivity(intent);
                    flag = 3;
                    break;
                case R.id.linearlayout_network_bluetooth:
                    intent = new Intent(this,BluetoothActivity.class);
                    startActivity(intent);
                    flag=4;
                    break;
                case R.id.linearlayout_intelligence_identify:
                    flag=0;
                    intelligencePosion=flag;
                    dialog= new SelectDialog(MainActivity.this,"IntelligenceSwitch");
                    dialog.show();
                    break;
                case R.id.linearlayout_intelligence_childlock:
                    flag=1;
                    intelligencePosion=flag;
                    dialog= new SelectDialog(MainActivity.this,"IntelligenceSwitch");
                    dialog.show();
                    break;
                case R.id.linearlayout_intelligence_energysaving:
                    flag=2;
                    intelligencePosion=flag;
                    dialog= new SelectDialog(MainActivity.this,"IntelligenceSwitch");
                    dialog.show();
                    break;
                case R.id.linearlayout_intelligence_temperaturedet:
                    flag=3;
                    intelligencePosion=flag;
                    dialog= new SelectDialog(MainActivity.this,"IntelligenceSwitch");
                    dialog.show();
                    break;
                case R.id.linearlayout_intelligence_sensorlight:
                    flag=4;
                    intelligencePosion=flag;
                    dialog= new SelectDialog(MainActivity.this,"IntelligenceSwitch");
                    dialog.show();
                    break;
                case R.id.linearlayout_intelligence_protecteyes:
                    flag=5;
                    intelligencePosion=flag;
                    dialog= new SelectDialog(MainActivity.this,"IntelligenceSwitch");
                    dialog.show();
                    break;
                case R.id.linearlayout_intelligence_nosignalreturn:
                    flag=6;
                    intelligencePosion=flag;
                    dialog= new SelectDialog(MainActivity.this,"IntelligenceSwitch");
                    dialog.show();
                    break;
                case R.id.linearlayout_intelligence_nosignalstandby:
                    flag=7;
                    intelligencePosion=flag;
                    dialog= new SelectDialog(MainActivity.this,"IntelligenceSwitch");
                    dialog.show();
                    break;
                case R.id.linearlayout_intelligence_sleeptime:
                    flag=8;
                    isSleep=true;
                    intent=new Intent(MainActivity.this, SetTimeOffDialogActivity.class);
                    startActivity(intent);
                    break;
                case R.id.linearlayout_intelligence_power_on_time:
                    flag=9;
                    isSleep=false;
                    intent=new Intent(MainActivity.this, SetTimeOnDialogActivity.class);
                    startActivity(intent);
                    break;
                case R.id.linearlayout_intelligence_menu_display_time:
                    dialog= new SelectDialog(MainActivity.this,"MenuShowTime");
                    dialog.show();
                    flag=10;
                    break;
                case R.id.linearlayout_system_inputmethod:
                    intent=new Intent(MainActivity.this, InputMethodAndLanguageSettingsActivity.class);
                    startActivity(intent);
                    //finish();
                    flag=0;
                    break;
                case R.id.linearlayout_system_location:
                    flag=1;
                    intent=new Intent(MainActivity.this, CitySettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.linearlayout_system_powermusic:
                    dialog= new SelectDialog(MainActivity.this,"PowerMusic");
                    dialog.show();
                    flag=2;
                    break;
                case R.id.linearlayout_about_systeminfo:
                    intent=new Intent(MainActivity.this, SystemInfoActivity.class);
                    startActivity(intent);
                    flag=0;
                    break;
                case R.id.linearlayout_about_instructions:
                    flag=1;
                    break;
                case R.id.linearlayout_about_localname:
                    inputTitleDialog();
                    flag=2;
                    break;
                case R.id.linearlayout_about_restore:
                    intent=new Intent(MainActivity.this, SystemRestoreFactoryActivity.class);
                    startActivity(intent);
                    flag=3;
                    break;
                case R.id.linearlayout_about_local_upgrade:
                    intent=new Intent(MainActivity.this, SystemLocalUpdateActivity.class);
                    startActivity(intent);
                    flag=4;
                    break;
                case R.id.linearlayout_about_network_upgrade:
                    intent=new Intent(MainActivity.this,  SystemNetUpdateActivity.class);
                    startActivity(intent);
                    flag=5;
                    break;
            }



        }
    }

    private void inputTitleDialog() {

        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.str_mainmenu_about_localname))
                .setView(inputServer)
                .setNegativeButton(getString(R.string.str_mainmenu_dialog_cancel), null);
        builder.setPositiveButton(getString(R.string.str_mainmenu_dialog_confirm),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String inputName = inputServer.getText().toString();
                        if (inputName==null||inputName.equals("")){
                            Toast.makeText(MainActivity.this,"修改名不能为空，修改失败！",Toast.LENGTH_SHORT).show();
                        }else{
                            setLocalName(inputName);
                        }
                    }
                });
        builder.show();
    }

    public void setLocalName(String localName) {
        this.localName = localName;
        handler.sendEmptyMessage(MainActivity.UPDATE_ABOUT);
    }





    private void sourceInTvFocus() {
        if(isSourceInTv()){
            button[2].setNextFocusDownId(buttonId[3]);
            button[4].setNextFocusUpId(buttonId[3]);
            button[3].setTextColor(Color.WHITE);
            button[3].setFocusable(true);
            button[3].setEnabled(true);
        }else{
            button[2].setNextFocusDownId(buttonId[4]);
            button[4].setNextFocusUpId(buttonId[2]);
            button[3].setTextColor(Color.GRAY);
            button[3].setFocusable(false);
            button[3].setEnabled(false);
        }

    }





    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            //time=0;
            downTime=System.currentTimeMillis();
            //Log.d("Tufer",downTime+"");
        }
        return super.onTouchEvent(event);
    }
    private boolean isSourceInTv() {
        int curis = mTvCommonManager.getCurrentTvInputSource();
        //Toast.makeText(this, mTvCommonManager.getCurrentTvInputSource()+":"+SystemProperties.getBoolean(PROPERTY_TVPLAYER_STATUS,false),Toast.LENGTH_SHORT).show();
        if ((curis == TvCommonManager.INPUT_SOURCE_ATV || curis == TvCommonManager.INPUT_SOURCE_DTV)&& SystemProperties.getBoolean(PROPERTY_TVPLAYER_STATUS,false)) {
            return true;
        } else {
            return false;
        }
    }
    private boolean checkCurRunningActivity(String strPackage) {
        boolean res = false;
        ActivityManager mgr =(ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        RunningTaskInfo info = null;
        try {
            info = mgr.getRunningTasks(1).get(0);//launcher has the permission
        } catch (Exception e) {
            Log.d(TAG,"have no permission to getCurRunningActivityPackageName");
        }
        if(info != null && strPackage != null){
            if(strPackage.equals(info.topActivity.getPackageName())){
                res = true;
                Log.i(TAG, "top task is " + strPackage);
            }
        }
        return res;
    }

}
