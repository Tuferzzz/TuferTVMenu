package tufer.com.menutest.UIActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.provider.Settings;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mstar.android.tv.TvAudioManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tv.TvTimerManager;

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


/**
 * Created by Administrator on 2017/8/25 0025.
 */

public class MainMenuViewHolder {

    private MainActivity activity;

    Button[] button;
    int[] buttonId={R.id.generalButton,R.id.pictureButton,
            R.id.soundButton,R.id.channelButton,
            R.id.networkButton,R.id.intelligenceButton,
            R.id.systemButton,R.id.aboutButton};

    LinearLayout[] general;
    int[] generalId={R.id.linearlayout_general_powerinput,R.id.linearlayout_general_appmanager,
            R.id.linearlayout_general_timedate,R.id.linearlayout_general_weather,R.id.linearlayout_general_wind};
    TextView powerinput_val,auxiliary_val;

    LinearLayout[] picture;
    int[] pictureId={R.id.linearlayout_picture_picturemode,R.id.linearlayout_picture_xvYCC,
            R.id.linearlayout_picture_zoommode,R.id.linearlayout_picture_color_temperature,
            R.id.linearlayout_picture_mpegnoisereduction,R.id.linearlayout_picture_imgnoisereduction,
            R.id.linearlayout_picture_brightness,
            R.id.linearlayout_picture_contrast,R.id.linearlayout_picture_backlight,
            R.id.linearlayout_picture_hue};
    TextView picturemode_val,xvYCC_val,zoommode_val,color_temperature_val,imgnoisereduction_val,mpegnoisereduction_val;
    int[] pictrueNumber;
    int[] pictrueTextViewId={R.id.textview_picture_brightness_val,R.id.textview_picture_contrast_val,
            R.id.textview_picture_backlight_val,R.id.textview_picture_hue_val};
    TextView[] pictrueTextView;

    LinearLayout[] sound;
    int[] soundId={R.id.linearlayout_sound_soundmode,R.id.linearlayout_sound_srs,
            R.id.linearlayout_sound_equalizer,
            R.id.linearlayout_sound_avc,R.id.linearlayout_sound_surround,
            R.id.linearlayout_sound_auto_hoh,R.id.linearlayout_sound_mute};
    TextView soundmode_val,srs_val,avc_val,surround_val,autohoh_val,mute_val;

    LinearLayout[] channel;
    int[] channelId={R.id.linearlayout_channel_autotuning,
            R.id.linearlayout_channel_programedit};

    LinearLayout[] network;
    int[] networkId={R.id.linearlayout_network_networkselection,R.id.linearlayout_network_wifi,
            R.id.linearlayout_network_wifihotspot,
            R.id.linearlayout_network_networkdetails,
            R.id.linearlayout_network_bluetooth};
    TextView wifihotspot_val,wifi_val,bluetooth_val;

    LinearLayout[] intelligence;
    int[] intelligenceId={R.id.linearlayout_intelligence_identify,R.id.linearlayout_intelligence_childlock,
            R.id.linearlayout_intelligence_energysaving,R.id.linearlayout_intelligence_temperaturedet,
            R.id.linearlayout_intelligence_sensorlight,R.id.linearlayout_intelligence_protecteyes,
            R.id.linearlayout_intelligence_nosignalreturn,R.id.linearlayout_intelligence_nosignalstandby,
            R.id.linearlayout_intelligence_sleeptime,R.id.linearlayout_intelligence_power_on_time,
            R.id.linearlayout_intelligence_menu_display_time};
    TextView sleeptime_val,power_on_time_val,menu_display_time_val;
    TextView[] intelligenceTextView;
    int[] intelligenceTextViewId={R.id.textview_intelligence_identify_val,
            R.id.textview_intelligence_childlock_val,
            R.id.textview_intelligence_energysaving_val,R.id.textview_intelligence_temperaturedet_val,
            R.id.textview_intelligence_sensorlight_val,R.id.textview_intelligence_protecteyes_val,
            R.id.textview_intelligence_nosignalreturn_val,R.id.textview_intelligence_nosignalstandby_val};

    LinearLayout[] system;
    int[] systemId={R.id.linearlayout_system_inputmethod,R.id.linearlayout_system_location,
            R.id.linearlayout_system_powermusic};
    TextView location_val,powermusic_val;

