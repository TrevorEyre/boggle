package com.team1.cs410.boggle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


// This class does all the work for setting up and managing Bluetooth
// connections with other devices. It has a thread that listens for
// incoming connections, a thread for connecting with a device, and a
// thread for performing data transmissions when connected.
public class BluetoothService {

    // Tag for debug statements
    private static final String TAG = "BluetoothService";
    // Name for the SDP record when creating server socket
//    private static final String NAME_SECURE = "BluetoothChatSecure";
//    private static final String NAME_INSECURE = "BluetoothChatInsecure";
//
//    // Unique UUID for this application
//    private static final UUID MY_UUID_SECURE =
//            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
//    private static final UUID MY_UUID_INSECURE =
//            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    // Unique UUID for this application
    private static final String NAME = "BluetoothBoggle";
    private static final UUID MY_UUID = UUID.fromString("01f95e5d-2e59-bf0c-8f5f-5f1d6243abe7");

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // Not doing anything
    public static final int STATE_LISTEN = 1;     // Listening for incoming connections
    public static final int STATE_CONNECTING = 2; // Initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // Successfully connected to a remote device

    // Member fields
    private final BluetoothAdapter bluetoothAdapter;
    private final Handler handler;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int state;

    // Constructor
    public BluetoothService(Context context, Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        this.handler = handler;
    }

    // Set the current state of the bluetooth connection
    private synchronized void setState(int newState) {
        Log.d(TAG, "setState() " + state + " -> " + newState);
        state = newState;

        // Give the new state to the Handler so the UI Activity can update
        handler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    // Return current connection state
    public synchronized int getState() {
        return state;
    }

    // Start the AcceptThread to begin a session in listening (server) mode.
    // Called by the Activity onResume()
    public synchronized void start() {
        Log.d(TAG, "start()");

        // Cancel any connect or connected threads
        cancelConnectThread();
        cancelConnectedThread();

        // Start the thread to listen on a BluetoothServerSocket
        setState(STATE_LISTEN);
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    // Start the ConnectThread to initiate a connection to a remote device
    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device);

        // Cancel any connect or connected threads
        if (state == STATE_CONNECTING) {
            cancelConnectThread();
        }
        cancelConnectedThread();

        // Start the thread to connect with the given device
        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    // Start the ConnectedThread to begin managing a Bluetooth connection
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
        Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection, and any thread currently
        // running a connection. Also cancel accept thread so we only connect to one device
        cancelConnectThread();
        cancelConnectedThread();
        cancelAcceptThread();

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(socket, socketType);
        connectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = handler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    // Stop all threads
    public synchronized void stop() {
        Log.d(TAG, "stop");

        cancelConnectThread();
        cancelConnectedThread();
        cancelAcceptThread();

        setState(STATE_NONE);
    }

    // Cancel the accept thread
    private void cancelAcceptThread() {
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
    }

    // Cancel any thread attempting to make a connection
    private void cancelConnectThread() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
    }

    // Cancel any thread currently running a connection
    private void cancelConnectedThread() {
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
    }

    // Write to the ConnectedThread in an unsynchronized manner
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (state != STATE_CONNECTED) return;
            r = connectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    // Indicate that the connection attempt failed and notify the UI Activity
    private void connectionFailed() {
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothService.this.start();
    }

    // Indicate that the connection was lost and notify the UI Activity.
    private void connectionLost() {
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothService.this.start();
    }

    // This thread runs while listening for incoming connections. It behaves
    // like a server-side client. It runs until a connection is accepted
    // (or until cancelled).
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket serverSocket;
        private String socketType;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            socketType = "Secure";

            // Create a new listening server socket
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + socketType + "listen() failed", e);
            }
            serverSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "Socket Type: " + socketType + "BEGIN acceptThread" + this);
            setName("AcceptThread" + socketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (state != STATE_CONNECTED) {
                try {
                    // Attempt to accept a connection. Notify handler if successful
                    socket = serverSocket.accept();
                    handler.obtainMessage(Constants.MESSAGE_HOST_GAME).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + socketType + "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(), socketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.d(TAG, "END acceptThread, socket Type: " + socketType);

        }

        public void cancel() {
            Log.d(TAG, "Socket Type" + socketType + "cancel " + this);
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + socketType + "close() of server failed", e);
            }
        }
    }


    // This thread runs while attempting to make an outgoing connection
    // with a device. It runs straight through; the connection either
    // succeeds or fails.
    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;
        private String socketType;

        public ConnectThread(BluetoothDevice bluetoothDevice) {
            device = bluetoothDevice;
            BluetoothSocket tmp = null;
            socketType = "Secure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + socketType + "create() failed", e);
            }
            socket = tmp;
        }

        public void run() {
            Log.d(TAG, "BEGIN connectThread SocketType:" + socketType);
            setName("ConnectThread" + socketType);

            // Always cancel discovery because it will slow down a connection
            bluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    socket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + socketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                connectThread = null;
            }

            // Start the connected thread
            connected(socket, device, socketType);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + socketType + " socket failed", e);
            }
        }
    }

    // This thread runs during a connection with a remote device.
    // It handles all incoming and outgoing transmissions.
    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket bluetoothSocket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            socket = bluetoothSocket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {
            Log.d(TAG, "BEGIN connectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream and send the obtained bytes to the UI Activity
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    // Start the service over to restart listening mode
                    BluetoothService.this.start();
                    break;
                }
            }
        }

        // Write to the connected OutStream.
        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);

                // Share the sent message back to the UI Activity
                handler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    private static final int MESSAGE_READ = 1;
