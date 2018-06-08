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

import java.util.Locale;

import edu.teco.smartaqnet.bluetooth.BLEReadService;
import edu.teco.smartaqnet.sensorthings.Location;
import edu.teco.smartaqnet.sensorthings.Observation;

public class

SmartAQDataService extends Service {

    private final String TAG = SmartAQDataService.class.getName();

    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver();
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
                if(action.equals(BLEReadService.ACTION_DATA_AVAILABLE)) {
                    //TODO: Daten verarbeiten
                    Log.d(TAG, "Received data in SmartAQDataservice");
                    byte[] bytes = intent.getByteArrayExtra(BLEReadService.EXTRA_BYTES);
                    SmartAQDataObject smartAQData = (SmartAQDataObject) ObjectByteConverterUtility.convertFromByte(bytes);
                    Gson gsonloc = new Gson();
                    Location loc = new Location();
                    String locgson = gsonloc.toJson(loc);
                    String obsgson = gsonloc.toJson(new Observation(smartAQData.getBleDustData()));
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

}
