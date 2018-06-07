package edu.teco.smartaqnet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import edu.teco.smartaqnet.bluetooth.BTDetect;
import edu.teco.smartaqnet.dataprocessing.SmartAQDataService;

import static edu.teco.smartaqnet.SetMainView.*;

public class MainActivity extends AppCompatActivity {

    //Activate Button that controls the BLE Connection
    BTStateButton btStateButton;
    BTDetect bleHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*Set first view of app and activate Button that controls BLE Connection*/
        btStateButton = findViewById(R.id.btStateButton);
        setView(views.startScan, this, btStateButton);
        //Activate Bluetooth
        bleHandler = new BTDetect(this, btStateButton);
        btStateButton.setClickListener(bleHandler);
        //Starting data processing service
        Intent smartAQDataService = new Intent(this, SmartAQDataService.class);
        startService(smartAQDataService);



    }


    //TODO: Disconnect hat noch einen Fehler
    //TODO: Verbindungsabbruch BLE checken
    //TODO: Settingspage
    //TODO: Settingspage clear data smartAQData.clear()
    //TODO: GPS-Verbindung
    //TODO: GPS-Trace
    //TODO: WLAN konnektieren, Verbindungsabbruch verarbeiten
    //TODO: Mobilen Dienst konnektieren, Verbindungsabbruch verarbeiten
    //TODO: Datenaufbereitung für senden
    //TODO: MQTT
    //TODO: Clear Data Button

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
