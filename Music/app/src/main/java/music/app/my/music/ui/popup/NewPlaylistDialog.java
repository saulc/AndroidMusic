package music.app.my.music.ui.popup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import music.app.my.music.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewPlaylistDialog.OnDialogInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewPlaylistDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPlaylistDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private boolean mParam1;

    private  EditText name;
    private OnDialogInteractionListener mListener;

    public NewPlaylistDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment NewPlaylistDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPlaylistDialog newInstance(boolean isQ) {
        NewPlaylistDialog fragment = new NewPlaylistDialog();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isQ);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getBoolean(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.dialog, container, false);

        Button go = (Button) v.findViewById(R.id.go);
        Button cancel = (Button) v.findViewById(R.id.cancel);
         name = (EditText) v.findViewById(R.id.name);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(name.getText().toString(), mParam1);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelPressed();
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String name, Boolean isQ) {
        if (mListener != null) {
            mListener.nameEnted(name, isQ);
            this.dismiss();
        }
    }
    public void onCancelPressed() {
        if (mListener != null) {
            mListener.cancelClicked();
            this.dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDialogInteractionListener) {
            mListener = (OnDialogInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDialogInteractionListener {
        // TODO: Update argument type and name
        void nameEnted(String name, boolean b);
        void cancelClicked();
    }
}
