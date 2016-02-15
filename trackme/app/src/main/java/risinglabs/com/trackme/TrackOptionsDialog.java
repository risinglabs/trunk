package risinglabs.com.trackme;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;


public class TrackOptionsDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private ArrayList mSelectedItems;
    private DialogCallbacks callbacks;

    public TrackOptionsDialog(DialogCallbacks callbacks){
        this.callbacks = callbacks;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.myoptions)
                .setItems(R.array.multipleoptions, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // The 'which' argument contains the index position
        // of the selected item
        //mSelectedItems.add(which);
        callbacks.onItemSelected(which);
        //GoogleMapInstance.getInstance().takeSnapshot();
    }
}
