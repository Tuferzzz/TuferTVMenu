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

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ApplicationInformation implements Parcelable {

    private Bitmap iconAppDrawable;

    private String packageName;

    private String appName;

    private String versionName = "";

    private int versionCode = 0;

    private long cacheSize;

    private long dataSize;

    private long appSize;

    private long totalSize;

    private byte[] bitmap;

    // kernel user-ID
    private int uid;

    public ApplicationInformation() {
    }

    /**
     * @return the iconAppDrawable.
     */
    public Bitmap getIconAppDrawable() {
        return iconAppDrawable;
    }

    /**
     * @param iconAppDrawable the iconAppDrawable to set.
     */
    public void setIconAppDrawable(Bitmap iconAppDrawable) {
        this.iconAppDrawable = iconAppDrawable;
    }

    /**
     * @return the packageName.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName the packageName to set.
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return the appName.
     */
    public String getAppName() {
        return appName;
    }

    /**
     * @param appName the appName to set.
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return the versionName.
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * @param versionName the versionName to set.
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * @return the versionCode.
     */
    public int getVersionCode() {
        return versionCode;
    }

    /**
     * @param versionCode the versionCode to set.
     */
    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * @return the cacheSize.
     */
    public long getCacheSize() {
        return cacheSize;
    }

    /**
     * @param cacheSize the cacheSize to set.
     */
    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    /**
     * @return the dataSize.
     */
    public long getDataSize() {
        return dataSize;
    }

    /**
     * @param dataSize the dataSize to set.
     */
    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    /**
     * @return the appSize.
     */
    public long getAppSize() {
        return appSize;
    }

    /**
     * @param appSize the appSize to set.
     */
    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    /**
     * @return the totalSize.
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * @param totalSize the totalSize to set.
     */
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * @return the bitmap.
     */
    public byte[] getBitmap() {
        return bitmap;
    }

    /**
     * @param bitmap the bitmap to set.
     */
    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * @return the uid.
     */
    public int getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set.
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    public static final Creator<ApplicationInformation> CREATOR = new Creator<ApplicationInformation>() {
        public ApplicationInformation createFromParcel(Parcel source) {
            ApplicationInformation mAppInfo = new ApplicationInformation();
            mAppInfo.appName = source.readString();
            mAppInfo.packageName = source.readString();
            mAppInfo.versionName = source.readString();
            mAppInfo.totalSize = source.readLong();
            mAppInfo.appSize = source.readLong();
            mAppInfo.dataSize = source.readLong();
            mAppInfo.cacheSize = source.readLong();
            mAppInfo.versionCode = source.readInt();
            mAppInfo.uid = source.readInt();
            mAppInfo.bitmap = source.createByteArray();

            return mAppInfo;
        }

        public ApplicationInformation[] newArray(int size) {
            return new ApplicationInformation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeString(packageName);
        dest.writeString(versionName);
        dest.writeLong(totalSize);
        dest.writeLong(appSize);
        dest.writeLong(dataSize);
        dest.writeLong(cacheSize);
        dest.writeInt(versionCode);
        dest.writeInt(uid);
        dest.writeByteArray(bitmap);
    }

}
