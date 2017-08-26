package tufer.com.menutest.UIActivity.sound;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mstar.android.tv.TvAudioManager;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;
import tufer.com.menutest.UIActivity.component.SeekBarButton;

/**
 * Created by Administrator on 2017/7/7 0007.
 */

public class EqualizerActivity extends Activity {
    private SeekBarButton seekBarBtn120Hz;

    private SeekBarButton seekBarBtn500Hz;

    private SeekBarButton seekBarBtn1_5KHz;// 1.5khz

    private SeekBarButton seekBarBtn5KHz;

    private SeekBarButton seekBarBtn10KHz;

    private static final int STEP = 1;

//    SeekBar[] seekBars;
//    SeekBar seekBar1,seekBar2,seekBar3,seekBar4,seekBar5;
//    int[] seekBarId={R.id.seekbar1,R.id.seekbar2,R.id.seekbar3,R.id.seekbar4,R.id.seekbar5};
//    int[] textViewId={R.id.textview1,R.id.textview2,R.id.textview3,R.id.textview4,R.id.textview5};
//    TextView[] textViews;
//    TextView textView1,textView2,textView3,textView4,textView5;
//    int[] equalizerNumber;
//    LinearLayout equalizer_120hz,equalizer_500hz,equalizer_1_5Khz,equalizer_5Khz,equalizer_10Khz;
//    int[] linearLayoutId={R.id.linearlayout_sound_equalizer_120hz,R.id.linearlayout_sound_equalizer_500hz,
//            R.id.linearlayout_sound_equalizer_1_5khz,R.id.linearlayout_sound_equalizer_5khz,R.id.linearlayout_sound_equalizer_10khz};
//    LinearLayout[] linearLayout;
//    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_sound_equalizer);
        seekBarBtn120Hz = new SeekBarButton(this, R.id.linearlayout_sound_equalizer_120hz, STEP,
                false) {
            @Override
            public void doUpdate() {
                TvAudioManager.getInstance().setEqBand120(seekBarBtn120Hz.getProgress());
            }
        };

        seekBarBtn500Hz = new SeekBarButton(this,

                R.id.linearlayout_sound_equalizer_500hz, STEP, false) {
            @Override
            public void doUpdate() {
                TvAudioManager.getInstance().setEqBand500(seekBarBtn500Hz.getProgress());
            }
        };

        seekBarBtn1_5KHz = new SeekBarButton(this, R.id.linearlayout_sound_equalizer_1_5khz, STEP,
                false) {
            @Override
            public void doUpdate() {
                TvAudioManager.getInstance().setEqBand1500(seekBarBtn1_5KHz.getProgress());
            }
        };

        seekBarBtn5KHz = new SeekBarButton(this, R.id.linearlayout_sound_equalizer_5khz, STEP, false) {
            @Override
            public void doUpdate() {
                TvAudioManager.getInstance().setEqBand5k(seekBarBtn5KHz.getProgress());
            }
        };

        seekBarBtn10KHz = new SeekBarButton(this, R.id.linearlayout_sound_equalizer_10khz, STEP,
                false) {
            @Override
            public void doUpdate() {
                TvAudioManager.getInstance().setEqBand10k(seekBarBtn10KHz.getProgress());
            }
        };

        LoadDataToDialog();
        setOnFocusChangeListeners();
        seekBarBtn120Hz.setFocused();
        //initView();
    }

    private void LoadDataToDialog() {
        seekBarBtn120Hz.setProgress((short) TvAudioManager.getInstance().getEqBand120());
        seekBarBtn500Hz.setProgress((short) TvAudioManager.getInstance().getEqBand500());
        seekBarBtn1_5KHz.setProgress((short) TvAudioManager.getInstance().getEqBand1500());
        seekBarBtn5KHz.setProgress((short) TvAudioManager.getInstance().getEqBand5k());
        seekBarBtn10KHz.setProgress((short) TvAudioManager.getInstance().getEqBand10k());
    }

    private void setOnFocusChangeListeners() {
        View.OnFocusChangeListener seekBarBtnFocusListenser = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    LinearLayout container = (LinearLayout) v;
                    container.getChildAt(2).setVisibility(View.VISIBLE);
                } else {
                    LinearLayout container = (LinearLayout) v;
                    container.getChildAt(2).setVisibility(View.VISIBLE);
                }
            }
        };

        seekBarBtn120Hz.setOnFocusChangeListener(seekBarBtnFocusListenser);
        seekBarBtn500Hz.setOnFocusChangeListener(seekBarBtnFocusListenser);
        seekBarBtn1_5KHz.setOnFocusChangeListener(seekBarBtnFocusListenser);
        seekBarBtn5KHz.setOnFocusChangeListener(seekBarBtnFocusListenser);
        seekBarBtn10KHz.setOnFocusChangeListener(seekBarBtnFocusListenser);

        View.OnClickListener seekBarBtnOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };

        seekBarBtn120Hz.setOnClickListener(seekBarBtnOnClickListener);
        seekBarBtn500Hz.setOnClickListener(seekBarBtnOnClickListener);
        seekBarBtn1_5KHz.setOnClickListener(seekBarBtnOnClickListener);
        seekBarBtn5KHz.setOnClickListener(seekBarBtnOnClickListener);
        seekBarBtn10KHz.setOnClickListener(seekBarBtnOnClickListener);
    }
    @Override
    protected void onStop() {
        MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_SOUND);
        super.onStop();
    }


}