    LinearLayout[] about;
    int[] aboutId={R.id.linearlayout_about_systeminfo,R.id.linearlayout_about_instructions,
            R.id.linearlayout_about_localname,
            R.id.linearlayout_about_restore,R.id.linearlayout_about_local_upgrade,R.id.linearlayout_about_network_upgrade};
    TextView local_name;

    public MainMenuViewHolder(MainActivity activity){
        this.activity=activity;
    }

    public void initView() {

        initButton();

        initGeneral();

        initPicture();

        initSound();

        initChannel();

        initNetWork();

        initIntelligence();

        initSystem();

        initAbout();
    }

    private void initButton() {
        button=new Button[8];
        for (int i=0;i<button.length;i++){
            button[i]=new Button(activity);
            button[i]= (Button) activity.findViewById(buttonId[i]);
            button[i].setFocusable(false);
            button[i].setClickable(false);
        }
    }

    private void initGeneral() {
        general=new LinearLayout[5];
        for(int i=0;i<general.length;i++){
            general[i]=new LinearLayout(activity);
            general[i]= (LinearLayout) activity.findViewById(generalId[i]);
            general[i].setFocusable(false);
            general[i].setClickable(false);
        }
        powerinput_val= (TextView) activity.findViewById(R.id.textview_general_powerinput_val);
        auxiliary_val= (TextView) activity.findViewById(R.id.textview_general_wind_val);
        auxiliary_val.setText(activity.getString(R.string.str_mainmenu_default_switch_off));
    }

    private void initPicture() {
        picture=new LinearLayout[10];
        for(int i=0;i<picture.length;i++){
            picture[i]=new LinearLayout(activity);
            picture[i]= (LinearLayout) activity.findViewById(pictureId[i]);
            picture[i].setFocusable(false);
            picture[i].setClickable(false);
        }
        picturemode_val= (TextView) activity.findViewById(R.id.textview_picture_picturemode_val);
        picturemode_val.setText(activity.getResources().getStringArray(R.array.str_arr_picture_picturemode_vals)
                [activity.mTvPictureManager.getPictureMode()]);
        if(activity.mTvPictureManager.getPictureMode()!=3){
            activity.enableSingleItemOrNot(picture[6],false);
            activity.enableSingleItemOrNot(picture[7],false);
            activity.enableSingleItemOrNot(picture[8],false);
            activity.enableSingleItemOrNot(picture[9],false);

        }else {
            activity.enableSingleItemOrNot(picture[6],true);
            activity.enableSingleItemOrNot(picture[7],true);
            activity.enableSingleItemOrNot(picture[8],true);
            activity.enableSingleItemOrNot(picture[9],true);
        }
        //setFocus(picture);
        zoommode_val= (TextView) activity.findViewById(R.id.textview_picture_zoommode_val);
        zoommode_val.setText(activity.getResources().getStringArray(R.array.str_arr_picture_zoommode_vals)
                [activity.mTvPictureManager.getInstance().getVideoArcType()]);
        pictrueNumber=new int[]{activity.mTvPictureManager.getVideoItem(0),activity.mTvPictureManager.getVideoItem(1),
                activity.mTvPictureManager.getBacklight(),activity.mTvPictureManager.getVideoItem(4)};
        pictrueTextView=new TextView[4];
        for (int i=0;i<pictrueTextView.length;i++){
            pictrueTextView[i]=new TextView(activity);
            pictrueTextView[i]= (TextView) activity.findViewById(pictrueTextViewId[i]);
            pictrueTextView[i].setText(pictrueNumber[i]+"");
        }
        color_temperature_val= (TextView) activity.findViewById(R.id.textview_picture_color_temperature_val);
        color_temperature_val.setText(activity.getResources().getStringArray(R.array.str_arr_picture_colortemperature_vals)
                [activity.mTvPictureManager.getColorTemprature()]);
        imgnoisereduction_val= (TextView) activity.findViewById(R.id.textview_picture_imgnoisereduction_val);
        imgnoisereduction_val.setText(activity.getResources().getStringArray(R.array.str_arr_pic_imgnoisereduction_vals)
                [activity.mTvPictureManager.getNoiseReduction()]);
        mpegnoisereduction_val= (TextView) activity.findViewById(R.id.mpegnoisereduction_val);
        mpegnoisereduction_val.setText(activity.getResources().getStringArray(R.array.str_arr_pic_mpegnoisereduction_vals)
                [activity.mTvPictureManager.getMpegNoiseReduction()]);
        xvYCC_val= (TextView) activity.findViewById(R.id.xvycc_val);
        try{
            xvYCC_val.setText(activity.mTvPictureManager.getxvYCCEnable()?activity.getString(R.string.str_mainmenu_default_switch_on):
                    activity.getString(R.string.str_mainmenu_default_switch_off));
        }catch (NoSuchMethodError e){
            e.printStackTrace();
        }
    }

