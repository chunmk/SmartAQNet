package edu.teco.smartaqnet.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class HttpPostData {

    private static final String TAG = HttpPostData.class.getName();

    //Starts http connection in own thread, so that mainActivity can continue
    public static void startJsonPost(final String surl, final String json){
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(surl);
                    URLConnection con;
                    con = url.openConnection();
                    HttpURLConnection http = (HttpURLConnection)con;
                    http.setRequestMethod("POST");
                    http.setDoOutput(true);
                    http.setFixedLengthStreamingMode(json.length());
                    http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    http.connect();
                    try(OutputStream os = http.getOutputStream()) {
                        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                        os.write(bytes);
                        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        Log.d(TAG, "HttpResponse: " + response.toString());
                    } catch (IOException e) {
                        //TODO: Handle Exception
                        e.printStackTrace();
                    }

                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
}
