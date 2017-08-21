package tufer.com.menutest.UIActivity.general.weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tufer.com.menutest.R;
import tufer.com.menutest.Util.ForecastWeatherInfo;

/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class WeatherCityActivity extends Activity {
    private weather mWeather;
    private TextView weather_city_name,careful;
    private ScrollView weatherScrollView;
    private ArrayList<ForecastWeatherInfo> weatherInfoList;
    protected LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_general_weather);
        initView();
        weather_city_name.setText(mWeather.getCity()+getString(R.string.str_mainmenu_general_weather));
        careful.setText(mWeather.getGanmao()+","+weatherInfoList.get(0).getNotice());
        careful.setSelected(true);
        this.mLinearLayout = new LinearLayout(this);
        Object localObject = new LinearLayout.LayoutParams(-1, -1);
        this.mLinearLayout.setOrientation(1);
        this.mLinearLayout.setGravity(17);
        this.mLinearLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
        int i = 0;
        ForecastWeatherInfo weatherInfo;
        LinearLayout localLinearLayout;
        ImageView localImageView;
        TextView localTextView1,localTextView2,localTextView3;
        while (i < weatherInfoList.size())
        {

            weatherInfo = (ForecastWeatherInfo)weatherInfoList.get(i);
            localObject = LayoutInflater.from(this).inflate(R.layout.mainmenu_general_weather_item, null);
            localLinearLayout = (LinearLayout)((View)localObject).findViewById(R.id.weather_item_LinearLayout);

            localTextView1 = (TextView)localLinearLayout.getChildAt(0);
            localImageView = (ImageView) localLinearLayout.getChildAt(1);
            localTextView2 = (TextView)localLinearLayout.getChildAt(2);
            localTextView3= (TextView) localLinearLayout.getChildAt(3);
            int condIcon = mWeather.CondIcon(weatherInfo.getType(), WeatherCityActivity.this);
            localImageView.setImageResource(condIcon);
            localTextView1.setText((Calendar.getInstance().get(Calendar.MONTH) + 1)+getString(R.string.month)+weatherInfo.getDate());
            String L_tmp = weatherInfo.getLow();
            String H_tmp = weatherInfo.getHigh();
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(L_tmp);
            Matcher d = p.matcher(H_tmp);
            d.find();
            m.find();
            localTextView3.setText(m.group() + "~" + d.group() + getString(R.string.degree) + "C");
            localTextView2.setText(weatherInfo.getType());
            mLinearLayout.addView((View)localObject);
            i ++;
        }
        weatherScrollView.addView(mLinearLayout);
    }

    private void initView() {
        mWeather= WeaterActivity.mWeather;
        weatherInfoList=mWeather.getWeatherInfoList();
        weather_city_name= (TextView) findViewById(R.id.weather_city_name);
        careful= (TextView) findViewById(R.id.careful);
        weatherScrollView= (ScrollView) findViewById(R.id.weatherScrollView);
    }
}
