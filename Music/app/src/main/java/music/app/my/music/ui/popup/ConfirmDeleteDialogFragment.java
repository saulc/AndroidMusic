package music.app.my.music.ui.popup;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import music.app.my.music.DrawerActivity;

public  class ConfirmDeleteDialogFragment extends DialogFragment {

    public enum DeleteDialogType { PLAYLIST, REMOVESONG }
    public  DeleteDialogType type = null;
    public static ConfirmDeleteDialogFragment newInstance(String name) {
        ConfirmDeleteDialogFragment fragment = new ConfirmDeleteDialogFragment();
        Bundle b = new Bundle();
        b.putInt("TYPE", 0);
        b.putString("Name", name);
        fragment.setArguments(b);
        return fragment;
    }


    public static ConfirmDeleteDialogFragment newInstance(String pname, String pid, String sid, int pos) {
        ConfirmDeleteDialogFragment fragment = new ConfirmDeleteDialogFragment();
        Bundle b = new Bundle();
        b.putInt("TYPE", 1);
        b.putString("Name", pname);
        b.putString("PID", pid);
        b.putString("SID", sid);
        b.putInt("POS", pos);
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final int pos;
        final String pid, sid;
        final int t = getArguments().getInt("TYPE");
        if(t == 0) type = DeleteDialogType.PLAYLIST;
        else if(t == 1) type = DeleteDialogType.REMOVESONG;

            Bundle b = getArguments();
             pid = b.getString("PID");
             sid = b.getString("SID");
             pos = b.getInt("POS");


        final String n = getArguments().getString("Name");

        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle( " Delete this playlist?");
        if(n.compareTo("NOWPLAYING") == 0) builder.setTitle(" Clear now playing items?");
        else if(t == 1) builder.setTitle(" Remove this song?");

       // builder.setIconAttribute(android.R.drawable.ic_dialog_alert);
        builder.setMessage("Playlist : " + n)
                .setPositiveButton("Delete!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(t == 0)  ((DrawerActivity)  getActivity()).deleted(getTag().toString(), n);
                        else if(t == 1)   ((DrawerActivity)  getActivity()).deletedSong(n, pid, sid, pos);

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