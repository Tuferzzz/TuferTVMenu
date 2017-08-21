package tufer.com.menutest.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Administrator on 2017/7/11 0011.
 */
public class GetHttpConnectionData  {
    public static String getData(String path) {
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");// 设置请求的方式
            urlConnection.setReadTimeout(5000);// 设置超时的时间
            urlConnection.setConnectTimeout(5000);// 设置链接超时的时间
            if (urlConnection.getResponseCode() == 200) {
                InputStream in = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line = null;
                StringBuffer buffer = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }
                System.out.println(buffer);
                in.close();
                br.close();
                String result = buffer.toString();
                return result;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // url.openConnection()打开网络链接

        return "";
    }
}
