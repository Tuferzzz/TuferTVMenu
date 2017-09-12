package tufer.com.menutest.UIActivity.general.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import tufer.com.menutest.R;
import tufer.com.menutest.Util.ForecastWeatherInfo;


public class weather implements Serializable{
	private String city; 
	private ArrayList<ForecastWeatherInfo> WeatherInfoList ;
	private JSONObject json;
	private String tmp;
	private String ganmao;
	private double pm25;
	private double pm10;
	private String quality;
	public weather(){
		WeatherInfoList=new ArrayList<ForecastWeatherInfo>();
	}




	private int[] weatherCondIcon = {R.drawable.baoxue, R.drawable.baoyu,R.drawable.baoyuzhuandabaoyu,
			R.drawable.dabaoyu,R.drawable.dabaoyuzhuantedabaoyu,R.drawable.daxue,
			R.drawable.daxuezhuanbaoxue,R.drawable.dayu,R.drawable.dayuzhuanbaoyu,
			R.drawable.dongyu,R.drawable.duoyun,R.drawable.fuchen,
			R.drawable.leizhenyu,R.drawable.leizhenyubanyoubingbao,
			R.drawable.mai,R.drawable.xiaoyu,R.drawable.qiangshachenbao,R.drawable.qing,R.drawable.shachenbao,
			R.drawable.tedabaoyu,R.drawable.wu,R.drawable.xiaoxue,R.drawable.xiaoxuezhuanzhongxue,
			R.drawable.xiaoyu,R.drawable.xiaoyuzhuanzhongyu,
			R.drawable.yangsha,R.drawable.yin,R.drawable.yujiaxue,R.drawable.zhenxue,
			R.drawable.zhenyu,R.drawable.zhongxue,R.drawable.zhongxuezhuandaxue,
			R.drawable.zhongyu,R.drawable.zhongyuzhuandayu};


	public  JSONObject myJson2(String responseString) {
		if(responseString!=null){
			try {
				json = new JSONObject(responseString);
				String isdescok = json.getString("message");
				if(isdescok.equals("Success !")){
					if(WeatherInfoList.size()>0){
						WeatherInfoList=new ArrayList<ForecastWeatherInfo>();
					}
					city=json.getString("city");
					JSONObject reson =json.getJSONObject("data");
					tmp=reson.getString("wendu");
					ganmao=reson.getString("ganmao");
					pm25=reson.getDouble("pm25");
					pm10=reson.getDouble("pm10");
					quality=reson.getString("quality");
					JSONObject forecast;
					for(int i=0;i<5;i++){
						ForecastWeatherInfo weatherInfo=new ForecastWeatherInfo();
						forecast= reson.getJSONArray("forecast").getJSONObject(i);
						if(forecast .getString("high")!=null){
							weatherInfo.setDate(forecast.getString("date"));
							weatherInfo.setHigh(forecast.getString("high"));
							weatherInfo.setFengli(forecast.getString("fl"));
							weatherInfo.setLow(forecast.getString("low"));
							weatherInfo.setType(forecast.getString("type"));
							weatherInfo.setFengxiang(forecast.getString("fx"));
							weatherInfo.setNotice(forecast.getString("notice"));
						}
						WeatherInfoList.add(weatherInfo);
					}
				}else{
					return null;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return json;
	}


	public  JSONObject myJson1(String responseString) {
		if(responseString!=null){
			try {
				json = new JSONObject(responseString);
				String isdescok = json.getString("desc");
				if(isdescok.equals("OK")){
					if(WeatherInfoList.size()>0){
						WeatherInfoList=new ArrayList<ForecastWeatherInfo>();
					}
					JSONObject reson =json.getJSONObject("data");
					city=reson.getString("city");
					tmp=reson.getString("wendu");
					ganmao=reson.getString("ganmao");
//					pm25=reson.getDouble("pm25");
//					pm10=reson.getDouble("pm10");
//					quality=reson.getString("quality");
					JSONObject forecast;
					for(int i=0;i<5;i++){
						ForecastWeatherInfo weatherInfo=new ForecastWeatherInfo();
						forecast= reson.getJSONArray("forecast").getJSONObject(i);
						if(forecast .getString("high")!=null){
							weatherInfo.setDate(forecast.getString("date"));
							weatherInfo.setHigh(forecast.getString("high"));
							String str=forecast.getString("fengli");
							weatherInfo.setFengli(str.substring(9,str.length()-3));
							weatherInfo.setLow(forecast.getString("low"));
							weatherInfo.setType(forecast.getString("type"));
							weatherInfo.setFengxiang(forecast.getString("fengxiang"));
//							weatherInfo.setNotice(forecast.getString("notice"));
						}
						WeatherInfoList.add(weatherInfo);
					}
				}else{
					return null;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return json;
	}
	public  JSONObject myJsonxz(String responseString) {
		WeatherInfoList=new ArrayList<ForecastWeatherInfo>();
		if(responseString!=null){
			try {
				json = new JSONObject(responseString);
				JSONArray jsdescok = json.getJSONArray("results");
				JSONObject forecastz = jsdescok.getJSONObject(0);
				JSONObject forecast;
				for(int i=0;i<5;i++){
					ForecastWeatherInfo weatherInfo=new ForecastWeatherInfo();
					forecast= forecastz.getJSONArray("daily").getJSONObject(i);
					if(forecast .getString("high")!=null){
						weatherInfo.setDate(forecast.getString("date"));
						weatherInfo.setHigh(forecast.getString("high"));
						weatherInfo.setFengli(forecast.getString("fengli"));
						weatherInfo.setLow(forecast.getString("low"));
						weatherInfo.setType(forecast.getString("type"));
						//setCity(json.getString("city"));
					}
					WeatherInfoList.add(weatherInfo);
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

		}else{
			return null;
		}
		return json;
	}
	/**
	 * 
	 * @param 
	 * @return city
	 * @throws JSONException
	 */
	public  JSONObject myJsons(String responseString){

		if(responseString!=null){
			try {
				json = new JSONObject(responseString);
				JSONObject reson =json.getJSONObject("data");
				city=reson.getString("city");
				tmp=reson.getString("wendu");
				ganmao=reson.getString("ganmao");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		

		}else{
			return null;
		}
		return json;

	}
		
	/**
	 * @param 
	 * @return 
	 * **/
	public int CondIcon(String getWeatherCond,Context context){
		  String[] weatherCond = context.getResources().getStringArray(R.array.weather);	
		  if(getWeatherCond!=null){
			  for (int i = 0; i < weatherCond.length; i++) {
				   if (getWeatherCond.equals(weatherCond[i])) {
					    return weatherCondIcon[i];
				   }
			  }
		  }else{
			  return (Integer) null;
		  }
		  return weatherCondIcon[10];
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public ArrayList<ForecastWeatherInfo> getWeatherInfoList() {
		return WeatherInfoList;
	}

	public void setWeatherInfoList(ArrayList<ForecastWeatherInfo> weatherInfoList) {
		WeatherInfoList = weatherInfoList;
	}

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public String getTmp() {
		return tmp;
	}

	public void setTmp(String tmp) {
		this.tmp = tmp;
	}

	public int[] getWeatherCondIcon() {
		return weatherCondIcon;
	}

	public void setWeatherCondIcon(int[] weatherCondIcon) {
		this.weatherCondIcon = weatherCondIcon;
	}

	public String getGanmao() {
		return ganmao;
	}

	public void setGanmao(String ganmao) {
		this.ganmao = ganmao;
	}
}
