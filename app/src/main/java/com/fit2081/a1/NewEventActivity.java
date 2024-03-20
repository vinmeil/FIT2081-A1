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
    EditText etEventId, etEventName, etCategoryId, etTicketsAvailable;
    Switch isEventActive;
    String[] splitMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event_category);

        etEventId = findViewById(R.id.editTextEventId);
        etEventName = findViewById(R.id.editTextEventName);
        etCategoryId = findViewById(R.id.editTextCategoryId);
        etTicketsAvailable = findViewById(R.id.editTextTicketsAvailable);
        isEventActive = findViewById(R.id.switchEventIsActive);

        etCategoryId.setFocusable(false);

        EventCategoryBroadCastReceiver eventCategoryBroadCastReceiver = new EventCategoryBroadCastReceiver();
        registerReceiver(eventCategoryBroadCastReceiver, new IntentFilter(SMSReceiver.SMS_FILTER), RECEIVER_EXPORTED);
    }

    public void onCreateNewEventButtonClick(View view) {
        String categoryId = etCategoryId.getText().toString().isEmpty() ? generateEventId() : etCategoryId.getText().toString();
        String[] temporaryMessage = new String[4];
        temporaryMessage[0] = etEventName.getText().toString();
        temporaryMessage[1] = etCategoryId.getText().toString();
        temporaryMessage[2] = etTicketsAvailable.getText().toString();
        temporaryMessage[3] = String.valueOf(isEventActive.isChecked());
        boolean isValid = checkValidMessage(temporaryMessage);

        if (isValid) {
            splitMessage = temporaryMessage;
            saveDataToSharedPreference(categoryId, splitMessage);

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
        String categoryId = "E";
        for (int i = 0; i < 2; ++i) {
            categoryId += (char)('A' + (int)(Math.random() * 26));
        }

        categoryId += "-";
        for (int i = 0; i < 5; ++i) {
            categoryId += (char)('0' + (int)(Math.random() * 10));
        }

        return categoryId;
    }

    private boolean checkValidMessage(String[] splitMessage) {
        boolean isValid = true;
        if (splitMessage.length != 4) {
            isValid = false;
        } else {
            if (splitMessage[0].isEmpty() || splitMessage[1].isEmpty()) {
                isValid = false;
            }

            if (!splitMessage[2].isEmpty()) {
                try {
                    int ticketsAvailable = Integer.parseInt(splitMessage[1]);

                    if (ticketsAvailable <= 0) {
                        isValid = false;
                    }
                } catch (Exception e) {
                    isValid = false;
                }
            }

            if (!splitMessage[3].isEmpty() && !splitMessage[2].equalsIgnoreCase("true") && !splitMessage[2].equalsIgnoreCase("false")) {
                isValid = false;
            }
        }

        return isValid;
    }

    public class EventCategoryBroadCastReceiver extends BroadcastReceiver {
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
                etCategoryId.setText(splitMessage[1]);
                etTicketsAvailable.setText(splitMessage[2]);
                isEventActive.setChecked(Boolean.parseBoolean(splitMessage[2]));
            } else {
                Toast.makeText(context, "Invalid message format!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}