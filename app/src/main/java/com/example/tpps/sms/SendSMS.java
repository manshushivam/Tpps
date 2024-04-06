package com.example.tpps;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SendSMS {

    private Context context;

    // Constructor to initialize the context
    public SendSMS(Context context) {
        this.context = context;
    }

    public void sendSms(String mobileNo, String message) {
        try {
            // Get the default instance of SmsManager
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mobileNo, null, message, null, null);

            // A Toast to indicate that the message was sent
            Toast.makeText(context, "SMS Sent: "  , Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            // Handle exceptions here
            Toast.makeText(context, "SMS fail" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }
    }
}
