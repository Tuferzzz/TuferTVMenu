//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2012 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>

package tufer.com.menutest.UIActivity.general.applications;

import java.util.List;



import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Toast;
import android.graphics.drawable.Drawable;

import tufer.com.menutest.R;
import tufer.com.menutest.Util.Tools;


public class AppDetailInfoActivity extends Activity {

    private static final String TAG = "MSettings.AppDetailInfoActivity";

    // force stop
    protected static final int FORCE_STOP = 0;

    // uninstall
    protected static final int UNINSTALL = 1;

    // clear data
    protected static final int CLEAR_DATA = 2;

    // clear data success
    private static final int OP_SUCCESSFUL = 3;

    // clear data failure
    private static final int OP_FAILED = 4;

    // uninstall donw
    private final int UNINSTALL_COMPLETE = 5;

    // check the app is or not running
    protected boolean isRunning = false;

    // check the app has data
    protected boolean isHasData = false;

    private boolean uninstallSuccess = false;

    private AppDetailInfoViewHolder mAppDetailInfoViewHolder;

    private AppDetailInfoListener mAppDetailInfoListener;

    private ApplicationInformation mAppInfo;

    private ClearUserDataObserver mClearDataObserver;

    // package name
    public String packageName;

    private ActivityManager mActivityManager;

    private volatile int mResultCode = -1;

    private PackageManager mPackageManager;

    private int mMoveFlags;

    private PackageMoveObserver mPackageMoveObserver;

    private static final int MSG_SDCARD_NOT_EXIST = 11; // sdcard is not exist
    private static final int MSG_APPLICATION_MOVE_FAIL = 12; // app move fail
    private static final int MSG_CHECK_APP_POSITION = 14; // check app position
    private static final int MSG_APP_MOVE_FLAG = 15;
    private static final int MSG_REFRESH_APP_SIZE = 3;

    private boolean mMoveFlag = false;

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            // If the activity is gone, don't process any more messages.
            if (isFinishing()) {
                return;
            }

