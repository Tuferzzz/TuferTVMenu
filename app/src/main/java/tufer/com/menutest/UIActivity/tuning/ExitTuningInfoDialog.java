
package tufer.com.menutest.UIActivity.tuning;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvIsdbChannelManager;
import com.mstar.android.tv.TvCommonManager;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.holder.ViewHolder;
import tufer.com.menutest.Util.TvIntent;
//import com.mstar.tv.tvplayer.ui.R;
//import com.mstar.tv.tvplayer.ui.TvIntent;
//import com.mstar.tv.tvplayer.ui.holder.ViewHolder;
//import com.mstar.tv.tvplayer.ui.MainMenuActivity;

public class ExitTuningInfoDialog extends Dialog {
    /** Called when the activity is first created. */
    @SuppressWarnings("unused")
    private ViewHolder viewholder_exittune;

    private static int ATV_MIN_FREQ = 45200;

    private static int ATV_MAX_FREQ = 876250;

    private static int ATV_EVENTINTERVAL = 500 * 1000;// every 500ms to show

    protected TextView textview_cha_exittune_yes;

    protected TextView textview_cha_exittune_no;

    TvChannelManager mTvChannelManager = null;

    @SuppressWarnings("unused")
    private ViewHolder viewholder_channeltune;

    public ExitTuningInfoDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exittuninginfo_dialog);
        textview_cha_exittune_yes = (TextView) findViewById(R.id.textview_cha_exittune_yes);
        textview_cha_exittune_no = (TextView) findViewById(R.id.textview_cha_exittune_no);
        viewholder_exittune = new ViewHolder(ExitTuningInfoDialog.this);
        textview_cha_exittune_yes.requestFocus();
        registerListeners();
        mTvChannelManager = TvChannelManager.getInstance();
    }

    private void registerListeners() {
        textview_cha_exittune_yes.setOnClickListener(listener);
        textview_cha_exittune_no.setOnClickListener(listener);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                ExitTuningActivityExit(false);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            switch (view.getId()) {
                case R.id.textview_cha_exittune_yes:
                    ExitTuningActivityExit(true);
                    break;
                case R.id.textview_cha_exittune_no:
                    ExitTuningActivityExit(false);
                    break;
                default:
                    ExitTuningActivityExit(false);
                    break;
            }
        }
    };

    private void ExitTuningActivityExit(boolean flag) {
        Intent intent = new Intent();
        if (flag == true)// stop tuning
        {
            switch (mTvChannelManager.getTuningStatus()) {
                case TvChannelManager.TUNING_STATUS_ATV_SCAN_PAUSING:
                    mTvChannelManager.stopAtvAutoTuning();
                    mTvChannelManager.changeToFirstService(
                            TvChannelManager.FIRST_SERVICE_INPUT_TYPE_ATV,
                            TvChannelManager.FIRST_SERVICE_DEFAULT);
                    intent.setAction(TvIntent.MAINMENU);
                    //intent.putExtra("currentPage", MainMenuActivity.CHANNEL_PAGE);
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        getContext().startActivity(intent);
                    }
                    this.dismiss();
                    break;
                case TvChannelManager.TUNING_STATUS_DTV_SCAN_PAUSING:
                    mTvChannelManager.stopDtvScan();
                    // DVB-T doesn't do this until ChannelTuning receives DTV_SCAN_STATUS_END event
                    // Because DVB-T may receive SET REGION or other status after stop scan
                    // DVB-C/ISDB/DTMB also receive DTV_SCAN_STATUS_END after stopDtvScan
                    // But ATSC seems not
                    int dvbtRouteIndex = mTvChannelManager
                            .getSpecificDtvRouteIndex(TvChannelManager.TV_ROUTE_DVBT);
                    if (mTvChannelManager.getCurrentDtvRouteIndex() != dvbtRouteIndex) {
                        if (mTvChannelManager.getUserScanType() == mTvChannelManager.TV_SCAN_ALL) {
                            boolean res = mTvChannelManager.startAtvAutoTuning(ATV_EVENTINTERVAL,
                                    ATV_MIN_FREQ, ATV_MAX_FREQ);
                            if (res == false) {
                                Log.e("TuningService", "atvSetAutoTuningStart Error!!!");
                            }
                        } else {
                            if (TvCommonManager.getInstance().getCurrentTvSystem() == TvCommonManager.TV_SYSTEM_ISDB) {
                                TvIsdbChannelManager.getInstance().genMixProgList(false);
                            }
                            mTvChannelManager.changeToFirstService(
                                    TvChannelManager.FIRST_SERVICE_INPUT_TYPE_DTV,
                                    TvChannelManager.FIRST_SERVICE_DEFAULT);
                            intent.setAction(TvIntent.MAINMENU);
                            //intent.putExtra("currentPage", MainMenuActivity.CHANNEL_PAGE);
                            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                                getContext().startActivity(intent);
                            }
                        }
                    }
                    this.dismiss();
                    break;
                default:
                    break;
            }
        } else
        // resume tuning
        {
            switch (mTvChannelManager.getTuningStatus()) {
                case TvChannelManager.TUNING_STATUS_ATV_SCAN_PAUSING:
                    mTvChannelManager.resumeAtvAutoTuning();
                    this.dismiss();
                    break;
                case TvChannelManager.TUNING_STATUS_DTV_SCAN_PAUSING:
                    mTvChannelManager.resumeDtvScan();
                    this.dismiss();
                    break;
                default:
                    break;
            }
        }
    }
}
