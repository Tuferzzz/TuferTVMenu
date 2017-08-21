package tufer.com.menutest.UIActivity.general.appinfo;

/**
 * Created by Administrator on 2017/7/20 0020.
 */

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class AppInfo implements Serializable
{
    private String appName;
    private long cacheSize;
    private long codeSize;
    private long dataSize;
    private Drawable icon;
    private boolean isSystemApp;
    private String packageName;
    private String spaceManagerActivityName;
    private String versionName;

    public Drawable getAppIcon()
    {
        return this.icon;
    }

    public String getAppName()
    {
        return this.appName;
    }

    public long getCacheSize()
    {
        return this.cacheSize;
    }

    public long getCodeSize()
    {
        return this.codeSize;
    }

    public long getDataSize()
    {
        return this.dataSize;
    }

    public String getPackageName()
    {
        return this.packageName;
    }

    public long getTotalSize()
    {
        return getCacheSize() + getDataSize() + getCodeSize();
    }

    public String getVersionName()
    {
        return this.versionName;
    }

    public void setAppIcon(Drawable paramDrawable)
    {
        this.icon = paramDrawable;
    }

    public void setAppName(String paramString)
    {
        this.appName = paramString;
    }

    public void setCacheSize(long paramLong)
    {
        this.cacheSize = paramLong;
    }

    public void setCodeSize(long paramLong)
    {
        this.codeSize = paramLong;
    }

    public void setDataSize(long paramLong)
    {
        this.dataSize = paramLong;
    }

    public void setPackageName(String paramString)
    {
        this.packageName = paramString;
    }

    public void setSpaceManagerActivityName(String paramString)
    {
        this.spaceManagerActivityName = paramString;
    }

    public void setSystemApp(boolean paramBoolean)
    {
        this.isSystemApp = paramBoolean;
    }

    public void setVersionName(String paramString)
    {
        this.versionName = paramString;
    }
}
