package edu.teco.smartaqnet;

import android.app.Activity;
import android.app.Application;

public class ApplicationGlobal extends Application{

    private Activity mainActivity;

    public void setMainActivity(Activity mainActivity){
        this.mainActivity = mainActivity;
    }

    public Activity getMainActivity() {
        return mainActivity;
    }
}
