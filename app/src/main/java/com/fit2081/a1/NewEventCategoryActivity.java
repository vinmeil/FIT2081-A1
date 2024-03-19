package com.fit2081.a1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class NewEventCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event_category);

        EventCategoryBroadCastReceiver eventCategoryBroadCastReceiver = new EventCategoryBroadCastReceiver();
        registerReceiver(eventCategoryBroadCastReceiver, new IntentFilter(SMSReceiver.SMS_FILTER), RECEIVER_EXPORTED);
    }

    public class EventCategoryBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(SMSReceiver.SMS_MSG_KEY);
            String[] splitMessage = message.split(";");
        }

        private boolean isMessageValid(String[] splitMessage) {
            boolean isValid = true;
            if (splitMessage.length != 3) {
                isValid = false;
            } else {
                try {
                    Integer.parseInt(splitMessage[2]);
                } catch (NumberFormatException e) {
                    isValid = false;
                }
            }

            return isValid;
        }
    }
}