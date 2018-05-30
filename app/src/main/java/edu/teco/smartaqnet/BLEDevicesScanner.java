package edu.teco.smartaqnet;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import static edu.teco.smartaqnet.SetMainView.*;

class BLEDevicesScanner {

    private final int MAXDEVICES = 3;

    private BluetoothLeScanner btScanner;

    private final static int REQUEST_ENABLE_BT = 1;

    private BLEConnectedDeviceController deviceController;

    private Context context;
    private BLEConnectionButton bleConnectionButton;
    private int deviceIndex = 0;
    private ArrayList<BluetoothDevice> devicesDiscovered = new ArrayList<>();
    // Stops scanning after 30 seconds.
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 5000;


    protected BLEDevicesScanner(Context context, BLEConnectionButton bleConnectionButton) {
        this.context = context;
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
            //TODO: Fehler behandel
        }
        /*Activate Bluetooth if not active*/
        if (!btAdapter.isEnabled())
            activateBT(context);
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
        setView(SetMainView.views.scanning,(Activity) context, bleConnectionButton);
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

    private void showScanResults(){
        if(deviceIndex > 0){
            SetMainView.setView(SetMainView.views.devicesFound, (Activity) context, bleConnectionButton);
            setDeviceButtons();
        } else
            SetMainView.setView(SetMainView.views.noDevicesFound, (Activity) context, bleConnectionButton);
    }

    private void setDeviceButtons(){
        Activity mainActivity = (Activity) context;
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


//    private void connectToDeviceSelected(int deviceSelected){
//        deviceController = new BLEConnectedDeviceController(context);
//        deviceController.setBluetoothGatt(devicesDiscovered.get(deviceSelected).connectGatt(context, false, deviceController.btleGattCallback));
//        //TODO: Checken ob Verbindung steht
//        deviceController.setDisconnect();
//        SetMainView.setView(SetMainView.views.connected);
//    }


    private void connectToDeviceSelected(int deviceSelected){
        deviceController = new BLEConnectedDeviceController(context, bleConnectionButton);
        deviceController.setBluetoothGatt(devicesDiscovered.get(deviceSelected).connectGatt(context, false, deviceController.btleGattCallback));
        //TODO: Checken ob Verbindung steht
        deviceController.setDisconnect();
        SetMainView.setView(SetMainView.views.connected, (Activity) context, bleConnectionButton);
    }

    public void disconnectDevice(){
        deviceController.disconnectDevice();
    }

    private void activateBT(Context context) {
        // Bluetooth is not enable :)
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity) context).startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

}