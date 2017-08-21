package tufer.com.menutest.UIActivity.update;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONData1 {


	public static String getJsonString(JSONObject json) {
		Log.i("JRM", "send:" + json.toString());
		StringBuffer buffer = new StringBuffer();
		try {

			// String jsonUrl = "http://61.183.248.221:8086/wigAdmin/clientService.jsp";
			String jsonUrl = "http://tvosapp.babao.com/interface/clientService.jsp";

			URL url = new URL(jsonUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "text/json; charset=UTF-8");
		
			String clientInfor = "full_mstara3-eng_20_100";
			conn.setRequestProperty("Ttag", clientInfor);
			conn.setDoOutput(true);
			conn.connect();

			Writer writer = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
			writer.write(json.toString());
			writer.flush();

			System.out.println(conn.getResponseCode());

			InputStream u = conn.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(u, "utf-8"));

			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("JRM", "return:" + buffer.toString());
		return buffer.toString();
	}

	public static String getInfor() {

		JSONObject json = new JSONObject();

		try {
			json.put("ifid", "TVOSVerUpdate");
			json.put("pla", "full_mstara3-eng");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return getJsonString(json);
	}

}