            switch (msg.what) {
                case CLEAR_DATA:
                    processClearMsg(msg);
                    break;
                case UNINSTALL_COMPLETE:
                    mResultCode = msg.arg1;
                    final String packageName = (String) msg.obj;
                    Log.d(TAG, "packageName, " + packageName);

                    // Update the status text
                    final int statusText;
                    if (PackageManager.DELETE_SUCCEEDED == msg.arg1) {
                        statusText = R.string.uninstall_done;
                        mAppDetailInfoListener.setInvisible();
                        //packageName=null;
                    } else {
                        statusText = R.string.uninstall_failure;
                    }
                    mAppDetailInfoViewHolder.uninstall.setText(statusText);
                    // Hide the progress bar; Show the ok button
                    mAppDetailInfoViewHolder.mProgressBar.setVisibility(View.INVISIBLE);
                    break;
                case MSG_APPLICATION_MOVE_FAIL:
                    Toast.makeText(AppDetailInfoActivity.this, R.string.app_move_fail,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MSG_SDCARD_NOT_EXIST:
                    Toast.makeText(AppDetailInfoActivity.this, R.string.sdcard_not_exist,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MSG_CHECK_APP_POSITION:
                    checkAppLocation();
                    break;
                case MSG_APP_MOVE_FLAG:
                    mAppDetailInfoViewHolder.mMoveApp.setText(R.string.app_moving);
                    break;
                case MSG_REFRESH_APP_SIZE:
                    Log.d(TAG, "MSG_REFRESH_APP_SIZE");
                    mAppDetailInfoViewHolder.app_total.setText(formateFileSize(mAppInfo
                            .getTotalSize()));
                    mAppDetailInfoViewHolder.app_size
                            .setText(formateFileSize(mAppInfo.getAppSize()));
                    mAppDetailInfoViewHolder.app_data.setText(formateFileSize(mAppInfo
                            .getDataSize()));
                    mAppDetailInfoViewHolder.app_cache.setText(formateFileSize(mAppInfo
                            .getCacheSize()));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        String packageName = getIntent().getStringExtra("package");
        Log.d(TAG, "packageName, " + packageName);

        /*
        Log.d(TAG, "getIntent().toShortString=" + getIntent().toShortString(true, true, true, false));
        Log.d(TAG, "getIntent().getDataString()=" + getIntent().getDataString());
        */
        // fetch data string that starts with "package:"
        try {
            if (packageName == null && getIntent().getDataString().length() > 8) {
                packageName = getIntent().getDataString().substring(8);
                Log.d(TAG, "renew packageName=" + packageName);
            }

            mAppInfo = getInfoOfApps(this.getPackageManager().getPackageInfo(packageName, 0));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (mAppInfo == null) {
            mAppInfo = (ApplicationInformation) getIntent().getExtras().getParcelable("app_infor");
        }
        if (mAppInfo == null) {
            Toast.makeText(this, R.string.load_app_failed, Toast.LENGTH_SHORT).show();
            finish();
        }
        setContentView(R.layout.application_information);
        findViews();
        registerListeners();
    }

    /**
     * initialized data.
     */
    private void initData() {
        packageName = mAppInfo.getPackageName();

        mAppDetailInfoViewHolder.app_name_tv.setText(mAppInfo.getAppName());
        mAppDetailInfoViewHolder.app_version.setText(mAppInfo.getVersionName());
        mAppDetailInfoViewHolder.app_total.setText(formateFileSize(mAppInfo.getTotalSize()));
        mAppDetailInfoViewHolder.app_size.setText(formateFileSize(mAppInfo.getAppSize()));
        mAppDetailInfoViewHolder.app_data.setText(formateFileSize(mAppInfo.getDataSize()));
        mAppDetailInfoViewHolder.app_cache.setText(formateFileSize(mAppInfo.getCacheSize()));
        byte[] bitmap = mAppInfo.getBitmap();
        Bitmap bm = Tools.byte2Bitmap(bitmap);
        BitmapDrawable bd = new BitmapDrawable(bm);
        mAppDetailInfoViewHolder.app_icon_iv.setBackgroundDrawable(bd);
    }

    /**
     * the component initialized.
     */
    private void findViews() {
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mAppDetailInfoViewHolder = new AppDetailInfoViewHolder(AppDetailInfoActivity.this);
        initData();
        isRunning = isRunning(packageName);
        mAppDetailInfoViewHolder.open_app.setFocusable(true);
        mAppDetailInfoViewHolder.open_app.requestFocus();
        mAppDetailInfoViewHolder.open_app.setBackgroundResource(R.drawable.left_bg);

        isHasData = getData();
        // add by zhudz 2012-3-6
        mPackageManager = this.getPackageManager();
        mHandler.sendEmptyMessage(MSG_CHECK_APP_POSITION);
    }

    // add by zhudz 2012-3-6
    private void checkAppLocation() {
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(mAppInfo.getPackageName(), 0);
            mMoveFlags = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0 ? PackageManager.MOVE_INTERNAL
                    : PackageManager.MOVE_EXTERNAL_MEDIA;
            if (mMoveFlags == 1) {
                mAppDetailInfoViewHolder.mMoveApp.setText(getResources().getString(
                        R.string.app_move_to_tv));
            } else if (mMoveFlags == 2) {
                mAppDetailInfoViewHolder.mMoveApp.setText(getResources().getString(
                        R.string.app_move_to_sdcard));
            }
        } catch (NameNotFoundException e) {
            // do nothing
            e.printStackTrace();
        }
    }

    class PackageMoveObserver extends IPackageMoveObserver.Stub {

        @Override
        public void packageMoved(String packageName, int returnCode) throws RemoteException {
            mMoveFlag = false;
            if (returnCode == PackageManager.MOVE_SUCCEEDED) {
                mHandler.sendEmptyMessageDelayed(MSG_CHECK_APP_POSITION, 3000);
            } else {
                mHandler.sendEmptyMessageDelayed(MSG_APPLICATION_MOVE_FAIL, 3000);
            }
        }
    }

    /**
     * initialized.
     */
    private void registerListeners() {
        mAppDetailInfoListener = new AppDetailInfoListener(this, mAppDetailInfoViewHolder);

        mAppDetailInfoViewHolder.mMoveApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPackageMoveObserver == null) {
                    mPackageMoveObserver = new PackageMoveObserver();
                }

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    mMoveFlag = true;
                    mHandler.sendEmptyMessage(MSG_APP_MOVE_FLAG);
                    if (mPackageManager != null) {
                        mPackageManager.movePackage(mAppInfo.getPackageName(),
                                mPackageMoveObserver, mMoveFlags);
                    }
                } else {
                    mHandler.sendEmptyMessage(MSG_SDCARD_NOT_EXIST);
                }
            }
        });

        mAppDetailInfoViewHolder.mMoveApp.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAppDetailInfoViewHolder.mMoveApp.setBackgroundResource(R.drawable.left_bg);
                } else {
                    mAppDetailInfoViewHolder.mMoveApp.setBackgroundResource(R.drawable.one_px);
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case FORCE_STOP:
                // forece stop
                return new AlertDialog.Builder(AppDetailInfoActivity.this)
                        .setTitle(R.string.force_stop).setMessage(R.string.force_close_tips)
                        .setPositiveButton(R.string.ok, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                forceStopPackage(packageName);
                                isRunning = false;
                                // focusChange();
                            }
                        }).setNegativeButton(R.string.cancle, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            case UNINSTALL:
                // uninstall
                return new AlertDialog.Builder(AppDetailInfoActivity.this)
                        .setTitle(R.string.uninstall_app).setMessage(R.string.uninstall_app_tips)
                        .setPositiveButton(R.string.ok, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uninstallPkg(packageName);
                                mAppDetailInfoViewHolder.uninstall
                                        .setText(getString(R.string.uninstalling));
                                mAppDetailInfoViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                                mAppDetailInfoViewHolder.uninstall.setVisibility(View.VISIBLE);
                            }
                        }).setNegativeButton(R.string.cancle, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            case CLEAR_DATA:
                // clear data
                return new AlertDialog.Builder(AppDetailInfoActivity.this)
                        .setTitle(R.string.delete).setMessage(R.string.delete_tips)
                        .setPositiveButton(R.string.ok, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                initiateClearUserData();
                            }
                        }).setNegativeButton(R.string.cancle, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            default:
                break;
        }

        return null;
    }

    /**
     * check current application is running.
     */
    private boolean isRunning(String packageName) {
        boolean isStarted = false;
        try {
            final int intGetTastCounter = 1000;
            mActivityManager.getRunningTasks(intGetTastCounter);
            List<ActivityManager.RunningAppProcessInfo> mRunningApp = mActivityManager
                    .getRunningAppProcesses();

            Log.d(TAG, "RunningTaskInfo size, " + mRunningApp.size());
            for (ActivityManager.RunningAppProcessInfo amApp : mRunningApp) {
                if (amApp.processName.compareTo(packageName) == 0) {
                    isStarted = true;
                    break;
                }
            }
        } catch (SecurityException e) {
            // do nothing
            e.printStackTrace();
        }

        return isStarted;
    }

    /**
     * whether current application has data.
     */
    private boolean getData() {
        return mAppInfo.getDataSize() > 0;
    }

    /**
     * after clear data.
     */
    private void processClearMsg(Message msg) {
        int result = msg.arg1;
        if (result == OP_SUCCESSFUL) {
            isHasData = false;
            isRunning = false;
            mAppDetailInfoViewHolder.clear_data_btn.setBackgroundResource(R.drawable.one_px);
            mAppDetailInfoViewHolder.clear_data_btn.clearFocus();
            // mAppDetailInfoViewHolder.app_data.setText("0KB");
            queryPackageSize(mAppInfo, packageName);
        }
        mAppDetailInfoViewHolder.clear_data_btn.setEnabled(true);
        forceStopPackage(packageName);
    }

    private void queryPackageSize(ApplicationInformation appInfo, String pkgName) {
        if (pkgName != null) {
            Log.d(TAG, "query size of pkgName : " + pkgName);
            PackageManager pm = getPackageManager();
            pm.getPackageSizeInfo(pkgName, new PackageSizeObserver(appInfo, mHandler));
        }
        Log.d(TAG, "query size of pkgName is null");
    }

    /**
     * force current application to stop.
     */
    private void forceStopPackage(String pkgName) {
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mActivityManager.forceStopPackage(pkgName);
        checkForceStop();
    }

    /**
     * check current application whether stopping.
     */
    private void checkForceStop() {
        Intent intent = new Intent(Intent.ACTION_QUERY_PACKAGE_RESTART, Uri.fromParts("package",
                packageName, null));
        intent.putExtra(Intent.EXTRA_PACKAGES, new String[] {
            packageName
        });
        intent.putExtra(Intent.EXTRA_UID, mAppInfo.getUid());
        intent.putExtra(Intent.EXTRA_USER_HANDLE, UserHandle.getUserId(mAppInfo.getUid()));

        sendOrderedBroadcast(intent, null, mCheckKillProcessesReceiver, null,
                Activity.RESULT_CANCELED, null, null);
    }

    private final BroadcastReceiver mCheckKillProcessesReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean abled = getResultCode() != RESULT_CANCELED;
            Log.d(TAG, "abled, " + abled);
        }
    };

    private ApplicationInformation getInfoOfApps(final PackageInfo packageInfo) {
        ApplicationInformation app = new ApplicationInformation();
        app.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
        app.setPackageName(packageInfo.packageName);
        app.setVersionName(packageInfo.versionName);
        app.setVersionCode(packageInfo.versionCode);
        app.setUid(packageInfo.applicationInfo.uid);
        Drawable drawable = packageInfo.applicationInfo.loadIcon(getPackageManager());
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        byte[] mapArray = Tools.bitmap2Bytes(bitmap);
        app.setIconAppDrawable(bitmap);
        app.setBitmap(mapArray);
        queryPackageSize(app, packageInfo.packageName);
        return app;
    }


    /*
     * Private method to initiate clearing user data when the user clicks the
     * clear data button for a system package.
     */
    private void initiateClearUserData() {
        mAppDetailInfoViewHolder.clear_data_btn.setEnabled(false);
        // Invoke uninstall or clear user data based on sysPackage
        if (mClearDataObserver == null) {
            mClearDataObserver = new ClearUserDataObserver();
        }

        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        }
        boolean res = mActivityManager.clearApplicationUserData(packageName, mClearDataObserver);
        if (!res) {
            // Clearing data failed for some obscure reason.
            Log.d(TAG, "Couldnt clear application user data for package, " + packageName);
        }
    }

    private void uninstallPkg(String pkgName) {
        PackageDeleteObserver observer = new PackageDeleteObserver();
        getPackageManager().deletePackage(pkgName, observer, 0);
        Log.d(TAG, "uninstallPkg");
    }

    private void focusChange() {
        mAppDetailInfoViewHolder.force_stop_btn.setEnabled(false);
        mAppDetailInfoViewHolder.force_stop_btn.setBackgroundResource(R.drawable.one_px);
        mAppDetailInfoViewHolder.force_stop_btn.clearFocus();
        mAppDetailInfoViewHolder.uninstall_btn.setBackgroundResource(R.drawable.left_bg);
    }

    private String formateFileSize(long size) {
        if (size > 0) {
            return Formatter.formatFileSize(AppDetailInfoActivity.this, size);
        } else {
            return "0KB";
        }
    }

    class PackageDeleteObserver extends IPackageDeleteObserver.Stub {

        public void packageDeleted(String packageName, int returnCode) throws RemoteException {
            Message msg = mHandler.obtainMessage(UNINSTALL_COMPLETE);
            msg.arg1 = returnCode;
            msg.obj = packageName;
            mHandler.sendMessage(msg);
        }

    }

    class ClearUserDataObserver extends IPackageDataObserver.Stub {

        public void onRemoveCompleted(final String packageName, final boolean succeeded) {
            final Message msg = mHandler.obtainMessage(CLEAR_DATA);
            msg.arg1 = succeeded ? OP_SUCCESSFUL : OP_FAILED;
            mHandler.sendMessage(msg);
        }
    }

}
