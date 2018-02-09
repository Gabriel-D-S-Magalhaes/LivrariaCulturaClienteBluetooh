package vivacity.com.br.livrariacultura_clientebluetooh;

import android.bluetooth.BluetoothSocket;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by mac on 08/02/18.
 *
 * @author
 */

public class ManageSocketThread extends Thread {

    private static final String TAG = ManageSocketThread.class.getSimpleName();

    private final BluetoothSocket bluetoothSocket;
    private final OutputStream outputStream;

    public ManageSocketThread(@NonNull BluetoothSocket socket) {

        this.bluetoothSocket = socket;

        OutputStream tmpOut = null;

        // Get the output stream, using temp objects because member stream are final
        try {

            tmpOut = socket.getOutputStream();

        } catch (IOException e) {

            Log.e(TAG, e.getMessage());
        }

        outputStream = tmpOut;

    }

    @Override
    public void run() {

    }
}
