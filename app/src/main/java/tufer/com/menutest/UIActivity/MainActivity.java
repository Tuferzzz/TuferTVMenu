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

import android.graphics.Color;

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


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.about.SystemInfoActivity;
import tufer.com.menutest.UIActivity.about.SystemRestoreFactoryActivity;
import tufer.com.menutest.UIActivity.bluetooth.BluetoothActivity;
import tufer.com.menutest.UIActivity.channel.ChannelActivity;
import tufer.com.menutest.UIActivity.channel.ProgramListViewActivity;

import tufer.com.menutest.UIActivity.general.appinfo.AppManagerActivity;


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


import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;


/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity  {
    public static final int UPDATE_PICTURE = 1;
    public static final int UPDATE_GENERAL = 0;
    public static final int UPDATE_SOUND = 2;
    public static final int UPDATE_CHANNEL = 3;
    public static final int UPDATE_NETWORK= 4;
    public static final int UPDATE_INTELLIGENCE = 5;
    public static final int UPDATE_SYSTEM = 6;
    public static final int UPDATE_ABOUT = 7;
    private String TAG="MainActivity";
    private int time=0;
    protected long downTime=0;
    protected boolean isMenuShow=false;
    private boolean isMenuShowThis=false;
    public static MainActivity myMainActivity=null;
    private static final String TVPLAYER_PACKAGE = "com.mstar.tv.tvplayer.ui";
    private static final String PROPERTY_TVPLAYER_STATUS = "persist.sys.istvplayer";
    private int menuDisplayTime=0;
    private LinearLayout MainMenu;
    MainMenuViewHolder mainMenuViewHolder;
    protected TvAudioManager tvAudioManager;
    protected TvPictureManager mTvPictureManager ;
    protected TvCommonManager mTvCommonManager;
    protected AudioManager audio;
    private TVRootApp mTvRootApp;
    public static int[] intelligenceNameList={R.string.str_mainmenu_intelligence_identify,R.string.str_mainmenu_intelligence_childlock,
            R.string.str_mainmenu_intelligence_energysaving,R.string.str_mainmenu_intelligence_temperaturedet,
            R.string.str_mainmenu_intelligence_sensorlight,R.string.str_mainmenu_intelligence_protecteyes,
            R.string.str_mainmenu_intelligence_nosignalreturn,R.string.str_mainmenu_intelligence_nosignal_standby,};
    protected ViewFlipper viewFlipper;
    protected LayoutInflater lf;
    protected int posion ;
    protected int flag;
    public static int generalInputPosition=0;
    public static int intelligencePosion=0;
    public static boolean isSleep;
    public static String powerInputString="";
    public static int[] pictrueTitleNameList={R.string.str_mainmenu_picture_brightness,
            R.string.str_mainmenu_picture_contrast,
            R.string.str_mainmenu_picture_backlight,R.string.str_mainmenu_picture_hue};
    public static int pictruePosition;
    protected int volume;
    protected static boolean isMute=false;
    public static boolean isWifiHotspotOn=false;
    public static boolean isWifiOn=false;
    public static boolean isBuletoothOn=false;
    protected boolean isIntelligence=false;

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
                case UPDATE_CHANNEL:
                    //initChannelCallback();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        mainMenuViewHolder=new MainMenuViewHolder(MainActivity.this);
        addView();
        initView();

    }

    @Override
    protected void onResume() {
        downTime=System.currentTimeMillis();
        setMenuDisPlayTime();
        sourceInTvFocus();
        super.onResume();
    }

    private void setMenuDisPlayTime() {
        SharedPreferences preferences=getSharedPreferences("TuferTvMenu", Context.MODE_PRIVATE);
        isMenuShow=preferences.getBoolean("isMenuShow", false);
        menuDisplayTime=preferences.getInt("menuDisplayTime", 0);
        if(isMenuShow){
            mainMenuViewHolder.menu_display_time_val.setText(menuDisplayTime+"s");
        }else{
            downTime=0;
            mainMenuViewHolder.menu_display_time_val.setText(getString(R.string.str_mainmenu_default_switch_off));
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

        mainMenuViewHolder.initView();

        MainMenu= (LinearLayout) findViewById(R.id.MainMenu);

        registerListeners();

    }

    private void registerListeners() {
        for (int i=0;i<mainMenuViewHolder.button.length;i++){
            final int finalI = i;
            mainMenuViewHolder.button[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downTime=System.currentTimeMillis();
                    if(finalI==3&&!isSourceInTv()){
                        Toast.makeText(MainActivity.this,getString(R.string.str_mainmenu_channal_tip),Toast.LENGTH_SHORT).show();
                    }else{
                        mainMenuViewHolder.setNoneBackground(posion,flag);
                        posion= finalI;
                        flag=-1;
                        mainMenuViewHolder.setBackground(posion,flag);
                        click();
                    }
                }
            });
        }
        for (int i=0;i<mainMenuViewHolder.general.length;i++){
            final int finalI = i;
            mainMenuViewHolder.general[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downTime=System.currentTimeMillis();
                    mainMenuViewHolder.setNoneBackground(posion,flag);
                    flag=finalI;
                    mainMenuViewHolder.setBackground(posion,flag);
                    click();
                }
            });
        }
        for (int i=0;i<mainMenuViewHolder.picture.length;i++){
            final int finalI = i;
            mainMenuViewHolder.picture[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downTime=System.currentTimeMillis();
                    if(mTvPictureManager.getPictureMode()!=3&&finalI>=6&&finalI<=9){
                        Toast.makeText(MainActivity.this, getString(R.string.str_only_usermode_available),Toast.LENGTH_SHORT).show();
                    }else{
                        mainMenuViewHolder.setNoneBackground(posion,flag);
                        flag=finalI;
                        mainMenuViewHolder.setBackground(posion,flag);
                        click();
                    }
                }
            });
        }
        for (int i=0;i<mainMenuViewHolder.sound.length;i++){
            final int finalI = i;
            mainMenuViewHolder.sound[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downTime=System.currentTimeMillis();
                    mainMenuViewHolder.setNoneBackground(posion,flag);
                    flag=finalI;
                    mainMenuViewHolder.setBackground(posion,flag);
                    click();
                }
            });
        }
        for (int i=0;i<mainMenuViewHolder.channel.length;i++){
            final int finalI = i;
            mainMenuViewHolder.channel[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downTime=System.currentTimeMillis();
                    mainMenuViewHolder.setNoneBackground(posion,flag);
                    flag=finalI;
                    mainMenuViewHolder.setBackground(posion,flag);
                    click();
                }
            });
        }
        for (int i=0;i<mainMenuViewHolder.network.length;i++){
            final int finalI = i;
            mainMenuViewHolder.network[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downTime=System.currentTimeMillis();
                    mainMenuViewHolder.setNoneBackground(posion,flag);
                    flag=finalI;
                    mainMenuViewHolder.setBackground(posion,flag);
                    click();
                }
            });
        }
        for (int i=0;i<mainMenuViewHolder.intelligence.length;i++){
            final int finalI = i;
            mainMenuViewHolder.intelligence[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downTime=System.currentTimeMillis();
                    mainMenuViewHolder.setNoneBackground(posion,flag);
                    flag=finalI;
                    mainMenuViewHolder.setBackground(posion,flag);
                    click();
                }
            });
        }
        for (int i=0;i<mainMenuViewHolder.system.length;i++){
            final int finalI = i;
            mainMenuViewHolder.system[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downTime=System.currentTimeMillis();
                    mainMenuViewHolder.setNoneBackground(posion,flag);
                    flag=finalI;
                    mainMenuViewHolder.setBackground(posion,flag);
                    click();
                }
            });
        }
        for (int i=0;i<mainMenuViewHolder.about.length;i++){
            final int finalI = i;
            mainMenuViewHolder.about[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downTime=System.currentTimeMillis();
                    mainMenuViewHolder.setNoneBackground(posion,flag);
                    flag=finalI;
                    mainMenuViewHolder.setBackground(posion,flag);
                    click();
                }
            });
        }
    }

    protected void setSleepAndPowerTime() {
        if(TvTimerManager.getInstance().isOffTimerEnable()) {
            Time dateTime = TvTimerManager.getInstance().getOffTimer();
            mainMenuViewHolder.sleeptime_val.setText(dateTime.hour+":"+dateTime.minute);
        }else{
            mainMenuViewHolder.sleeptime_val.setText(getString(R.string.str_mainmenu_default_switch_off));
        }
        if(TvTimerManager.getInstance().isOnTimerEnable()) {
            Time dateTime = TvTimerManager.getInstance().getOnTimer();
            mainMenuViewHolder.power_on_time_val.setText(dateTime.hour+":"+dateTime.minute);
        }else{
            mainMenuViewHolder.power_on_time_val.setText(getString(R.string.str_mainmenu_default_switch_off));
        }
    }

    protected void enableSingleItemOrNot(LinearLayout linearLayout, boolean isEnable) {
        if (!isEnable) {
            ((TextView) (linearLayout.getChildAt(1))).setTextColor(Color.GRAY);
            ((TextView) (linearLayout.getChildAt(2))).setTextColor(Color.GRAY);
        } else {
            ((TextView) (linearLayout.getChildAt(1))).setTextColor(Color.WHITE);
            ((TextView) (linearLayout.getChildAt(2))).setTextColor(Color.WHITE);
        }
    }

    protected void addView() {
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
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                dropUp();
                //Toast.makeText(this,"遥控上键："+posion+","+flag,Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                dropDown();
                //Toast.makeText(this,"遥控下键："+posion+","+flag,Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                dropLeft();
                //Toast.makeText(this,"遥控左键："+posion+","+flag,Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                dropRight();
                //Toast.makeText(this,"遥控右键："+posion+","+flag,Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_ENTER:
                click();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void dropUp() {
        if(flag==-1){
            if(posion==4&&!isSourceInTv()){
                mainMenuViewHolder.button[posion].setBackgroundResource(0);
                posion=2;
                mainMenuViewHolder.button[posion].setBackgroundResource(R.drawable.mainmenu_button1_focus);
            }else{
                mainMenuViewHolder.button[posion].setBackgroundResource(0);
                posion--;
                if(posion==-1) posion=7;
                mainMenuViewHolder.button[posion].setBackgroundResource(R.drawable.mainmenu_button1_focus);
            }
            viewFlipper.setDisplayedChild(posion);
        }else{
            switch (posion){
                case 0:
                    mainMenuViewHolder.general[flag].setBackgroundResource(0);
                    flag=getFlag(-4,mainMenuViewHolder.general.length-1);
                    if(flag!=-1) mainMenuViewHolder.general[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 1:
                    mainMenuViewHolder.picture[flag].setBackgroundResource(0);
                    if(mTvPictureManager.getPictureMode()!=3){
                        flag=getFlag(-4,5);
                    }else{
                        flag=getFlag(-4,9);
                    }
                    if(flag!=-1) mainMenuViewHolder.picture[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 2:
                    mainMenuViewHolder.sound[flag].setBackgroundResource(0);
                    flag=getFlag(-4,mainMenuViewHolder.sound.length-1);
                    if(flag!=-1) mainMenuViewHolder.sound[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 3:
                    mainMenuViewHolder.channel[flag].setBackgroundResource(0);
                    flag=getFlag(-4,mainMenuViewHolder.channel.length-1);
                    if(flag!=-1) mainMenuViewHolder.channel[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 4:
                    mainMenuViewHolder.network[flag].setBackgroundResource(0);
                    flag=getFlag(-4,mainMenuViewHolder.network.length-1);
                    if(flag!=-1) mainMenuViewHolder.network[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 5:
                    mainMenuViewHolder.intelligence[flag].setBackgroundResource(0);
                    flag=getFlag(-4,mainMenuViewHolder.intelligence.length-1);
                    if(flag!=-1) mainMenuViewHolder.intelligence[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 6:
                    mainMenuViewHolder.system[flag].setBackgroundResource(0);
                    flag=getFlag(-4,mainMenuViewHolder.system.length-1);
                    if(flag!=-1) mainMenuViewHolder.system[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 7:
                    mainMenuViewHolder.about[flag].setBackgroundResource(0);
                    flag=getFlag(-4,mainMenuViewHolder.about.length-1);
                    if(flag!=-1) mainMenuViewHolder.about[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
            }
        }
    }

    private void dropDown() {
        if(flag==-1){
            if(posion==2&&!isSourceInTv()){
                mainMenuViewHolder.button[posion].setBackgroundResource(0);
                posion=4;
                mainMenuViewHolder.button[posion].setBackgroundResource(R.drawable.mainmenu_button1_focus);
            }else{
                mainMenuViewHolder.button[posion].setBackgroundResource(0);
                posion++;
                if(posion==8) posion=0;
                mainMenuViewHolder.button[posion].setBackgroundResource(R.drawable.mainmenu_button1_focus);
            }
            viewFlipper.setDisplayedChild(posion);
        }else{
            switch (posion){
                case 0:
                    mainMenuViewHolder.general[flag].setBackgroundResource(0);
                    flag=getFlag(4,mainMenuViewHolder.general.length-1);
                    if(flag!=-1) mainMenuViewHolder.general[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 1:
                    mainMenuViewHolder.picture[flag].setBackgroundResource(0);
                    if(mTvPictureManager.getPictureMode()!=3){
                        flag=getFlag(4,5);
                    }else{
                        flag=getFlag(4,9);
                    }
                    if(flag!=-1) mainMenuViewHolder.picture[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 2:
                    mainMenuViewHolder.sound[flag].setBackgroundResource(0);
                    flag=getFlag(4,mainMenuViewHolder.sound.length-1);
                    if(flag!=-1) mainMenuViewHolder.sound[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 3:
                    mainMenuViewHolder.channel[flag].setBackgroundResource(0);
                    flag=getFlag(4,mainMenuViewHolder.channel.length-1);
                    if(flag!=-1) mainMenuViewHolder.channel[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 4:
                    mainMenuViewHolder.network[flag].setBackgroundResource(0);
                    flag=getFlag(4,mainMenuViewHolder.network.length-1);
                    if(flag!=-1) mainMenuViewHolder.network[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 5:
                    mainMenuViewHolder.intelligence[flag].setBackgroundResource(0);
                    flag=getFlag(4,mainMenuViewHolder.intelligence.length-1);
                    if(flag!=-1) mainMenuViewHolder.intelligence[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 6:
                    mainMenuViewHolder.system[flag].setBackgroundResource(0);
                    flag=getFlag(4,mainMenuViewHolder.system.length-1);
                    if(flag!=-1) mainMenuViewHolder.system[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 7:
                    mainMenuViewHolder.about[flag].setBackgroundResource(0);
                    flag=getFlag(4,mainMenuViewHolder.about.length-1);
                    if(flag!=-1) mainMenuViewHolder.about[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
            }
        }
    }

    private void dropLeft() {
        if(flag==-1){

        }else{
            switch (posion){
                case 0:
                    mainMenuViewHolder.general[flag].setBackgroundResource(0);
                    flag=getFlag(-1,mainMenuViewHolder.general.length-1);
                    if(flag!=-1) mainMenuViewHolder.general[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 1:
                    mainMenuViewHolder.picture[flag].setBackgroundResource(0);
                    if(mTvPictureManager.getPictureMode()!=3){
                        flag=getFlag(-1,5);
                    }else{
                        flag=getFlag(-1,9);
                    }
                    if(flag!=-1) mainMenuViewHolder.picture[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 2:
                    mainMenuViewHolder.sound[flag].setBackgroundResource(0);
                    flag=getFlag(-1,mainMenuViewHolder.sound.length-1);
                    if(flag!=-1) mainMenuViewHolder.sound[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 3:
                    mainMenuViewHolder.channel[flag].setBackgroundResource(0);
                    flag=getFlag(-1,mainMenuViewHolder.channel.length-1);
                    if(flag!=-1) mainMenuViewHolder.channel[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 4:
                    mainMenuViewHolder.network[flag].setBackgroundResource(0);
                    flag=getFlag(-1,mainMenuViewHolder.network.length-1);
                    if(flag!=-1) mainMenuViewHolder.network[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 5:
                    mainMenuViewHolder.intelligence[flag].setBackgroundResource(0);
                    flag=getFlag(-1,mainMenuViewHolder.intelligence.length-1);
                    if(flag!=-1) mainMenuViewHolder.intelligence[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 6:
                    mainMenuViewHolder.system[flag].setBackgroundResource(0);
                    flag=getFlag(-1,mainMenuViewHolder.system.length-1);
                    if(flag!=-1) mainMenuViewHolder.system[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 7:
                    mainMenuViewHolder.about[flag].setBackgroundResource(0);
                    flag=getFlag(-1,mainMenuViewHolder.about.length-1);
                    if(flag!=-1) mainMenuViewHolder.about[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
            }
        }
    }

    private void dropRight() {
        if(flag==-1){
            flag=0;
            switch (posion){
                case 0:
                    mainMenuViewHolder.general[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 1:
                    mainMenuViewHolder.picture[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 2:
                    mainMenuViewHolder.sound[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 3:
                    mainMenuViewHolder.channel[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 4:
                    mainMenuViewHolder.network[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 5:
                    mainMenuViewHolder.intelligence[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 6:
                    mainMenuViewHolder.system[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 7:
                    mainMenuViewHolder.about[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
            }
        }
       else{
            switch (posion){
                case 0:
                    mainMenuViewHolder.general[flag].setBackgroundResource(0);
                    flag=getFlag(1,mainMenuViewHolder.general.length-1);
                    if(flag!=-1) mainMenuViewHolder.general[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 1:
                    mainMenuViewHolder.picture[flag].setBackgroundResource(0);
                    if(mTvPictureManager.getPictureMode()!=3){
                        flag=getFlag(1,5);
                    }else{
                        flag=getFlag(1,9);
                    }
                    if(flag!=-1) mainMenuViewHolder.picture[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 2:
                    mainMenuViewHolder.sound[flag].setBackgroundResource(0);
                    flag=getFlag(1,mainMenuViewHolder.sound.length-1);
                    if(flag!=-1) mainMenuViewHolder.sound[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 3:
                    mainMenuViewHolder.channel[flag].setBackgroundResource(0);
                    flag=getFlag(1,mainMenuViewHolder.channel.length-1);
                    if(flag!=-1) mainMenuViewHolder.channel[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 4:
                    mainMenuViewHolder.network[flag].setBackgroundResource(0);
                    flag=getFlag(1,mainMenuViewHolder.network.length-1);
                    if(flag!=-1) mainMenuViewHolder.network[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 5:
                    mainMenuViewHolder.intelligence[flag].setBackgroundResource(0);
                    flag=getFlag(1,mainMenuViewHolder.intelligence.length-1);
                    if(flag!=-1) mainMenuViewHolder.intelligence[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 6:
                    mainMenuViewHolder.system[flag].setBackgroundResource(0);
                    flag=getFlag(1,mainMenuViewHolder.system.length-1);
                    if(flag!=-1) mainMenuViewHolder.system[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 7:
                    mainMenuViewHolder.about[flag].setBackgroundResource(0);
                    flag=getFlag(1,mainMenuViewHolder.about.length-1);
                    if(flag!=-1) mainMenuViewHolder.about[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
            }
        }
    }

    private void initNetworkCallback() {
        mainMenuViewHolder.wifi_val.setText(isWifiOn?getString(R.string.str_mainmenu_default_switch_on):
                getString(R.string.str_mainmenu_default_switch_off));
        mainMenuViewHolder.wifihotspot_val.setText(isWifiHotspotOn?getString(R.string.str_mainmenu_default_switch_on):
                getString(R.string.str_mainmenu_default_switch_off));
        mainMenuViewHolder.bluetooth_val.setText(isBuletoothOn?getString(R.string.str_mainmenu_default_switch_on):
                getString(R.string.str_mainmenu_default_switch_off));
    }

    private void initSystemCallback() {
        mainMenuViewHolder.powermusic_val.setText(TvFactoryManager.getInstance().getPowerOnMusicMode()!=0?
                getString(R.string.str_mainmenu_default_switch_on):getString(R.string.str_mainmenu_default_switch_off));
        mainMenuViewHolder.location_val.setText(getSharedPreferences(
                CitySettingActivity.SHARE_NAME, Context.MODE_PRIVATE).getString("cn_city_name", null));
    }

    private void initAboutCallback(){
        mainMenuViewHolder.local_name.setText(getSharedPreferences("MyTvSetting",0).getString(
                "LocalName",android.os.Build.MODEL
        ));
    }

    private void initSoundCallback() {
        mainMenuViewHolder.soundmode_val.setText(getResources().getStringArray(R.array.str_arr_sound_soundmode_vals)
                [tvAudioManager.getAudioSoundMode()]);
        mainMenuViewHolder.srs_val.setText(tvAudioManager.isSRSEnable()?R.string.str_mainmenu_default_switch_on:R.string.str_mainmenu_default_switch_off);
        mainMenuViewHolder.avc_val.setText(tvAudioManager.getAvcMode()?
                getString(R.string.str_mainmenu_default_switch_on):getString(R.string.str_mainmenu_default_switch_off));
        mainMenuViewHolder.surround_val.setText(tvAudioManager.getAudioSurroundMode()==1?
                getString(R.string.str_mainmenu_default_switch_on):getString(R.string.str_mainmenu_default_switch_off));
        mainMenuViewHolder.autohoh_val.setText(tvAudioManager.getHOHStatus()?
                getString(R.string.str_mainmenu_default_switch_on):getString(R.string.str_mainmenu_default_switch_off));

    }

    private void initGeneralCallback() {
        /**
         * 通用模块回调
         */
        mainMenuViewHolder.powerinput_val.setText(powerInputString);
        mainMenuViewHolder.auxiliary_val.setText(getString(R.string.str_mainmenu_default_switch_off));
        if(WeaterActivity.mWeather!=null&&WeaterActivity.mWeather.getWeatherInfoList().size()>0){
            TextView tv=(TextView) mainMenuViewHolder.general[3].getChildAt(2);
            tv.setText(WeaterActivity.mWeather.getWeatherInfoList().get(0).getType());
            tv.setSelected(true);
        }
    }

    private void initIntelligenceCallback() {
        setSleepAndPowerTime();
//        if(Settings.Global.getInt(getContentResolver(),Settings.Global.ON_INTELLIGENT_IDENTIFICATION, 0) == 0){
//            mainMenuViewHolder.intelligenceTextView[0].setText(getString(R.string.str_mainmenu_default_switch_off));
//        }else{
//            mainMenuViewHolder.intelligenceTextView[0].setText(getString(R.string.str_mainmenu_default_switch_on));
//        }
//        if(Settings.Global.getInt(getContentResolver(),Settings.Global.ON_SIGNAL_TV_STANBY, 0) == 0){
//            mainMenuViewHolder.intelligenceTextView[7].setText(getString(R.string.str_mainmenu_default_switch_off));
//        }else{
//            mainMenuViewHolder.intelligenceTextView[7].setText(getString(R.string.str_mainmenu_default_switch_on));
//        }
        //setMenuDisPlayTime();
    }

    private void initPictureCallback() {
        mainMenuViewHolder.picturemode_val.setText(getResources().getStringArray(R.array.str_arr_picture_picturemode_vals)
                [mTvPictureManager.getPictureMode()]);
        mainMenuViewHolder.zoommode_val.setText(getResources().getStringArray(R.array.str_arr_picture_zoommode_vals)
                [mTvPictureManager.getVideoArcType()]);
        mainMenuViewHolder.pictrueNumber=new int[]{mTvPictureManager.getVideoItem(0),mTvPictureManager.getVideoItem(1),
                mTvPictureManager.getBacklight(),mTvPictureManager.getVideoItem(4)};
        for (int i=0;i<mainMenuViewHolder.pictrueNumber.length;i++){
            mainMenuViewHolder.pictrueTextView[i].setText(mainMenuViewHolder.pictrueNumber[i]+"");
        }
        mainMenuViewHolder.color_temperature_val.setText(getResources().getStringArray(R.array.str_arr_picture_colortemperature_vals)[mTvPictureManager.getColorTemprature()]);
        mainMenuViewHolder.imgnoisereduction_val.setText(getResources().getStringArray(R.array.str_arr_pic_imgnoisereduction_vals)[mTvPictureManager.getNoiseReduction()]);
        mainMenuViewHolder.mpegnoisereduction_val.setText(getResources().getStringArray(R.array.str_arr_pic_mpegnoisereduction_vals)[mTvPictureManager.getMpegNoiseReduction()]);
        try{
            mainMenuViewHolder.xvYCC_val.setText(mTvPictureManager.getxvYCCEnable()?getString(R.string.str_mainmenu_default_switch_on):
                    getString(R.string.str_mainmenu_default_switch_off));
        }catch (NoSuchMethodError e){
            e.printStackTrace();
        }
        if(mTvPictureManager.getPictureMode()!=3){
            enableSingleItemOrNot(mainMenuViewHolder.picture[6],false);
            enableSingleItemOrNot(mainMenuViewHolder.picture[7],false);
            enableSingleItemOrNot(mainMenuViewHolder.picture[8],false);
            enableSingleItemOrNot(mainMenuViewHolder.picture[9],false);
        }else {
            enableSingleItemOrNot(mainMenuViewHolder.picture[6],true);
            enableSingleItemOrNot(mainMenuViewHolder.picture[7],true);
            enableSingleItemOrNot(mainMenuViewHolder.picture[8],true);
            enableSingleItemOrNot(mainMenuViewHolder.picture[9],true);
        }
    }

    private void inputTitleDialog() {

        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.str_mainmenu_about_localname))
                .setView(inputServer)
                .setNegativeButton(getString(R.string.str_mainmenu_dialog_cancel),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downTime=System.currentTimeMillis();
                        if(isMenuShowThis){
                            isMenuShow=true;
                            isMenuShowThis=false;
                        }
                    }
                }
                );
        builder.setPositiveButton(getString(R.string.str_mainmenu_dialog_confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(isMenuShowThis){
                            downTime=System.currentTimeMillis();
                            isMenuShow=true;
                            isMenuShowThis=false;
                        }
                        String inputName = inputServer.getText().toString();
                        if (inputName==null||inputName.equals("")){
                            Toast.makeText(MainActivity.this,getString(R.string.rename_null),Toast.LENGTH_SHORT).show();
                        }else{
                            if (inputName!=null) {
                                Pattern p = Pattern.compile("\t|\r|\n");
                                Matcher m = p.matcher(inputName);
                                inputName = m.replaceAll("");
                            }
                            setLocalName(inputName);
                        }
//                        setFocusCallback();
                    }
                });
        builder.show();
    }

    public void setLocalName(String localName) {
        SharedPreferences.Editor localEditor = getSharedPreferences("MyTvSetting", 0).edit();
        localEditor.putString("LocalName",localName );
        localEditor.apply();
        handler.sendEmptyMessage(MainActivity.UPDATE_ABOUT);
    }

    protected void sourceInTvFocus() {
        if(isSourceInTv()){
            mainMenuViewHolder.button[3].setTextColor(Color.WHITE);
        }else{
            mainMenuViewHolder.button[3].setTextColor(Color.GRAY);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            downTime=System.currentTimeMillis();
        }
        return super.onTouchEvent(event);
    }

    protected boolean isSourceInTv() {
        int curis = mTvCommonManager.getCurrentTvInputSource();
        //Toast.makeText(this,curis+":"+SystemProperties.getBoolean(PROPERTY_TVPLAYER_STATUS,false),Toast.LENGTH_SHORT).show();
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

    public int getFlag(int i,int len) {
        if(flag-4<0&&flag+4>len){
            if(i==-4){
                return flag;
            }else if(i==4){
                return flag;
            }
        }else if(flag-4>=0&&flag+4>len){
            if(i==-4){
                return flag-4;
            }else if(i==4){
                return flag%4;
            }
        }else if(flag-4<0&&flag+4<=len){
            if(i==-4){
                return flag+(len/4)*4>len?flag+(len/4)*4-4:flag+(len/4)*4;
            }else if(i==4){
                return flag+4;
            }
        }
        else if(flag-4>=0&&flag+4<=len){
            if(i==-4){
                return flag-4;
            }else if(i==4){
                return flag+4;
            }
        }
        if(i==-1){
            return flag-1;
        }else if(i==1){
            if(flag==len){
                return -1;
            } else{
                return flag+1;
            }
        }
        return flag;
    }

    public void click() {
        if(isMenuShow){
            isMenuShow=false;
            if(posion==7&&flag==2){
                isMenuShowThis=true;
            }
        }
        if(flag==-1){
            viewFlipper.setDisplayedChild(posion);
        }else{
            Intent intent ;
            switch (posion){
                case 0:
                    switch (flag){
                        case 0:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","PowerInput");
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent(MainActivity.this,AppManagerActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(MainActivity.this,DateTimeSettings.class);
                            startActivity(intent);
                            break;
                        case 3:
                            intent = new Intent(MainActivity.this,WeaterActivity.class);
                            startActivity(intent);
                            break;
                        case 4:
                            intent = new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","Auxiliary");
                            startActivity(intent);
                            break;
                    }
                    break;
                case 1:
                    switch (flag){
                        case 0:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","PictureMode");
                            startActivity(intent);
                            break;
                        case 1:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","XvYCC");
                            startActivity(intent);
                            break;
                        case 2:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","ZoomMode");
                            startActivity(intent);
                            break;
                        case 3:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","ColorTemperature");
                            startActivity(intent);
                            break;
                        case 4:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","MPEGNoiseReduction");
                            startActivity(intent);
                            break;
                        case 5:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","ImgNoiseReduction");
                            startActivity(intent);
                            break;
                        case 6:
                            if(mTvPictureManager.getPictureMode()==3){
                                pictruePosition=0;
                                intent = new Intent(MainActivity.this,SetLightActivity.class);
                                startActivity(intent);
                                flag=6;
                            }else{
                                Toast.makeText(this, getString(R.string.str_only_usermode_available),Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 7:
                            if(mTvPictureManager.getPictureMode()==3){
                                pictruePosition=1;
                                intent = new Intent(MainActivity.this,SetLightActivity.class);
                                startActivity(intent);
                                flag=7;
                            }else{
                                Toast.makeText(this, getString(R.string.str_only_usermode_available),Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 8:
                            if(mTvPictureManager.getPictureMode()==3){
                                pictruePosition=2;
                                intent = new Intent(MainActivity.this,SetLightActivity.class);
                                startActivity(intent);
                                flag=8;
                            }else{
                                Toast.makeText(this, getString(R.string.str_only_usermode_available),Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 9:
                            if(mTvPictureManager.getPictureMode()==3){
                                pictruePosition=3;
                                intent = new Intent(MainActivity.this,SetLightActivity.class);
                                startActivity(intent);
                                flag=9;
                            }else{
                                Toast.makeText(this, getString(R.string.str_only_usermode_available),Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                    break;
                case 2:
                    switch (flag) {
                        case 0:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","SoundMode");
                            startActivity(intent);
                            break;
                        case 1:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","SoundSrs");
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(MainActivity.this,EqualizerActivity.class);
                            startActivity(intent);
                            break;
                        case 3:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","AVC");
                            startActivity(intent);
                            break;
                        case 4:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","Surround");
                            startActivity(intent);
                            break;
                        case 5:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","AutoHoh");
                            startActivity(intent);
                            break;
                        case 6:
                            if(isMute){
                                isMute=!isMute;
                                audio.setStreamVolume(AudioManager.STREAM_SYSTEM,volume,AudioManager.FLAG_PLAY_SOUND
                                        | AudioManager.FLAG_SHOW_UI);
                            }else{
                                isMute=!isMute;
                                audio.setStreamVolume(AudioManager.STREAM_SYSTEM,0,AudioManager.FLAG_PLAY_SOUND
                                        | AudioManager.FLAG_SHOW_UI);
                            }
                            mainMenuViewHolder.mute_val.setText(audio.getStreamVolume( AudioManager.STREAM_SYSTEM  )==0?R.string.str_mainmenu_default_switch_on:R.string.str_mainmenu_default_switch_off);
                            break;
                    }
                    break;
                case 3:
                    switch (flag) {
                        case 0:
                            intent = new Intent(MainActivity.this,ChannelActivity.class);
                            startActivity(intent);
                            break;
                        case 1:
                            intent=new Intent(this, ProgramListViewActivity.class);
                            startActivity(intent);
                            break;
                    }
                    break;
                case 4:
                    switch (flag) {
                        case 0:
                            intent = new Intent(this,NetworkSettingsActivity.class);
                            intent.putExtra("network_type_number",0);
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent(this,NetworkSettingsActivity.class);
                            intent.putExtra("network_type_number",1);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(this,NetworkSettingsActivity.class);
                            intent.putExtra("network_type_number",2);
                            startActivity(intent);
                            break;
                        case 3:
                            intent = new Intent(this,NetworkSettingsActivity.class);
                            intent.putExtra("network_type_number",3);
                            startActivity(intent);
                            break;
                        case 4:
                            intent = new Intent(this,BluetoothActivity.class);
                            startActivity(intent);
                            break;
                    }
                    break;
                case 5:
                    switch (flag){
                        case 0:
                            intelligencePosion=flag;
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","IntelligenceSwitch");
                            startActivity(intent);
                            break;
                        case 1:
                            intelligencePosion=flag;
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","IntelligenceSwitch");
                            startActivity(intent);
                            break;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            if(isIntelligence){
                                intelligencePosion=flag;
                                intent=new Intent(MainActivity.this,SelectDialog.class);
                                intent.putExtra("Type","IntelligenceSwitch");
                                startActivity(intent);
                            }else{
                                Toast.makeText(this, getString(R.string.str_function_not_open),Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 7:
                            intelligencePosion=flag;
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","IntelligenceSwitch");
                            startActivity(intent);
                            break;
                        case 8:
                            isSleep=true;
                            intent=new Intent(MainActivity.this, SetTimeOffDialogActivity.class);
                            startActivity(intent);
                            break;
                        case 9:
                            isSleep=false;
                            intent=new Intent(MainActivity.this, SetTimeOnDialogActivity.class);
                            startActivity(intent);
                            break;
                        case 10:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","MenuShowTime");
                            startActivity(intent);
                            break;
                    }
                    break;
                case 6:
                    switch (flag) {
                        case 0:
                            intent=new Intent(MainActivity.this, InputMethodAndLanguageSettingsActivity.class);
                            startActivity(intent);
                            break;
                        case 1:
                            intent=new Intent(MainActivity.this, CitySettingActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            intent=new Intent(MainActivity.this,SelectDialog.class);
                            intent.putExtra("Type","PowerMusic");
                            startActivity(intent);
                            break;
                    }
                    break;
                case 7:
                    switch (flag) {
                        case 0:
                            intent=new Intent(MainActivity.this, SystemInfoActivity.class);
                            startActivity(intent);
                            break;
                        case 1:
                            Toast.makeText(this, getString(R.string.str_function_not_open),Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            inputTitleDialog();
                            break;
                        case 3:
                            intent=new Intent(MainActivity.this, SystemRestoreFactoryActivity.class);
                            startActivity(intent);
                            break;
                        case 4:
                            intent=new Intent(MainActivity.this, SystemLocalUpdateActivity.class);
                            startActivity(intent);
                            break;
                        case 5:
                            intent=new Intent(MainActivity.this,  SystemNetUpdateActivity.class);
                            startActivity(intent);
                            break;
                    }
                    break;

            }
        }
    }
}
