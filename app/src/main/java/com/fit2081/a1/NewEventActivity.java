package com.fit2081.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class NewEventActivity extends AppCompatActivity {
    EditText etEventId, etEventName, etEventCategoryId, etTicketsAvailable;
    Switch isEventActive;
    String[] splitMessage;
    EventBroadCastReceiver eventBroadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        etEventId = findViewById(R.id.editTextEventId);
        etEventName = findViewById(R.id.editTextEventName);
        etEventCategoryId = findViewById(R.id.editTextEventCategoryId);
        etTicketsAvailable = findViewById(R.id.editTextTicketsAvailable);
        isEventActive = findViewById(R.id.switchEventIsActive);

        etEventId.setFocusable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBroadCastReceiver = new EventBroadCastReceiver();
        registerReceiver(eventBroadCastReceiver, new IntentFilter(SMSReceiver.SMS_FILTER), RECEIVER_EXPORTED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(eventBroadCastReceiver);
    }

    public void onCreateNewEventButtonClick(View view) {
        String eventId = etEventId.getText().toString().isEmpty() ? generateEventId() : etEventId.getText().toString();
        String[] temporaryMessage = new String[4];
        temporaryMessage[0] = etEventName.getText().toString();
        temporaryMessage[1] = etEventCategoryId.getText().toString();
        temporaryMessage[2] = etTicketsAvailable.getText().toString();
        temporaryMessage[3] = String.valueOf(isEventActive.isChecked());
        boolean isValid = checkValidMessage(temporaryMessage);

        if (isValid) {
            splitMessage = temporaryMessage;
            saveDataToSharedPreference(eventId, splitMessage);
            String toastMessage = String.format("Event saved: %s to %s.", eventId, splitMessage[1]);
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Invalid inputs in fields!", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveDataToSharedPreference(String eventId, String[] messageDetails) {
        SharedPreferences sharedPreferences = getSharedPreferences(KeyStore.FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KeyStore.KEY_EVENT_ID, eventId);
        editor.putString(KeyStore.KEY_EVENT_NAME, messageDetails[0]);
        editor.putString(KeyStore.KEY_EVENT_CATEGORY_ID, messageDetails[1]);

        if (!messageDetails[2].isEmpty()) {
            int ticketsAvailable = Integer.parseInt(messageDetails[2]);
            editor.putInt(KeyStore.KEY_EVENT_TICKETS_AVAILABLE, ticketsAvailable);
        }

        if (!messageDetails[3].isEmpty()) {
            boolean isEventActive = Boolean.parseBoolean(messageDetails[3]);
            editor.putBoolean(KeyStore.KEY_IS_EVENT_ACTIVE, isEventActive);
        }

        editor.apply();
    }


    private String generateEventId() {
        String eventId = "E";
        for (int i = 0; i < 2; ++i) {
            eventId += (char)('A' + (int)(Math.random() * 26));
        }

        eventId += "-";
        for (int i = 0; i < 5; ++i) {
            eventId += (char)('0' + (int)(Math.random() * 10));
        }

        return eventId;
    }

    private boolean checkValidMessage(String[] splitMessage) {
        boolean isValid = true;
        if (splitMessage.length != 4) {
            isValid = false;
        } else {
            String eventName = splitMessage[0];
            String eventCategoryId = splitMessage[1];
            String ticketsAvailable = splitMessage[2];
            String isEventActive = splitMessage[3];
            if (eventName.isEmpty() || eventCategoryId.isEmpty()) {
                isValid = false;
            }

            if (!ticketsAvailable.isEmpty()) {
                try {
                    int ticketsAvailableInt = Integer.parseInt(splitMessage[2]);

                    if (ticketsAvailableInt <= 0) {
                        isValid = false;
                    }
                } catch (Exception e) {
                    isValid = false;
                }
            }

            if (!isEventActive.isEmpty() && !isEventActive.equalsIgnoreCase("true") && !isEventActive.equalsIgnoreCase("false")) {
                isValid = false;
            }
        }

        return isValid;
    }

    public class EventBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(SMSReceiver.SMS_MSG_KEY);
            String[] identifier = message.split(":");
            boolean isCorrectIdentifier = true;

            if (!identifier[0].equals("event")) {
                isCorrectIdentifier = false;
            }

            boolean isMessageValid;
            if (isCorrectIdentifier) {
                splitMessage = identifier[1].split(";", -1);;
                isMessageValid = checkValidMessage(splitMessage);
            } else {
                isMessageValid = false;
            }

            if (isMessageValid) {
                String eventId = generateEventId();
                etEventId.setText(eventId);
                etEventName.setText(splitMessage[0]);
                etEventCategoryId.setText(splitMessage[1]);
                etTicketsAvailable.setText(splitMessage[2]);
                isEventActive.setChecked(Boolean.parseBoolean(splitMessage[3]));
            } else {
                Toast.makeText(context, "Invalid message format!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}