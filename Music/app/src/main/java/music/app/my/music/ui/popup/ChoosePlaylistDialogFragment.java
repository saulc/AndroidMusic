package music.app.my.music.ui.popup;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

import music.app.my.music.DrawerActivity;

public  class ChoosePlaylistDialogFragment extends DialogFragment {

    public static ChoosePlaylistDialogFragment newInstance(String name, String sid, boolean isTop, boolean isGroup, long[] ids) {
        ChoosePlaylistDialogFragment fragment = new ChoosePlaylistDialogFragment();
        Bundle b = new Bundle();
        b.putString("Name", name);
        b.putString("ID", sid);
        b.putBoolean("isGroup", isGroup);
        if(isGroup) b.putLongArray("IDS", ids);
        b.putBoolean("isTop", isTop);
        fragment.setArguments(b);
        return fragment;
    }

    private   ArrayList<String> ids;
    private ArrayList<String> items;

    private   CharSequence[] getItems(){
        items = new  ArrayList<String>();
        ids = new  ArrayList<String>();

            Log.d("M6", "Looking for playlist: " );
            ContentResolver resolver = getContext().getContentResolver();
            String[] playlistProjection = { MediaStore.Audio.Playlists.NAME,  MediaStore.Audio.Playlists._ID};
            Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

            String playlistSortOrder = MediaStore.Audio.Playlists.NAME   + "  COLLATE NOCASE ASC";
            Cursor cur = resolver.query(uri, playlistProjection, null, null, playlistSortOrder);

            String id = "";
            String pname = "";
            while(cur.moveToNext()){
                    id = cur.getString(1);
                     pname = cur.getString(0);
                    items.add(pname);
                    ids.add(id);
                }


        CharSequence[] i = new CharSequence[items.size()];
        int f = 0;
        for(String s: items)
            i[f++] = s;

        return i;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String n = getArguments().getString("Name");
        final boolean g = getArguments().getBoolean("isGroup");
        final long[] i = getArguments().getLongArray("IDS");
        final boolean t = getArguments().getBoolean("isTop");
        final String id = getArguments().getString("ID");
        builder.setTitle(n + " : Add to Playlist: ");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((DrawerActivity) getActivity()).addToPlaylistCanceled();
                dismiss();
            }
        });
        builder.setItems(getItems() , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(g) ((DrawerActivity) getActivity()).addPlaylistPicked(i , t, ids.get(which));
                      else    ((DrawerActivity) getActivity()).addSongToPlaylist(items.get(which), id, which, ids.get(which), t);

                    }
                });


        return builder.create();
    }

}