    private void initSound() {
        sound=new LinearLayout[7];
        for(int i=0;i<sound.length;i++){
            sound[i]=new LinearLayout(activity);
            sound[i]= (LinearLayout) activity.findViewById(soundId[i]);
            sound[i].setFocusable(false);
            sound[i].setClickable(false);
        }
        soundmode_val= (TextView) activity.findViewById(R.id.textview_sound_soundmode_val);
        soundmode_val.setText(activity.getResources().getStringArray(R.array.str_arr_sound_soundmode_vals)
                [activity.tvAudioManager.getAudioSoundMode()]);
        srs_val= (TextView) activity.findViewById(R.id.textview_sound_srs_val);
        srs_val.setText(activity.tvAudioManager.isSRSEnable()?
                activity.getString(R.string.str_mainmenu_default_switch_on):
                activity.getString(R.string.str_mainmenu_default_switch_off));
        surround_val= (TextView) activity.findViewById(R.id.textview_sound_surround_val);
        surround_val.setText(activity.tvAudioManager.getAudioSurroundMode()==1?
                activity.getString(R.string.str_mainmenu_default_switch_on):
                activity.getString(R.string.str_mainmenu_default_switch_off));
        avc_val= (TextView) activity.findViewById(R.id.textview_sound_avc_val);
        avc_val.setText(activity.tvAudioManager.getAvcMode()?
                activity.getString(R.string.str_mainmenu_default_switch_on):
                activity.getString(R.string.str_mainmenu_default_switch_off));
        autohoh_val= (TextView) activity.findViewById(R.id.textview_sound_auto_hoh_val);
        autohoh_val.setText(activity.tvAudioManager.getHOHStatus()?
                activity.getString(R.string.str_mainmenu_default_switch_on):
                activity.getString(R.string.str_mainmenu_default_switch_off));
        mute_val= (TextView) activity.findViewById(R.id.mute_val);
        mute_val.setText(activity.volume==0?
                activity.getString(R.string.str_mainmenu_default_switch_on):
                activity.getString(R.string.str_mainmenu_default_switch_off));
        activity.isMute=(activity.volume==0?true:false);
        if(activity.isMute) activity.volume=50;
    }

    private void initChannel() {
        channel=new LinearLayout[2];
        for(int i=0;i<channel.length;i++){
            channel[i]=new LinearLayout(activity);
            channel[i]= (LinearLayout) activity.findViewById(channelId[i]);
            channel[i].setFocusable(false);
            channel[i].setClickable(false);
        }
    }

    private void initNetWork() {
        network=new LinearLayout[5];
        for(int i=0;i<network.length;i++){
            network[i]=new LinearLayout(activity);
            network[i]= (LinearLayout) activity.findViewById(networkId[i]);
            network[i].setFocusable(false);
            network[i].setClickable(false);
        }
        wifihotspot_val= (TextView) activity.findViewById(R.id.textview_network_wifihotspot_val);
        wifi_val= (TextView) activity.findViewById(R.id.wifi_val);
        bluetooth_val= (TextView) activity.findViewById(R.id.bluetooth_val);
    }

    private void initIntelligence() {
        intelligence=new LinearLayout[11];
        for(int i=0;i<intelligence.length;i++){
            intelligence[i]=new LinearLayout(activity);
            intelligence[i]= (LinearLayout) activity.findViewById(intelligenceId[i]);
            intelligence[i].setFocusable(false);
            intelligence[i].setClickable(false);
            if(i>=2&&i<=6&&!activity.isIntelligence){
                activity.enableSingleItemOrNot(intelligence[i],false);
            }
        }
        sleeptime_val= (TextView) activity.findViewById(R.id.textview_intelligence_sleeptime_val);
        power_on_time_val= (TextView) activity.findViewById(R.id.textview_intelligence_power_on_time_val);
        menu_display_time_val= (TextView) activity.findViewById(R.id.textview_intelligence_menu_display_time_val);
        activity.setSleepAndPowerTime();
        intelligenceTextView=new TextView[8];
        for(int i=0;i<intelligenceTextView.length;i++){
            intelligenceTextView[i]=new TextView(activity);
            intelligenceTextView[i]=(TextView)activity.findViewById(intelligenceTextViewId[i]);
        }
//        if(Settings.Global.getInt(activity.getContentResolver(),Settings.Global.ON_INTELLIGENT_IDENTIFICATION, 0) == 0){
//            intelligenceTextView[0].setText(activity.getString(R.string.str_mainmenu_default_switch_off));
//        }else{
//            intelligenceTextView[0].setText(activity.getString(R.string.str_mainmenu_default_switch_on));
//        }
//        if(Settings.Global.getInt(activity.getContentResolver(),Settings.Global.ON_SIGNAL_TV_STANBY, 0) == 0){
//            intelligenceTextView[7].setText(activity.getString(R.string.str_mainmenu_default_switch_off));
//        }else{
//            intelligenceTextView[7].setText(activity.getString(R.string.str_mainmenu_default_switch_on));
//        }
    }

