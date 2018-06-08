package edu.teco.smartaqnet.sensorthings;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PostData {

    private static final String TAG = PostData.class.getName();


    public static String buildHashmap(HashMap <String, String> map, String key, String value){
        Map<String, String> comment = new HashMap<String, String>();
        comment.put(key, value);
        return new GsonBuilder().create().toJson(comment, Map.class);

    }

    public static void execute(String json) {
        if (makeRequest("http://192.168.0.1:3000/post/77/comments", json) == null){
            Log.d(TAG, "Post Request failed");
        }
    }

    @Nullable
    public static HttpResponse makeRequest(String uri, String json) {
        try {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            return new DefaultHttpClient().execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
