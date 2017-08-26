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

package tufer.com.menutest.UIActivity.about;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import tufer.com.menutest.R;
import tufer.com.menutest.UIActivity.MainActivity;
import tufer.com.menutest.Util.Tools;


public class SystemRestoreFactoryActivity extends Activity {

    private static final String TAG = "SystemRestoreFactoryActivity";

    private static final String MSTAR_CLEAR = "android.intent.action.MASTER_CLEAR";

    // backup path
    private static final String TV_DB_BACKUP_DIR = "/tvdatabase/DatabaseBackup/";

    private static final String TV_DB_FILE__USER_SETTING = "user_setting.db";

    private static final String TV_DB_FILE_FACTORY = "factory.db";

    private static final String TV_DB_DIR = "/tvdatabase/Database/";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "===>SystemRestoreFactoryActivity");
        setContentView(R.layout.system_restore_factory);

        mContext = this;
        registerListener();
    }

    private void registerListener() {
        // find confirm button
        Button confirmButton = (Button) findViewById(R.id.clear_button);
        Button cancleButton= (Button) findViewById(R.id.cancle_button);
        if (confirmButton == null||cancleButton==null) {
            return;
        }

        // register click event
        confirmButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Tools.isMonkeyTesting()) {
                    return;
                }
                Log.d(TAG, "===>MASTER_CLEAR");

                Builder builder = new Builder(mContext);
                Resources resource = mContext.getResources();
                builder.setMessage(resource.getString(R.string.restore_factory_confirm_restore));
                builder.setTitle(resource.getString(R.string.system_restore_factory));
                builder.setPositiveButton(resource.getString(R.string.ok), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "choose ok to restore factory");
                        boolean ret = restoreFiles();
                        if (ret) {
                            Log.d(TAG, "restoreFiles ===>successful");
                        } else {
                            Log.d(TAG, "restoreFiles===>failed");
                        }
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(
                                        R.string.restore_factory_system_reboot), Toast.LENGTH_LONG)
                                .show();

                        Log.d(TAG, "===>MASTER_CLEAR");
                        Intent intent=new Intent(MSTAR_CLEAR);
                        intent.putExtra("from", "restorefactory");
                        sendBroadcast(intent);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton(mContext.getResources().getString(R.string.cancle),
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                // show dialog
                builder.create().show();
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean restoreFiles() {
        boolean result = false;
        File srcFile = new File(TV_DB_BACKUP_DIR, TV_DB_FILE__USER_SETTING);
        File destFile = new File(TV_DB_DIR, TV_DB_FILE__USER_SETTING);
        // reset user_setting.db
        result = copyFile(srcFile, destFile);
        Log.d(TAG, "===>restore user_setting data, " + result);
        if (!result) {
            // ret = false;
            return false;
        }

        srcFile = new File(TV_DB_BACKUP_DIR, TV_DB_FILE_FACTORY);
        destFile = new File(TV_DB_DIR, TV_DB_FILE_FACTORY);
        // reset factory.db
        result = copyFile(srcFile, destFile);
        Log.d(TAG, "===>restore factory data, " + result);
        if (!result) {
            return false;
        }

        return true;
    }

    /**
     * copy a file from srcFile to destFile, return true if succeed, return
     * false if fail.
     */
    private boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(srcFile);
            result = writeFile(inputStream, destFile);
        } catch (Exception e) {
            Log.d(TAG, "copyFile(File srcFile, File destFile), " + e.getMessage());

            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
            }
        }
        // change file mode
        chmod(destFile);

        return result;
    }

    /**
     * Copy data from a source stream to destFile. Return true if succeed,
     * return false if failed.
     */
    private boolean writeFile(InputStream inputStream, File destFile) {
        // delete old version
        if (destFile.exists()) {
            destFile.delete();
        }

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) >= 0) {
                Log.d(TAG, "out.write(buffer, 0, bytesRead);");
                outStream.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (Exception e) {
            Log.d(TAG, "writeFile occur exception, " + e.getMessage());

            return false;
        } finally {
            if (outStream != null) {
                try {
                    outStream.flush();
                    outStream.getFD().sync();
                    outStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void chmod(File file) {
        try {
            String command = "chmod 666 " + file.getAbsolutePath();
            Log.d(TAG, "command = " + command);

            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);
        } catch (IOException e) {
            Log.d(TAG, "chmod 666 fail!");
            e.printStackTrace();
        }
    }
    @Override
    protected void onStop() {
        MainActivity.myMainActivity.handler.sendEmptyMessage(MainActivity.UPDATE_ABOUT);
        super.onStop();
    }


}
