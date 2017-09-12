package tufer.com.menutest.UIActivity;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mstar.android.tv.TvAudioManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tv.TvPictureManager;

import java.util.ArrayList;

import tufer.com.menutest.R;

import tufer.com.menutest.UIActivity.general.powerinput.GetTvSource;
import tufer.com.menutest.UIActivity.general.powerinput.InputSourceItem;

/**
 * Created by Administrator on 2017/8/4 0004.
 */

public class SelectDialog extends Activity {
    private static final String TAG = "SelectDialog";
    private static final String[] mSourceListname = {
            "VGA2","VGA3","HDMI-DP","OPS"
       };
    private ListView mPowerInputListView;
    private String[] inputdata;
    private int mSelect;

    private ArrayList<InputSourceItem> mGalleryItemList;

    private String list[];

    private ArrayAdapter<String> mAdapter;

//    private MainActivity activity;

    private TextView title;
    private int intelligencePosition;
    private int[] titleName;

    //private TvCommonManager mTvCommonManager = null;
    private TvPictureManager mTvPictureManager = null;
    private TvAudioManager mTvAudioManager = null;
    private TvFactoryManager mTvFactoryManager;
    private TvCommonManager mTvCommonmanager;
    private String wind;
    private String[] type={"PowerInput", "PictureMode", "ZoomMode", "ColorTemperature",
            "ImgNoiseReduction", "SoundMode", "SoundSrs", "AVC", "Surround", "AutoHoh",
            "IntelligenceSwitch", "MenuShowTime", "PowerMusic","XvYCC","MPEGNoiseReduction","Auxiliary"};

//    public SelectDialog(MainActivity activity , String str) {
//        super(activity);
//        this.activity=activity;
//        wind=str;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
        wind=getIntent().getStringExtra("Type");
        Window w = getWindow();
        Resources resources = getResources();
        Drawable drawable = resources.getDrawable(R.drawable.dialog_bg);
        w.setBackgroundDrawable(drawable);
        w.setTitle(null);

        Point point = new Point();
        Display display = w.getWindowManager().getDefaultDisplay();
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

