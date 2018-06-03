package edu.teco.smartaqnet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import edu.teco.smartaqnet.buffering.ObjectQueue;
import edu.teco.smartaqnet.buffering.QueueFile;
import edu.teco.smartaqnet.buffering.SmartAQDataQueue;

import static edu.teco.smartaqnet.SetMainView.*;

public class MainActivity extends AppCompatActivity {

    //Activate Button that controls the BLE Connection
    BLEConnectionButton controlBLEButton;
    BLEDevicesScanner bleHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*Set first view of app and activate Button that controls BLE Connection*/
        controlBLEButton = findViewById(R.id.controlBLEButton);
        setView(views.startScan, this, controlBLEButton);
        //Activate Bluetooth
        bleHandler = new BLEDevicesScanner(this, controlBLEButton);
        controlBLEButton.setClickListener(bleHandler);
    }


    //TODO: Control-Button Ablauf
    //TODO: Bluetooth-Verbindung aufbauen
    //TODO: Bluetooth-Services identifizieren max. 4
    //TODO: RSSI messen
    //TODO: Datenübertragung aktivieren
    //TODO: Datenübertragung puffern
    //TODO: Buttons für Services aktivieren
    //TODO: Services Dummy Page
    //TODO: GPS-Verbindung
    //TODO: GPS-Track
    //TODO: Settingspage
    //TODO: WLAN konnektieren, Verbindungsabbruch verarbeiten
    //TODO: Mobilen Dienst konnektieren, Verbindungsabbruch verarbeiten
    //TODO: Datenaufereitung für senden
    //TODO: MQTT

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
