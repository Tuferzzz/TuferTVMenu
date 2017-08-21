package tufer.com.menutest.UIActivity.pictrue;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mstar.android.tv.TvPictureManager;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;
import tufer.com.menutest.UIActivity.component.SeekBarButton;

/**
 * Created by Administrator on 2017/7/7 0007.
 */

public class SetLightActivity extends Activity {
    TextView titleName,name;
    SeekBarButton seekBarButton;
    int number;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_picture_setlight);
        initView();


    }

    private void initView() {

        position=MainActivity.pictruePosition;
        seekBarButton=new SeekBarButton(this,R.id.set_light,1,true){
            public void doUpdate()
            {
                if (TvPictureManager.getInstance() != null) {
                    switch (position){
                        case 0:
                            TvPictureManager.getInstance().setVideoItem(0, seekBarButton.getProgress());
                            break;
                        case 1:
                            TvPictureManager.getInstance().setVideoItem(1, seekBarButton.getProgress());
                            break;
                        case 2:
                            TvPictureManager.getInstance().setBacklight(seekBarButton.getProgress());
                            break;
                        case 3:
                            TvPictureManager.getInstance().setVideoItem(TvPictureManager.PICTURE_HUE, seekBarButton.getProgress());
                            break;
                    }

                }
            }
        };
        switch (position){
            case 0:
                seekBarButton.setProgress((short)TvPictureManager.getInstance().getVideoItem(0));
                number=TvPictureManager.getInstance().getVideoItem(0);
                break;
            case 1:
                seekBarButton.setProgress((short)TvPictureManager.getInstance().getVideoItem(1));
                number=TvPictureManager.getInstance().getVideoItem(1);
                break;
            case 2:
                seekBarButton.setProgress((short)TvPictureManager.getInstance().getBacklight());
                number=TvPictureManager.getInstance().getBacklight();
                break;
            case 3:
                seekBarButton.setProgress((short)TvPictureManager.getInstance().getVideoItem(4));
                number=TvPictureManager.getInstance().getVideoItem(4);
                break;
        }
        titleName= (TextView) findViewById(R.id.title_textview);
        name= (TextView) findViewById(R.id.name);
        titleName.setText(MainActivity.pictrueTitleNameList[position]);
        name.setText(MainActivity.pictrueTitleNameList[position]);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_ENTER:
                    finish();
                    break;
            }

        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStop() {
        MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_PICTURE);
        super.onStop();
    }
}
