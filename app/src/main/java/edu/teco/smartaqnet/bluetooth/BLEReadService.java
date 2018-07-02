/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.teco.smartaqnet.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

import edu.teco.smartaqnet.dataprocessing.SmartAQDataObject;
import edu.teco.smartaqnet.dataprocessing.ObjectByteConverterUtility;

import static android.content.Intent.EXTRA_TEXT;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device. Code partly taken from
 * https://developer.android.com/guide/topics/connectivity/bluetooth-le
 * Implemented as service, so that data maybe collected as background task
 */
public class BLEReadService extends Service {
    private final static String TAG = BLEReadService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private String deviceName;
    //Just to delay operations
    // Stops scanning after 2 seconds.
    private Handler mHandler = new Handler();
    //Time period used to search for BLE devices
    private static final long SCAN_PERIOD = 2000;

    //Actual UUID's used from SmartAQ BLE devices
    //Might be subject to change
    private final static UUID SMARTAQ_SERVICE_UUID =
            UUID.fromString("6e57fcf9-8064-4995-a3a8-e5ca44552192");
    private final static UUID CHARACTERISTIC_UUID =
            UUID.fromString("7a812f99-06fa-4d89-819d-98e9aafbd4ef");
    private final static UUID DESCRIPTOR_UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    public final static String ACTION_GATT_CONNECTED =
            "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "ACTION_DATA_AVAILABLE";

    public final static String EXTRA_BYTES =
            "EXTRA_BYTES";
    public final static String EXTRA_ASIZE =
            "EXTRA_ASIZE";
    public final static String EXTRA_DEVICE_NAME =
            "DEVICE_NAME";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Initialize Bluetooth services
        initialize();
        //get the device name chosen by user
        //TODO: Check id device has already been named
        connect(intent.getStringExtra("bleDeviceAdress"));
        deviceName = intent.getStringExtra("device_name");
        return START_REDELIVER_INTENT;
    }

    /*
     * Implements callback methods for GATT events that the app cares about.  For example,
     * connection change and services discovered.
     * As described in https://developer.android.com/guide/topics/connectivity/bluetooth-le
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            switch (newState){
                case BluetoothProfile.STATE_CONNECTED:
                    intentAction = ACTION_GATT_CONNECTED;
                    //Posting successful gatt server connection
                    broadcastUpdate(intentAction);
                    Log.i(TAG, "Connected to GATT server.");
                    mBluetoothGatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    intentAction = ACTION_GATT_DISCONNECTED;
                    Log.i(TAG, "Disconnected from GATT server.");
                    broadcastUpdate(intentAction);
                    break;
            }
        }

        /*
         * Connect with services offered by BL device
         * Notification switched on, so that measured data will be sent from sensor
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                BluetoothGattService service = mBluetoothGatt.getService(SMARTAQ_SERVICE_UUID);
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
                setCharacteristicNotification(characteristic,true);

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }


        /*
         * Not used as data is not called, but sent via notifications
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                characteristic.getStringValue(0);
                SmartAQDataObject data = new SmartAQDataObject();
                data.setBleDustData(characteristic.getStringValue(0));
                byte[] bytes = ObjectByteConverterUtility.convertToByte(data);
                broadcastUpdate(ACTION_DATA_AVAILABLE, bytes, deviceName);
            }
        }

        /*
        * Sends data received as notification from sensor via broadcast
        */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            try {
                SmartAQDataObject data = new SmartAQDataObject();
                data.setBleDustData(characteristic.getStringValue(0));
                //TODO: Try to write data directly to Output with:
                //Activity activity = getApplicationContext();
                //SetMainview.setData();
                //Should make receiving Data in BTDetect
                byte[] bytes = ObjectByteConverterUtility.convertToByte(data);
                broadcastUpdate(ACTION_DATA_AVAILABLE, bytes, deviceName);
            } catch (Exception e){
                //TODO: Handle exception
                e.printStackTrace();
            }
        }
    };

    // TODO: Check if everything is correctly finishd
    @Override
    public void onDestroy(){
        close();
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /*
     * Broadcast new data as serialized SmartAQDataobject (TODO: Check if data might also be sent as string)     */
    private void broadcastUpdate(final String action,
                                 byte[] data, String deviceName) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_BYTES, data);
        intent.putExtra(EXTRA_ASIZE, data.length);
        intent.putExtra(EXTRA_DEVICE_NAME, deviceName);
        sendBroadcast(intent);
    }

    /*unused*/
    public class LocalBinder extends Binder {
        BLEReadService getService() {
            return BLEReadService.this;
        }
    }

    /*unused*/
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /*unused*/
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /*
     * Initializes Services needed for BLE connections
     * Bluetoothmanager is a service offered by android to handle Bluetooth
     * independent from actual device having Bluetooth
     * If Bluetooth is available on actual device maybe checked with Bluetoothadapter
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }


    /**
     * Connects to the GATT server hosted on the BLE device.
     *
     * @param address The devices address
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Only necessary for pull request an BLE device
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notifications on a given characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(final BluetoothGattCharacteristic characteristic,
                                              final boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        // Not sure if device needs all three delays to establish a connection
        // not quite sur sure
        //TODO: Further examination which delays are indispensable
        //TODO: Use different time period in this place
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.setCharacteristicNotification(characteristic,enabled);
            }
        }, SCAN_PERIOD);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTOR_UUID);
        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }, SCAN_PERIOD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }, SCAN_PERIOD);

    }
}
