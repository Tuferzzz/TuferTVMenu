package tufer.com.menutest.UIActivity.general.appinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;
import tufer.com.menutest.UIActivity.general.applications.AppDetailInfoActivity;




/**
 * Created by Administrator on 2017/7/4 0004.
 */

public class AppManagerActivity extends Activity {
    private static final String TAG = "AppManagerActivity";
    private ProgressDialog processDia=null;
    TextView total_space_text;
    TextView free_space_text;
    protected LinearLayout mLinearLayout;
    protected List<AppInfo> myAppList;
    private PackageManager packageManager;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            init();
            initView();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_general_appmanager);
        showLoadingDialog(getString(R.string.str_mainmenu_general_app_serach),true);
        packageManager= getPackageManager();
        getAllApps();
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (!isSucess(myAppList)){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(0);
                closeLoadingDialog();
            }
        }.start();
    }
    private boolean isSucess(List<AppInfo> appInfos){
        for (int i=0;i<appInfos.size();i++){
            if(appInfos.get(i).getTotalSize()==0){
                return false;
            }
        }
        return true;
    }
    private void getAllApps()
    {

        myAppList = new ArrayList();
        List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(0);
        for (PackageInfo pkg : packageInfos) {

            AppInfo localAppInfo = new AppInfo();

            Drawable icon = pkg.applicationInfo.loadIcon(getPackageManager());
            String appName = pkg.applicationInfo.loadLabel(getPackageManager()).toString();
            String versionName = pkg.versionName;
            String pkgName = pkg.packageName;
            localAppInfo.setAppName(appName);
            localAppInfo.setAppIcon(icon);
            localAppInfo.setPackageName(pkgName);
            localAppInfo.setVersionName(versionName);
            if(pkgName.equals("tufer.com.menutest")){
                continue;
            }
            // 第三方应用程序
            if((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0 ) {
                myAppList.add(localAppInfo);
                queryPackageSize(localAppInfo,localAppInfo.getPackageName());
            }
            //本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
            else if ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
                myAppList.add(localAppInfo);
                queryPackageSize(localAppInfo,localAppInfo.getPackageName());
            }
            //SD卡上的应用程序
            else if( (pkg.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                myAppList.add(localAppInfo);
                queryPackageSize(localAppInfo,localAppInfo.getPackageName());
            }
//            myAppList.add(localAppInfo);
//            queryPackageSize(localAppInfo,localAppInfo.getPackageName());

        }
        Collections.sort(myAppList, new AppInfoComparator());
        //mHandler.sendEmptyMessage(1);
//        for(AppInfo app:myAppList){
//            queryPackageSize(app,app.getPackageName());
//        }
        //Log.d("msg",myAppList.size()+"");

    }
    class AppInfoComparator
            implements Comparator<AppInfo>
    {
        AppInfoComparator() {}

        public int compare(AppInfo paramAppInfo1, AppInfo paramAppInfo2)
        {
            return paramAppInfo1.getAppName().compareToIgnoreCase(paramAppInfo2.getAppName());
        }
    }
    private void queryPackageSize(AppInfo appInfo, String pkgName) {
        if (pkgName != null) {
            //Log.d(TAG, "query size of pkgName : " + pkgName);
            PackageManager pm = getPackageManager();
            pm.getPackageSizeInfo(pkgName, new PackageSizeObserver(appInfo, new Handler()));
        }
        //Log.d(TAG, "query size of pkgName is null");
    }


    private void init() {
        total_space_text= (TextView) findViewById(R.id.total_space_text);
        free_space_text= (TextView) findViewById(R.id.free_space_text);
        //tv_no_app= (TextView) findViewById(R.id.tv_no_app);
        total_space_text.setText(getRomTotalSize());
        free_space_text.setText(getRomAvailableSize());

    }

    private void initView()
    {
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        myAppList= MainActivity.myAppList;
        if (myAppList==null||myAppList.size()==0){
            //tv_no_app.setVisibility(View.VISIBLE);
            Toast.makeText(this,getString(R.string.str_no_app),Toast.LENGTH_SHORT).show();
        }
//        if (mTempAppInfoList==null||mTempAppInfoList.size()==0){
//            //tv_no_app.setVisibility(View.VISIBLE);
//            Toast.makeText(this,getString(R.string.str_no_app),Toast.LENGTH_SHORT).show();
//        }
        ScrollView localScrollView = (ScrollView)findViewById(R.id.appScrollView);
        this.mLinearLayout = new LinearLayout(this);
        Object localObject = new LinearLayout.LayoutParams(-1, -1);
        this.mLinearLayout.setOrientation(1);
        this.mLinearLayout.setGravity(17);
        this.mLinearLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
        int i = 0;
        AppInfo localAppInfo;
        LinearLayout localLinearLayout;
        ImageView localImageView;
        TextView localTextView1,localTextView2,localTextView3;
        while (i < myAppList.size())
        {

            localAppInfo = (AppInfo)myAppList.get(i);
            localObject = LayoutInflater.from(this).inflate(R.layout.mainmenu_general_appmanager_item, null);
            localLinearLayout = (LinearLayout)((View)localObject).findViewById(R.id.appManager_item_LinearLayout);
            localImageView= (ImageView)localLinearLayout.getChildAt(0);
            localTextView1 = (TextView)localLinearLayout.getChildAt(1);
            localTextView2 = (TextView)localLinearLayout.getChildAt(2);
            localTextView3 = (TextView)localLinearLayout.getChildAt(3);
            localImageView.setBackground(localAppInfo.getAppIcon());
            localTextView1.setText(localAppInfo.getAppName());
            localTextView2.setText(localAppInfo.getVersionName());
            localTextView3.setText(FormetAppSize(localAppInfo.getTotalSize()));
            localLinearLayout.setOnClickListener(this.layoutOnClickListener);
            localLinearLayout.setOnFocusChangeListener(this.layoutOnFocusChangeListener);
            localLinearLayout.setOnTouchListener(this.layoutOnTouchListener);
            mLinearLayout.addView((View)localObject);
            i ++;
        }
        localScrollView.addView(mLinearLayout);

    }
    private String FormetAppSize(long paramLong)
    {
        DecimalFormat localDecimalFormat = new DecimalFormat("#.00");
        if (paramLong < 1024L) {
            return localDecimalFormat.format(paramLong) + "B";
        }
        if (paramLong < 1048576L) {
            return localDecimalFormat.format(paramLong / 1024.0D) + "KB";
        }
        if (paramLong < 1073741824L) {
            return localDecimalFormat.format(paramLong / 1048576.0D) + "MB";
        }
        return localDecimalFormat.format(paramLong / 1073741824.0D) + "GB";
    }
    private View.OnClickListener layoutOnClickListener = new View.OnClickListener()
    {
        public void onClick(View paramAnonymousView)
        {
            changeFocusView((LinearLayout)paramAnonymousView);
        }
    };
    private View.OnFocusChangeListener layoutOnFocusChangeListener = new View.OnFocusChangeListener()
    {
        public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
        {
            //paramAnonymousView = (LinearLayout)paramAnonymousView;
            if (paramAnonymousBoolean)
            {
                ((LinearLayout)paramAnonymousView).getChildAt(1).setSelected(true);
                ((LinearLayout)paramAnonymousView).getChildAt(2).setSelected(true);
                return;
            }
            ((LinearLayout)paramAnonymousView).getChildAt(1).setSelected(false);
            ((LinearLayout)paramAnonymousView).getChildAt(2).setSelected(false);
        }
    };
    private View.OnTouchListener layoutOnTouchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
            if (paramAnonymousMotionEvent.getAction() == 1) {
                changeFocusView((LinearLayout)paramAnonymousView);
            }
            return true;
        }
    };
    private void changeFocusView(LinearLayout paramLinearLayout)
    {
        int i = 0;
        while (i < this.mLinearLayout.getChildCount())
        {
            LinearLayout localLinearLayout = (LinearLayout)((LinearLayout)this.mLinearLayout.getChildAt(i)).getChildAt(0);
            if (paramLinearLayout == localLinearLayout)
            {
                Log.d("AppsBrowseListActivity", "you select : " + i);
                openAppManager(i);
                localLinearLayout.requestFocus();
                finish();
            }
            i += 1;
        }
    }
    private void openAppManager(int paramInt)
    {
        startActivity(getLaunchIntent(((AppInfo)this.myAppList.get(paramInt))));
    }
    private Intent getLaunchIntent(AppInfo appInfo)
    {
        Intent localIntent = new Intent(AppManagerActivity.this,AppDetailInfoActivity.class);
        localIntent.putExtra("package",appInfo.getPackageName());
//        localIntent.setComponent(new ComponentName("com.android.tv.settings", "com.android.tv.settings.device.apps.AppManagementActivity"));
//        localIntent.putExtra("com.android.tv.settings.device.apps.PACKAGE_NAME", paramString);
        return localIntent;
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
        return Formatter.formatFileSize(AppManagerActivity.this, blockSize * totalBlocks);
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
        return Formatter.formatFileSize(AppManagerActivity.this, blockSize * availableBlocks);
    }

    @Override
    protected void onStop() {
        MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_GENERAL);
        super.onStop();
    }
    /**
     * 显示加载中对话框
     *
     *
     */
    private void showLoadingDialog(String message, boolean isCancelable) {
        if (processDia == null) {
            processDia= new ProgressDialog(this, R.style.dialog1);
            //点击提示框外面是否取消提示框
            processDia.setCanceledOnTouchOutside(false);
            //点击返回键是否取消提示框
            processDia.setCancelable(isCancelable);
            processDia.setIndeterminate(true);
            processDia.setMessage(message);
            processDia.show();
        }
    }

    /**
     * 关闭加载对话框
     */
    private void closeLoadingDialog() {
        if (processDia != null) {
            if (processDia.isShowing()) {
                processDia.cancel();
            }
            processDia = null;
        }
    }

}
