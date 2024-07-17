package com.example.bluetometa_device;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private static final String TAG = "ConnectedThread";
    private final BluetoothSocket socket;
    private final InputStream inStream;
    private final OutputStream outStream;
    private final Handler handler;

    // Message types
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error creating connected thread", e);
        }

        inStream = tmpIn;
        outStream = tmpOut;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = inStream.read(buffer);
                String readMessage = new String(buffer, 0, bytes);
                Log.d(TAG, "Received message: " + readMessage);
                // Send the obtained bytes to the UI activity via handler
                handler.obtainMessage(MESSAGE_READ, bytes, -1, readMessage).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error reading from input stream", e);
                break;
            }
        }
    }

    public void write(String message) {
        try {
            outStream.write(message.getBytes());
            Log.d(TAG, "Sent message: " + message);
            // Notify UI activity that message was sent
            handler.obtainMessage(MESSAGE_WRITE, -1, -1, message).sendToTarget();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to output stream", e);
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing socket", e);
        }
    }
}
