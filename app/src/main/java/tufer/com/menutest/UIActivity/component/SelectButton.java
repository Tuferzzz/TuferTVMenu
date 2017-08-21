package tufer.com.menutest.UIActivity.component;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import tufer.com.menutest.R;

/**
 * Created by Administrator on 2017/7/18 0018.
 */

public class SelectButton implements IUpdateSysData {

    private static final int TEXT_VIEW_NAME_IDX = 0;

    private static final int SELECT_IDX = 1;

    private boolean isSelectedDifferent = false;

    private LinearLayout mLayout;

    TextView textViewName;

    ImageView imageView;

    public SelectButton(Activity context, int resId, final boolean isSelectedDiff){
        this.isSelectedDifferent = isSelectedDiff;
        mLayout = (LinearLayout) context.findViewById(resId);
        if (mLayout != null) {
            textViewName = (TextView) mLayout.getChildAt(TEXT_VIEW_NAME_IDX);
            imageView = (ImageView) mLayout.getChildAt(SELECT_IDX);
            mLayout.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    /*if (keyCode == KeyEvent.KEYCODE_ENTER
                            && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (mLayout.isSelected()) {
                            mLayout.setSelected(false);
                        } else {
                            mLayout.setSelected(true);
                        }
                        return false;
                    }*/
                    mLayout.setSelected(true);
                    ((ImageView) mLayout.getChildAt(SELECT_IDX)).setImageResource(R.drawable.mainmenu_third_item_focus);
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                            || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        mLayout.setSelected(false);
                        imageView.setImageResource(R.drawable.mainmenu_third_item_no_focus);
                    }
                    return false;
                }
            });
            mLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        SelectButton.this.setFocused();
                        imageView.setImageResource(R.drawable.mainmenu_third_item_focus);
                        return true;
                    }
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        SelectButton.this.setFocused();
                        imageView.setImageResource(R.drawable.mainmenu_third_item_focus);
                        return true;
                    }
                    return false;
                }
            });
        }
    }
    public void setFocused() {
        mLayout.setFocusable(true);
        mLayout.setFocusableInTouchMode(true);
        mLayout.requestFocus();
    }
    @Override
    public void doUpdate() {

    }
}
