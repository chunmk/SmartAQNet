package edu.teco.smartaqnet;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.UUID;

import edu.teco.smartaqnet.buffering.ObjectQueue;
import edu.teco.smartaqnet.buffering.SmartAQDataQueue;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED;


public class BLEConnectedDeviceController {

    private Activity mainActivity;
    private BLEConnectionButton bleConButton;
    private BluetoothGatt bluetoothGatt;

    private final static UUID SMARTAQ_SERVICE_UUID =
            UUID.fromString("6e57fcf9-8064-4995-a3a8-e5ca44552192");
    private final static UUID CHARACTERISTIC_UUID =
            UUID.fromString("7a812f99-06fa-4d89-819d-98e9aafbd4ef");
    private final static UUID DESCRIPTOR_UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private ObjectQueue<String> smartAQDataQueue;



    public BLEConnectedDeviceController(Context context, BLEConnectionButton bleConnectionButton){
        bleConButton = bleConnectionButton;
        mainActivity = (Activity) context;
        //Initialize persistant FIFO-Buffer for notifications from BLE
    }

    // Stops scanning after 5 seconds.
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 5000;

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt){
        this.bluetoothGatt = bluetoothGatt;
    }
    // These are the callbacks used on the connected BLE device
    public final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        /* When Notifications are enabled on BLE device, this method is triggered
        /*   every time the Characteristic is updated
         */
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation
            mainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    String res = characteristic.getStringValue(0);
                    String result ="";
                    try {
                        smartAQDataQueue.add(res);
                    } catch (Exception e) {
                        //TODO IOException adding data to smartAQDataQueue behandeln
                        e.printStackTrace();
                    }
                    try {
                        result = smartAQDataQueue.peek();
                    } catch (Exception e){
                        //TODO IOException reading data from smartAQDataQueue behandeln
                        e.printStackTrace();
                    }
                    ((TextView) mainActivity.findViewById(R.id.deviceValueText)).setText(result);
                    try {
                        smartAQDataQueue.remove();
                    } catch (Exception e){
                        //TODO IOException removing data to smartAQDataQueue behandeln
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            System.out.println(newState);
            switch (newState) {
                case STATE_DISCONNECTED:
                    mainActivity.runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                    break;
                case STATE_CONNECTED:
                    mainActivity.runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });

                    // discover services and characteristics for this device
                    bluetoothGatt.discoverServices();

                    break;
                default:
                    mainActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) mainActivity.findViewById(R.id.deviceValueText)).setText("we encounterned an unknown state, uh oh\n");
                            (mainActivity.findViewById(R.id.deviceValueText)).setVisibility(View.VISIBLE);
                        }
                    });
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            setNotifySensor();
        }

        private void setNotifySensor(){
            BluetoothGattService service = bluetoothGatt.getService(SMARTAQ_SERVICE_UUID);
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
            BluetoothGattDescriptor desc = characteristic.getDescriptor(DESCRIPTOR_UUID);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothGatt.setCharacteristicNotification(bluetoothGatt.getService(SMARTAQ_SERVICE_UUID)
                            .getCharacteristic(CHARACTERISTIC_UUID),true);
                }
            }, SCAN_PERIOD);
            desc.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(desc);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothGatt.writeDescriptor(bluetoothGatt.getService(SMARTAQ_SERVICE_UUID)
                            .getCharacteristic(CHARACTERISTIC_UUID).getDescriptor(DESCRIPTOR_UUID));
                }
            }, SCAN_PERIOD);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(desc);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothGatt.writeDescriptor(bluetoothGatt.getService(SMARTAQ_SERVICE_UUID)
                            .getCharacteristic(CHARACTERISTIC_UUID).getDescriptor(DESCRIPTOR_UUID));
                }
            }, SCAN_PERIOD);
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        private void broadcastUpdate(final String action,
                                     final BluetoothGattCharacteristic characteristic) {

            System.out.println(characteristic.getUuid());
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
        }
    };

    public void disconnectDevice() {
        SetMainView.setView(SetMainView.views.startScan, mainActivity, bleConButton);
        bluetoothGatt.disconnect();
    }

    public void setDisconnect(){
        bleConButton.setState(BLEConnectionButton.States.DISCONNECT);
    }
}