    private void registerListeners() {
        mPowerInputListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelect=i;
                setData();
                MainActivity.myMainActivity.downTime=System.currentTimeMillis();
                SharedPreferences preferences=getSharedPreferences("TuferTvMenu", Context.MODE_PRIVATE);
                MainActivity.myMainActivity.isMenuShow=preferences.getBoolean("isMenuShow", false);
                //activity.setFocusCallback();
                finish();

            }
        });
    }



    private void findViews() {
		inputdata = getResources().getStringArray(
              R.array.str_arr_input_source_vals);
        mPowerInputListView = (ListView) findViewById(R.id.select_listview);
        title= (TextView) findViewById(R.id.select_textview);
        mPowerInputListView.setDivider(null);
        getData();

        mAdapter = new ArrayAdapter<String>(this,
                R.layout.list_item_city_select, list);
        mPowerInputListView.setDividerHeight(0);
        mPowerInputListView.setItemsCanFocus(false);
        mPowerInputListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mPowerInputListView.setAdapter(mAdapter);
        Log.d(TAG, "mSelect, " + mSelect);
        mPowerInputListView.setItemChecked(mSelect, true);
        mPowerInputListView.setSelection(mSelect);
    }
    private int getPictureMode()
    {
        if (this.mTvPictureManager != null) {
            this.mSelect = this.mTvPictureManager.getPictureMode();
        }
        return this.mSelect;
    }
    private void setPictureMode(int paramInt)
    {

        if (this.mTvPictureManager != null) {
            this.mTvPictureManager.setPictureMode(paramInt);
            //TvPictureManager.getInstance().getVideoArcType();
        }
    }

    public void getData() {
        mTvPictureManager = TvPictureManager.getInstance();
        mTvAudioManager = TvAudioManager.getInstance();
        mTvFactoryManager = TvFactoryManager.getInstance();
        mTvCommonmanager = TvCommonManager.getInstance();
        GetTvSource getTvSource = new GetTvSource(this);
        mGalleryItemList = getTvSource.getSource();
        int position = 0;
        for (int i = 0; i < type.length; i++) {
            if (wind.equals(type[i])) {
                position = i;
                break;
            }
        }
        switch (position) {
            case 0:
                ArrayList<InputSourceItem> mGalleryItemList;
				new Thread(new Runnable() {
					@Override
					public void run() {
						mSelect=getinputpostion(getinputboot());
					}
				}).start();
                //mSelect=MainActivity.generalInputPosition;
                getTvSource = new GetTvSource(this);
                mGalleryItemList = getTvSource.getSource();
                list = new String[mGalleryItemList.size() + 2];
                list[0] = this.getResources().getStringArray(R.array.str_arr_general_powerinput_vals)[0];
                list[1] = this.getString(R.string.str_mainmenu_general_powerinput_android);
                for (int i = 2; i <= mGalleryItemList.size() + 1; i++) {
                    list[i] = mGalleryItemList.get(i - 2).getInputSourceName();
                }
                title.setText(this.getString(R.string.str_mainmenu_general_powerinput));
                break;
            case 1:
                list = this.getResources().getStringArray(R.array.str_arr_picture_picturemode_vals);
                mSelect = getPictureMode();
                title.setText(this.getString(R.string.str_mainmenu_picture_picturemode));
                break;
            case 2:
                list = this.getResources().getStringArray(R.array.str_arr_picture_zoommode_vals);
                mSelect = getZoomMode();
                title.setText(this.getString(R.string.str_mainmenu_picture_zoommode));
                break;
            case 3:
                list = this.getResources().getStringArray(R.array.str_arr_picture_colortemperature_vals);
                mSelect = getColorTemperature();
                title.setText(this.getString(R.string.str_mainmenu_picture_color_temperature));
                break;
            case 4:
                list = this.getResources().getStringArray(R.array.str_arr_pic_imgnoisereduction_vals);
                mSelect = getNoisReduction();
                title.setText(this.getString(R.string.str_mainmenu_picture_imgnoisereduction));
                break;
            case 5:
                list = this.getResources().getStringArray(R.array.str_arr_sound_soundmode_vals);
                mSelect = getSoundMode();
                title.setText(this.getString(R.string.str_mainmenu_sound_soundmode));
                break;
            case 6:
                list = this.getResources().getStringArray(R.array.str_arr_mainmenu_switch_vals);
                mSelect = getSoundSrs();
                title.setText(this.getString(R.string.str_mainmenu_sound_srs));
                break;
            case 7:
                list = this.getResources().getStringArray(R.array.str_arr_mainmenu_switch_vals);
                mSelect = getAvc();
                title.setText(this.getString(R.string.str_sound_avc));
                break;
            case 8:
                list = this.getResources().getStringArray(R.array.str_arr_mainmenu_switch_vals);
                mSelect = getSurround();
                title.setText(this.getString(R.string.str_sound_surround));
                break;
            case 9:
                list = this.getResources().getStringArray(R.array.str_arr_mainmenu_switch_vals);
                mSelect = getAutoHoh();
                title.setText(this.getString(R.string.str_sound_autohoh));
                break;
            case 10:
                titleName = MainActivity.intelligenceNameList;
                intelligencePosition = MainActivity.intelligencePosion;
                list = this.getResources().getStringArray(R.array.str_arr_mainmenu_switch_vals);
                mSelect = getIsOff() ? 0 : 1;
                title.setText(this.getString(titleName[intelligencePosition]));
                break;
            case 11:
                list = this.getResources().getStringArray(R.array.str_arr_intelligence_menu_display_time_vals);
                mSelect = getMenudisplaytimeselect();
                title.setText(this.getString(R.string.str_mainmenu_intelligence_menu_display_time));
                break;
            case 12:
                list = this.getResources().getStringArray(R.array.str_arr_mainmenu_switch_vals);
                mSelect = getPowerOnMusicMode();
                title.setText(this.getString(R.string.str_mainmenu_system_powermusic));
                break;
            case 13:
                list = this.getResources().getStringArray(R.array.str_arr_mainmenu_switch_vals);
                mSelect = getXvYCC();
                title.setText(this.getString(R.string.str_pic_xvycc));
                break;
            case 14:
                list = this.getResources().getStringArray(R.array.str_arr_pic_mpegnoisereduction_vals);
                mSelect = getMPEGNoiseReduction();
                title.setText(this.getString(R.string.str_pic_mpegnoisereduction));
                break;
            case 15:
                list = this.getResources().getStringArray(R.array.str_arr_mainmenu_switch_vals);
                mSelect = getAuxiliary();
                title.setText(this.getString(R.string.str_mainmenu_general_wind));
                break;
        }


    }
    private void setData() {
        int position=0;
        for(int i=0;i<type.length;i++){
            if(wind.equals(type[i])){
                position=i;
                break;
            }
        }
        switch (position){
            case 0:
                MainActivity.generalInputPosition=mSelect;
                MainActivity.powerInputString=list[mSelect];
                int TvPosition = 0;
                switch (mSelect){
                    case 0:
                        SystemProperties.set("persist.adaboot.channel",String.valueOf(TvCommonManager.INPUT_SOURCE_NONE));
                        break;
                    case 1:
                        SystemProperties.set("persist.adaboot.channel",String.valueOf(TvCommonManager.INPUT_SOURCE_STORAGE));
                        break;
                    default:
                        TvPosition = mSelect -2;
						setTvonmesger(TvPosition);
                        break;
                }

                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_GENERAL);
                break;
            case 1:
                setPictureMode(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_PICTURE);
                break;
            case 2:
                setZoomMode(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_PICTURE);
                break;
            case 3:
                setColorTemperature(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_PICTURE);
                break;
            case 4:
                setNoisReduction(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_PICTURE);
                break;
            case 5:
                setSoundMode(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_SOUND);
                break;
            case 6:
                setSoundSrs(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_SOUND);
                break;
            case 7:
                setAvc(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_SOUND);
                break;
            case 8:
                setSurround(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_SOUND);
                break;
            case 9:
                setAutoHoh(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_SOUND);
                break;
            case 10:
                setIsOff(mSelect==0?true:false);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_INTELLIGENCE);
                break;
            case 11:
                setMenudisplaytimeselect(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_INTELLIGENCE);
                break;
            case 12:
                setPowerOnMusicMode(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_SYSTEM);
                break;
            case 13:
                setXvYCC(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_PICTURE);
                break;
            case 14:
                setMPEGNoiseReduction(mSelect);
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_PICTURE);
                break;
            case 15:
                setAuxiliary(mSelect);
                break;
        }

    }
    private int getZoomMode()
    {
        if (this.mTvPictureManager != null) {
            this.mSelect = this.mTvPictureManager.getVideoArcType();
        }
        return this.mSelect;
    }
    private void setZoomMode(int paramInt)
    {
        if (this.mTvPictureManager != null) {
            this.mTvPictureManager.setVideoArcType(paramInt);
        }
    }
    private int getColorTemperature()
    {
        if (this.mTvPictureManager != null) {
            this.mSelect = this.mTvPictureManager.getColorTemprature();
        }
        return this.mSelect;
    }
    private void setColorTemperature(int paramInt)
    {
        if (this.mTvPictureManager != null) {
            this.mTvPictureManager.setColorTemprature(paramInt);
        }
    }
    private int getNoisReduction()
    {
        if (this.mTvPictureManager != null) {
            this.mSelect = this.mTvPictureManager.getNoiseReduction();
        }
        return this.mSelect;
    }
    private void setNoisReduction(int paramInt)
    {
        if (this.mTvPictureManager != null) {
            this.mTvPictureManager.setNoiseReduction(paramInt);
        }
    }
    public int getSoundMode()
    {
        if (this.mTvAudioManager != null) {
            this.mSelect = this.mTvAudioManager.getAudioSoundMode();
        }
        return this.mSelect;
    }
    public void setSoundMode(int paramInt)
    {
        if (this.mTvAudioManager != null) {
            this.mTvAudioManager.setAudioSoundMode(paramInt);
        }
    }
    private int getSoundSrs()
    {
        if (this.mTvAudioManager != null) {
            if (this.mTvAudioManager.isSRSEnable()) {
                mSelect=1;
            } else {
                mSelect=0;
            }

        }
        return mSelect;
    }
    private void setSoundSrs(int paramInt) {
        if (this.mTvAudioManager != null) {
            mTvAudioManager.enableSRS(mSelect==0?false:true);
        }
    }

    private int getMenudisplaytimeselect() {
        mSelect=getSharedPreferences("TuferTvMenu",Context.MODE_PRIVATE ).getInt("menuDisplayTime",0)/5;
        return mSelect;
    }
    private void setMenudisplaytimeselect(int paramInt)
    {
        SharedPreferences.Editor localEditor = getSharedPreferences("TuferTvMenu", Context.MODE_PRIVATE).edit();
        if(paramInt==0){
            localEditor.putBoolean("isMenuShow", false);
            localEditor.putInt("menuDisplayTime", paramInt*5);
        }else{
            localEditor.putBoolean("isMenuShow", true);
            localEditor.putInt("menuDisplayTime", paramInt*5);
        }
        localEditor.apply();
    }
    private boolean getIsOff() {
        boolean b=false;
        switch (intelligencePosition){
            case 0:
                b=getIdentifyDetectionStatus();
                break;
            case 1:
                b=getChildLockMode();
                break;
            case 2:
                b=getEnergySavingMode();
                break;
            case 3:
                b=getTemperatureDetectionStatus();
                break;
            case 4:
                b=getSensorLightMode();
                break;
            case 5:
                b=getProtectEyesMode();
                break;
            case 6:
                b=getNoSignalReturnMode();
                break;
            case 7:
                b=getNoSignalStandbyMode();
                break;
        }
        return b;
    }
    private void setIsOff(boolean b) {
        switch (intelligencePosition){
            case 0:
                setIdentifyDetectionStatus(b);
                break;
            case 1:
                setChildLockMode(b);
                break;
            case 2:
                setEnergySavingMode(b);
                break;
            case 3:
                setTemperatureDetectionStatus(b);
                break;
            case 4:
                setSensorLightMode(b);
                break;
            case 5:
                setProtectEyesMode(b);
                break;
            case 6:
                setNoSignalReturnMode(b);
                break;
            case 7:
                setNoSignalStandbyMode(b);
                break;
        }

    }

    public boolean getIdentifyDetectionStatus()
    {
//        return Settings.Global.getInt(getContentResolver(),
//                        Settings.Global.ON_INTELLIGENT_IDENTIFICATION, 0) == 0;
        return true;

    }
    public void setIdentifyDetectionStatus(boolean param)
    {
//        Settings.Global.putInt(getContentResolver(),
//                Settings.Global.ON_INTELLIGENT_IDENTIFICATION, param?0:1) ;


    }
    private boolean getChildLockMode()
    {
        return true;
    }
    private void setChildLockMode(boolean b)
    {
        if(!b){
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("com.szada.systemui.action.LOCK.START");
            this.sendBroadcast(intent);
            MainActivity.myMainActivity.finish();
        }

    }
    private boolean getEnergySavingMode()
    {
        return true;

    }
    private void setEnergySavingMode(boolean b)
    {

    }
    private boolean getTemperatureDetectionStatus()
    {
        return !getSharedPreferences("MyTvSetting", 0).getBoolean("temperatureCheckStatus", false);
    }
    private void setTemperatureDetectionStatus(boolean b)
    {
        boolean bool = false;
        SharedPreferences.Editor localEditor =getSharedPreferences("MyTvSetting", 0).edit();
        if (!b) {
            bool = true;
        }
        localEditor.putBoolean("temperatureCheckStatus", bool);
        localEditor.apply();
    }
    private boolean getSensorLightMode()
    {
        return !getSharedPreferences("MyTvSetting", 0).getBoolean("lightSensorStatus", false);
    }
    private void setSensorLightMode(boolean b)
    {

    }
    private boolean getProtectEyesMode()
    {
        return !getSharedPreferences("MyTvSetting", 0).getBoolean("protectEyesStatus", false);
    }
    private void setProtectEyesMode(boolean b)
    {

    }
    private boolean getNoSignalReturnMode()
    {
        return true;

    }
    private void setNoSignalReturnMode(boolean b)
    {

    }
    private boolean getNoSignalStandbyMode()
    {
//        return Settings.Global.getInt(getContentResolver(),
//                Settings.Global.ON_SIGNAL_TV_STANBY, 0) == 0;
        return true;
    }
    private void setNoSignalStandbyMode(boolean b)
    {
//        Settings.Global.putInt(getContentResolver(),
//                       Settings.Global.ON_SIGNAL_TV_STANBY, b?0:1) ;

    }
    private void setPowerOnMusicMode(int paramInt)
    {
        mTvFactoryManager.setPowerOnMusicMode(paramInt);
    }
    private int getPowerOnMusicMode()
    {
        this.mSelect = this.mTvFactoryManager.getPowerOnMusicMode();
        return mSelect;
    }

    public int getAvc() {
        return mTvAudioManager.getAvcMode()?1:0 ;   }

    public int getSurround() {
        return mTvAudioManager.getAudioSurroundMode();
    }

    public int getAutoHoh() {
        return mTvAudioManager.getHOHStatus()?1:0;
    }

    public void setAvc(int avc) {
        mTvAudioManager.setAvcMode(avc==0?false:true);
    }

    public void setSurround(int surround) {
        mTvAudioManager.setAudioSurroundMode(surround);
    }

    public void setAutoHoh(int autoHoh) {
        mTvAudioManager.setHOHStatus(autoHoh==0?false:true);
    }
    private void setTvonmesger(int position) {
        InputSourceItem item = mGalleryItemList.get(position);
        int inputSrcIndex = item.getPositon();
      
        switch (inputSrcIndex) {
            case TvCommonManager.INPUT_SOURCE_VGA:
                if(item.getInputSourceName().equals("VGA2")){
                    inputSrcIndex = TvCommonManager.INPUT_SOURCE_NONE + 1;
                }else if(item.getInputSourceName().equals("VGA3")){
                    inputSrcIndex = TvCommonManager.INPUT_SOURCE_NONE + 2;
                }
                break;

            case TvCommonManager.INPUT_SOURCE_HDMI2:
                  if(item.getInputSourceName().equals("HDMI-DP")){
                       inputSrcIndex = TvCommonManager.INPUT_SOURCE_NONE + 3;
                    }

                break;
            case TvCommonManager.INPUT_SOURCE_HDMI3:
                if(item.getInputSourceName().equals("OPS")){
                    inputSrcIndex = TvCommonManager.INPUT_SOURCE_NONE + 4;
                }
                break;
            default:
                break;

        }
		 
        SystemProperties.set("persist.adaboot.channel",String.valueOf(inputSrcIndex));
    }

    public int getXvYCC() {
        return mTvPictureManager.getxvYCCEnable()?1:0;
    }
	private int getinputboot(){
		return Integer.valueOf(SystemProperties.get("persist.adaboot.channel", TvCommonManager.INPUT_SOURCE_STORAGE+""));
	}
	private int getinputpostion(int inputSrcIndexs){
       int inputpostion = 1;
		switch(inputSrcIndexs){
			case TvCommonManager.INPUT_SOURCE_STORAGE :
                inputpostion = 1;
				break;
			case TvCommonManager.INPUT_SOURCE_NONE :
                inputpostion = 0;
				break;
			case TvCommonManager.INPUT_SOURCE_VGA:
				  inputpostion = 2;
				break;
			default :
                String inputSrcIndexsname;
                String InputSourceName;
                int inputnameint;
                for (int i = 0; i < mGalleryItemList.size(); i++){
                    InputSourceName = mGalleryItemList.get(i).getInputSourceName();
                    inputnameint = mGalleryItemList.get(i).getPositon();
                    if(inputSrcIndexs > TvCommonManager.INPUT_SOURCE_NONE) {
                        inputSrcIndexsname = mSourceListname[inputSrcIndexs - TvCommonManager.INPUT_SOURCE_NONE - 1];
                        if (InputSourceName.equals(inputSrcIndexsname)) {
                            inputpostion = i + 2;
                            return inputpostion;
                        }
                    }else if(InputSourceName.equals(inputdata[inputSrcIndexs]) ){

                        inputpostion = i + 2;

                        return inputpostion;
                    }
                }
				break;
		}
		return inputpostion;
	}

    public void setXvYCC(int xvYCC) {
        if(xvYCC==0){
            mTvPictureManager.setxvYCCEnable(false, 2);
        }else{
            mTvPictureManager.setxvYCCEnable(true,0);
        }
	 }
    public int getMPEGNoiseReduction() {
        return mTvPictureManager.getMpegNoiseReduction();
    }

    public void setMPEGNoiseReduction(int MPEGNoiseReduction) {
        mTvPictureManager.setMpegNoiseReduction(MPEGNoiseReduction);
    }

    @Override
    protected void onStop() {
        MainActivity.myMainActivity.downTime=System.currentTimeMillis();
        SharedPreferences preferences=getSharedPreferences("TuferTvMenu", Context.MODE_PRIVATE);
        MainActivity.myMainActivity.isMenuShow=preferences.getBoolean("isMenuShow", false);
        switch (MainActivity.myMainActivity.posion){
            case 0:
                MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_GENERAL);
                break;
            case 1:
                MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_PICTURE);
                break;
            case 2:
                MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_SOUND);
                break;
            case 3:
                //MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_CHANNEL);
                break;
            case 4:
                MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_NETWORK);
                break;
            case 5:
                MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_INTELLIGENCE);
                break;
            case 6:
                MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_SYSTEM);
                break;
            case 7:
                MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_ABOUT);
                break;


        }
        super.onStop();
    }


    public int getAuxiliary() {
        return 0;
    }

    public void setAuxiliary(int auxiliary) {

    }
}
