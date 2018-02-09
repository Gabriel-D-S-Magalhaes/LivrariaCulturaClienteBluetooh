package vivacity.com.br.livrariacultura_clientebluetooh;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements ListPairedDevicesDialogFragment.ListPairedDevicesListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_RECOGNIZE_SPEECH = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 2; // Deve ser maior que 0!

    private TextView resultTextView;

    // Bluetooth
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice sanbot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Keep screen ON
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        resultTextView = findViewById(R.id.resultTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Assim que a activity fica visível ao usuário, começa a configuração do bluetooth.
        setUpBluetooth();
    }

    /**
     * Evento disparado quando a ImageView do microfone é clicada.
     *
     * @param view - {@link android.widget.ImageView} do microfone.
     */
    public void startListening(View view) {
        switch (view.getId()) {
            case R.id.imageViewMic:
                showAndListen();
                break;
        }
    }

    /**
     * Inicia a activity responsável pela conversão de fala em voz.
     */
    private void showAndListen() {

        // See: https://developer.android.com/reference/android/speech/RecognizerIntent.html#ACTION_RECOGNIZE_SPEECH
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        //Use a language model based on free-form speech recognition. This is a value to use for EXTRA_LANGUAGE_MODEL.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Optional text prompt to show to the user when asking them to speak.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale");

        // Optional IETF language tag (as defined by BCP 47), for example "en-US".
        // This tag informs the recognizer to perform speech recognition in a language different than the one set in the getDefault().
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");

        try {

            startActivityForResult(intent, REQUEST_RECOGNIZE_SPEECH);

        } catch (ActivityNotFoundException e) {

            Log.e(TAG, e.getMessage());

            Toast.makeText(getApplicationContext(),
                    "Não há nenhum aplicativo instalado para lidar com esta ação.",
                    Toast.LENGTH_SHORT).show();

            // Aqui ainda posso fazer com que o Google App seja instalado
        }
    }

    private void sendText() {

        if (TextUtils.isEmpty(resultTextView.getText())) {

            Toast.makeText(getApplicationContext(), "Sem mensagem", Toast.LENGTH_SHORT).show();

        } else if (bluetoothAdapter.isEnabled()) {

            showListPairedDevices();

        } else {

            enableBluetooth();
        }
    }

    /**
     * Mostra a AlertDialog com uma lista de todos os dispositivos pareados.
     */
    private void showListPairedDevices() {

        DialogFragment dialogFragment = new ListPairedDevicesDialogFragment();

        // Enviar argumentos a AlertDialog
        Bundle args = new Bundle();// Criando os argumentos
        args.putCharSequenceArray(null, queryPairedDevices());// Passsando os nomes dos dispositivos pareados para que sejam mostrados na AlertDialog
        dialogFragment.setArguments(args);// Enviando argumentos para a AlertDialog.Builder usando o objeto DialogFragment

        dialogFragment.show(getFragmentManager(), "Dispositivos Pareados");// Mostrando a AlertDialog
    }

    /**
     * @param index - index do item, (i.e. dispositivo pareado), selecionado.
     */
    @Override
    public void clickedItem(int index) {
        Toast.makeText(getApplicationContext(), "Index do item selecionado = " + index,
                Toast.LENGTH_SHORT).show();
    }

    public void clickedView(View view) {
        switch (view.getId()) {
            case R.id.imageViewSend:
                sendText();
                break;
        }
    }

    /**
     * Antes que o aplicativo se comunique usando o Bluetooth, é necessário verificar se o
     * dispositivo permite Bluetooth e, caso permita, se o Bluetooth está ativado
     */
    private void setUpBluetooth() {

        // 1º Obtenha o BluetoothAdapter

        // Retornará um BluetoothAdapter que representa o próprio adaptador Bluetooth do dispositivo
        // (o rádio Bluetooth).
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Há um adaptador Bluetooth para o sistema completo e o aplicativo pode usar esse objeto
        // para interagir com ele.

        // Se getDefaultAdapter() retornar nulo, o dispositivo não permite Bluetooth e fim de papo.
        if (bluetoothAdapter == null) {

            Toast.makeText(getApplicationContext(), "O dispositivo não permite Bluetooth",
                    Toast.LENGTH_SHORT).show();

            new CountDownTimer(5000, 1000) {

                /**
                 * Callback fired on regular interval.
                 *
                 * @param millisUntilFinished The amount of time until finished.
                 */
                @Override
                public void onTick(long millisUntilFinished) {

                }

                /**
                 * Callback fired when the time is up.
                 */
                @Override
                public void onFinish() {
                    // Ao final do contador
                    finish();// Encerra a activity
                }
            }.start();

        } else {

            // 2º Ativar Bluetooth

            // É necessário assegurar a ativação do Bluetooth. Chame isEnabled() para verificar se o
            // Bluetooth está ativado no momento. Se o método retornar false, o Bluetooth está
            // desativado.
            if (!bluetoothAdapter.isEnabled()) {
                // Não está habilitado. Solicitar ao usuário, sem sair do APP, que habilite.
                enableBluetooth();
            }
        }
    }

    /**
     * Solicita ao usuário que habilite o Bluetooth
     */
    private void enableBluetooth() {

        // Para solicitar a ativação do Bluetooth, chame startActivityForResult() com o
        // Intent de ação ACTION_REQUEST_ENABLE.
        Intent enableBlIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBlIntent, REQUEST_ENABLE_BLUETOOTH);

        // Será emitida uma solicitação de ativação do Bluetooth por meio das configurações
        // do sistema (sem interromper o aplicativo).
    }

    /**
     * Antes de executar a descoberta de dispositivos, vale a pena consultar o conjunto de
     * dispositivos pareados para verificar se o dispositivo desejado já é conhecido.
     *
     * @return CharSequence array - Nomes dos dispositivos pareados.
     */
    private CharSequence[] queryPairedDevices() {

        // Chame getBondedDevices()
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // É retornado um conjunto de BluetoothDevices, representando dispositivos pareados.

        // If there are paired devices
        if (pairedDevices.size() > 0) {

            CharSequence[] devicesNames = new CharSequence[pairedDevices.size()];
            int i = 0;

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {

                Log.i(TAG, "Name: " + device.getName() + "\nAddress: " + device.getAddress()
                        + "\nUUIDs: " + Arrays.toString(device.getUuids())
                        + "\nBluetooth class: " + device.getBluetoothClass().toString()
                        + "\nBond state: " + device.getBondState()
                        + "\nType: " + device.getType()
                        + "\nDescribe contents: " + device.describeContents());

                devicesNames[i] = device.getName();
                i++;
            }

            return devicesNames;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case REQUEST_RECOGNIZE_SPEECH:

                if (resultCode == RESULT_OK && data != null) {
                    // See: https://developer.android.com/reference/android/speech/RecognizerIntent.html#EXTRA_RESULTS
                    ArrayList<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    resultTextView.setText(results.get(0));
                }

                break;

            case REQUEST_ENABLE_BLUETOOTH:

                if (resultCode == RESULT_CANCELED) {

                    Toast.makeText(getApplicationContext(), "O Bluetooth é necessário",
                            Toast.LENGTH_SHORT).show();

                } else if (resultCode == RESULT_OK) {

                    Toast.makeText(getApplicationContext(), "Bluetooth ativado",
                            Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
