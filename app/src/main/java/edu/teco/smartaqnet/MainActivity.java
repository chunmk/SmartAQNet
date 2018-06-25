package edu.teco.smartaqnet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import edu.teco.smartaqnet.bluetooth.BTDetect;
import edu.teco.smartaqnet.dataprocessing.SmartAQDataService;

import static edu.teco.smartaqnet.SetMainView.*;

/**
 * SmartAQNet is a tool that connects to dust sensors via BLE, used for supervising air quality(TODO: Sensor type),
 * therefore collecting measurement data and sending them to a Frost Server (https://github.com/FraunhoferIOSB/FROST-Server)
 * using REST (https://de.wikipedia.org/wiki/Representational_State_Transfer).
 * The API used to communicate with sensors is described in (https://github.com/FraunhoferIOSB/FROST-Server)
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Button to control the BLE connection via states described in BTStateButton class
     */
    BTStateButton btStateButton;
    /**
     * The Ble handler.
     */
    BTDetect bleHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Starting data processing service
        Intent smartAQDataService = new Intent(this, SmartAQDataService.class);
        startService(smartAQDataService);
        /*Set first view of app and activate Button that controls BLE Connection*/
        btStateButton = findViewById(R.id.btStateButton);
        setView(views.startScan, this, btStateButton);
        //Activate Bluetooth
        bleHandler = new BTDetect(this, btStateButton);
        btStateButton.setClickListener(bleHandler);
    }


    //TODO: Verbindungsabbruch BLE checken
    //TODO: Settingspage
    //TODO: Settingspage clear data smartAQData.clear()
    //TODO: GPS-Verbindung
    //TODO: Datenaufbereitung für senden
    //TODO: MQTT
    //TODO: Clear Data Button
    //TODO: Ablauf Http, Success checken -> Daten aus FIFO entfernen(dann mehr Daten gleichzeitig immer x 2?), kein Success -> Datastream neu erzeugen, Anzahl gleichzeitig reset auf 1
    //TODO: Besser prüfen ob Sensorthings vorhanden sind statt Fehler zu ignorieren
    //TODO: Alert Message für fehlendes GPS führt zum Absturz



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

    /**
     * Send message.
     *
     * @param view the view
     */
//Opens Graph view
    public void sendMessage(View view)
    {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }
}

