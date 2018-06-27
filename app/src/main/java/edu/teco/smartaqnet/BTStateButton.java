package edu.teco.smartaqnet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import edu.teco.smartaqnet.bluetooth.BTDetect;

/**
 * Button to control the view of the different ble connection states
 * First state after creation is SCAN.
 *
 * States are
 * SCAN: No connection -> Start scanning
 * STOP: Scanning -> Stop scanning
 * DISCONNECT: Device is connected -> disconnect
 * as more then one device can be found connection is done with other elements
 * Designed as Singleton as only one instance is needed
 *
 */
public class BTStateButton extends android.support.v7.widget.AppCompatButton{

    private static BTStateButton btStateButton;

    public enum States {
        /**
         * Ready to start scanning
         */
        SCAN,
        /**
         * Stop scanning
         */
        STOP,
        /**
         * Disconnect from device
         */
        DISCONNECT}

    private States stateTriggered;

    /**
     *  Constructor
     *
     * @param context the context
     */
    private BTStateButton(Context context){
        super(context);
        stateTriggered = States.SCAN;
    }

    /**
     * Default constructor needed for superclass
     *
     * @param context    the context
     * @param attributes the attributes
     */
    private BTStateButton(Context context, AttributeSet attributes){
        super(context,attributes);
        stateTriggered = States.SCAN;
    }

    /**
     * Default constructor needed for superclass
     *
     * @param context      the context
     * @param attributes   the attributes
     * @param defStyleAttr the def style attr
     */
    private BTStateButton(Context context, AttributeSet attributes, int defStyleAttr){
        super(context, attributes,defStyleAttr);
        stateTriggered = States.SCAN;
    }

    /**
     *  Method to get the singleton instance of a BTStatebutton
     *
     * @param context the context
     */
    public static BTStateButton getInstance(Context context){
        if (btStateButton == null) {
            BTStateButton.btStateButton = new BTStateButton(context);
        }
        return BTStateButton.btStateButton;
    }

    /**
     * Set state.
     *
     * @param stateTriggered
     */
    public void setState(States stateTriggered){
        this.stateTriggered = stateTriggered;
    }

    /**
     * Connects appropriate method with click listener according to state
     *
     * @param bleHandler
     */
    public void setClickListener(final BTDetect bleHandler) {
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switch (stateTriggered){
                    case SCAN:
                        //Scan Button pressed
                        bleHandler.startDetectingDevices();
                        break;
                    case STOP:
                        //Stop Button pressed
                        bleHandler.stopDetectingDevices();
                        break;
                    case DISCONNECT:
                        //Disconnect Button pressed
                        //TODO: wie disconnect
                        bleHandler.disconnectDevice();
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
