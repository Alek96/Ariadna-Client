package com.example.ariadna;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionSrv";

    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_CONNECTING = 1; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 2;  // now connected to a remote device

    //Standard SerialPortService ID
    private static final UUID MY_UUID_SECURE =
//            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
//            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private Handler mHandler;
    private final BluetoothAdapter mBluetoothAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private Context mContext;
    private int mState;
    private String mAddress;



    public BluetoothConnectionService(Context context, String address) {
        Log.d(TAG, "BluetoothConnectionService: Created");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mContext = context;
        mAddress = address;
    }

    public synchronized int getState() {
        Log.d(TAG, "getState: State: " + mState);
        return mState;
    }

    public synchronized void setHandler(Handler handler) {
        Log.d(TAG, "setHandler: Handler: " + handler);
        mHandler = handler;
    }

    public synchronized void start() {
        Log.d(TAG, "start: Started");
        stop();

        BluetoothDevice devices = mBluetoothAdapter.getRemoteDevice(mAddress);
        connect(devices, devices.getUuids()[0].getUuid());
    }

    public synchronized void stop() {
        Log.d(TAG, "stop: Started");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mState = STATE_NONE;
    }


    public synchronized void connect(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "connect: Started.");

        Log.d(TAG, "connect: connect to: " + device.getName() + ", " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    public synchronized void connected(BluetoothSocket socket) {
        Log.d(TAG, "connected: Starting.");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }


    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;

        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        Log.d(TAG, "write: Write Called.");
        r.write(out);
    }

    public boolean isConnected() {
        Log.d(TAG, "isConnected: Started");
        if (getState() != STATE_CONNECTED) {
            Log.d(TAG, "isConnected: Not connected");
            Toast.makeText(mContext, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Log.d(TAG, "isConnected: Connected");
            return true;
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;
        private UUID mmUUID;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            mmUUID = uuid;
            mState = STATE_CONNECTING;
        }

        public void run() {
            Log.i(TAG, "ConnectThread: run: Run mConnectThread ");
            BluetoothSocket tmp = null;

            try {
                Log.d(TAG, "ConnectThread: run: Trying to create InsecureRfcommSocket using UUID: " + mmUUID);
                tmp = mmDevice.createRfcommSocketToServiceRecord(mmUUID);
//                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(mmUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: run: Could not create InsecureRfcommSocket " + e.getMessage());
                return;
            }

            mmSocket = tmp;

            try {
                mmSocket.connect();
                Log.d(TAG, "ConnectThread: run: Socked connected.");
            } catch (IOException e) {
                try {
                    Log.d(TAG, "ConnectThread: run: Could not connect to UUID: " + mmUUID + ". Error: " + e.getMessage());
                    mmSocket.close();
                    Log.d(TAG, "ConnectThread: run: Closed Socket.");
                    mState = STATE_NONE;
                } catch (IOException e1) {
                    Log.e(TAG, "ConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                return;
            }

            connected(mmSocket);
        }

        public void cancel() {
            try {
                Log.d(TAG, "ConnectThread: cancel: Closing Client Socket.");
                mmSocket.close();
                mState = STATE_NONE;
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: cancel: close() of mmSocket in ConnectThread failed. " + e.getMessage());
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocked;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");

            this.mmSocked = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public void run() {
            Log.i(TAG, "ConnectedThread: run: RUN mConnectedThread ");
            byte[] buffer = new byte[1024];
            int bytes;

            while (mState == STATE_CONNECTED) {
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "ConnectedThread: run: InputStream: " + incomingMessage);

                    if (mHandler != null) {
                        // Send the obtained bytes to the UI Activity
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "ConnectedThread: run: Error reading Input Stream. " + e.getMessage());
                    break;
                }
            }
        }

        // write to OutputStream
        public void write(byte[] buffer) {
            String text = new String(buffer, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to OutputStream: " + text);
            try {
                mmOutStream.write(buffer);
                if (mHandler != null) {
                    mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
                            .sendToTarget();
                }
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
            }
        }

        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocked.close();
                mState = STATE_NONE;
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in ConnectedThread failed. " + e.getMessage());
            }
        }
    }
}
