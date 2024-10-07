package music.app.my.music.ui.popup;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.util.Log;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;

public  class ChooseThemeDialogFragment extends DialogFragment {

    public static ChooseThemeDialogFragment newInstance(int currentTheme) {
        ChooseThemeDialogFragment fragment = new ChooseThemeDialogFragment();
        Bundle b = new Bundle();
        b.putInt("CURRENT", currentTheme);
        fragment.setArguments(b);
        return fragment;
    }

    private ArrayList<Integer> items;

    private   CharSequence[] getItems(){
        items = new  ArrayList<>();
        int a = R.style.AppThemeLight;
        items.add(a);
        a = R.style.DarkSide;
        items.add(a);
        a = R.style.AppTheme;
        items.add(a);
        a = R.style.AppThemeNior;
        items.add(a);
        a = R.style.AppThemeColors;
        items.add(a);
        a = R.style.AppThemeJarvis;
        items.add(a);

        CharSequence[] i = { "Light", "Darkside", "Lake", "Nior", "Colors", "Jarvis"};

        return i;
    }

    private  final String TAG = getClass().getSimpleName();

    private void log(String s){
        Log.d(TAG, s);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       // final String n = getArguments().getString("CURRENT");
        builder.setTitle("Choose app Theme");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
            }
        });
        builder.setItems(getItems() , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        log(which + " Theme picked: " + getItems()[which]);
                         ((DrawerActivity) getActivity()).themePicked(getItems()[which]+"", items.get(which));
                    }
                });


        return builder.create();
    }

}