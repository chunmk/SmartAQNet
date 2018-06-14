package edu.teco.smartaqnet.errormessages;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastMessages {

    public static void showToast(Context context, String text) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}
