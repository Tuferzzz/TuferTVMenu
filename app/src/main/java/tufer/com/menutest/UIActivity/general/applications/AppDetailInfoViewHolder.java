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



import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import tufer.com.menutest.R;


public class AppDetailInfoViewHolder {

    private AppDetailInfoActivity mAppDetailInfoActivity;

    private Context mContext;

    protected ImageView app_icon_iv;

    protected TextView app_name_tv;

    protected TextView app_version;

    protected TextView app_total;

    protected TextView app_size;

    protected TextView app_data;

    protected TextView app_cache;

    //protected ListView mAppOperation;

    protected Button open_app;

    protected Button force_stop_btn;

    protected Button uninstall_btn;

    protected Button clear_data_btn;

    protected Button mMoveApp;

    protected ProgressBar mProgressBar;

    protected TextView uninstall;

    protected LinearLayout totalLayout;

    protected LinearLayout appLayout;

    protected LinearLayout dataLayout;

    protected LinearLayout cacheLayout;

    public AppDetailInfoViewHolder(AppDetailInfoActivity appDetailInforActivity) {
        this.mAppDetailInfoActivity = appDetailInforActivity;
        findViews();
    }

    private void findViews() {

        app_icon_iv = (ImageView) mAppDetailInfoActivity.findViewById(R.id.app_icon);
        app_name_tv = (TextView) mAppDetailInfoActivity.findViewById(R.id.app_name);
        app_version = (TextView) mAppDetailInfoActivity.findViewById(R.id.app_version_tv);
        app_total = (TextView) mAppDetailInfoActivity.findViewById(R.id.app_total_size);
        app_size = (TextView) mAppDetailInfoActivity.findViewById(R.id.app_size_tv);
        app_data = (TextView) mAppDetailInfoActivity.findViewById(R.id.app_data_tv);
        app_cache = (TextView) mAppDetailInfoActivity.findViewById(R.id.app_cache_tv);

        open_app= (Button) mAppDetailInfoActivity.findViewById(R.id.open_app);
        open_app.setFocusable(false);
        open_app.setClickable(false);
        force_stop_btn = (Button) mAppDetailInfoActivity.findViewById(R.id.force_stop_btn);
        force_stop_btn.setFocusable(false);
        force_stop_btn.setClickable(false);
        uninstall_btn = (Button) mAppDetailInfoActivity.findViewById(R.id.uninstall_btn);
        uninstall_btn.setFocusable(false);
        uninstall_btn.setClickable(false);
        clear_data_btn = (Button) mAppDetailInfoActivity.findViewById(R.id.clear_data_btn);
        clear_data_btn.setFocusable(false);
        clear_data_btn.setClickable(false);
        mMoveApp = (Button) mAppDetailInfoActivity.findViewById(R.id.move_app_btn);
        mMoveApp.setFocusable(false);
        mMoveApp.setClickable(false);

        mProgressBar = (ProgressBar) mAppDetailInfoActivity.findViewById(R.id.progress_bar);
        mProgressBar.setIndeterminate(true);

        uninstall = (TextView) mAppDetailInfoActivity.findViewById(R.id.uninstall_progress);

        totalLayout = (LinearLayout) mAppDetailInfoActivity.findViewById(R.id.total_ll);
        appLayout = (LinearLayout) mAppDetailInfoActivity.findViewById(R.id.app_ll);
        dataLayout = (LinearLayout) mAppDetailInfoActivity.findViewById(R.id.data_ll);
        cacheLayout = (LinearLayout) mAppDetailInfoActivity.findViewById(R.id.cache_ll);
    }
    protected void setNoneBackground() {
        open_app.setBackgroundResource(R.drawable.one_px);
        force_stop_btn.setBackgroundResource(R.drawable.one_px);
        uninstall_btn.setBackgroundResource(R.drawable.one_px);
        clear_data_btn.setBackgroundResource(R.drawable.one_px);
        mMoveApp.setBackgroundResource(R.drawable.one_px);
    }

    protected void setBackground(int index) {
        switch (index) {
            case 0:
                open_app.setBackgroundResource(R.drawable.set_button);
                break;
            case 1:
                force_stop_btn.setBackgroundResource(R.drawable.set_button);
                break;
            case 2:
                uninstall_btn.setBackgroundResource(R.drawable.set_button);
                break;
            case 3:
                clear_data_btn.setBackgroundResource(R.drawable.set_button);
                break;
            case 4:
                mMoveApp.setBackgroundResource(R.drawable.set_button);
                break;
            default:
                break;
        }
    }

}
