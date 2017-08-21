package tufer.com.menutest.Util;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/3 0003.
 */
public class ForecastWeatherInfo implements Serializable{
    private String date;
    private String high;
    private String fengli;
    private String low;
    private String type;
    private String fengxiang;
    private String notice;

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getFengli() {
        return fengli;
    }

    public String getLow() {
        return low;
    }

    public String getType() {
        return type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setType(String type) {
        this.type = type;
    }
}
