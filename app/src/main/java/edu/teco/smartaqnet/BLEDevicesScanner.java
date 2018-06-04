package edu.teco.smartaqnet;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.teco.smartaqnet.buffering.ObjectQueue;
import edu.teco.smartaqnet.buffering.SmartAQDataQueue;

import static edu.teco.smartaqnet.SetMainView.*;

class BLEDevicesScanner {

    private final static String TAG = BLEDevicesScanner.class.getSimpleName();

    private final int MAXDEVICES = 3;
    private BluetoothLeScanner btScanner;

    private final static int REQUEST_ENABLE_BT = 1;

    private Activity mainActivity;
    private BLEConnectionButton bleConnectionButton;
    private int deviceIndex = 0;
    private ArrayList<BluetoothDevice> devicesDiscovered = new ArrayList<>();
    private String bleDeviceAdress;
    private Intent gattServiceIntent;

    // Stops scanning after 30 seconds.
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 10000;

    private ObjectQueue<String> smartAQDataQueue;

    protected BLEDevicesScanner(Context context, BLEConnectionButton bleConnectionButton) {
        this.mainActivity = (Activity) context;
        this.bleConnectionButton = bleConnectionButton;
        BluetoothAdapter btAdapter = ((BluetoothManager) context.getSystemService(android.content.Context.BLUETOOTH_SERVICE)).getAdapter();
        //Check if Bluetooth is supported
        if (btAdapter == null) {
            // Device does not support Bluetooth
            final Activity activity = (Activity) context;
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setMessage("Bluetooth not supported!").setTitle("Bluetooth Connectivity");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    activity.finish();
                }
            });
            alert.show();
        }
        try {
            btScanner = btAdapter.getBluetoothLeScanner();
        } catch (NullPointerException e){
            //TODO: Fehler behandeln
        }
        /*Activate Bluetooth if not active*/
        if (!btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        smartAQDataQueue = (new SmartAQDataQueue(mainActivity.getCacheDir().toString())).getSmartAQDataQueue();

    }

    // Device scan callback.
    private ScanCallback bleScanCallback = new ScanCallback() {
        // Is triggered for every Bluetooth device found
        // Teco Devices will be identified by device name
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (deviceIndex < MAXDEVICES && !devicesDiscovered.contains(result.getDevice())) {
                if(device.getName() != null && device.getName().contains("TECO-AQNode")) {
                    devicesDiscovered.add(result.getDevice());
                    deviceIndex++;
                }
            }
        }

    };

    /*
     * Scanning for AQNet Services
     */
    public void startDetectingDevices() {
        setView(SetMainView.views.scanning, mainActivity, bleConnectionButton);
        deviceIndex = 0;
        devicesDiscovered.clear();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(bleScanCallback);
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopDetectingDevices();
            }
        }, SCAN_PERIOD);
    }

    /*
    Button Stop Scanning pressed or SCAN_Period exhausted
     */
    public void stopDetectingDevices() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(bleScanCallback);
            }
        });
        showScanResults();
    }

    /*
    TODO: Actually activates at maximum three buttons to connect to devices
    Eventually using ExpandableListview is better
     */
    private void showScanResults(){
        if(deviceIndex > 0){
            SetMainView.setView(SetMainView.views.devicesFound, mainActivity, bleConnectionButton);
            setDeviceButtons();
        } else
            SetMainView.setView(SetMainView.views.noDevicesFound, mainActivity, bleConnectionButton);
    }

    private void setDeviceButtons(){
        Button[] deviceButtons = {mainActivity.findViewById(R.id.device1_button),
                                  mainActivity.findViewById(R.id.device2_button),
                                  mainActivity.findViewById(R.id.device3_button)};

        for(int i = 0; i< deviceIndex; i++){
            final int vcount = i;
            deviceButtons[i].setEnabled(true);
            deviceButtons[i].setText(devicesDiscovered.get(i).getName());
            deviceButtons[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    connectToDeviceSelected(vcount);
                }
            });
        }
    }


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        BluetoothLeService mBluetoothLeService;
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");

                mainActivity.finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(bleDeviceAdress);
            mBluetoothLeService.setOutPutDir(mainActivity.getCacheDir().toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    /*
    Starts connection to the selected device as a service,
    defined in BluetoothLEService
     */
    private void connectToDeviceSelected(int deviceSelected){
        bleDeviceAdress = devicesDiscovered.get(deviceSelected).getAddress();
        gattServiceIntent = new Intent(mainActivity, BluetoothLeService.class);
        gattServiceIntent.putExtra("outPutDir", mainActivity.getCacheDir().toString());
        gattServiceIntent.putExtra("bleDeviceAdress", bleDeviceAdress);
        //mainActivity.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mainActivity.startService(gattServiceIntent);
        mainActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        SetMainView.setView(SetMainView.views.connected, mainActivity, bleConnectionButton);
    }

    public void disconnectDevice(){
        mainActivity.stopService(gattServiceIntent);
        SetMainView.setView(SetMainView.views.startScan, mainActivity, bleConnectionButton);
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
            switch(action){
                case BluetoothLeService.ACTION_GATT_CONNECTED:
                    bleConnectionButton.setState(BLEConnectionButton.States.DISCONNECT);
                    break;
                case BluetoothLeService.ACTION_GATT_DISCONNECTED:
                    //TODO: testen ob als Abbruchkriterium nutzbar
                    SetMainView.setView(SetMainView.views.startScan, mainActivity, bleConnectionButton);
                    break;
                case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED:
                    //Not used
                    break;
                case BluetoothLeService.ACTION_DATA_AVAILABLE:
                    //TODO: Daten anzeigen
                    String result = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                    ((TextView) mainActivity.findViewById(R.id.deviceValueText)).setText(result);

//                    try {
//                        smartAQDataQueue.add(result);
//                    } catch (Exception e){
//                        //TODO IOException reading data from smartAQDataQueue behandeln
//                        e.printStackTrace();
//                    }
//                    try {
//                        smartAQDataQueue.remove();
//                    } catch (Exception e){
//                        //TODO IOException removing data to smartAQDataQueue behandeln
//                        e.printStackTrace();
//                    }

                    break;
                default:
                    //TODO Handle default

            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}