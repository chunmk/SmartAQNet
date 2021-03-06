package edu.teco.smartaqnet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import edu.teco.smartaqnet.bluetooth.BLEReadService;
import edu.teco.smartaqnet.dataprocessing.ObjectByteConverterUtility;
import edu.teco.smartaqnet.dataprocessing.SmartAQDataObject;



/**
 * Graph and Chart Library from https://github.com/jjoe64/GraphView
 *
 */
public class GraphActivity extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series;
    private Double graphData;
    private int lastX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        // we get graph view instance
        GraphView graph = (GraphView) findViewById(R.id.graph);
        GridLabelRenderer glr = graph.getGridLabelRenderer();
        glr.setPadding(128);
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setScrollable(true);
        // activate horizontal zooming and scrolling
        viewport.setScalable(true);
        // activate horizontal and vertical zooming and scrolling
        viewport.setScalableY(true);
        // activate vertical scrolling
        viewport.setScrollableY(true);
        graphData = 0D;
        registerReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                //TODO: Can be changed to show continuously data point instead of 5000
                for (int i = 0; i < 5000; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }


    /**
     *  Add data to graph
     */
    private void addEntry() {
        // here, we choose to display max 30 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, graphData), true, 10);
    }

    /**
     * Initializes callback to receive data from BLE device
     */
    private final BroadcastReceiver smartAQUpdateREceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            String url = "";
            switch(action){
                case BLEReadService.ACTION_DATA_AVAILABLE:
                    byte[] bytes = intent.getByteArrayExtra(BLEReadService.EXTRA_BYTES);
                    SmartAQDataObject smartAQData = (SmartAQDataObject) ObjectByteConverterUtility.convertFromByte(bytes);
                    graphData = Double.parseDouble(smartAQData.getBleDustData());
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * Registers receiver to get data defined by smartAQUpdateIntentFilter() in actual context
     *
     */
    private void registerReceiver(){
        getApplicationContext().registerReceiver(smartAQUpdateREceiver, smartAQUpdateIntentFilter());
    }

    /**
     * Defines which data will be received
     */
    private static IntentFilter smartAQUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEReadService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}


