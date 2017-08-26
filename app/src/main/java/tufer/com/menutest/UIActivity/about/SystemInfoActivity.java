package tufer.com.menutest.UIActivity.about;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.widget.TextView;

import java.io.File;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;

/**
 * Created by Administrator on 2017/7/6 0006.
 */

public class SystemInfoActivity extends Activity {
    TextView system_info_model,system_info_version,system_info_system_version,system_info_memory_info,system_info_localname;
    //String[] api={"1.0","1.1","1.5","1.6","2.0","2.0.1","2.1","2.2-2.2.3","2.3-2.3.2","2.3.3-2.3.7","3.0","3.1","3.2.x","4.0.1-4.0.2","4.0.3-4.0.4","4.1.x","4.2.x","4.3.x","4.4","4.4w","5.0","5.1","6.0","7.0"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_about_systeminfomode);
        initView();
        system_info_model.setText(android.os.Build.MODEL);
        system_info_version.setText(getAppVersionName(this));
        system_info_system_version.setText("Android "+ android.os.Build.VERSION.RELEASE+"(API:"+android.os.Build.VERSION.SDK_INT+")");
        system_info_memory_info.setText(getRomAvailableSize()+"/"+getRomTotalSize());
        system_info_localname.setText(getSharedPreferences("MyTvSetting",0).getString(
                "LocalName",android.os.Build.MODEL
        ));
    }

    private void initView() {
        system_info_model= (TextView) findViewById(R.id.system_info_model);
        system_info_localname= (TextView) findViewById(R.id.system_info_localname);
        system_info_version= (TextView) findViewById(R.id.system_info_version);
        system_info_system_version= (TextView) findViewById(R.id.system_info_system_version);
        system_info_memory_info= (TextView) findViewById(R.id.system_info_memory_info);
    }
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            //versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    private String getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(SystemInfoActivity.this, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    private String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(SystemInfoActivity.this, blockSize * availableBlocks);
    }

    @Override
    protected void onStop() {
        MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_ABOUT);
        super.onStop();
    }
}
