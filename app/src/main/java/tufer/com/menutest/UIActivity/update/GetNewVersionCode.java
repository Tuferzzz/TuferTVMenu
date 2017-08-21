package tufer.com.menutest.UIActivity.update;


import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class GetNewVersionCode {

	public static JSONObject getVersionJSON(String VerJSONPath) throws  IOException, JSONException{
		Log.d("jim","GetNewVersionCode()->VerJSONPath = "+VerJSONPath);
		StringBuilder VerJSON = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		HttpResponse response;
		response = client.execute(new HttpGet(VerJSONPath));

		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		
			HttpEntity entity = response.getEntity();
			if(entity != null){
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"), 8192);
				String line = null;
				while((line = reader.readLine()) != null){
					Log.d("jim","line="+line);
					VerJSON.append(line+"\n");
				}
				reader.close();
				Log.d("chend","VerJSON.toString()="+VerJSON.toString());				
				//JSONArray verJSONArray = new JSONArray(VerJSON.toString());
				JSONObject jsimple = new JSONObject(VerJSON.toString());
				Log.d("jim","VerJSON.toString()="+jsimple);
				//Log.d("jim","verJSONArray="+verJSONArray);
				//Log.d("jim","verJSONArray.length()="+verJSONArray.length());
				return jsimple;
				//if(verJSONArray.length() > 0){
				//	JSONObject obj = verJSONArray.getJSONObject(0);
				//	Log.d("jim","obj="+obj);
				//	return obj;
			//	}
			}
	
			return null;
		}

		return null;
	}

	
	public static int getServerMACJSON(String macJSONPath,String macaddr) throws ClientProtocolException, IOException, JSONException{
		StringBuilder VerJSON = new StringBuilder();
		String mac = "**:**:**:**:**:**";
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		HttpResponse response;
		response = client.execute(new HttpGet(macJSONPath));
		
	
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
	
			HttpEntity entity = response.getEntity();
			if(entity != null){
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"), 8192);
				String line = null;
				while((line = reader.readLine()) != null){
					VerJSON.append(line+"\n");
					Log.d("jim","line="+line);
					if(line.contains(macaddr)){
						return 0;
					}
					if(line.contains(mac)){
						return 0;
					}
				}
				reader.close();
			}
	
			return -1;
		}		
		return -1;
	}
}
