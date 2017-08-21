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

package tufer.com.menutest.UIActivity.network;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import tufer.com.menutest.R;

public class WiFiSignalListAdapter extends BaseAdapter {

    private static final int SECURITY_NONE = 0;
    private static final int SECURITY_WEP = 1;
    private static final int SECURITY_PSK = 2;
    private static final int SECURITY_EAP = 3;

    private static final int[][] WIFI_SIGNAL_IMG = {
            {
                    R.drawable.wifi_lock_signal_0, R.drawable.wifi_lock_signal_1,
                    R.drawable.wifi_lock_signal_2, R.drawable.wifi_lock_signal_3
            },
            {
                    R.drawable.wifi_signal_0, R.drawable.wifi_signal_1, R.drawable.wifi_signal_2,
                    R.drawable.wifi_signal_3
            }
    };

    private Context mContext;

    private List<ScanResult> mScanResultList;

    private ViewHolder viewHolder = null;

    // security level
    private int mSecurity;

    private String mSsid = "";
    private boolean mConnected;

    public WiFiSignalListAdapter(Context context, List<ScanResult> list) {
        this.mContext = context;
        this.mScanResultList = list;
    }

    public void updateConnectedSsid(String ssid, boolean connected) {
        mSsid = ssid;
        mConnected = connected;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mScanResultList.size();
    }

    @Override
    public Object getItem(int position) {
        return mScanResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View view = null;

        if (convertView == null || convertView.getTag() == null) {
            view = factory.inflate(R.layout.wifi_list_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ScanResult result = mScanResultList.get(position);

        viewHolder.ssid.setText(result.SSID);
        String tmp = mSsid.replaceAll("\"", "");
        if (result.SSID.equals(tmp)) {
            if (mConnected) {
                viewHolder.connect.setText(R.string.wifi_display_status_connected);
            }
//            else {
//                viewHolder.connect.setText(R.string.wifi_display_status_connecting);
//            }
        } else {
            viewHolder.connect.setText("");
        }

        viewHolder.level.setText(getLevel(result.level) + "");
        try {
            mSecurity = getSecurity(result);
        } catch (Exception e) {
            mSecurity = SECURITY_NONE;
            // TODO: handle exception
        }

        setBackground(getLevel(result.level));

        return view;
    }

    /**
     * Calculates the level of the signal.
     */
    int getLevel(int level) {
        if (level == Integer.MAX_VALUE) {
            return -1;
        }

        return WifiManager.calculateSignalLevel(level, 4);
    }

    /**
     * set the drawable to show the wifi signal level.
     */
    private void setBackground(int level) {
        if (level == Integer.MAX_VALUE) {
            viewHolder.icon.setImageDrawable(null);
        } else {
            int index = (mSecurity != SECURITY_NONE) ? 0 : 1;
            if (level < 0 || 3 < level)
                level = 0;
            viewHolder.icon.setImageResource(WIFI_SIGNAL_IMG[index][level]);
        }
    }

    /**
     * get the security level.
     */
    private int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }

        return SECURITY_NONE;
    }

    class ViewHolder {
        TextView ssid;

        TextView level;

        TextView connect;

        ImageView icon;

        public ViewHolder(View view) {
            this.ssid = (TextView) view.findViewById(R.id.ssid);
            this.level = (TextView) view.findViewById(R.id.level);
            this.connect = (TextView) view.findViewById(R.id.connect_hint);
            this.icon = (ImageView) view.findViewById(R.id.iv);
        }
    }

}