//
//    private static final String NAME = "BluetoothBoggle";
//    private static final UUID MY_UUID = UUID.fromString("01f95e5d-2e59-bf0c-8f5f-5f1d6243abe7");
//    private static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//    private AcceptThread acceptThread;
//    private ConnectThread connectThread;
//    private ConnectedThread connectedThread;
//
//    private int mState;
//    private final Handler mHandler;
//
//    // Constants that indicate the current connection state
//    public static final int STATE_NONE = 0;       // we're doing nothing
//    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
//    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
//    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
//
//    public BluetoothService(Context context, Handler handler) {
//        mState = STATE_NONE;
//        mHandler = handler;
//    }
//
//    public void accept(Handler handler) {
//        //mHandler=handler;
//        acceptThread = new AcceptThread(handler);
//        acceptThread.start();
//    }
//
//    public void waitForOtherDevice() {
//        while (connectedThread == null);
//    }
//
//    public void write(byte[] bytes) {
//        connectedThread.write(bytes);
//    }
//
//    public void connectToDevice(BluetoothDevice device, Handler handler) {
//        connectThread = new ConnectThread(device, handler);
//        connectThread.start();
//    }
//
//    private class AcceptThread extends Thread {
//        private final BluetoothServerSocket mmServerSocket;
//        private Handler mHandler;
//
//        public AcceptThread(Handler handler) {
//            // Use a temporary object that is later assigned to mmServerSocket,
//            // because mmServerSocket is final
//            BluetoothServerSocket tmp = null;
//            try {
//                // MY_UUID is the app's UUID string, also used by the client code
//                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
//            } catch (IOException e) { }
//            mmServerSocket = tmp;
//            mHandler = handler;
//        }
//
//        public void run() {
//            BluetoothSocket socket = null;
//            // Keep listening until exception occurs or a socket is returned
//            while (true) {
//                try {
//                    socket = mmServerSocket.accept();
//                } catch (IOException e) {
//                    break;
//                }
//                // If a connection was accepted
//                if (socket != null) {
//                    // Do work to manage the connection (in a separate thread)
//                    connectedThread = new ConnectedThread(socket, mHandler);
//                    Log.d("About connection", "Starting connectedThread");
//                    connectedThread.start();
//                    try {
//                        mmServerSocket.close();
//                    } catch (IOException e) {
//                    }
//                    break;
//                }
//            }
//        }
//
//        /** Will cancel the listening socket, and cause the thread to finish */
//        public void cancel() {
//            try {
//                mmServerSocket.close();
//            } catch (IOException e) { }
//        }
//    }
//
//
//    private class ConnectThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private Handler mHandler;
//
//        public ConnectThread(BluetoothDevice device, Handler handler) {
//            // Use a temporary object that is later assigned to mmSocket,
//            // because mmSocket is final
//            BluetoothSocket tmp = null;
//            mHandler = handler;
//
//            // Get a BluetoothSocket to connect with the given BluetoothDevice
//            try {
//                // MY_UUID is the app's UUID string, also used by the server code
//                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//            } catch (IOException e) { }
//            mmSocket = tmp;
//        }
//
//        public void run() {
//            // Cancel discovery because it will slow down the connection
//            mBluetoothAdapter.cancelDiscovery();
//
//            try {
//                // Connect the device through the socket. This will block
//                // until it succeeds or throws an exception
//                mmSocket.connect();
//            } catch (IOException connectException) {
//                // Unable to connect; close the socket and get out
//                try {
//                    mmSocket.close();
//                } catch (IOException closeException) { }
//                return;
//            }
//
//            // Do work to manage the connection (in a separate thread)
//            connectedThread = new ConnectedThread(mmSocket, mHandler);
//            Log.d("About connection","Starting connectedThread");
//            connectedThread.start();
//        }
//
//        /** Will cancel an in-progress connection, and close the socket */
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) { }
//        }
//    }
//
//
//    private class ConnectedThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final InputStream mmInStream;
//        private final OutputStream mmOutStream;
//        private final android.os.Handler mHandler;
//
//        public ConnectedThread(BluetoothSocket socket, android.os.Handler handler) {
//            mmSocket = socket;
//            mHandler = handler;
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//
//            // Get the input and output streams, using temp objects because
//            // member streams are final
//            try {
//                tmpIn = socket.getInputStream();
//                tmpOut = socket.getOutputStream();
//            } catch (IOException e) { }
//
//            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
//        }
//
//        public void run() {
//            byte[] buffer = new byte[1024];  // buffer store for the stream
//            int bytes; // bytes returned from read()
//
//            // Keep listening to the InputStream until an exception occurs
//            Log.d("Thread","Connected thread has started and listening");
//            while (true) {
//                try {
//                    // Read from the InputStream
//                    bytes = mmInStream.read(buffer);
//                    Log.d("Read",new String(buffer));
//                    // Send the obtained bytes to the UI activity
////                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
////                            .sendToTarget();
//                    mHandler.obtainMessage(Constants.MESSAGE_READ,bytes,-1,buffer).sendToTarget();
//                    Log.d("Read","Sent to handler" + new String(buffer));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.e("Read: ","Exception in read");
//                    break;
//                }
//            }
//        }
//
//        /* Call this from the main activity to send data to the remote device */
//        public void write(byte[] bytes) {
//            try {
//                Log.d("Write debug","Writing bytes");
//                mmOutStream.write(bytes);
//                Log.d("Write debug","Written bytes");
//                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, bytes).sendToTarget();
//            } catch (IOException e) { }
//        }
//
//        /* Call this from the main activity to shutdown the connection */
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) { }
//        }
//    }
//}
