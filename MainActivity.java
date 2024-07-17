package com.example.bluetometa_device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String DEVICE_ADDRESS = "00:23:10:00:50:E1"; // Replace with your Bluetooth module address
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 1;

    private TextView statusTextView;
    private Button roomButton;
    private Button kitchenButton;
    private Button connectButton;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    public static ConnectedThread connectedThread;

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ConnectedThread.MESSAGE_READ:
                    String readMessage = (String) msg.obj;
                    // Handle the received message
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        connectButton = findViewById(R.id.connectButton);
        roomButton = findViewById(R.id.roomButton);
        kitchenButton = findViewById(R.id.kitchenButton);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Register for Bluetooth state change broadcasts
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter);

        updateButtonStates();

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToBluetooth();
            }
        });

        roomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send token 'R' for Room
                if (connectedThread != null) {
                    connectedThread.write("R");
                }
                // Navigate to RoomActivity
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                startActivity(intent);
            }
        });

        kitchenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send token 'K' for Kitchen
                if (connectedThread != null) {
                    connectedThread.write("K");
                }
                // Navigate to KitchenActivity
                Intent intent = new Intent(MainActivity.this, KitchenActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateButtonStates() {
        if (bluetoothAdapter.isEnabled()) {
            roomButton.setEnabled(true);
            kitchenButton.setEnabled(true);
            connectButton.setEnabled(true);
            statusTextView.setText("Status: Bluetooth is on");
        } else {
            roomButton.setEnabled(false);
            kitchenButton.setEnabled(false);
            connectButton.setEnabled(false);
            statusTextView.setText("Status: Bluetooth is off. Please turn on Bluetooth.");
        }
    }

    private void connectToBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please turn on Bluetooth first", Toast.LENGTH_SHORT).show();
            return;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                return;
            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            connectedThread = new ConnectedThread(bluetoothSocket, handler);
            connectedThread.start();
            statusTextView.setText("Status: Connected");
            updateButtonStates();
        } catch (IOException e) {
            statusTextView.setText("Status: Connection Failed");
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_CONNECT_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                connectToBluetooth();
            } else {
                Toast.makeText(this, "Bluetooth connect permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON || state == BluetoothAdapter.STATE_OFF) {
                    updateButtonStates();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothStateReceiver);
        if (connectedThread != null) {
            connectedThread.cancel();
        }
    }
}
