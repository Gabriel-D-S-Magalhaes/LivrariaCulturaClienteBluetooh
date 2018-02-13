package vivacity.com.br.livrariacultura_clientebluetooh;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
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

    private OutputStream outputStream;

    /**
     * Método construtor
     *
     * @param device - Um objeto {@link BluetoothDevice} que representa o dispositivo remoto
     *               (i.e. Bluetooth Server).
     */
    public ConnectThread(@NonNull BluetoothDevice device) {

        // Use a temporary object that is later assigned to mBluetoothSocket, because
        // mBluetoothSocket is final
        BluetoothSocket tmp = null;

        // Guardo meu BluetoothDevice
        mBluetoothDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {

            // Use BluetoothDevice para adquirir um BluetoothSocket e iniciar a conexão.
            // Usando o BluetoothDevice, chame createRfcommSocketToServiceRecord(UUID) para obter um
            // BluetoothSocket.
            // Isso inicializa um BluetoothSocket que se conectará ao BluetoothDevice.
            // O UUID passado aqui deve corresponder ao UUID usado pelo dispositivo servidor quando
            // abriu seu BluetoothServerSocket (com listenUsingRfcommWithServiceRecord(String, UUID)).
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));

        } catch (IOException e) {

            Log.e(TAG, e.getMessage());
        }

        mBluetoothSocket = tmp;
    }

    @Override
    public void run() {
        // Cancel discovery because it will slow down the connection
        //bluetoothAdapter.cancelDiscovery();

        try {

            // Connect the device through the socket. This will block until it succeeds or throws an
            // exception
            mBluetoothSocket.connect();// Para iniciar a conexão.
            // Se a pesquisa for bem-sucedida e o dispositivo remoto aceitar a conexão, ele
            // compartilhará o canal RFCOMM para uso durante a conexão e connect() retornará.

            if (mBluetoothSocket.isConnected()) {

                Log.i(TAG, "Conectado!");
            }

        } catch (IOException connectException) {

            Log.i(TAG, "Conexão falhou ou esgotou o tempo limite.");

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

    /**
     * Inicia o encadeamento para transferir dados. Nesse caso, só envia dados.
     */
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
    public void write(final byte[] bytes) {

        // Deve-se usar um encadeamento dedicado para todas as leituras e gravações do stream.
        // Nesse caso será gravações.

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    outputStream.write(bytes);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }).start();
    }
}
