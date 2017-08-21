//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2014 MStar Semiconductor, Inc. All rights reserved.
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

package tufer.com.menutest.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.mstar.android.tv.TvLanguage;

public class Tools {
    private final static int DEFAULT_BUFFER_SIZE = 1024;
    private static final boolean LOG = true;

    public static void toastShow(int resId, Context context) {
        Toast toast = new Toast(context);
        TextView MsgShow = new TextView(context);
        toast.setDuration(Toast.LENGTH_LONG);
        MsgShow.setTextColor(Color.RED);
        MsgShow.setTextSize(25);
        MsgShow.setText(resId);
        toast.setView(MsgShow);
        toast.show();
    }

    private static final String MSTAR_PRODUCT_CHARACTERISTICS = "mstar.product.characteristics";
    private static final String MSTAR_PRODUCT_STB = "stb";
    private static String mProduct = null;
    public static boolean isBox() {
        if (mProduct == null) {
            Class<?> systemProperties = null;
            Method method = null;
            try {
                systemProperties = Class.forName("android.os.SystemProperties");
                method = systemProperties.getMethod("get", String.class, String.class);
                mProduct = (String) method.invoke(null, MSTAR_PRODUCT_CHARACTERISTICS, "");
            } catch (Exception e) {
                return false;
            }
        }
        if (MSTAR_PRODUCT_STB.equals(mProduct)) {
            return true;
        } else {
            return false;
        }
    }
//    public static boolean isBox() {
//        //return SystemProperties.getBoolean("mstar.build.for.stb", false);//tianky@2k151119
//        return true;
//    }

    public static String sizeToM(long size) {
        if (size / 1024 / 1024 >= 1) {
            return size / 1024 / 1024 + (size / 1024 % 1024 / 1024.0 + "").substring(1, 3) + "MB";
        } else {
            return size / 1024 + (size % 1024 / 1024.0 + "").substring(1, 3) + "KB";
        }
    }

    public static void intentForward(Context context, Class<?> forwardClass) {
        Intent intent = new Intent();
        intent.setClass(context, forwardClass);
        context.startActivity(intent);
    }

    public static boolean string2File(String res, String filePath) {
        boolean flag = true;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists())
                distFile.getParentFile().mkdirs();
            bufferedReader = new BufferedReader(new StringReader(res));
            bufferedWriter = new BufferedWriter(new FileWriter(distFile));
            char buf[] = new char[1024];
            int len;
            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
            return flag;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    public static String file2String(File file, String encoding) {
        InputStreamReader reader = null;
        StringWriter writer = new StringWriter();
        try {
            if (encoding == null || "".equals(encoding.trim())) {
                reader = new InputStreamReader(new FileInputStream(file), encoding);
            } else {
                reader = new InputStreamReader(new FileInputStream(file));
            }
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return writer.toString();
    }

    public static String getSystemVersion() {
        return Build.VERSION.INCREMENTAL.substring(1);
    }

    public static boolean matchIP(String ip) {
        String regex = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    public static String[] resolutionIP(String ip) {
        return ip.split("\\.");
    }

    public static Bitmap byte2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static String getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        System.out.println("mi.availMem;" + mi.availMem);

        return Formatter.formatFileSize(context, mi.availMem);
    }

    public static String getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(context, initial_memory);
    }

    public static boolean isMonkeyTesting() {
        return ActivityManager.isUserAMonkey();
    }

    // comment : add proxy
    public static int validate(final String hostname, final String port) {
        // check
        if (TextUtils.isEmpty(hostname)) {
            return -1;
        }
        if (TextUtils.isEmpty(port)) {
            return -1;
        }

        final String HOSTNAME_REGEXP = "^$|^[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*$";
        final Pattern HOSTNAME_PATTERN = Pattern.compile(HOSTNAME_REGEXP);
        Matcher match = HOSTNAME_PATTERN.matcher(hostname);
        if (!match.matches())
            return -1;

        if (hostname.length() > 0 && port.length() == 0) {
            return -2;
        }

        if (port.length() > 0) {
            if (hostname.length() == 0) {
                return -1;
            }
            int portVal = -1;
            try {
                portVal = Integer.parseInt(port);
            } catch (NumberFormatException ex) {
                return -2;
            }
            if (portVal <= 0 || portVal > 0xFFFF) {
                return -2;
            }
        }

        return 0;
    }

    public static void logd(final String tag, final String msg) {
        if (LOG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            Log.d(tag, msg + " @method : " + traceElement.getMethodName() + " line : "
                    + traceElement.getLineNumber());
        }
    }

    public static String buildUpNetmask(final String ip, final String gateway) {
        if (ip == null || gateway == null) {
            return "255.255.0.0";
        }

        StringBuffer netmask = new StringBuffer();
        String[] ipArray = ip.split("\\.");
        String[] gatewayArray = gateway.split("\\.");
        if (ipArray[0].equals(gatewayArray[0])) {
            netmask.append("255");
        } else {
            netmask.append("0");
        }
        if (ipArray[1].equals(gatewayArray[1])) {
            netmask.append(".255");
        } else {
            netmask.append(".0");
        }
        if (ipArray[2].equals(gatewayArray[2])) {
            netmask.append(".255");
        } else {
            netmask.append(".0");
        }
        if (ipArray[3].equals(gatewayArray[3])) {
            netmask.append(".255");
        } else {
            netmask.append(".0");
        }

        return netmask.toString();
    }
    public static int twoget( final String netwak){
        int wak = 0;

        if(netwak==null){
            return 24;
        }
        StringBuffer netmask = new StringBuffer();
        String[] ipArray = netwak.split("\\.");

        for (int j = 0; j < ipArray.length; j++) {
            String s = ipArray[j];
            String a=Integer.toBinaryString(Integer.valueOf(s));
            String aString2[]=a.split("");
            for (int i = 0; i < aString2.length; i++) {
                if ("1".equals(aString2[i])) {
                    wak++;
                }
            }
        }


        if(wak<=0 && wak>32){
            return 24;
        }

        return wak;


    }




}
