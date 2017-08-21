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

public class Constants {
	   public static final String ETHERNET_STATE_CHANGED_ACTION =
	            "com.mstar.android.ethernet.ETHERNET_STATE_CHANGED";
	    public static final String NETWORK_STATE_CHANGED_ACTION =
	            "com.mstar.android.ethernet.STATE_CHANGE";
	public final static int NET_STATE = 0;

	public final static int WIRE_SETTING = 1;
	public final static int ETHERNET_STATE_INITIAL = 99;
	public final static int ETHERNET_STATE_DISABLED = 98;
	public final static int ETHERNET_STATE_ENABLED = 97;

	public final static int WIRELESS_SETTING = 2;

	public final static int PPPOE_SETTING = 3;

	// Wifi direct setting
	public final static int WIFI_DIRECT = 4;

	// /Wifi hotspot
	public final static int WIFI_HOTSPOT = 5;

	// /location
	public final static int LOCALTON_SERVICE = 6;

	public final static int LIST_ITEMS = 7;

	public final static int WIRE_AUTO_IP_ADDRESS = 8;

	public final static int IP_ADDRESS = 9;

	public final static int SUBNET_MASK = 10;

	public final static int DEFAULT_GATEWAY = 11;

	public final static int FIRST_DNS = 12;

	public final static int SECOND_DNS = 13;

	public final static int IS_AUTO_IP_ADDRESS = 14;

	// wifi switch
	public final static int WIFI_SWITCH = 15;

	// SSID
	public final static int SSID = 16;

	public final static int CONNECT_FORMAT = 17;

	public final static int PPPOE_DIALER = 18;

	public final static int PPPOE_AUTO_IP = 19;

	public final static int PPPOE_USERNAME = 20;

	public final static int PPPOE_PASSWORD = 21;

	public final static int PPPOE_PASSWORD_SHOW = 22;

	public final static int PPPOE_AUTO_DIALER = 23;

	public final static int PPPOE_DIALER_ACTION = 24;

	// is wifi direct open
	public final static int IS_WIFI_DIRECT_OPEN = 25;

	// //wifi direct discover
	public final static int WIFI_DIRECT_DISCOVER = 26;

	// /ip address save
	public final static int IP_ADDRESS_SAVE = 27;

	// /wifi hotspot sitch
	public final static int WIFI_AP_SWITCH = 28;

	// /wifi hotspot setting
	public final static int WIFI_AP_SETTING = 29;

	// /wifi hotspot ssid edit
	public final static int WIFI_AP_SSID_EDIT = 30;

	// /wifi hotspot secure select
	public final static int WIFI_AP_SECURE = 31;

	// /wifi hotspot password edit
	public final static int WIFI_AP_PWD_EDIT = 32;

	// /wifi hotspot show password checkbox
	public final static int WIFI_AP_SHOW_PWD = 33;

	// /wifi hotspot save config button
	public final static int WIFI_AP_SHOW_SAVE = 34;

	// /location service switch
	public final static int LOCATION_SERVICE_SW = 35;

	// add by felix.hu begin 2012/11/23
	// comment : add proxy
	// hostname
	public static final int PROXY_HOSTNAME = LOCATION_SERVICE_SW + 1;
	
	// port
	public static final int PROXY_PORT = LOCATION_SERVICE_SW + 2;

	public static final int BASE_SETTINGS = 0;
	public static final int NETWORK_STATUS = BASE_SETTINGS;
	public static final int ETHERNET_SETTINGS = BASE_SETTINGS + 1;
	public static final int WIFI_SETTINGS = BASE_SETTINGS + 2;
	// public static final int PPPOE_SETTINGS = BASE_SETTINGS + 3;
//	 public static final int PROXY_SETTINGS = BASE_SETTINGS + 4;
	// public static final int WIFIP2P_SETTINGS = BASE_SETTINGS + 5;
	public static final int WIFIAP_SETTINGS = BASE_SETTINGS + 3;
	// public static final int LOCATION_SETTINGS = BASE_SETTINGS + 7;
	public static final int MAC_ADDRESS_SETTINGS = BASE_SETTINGS + 4;
	public static final int BLUETOOTH_SETTING = BASE_SETTINGS + 5;

	// setting items on the right
	public static final int BASE_SETTING_ITEM = 0;
	public static final int SETTING_ITEM_0 = BASE_SETTING_ITEM;
	public static final int SETTING_ITEM_1 = BASE_SETTING_ITEM + 1;
	public static final int SETTING_ITEM_2 = BASE_SETTING_ITEM + 2;
	public static final int SETTING_ITEM_3 = BASE_SETTING_ITEM + 3;
	public static final int SETTING_ITEM_4 = BASE_SETTING_ITEM + 4;
	public static final int SETTING_ITEM_5 = BASE_SETTING_ITEM + 5;
	public static final int SETTING_ITEM_6 = BASE_SETTING_ITEM + 6;
	public static final int SETTING_ITEM_7 = BASE_SETTING_ITEM + 7;
	public static final int SETTING_ITEM_8 = BASE_SETTING_ITEM + 8;
	public static final int SETTING_ITEM_9 = BASE_SETTING_ITEM + 9;
}