    private void initSystem() {
        system=new LinearLayout[3];
        for(int i=0;i<system.length;i++){
            system[i]=new LinearLayout(activity);
            system[i]= (LinearLayout) activity.findViewById(systemId[i]);
            system[i].setFocusable(false);
            system[i].setClickable(false);
        }
        powermusic_val= (TextView) activity.findViewById(R.id.textview_system_powermusic_val);
        powermusic_val.setText(TvFactoryManager.getInstance().getPowerOnMusicMode()!=0?
                activity.getString(R.string.str_mainmenu_default_switch_on):
                activity.getString(R.string.str_mainmenu_default_switch_off));
        location_val= (TextView) activity.findViewById(R.id.textview_system_location_val);
        location_val.setText(activity.getSharedPreferences(
                CitySettingActivity.SHARE_NAME, Context.MODE_PRIVATE).getString("cn_city_name", null));
    }

    private void initAbout() {
        about=new LinearLayout[6];
        for(int i=0;i<about.length;i++){
            about[i]=new LinearLayout(activity);
            about[i]= (LinearLayout) activity.findViewById(aboutId[i]);
            about[i].setFocusable(false);
            about[i].setClickable(false);
            if(i==1){
                activity.enableSingleItemOrNot(about[i],false);
            }
        }
        local_name= (TextView) activity.findViewById(R.id.textview_local_name);
        local_name.setText(activity.getSharedPreferences("MyTvSetting",0).getString(
                "LocalName",android.os.Build.MODEL
        ));
    }

    public void setNoneBackground(int position,int flag){
        if(flag==-1){
            button[position].setBackgroundResource(0);
        }else{
            button[position].setBackgroundResource(0);
            switch (position){
                case 0:
                    general[flag].setBackgroundResource(0);
                    break;
                case 1:
                    picture[flag].setBackgroundResource(0);
                    break;
                case 2:
                    sound[flag].setBackgroundResource(0);
                    break;
                case 3:
                    channel[flag].setBackgroundResource(0);
                    break;
                case 4:
                    network[flag].setBackgroundResource(0);
                    break;
                case 5:
                    intelligence[flag].setBackgroundResource(0);
                    break;
                case 6:
                    system[flag].setBackgroundResource(0);
                    break;
                case 7:
                    about[flag].setBackgroundResource(0);
                    break;
            }
        }
        for (Button b:button){
            b.setBackgroundResource(0);
        }
        for (LinearLayout l:general){
            l.setBackgroundResource(0);
        }
        for (LinearLayout l:picture){
            l.setBackgroundResource(0);
        }
        for (LinearLayout l:sound){
            l.setBackgroundResource(0);
        }
        for (LinearLayout l:channel){
            l.setBackgroundResource(0);
        }
        for (LinearLayout l:network){
            l.setBackgroundResource(0);
        }
        for (LinearLayout l:intelligence){
            l.setBackgroundResource(0);
        }
        for (LinearLayout l:system){
            l.setBackgroundResource(0);
        }
        for (LinearLayout l:about){
            l.setBackgroundResource(0);
        }
    }

    public void setBackground(int position,int flag){
        if(flag==-1){
            button[position].setBackgroundResource(R.drawable.mainmenu_button1_focus);
        }else{
            button[position].setBackgroundResource(R.drawable.mainmenu_button1_focus);
            switch (position){
                case 0:
                    general[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 1:
                    picture[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 2:
                    sound[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 3:
                    channel[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 4:
                    network[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 5:
                    intelligence[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 6:
                    system[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
                case 7:
                    about[flag].setBackgroundResource(R.drawable.mainmenu_button2_focus);
                    break;
            }
        }
    }

}
