package tufer.com.menutest.UIActivity.general.weather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tufer.com.menutest.R;
import tufer.com.menutest.Util.ForecastWeatherInfo;
import tufer.com.menutest.Util.Tools;
import tufer.com.menutest.Util.url;

/**
 * Created by Administrator on 2017/7/11 0011.
 */
public class WeaterActivity extends Activity  {
    //Button weather_btn_return,weather_btn_submit;
    TextView weather_text_city,weather_text_currentTemperature, weather_text_today_temperature,
            weather_text_today_weather,weather_text_today_wind;
    Button queryCityWeather,seeCityWeather;
    ImageView weather_image_today_icon;
    private HttpUtils httpUtils;
    private boolean isxztrue=false;
    private String city;
    public static weather mWeather;
    private ProgressDialog processDia=null;
    private ArrayList<ForecastWeatherInfo> weatherInfoList;
    LinearLayout mainlinear_general_weather;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeLoadingDialog();
            switch (msg.what){
                case 0:
                    Toast.makeText(WeaterActivity.this,getString(R.string.get_weather_data_shibai),Toast.LENGTH_SHORT).show();
                    //finish();
                    //mWeather=null;
                    break;
                case 1:


                    queryCityWeather.requestFocus();
                    queryCityWeather.requestFocusFromTouch();
                    mainlinear_general_weather.invalidate();
                    break;
                case 2:
                    Toast.makeText(WeaterActivity.this,getString(R.string.query_cityname_shibai),Toast.LENGTH_SHORT).show();
                    //getWeather(city);
                    break;
            }

        }
    };

    //ListView myListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_general_weather_city);
        showLoadingDialog(getString(R.string.str_mainmenu_general_weather_query),true);
        initView();
        new Thread(){
            @Override
            public void run() {
                super.run();
                allNetworkRequest();

//                try {
//                    Thread.sleep(500);
//                    if(mWeather.getCity()!=null){
//                        Tools.closeLoadingDialog();
//                    }
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }.start();




    }



    private void initView() {
        httpUtils = new HttpUtils();
        mWeather = new weather();
        weather_text_city= (TextView) findViewById(R.id.city);
        weather_text_currentTemperature= (TextView) findViewById(R.id.current_temperature);
        weather_text_today_temperature= (TextView) findViewById(R.id.today_temperature);
        weather_text_today_weather= (TextView) findViewById(R.id.weather_type);
        weather_text_today_wind= (TextView) findViewById(R.id.wind_power);
        //weather_text_today_ganmao= (TextView) findViewById(R.id.careful);
        weather_image_today_icon= (ImageView) findViewById(R.id.weather_icon);
        mainlinear_general_weather= (LinearLayout) findViewById(R.id.mainlinear_general_weather);
        queryCityWeather= (Button) findViewById(R.id.query_city_weather);
        seeCityWeather= (Button) findViewById(R.id.see_detail_weather);
        queryCityWeather.requestFocus();
        queryCityWeather.requestFocusFromTouch();
        queryCityWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputTitleDialog();
            }
        });
        seeCityWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mWeather.getWeatherInfoList()!=null&&mWeather.getWeatherInfoList().size()>0){
                    Intent intent=new Intent(WeaterActivity.this,WeatherCityActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putSerializable("mWeather",mWeather);
//                intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
    }
    private void inputTitleDialog() {

        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.input_city_name))
                .setView(inputServer)
                .setNegativeButton(getString(R.string.str_mainmenu_dialog_cancel), null);
        builder.setPositiveButton(getString(R.string.str_mainmenu_dialog_confirm),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String inputName = inputServer.getText().toString();
                        if (inputName==null||inputName.equals("")){
                            Toast.makeText(WeaterActivity.this,"城市名不能为空，查询失败！",Toast.LENGTH_SHORT).show();
                        }else{
                            if (inputName!=null) {
                                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                                Matcher m = p.matcher(inputName);
                                inputName = m.replaceAll("");
                            }
                            //city=inputName;
                            showLoadingDialog(getString(R.string.str_mainmenu_general_weather_query),true);
                            final String finalInputName = inputName;
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    getWeather(finalInputName);
//                                    handler.sendEmptyMessage(1);
//                                    Tools.closeLoadingDialog();
//                                    try {
//                                        Thread.sleep(500);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
                                }
                            }.start();
                        }
                    }
                });
        builder.show();
    }




    public void allNetworkRequest() {

        httpUtils.send(HttpRequest.HttpMethod.GET, url.getCity,
                new RequestCallBack<String>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        //ToastUtils.showShort(WeaterActivity.this, "network error ! ");
                        handler.sendEmptyMessage(0);
                    }
                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        String result = arg0.result;
                        if (!result.contains("<")) {
                            try {
                                if(mWeather.myJsons(result)!=null)
                                    city = mWeather.myJsons(result).getString("city");
                                RequestParams params = new RequestParams();
                                params.addHeader("apikey", "");
                                getWeather(city);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void getWeather(final String strCity) {
        httpUtils.send(HttpRequest.HttpMethod.GET,
                url.weatherUrl+ strCity,
                new RequestCallBack<String>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        ///ToastUtils.showShort(WeaterActivity.this, "network error ! ");
                        handler.sendEmptyMessage(0);
                    }
                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        String result = arg0.result;
                        Log.d("yesuo","result___________"+result);
                        JSONObject isgetweter = mWeather.myJson(result);
                        isxztrue=false;
                        if(isgetweter!=null){
                            city=strCity;
                            setwehter();
                            handler.sendEmptyMessage(1);
                            //Tools.closeLoadingDialog();


                        }else{
                            handler.sendEmptyMessage(2);
//                            httpUtils.send(HttpRequest.HttpMethod.GET,
//                                    url.worldWeatherUrlz+ city,
//                                    new RequestCallBack<String>() {
//                                        @Override
//                                        public void onFailure(HttpException arg0, String arg1) {
//                                            //ToastUtils.showShort(WeaterActivity.this, "network error ! ");
//                                            handler.sendEmptyMessage(2);
//                                        }
//                                        @Override
//                                        public void onSuccess(ResponseInfo<String> arg0) {
//                                            String result = arg0.result;
//                                            mWeather.myJsonxz(result);
//                                            isxztrue = true;
//                                            setwehter();
//                                        }
//                                    });
                        }
                    }
                });
    }

    public void setwehter(){
        weatherInfoList=mWeather.getWeatherInfoList();
        ForecastWeatherInfo weatherInfo=weatherInfoList.get(0);
        String L_tmp = weatherInfo.getLow();
        String H_tmp = weatherInfo.getHigh();

        if(L_tmp!=null){
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(L_tmp);
            Matcher d = p.matcher(H_tmp);
            d.find();
            m.find();
            weather_text_city.setText(city+getString(R.string.str_mainmenu_general_weather));
            weather_text_today_weather.setText(weatherInfo.getType());
            weather_text_today_temperature.setText(m.group() + "~" + d.group() + getString(R.string.degree) + "C");
            weather_text_currentTemperature.setText(mWeather.getTmp()+getString(R.string.str_temperaturedet_centigrade));
            //weather_text_today_ganmao.setText(mWeather.getGanmao());
            if(isxztrue){
                weather_text_today_wind.setText(weatherInfo.getFengli()+getString(R.string.whtertji));
            }else{
                weather_text_today_wind.setText(weatherInfo.getFengxiang()+","+weatherInfo.getFengli());
            }
            int condIcon = mWeather.CondIcon(weatherInfo.getType(), WeaterActivity.this);
            Log.d("yesuo","condicon"+condIcon);
            weather_image_today_icon.setImageResource(condIcon);
        }
    }
    /**
     * 显示加载中对话框
     *
     *
     */
    private void showLoadingDialog(String message, boolean isCancelable) {
        if (processDia == null) {
            processDia= new ProgressDialog(this, R.style.dialog1);
            //点击提示框外面是否取消提示框
            processDia.setCanceledOnTouchOutside(false);
            //点击返回键是否取消提示框
            processDia.setCancelable(isCancelable);
            processDia.setIndeterminate(true);
            processDia.setMessage(message);
            processDia.show();
        }
    }

    /**
     * 关闭加载对话框
     */
    private void closeLoadingDialog() {
        if (processDia != null) {
            if (processDia.isShowing()) {
                processDia.cancel();
            }
            processDia = null;
        }
    }


}
