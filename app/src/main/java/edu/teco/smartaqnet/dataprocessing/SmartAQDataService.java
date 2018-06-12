package edu.teco.smartaqnet.dataprocessing;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import edu.teco.smartaqnet.bluetooth.BLEReadService;
import edu.teco.smartaqnet.sensorthings.Datastream;
import edu.teco.smartaqnet.sensorthings.Observation;
import edu.teco.smartaqnet.http.HttpPostData;
import edu.teco.smartaqnet.sensorthings.TimestampUtils;

public class SmartAQDataService extends Service {

    private final String sensorsURL = "http://smartaqnet-dev.teco.edu:8080/FROST-Server/v1.0/Sensors";
    private final String observedPropertiesURL = "http://smartaqnet-dev.teco.edu:8080/FROST-Server/v1.0/ObservedProperties";
    private final String thingsURL = "http://smartaqnet-dev.teco.edu:8080/FROST-Server/v1.0/Things";
    private final String datastreamsURL = "http://smartaqnet-dev.teco.edu:8080/FROST-Server/v1.0/Datastreams";
    private final String observationsURL = "http://smartaqnet-dev.teco.edu:8080/FROST-Server/v1.0/Observations";


    private final String TAG = SmartAQDataService.class.getName();
    private boolean isCreatedDatastream;
    private ObjectQueue<SmartAQDataObject> smartAQDataqueue;

    public int onStartCommand(Intent intent, int flags, int startId) {
        //init
        isCreatedDatastream = false;
        smartAQDataqueue = (new SmartAQDataQueue(getApplicationContext().getCacheDir().toString())).getSmartAQDataQueue();
        registerReceiver();
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    // Handles various events fired by the Service.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver smartAQUpdateREceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            String url = "";
                switch(action){
                    case BLEReadService.ACTION_DATA_AVAILABLE:
                        //TODO: Create BLEReadService.ACTION_SET_DEVICE_NAME and move createDatastream
                        if (!isCreatedDatastream) {
                            createDatastream(intent.getStringExtra(BLEReadService.EXTRA_DEVICE_NAME));
                            isCreatedDatastream = true;
                        } else {
                            // TODO: Fehelerbehandlung
                        }

                        byte[] bytes = intent.getByteArrayExtra(BLEReadService.EXTRA_BYTES);
                        SmartAQDataObject smartAQData = (SmartAQDataObject) ObjectByteConverterUtility.convertFromByte(bytes);
                        setDataTimeStamp(smartAQData);
                        //TODO: Kapseln in sendObservation
                        try {
                            smartAQDataqueue.add(smartAQData);
                            Gson gson = new Gson();
                            String observationAsJson = gson.toJson(new Observation(smartAQData,getApplicationContext()));
                            HttpPostData.startJsonPost(observationsURL, observationAsJson, getApplicationContext());
                            //TODO: Http Success not Checked
                        } catch (IOException e) {
                            //TODO: Unhandled Exception
                            e.printStackTrace();
                        }
                        break;
                    case HttpPostData.ACTION_HTTP_POST_SUCESS:
                        //TODO: Not working HTTP as a service?
                        url = intent.getStringExtra(HttpPostData.EXTRA_URL);
                        if(url.equals(observationsURL)){
                            try {
                                smartAQDataqueue.remove();
                            } catch (IOException e) {
                                //TODO: Unhandled Exception
                                e.printStackTrace();
                            }
                            //TODO: COntinue sending observations
                        }
                        Log.d(TAG, "onReceive: HTTP_SUCCESS: " + url);
                        //TODO: Continue sending data in larger chunks
                        break;
                    case HttpPostData.ACTION_HTTP_POST_FAILED:
                        url = intent.getStringExtra(HttpPostData.EXTRA_URL);
                        Log.d(TAG, "onReceive: HTTP_FAILED: " + url);
                        //TODO; Reset sending to one object at a time and keep trying
                        break;
                    default:
                        break;
                }
        }
    };

    private void registerReceiver(){
        registerReceiver(smartAQUpdateREceiver, smartAQUpdateIntentFilter());
    }

    private static IntentFilter smartAQUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEReadService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(HttpPostData.ACTION_HTTP_POST_SUCESS);
        intentFilter.addAction(HttpPostData.ACTION_HTTP_POST_FAILED);
        return intentFilter;
    }
    public void createDatastream(String device_name){
        Datastream datastream = new Datastream(device_name);
        Gson gson = new Gson();
        String gsonsensor = gson.toJson(datastream.getSensor());
        String gsonobservedproperty = gson.toJson(datastream.getObservedProperty());
        String gsonthing = gson.toJson(datastream.getThing());
        String gsondatastream = gson.toJson(datastream);
        HttpPostData.startJsonPost(sensorsURL, gsonsensor, getApplicationContext());
        HttpPostData.startJsonPost(observedPropertiesURL, gsonobservedproperty, getApplicationContext());
        HttpPostData.startJsonPost(thingsURL, gsonthing, getApplicationContext());
        HttpPostData.startJsonPost(datastreamsURL, gsondatastream, getApplicationContext());
        //System.out.println("Hier");
    }

    public void setDataTimeStamp(SmartAQDataObject data){
        data.setTimeStamp(TimestampUtils.getISO8601StringForCurrentDate());
    }
}
