package music.app.my.music.ui.popup;



import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import music.app.my.music.DrawerActivity;

public  class ConfirmDeleteDialogFragment extends DialogFragment {

    public static ConfirmDeleteDialogFragment newInstance(String name) {
        ConfirmDeleteDialogFragment fragment = new ConfirmDeleteDialogFragment();
        Bundle b = new Bundle();
        b.putString("Name", name);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String n = getArguments().getString("Name");

        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle( " Delete this playlist?");
        if(n.compareTo("NOWPLAYING") == 0) builder.setTitle(" Clear now playing items?");

       // builder.setIconAttribute(android.R.drawable.ic_dialog_alert);
        builder.setMessage("Playlist : " + n)
                .setPositiveButton("Delete!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((DrawerActivity)  getActivity()).deleted(getTag().toString(), n);
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}