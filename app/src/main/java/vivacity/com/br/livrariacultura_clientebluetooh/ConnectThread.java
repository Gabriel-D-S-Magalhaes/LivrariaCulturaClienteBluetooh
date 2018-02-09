package vivacity.com.br.livrariacultura_clientebluetooh;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by mac on 08/02/18.
 *
 * @author Gabriel Dos Santos Magalhães
 */

public class ConnectThread extends Thread {

    private final String TAG = ConnectThread.class.getSimpleName();

    private final String MY_UUID = "5419a893-467c-4812-8cdb-282f5853168f";

    private final BluetoothDevice mBluetoothDevice;
    private final BluetoothSocket mBluetoothSocket;
    private BluetoothAdapter bluetoothAdapter;

    private OutputStream outputStream;

    public ConnectThread(@NonNull BluetoothDevice device, @NonNull BluetoothAdapter bluetoothAdapter) {

        this.bluetoothAdapter = bluetoothAdapter;

        // Use a temporary object that is later assigned to mBluetoothSocket, because
        // mBluetoothSocket is final
        BluetoothSocket tmp = null;

        mBluetoothDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {

            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));

        } catch (IOException e) {

            Log.e(TAG, e.getMessage());
        }

        mBluetoothSocket = tmp;
    }

    @Override
    public void run() {
        // Cancel discovery because it will slow down the connection
        bluetoothAdapter.cancelDiscovery();

        try {

            // Connect the device through the socket. This will block until it succeeds or throws an
            // exception
            mBluetoothSocket.connect();

        } catch (IOException connectException) {

            // Unable to connect; close the socket and get out
            try {
                mBluetoothSocket.close();

            } catch (IOException closeException) {

                Log.e(TAG, closeException.getMessage());
            }

            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mBluetoothSocket);
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {

            mBluetoothSocket.close();

        } catch (IOException e) {

            Log.e(TAG, e.getMessage());
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {

        try {

            this.outputStream = socket.getOutputStream();

        } catch (IOException e) {

            Log.e(TAG, e.getMessage());
        }

    }

    /**
     * Call this from the main activity to send data to the remote device
     */
    public void write(byte[] bytes) {

        try {
            this.outputStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

    }

}
