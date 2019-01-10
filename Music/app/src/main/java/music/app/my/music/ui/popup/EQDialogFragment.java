package music.app.my.music.ui.popup;



import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;

import music.app.my.music.DrawerActivity;

public  class EQDialogFragment extends DialogFragment {

    public static EQDialogFragment newInstance(ArrayList<String> presents) {
        EQDialogFragment fragment = new EQDialogFragment();
        Bundle b = new Bundle();
        b.putStringArrayList("PRESETS", presents);
        fragment.setArguments(b);
        return fragment;
    }

    private ArrayList<String> items;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        items = getArguments().getStringArrayList("PRESETS");

        builder.setTitle("Eq presets: ");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((DrawerActivity) getActivity()).addToPlaylistCanceled();
                dismiss();
            }
        });
        builder.setItems(getItems() , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((DrawerActivity) getActivity()).setEQ(which, items.get(which));

                    }
                });
        return builder.create();
    }

    private CharSequence[] getItems() {
        CharSequence[] a = new CharSequence[items.size()];
        for(int i =0; i<items.size(); i++)
            a[i] = items.get(i);
        return a;

    }

}