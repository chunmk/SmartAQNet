package edu.teco.smartaqnet;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SetMainView {

    public enum views {startScan, scanning, devicesFound, connected, noDevicesFound}

    /*
    Choose which elements are visible in Main activity
    controlBLEButton is always visible
     */
    static public void setView(views view, Activity mainActivity, BTStateButton bleConnectionButton){
        Button device1_button = mainActivity.findViewById(R.id.device1_button);
        Button device2_button = mainActivity.findViewById(R.id.device2_button);
        Button device3_button = mainActivity.findViewById(R.id.device3_button);
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
                progressBar.setVisibility(View.INVISIBLE);
                deviceValueText.setText("Press Button");
                deviceValueText.setVisibility(View.VISIBLE);
                //Text on controlBLEButton "Scan for Devices"
                bleConnectionButton.setText("Scan for Devices");
                bleConnectionButton.setState(BTStateButton.States.SCAN);
                break;
            case scanning:
                //Everything invisible progress bar
                device1_button.setVisibility(View.INVISIBLE);
                device2_button.setVisibility(View.INVISIBLE);
                device3_button.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                deviceValueText.setVisibility(View.INVISIBLE);
                //Text on controlBLEButton "Stop Scanning"
                bleConnectionButton.setText("Stop scanning");
                bleConnectionButton.setState(BTStateButton.States.STOP);
                break;
            case devicesFound:
                //Everything invisible except Buttons to choose device
                device1_button.setVisibility(View.VISIBLE);
                device2_button.setVisibility(View.VISIBLE);
                device3_button.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                deviceValueText.setVisibility(View.INVISIBLE);
                //Text on controlBLEButton "Scan for Devices"
                bleConnectionButton.setText("Scan for Devices");
                bleConnectionButton.setState(BTStateButton.States.SCAN);
                break;
            case connected:
                //Everything invisible except deviceValueText
                device1_button.setVisibility(View.INVISIBLE);
                device2_button.setVisibility(View.INVISIBLE);
                device3_button.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                deviceValueText.setText("Getting Data");
                deviceValueText.setVisibility(View.VISIBLE);
                //Text on controlBLEButton "Disconnet from Device"
                bleConnectionButton.setText("Disconnect");
                bleConnectionButton.setState(BTStateButton.States.DISCONNECT);
                break;
            case noDevicesFound:
                //Everything invisible except deviceValueText
                device1_button.setVisibility(View.INVISIBLE);
                device2_button.setVisibility(View.INVISIBLE);
                device3_button.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                deviceValueText.setVisibility(View.VISIBLE);
                deviceValueText.setText("No Devices found");
                //Text on controlBLEButton "Disconnet from Device"
                bleConnectionButton.setText("SCAN");
                bleConnectionButton.setState(BTStateButton.States.SCAN);
                break;
            default:
                break;
        }
    }
}
