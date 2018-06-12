package edu.teco.smartaqnet.dataprocessing;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import edu.teco.smartaqnet.bluetooth.BLEReadService;
import edu.teco.smartaqnet.sensorthings.Datastream;
import edu.teco.smartaqnet.sensorthings.Observation;
import edu.teco.smartaqnet.http.HttpPostData;
import edu.teco.smartaqnet.sensorthings.TimestampUtils;

public class

SmartAQDataService extends Service {

    private final String TAG = SmartAQDataService.class.getName();
    private boolean isCreatedDatastream;
    private String actualDevice;
    private final ObjectQueue<SmartAQDataObject> smartAQDataqueue =
            (new SmartAQDataQueue(getApplicationContext().getCacheDir().toString())).getSmartAQDataQueue();

    public int onStartCommand(Intent intent, int flags, int startId) {
        //init
        isCreatedDatastream = false;
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
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
                if(action.equals(BLEReadService.ACTION_DATA_AVAILABLE)) {
                    if (!isCreatedDatastream) {
                        createDatastream(intent.getStringExtra(BLEReadService.EXTRA_DEVICE_NAME));
                        isCreatedDatastream = true;
                    } else {
                        // TODO: Fehelerbehandlung
                    }

                    //TODO: Daten verarbeiten
                    byte[] bytes = intent.getByteArrayExtra(BLEReadService.EXTRA_BYTES);
                    SmartAQDataObject smartAQData = (SmartAQDataObject) ObjectByteConverterUtility.convertFromByte(bytes);
                    setDataTimeStamp(smartAQData);
                    try {
                        smartAQDataqueue.add(smartAQData);
                        smartAQData = smartAQDataqueue.peek();
                    } catch (IOException e) {
                        //TODO: Unhandled Exception
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    String observationAsJson = gson.toJson(new Observation(smartAQData));
                    Log.d(TAG, "Received data in SmartAQDataservice");
                }
        }
    };

    private void registerReceiver(){
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEReadService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    public void createDatastream(String device_name){
        Datastream datastream = new Datastream(device_name);
        Gson gson = new Gson();
        String gsonsensor = gson.toJson(datastream.getSensor());
        String gsonobservedproperty = gson.toJson(datastream.getObservedProperty());
        String gsonthing = gson.toJson(datastream.getThing());
        String gsondatastream = gson.toJson(datastream);
        HttpPostData.startJsonPost("http://smartaqnet-dev.teco.edu:8080/FROST-Server/v1.0/Sensors", gsonsensor);
        //System.out.println("Hier");
    }

    public void setDataTimeStamp(SmartAQDataObject data){
        data.setTimeStamp(TimestampUtils.getISO8601StringForCurrentDate());
    }
}
