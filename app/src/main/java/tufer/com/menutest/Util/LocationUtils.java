package tufer.com.menutest.Util;


import android.Manifest;
import android.content.Context;

import android.location.Location;

import android.location.LocationManager;
import android.os.AsyncTask;

import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.List;

/**
 * Created by Administrator on 2017/7/11 0011.
 */

public class LocationUtils {

    //public static String cityName = "深圳";  //城市名
    public static String cityName;  //城市名


    public static void getCityName(Context context) {
        Location location;
        String provider;//位置提供器
        LocationManager locationManager;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);//获得位置服务
        provider = judgeProvider(locationManager, context);
        if (provider != null) {//有位置提供器的情况
            //为了压制getLastKnownLocation方法的警告
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }

            location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                getLocation(location);//得到当前经纬度并开启线程去反向地理编码
            } else {
                Toast.makeText(context, "暂时无法获得当前位置", Toast.LENGTH_SHORT).show();
            }
        } else {//不存在位置提供器的情况

        }
    }

    private static void getLocation(Location location) {
        String latitude = location.getLatitude() + "";
        String longitude = location.getLongitude() + "";
        String url = "http://api.map.baidu.com/geocoder/v2/?ak=XmHVLqm0p3TIBOGkA7NuYjc3VqGeiKuL&callback=renderReverse&location=" + latitude + "," + longitude + "&output=json&pois=0";
        new MyAsyncTask(url).execute();
    }


    private static String judgeProvider(LocationManager locationManager, Context context) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(context, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    static class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        String url = null;//要请求的网址
        String str = null;//服务器返回的数据
        String address = null;

        public MyAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            str = GetHttpConnectionData.getData(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                str = str.replace("renderReverse&&renderReverse","");
                str = str.replace("(","");
                str = str.replace(")","");
                JSONObject jsonObject = new JSONObject(str);
                JSONObject address = jsonObject.getJSONObject("result");
                String city = address.getString("formatted_address");
                String district = address.getString("sematic_description");
                cityName=city+district;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(aVoid);

        }
    }
}
