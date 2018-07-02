package edu.teco.smartaqnet.errormessages;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;


/**
 * Not yet in use, could be used to show error messages
 */
public class ToastMessages {

    /**
     * Shows error message text
     *
     * @param context the context
     * @param text    the text
     */
    public static void showToast(Context context, String text) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}
