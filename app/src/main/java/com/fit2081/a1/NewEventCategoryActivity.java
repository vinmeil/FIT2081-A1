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

public class NewEventCategoryActivity extends AppCompatActivity {
    EditText etCategoryId, etCategoryName, etCategoryEventCount;
    Switch isCategoryEventActive;
    String[] splitMessage;
    EventCategoryBroadCastReceiver eventCategoryBroadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event_category);

        etCategoryId = findViewById(R.id.editTextCategoryId);
        etCategoryName = findViewById(R.id.editTextCategoryName);
        etCategoryEventCount = findViewById(R.id.editTextCategoryCount);
        isCategoryEventActive = findViewById(R.id.switchCategoryIsActive);

        etCategoryId.setFocusable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventCategoryBroadCastReceiver = new EventCategoryBroadCastReceiver();
        registerReceiver(eventCategoryBroadCastReceiver, new IntentFilter(SMSReceiver.SMS_FILTER), RECEIVER_EXPORTED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(eventCategoryBroadCastReceiver);
    }

    public void onCreateNewCategoryButtonClick(View view) {
        String categoryId = etCategoryId.getText().toString().isEmpty() ? generateCategoryId() : etCategoryId.getText().toString();
        String[] temporaryMessage = new String[3];
        temporaryMessage[0] = etCategoryName.getText().toString();
        temporaryMessage[1] = etCategoryEventCount.getText().toString();
        temporaryMessage[2] = String.valueOf(isCategoryEventActive.isChecked());
        boolean isValid = checkValidMessage(temporaryMessage);

        if (isValid) {
            splitMessage = temporaryMessage;
            saveDataToSharedPreference(categoryId, splitMessage);
            String toastMessage = String.format("Category saved successfully: %s.", categoryId);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Invalid inputs in fields!", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveDataToSharedPreference(String categoryId, String[] messageDetails) {
        SharedPreferences sharedPreferences = getSharedPreferences(KeyStore.FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KeyStore.KEY_CATEGORY_ID, categoryId);
        editor.putString(KeyStore.KEY_CATEGORY_NAME, messageDetails[0]);

        if (!messageDetails[1].isEmpty()) {
            int categoryEventCount = Integer.parseInt(messageDetails[1]);
            editor.putInt(KeyStore.KEY_CATEGORY_EVENT_COUNT, categoryEventCount);
        }

        if (!messageDetails[2].isEmpty()) {
            boolean isCategoryEventActive = Boolean.parseBoolean(messageDetails[2]);
            editor.putBoolean(KeyStore.KEY_IS_CATEGORY_EVENT_ACTIVE, isCategoryEventActive);
        }

        editor.apply();
    }


    private String generateCategoryId() {
        String categoryId = "C";
        for (int i = 0; i < 2; ++i) {
            categoryId += (char)('A' + (int)(Math.random() * 26));
        }

        categoryId += "-";
        for (int i = 0; i < 4; ++i) {
            categoryId += (char)('0' + (int)(Math.random() * 10));
        }

        return categoryId;
    }

    private boolean checkValidMessage(String[] splitMessage) {
        boolean isValid = true;
        if (splitMessage.length != 3) {
            isValid = false;
        } else {
            String categoryName = splitMessage[0];
            String categoryEventCount = splitMessage[1];
            String isCategoryEventActive = splitMessage[2];
            if (categoryName.isEmpty()) {
                isValid = false;
            }

            if (!categoryEventCount.isEmpty()) {
                try {
                    Integer.parseInt(splitMessage[1]);
                } catch (Exception e) {
                    isValid = false;
                }
            }

            if (!isCategoryEventActive.isEmpty() && !isCategoryEventActive.equalsIgnoreCase("true") && !isCategoryEventActive.equalsIgnoreCase("false")) {
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

            if (!identifier[0].equals("category")) {
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
                String categoryId = generateCategoryId();
                etCategoryId.setText(categoryId);
                etCategoryName.setText(splitMessage[0]);
                etCategoryEventCount.setText(splitMessage[1]);
                isCategoryEventActive.setChecked(Boolean.parseBoolean(splitMessage[2]));
            } else {
                Toast.makeText(context, "Invalid message format!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}