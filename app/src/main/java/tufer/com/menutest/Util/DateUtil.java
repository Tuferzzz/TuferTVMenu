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

package tufer.com.menutest.Util;

import java.util.Calendar;
import java.util.regex.Pattern;

public class DateUtil {
    private static String[] dateFormat = {
            "YYYYMMDD", "YYYY MM DD", "YYYY/MM/DD"
    };

    public static String dateFString() {
        String s = "";
        for (String ss : dateFormat) {
            s += ";" + ss;
        }

        return s;
    }

    public static void validate(String date) throws InvalidDateFormatException {
        Pattern p = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\/\\/\\s]?((((0?[13578])|(1[02]))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\/\\/\\s]?((((0?[13578])|(1[02]))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\/\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))");
        if (!p.matcher(date).matches()) {
            throw new InvalidDateFormatException("Date format error! Correct format:"
                    + dateFString());
        }
    }

    /**
     * for the corresponding week of the specified date 1..7
     * 
     * @param date
     * @return
     * @throws InvalidDateFormatException
     */
    public static String getWeek(String date) throws InvalidDateFormatException {
        validate(date);
        Pattern p = Pattern.compile("[\\/\\/\\s]?");
        String s = p.matcher(date).replaceAll("");

        int year = Integer.valueOf(s.substring(0, 4));
        int month = Integer.valueOf(s.substring(4, 6));
        int day = Integer.valueOf(s.substring(6, 8));
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day);
        int week = c.get(Calendar.DAY_OF_WEEK) - 1;
        switch (week) {
            case 0:
                week = 7;
            default:
                ;
        }

        return week + "";
    }

    public static String getLastDayOfWeek(String day) throws InvalidDateFormatException {
        int week = -1;
        int iday = -1;
        try {
            iday = Integer.parseInt(day);
        } catch (NumberFormatException ex) {
            throw new InvalidDateFormatException("Week provided only 1 .. 7!");
        }
        for (int i = 0; i < 6; i++) {
            iday++;
            if (iday > 7) {
                iday = 1;
            }
        }
        week = iday;

        return week + "";
    }

    public static String getLastDayOfMonth(String day, String date)
            throws InvalidDateFormatException {
        int week = -1;
        int iday = -1;
        try {
            iday = Integer.parseInt(day);
        } catch (NumberFormatException ex) {
            throw new InvalidDateFormatException("Week provided only 1 .. 7!");
        }
        for (int i = 0; i < 6; i++) {
            iday++;
            if (iday > 7) {
                iday = 1;
            }
        }
        week = iday;

        return week + "";
    }

    public static String getDay2Str(Calendar calendar, String connector)
            throws InvalidDateFormatException {
        Calendar cal = calendar;
        if (cal == null)
            cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String sYear = Integer.toString(year);
        String sMonth = "";
        String sDay = "";
        if (month < 10) {
            sMonth = "0" + Integer.toString(month);
        } else {
            sMonth = Integer.toString(month);
        }
        if (day < 10) {
            sDay = "0" + Integer.toString(day);
        } else {
            sDay = Integer.toString(day);
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(sYear).append(connector).append(sMonth).append(connector).append(sDay);

        return buffer.toString();
    }

    public static String[] getAreaWeek(String date) throws InvalidDateFormatException {
        validate(date);
        Pattern p = Pattern.compile("[\\/\\/\\s]?");
        String s = p.matcher(date).replaceAll("");
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(s.substring(0, 4)), Integer.parseInt(s.substring(4, 6)) - 1,
                Integer.parseInt(s.substring(6)));
        cal.add(Calendar.DAY_OF_MONTH, -1);
        String eArea = getDay2Str(cal, "");
        cal.add(Calendar.DAY_OF_MONTH, -6);
        String sArea = getDay2Str(cal, "");
        String[] area = {
                sArea, eArea
        };

        return area;
    }

    public static String[] getAreaMonth(String date) throws InvalidDateFormatException {
        validate(date);
        Pattern p = Pattern.compile("[\\/\\/\\s]?");
        String s = p.matcher(date).replaceAll("");
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(s.substring(0, 4)), Integer.parseInt(s.substring(4, 6)) - 1,
                Integer.parseInt(s.substring(6)));
        cal.add(Calendar.DAY_OF_MONTH, -1);
        String eArea = getDay2Str(cal, "");
        cal.add(Calendar.DAY_OF_MONTH, -6);
        String sArea = eArea.substring(0, 6) + "01";
        String[] area = {
                sArea, eArea
        };

        return area;
    }

    public static String DateFormat(String date, String style) throws InvalidDateFormatException {
        validate(date);

        Pattern p = Pattern.compile("[\\/\\/\\s]?");
        String s = p.matcher(date).replaceAll("");
        String[] fo = style.split("%");
        String year = "";
        String month = "";
        String day = "";
        String con = "";

        if (fo[0].equals("Y")) {
            year = s.substring(0, 4);
        } else if (fo[0].equals("y")) {
            year = s.substring(2, 4);
        }
        month = s.substring(4, 6);
        day = s.substring(6);
        con = style.substring(2, 3);

        return year + con + month + con + day;
    }

    public static String getMonth(Calendar calendar, String style) {
        Calendar cal = calendar;
        if (cal == null)
            cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        String monthS = Integer.toString(month);
        if ("m".equals(style)) {
            return monthS;
        } else if ("mm".equalsIgnoreCase(style)) {
            if (month < 10) {
                return "0" + monthS;
            }
            return monthS;
        } else {
            return null;
        }
    }

}
