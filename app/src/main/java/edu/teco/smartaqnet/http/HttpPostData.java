package edu.teco.smartaqnet.http;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Utility class to send a JSon String via Http connection to a server
 * indicated by a url
 */
public class HttpPostData{

    private static final String TAG = HttpPostData.class.getName();

    public static final String ACTION_HTTP_POST_SUCESS = "ACTION_HTTP_POST_SUCESS";
    public static final String ACTION_HTTP_POST_FAILED = "ACTION_HTTP_POST_FAILED";
    /**
     * Identifier for broadcasting HTTP send results
     */
    public static final String EXTRA_URL = "EXTRA_URL";

    /**
     * Private constructor: class cannot be instantiated
     */
    private HttpPostData(){

    }
    /**
     * Starts http connection for sending JSon Data in own thread,
     * so that calling Activity may continue
     *
     * @param surl    adress of receiving server
     * @param json    to be sent
     * @param context calling activity
     */
    public static void startJsonPost(final String surl, final String json, final Context context){
        Thread actualThread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                    URL url = new URL(surl);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setRequestMethod("POST");
                    http.setDoInput(true);
                    http.setDoOutput(true);
                    http.setFixedLengthStreamingMode(bytes.length);
                    http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    http.setRequestProperty("Content-Encoding", "charset=UTF-8");
                    http.setRequestProperty("Accept", "application/json");
                    http.connect();
                    try(OutputStream os = http.getOutputStream()) {
                        os.write(bytes);
                        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        final Intent intent = new Intent(ACTION_HTTP_POST_SUCESS);
                        intent.putExtra(EXTRA_URL, surl);
                        //LocalBroadcastManager.getInstance(context).sendBroadcastSync(intent);
                        context.sendBroadcast(intent);
                        Log.d(TAG, "HttpResponse: " + response.toString());
                        Log.d(TAG, "Actual Sensorthing: " + surl);
                    } catch (IOException e) {
                        //TODO: Handle Exception
                        e.printStackTrace();
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    final Intent intent = new Intent(ACTION_HTTP_POST_FAILED);
                    intent.putExtra(EXTRA_URL, surl);
                    context.sendBroadcast(intent);
                }
            }
        });
        actualThread.start();
        try {
            actualThread.join();
        } catch(InterruptedException e){
            //TODO: unhandled Exception
            e.printStackTrace();
        }
    }
}
