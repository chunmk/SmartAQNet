package edu.teco.smartaqnet;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Utility class to adjust screen to available options depending on state of BLE connection
 */
public class SetMainView {

    /**
     * The views represent the different states and thus the available options of the application
     */
    public enum views {
        /**
         * Ready to scan for devices
         */
        startScan,
        /**
         * Scanning for devices
         */
        scanning,
        /**
         * Devices found are shown
         */
        devicesFound,
        /**
         * Shows received data from device as graph or numbers
         */
        connected,
        /**
         * Start rescan or stop looking for devices
         */
        noDevicesFound}

    /**
     * Set visible elments in view depending on BLE connection state
     *
     * @param view                Actaul screen
     * @param mainActivity        Main activity
     * @param bleConnectionButton Button to start next available option
     */

    static public void setView(views view, Activity mainActivity, BTStateButton bleConnectionButton){
        Button device1_button = mainActivity.findViewById(R.id.device1_button);
        Button device2_button = mainActivity.findViewById(R.id.device2_button);
        Button device3_button = mainActivity.findViewById(R.id.device3_button);
        ImageButton imageButton = mainActivity.findViewById(R.id.imageButton);
        ProgressBar progressBar = mainActivity.findViewById(R.id.progressBar);
        TextView deviceValueText =  mainActivity.findViewById(R.id.deviceValueText);
        switch (view){
            case startScan:
                //Everything is invisible
                device1_button.setVisibility(View.INVISIBLE);
                device2_button.setVisibility(View.INVISIBLE);
                device3_button.setVisibility(View.INVISIBLE);
                device1_button.setEnabled(false);
                device2_button.setEnabled(false);
                device3_button.setEnabled(false);
                imageButton.setEnabled(false);
                imageButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                deviceValueText.setText("Press Button");
                deviceValueText.setVisibility(View.VISIBLE);
                bleConnectionButton.setText("Scan for Devices");
                bleConnectionButton.setState(BTStateButton.States.SCAN);
                break;
            case scanning:
                //Everything invisible except progress bar
                device1_button.setVisibility(View.INVISIBLE);
                device2_button.setVisibility(View.INVISIBLE);
                device3_button.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                deviceValueText.setVisibility(View.INVISIBLE);
                imageButton.setEnabled(false);
                imageButton.setVisibility(View.INVISIBLE);
                bleConnectionButton.setText("Stop scanning");
                bleConnectionButton.setState(BTStateButton.States.STOP);
                break;
            case devicesFound:
                //Everything invisible except Buttons to choose form detected devices
                device1_button.setVisibility(View.VISIBLE);
                device2_button.setVisibility(View.VISIBLE);
                device3_button.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                deviceValueText.setVisibility(View.INVISIBLE);
                imageButton.setEnabled(false);
                imageButton.setVisibility(View.INVISIBLE);
                bleConnectionButton.setText("Scan for Devices");
                bleConnectionButton.setState(BTStateButton.States.SCAN);
                break;
            case connected:
                //Everything invisible except deviceValueText with received data
                device1_button.setVisibility(View.INVISIBLE);
                device2_button.setVisibility(View.INVISIBLE);
                device3_button.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                imageButton.setEnabled(true);
                imageButton.setVisibility(View.VISIBLE);
                deviceValueText.setText("Getting Data");
                deviceValueText.setVisibility(View.VISIBLE);
                bleConnectionButton.setText("Disconnect");
                bleConnectionButton.setState(BTStateButton.States.DISCONNECT);
                break;
            case noDevicesFound:
                //Everything invisible except deviceValueText showing "No devices found"
                device1_button.setVisibility(View.INVISIBLE);
                device2_button.setVisibility(View.INVISIBLE);
                device3_button.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                deviceValueText.setVisibility(View.VISIBLE);
                imageButton.setEnabled(false);
                imageButton.setVisibility(View.INVISIBLE);
                deviceValueText.setText("No Devices found");
                bleConnectionButton.setText("SCAN");
                bleConnectionButton.setState(BTStateButton.States.SCAN);
                break;
            default:
                break;
        }
    }
}
