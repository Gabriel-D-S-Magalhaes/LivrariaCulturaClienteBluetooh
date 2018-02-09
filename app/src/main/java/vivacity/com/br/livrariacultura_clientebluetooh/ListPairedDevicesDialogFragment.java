package vivacity.com.br.livrariacultura_clientebluetooh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by mac on 09/02/18.
 *
 * @author Gabriel Dos Santos Magalhães
 */

public class ListPairedDevicesDialogFragment extends DialogFragment {

    // Constante que armazena o nome na classe
    private static final String TAG = ListPairedDevicesDialogFragment.class.getSimpleName();

    /**
     * CharSequence array que armazena o array passado como argumento pelo método
     * {@link #setArguments(Bundle)} da classe {@link Fragment}
     */
    private CharSequence[] items;


    // The activity that creates an instance of this dialog fragment must implement this interface
    // in order to receive event callbacks.
    // Each method passes the DialogFragment in case the host needs to query it. */
    public interface ListPairedDevicesListener {
        void clickedItem(int index);
    }

    // Use this instance of the interface to deliver action events
    ListPairedDevicesListener listener;

    /**
     * Override the {@link Fragment#onAttach(Activity)} method to instantiate the
     * ListPairedDevicesListener
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {

            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ListPairedDevicesListener) activity;

        } catch (ClassCastException e) {

            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " deve implementar ListPairedDevicesListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Se foi passado argumentos pelo método Fragment.setArguments(Bundle args)
        if (!getArguments().isEmpty() && getArguments().getCharSequenceArray(null) != null) {

            // Então, o array do tipo CharSequence recebe os argumentos passados
            this.items = getArguments().getCharSequenceArray(null);

        } else {

            // Do contrário, add um único item na lista
            this.items[0] = "Nenhum dispositivo pareado";
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Setup the AlertDialog
        builder.setTitle("Dispositivos pareados").setItems(this.items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.clickedItem(which);
            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }
}
