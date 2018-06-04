package edu.teco.smartaqnet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class BTStateButton extends android.support.v7.widget.AppCompatButton{
    public enum States {SCAN, STOP, DISCONNECT}
    private States stateTriggered;

    public BTStateButton(Context context){
        super(context);
        stateTriggered = States.SCAN;
    }

    public BTStateButton(Context context, AttributeSet attributes){
        super(context,attributes);
        stateTriggered = States.SCAN;
    }

    public BTStateButton(Context context, AttributeSet attributes, int defStyleAttr){
        super(context, attributes,defStyleAttr);
        stateTriggered = States.SCAN;
    }

    public void setState(States stateTriggered){
        this.stateTriggered = stateTriggered;
    }

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