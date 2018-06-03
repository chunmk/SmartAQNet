package edu.teco.smartaqnet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class BLEConnectionButton extends android.support.v7.widget.AppCompatButton{
    public enum States {SCAN, STOP, DISCONNECT}
    private States stateTriggered;

    public BLEConnectionButton(Context context){
        super(context);
        stateTriggered = States.SCAN;
    }

    public BLEConnectionButton(Context context, AttributeSet attributes){
        super(context,attributes);
        stateTriggered = States.SCAN;
    }

    public BLEConnectionButton(Context context, AttributeSet attributes, int defStyleAttr){
        super(context, attributes,defStyleAttr);
        stateTriggered = States.SCAN;
    }

    public void setState(States stateTriggered){
        this.stateTriggered = stateTriggered;
    }

    public void setClickListener(final BLEDevicesScanner bleHandler) {
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
                        bleHandler.disconnectDevice();
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
