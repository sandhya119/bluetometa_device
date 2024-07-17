package com.example.bluetometa_device;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class KitchenActivity extends AppCompatActivity {

    private Button offBuzzerButton;
    private boolean isGasDetected = false; // This should be updated based on your gas sensor input

    private static final String CHANNEL_ID = "GasAlertChannel";
    private static final int NOTIFICATION_PERMISSION_CODE = 100;

    private ConnectedThread connectedThread;

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ConnectedThread.MESSAGE_READ:
                    String readMessage = (String) msg.obj;
                    if (readMessage.equals("GAS_DETECTED")) {
                        isGasDetected = true;
                    } else if (readMessage.equals("GAS_CLEARED")) {
                        isGasDetected = false;
                    }
                    updateBuzzerStatus();
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        offBuzzerButton = findViewById(R.id.offBuzzerButton);

        offBuzzerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGasDetected) {
                    isGasDetected = false;
                    updateBuzzerStatus();
                    if (connectedThread != null) {
                        connectedThread.write("BUZZER_OFF");
                    }
                }
            }
        });

        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_NOTIFICATION_POLICY}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    private void updateBuzzerStatus() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isGasDetected) {
                    offBuzzerButton.setEnabled(true);
                    Toast.makeText(KitchenActivity.this, "Gas detected! Buzzer is on.", Toast.LENGTH_SHORT).show();
                    sendGasAlertNotification();
                } else {
                    offBuzzerButton.setEnabled(false);
                    Toast.makeText(KitchenActivity.this, "Buzzer is off.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendGasAlertNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icc_gas_alert)
                .setContentTitle("Gas Alert")
                .setContentText("Gas detected in the kitchen! Please take action.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with operations requiring this permission
            } else {
                Toast.makeText(this, "Notification permission is required to receive gas alerts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Gas Alert Channel";
            String description = "Channel for gas detection alerts";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
